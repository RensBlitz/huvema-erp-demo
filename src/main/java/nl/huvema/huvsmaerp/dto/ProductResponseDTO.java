package nl.huvema.huvsmaerp.dto;

import java.math.BigDecimal;

public class ProductResponseDTO {
    
    private String id;
    private String sku;
    private String naam;
    private String beschrijving;
    private String categorie;
    private BigDecimal inkoopPrijs;
    private BigDecimal verkoopPrijs;
    private Integer voorraadAantal;
    private String leverancierId;

    // Constructors
    public ProductResponseDTO() {}

    public ProductResponseDTO(String id, String sku, String naam, String beschrijving, String categorie, 
                            BigDecimal inkoopPrijs, BigDecimal verkoopPrijs, 
                            Integer voorraadAantal, String leverancierId) {
        this.id = id;
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
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

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
