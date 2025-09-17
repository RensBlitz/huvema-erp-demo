package nl.huvema.huvsmaerp.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SeedingService {
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private KlantRepository klantRepository;
    
    @Autowired
    private LeverancierRepository leverancierRepository;
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private VoorraadbewegingRepository voorraadbewegingRepository;
    
    @Autowired
    private FactuurRepository factuurRepository;

    public void seedAllData() {
        // Clear existing data
        clearAllData();
        
        // Seed in order to maintain referential integrity
        leverancierRepository.seedData();
        productRepository.seedData();
        klantRepository.seedData();
        orderRepository.seedData();
        voorraadbewegingRepository.seedData();
        factuurRepository.seedData();
    }

    public void clearAllData() {
        factuurRepository.clear();
        voorraadbewegingRepository.clear();
        orderRepository.clear();
        klantRepository.clear();
        productRepository.clear();
        leverancierRepository.clear();
    }
}
