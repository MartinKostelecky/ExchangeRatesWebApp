package cz.martinkostelecky.exchangerates.cnbweb;

public class CurrencyDataNotFoundException extends Exception {

    public CurrencyDataNotFoundException(String message, Throwable cause) {

    }

    public CurrencyDataNotFoundException(String message) {
        super(message);
    }
}
