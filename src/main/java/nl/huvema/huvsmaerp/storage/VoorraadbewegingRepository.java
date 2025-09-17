package nl.huvema.huvsmaerp.storage;

import nl.huvema.huvsmaerp.dto.*;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class VoorraadbewegingRepository {
    
    private final Map<String, VoorraadbewegingResponseDTO> bewegingen = new ConcurrentHashMap<>();
    private int nextId = 1001;

    public List<VoorraadbewegingResponseDTO> findAll() {
        return new ArrayList<>(bewegingen.values());
    }

    public Optional<VoorraadbewegingResponseDTO> findById(String id) {
        return Optional.ofNullable(bewegingen.get(id));
    }

    public VoorraadbewegingResponseDTO save(VoorraadbewegingResponseDTO beweging) {
        if (beweging.getId() == null) {
            beweging.setId("MOV-" + nextId++);
        }
        bewegingen.put(beweging.getId(), beweging);
        return beweging;
    }

    public void deleteById(String id) {
        bewegingen.remove(id);
    }

    public boolean existsById(String id) {
        return bewegingen.containsKey(id);
    }

    public List<VoorraadbewegingResponseDTO> findByProductId(String productId) {
        return bewegingen.values().stream()
                .filter(v -> v.getProductId().equals(productId))
                .sorted(Comparator.comparing(VoorraadbewegingResponseDTO::getDatum).reversed())
                .collect(Collectors.toList());
    }

    public List<VoorraadbewegingResponseDTO> findByMutatieType(MutatieType mutatieType) {
        return bewegingen.values().stream()
                .filter(v -> v.getMutatieType() == mutatieType)
                .collect(Collectors.toList());
    }

    public List<VoorraadbewegingResponseDTO> findByDatumBetween(LocalDate van, LocalDate tot) {
        return bewegingen.values().stream()
                .filter(v -> !v.getDatum().isBefore(van) && !v.getDatum().isAfter(tot))
                .collect(Collectors.toList());
    }

    public List<VoorraadbewegingResponseDTO> findLast5ByProductId(String productId) {
        return bewegingen.values().stream()
                .filter(v -> v.getProductId().equals(productId))
                .sorted(Comparator.comparing(VoorraadbewegingResponseDTO::getDatum).reversed())
                .limit(5)
                .collect(Collectors.toList());
    }

    public void clear() {
        bewegingen.clear();
        nextId = 1001;
    }

    public void seedData() {
        // Seed stock movements
        save(new VoorraadbewegingResponseDTO(null, "PRD-1001", MutatieType.IN, 5, 
                LocalDate.now().minusDays(10), "Initiële voorraad"));
        
        save(new VoorraadbewegingResponseDTO(null, "PRD-1001", MutatieType.UIT, 2, 
                LocalDate.now().minusDays(5), "Verkoop"));
        
        save(new VoorraadbewegingResponseDTO(null, "PRD-1002", MutatieType.IN, 2, 
                LocalDate.now().minusDays(8), "Initiële voorraad"));
        
        save(new VoorraadbewegingResponseDTO(null, "PRD-1003", MutatieType.IN, 100, 
                LocalDate.now().minusDays(15), "Bulk inkoop"));
        
        save(new VoorraadbewegingResponseDTO(null, "PRD-1003", MutatieType.UIT, 20, 
                LocalDate.now().minusDays(3), "Verkoop"));
        
        save(new VoorraadbewegingResponseDTO(null, "PRD-1004", MutatieType.CORRECTIE, 5, 
                LocalDate.now().minusDays(1), "Inventaris correctie"));
    }
}
