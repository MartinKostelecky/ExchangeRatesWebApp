package cz.martinkostelecky.exchangerates.controller;

import cz.martinkostelecky.exchangerates.camel.CurrencyDataToFileRouter;
import cz.martinkostelecky.exchangerates.cnbweb.CNBwebProcessor;
import cz.martinkostelecky.exchangerates.cnbweb.Currency;
import cz.martinkostelecky.exchangerates.cnbweb.CurrencyDataNotFoundException;
import cz.martinkostelecky.exchangerates.cnbweb.UnexpectedWebPageStateException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@Slf4j
public class CNBController {

    private CNBwebProcessor cnbWebProcessor;

    private CurrencyDataToFileRouter currencyDataToFileRouter;

    public CNBController(CNBwebProcessor cnbWebProcessor, CurrencyDataToFileRouter currencyDataToFileRouter) {
        this.cnbWebProcessor = cnbWebProcessor;
        this.currencyDataToFileRouter = currencyDataToFileRouter;
    }

    @RequestMapping(value = "/exchangerates", method = GET)
    public String createExchangeRateRequest(Model model) {
        Currency currency = new Currency();
        model.addAttribute("currency", currency);
        return "exchangerates";
    }

    @RequestMapping(value = "/exchangerates", method = POST)
    public String getExchangeRateFromCNBwebpage(@Valid @ModelAttribute("currency") Currency currency, BindingResult bindingResult) throws UnexpectedWebPageStateException, CurrencyDataNotFoundException {
        log.info("Getting exchange rate for country: " + currency.getCountry());
        if (bindingResult.hasErrors()) {
            return "exchangerates";
        }

        currencyDataToFileRouter.getContext().createProducerTemplate().requestBody("direct:start", cnbWebProcessor.getCurrencyData(currency), String.class);

        cnbWebProcessor.getCurrencyData(currency);
        log.info("Redirecting to /exchangerates");

        return "exchangerates";
    }
}

