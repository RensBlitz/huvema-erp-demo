package nl.huvema.huvsmaerp.controller;

import nl.huvema.huvsmaerp.dto.ApiResponse;
import nl.huvema.huvsmaerp.storage.SeedingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/_admin")
public class AdminController {
    
    @Autowired
    private SeedingService seedingService;

    @PostMapping("/reset")
    public ResponseEntity<ApiResponse<Map<String, String>>> reset() {
        seedingService.seedAllData();
        
        Map<String, String> response = Map.of(
            "message", "Alle data is gereset naar de initiële seed data",
            "timestamp", java.time.LocalDateTime.now().toString()
        );
        
        return ResponseEntity.ok(new ApiResponse<>(response));
    }
}
