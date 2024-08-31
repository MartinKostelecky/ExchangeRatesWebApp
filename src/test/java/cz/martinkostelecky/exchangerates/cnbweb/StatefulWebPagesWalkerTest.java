package cz.martinkostelecky.exchangerates.cnbweb;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class StatefulWebPagesWalkerTest {

    @Autowired
    private CNBwebProcessor cnbWebProcessor;

    @Test
    void shouldLoadDataFromCNBWeb() throws UnexpectedWebPageStateException, CurrencyDataNotFoundException {
        //Given
        Currency currency = new Currency();
        currency.setCountry("Maďarsko");

        //When
        Currency returnedCurrency = cnbWebProcessor.getCurrencyData(currency);

        //Then
        assertNotNull(returnedCurrency);
        assertEquals("HUF", returnedCurrency.getCode());
        assertEquals(100, returnedCurrency.getAmount());
    }

    @Test
    void shouldThrowException_And_NotLoadDataFromCNBWeb() {
        //Given
        Currency currency = new Currency();
        currency.setCountry("Morava");

        //When
        CurrencyDataNotFoundException currencyDataNotFoundException = assertThrows(CurrencyDataNotFoundException.class, () -> cnbWebProcessor.getCurrencyData(currency));

        //Then
        assertEquals("Requested data not found.", currencyDataNotFoundException.getMessage());
    }
}