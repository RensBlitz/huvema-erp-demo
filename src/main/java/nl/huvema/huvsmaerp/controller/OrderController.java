package nl.huvema.huvsmaerp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import nl.huvema.huvsmaerp.dto.*;
import nl.huvema.huvsmaerp.storage.*;
import nl.huvema.huvsmaerp.util.PaginationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/orders")
@Tag(name = "Orders", description = "Order management endpoints")
public class OrderController {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private VoorraadbewegingRepository voorraadbewegingRepository;

    @GetMapping
    @Operation(summary = "Get all orders", description = "Retrieve orders with filtering, pagination and sorting")
    public ResponseEntity<ApiResponse<List<OrderResponseDTO>>> getOrders(
            @Parameter(description = "Filter by customer ID") @RequestParam(required = false) String klantId,
            @Parameter(description = "Filter by status") @RequestParam(required = false) OrderStatus status,
            @Parameter(description = "Filter by date from") @RequestParam(required = false) LocalDate datumVan,
            @Parameter(description = "Filter by date to") @RequestParam(required = false) LocalDate datumTot,
            @Parameter(description = "Filter by minimum total") @RequestParam(required = false) BigDecimal minTotaal,
            @Parameter(description = "Filter by maximum total") @RequestParam(required = false) BigDecimal maxTotaal,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field and direction (e.g., 'orderDatum,desc')") @RequestParam(required = false) String sort) {
        
        try {
            List<OrderResponseDTO> orders = orderRepository.findAll();
            
            // Apply filters
            if (klantId != null && !klantId.trim().isEmpty()) {
                orders = orders.stream()
                        .filter(o -> o.getKlantId().equals(klantId))
                        .collect(Collectors.toList());
            }
            if (status != null) {
                orders = orders.stream()
                        .filter(o -> o.getStatus() == status)
                        .collect(Collectors.toList());
            }
            if (datumVan != null) {
                orders = orders.stream()
                        .filter(o -> !o.getOrderDatum().isBefore(datumVan))
                        .collect(Collectors.toList());
            }
            if (datumTot != null) {
                orders = orders.stream()
                        .filter(o -> !o.getOrderDatum().isAfter(datumTot))
                        .collect(Collectors.toList());
            }
            if (minTotaal != null) {
                orders = orders.stream()
                        .filter(o -> o.getTotaalIncBtw().compareTo(minTotaal) >= 0)
                        .collect(Collectors.toList());
            }
            if (maxTotaal != null) {
                orders = orders.stream()
                        .filter(o -> o.getTotaalIncBtw().compareTo(maxTotaal) <= 0)
                        .collect(Collectors.toList());
            }
            
            // Apply sorting and pagination
            orders = PaginationUtil.applySorting(orders, sort);
            List<OrderResponseDTO> pageContent = PaginationUtil.applyPagination(orders, page, size);
            
            return ResponseEntity.ok(PaginationUtil.createResponse(pageContent, page, size, orders.size()));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(null, List.of("Fout bij ophalen orders: " + e.getMessage())));
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get order by ID", description = "Retrieve a specific order by its ID")
    public ResponseEntity<ApiResponse<OrderResponseDTO>> getOrder(@PathVariable String id) {
        Optional<OrderResponseDTO> order = orderRepository.findById(id);
        if (order.isPresent()) {
            return ResponseEntity.ok(new ApiResponse<>(order.get()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @Operation(summary = "Create new order", description = "Create a new order with validation and calculation")
    public ResponseEntity<ApiResponse<OrderResponseDTO>> createOrder(@Valid @RequestBody OrderRequestDTO request) {
        // Validate product IDs and quantities
        for (OrderRegelDTO regel : request.getRegels()) {
            Optional<ProductResponseDTO> product = productRepository.findById(regel.getProductId());
            if (product.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(null, List.of("Product met ID " + regel.getProductId() + " niet gevonden")));
            }
            if (regel.getAantal() <= 0) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(null, List.of("Aantal moet positief zijn voor product " + regel.getProductId())));
            }
        }
        
        // Calculate totals
        BigDecimal totaalExBtw = request.getRegels().stream()
                .map(OrderRegelDTO::getRegelTotaal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal btwBedrag = totaalExBtw.multiply(new BigDecimal("0.21"));
        BigDecimal totaalIncBtw = totaalExBtw.add(btwBedrag);
        
        OrderResponseDTO order = new OrderResponseDTO();
        order.setKlantId(request.getKlantId());
        order.setOrderDatum(request.getOrderDatum());
        order.setStatus(OrderStatus.NIEUW);
        order.setRegels(request.getRegels());
        order.setTotaalExBtw(totaalExBtw);
        order.setBtwBedrag(btwBedrag);
        order.setTotaalIncBtw(totaalIncBtw);
        
        OrderResponseDTO savedOrder = orderRepository.save(order);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(savedOrder));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update order status", description = "Update order status with business logic")
    public ResponseEntity<ApiResponse<OrderResponseDTO>> updateOrderStatus(@PathVariable String id, @RequestBody Map<String, OrderStatus> statusUpdate) {
        Optional<OrderResponseDTO> existingOrder = orderRepository.findById(id);
        if (existingOrder.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        OrderResponseDTO order = existingOrder.get();
        OrderStatus newStatus = statusUpdate.get("status");
        OrderStatus currentStatus = order.getStatus();
        
        // Validate status transitions
        if (!isValidStatusTransition(currentStatus, newStatus)) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(null, List.of("Ongeldige status overgang van " + currentStatus + " naar " + newStatus)));
        }
        
        order.setStatus(newStatus);
        
        // Handle stock reduction when order is delivered
        if (newStatus == OrderStatus.GELEVERD && currentStatus != OrderStatus.GELEVERD) {
            for (OrderRegelDTO regel : order.getRegels()) {
                // Update product stock
                Optional<ProductResponseDTO> product = productRepository.findById(regel.getProductId());
                if (product.isPresent()) {
                    ProductResponseDTO updatedProduct = product.get();
                    updatedProduct.setVoorraadAantal(updatedProduct.getVoorraadAantal() - regel.getAantal());
                    productRepository.save(updatedProduct);
                    
                    // Create stock movement
                    VoorraadbewegingResponseDTO beweging = new VoorraadbewegingResponseDTO();
                    beweging.setProductId(regel.getProductId());
                    beweging.setMutatieType(MutatieType.UIT);
                    beweging.setAantal(regel.getAantal());
                    beweging.setDatum(LocalDate.now());
                    beweging.setOpmerking("Order " + id + " geleverd");
                    voorraadbewegingRepository.save(beweging);
                }
            }
        }
        
        OrderResponseDTO savedOrder = orderRepository.save(order);
        return ResponseEntity.ok(new ApiResponse<>(savedOrder));
    }

    @PostMapping("/{id}/recalculate")
    @Operation(summary = "Recalculate order totals", description = "Recalculate order totals based on current product prices")
    public ResponseEntity<ApiResponse<OrderResponseDTO>> recalculateOrder(@PathVariable String id) {
        Optional<OrderResponseDTO> existingOrder = orderRepository.findById(id);
        if (existingOrder.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        OrderResponseDTO order = existingOrder.get();
        
        // Recalculate totals with current product prices
        for (OrderRegelDTO regel : order.getRegels()) {
            Optional<ProductResponseDTO> product = productRepository.findById(regel.getProductId());
            if (product.isPresent()) {
                regel.setStuksPrijs(product.get().getVerkoopPrijs());
                regel.setRegelTotaal(regel.getStuksPrijs().multiply(BigDecimal.valueOf(regel.getAantal())));
            }
        }
        
        BigDecimal totaalExBtw = order.getRegels().stream()
                .map(OrderRegelDTO::getRegelTotaal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal btwBedrag = totaalExBtw.multiply(new BigDecimal("0.21"));
        BigDecimal totaalIncBtw = totaalExBtw.add(btwBedrag);
        
        order.setTotaalExBtw(totaalExBtw);
        order.setBtwBedrag(btwBedrag);
        order.setTotaalIncBtw(totaalIncBtw);
        
        OrderResponseDTO savedOrder = orderRepository.save(order);
        return ResponseEntity.ok(new ApiResponse<>(savedOrder));
    }

    private boolean isValidStatusTransition(OrderStatus from, OrderStatus to) {
        return (from == OrderStatus.NIEUW && (to == OrderStatus.IN_BEHANDELING || to == OrderStatus.GEANNULEERD)) ||
               (from == OrderStatus.IN_BEHANDELING && (to == OrderStatus.GELEVERD || to == OrderStatus.GEANNULEERD)) ||
               from == to; // Allow same status
    }
}
