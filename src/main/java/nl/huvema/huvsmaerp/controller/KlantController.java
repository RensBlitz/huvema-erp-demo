package nl.huvema.huvsmaerp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import nl.huvema.huvsmaerp.dto.*;
import nl.huvema.huvsmaerp.storage.KlantRepository;
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
@RequestMapping("/api/v1/customers")
@Tag(name = "Customers", description = "Customer management endpoints")
public class KlantController {
    
    @Autowired
    private KlantRepository klantRepository;

    @GetMapping
    @Operation(summary = "Get all customers", description = "Retrieve customers with filtering, pagination and sorting")
    public ResponseEntity<ApiResponse<List<KlantResponseDTO>>> getCustomers(
            @Parameter(description = "Filter by company name (contains)") @RequestParam(required = false) String bedrijfsNaam,
            @Parameter(description = "Filter by VAT number (exact)") @RequestParam(required = false) String btwNummer,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field and direction (e.g., 'bedrijfsNaam,asc')") @RequestParam(required = false) String sort) {
        
        try {
            List<KlantResponseDTO> customers = klantRepository.findAll();
            
            // Apply filters
            if (bedrijfsNaam != null && !bedrijfsNaam.trim().isEmpty()) {
                customers = customers.stream()
                        .filter(k -> k.getBedrijfsNaam().toLowerCase().contains(bedrijfsNaam.toLowerCase()))
                        .collect(Collectors.toList());
            }
            if (btwNummer != null && !btwNummer.trim().isEmpty()) {
                customers = customers.stream()
                        .filter(k -> btwNummer.equals(k.getBtwNummer()))
                        .collect(Collectors.toList());
            }
            
            // Apply sorting and pagination
            customers = PaginationUtil.applySorting(customers, sort);
            List<KlantResponseDTO> pageContent = PaginationUtil.applyPagination(customers, page, size);
            
            return ResponseEntity.ok(PaginationUtil.createResponse(pageContent, page, size, customers.size()));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(null, List.of("Fout bij ophalen klanten: " + e.getMessage())));
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get customer by ID", description = "Retrieve a specific customer by its ID")
    public ResponseEntity<ApiResponse<KlantResponseDTO>> getCustomer(@PathVariable String id) {
        Optional<KlantResponseDTO> customer = klantRepository.findById(id);
        if (customer.isPresent()) {
            return ResponseEntity.ok(new ApiResponse<>(customer.get()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @Operation(summary = "Create new customer", description = "Create a new customer with validation")
    public ResponseEntity<ApiResponse<KlantResponseDTO>> createCustomer(@Valid @RequestBody KlantRequestDTO request) {
        KlantResponseDTO customer = new KlantResponseDTO();
        customer.setBedrijfsNaam(request.getBedrijfsNaam());
        customer.setBtwNummer(request.getBtwNummer());
        customer.setEmail(request.getEmail());
        customer.setTelefoon(request.getTelefoon());
        customer.setFactuurAdres(request.getFactuurAdres());
        customer.setVerzendAdres(request.getVerzendAdres());
        
        KlantResponseDTO savedCustomer = klantRepository.save(customer);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(savedCustomer));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update customer", description = "Update an existing customer")
    public ResponseEntity<ApiResponse<KlantResponseDTO>> updateCustomer(@PathVariable String id, @Valid @RequestBody KlantRequestDTO request) {
        Optional<KlantResponseDTO> existingCustomer = klantRepository.findById(id);
        if (existingCustomer.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        KlantResponseDTO customer = existingCustomer.get();
        customer.setBedrijfsNaam(request.getBedrijfsNaam());
        customer.setBtwNummer(request.getBtwNummer());
        customer.setEmail(request.getEmail());
        customer.setTelefoon(request.getTelefoon());
        customer.setFactuurAdres(request.getFactuurAdres());
        customer.setVerzendAdres(request.getVerzendAdres());
        
        KlantResponseDTO savedCustomer = klantRepository.save(customer);
        return ResponseEntity.ok(new ApiResponse<>(savedCustomer));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete customer", description = "Delete a customer (409 if has open orders/invoices)")
    public ResponseEntity<ApiResponse<Map<String, String>>> deleteCustomer(@PathVariable String id) {
        Optional<KlantResponseDTO> customer = klantRepository.findById(id);
        if (customer.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        // TODO: Check if customer has open orders/invoices (would return 409)
        // For now, we'll just delete it
        
        klantRepository.deleteById(id);
        return ResponseEntity.ok(new ApiResponse<>(Map.of("message", "Klant succesvol verwijderd")));
    }
}
