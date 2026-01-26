package com.elandroapi.modules.dto.response;

import com.elandroapi.modules.dto.filter.PageableFilter;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.function.Function;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Paginacao<T> {

    private List<T> content;
    private int totalPages;
    private long totalElements;
    private int size;
    private int page;

    public static <E, D> Paginacao<D> of(PanacheQuery<E> query, PageableFilter filter, Function<E, D> mapper) {
        query.page(filter.getPage(), filter.getSize());
        List<D> content = query.list().stream().map(mapper).toList();
        return new Paginacao<>(
                content,
                query.pageCount(),
                query.count(),
                filter.getSize(),
                filter.getPage()
        );
    }
}
