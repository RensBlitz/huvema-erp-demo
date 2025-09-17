package nl.huvema.huvsmaerp.util;

import nl.huvema.huvsmaerp.dto.ApiResponse;

import java.util.List;

public class PaginationUtil {
    
    public static <T> ApiResponse<List<T>> createResponse(List<T> content, int page, int size, long totalElements) {
        int totalPages = (int) Math.ceil((double) totalElements / size);
        ApiResponse.Meta meta = new ApiResponse.Meta(
            page,
            size,
            totalElements,
            totalPages,
            page == 0,
            page >= totalPages - 1
        );
        
        return new ApiResponse<>(content, meta);
    }
    
    public static <T> List<T> applyPagination(List<T> content, int page, int size) {
        int start = page * size;
        int end = Math.min(start + size, content.size());
        
        if (start >= content.size()) {
            return List.of();
        }
        
        return content.subList(start, end);
    }
    
    public static <T> List<T> applySorting(List<T> content, String sort) {
        if (sort == null || sort.trim().isEmpty()) {
            return content;
        }
        
        // For now, just return the content as-is
        // In a real implementation, you would implement sorting based on the sort parameter
        return content;
    }
}
