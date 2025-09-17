package nl.huvema.huvsmaerp.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class OrderResponseDTO {
    
    private String id;
    private String klantId;
    private LocalDate orderDatum;
    private OrderStatus status;
    private List<OrderRegelDTO> regels;
    private BigDecimal totaalExBtw;
    private BigDecimal btwBedrag;
    private BigDecimal totaalIncBtw;

    // Constructors
    public OrderResponseDTO() {}

    public OrderResponseDTO(String id, String klantId, LocalDate orderDatum, OrderStatus status, 
                           List<OrderRegelDTO> regels, BigDecimal totaalExBtw, 
                           BigDecimal btwBedrag, BigDecimal totaalIncBtw) {
        this.id = id;
        this.klantId = klantId;
        this.orderDatum = orderDatum;
        this.status = status;
        this.regels = regels;
        this.totaalExBtw = totaalExBtw;
        this.btwBedrag = btwBedrag;
        this.totaalIncBtw = totaalIncBtw;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getKlantId() { return klantId; }
    public void setKlantId(String klantId) { this.klantId = klantId; }

    public LocalDate getOrderDatum() { return orderDatum; }
    public void setOrderDatum(LocalDate orderDatum) { this.orderDatum = orderDatum; }

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }

    public List<OrderRegelDTO> getRegels() { return regels; }
    public void setRegels(List<OrderRegelDTO> regels) { this.regels = regels; }

    public BigDecimal getTotaalExBtw() { return totaalExBtw; }
    public void setTotaalExBtw(BigDecimal totaalExBtw) { this.totaalExBtw = totaalExBtw; }

    public BigDecimal getBtwBedrag() { return btwBedrag; }
    public void setBtwBedrag(BigDecimal btwBedrag) { this.btwBedrag = btwBedrag; }

    public BigDecimal getTotaalIncBtw() { return totaalIncBtw; }
    public void setTotaalIncBtw(BigDecimal totaalIncBtw) { this.totaalIncBtw = totaalIncBtw; }
}
