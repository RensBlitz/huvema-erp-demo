package nl.huvema.huvsmaerp.storage;

import nl.huvema.huvsmaerp.dto.*;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class OrderRepository {
    
    private final Map<String, OrderResponseDTO> orders = new ConcurrentHashMap<>();
    private int nextId = 1001;

    public List<OrderResponseDTO> findAll() {
        return new ArrayList<>(orders.values());
    }

    public Optional<OrderResponseDTO> findById(String id) {
        return Optional.ofNullable(orders.get(id));
    }

    public OrderResponseDTO save(OrderResponseDTO order) {
        if (order.getId() == null) {
            order.setId("ORD-" + nextId++);
        }
        orders.put(order.getId(), order);
        return order;
    }

    public void deleteById(String id) {
        orders.remove(id);
    }

    public boolean existsById(String id) {
        return orders.containsKey(id);
    }

    public List<OrderResponseDTO> findByKlantId(String klantId) {
        return orders.values().stream()
                .filter(o -> o.getKlantId().equals(klantId))
                .collect(Collectors.toList());
    }

    public List<OrderResponseDTO> findByStatus(OrderStatus status) {
        return orders.values().stream()
                .filter(o -> o.getStatus() == status)
                .collect(Collectors.toList());
    }

    public List<OrderResponseDTO> findByOrderDatumBetween(LocalDate van, LocalDate tot) {
        return orders.values().stream()
                .filter(o -> !o.getOrderDatum().isBefore(van) && !o.getOrderDatum().isAfter(tot))
                .collect(Collectors.toList());
    }

    public List<OrderResponseDTO> findByTotaalBetween(BigDecimal min, BigDecimal max) {
        return orders.values().stream()
                .filter(o -> o.getTotaalIncBtw().compareTo(min) >= 0 && o.getTotaalIncBtw().compareTo(max) <= 0)
                .collect(Collectors.toList());
    }

    public void clear() {
        orders.clear();
        nextId = 1001;
    }

    public void seedData() {
        // Create sample orders with realistic data
        List<OrderRegelDTO> regels1 = Arrays.asList(
            new OrderRegelDTO("PRD-1001", 1, new BigDecimal("35000.00")),
            new OrderRegelDTO("PRD-1003", 10, new BigDecimal("25.00"))
        );
        BigDecimal totaal1 = regels1.stream().map(OrderRegelDTO::getRegelTotaal).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal btw1 = totaal1.multiply(new BigDecimal("0.21"));
        
        save(new OrderResponseDTO(null, "CUS-1001", LocalDate.now().minusDays(5), 
                OrderStatus.IN_BEHANDELING, regels1, totaal1, btw1, totaal1.add(btw1)));
        
        List<OrderRegelDTO> regels2 = Arrays.asList(
            new OrderRegelDTO("PRD-1002", 1, new BigDecimal("65000.00")),
            new OrderRegelDTO("PRD-1004", 5, new BigDecimal("15.00"))
        );
        BigDecimal totaal2 = regels2.stream().map(OrderRegelDTO::getRegelTotaal).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal btw2 = totaal2.multiply(new BigDecimal("0.21"));
        
        save(new OrderResponseDTO(null, "CUS-1002", LocalDate.now().minusDays(3), 
                OrderStatus.NIEUW, regels2, totaal2, btw2, totaal2.add(btw2)));
        
        List<OrderRegelDTO> regels3 = Arrays.asList(
            new OrderRegelDTO("PRD-1006", 1, new BigDecimal("1800.00")),
            new OrderRegelDTO("PRD-1007", 50, new BigDecimal("1.50"))
        );
        BigDecimal totaal3 = regels3.stream().map(OrderRegelDTO::getRegelTotaal).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal btw3 = totaal3.multiply(new BigDecimal("0.21"));
        
        save(new OrderResponseDTO(null, "CUS-1003", LocalDate.now().minusDays(1), 
                OrderStatus.GELEVERD, regels3, totaal3, btw3, totaal3.add(btw3)));
        
        List<OrderRegelDTO> regels4 = Arrays.asList(
            new OrderRegelDTO("PRD-1005", 2, new BigDecimal("0.50")),
            new OrderRegelDTO("PRD-1008", 1, new BigDecimal("5200.00"))
        );
        BigDecimal totaal4 = regels4.stream().map(OrderRegelDTO::getRegelTotaal).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal btw4 = totaal4.multiply(new BigDecimal("0.21"));
        
        save(new OrderResponseDTO(null, "CUS-1004", LocalDate.now(), 
                OrderStatus.GEANNULEERD, regels4, totaal4, btw4, totaal4.add(btw4)));
    }
}
