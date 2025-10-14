package com.TaskManagement.TaskManagement.dto.request;

import jakarta.validation.constraints.Min;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaginationRequest {
    @Min(value = 0, message = "Page index must be zero or positive")
    private int page = 0;

    @Min(value = 1, message = "Page size must be at least one")
    private int size = 10;

    private String sortBy = "dueDate";
    private Sort.Direction direction = Sort.Direction.ASC;

    public void setSortBy(String sortBy) {
        // If the incoming value is null or the common Swagger placeholder "string"
        if (sortBy == null || sortBy.equalsIgnoreCase("string")) {
            // Fallback to a guaranteed safe property in the Task entity
            this.sortBy = "id";
        } else {
            this.sortBy = sortBy;
        }
    }

    // Utility method to convert to Spring's Pageable
    public Pageable toPageable() {
        return PageRequest.of(page, size, Sort.by(direction, sortBy));
    }
}