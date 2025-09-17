package nl.huvema.huvsmaerp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class KlantRequestDTO {
    
    @NotBlank(message = "Bedrijfsnaam is verplicht")
    @Size(max = 100, message = "Bedrijfsnaam mag maximaal 100 karakters bevatten")
    private String bedrijfsNaam;
    
    @Size(max = 20, message = "BTW nummer mag maximaal 20 karakters bevatten")
    private String btwNummer;
    
    @NotBlank(message = "Email is verplicht")
    @Email(message = "Ongeldig email adres")
    @Size(max = 100, message = "Email mag maximaal 100 karakters bevatten")
    private String email;
    
    @Size(max = 20, message = "Telefoon mag maximaal 20 karakters bevatten")
    private String telefoon;
    
    @NotBlank(message = "Factuuradres is verplicht")
    @Size(max = 200, message = "Factuuradres mag maximaal 200 karakters bevatten")
    private String factuurAdres;
    
    @NotBlank(message = "Verzendadres is verplicht")
    @Size(max = 200, message = "Verzendadres mag maximaal 200 karakters bevatten")
    private String verzendAdres;

    // Constructors
    public KlantRequestDTO() {}

    public KlantRequestDTO(String bedrijfsNaam, String btwNummer, String email, 
                          String telefoon, String factuurAdres, String verzendAdres) {
        this.bedrijfsNaam = bedrijfsNaam;
        this.btwNummer = btwNummer;
        this.email = email;
        this.telefoon = telefoon;
        this.factuurAdres = factuurAdres;
        this.verzendAdres = verzendAdres;
    }

    // Getters and Setters
    public String getBedrijfsNaam() { return bedrijfsNaam; }
    public void setBedrijfsNaam(String bedrijfsNaam) { this.bedrijfsNaam = bedrijfsNaam; }

    public String getBtwNummer() { return btwNummer; }
    public void setBtwNummer(String btwNummer) { this.btwNummer = btwNummer; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefoon() { return telefoon; }
    public void setTelefoon(String telefoon) { this.telefoon = telefoon; }

    public String getFactuurAdres() { return factuurAdres; }
    public void setFactuurAdres(String factuurAdres) { this.factuurAdres = factuurAdres; }

    public String getVerzendAdres() { return verzendAdres; }
    public void setVerzendAdres(String verzendAdres) { this.verzendAdres = verzendAdres; }
}
