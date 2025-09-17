package nl.huvema.huvsmaerp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class FactuurRequestDTO {
    
    @NotBlank(message = "Order ID is verplicht")
    private String orderId;
    
    @NotNull(message = "Factuur datum is verplicht")
    private LocalDate factuurDatum;

    // Constructors
    public FactuurRequestDTO() {}

    public FactuurRequestDTO(String orderId, LocalDate factuurDatum) {
        this.orderId = orderId;
        this.factuurDatum = factuurDatum;
    }

    // Getters and Setters
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public LocalDate getFactuurDatum() { return factuurDatum; }
    public void setFactuurDatum(LocalDate factuurDatum) { this.factuurDatum = factuurDatum; }
}
