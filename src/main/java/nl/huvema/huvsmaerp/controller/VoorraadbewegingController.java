package nl.huvema.huvsmaerp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import nl.huvema.huvsmaerp.dto.*;
import nl.huvema.huvsmaerp.storage.ProductRepository;
import nl.huvema.huvsmaerp.storage.VoorraadbewegingRepository;
import nl.huvema.huvsmaerp.util.PaginationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/stock-movements")
@Tag(name = "Stock Movements", description = "Stock movement management endpoints")
public class VoorraadbewegingController {
    
    @Autowired
    private VoorraadbewegingRepository voorraadbewegingRepository;
    
    @Autowired
    private ProductRepository productRepository;

    @GetMapping
    @Operation(summary = "Get all stock movements", description = "Retrieve stock movements with filtering, pagination and sorting")
    public ResponseEntity<ApiResponse<List<VoorraadbewegingResponseDTO>>> getStockMovements(
            @Parameter(description = "Filter by product ID") @RequestParam(required = false) String productId,
            @Parameter(description = "Filter by movement type") @RequestParam(required = false) MutatieType mutatieType,
            @Parameter(description = "Filter by date from") @RequestParam(required = false) LocalDate datumVan,
            @Parameter(description = "Filter by date to") @RequestParam(required = false) LocalDate datumTot,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field and direction (e.g., 'datum,desc')") @RequestParam(required = false) String sort) {
        
        try {
            List<VoorraadbewegingResponseDTO> movements = voorraadbewegingRepository.findAll();
            
            // Apply filters
            if (productId != null && !productId.trim().isEmpty()) {
                movements = movements.stream()
                        .filter(m -> m.getProductId().equals(productId))
                        .collect(Collectors.toList());
            }
            if (mutatieType != null) {
                movements = movements.stream()
                        .filter(m -> m.getMutatieType() == mutatieType)
                        .collect(Collectors.toList());
            }
            if (datumVan != null) {
                movements = movements.stream()
                        .filter(m -> !m.getDatum().isBefore(datumVan))
                        .collect(Collectors.toList());
            }
            if (datumTot != null) {
                movements = movements.stream()
                        .filter(m -> !m.getDatum().isAfter(datumTot))
                        .collect(Collectors.toList());
            }
            
            // Apply sorting and pagination
            movements = PaginationUtil.applySorting(movements, sort);
            List<VoorraadbewegingResponseDTO> pageContent = PaginationUtil.applyPagination(movements, page, size);
            
            return ResponseEntity.ok(PaginationUtil.createResponse(pageContent, page, size, movements.size()));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(null, List.of("Fout bij ophalen voorraadbewegingen: " + e.getMessage())));
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get stock movement by ID", description = "Retrieve a specific stock movement by its ID")
    public ResponseEntity<ApiResponse<VoorraadbewegingResponseDTO>> getStockMovement(@PathVariable String id) {
        Optional<VoorraadbewegingResponseDTO> movement = voorraadbewegingRepository.findById(id);
        if (movement.isPresent()) {
            return ResponseEntity.ok(new ApiResponse<>(movement.get()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @Operation(summary = "Create new stock movement", description = "Create a new stock movement and update product stock")
    public ResponseEntity<ApiResponse<VoorraadbewegingResponseDTO>> createStockMovement(@Valid @RequestBody VoorraadbewegingRequestDTO request) {
        // Validate product exists
        Optional<ProductResponseDTO> product = productRepository.findById(request.getProductId());
        if (product.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(null, List.of("Product met ID " + request.getProductId() + " niet gevonden")));
        }
        
        // Create stock movement
        VoorraadbewegingResponseDTO movement = new VoorraadbewegingResponseDTO();
        movement.setProductId(request.getProductId());
        movement.setMutatieType(request.getMutatieType());
        movement.setAantal(request.getAantal());
        movement.setDatum(request.getDatum());
        movement.setOpmerking(request.getOpmerking());
        
        VoorraadbewegingResponseDTO savedMovement = voorraadbewegingRepository.save(movement);
        
        // Update product stock
        ProductResponseDTO updatedProduct = product.get();
        int currentStock = updatedProduct.getVoorraadAantal();
        int newStock;
        
        switch (request.getMutatieType()) {
            case IN:
                newStock = currentStock + request.getAantal();
                break;
            case UIT:
                newStock = currentStock - request.getAantal();
                if (newStock < 0) {
                    return ResponseEntity.badRequest()
                            .body(new ApiResponse<>(null, List.of("Onvoldoende voorraad. Huidige voorraad: " + currentStock)));
                }
                break;
            case CORRECTIE:
                newStock = request.getAantal();
                break;
            default:
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(null, List.of("Onbekend mutatie type")));
        }
        
        updatedProduct.setVoorraadAantal(newStock);
        productRepository.save(updatedProduct);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(savedMovement));
    }
}
