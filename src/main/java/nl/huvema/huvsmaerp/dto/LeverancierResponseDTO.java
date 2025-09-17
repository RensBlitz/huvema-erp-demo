package nl.huvema.huvsmaerp.dto;

public class LeverancierResponseDTO {
    
    private String id;
    private String naam;
    private String kvkNummer;
    private String contactEmail;
    private String telefoon;
    private String adres;

    // Constructors
    public LeverancierResponseDTO() {}

    public LeverancierResponseDTO(String id, String naam, String kvkNummer, String contactEmail, 
                                 String telefoon, String adres) {
        this.id = id;
        this.naam = naam;
        this.kvkNummer = kvkNummer;
        this.contactEmail = contactEmail;
        this.telefoon = telefoon;
        this.adres = adres;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

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
