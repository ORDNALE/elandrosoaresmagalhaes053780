package com.elandroapi.core.pagination;

import io.quarkus.panache.common.Page;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.QueryParam;

import java.util.Objects;

public class PageRequest {

    @QueryParam("page")
    @DefaultValue("0")
    public @PositiveOrZero int page;

    @QueryParam("size")
    @DefaultValue("10")
    public @Positive int size;

    public Page toPage() {
        return Page.of(page, size);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof PageRequest that)) {
            return false;
        }
        return page == that.page && size == that.size;
    }

    @Override
    public int hashCode() {
        return Objects.hash(page, size);
    }

    @Override
    public String toString() {
        return "PageRequest{page=" + page + ", size=" + size + "}";
    }
}
