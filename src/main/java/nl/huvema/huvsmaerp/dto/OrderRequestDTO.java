package nl.huvema.huvsmaerp.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

public class OrderRequestDTO {
    
    @NotBlank(message = "Klant ID is verplicht")
    private String klantId;
    
    @NotNull(message = "Order datum is verplicht")
    private LocalDate orderDatum;
    
    @NotEmpty(message = "Order regels zijn verplicht")
    @Valid
    private List<OrderRegelDTO> regels;

    // Constructors
    public OrderRequestDTO() {}

    public OrderRequestDTO(String klantId, LocalDate orderDatum, List<OrderRegelDTO> regels) {
        this.klantId = klantId;
        this.orderDatum = orderDatum;
        this.regels = regels;
    }

    // Getters and Setters
    public String getKlantId() { return klantId; }
    public void setKlantId(String klantId) { this.klantId = klantId; }

    public LocalDate getOrderDatum() { return orderDatum; }
    public void setOrderDatum(LocalDate orderDatum) { this.orderDatum = orderDatum; }

    public List<OrderRegelDTO> getRegels() { return regels; }
    public void setRegels(List<OrderRegelDTO> regels) { this.regels = regels; }
}
