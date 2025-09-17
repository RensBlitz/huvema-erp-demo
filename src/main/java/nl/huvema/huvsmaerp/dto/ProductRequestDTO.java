package nl.huvema.huvsmaerp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class ProductRequestDTO {
    
    @NotBlank(message = "SKU is verplicht")
    @Size(max = 50, message = "SKU mag maximaal 50 karakters bevatten")
    private String sku;
    
    @NotBlank(message = "Naam is verplicht")
    @Size(max = 100, message = "Naam mag maximaal 100 karakters bevatten")
    private String naam;
    
    @Size(max = 500, message = "Beschrijving mag maximaal 500 karakters bevatten")
    private String beschrijving;
    
    @NotBlank(message = "Categorie is verplicht")
    @Size(max = 50, message = "Categorie mag maximaal 50 karakters bevatten")
    private String categorie;
    
    @NotNull(message = "Inkoop prijs is verplicht")
    @Positive(message = "Inkoop prijs moet positief zijn")
    private BigDecimal inkoopPrijs;
    
    @NotNull(message = "Verkoop prijs is verplicht")
    @Positive(message = "Verkoop prijs moet positief zijn")
    private BigDecimal verkoopPrijs;
    
    @NotNull(message = "Voorraad aantal is verplicht")
    @PositiveOrZero(message = "Voorraad aantal moet 0 of positief zijn")
    private Integer voorraadAantal;
    
    @NotBlank(message = "Leverancier ID is verplicht")
    @Size(max = 20, message = "Leverancier ID mag maximaal 20 karakters bevatten")
    private String leverancierId;

    // Constructors
    public ProductRequestDTO() {}

    public ProductRequestDTO(String sku, String naam, String beschrijving, String categorie, 
                           BigDecimal inkoopPrijs, BigDecimal verkoopPrijs, 
                           Integer voorraadAantal, String leverancierId) {
        this.sku = sku;
        this.naam = naam;
        this.beschrijving = beschrijving;
        this.categorie = categorie;
        this.inkoopPrijs = inkoopPrijs;
        this.verkoopPrijs = verkoopPrijs;
        this.voorraadAantal = voorraadAantal;
        this.leverancierId = leverancierId;
    }

    // Getters and Setters
    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public String getNaam() { return naam; }
    public void setNaam(String naam) { this.naam = naam; }

    public String getBeschrijving() { return beschrijving; }
    public void setBeschrijving(String beschrijving) { this.beschrijving = beschrijving; }

    public String getCategorie() { return categorie; }
    public void setCategorie(String categorie) { this.categorie = categorie; }

    public BigDecimal getInkoopPrijs() { return inkoopPrijs; }
    public void setInkoopPrijs(BigDecimal inkoopPrijs) { this.inkoopPrijs = inkoopPrijs; }

    public BigDecimal getVerkoopPrijs() { return verkoopPrijs; }
    public void setVerkoopPrijs(BigDecimal verkoopPrijs) { this.verkoopPrijs = verkoopPrijs; }

    public Integer getVoorraadAantal() { return voorraadAantal; }
    public void setVoorraadAantal(Integer voorraadAantal) { this.voorraadAantal = voorraadAantal; }

    public String getLeverancierId() { return leverancierId; }
    public void setLeverancierId(String leverancierId) { this.leverancierId = leverancierId; }
}
