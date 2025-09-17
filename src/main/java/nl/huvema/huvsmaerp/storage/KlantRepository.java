package nl.huvema.huvsmaerp.storage;

import nl.huvema.huvsmaerp.dto.KlantResponseDTO;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class KlantRepository {
    
    private final Map<String, KlantResponseDTO> klanten = new ConcurrentHashMap<>();
    private int nextId = 1001;

    public List<KlantResponseDTO> findAll() {
        return new ArrayList<>(klanten.values());
    }

    public Optional<KlantResponseDTO> findById(String id) {
        return Optional.ofNullable(klanten.get(id));
    }

    public KlantResponseDTO save(KlantResponseDTO klant) {
        if (klant.getId() == null) {
            klant.setId("CUS-" + nextId++);
        }
        klanten.put(klant.getId(), klant);
        return klant;
    }

    public void deleteById(String id) {
        klanten.remove(id);
    }

    public boolean existsById(String id) {
        return klanten.containsKey(id);
    }

    public List<KlantResponseDTO> findByBedrijfsNaamContaining(String bedrijfsNaam) {
        return klanten.values().stream()
                .filter(k -> k.getBedrijfsNaam().toLowerCase().contains(bedrijfsNaam.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<KlantResponseDTO> findByBtwNummer(String btwNummer) {
        return klanten.values().stream()
                .filter(k -> btwNummer.equals(k.getBtwNummer()))
                .collect(Collectors.toList());
    }

    public void clear() {
        klanten.clear();
        nextId = 1001;
    }

    public void seedData() {
        // Seed customers
        save(new KlantResponseDTO(null, "Metaalwerken BV", "NL123456789B01", 
                "info@metaalwerken.nl", "020-1234567", 
                "Industrieweg 123, 1000 AB Amsterdam", 
                "Industrieweg 123, 1000 AB Amsterdam"));
        
        save(new KlantResponseDTO(null, "Constructie & Co", "NL987654321B01", 
                "contact@constructieco.nl", "010-7654321", 
                "Havenstraat 456, 3000 AB Rotterdam", 
                "Havenstraat 456, 3000 AB Rotterdam"));
        
        save(new KlantResponseDTO(null, "Precisie Techniek", "NL555666777B01", 
                "verkoop@precisietechniek.nl", "040-555666", 
                "Technieklaan 789, 5600 AB Eindhoven", 
                "Technieklaan 789, 5600 AB Eindhoven"));
        
        save(new KlantResponseDTO(null, "Machine Service", null, 
                "service@machineservice.nl", "030-999888", 
                "Serviceweg 321, 3500 AB Utrecht", 
                "Serviceweg 321, 3500 AB Utrecht"));
        
        save(new KlantResponseDTO(null, "Industrieel Onderhoud", "NL111222333B01", 
                "onderhoud@industriel.nl", "050-111222", 
                "Onderhoudsstraat 654, 9700 AB Groningen", 
                "Onderhoudsstraat 654, 9700 AB Groningen"));
    }
}
