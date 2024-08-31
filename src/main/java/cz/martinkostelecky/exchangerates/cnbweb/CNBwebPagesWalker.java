package cz.martinkostelecky.exchangerates.cnbweb;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class CNBwebPagesWalker implements DisposableBean {

    private static final By lastElement = By.xpath("//*[normalize-space(text())=\"Prohlášení o přístupnosti\"]");

    private WebDriver driver;
    private WebDriverWait wait;
    private final ChromeDriverService service;

    /**
     * Chrome driver and binaries configuration in properties or yaml file needed
     * Driver path example: c:\\Users\\user\\Downloads\\chrome_driver\\chromedriver.exe
     * Binaries path example: c:\\Users\\user\\Downloads\\chrome_driver\\chrome.exe
     */

    public CNBwebPagesWalker(@Value("${chrome.driver:#{null}}") String chromeDriverExe, @Value("${chrome.binaries:#{null}}") String chromeBinaries) throws IOException {
        if (chromeDriverExe == null || chromeBinaries == null) {
            log.error("Chrome driver and binaries must be defined in configuration file.");
            throw new IllegalStateException("Chrome driver and binaries must be defined in configuration file.");
        }
        service = new ChromeDriverService.Builder()
                .usingDriverExecutable(new File(chromeDriverExe))
                .usingAnyFreePort()
                .build();
        service.start();
        useNewDriver(chromeBinaries);
        log.info("Browser opened.");
    }

    private void useNewDriver(String chromeBinaries) {
        close(false);
        log.info("Initializing new WebDriver - start");

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless", "--start-maximized", "--no-sandbox", "--disable-gpu", "--window-size=1920,1200", "--ignore-certificate-errors", "--disable-extensions", "--no-sandbox", "--disable-dev-shm-usage");
        options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.107 Safari/537.36");
        options.setBinary(chromeBinaries);

        driver = new ChromeDriver(service, options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10), Duration.ofSeconds(100));

        log.info("Initializing new WebDriver - done");
    }

    protected Currency loadDataFromCNBWeb(Currency currency) throws CurrencyDataNotFoundException, UnexpectedWebPageStateException {

        log.info("LOADING DATA FROM CNB WEB FOR COUNTRY: " + currency.getCountry());

        try {
            log.info("driver.getWindowHandles().size = " + driver.getWindowHandles().size());

            final String address = "https://www.cnb.cz/cs/financni-trhy/devizovy-trh/kurzy-devizoveho-trhu/kurzy-devizoveho-trhu/";
            log.info("Going to " + address);
            driver.get(address);

            log.info("Wait for page to load.");
            wait.until(ExpectedConditions.visibilityOfElementLocated(lastElement));
            log.info("Page loaded.");

            List<WebElement> countries = driver.findElements(By.xpath("//table//tbody/tr/td[1]"));
            List<WebElement> amounts = driver.findElements(By.xpath("//table//tbody/tr/td[3]"));
            List<WebElement> codes = driver.findElements(By.xpath("//table//tbody/tr/td[4]"));
            List<WebElement> exchangeRates = driver.findElements(By.xpath("//table//tbody/tr/td[5]"));

            for (WebElement country : countries) {

                if (currency.getCountry().equals(country.getText())) {

                    int index = countries.indexOf(country);
                    log.info("Loading exchange rate for country: " + country.getText());

                    currency.setAmount(Integer.valueOf(amounts.get(index).getText()));
                    currency.setCode(codes.get(index).getText());
                    currency.setExchangeRate(exchangeRates.get(index).getText().replace(",", "."));
                    currency.setDateTime(LocalDateTime.now());

                    log.info("Exchange rate found and set.");
                    return currency;
                }
            }

            throw new CurrencyDataNotFoundException("Requested data not found.");

        } catch (WebDriverException e) {
            log.error("Requested element or data not found: " + e.getMessage());
            throw new UnexpectedWebPageStateException("Requested element or data not found: " + e.getMessage(), e);
        }
    }

    public void destroy() {
        log.info("Closing browser.");
        close();
        log.info("Browser closed.");
    }

    protected void close() {
        close(true);
    }

    private void close(boolean closeServer) {
        log.info("Closing driver - start");
        if (driver != null) {
            try {
                driver.quit();
            } catch (WebDriverException webDriverException) {
                log.error("Exception while quitting driver.", webDriverException);
            }
            driver = null;
        }

        if (service != null && closeServer) {
            service.stop();
        }

        log.info("Closing driver - done");
    }
}
