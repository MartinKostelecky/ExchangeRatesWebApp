package cz.martinkostelecky.exchangerates.cnbweb;

public interface CNBwebProcessor {

    public Currency getCurrencyData(Currency currency) throws UnexpectedWebPageStateException, CurrencyDataNotFoundException;

    //public void saveCurrencyDataToFile(Currency currency);
}
