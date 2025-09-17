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

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/products")
@Tag(name = "Products", description = "Product management endpoints")
public class ProductController {
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private VoorraadbewegingRepository voorraadbewegingRepository;

    @GetMapping
    @Operation(summary = "Get all products", description = "Retrieve products with filtering, pagination and sorting")
    public ResponseEntity<ApiResponse<List<ProductResponseDTO>>> getProducts(
            @Parameter(description = "Filter by product name (contains)") @RequestParam(required = false) String naam,
            @Parameter(description = "Filter by SKU") @RequestParam(required = false) String sku,
            @Parameter(description = "Filter by category") @RequestParam(required = false) String categorie,
            @Parameter(description = "Filter by supplier ID") @RequestParam(required = false) String leverancierId,
            @Parameter(description = "Minimum selling price") @RequestParam(required = false) BigDecimal verkoopPrijsMin,
            @Parameter(description = "Maximum selling price") @RequestParam(required = false) BigDecimal verkoopPrijsMax,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field and direction (e.g., 'naam,asc')") @RequestParam(required = false) String sort) {
        
        try {
            List<ProductResponseDTO> products = productRepository.findAll();
            
            // Apply filters
            if (naam != null && !naam.trim().isEmpty()) {
                products = products.stream()
                        .filter(p -> p.getNaam().toLowerCase().contains(naam.toLowerCase()))
                        .collect(Collectors.toList());
            }
            if (sku != null && !sku.trim().isEmpty()) {
                products = products.stream()
                        .filter(p -> p.getSku().equals(sku))
                        .collect(Collectors.toList());
            }
            if (categorie != null && !categorie.trim().isEmpty()) {
                products = products.stream()
                        .filter(p -> p.getCategorie().equals(categorie))
                        .collect(Collectors.toList());
            }
            if (leverancierId != null && !leverancierId.trim().isEmpty()) {
                products = products.stream()
                        .filter(p -> p.getLeverancierId().equals(leverancierId))
                        .collect(Collectors.toList());
            }
            if (verkoopPrijsMin != null) {
                products = products.stream()
                        .filter(p -> p.getVerkoopPrijs().compareTo(verkoopPrijsMin) >= 0)
                        .collect(Collectors.toList());
            }
            if (verkoopPrijsMax != null) {
                products = products.stream()
                        .filter(p -> p.getVerkoopPrijs().compareTo(verkoopPrijsMax) <= 0)
                        .collect(Collectors.toList());
            }
            
            // Apply sorting and pagination
            products = PaginationUtil.applySorting(products, sort);
            List<ProductResponseDTO> pageContent = PaginationUtil.applyPagination(products, page, size);
            
            return ResponseEntity.ok(PaginationUtil.createResponse(pageContent, page, size, products.size()));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(null, List.of("Fout bij ophalen producten: " + e.getMessage())));
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID", description = "Retrieve a specific product by its ID")
    public ResponseEntity<ApiResponse<ProductResponseDTO>> getProduct(@PathVariable String id) {
        Optional<ProductResponseDTO> product = productRepository.findById(id);
        if (product.isPresent()) {
            return ResponseEntity.ok(new ApiResponse<>(product.get()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @Operation(summary = "Create new product", description = "Create a new product with validation")
    public ResponseEntity<ApiResponse<ProductResponseDTO>> createProduct(@Valid @RequestBody ProductRequestDTO request) {
        // Check if SKU already exists
        if (productRepository.findBySku(request.getSku()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ApiResponse<>(null, List.of("SKU '" + request.getSku() + "' bestaat al")));
        }
        
        ProductResponseDTO product = new ProductResponseDTO();
        product.setSku(request.getSku());
        product.setNaam(request.getNaam());
        product.setBeschrijving(request.getBeschrijving());
        product.setCategorie(request.getCategorie());
        product.setInkoopPrijs(request.getInkoopPrijs());
        product.setVerkoopPrijs(request.getVerkoopPrijs());
        product.setVoorraadAantal(request.getVoorraadAantal());
        product.setLeverancierId(request.getLeverancierId());
        
        ProductResponseDTO savedProduct = productRepository.save(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(savedProduct));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update product", description = "Update an existing product")
    public ResponseEntity<ApiResponse<ProductResponseDTO>> updateProduct(@PathVariable String id, @Valid @RequestBody ProductRequestDTO request) {
        Optional<ProductResponseDTO> existingProduct = productRepository.findById(id);
        if (existingProduct.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        // Check if SKU already exists for different product
        Optional<ProductResponseDTO> skuProduct = productRepository.findBySku(request.getSku());
        if (skuProduct.isPresent() && !skuProduct.get().getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ApiResponse<>(null, List.of("SKU '" + request.getSku() + "' bestaat al")));
        }
        
        ProductResponseDTO product = existingProduct.get();
        product.setSku(request.getSku());
        product.setNaam(request.getNaam());
        product.setBeschrijving(request.getBeschrijving());
        product.setCategorie(request.getCategorie());
        product.setInkoopPrijs(request.getInkoopPrijs());
        product.setVerkoopPrijs(request.getVerkoopPrijs());
        product.setVoorraadAantal(request.getVoorraadAantal());
        product.setLeverancierId(request.getLeverancierId());
        
        ProductResponseDTO savedProduct = productRepository.save(product);
        return ResponseEntity.ok(new ApiResponse<>(savedProduct));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete product", description = "Delete a product (409 if used in orders)")
    public ResponseEntity<ApiResponse<Map<String, String>>> deleteProduct(@PathVariable String id) {
        Optional<ProductResponseDTO> product = productRepository.findById(id);
        if (product.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        // TODO: Check if product is used in orders (would return 409)
        // For now, we'll just delete it
        
        productRepository.deleteById(id);
        return ResponseEntity.ok(new ApiResponse<>(Map.of("message", "Product succesvol verwijderd")));
    }

    @GetMapping("/{id}/stock")
    @Operation(summary = "Get product stock info", description = "Get current stock and last 5 stock movements")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getProductStock(@PathVariable String id) {
        Optional<ProductResponseDTO> product = productRepository.findById(id);
        if (product.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        List<VoorraadbewegingResponseDTO> movements = voorraadbewegingRepository.findLast5ByProductId(id);
        
        Map<String, Object> stockInfo = Map.of(
            "productId", id,
            "huidigVoorraadAantal", product.get().getVoorraadAantal(),
            "laatsteBewegingen", movements
        );
        
        return ResponseEntity.ok(new ApiResponse<>(stockInfo));
    }
}
