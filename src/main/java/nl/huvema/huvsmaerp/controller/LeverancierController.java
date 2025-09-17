package nl.huvema.huvsmaerp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import nl.huvema.huvsmaerp.dto.*;
import nl.huvema.huvsmaerp.storage.LeverancierRepository;
import nl.huvema.huvsmaerp.util.PaginationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/suppliers")
@Tag(name = "Suppliers", description = "Supplier management endpoints")
public class LeverancierController {
    
    @Autowired
    private LeverancierRepository leverancierRepository;

    @GetMapping
    @Operation(summary = "Get all suppliers", description = "Retrieve suppliers with filtering, pagination and sorting")
    public ResponseEntity<ApiResponse<List<LeverancierResponseDTO>>> getSuppliers(
            @Parameter(description = "Filter by supplier name (contains)") @RequestParam(required = false) String naam,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field and direction (e.g., 'naam,asc')") @RequestParam(required = false) String sort) {
        
        try {
            List<LeverancierResponseDTO> suppliers = leverancierRepository.findAll();
            
            // Apply filters
            if (naam != null && !naam.trim().isEmpty()) {
                suppliers = suppliers.stream()
                        .filter(s -> s.getNaam().toLowerCase().contains(naam.toLowerCase()))
                        .collect(Collectors.toList());
            }
            
            // Apply sorting and pagination
            suppliers = PaginationUtil.applySorting(suppliers, sort);
            List<LeverancierResponseDTO> pageContent = PaginationUtil.applyPagination(suppliers, page, size);
            
            return ResponseEntity.ok(PaginationUtil.createResponse(pageContent, page, size, suppliers.size()));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(null, List.of("Fout bij ophalen leveranciers: " + e.getMessage())));
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get supplier by ID", description = "Retrieve a specific supplier by its ID")
    public ResponseEntity<ApiResponse<LeverancierResponseDTO>> getSupplier(@PathVariable String id) {
        Optional<LeverancierResponseDTO> supplier = leverancierRepository.findById(id);
        if (supplier.isPresent()) {
            return ResponseEntity.ok(new ApiResponse<>(supplier.get()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @Operation(summary = "Create new supplier", description = "Create a new supplier with validation")
    public ResponseEntity<ApiResponse<LeverancierResponseDTO>> createSupplier(@Valid @RequestBody LeverancierRequestDTO request) {
        LeverancierResponseDTO supplier = new LeverancierResponseDTO();
        supplier.setNaam(request.getNaam());
        supplier.setKvkNummer(request.getKvkNummer());
        supplier.setContactEmail(request.getContactEmail());
        supplier.setTelefoon(request.getTelefoon());
        supplier.setAdres(request.getAdres());
        
        LeverancierResponseDTO savedSupplier = leverancierRepository.save(supplier);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(savedSupplier));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update supplier", description = "Update an existing supplier")
    public ResponseEntity<ApiResponse<LeverancierResponseDTO>> updateSupplier(@PathVariable String id, @Valid @RequestBody LeverancierRequestDTO request) {
        Optional<LeverancierResponseDTO> existingSupplier = leverancierRepository.findById(id);
        if (existingSupplier.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        LeverancierResponseDTO supplier = existingSupplier.get();
        supplier.setNaam(request.getNaam());
        supplier.setKvkNummer(request.getKvkNummer());
        supplier.setContactEmail(request.getContactEmail());
        supplier.setTelefoon(request.getTelefoon());
        supplier.setAdres(request.getAdres());
        
        LeverancierResponseDTO savedSupplier = leverancierRepository.save(supplier);
        return ResponseEntity.ok(new ApiResponse<>(savedSupplier));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete supplier", description = "Delete a supplier (409 if linked to products)")
    public ResponseEntity<ApiResponse<Map<String, String>>> deleteSupplier(@PathVariable String id) {
        Optional<LeverancierResponseDTO> supplier = leverancierRepository.findById(id);
        if (supplier.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        // TODO: Check if supplier is linked to products (would return 409)
        // For now, we'll just delete it
        
        leverancierRepository.deleteById(id);
        return ResponseEntity.ok(new ApiResponse<>(Map.of("message", "Leverancier succesvol verwijderd")));
    }
}
