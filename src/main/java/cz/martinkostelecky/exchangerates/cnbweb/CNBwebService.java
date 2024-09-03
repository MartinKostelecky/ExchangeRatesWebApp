package cz.martinkostelecky.exchangerates.cnbweb;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CNBwebService implements CNBwebProcessor {

    private final CNBwebPageWalker cnbWebPageWalker;

    public Currency getCurrencyData(Currency currency) throws UnexpectedWebPageStateException, CurrencyDataNotFoundException {
        return cnbWebPageWalker.loadDataFromCNBWeb(currency);
    }

    /*public void saveCurrencyDataToFile(Currency currency) {

    }*/
}
