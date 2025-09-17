package nl.huvema.huvsmaerp.storage;

import nl.huvema.huvsmaerp.dto.ProductResponseDTO;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class ProductRepository {
    
    private final Map<String, ProductResponseDTO> products = new ConcurrentHashMap<>();
    private int nextId = 1001;

    public List<ProductResponseDTO> findAll() {
        return new ArrayList<>(products.values());
    }

    public Optional<ProductResponseDTO> findById(String id) {
        return Optional.ofNullable(products.get(id));
    }

    public Optional<ProductResponseDTO> findBySku(String sku) {
        return products.values().stream()
                .filter(p -> p.getSku().equals(sku))
                .findFirst();
    }

    public ProductResponseDTO save(ProductResponseDTO product) {
        if (product.getId() == null) {
            product.setId("PRD-" + nextId++);
        }
        products.put(product.getId(), product);
        return product;
    }

    public void deleteById(String id) {
        products.remove(id);
    }

    public boolean existsById(String id) {
        return products.containsKey(id);
    }

    public List<ProductResponseDTO> findByCategorie(String categorie) {
        return products.values().stream()
                .filter(p -> p.getCategorie().equals(categorie))
                .collect(Collectors.toList());
    }

    public List<ProductResponseDTO> findByLeverancierId(String leverancierId) {
        return products.values().stream()
                .filter(p -> p.getLeverancierId().equals(leverancierId))
                .collect(Collectors.toList());
    }

    public List<ProductResponseDTO> findByNaamContaining(String naam) {
        return products.values().stream()
                .filter(p -> p.getNaam().toLowerCase().contains(naam.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<ProductResponseDTO> findByVerkoopPrijsBetween(BigDecimal min, BigDecimal max) {
        return products.values().stream()
                .filter(p -> p.getVerkoopPrijs().compareTo(min) >= 0 && p.getVerkoopPrijs().compareTo(max) <= 0)
                .collect(Collectors.toList());
    }

    public void clear() {
        products.clear();
        nextId = 1001;
    }

    public void seedData() {
        // Seed products
        save(new ProductResponseDTO(null, "MACH-001", "Draaibank", "Precisie draaibank voor metaalbewerking", 
                "Machines", new BigDecimal("25000.00"), new BigDecimal("35000.00"), 2, "SUP-001"));
        
        save(new ProductResponseDTO(null, "MACH-002", "Freesmachine", "CNC freesmachine voor complexe bewerkingen", 
                "Machines", new BigDecimal("45000.00"), new BigDecimal("65000.00"), 1, "SUP-001"));
        
        save(new ProductResponseDTO(null, "OND-001", "Snijplaatje", "HSS snijplaatje 10mm", 
                "Onderdelen", new BigDecimal("15.50"), new BigDecimal("25.00"), 50, "SUP-002"));
        
        save(new ProductResponseDTO(null, "OND-002", "Boor 8mm", "HSS boor 8mm x 100mm", 
                "Onderdelen", new BigDecimal("8.75"), new BigDecimal("15.00"), 100, "SUP-002"));
        
        save(new ProductResponseDTO(null, "OND-003", "Moer M8", "Stalen moer M8", 
                "Onderdelen", new BigDecimal("0.25"), new BigDecimal("0.50"), 500, "SUP-003"));
        
        save(new ProductResponseDTO(null, "MACH-003", "Lasapparaat", "MIG/MAG lasapparaat 200A", 
                "Machines", new BigDecimal("1200.00"), new BigDecimal("1800.00"), 3, "SUP-003"));
        
        save(new ProductResponseDTO(null, "OND-004", "Elektrode 3.25", "Rutiel elektrode 3.25mm", 
                "Onderdelen", new BigDecimal("0.85"), new BigDecimal("1.50"), 200, "SUP-003"));
        
        save(new ProductResponseDTO(null, "MACH-004", "Slijpmachine", "Bandenlijpmachine 75x2000mm", 
                "Machines", new BigDecimal("3500.00"), new BigDecimal("5200.00"), 1, "SUP-001"));
    }
}
