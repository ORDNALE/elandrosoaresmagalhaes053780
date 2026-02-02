package com.elandroapi.core.pagination;

import io.quarkus.hibernate.orm.panache.PanacheQuery;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Paged<T> {

    private long page;
    private long size;
    private long total;
    private long pageCount;
    private List<T> content;

    public Paged() {
        this(0L, 0L, 0L, 0L, Collections.emptyList());
    }

    public Paged(PanacheQuery<?> query, List<T> content) {
        this(
            query.page().index,
            query.page().size,
            query.count(),
            query.pageCount(),
            content
        );
    }

    public Paged(long page, long size, long total, long pageCount, List<T> content) {
        this.page = page;
        this.size = size;
        this.total = total;
        this.pageCount = pageCount;
        this.content = Collections.unmodifiableList(content);
    }

    public <R> Paged<R> map(Function<T, R> mapper) {
        List<R> mapped = content.stream().map(mapper).collect(Collectors.toList());
        return new Paged<>(page, size, total, pageCount, mapped);
    }

    public long getPage() {
        return page;
    }

    public void setPage(long page) {
        this.page = page;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getPageCount() {
        return pageCount;
    }

    public void setPageCount(long pageCount) {
        this.pageCount = pageCount;
    }

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }
}
