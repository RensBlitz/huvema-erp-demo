package nl.huvema.huvsmaerp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.huvema.huvsmaerp.dto.*;
import nl.huvema.huvsmaerp.storage.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/mcp")
public class McpController {
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private VoorraadbewegingRepository voorraadbewegingRepository;
    
    @Autowired
    private FactuurRepository factuurRepository;
    
    private final Map<String, SseEmitter> sessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @GetMapping(value = "/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter createSseConnection(@RequestParam(required = false) String sessionId) {
        final String finalSessionId = sessionId != null ? sessionId : UUID.randomUUID().toString();
        
        SseEmitter emitter = new SseEmitter(120000L); // 2 minutes timeout
        sessions.put(finalSessionId, emitter);
        
        // Send keep-alive pings every 30 seconds
        scheduler.scheduleAtFixedRate(() -> {
            try {
                emitter.send(SseEmitter.event().name("ping").data(": ping\n\n"));
            } catch (IOException e) {
                sessions.remove(finalSessionId);
            }
        }, 30, 30, TimeUnit.SECONDS);
        
        emitter.onCompletion(() -> sessions.remove(finalSessionId));
        emitter.onTimeout(() -> sessions.remove(finalSessionId));
        emitter.onError((ex) -> sessions.remove(finalSessionId));
        
        return emitter;
    }

    @PostMapping("/messages")
    public ResponseEntity<Map<String, String>> handleMcpMessage(
            @RequestBody McpRequest request,
            @RequestHeader(value = "X-MCP-Session", required = false) String sessionId,
            @RequestParam(required = false) String session) {
        
        String actualSessionId = sessionId != null ? sessionId : session;
        if (actualSessionId == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Session ID required"));
        }
        
        SseEmitter emitter = sessions.get(actualSessionId);
        if (emitter == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Session not found"));
        }
        
        try {
            McpResponse response = processMcpRequest(request);
            String responseJson = objectMapper.writeValueAsString(response);
            emitter.send(SseEmitter.event().name("message").data(responseJson));
            
            return ResponseEntity.ok(Map.of("status", "Message sent to SSE stream"));
        } catch (Exception e) {
            try {
                McpResponse errorResponse = new McpResponse(request.getId(), 
                    new McpResponse.McpError(-32603, "Internal error: " + e.getMessage()));
                String errorJson = objectMapper.writeValueAsString(errorResponse);
                emitter.send(SseEmitter.event().name("message").data(errorJson));
            } catch (IOException ioException) {
                // Ignore
            }
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    private McpResponse processMcpRequest(McpRequest request) {
        switch (request.getMethod()) {
            case "tools/list":
                return handleToolsList(request);
            case "tools/call":
                return handleToolsCall(request);
            default:
                return new McpResponse(request.getId(), 
                    new McpResponse.McpError(-32601, "Method not found: " + request.getMethod()));
        }
    }

    private McpResponse handleToolsList(McpRequest request) {
        List<McpTool> tools = Arrays.asList(
            new McpTool("huvsma.products.search", "Search products with filtering and pagination", 
                Map.of(
                    "type", "object",
                    "properties", Map.of(
                        "naam", Map.of("type", "string", "description", "Filter by product name (contains)"),
                        "sku", Map.of("type", "string", "description", "Filter by SKU"),
                        "categorie", Map.of("type", "string", "description", "Filter by category"),
                        "leverancierId", Map.of("type", "string", "description", "Filter by supplier ID"),
                        "verkoopPrijsMin", Map.of("type", "number", "description", "Minimum selling price"),
                        "verkoopPrijsMax", Map.of("type", "number", "description", "Maximum selling price"),
                        "page", Map.of("type", "integer", "description", "Page number (0-based)", "default", 0),
                        "size", Map.of("type", "integer", "description", "Page size", "default", 20),
                        "sort", Map.of("type", "string", "description", "Sort field and direction (e.g., 'naam,asc')")
                    )
                )
            ),
            new McpTool("huvsma.orders.getById", "Get order by ID", 
                Map.of(
                    "type", "object",
                    "properties", Map.of(
                        "id", Map.of("type", "string", "description", "Order ID")
                    ),
                    "required", Arrays.asList("id")
                )
            ),
            new McpTool("huvsma.inventory.adjust", "Adjust inventory stock", 
                Map.of(
                    "type", "object",
                    "properties", Map.of(
                        "productId", Map.of("type", "string", "description", "Product ID"),
                        "mutatieType", Map.of("type", "string", "enum", Arrays.asList("IN", "UIT", "CORRECTIE"), "description", "Movement type"),
                        "aantal", Map.of("type", "integer", "description", "Quantity", "minimum", 1),
                        "datum", Map.of("type", "string", "format", "date", "description", "Movement date"),
                        "opmerking", Map.of("type", "string", "description", "Optional remark")
                    ),
                    "required", Arrays.asList("productId", "mutatieType", "aantal", "datum")
                )
            ),
            new McpTool("huvsma.invoices.setStatus", "Set invoice status", 
                Map.of(
                    "type", "object",
                    "properties", Map.of(
                        "id", Map.of("type", "string", "description", "Invoice ID"),
                        "status", Map.of("type", "string", "enum", Arrays.asList("BETAALD", "TE_LAAT"), "description", "New status")
                    ),
                    "required", Arrays.asList("id", "status")
                )
            )
        );
        
        return new McpResponse(request.getId(), Map.of("tools", tools));
    }

    @SuppressWarnings("unchecked")
    private McpResponse handleToolsCall(McpRequest request) {
        Map<String, Object> params = (Map<String, Object>) request.getParams();
        String toolName = (String) params.get("name");
        Map<String, Object> arguments = (Map<String, Object>) params.get("arguments");
        
        try {
            Object result = switch (toolName) {
                case "huvsma.products.search" -> handleProductsSearch(arguments);
                case "huvsma.orders.getById" -> handleOrdersGetById(arguments);
                case "huvsma.inventory.adjust" -> handleInventoryAdjust(arguments);
                case "huvsma.invoices.setStatus" -> handleInvoicesSetStatus(arguments);
                default -> throw new IllegalArgumentException("Unknown tool: " + toolName);
            };
            
            return new McpResponse(request.getId(), Map.of("content", result));
        } catch (Exception e) {
            return new McpResponse(request.getId(), 
                new McpResponse.McpError(-32603, "Tool execution error: " + e.getMessage()));
        }
    }

    private Object handleProductsSearch(Map<String, Object> arguments) {
        // This would normally call the ProductController logic
        // For simplicity, we'll return a basic list
        return productRepository.findAll().stream()
                .limit(10)
                .toList();
    }

    private Object handleOrdersGetById(Map<String, Object> arguments) {
        String id = (String) arguments.get("id");
        return orderRepository.findById(id).orElse(null);
    }

    private Object handleInventoryAdjust(Map<String, Object> arguments) {
        // This would normally call the VoorraadbewegingController logic
        // For simplicity, we'll return a success message
        return Map.of("message", "Inventory adjustment would be processed here");
    }

    private Object handleInvoicesSetStatus(Map<String, Object> arguments) {
        // This would normally call the FactuurController logic
        // For simplicity, we'll return a success message
        return Map.of("message", "Invoice status update would be processed here");
    }
}
