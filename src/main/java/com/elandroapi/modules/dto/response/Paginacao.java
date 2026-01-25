package com.elandroapi.modules.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
public class Paginacao<T> {

    private Collection<T> content;
    private Integer totalPages;
    private Long totalElements;
    private Integer size;
    private Integer page;

    public Paginacao(Collection<T> content, Integer totalPages, Long totalElements, Integer size, Integer page) {
        this.content = content;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
        this.size = size;
        this.page = page;
    }
}