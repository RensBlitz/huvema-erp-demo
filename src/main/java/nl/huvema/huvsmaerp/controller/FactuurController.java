package nl.huvema.huvsmaerp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import nl.huvema.huvsmaerp.dto.*;
import nl.huvema.huvsmaerp.storage.FactuurRepository;
import nl.huvema.huvsmaerp.storage.OrderRepository;
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
@RequestMapping("/api/v1/invoices")
@Tag(name = "Invoices", description = "Invoice management endpoints")
public class FactuurController {
    
    @Autowired
    private FactuurRepository factuurRepository;
    
    @Autowired
    private OrderRepository orderRepository;

    @GetMapping
    @Operation(summary = "Get all invoices", description = "Retrieve invoices with filtering, pagination and sorting")
    public ResponseEntity<ApiResponse<List<FactuurResponseDTO>>> getInvoices(
            @Parameter(description = "Filter by status") @RequestParam(required = false) FactuurStatus status,
            @Parameter(description = "Filter by order ID") @RequestParam(required = false) String orderId,
            @Parameter(description = "Filter by date from") @RequestParam(required = false) LocalDate datumVan,
            @Parameter(description = "Filter by date to") @RequestParam(required = false) LocalDate datumTot,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field and direction (e.g., 'factuurDatum,desc')") @RequestParam(required = false) String sort) {
        
        try {
            List<FactuurResponseDTO> invoices = factuurRepository.findAll();
            
            // Apply filters
            if (status != null) {
                invoices = invoices.stream()
                        .filter(f -> f.getStatus() == status)
                        .collect(Collectors.toList());
            }
            if (orderId != null && !orderId.trim().isEmpty()) {
                invoices = invoices.stream()
                        .filter(f -> f.getOrderId().equals(orderId))
                        .collect(Collectors.toList());
            }
            if (datumVan != null) {
                invoices = invoices.stream()
                        .filter(f -> !f.getFactuurDatum().isBefore(datumVan))
                        .collect(Collectors.toList());
            }
            if (datumTot != null) {
                invoices = invoices.stream()
                        .filter(f -> !f.getFactuurDatum().isAfter(datumTot))
                        .collect(Collectors.toList());
            }
            
            // Apply sorting and pagination
            invoices = PaginationUtil.applySorting(invoices, sort);
            List<FactuurResponseDTO> pageContent = PaginationUtil.applyPagination(invoices, page, size);
            
            return ResponseEntity.ok(PaginationUtil.createResponse(pageContent, page, size, invoices.size()));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(null, List.of("Fout bij ophalen facturen: " + e.getMessage())));
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get invoice by ID", description = "Retrieve a specific invoice by its ID")
    public ResponseEntity<ApiResponse<FactuurResponseDTO>> getInvoice(@PathVariable String id) {
        Optional<FactuurResponseDTO> invoice = factuurRepository.findById(id);
        if (invoice.isPresent()) {
            return ResponseEntity.ok(new ApiResponse<>(invoice.get()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @Operation(summary = "Create new invoice", description = "Create a new invoice for an existing order")
    public ResponseEntity<ApiResponse<FactuurResponseDTO>> createInvoice(@Valid @RequestBody FactuurRequestDTO request) {
        // Validate order exists
        Optional<OrderResponseDTO> order = orderRepository.findById(request.getOrderId());
        if (order.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(null, List.of("Order met ID " + request.getOrderId() + " niet gevonden")));
        }
        
        // Check if invoice already exists for this order
        List<FactuurResponseDTO> existingInvoices = factuurRepository.findByOrderId(request.getOrderId());
        if (!existingInvoices.isEmpty()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ApiResponse<>(null, List.of("Factuur voor order " + request.getOrderId() + " bestaat al")));
        }
        
        // Create invoice
        FactuurResponseDTO invoice = new FactuurResponseDTO();
        invoice.setOrderId(request.getOrderId());
        invoice.setFactuurDatum(request.getFactuurDatum());
        invoice.setVervalDatum(request.getFactuurDatum().plusDays(30));
        invoice.setStatus(FactuurStatus.OPEN);
        invoice.setTotaalIncBtw(order.get().getTotaalIncBtw());
        
        FactuurResponseDTO savedInvoice = factuurRepository.save(invoice);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(savedInvoice));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update invoice status", description = "Update invoice status (only OPEN -> BETAALD or OPEN -> TE_LAAT)")
    public ResponseEntity<ApiResponse<FactuurResponseDTO>> updateInvoiceStatus(@PathVariable String id, @RequestBody Map<String, FactuurStatus> statusUpdate) {
        Optional<FactuurResponseDTO> existingInvoice = factuurRepository.findById(id);
        if (existingInvoice.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        FactuurResponseDTO invoice = existingInvoice.get();
        FactuurStatus newStatus = statusUpdate.get("status");
        FactuurStatus currentStatus = invoice.getStatus();
        
        // Validate status transitions
        if (currentStatus != FactuurStatus.OPEN || (newStatus != FactuurStatus.BETAALD && newStatus != FactuurStatus.TE_LAAT)) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(null, List.of("Ongeldige status overgang van " + currentStatus + " naar " + newStatus + ". Alleen OPEN -> BETAALD of OPEN -> TE_LAAT toegestaan")));
        }
        
        invoice.setStatus(newStatus);
        FactuurResponseDTO savedInvoice = factuurRepository.save(invoice);
        return ResponseEntity.ok(new ApiResponse<>(savedInvoice));
    }
}
