package co.com.pragma.bootcamp.model.applicationsummary;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class PageModel<T> {
    private final List<T> content;
    private final int page;
    private final int size;
    private final long totalElements;
    private final int totalPages;
}