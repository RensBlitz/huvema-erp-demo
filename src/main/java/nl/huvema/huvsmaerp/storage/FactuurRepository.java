package nl.huvema.huvsmaerp.storage;

import nl.huvema.huvsmaerp.dto.*;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class FactuurRepository {
    
    private final Map<String, FactuurResponseDTO> facturen = new ConcurrentHashMap<>();
    private int nextId = 1001;

    public List<FactuurResponseDTO> findAll() {
        return new ArrayList<>(facturen.values());
    }

    public Optional<FactuurResponseDTO> findById(String id) {
        return Optional.ofNullable(facturen.get(id));
    }

    public FactuurResponseDTO save(FactuurResponseDTO factuur) {
        if (factuur.getId() == null) {
            factuur.setId("INV-" + nextId++);
        }
        facturen.put(factuur.getId(), factuur);
        return factuur;
    }

    public void deleteById(String id) {
        facturen.remove(id);
    }

    public boolean existsById(String id) {
        return facturen.containsKey(id);
    }

    public List<FactuurResponseDTO> findByStatus(FactuurStatus status) {
        return facturen.values().stream()
                .filter(f -> f.getStatus() == status)
                .collect(Collectors.toList());
    }

    public List<FactuurResponseDTO> findByOrderId(String orderId) {
        return facturen.values().stream()
                .filter(f -> f.getOrderId().equals(orderId))
                .collect(Collectors.toList());
    }

    public List<FactuurResponseDTO> findByFactuurDatumBetween(LocalDate van, LocalDate tot) {
        return facturen.values().stream()
                .filter(f -> !f.getFactuurDatum().isBefore(van) && !f.getFactuurDatum().isAfter(tot))
                .collect(Collectors.toList());
    }

    public void clear() {
        facturen.clear();
        nextId = 1001;
    }

    public void seedData() {
        // Seed invoices
        save(new FactuurResponseDTO(null, "ORD-1001", LocalDate.now().minusDays(5), 
                LocalDate.now().plusDays(25), FactuurStatus.OPEN, new BigDecimal("35025.00")));
        
        save(new FactuurResponseDTO(null, "ORD-1002", LocalDate.now().minusDays(3), 
                LocalDate.now().plusDays(27), FactuurStatus.BETAALD, new BigDecimal("65075.00")));
        
        save(new FactuurResponseDTO(null, "ORD-1003", LocalDate.now().minusDays(10), 
                LocalDate.now().minusDays(5), FactuurStatus.TE_LAAT, new BigDecimal("1875.00")));
    }
}
