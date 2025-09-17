package nl.huvema.huvsmaerp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public class VoorraadbewegingRequestDTO {
    
    @NotBlank(message = "Product ID is verplicht")
    private String productId;
    
    @NotNull(message = "Mutatie type is verplicht")
    private MutatieType mutatieType;
    
    @NotNull(message = "Aantal is verplicht")
    @Positive(message = "Aantal moet positief zijn")
    private Integer aantal;
    
    @NotNull(message = "Datum is verplicht")
    private LocalDate datum;
    
    @Size(max = 200, message = "Opmerking mag maximaal 200 karakters bevatten")
    private String opmerking;

    // Constructors
    public VoorraadbewegingRequestDTO() {}

    public VoorraadbewegingRequestDTO(String productId, MutatieType mutatieType, Integer aantal, 
                                     LocalDate datum, String opmerking) {
        this.productId = productId;
        this.mutatieType = mutatieType;
        this.aantal = aantal;
        this.datum = datum;
        this.opmerking = opmerking;
    }

    // Getters and Setters
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public MutatieType getMutatieType() { return mutatieType; }
    public void setMutatieType(MutatieType mutatieType) { this.mutatieType = mutatieType; }

    public Integer getAantal() { return aantal; }
    public void setAantal(Integer aantal) { this.aantal = aantal; }

    public LocalDate getDatum() { return datum; }
    public void setDatum(LocalDate datum) { this.datum = datum; }

    public String getOpmerking() { return opmerking; }
    public void setOpmerking(String opmerking) { this.opmerking = opmerking; }
}
