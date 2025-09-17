package nl.huvema.huvsmaerp.dto;

public class KlantResponseDTO {
    
    private String id;
    private String bedrijfsNaam;
    private String btwNummer;
    private String email;
    private String telefoon;
    private String factuurAdres;
    private String verzendAdres;

    // Constructors
    public KlantResponseDTO() {}

    public KlantResponseDTO(String id, String bedrijfsNaam, String btwNummer, String email, 
                           String telefoon, String factuurAdres, String verzendAdres) {
        this.id = id;
        this.bedrijfsNaam = bedrijfsNaam;
        this.btwNummer = btwNummer;
        this.email = email;
        this.telefoon = telefoon;
        this.factuurAdres = factuurAdres;
        this.verzendAdres = verzendAdres;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

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
