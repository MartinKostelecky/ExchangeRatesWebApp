package cz.martinkostelecky.exchangerates.camel;

import cz.martinkostelecky.exchangerates.cnbweb.Currency;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class CurrencyDataToFileRouter extends RouteBuilder {

    //configures camel route that saves currency data in a file
    @Override
    public void configure() throws Exception {

        from("direct:start")
                .process(exchange -> {
                    Currency currency = exchange.getIn().getBody(Currency.class);
                    String country = currency.getCountry();
                    String currencyDataInCsv = "Country,Code,ExchangeRate,Amount,Date and time\n" + currency.toCsvString();

                    exchange.getIn().setBody(currencyDataInCsv);
                    exchange.getIn().setHeader("CamelFileName",country + "-currencyData.csv");
                })
                .log("Saving currency data to file...")
                //Define save destination path
                .to("file:///C:\\Users\\user\\Desktop?fileName=${header.CamelFileName}");
    }
}
