package nl.huvema.huvsmaerp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public class OrderRegelDTO {
    
    @NotBlank(message = "Product ID is verplicht")
    private String productId;
    
    @NotNull(message = "Aantal is verplicht")
    @Positive(message = "Aantal moet positief zijn")
    private Integer aantal;
    
    @NotNull(message = "Stuks prijs is verplicht")
    @Positive(message = "Stuks prijs moet positief zijn")
    private BigDecimal stuksPrijs;
    
    private BigDecimal regelTotaal;

    // Constructors
    public OrderRegelDTO() {}

    public OrderRegelDTO(String productId, Integer aantal, BigDecimal stuksPrijs) {
        this.productId = productId;
        this.aantal = aantal;
        this.stuksPrijs = stuksPrijs;
        this.regelTotaal = stuksPrijs.multiply(BigDecimal.valueOf(aantal));
    }

    // Getters and Setters
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public Integer getAantal() { return aantal; }
    public void setAantal(Integer aantal) { 
        this.aantal = aantal;
        if (this.stuksPrijs != null) {
            this.regelTotaal = this.stuksPrijs.multiply(BigDecimal.valueOf(aantal));
        }
    }

    public BigDecimal getStuksPrijs() { return stuksPrijs; }
    public void setStuksPrijs(BigDecimal stuksPrijs) { 
        this.stuksPrijs = stuksPrijs;
        if (this.aantal != null) {
            this.regelTotaal = stuksPrijs.multiply(BigDecimal.valueOf(aantal));
        }
    }

    public BigDecimal getRegelTotaal() { return regelTotaal; }
    public void setRegelTotaal(BigDecimal regelTotaal) { this.regelTotaal = regelTotaal; }
}
