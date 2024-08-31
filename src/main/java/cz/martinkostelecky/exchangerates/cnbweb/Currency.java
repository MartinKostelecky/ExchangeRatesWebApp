package cz.martinkostelecky.exchangerates.cnbweb;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Currency {

    @NotNull(message = "Zadejte zemi")
    @NotBlank(message = "Zadejte zemi")
    private String country;

    private String code;

    private String exchangeRate;

    private Integer amount;

    private LocalDateTime dateTime;

    public String toCsvString() {
        return country + "," + code + "," + exchangeRate + "," + amount + "," + dateTime;
    }
}
