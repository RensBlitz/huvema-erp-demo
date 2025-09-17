package nl.huvema.huvsmaerp.config;

import nl.huvema.huvsmaerp.storage.SeedingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class StartupService implements CommandLineRunner {
    
    @Autowired
    private SeedingService seedingService;

    @Override
    public void run(String... args) throws Exception {
        // Seed data on startup
        seedingService.seedAllData();
        System.out.println("Huvsma ERP application started with seed data loaded.");
    }
}
