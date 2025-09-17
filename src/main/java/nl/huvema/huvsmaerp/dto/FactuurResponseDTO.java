package nl.huvema.huvsmaerp.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class FactuurResponseDTO {
    
    private String id;
    private String orderId;
    private LocalDate factuurDatum;
    private LocalDate vervalDatum;
    private FactuurStatus status;
    private BigDecimal totaalIncBtw;

    // Constructors
    public FactuurResponseDTO() {}

    public FactuurResponseDTO(String id, String orderId, LocalDate factuurDatum, 
                             LocalDate vervalDatum, FactuurStatus status, BigDecimal totaalIncBtw) {
        this.id = id;
        this.orderId = orderId;
        this.factuurDatum = factuurDatum;
        this.vervalDatum = vervalDatum;
        this.status = status;
        this.totaalIncBtw = totaalIncBtw;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public LocalDate getFactuurDatum() { return factuurDatum; }
    public void setFactuurDatum(LocalDate factuurDatum) { this.factuurDatum = factuurDatum; }

    public LocalDate getVervalDatum() { return vervalDatum; }
    public void setVervalDatum(LocalDate vervalDatum) { this.vervalDatum = vervalDatum; }

    public FactuurStatus getStatus() { return status; }
    public void setStatus(FactuurStatus status) { this.status = status; }

    public BigDecimal getTotaalIncBtw() { return totaalIncBtw; }
    public void setTotaalIncBtw(BigDecimal totaalIncBtw) { this.totaalIncBtw = totaalIncBtw; }
}
