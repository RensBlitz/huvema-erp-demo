package nl.huvema.huvsmaerp.storage;

import nl.huvema.huvsmaerp.dto.LeverancierResponseDTO;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class LeverancierRepository {
    
    private final Map<String, LeverancierResponseDTO> leveranciers = new ConcurrentHashMap<>();
    private int nextId = 1001;

    public List<LeverancierResponseDTO> findAll() {
        return new ArrayList<>(leveranciers.values());
    }

    public Optional<LeverancierResponseDTO> findById(String id) {
        return Optional.ofNullable(leveranciers.get(id));
    }

    public LeverancierResponseDTO save(LeverancierResponseDTO leverancier) {
        if (leverancier.getId() == null) {
            leverancier.setId("SUP-" + nextId++);
        }
        leveranciers.put(leverancier.getId(), leverancier);
        return leverancier;
    }

    public void deleteById(String id) {
        leveranciers.remove(id);
    }

    public boolean existsById(String id) {
        return leveranciers.containsKey(id);
    }

    public List<LeverancierResponseDTO> findByNaamContaining(String naam) {
        return leveranciers.values().stream()
                .filter(l -> l.getNaam().toLowerCase().contains(naam.toLowerCase()))
                .collect(Collectors.toList());
    }

    public void clear() {
        leveranciers.clear();
        nextId = 1001;
    }

    public void seedData() {
        // Seed suppliers
        save(new LeverancierResponseDTO(null, "Machine Tools International", "NL111111111B01", 
                "sales@machinetools.nl", "020-1111111", 
                "Machineweg 1, 1000 AB Amsterdam"));
        
        save(new LeverancierResponseDTO(null, "Precision Parts BV", "NL222222222B01", 
                "info@precisionparts.nl", "010-2222222", 
                "Precisieweg 2, 3000 AB Rotterdam"));
        
        save(new LeverancierResponseDTO(null, "Industrial Supplies", "NL333333333B01", 
                "contact@industrialsupplies.nl", "040-3333333", 
                "Industrieweg 3, 5600 AB Eindhoven"));
    }
}
