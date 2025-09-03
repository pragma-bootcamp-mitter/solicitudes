package co.com.pragma.bootcamp.model.applicationsummary;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Pagination {
    private final int page;
    private final int size;
}