package nl.huvema.huvsmaerp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class LeverancierRequestDTO {
    
    @NotBlank(message = "Naam is verplicht")
    @Size(max = 100, message = "Naam mag maximaal 100 karakters bevatten")
    private String naam;
    
    @Size(max = 20, message = "KvK nummer mag maximaal 20 karakters bevatten")
    private String kvkNummer;
    
    @NotBlank(message = "Contact email is verplicht")
    @Email(message = "Ongeldig email adres")
    @Size(max = 100, message = "Contact email mag maximaal 100 karakters bevatten")
    private String contactEmail;
    
    @Size(max = 20, message = "Telefoon mag maximaal 20 karakters bevatten")
    private String telefoon;
    
    @NotBlank(message = "Adres is verplicht")
    @Size(max = 200, message = "Adres mag maximaal 200 karakters bevatten")
    private String adres;

    // Constructors
    public LeverancierRequestDTO() {}

    public LeverancierRequestDTO(String naam, String kvkNummer, String contactEmail, 
                                String telefoon, String adres) {
        this.naam = naam;
        this.kvkNummer = kvkNummer;
        this.contactEmail = contactEmail;
        this.telefoon = telefoon;
        this.adres = adres;
    }

    // Getters and Setters
    public String getNaam() { return naam; }
    public void setNaam(String naam) { this.naam = naam; }

    public String getKvkNummer() { return kvkNummer; }
    public void setKvkNummer(String kvkNummer) { this.kvkNummer = kvkNummer; }

    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }

    public String getTelefoon() { return telefoon; }
    public void setTelefoon(String telefoon) { this.telefoon = telefoon; }

    public String getAdres() { return adres; }
    public void setAdres(String adres) { this.adres = adres; }
}
