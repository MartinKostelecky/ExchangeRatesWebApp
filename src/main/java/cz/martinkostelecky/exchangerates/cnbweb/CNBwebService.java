package cz.martinkostelecky.exchangerates.cnbweb;

import org.springframework.stereotype.Service;

@Service
public class CNBwebService implements CNBwebProcessor {

    private CNBwebPagesWalker statefulWebPagesWalker;

    public CNBwebService(CNBwebPagesWalker statefulWebPagesWalker) {
        this.statefulWebPagesWalker = statefulWebPagesWalker;
    }

    public Currency getCurrencyData(Currency currency) throws UnexpectedWebPageStateException, CurrencyDataNotFoundException {
        return statefulWebPagesWalker.loadDataFromCNBWeb(currency);
    }

    /*public void saveCurrencyDataToFile(Currency currency) {

    }*/
}
