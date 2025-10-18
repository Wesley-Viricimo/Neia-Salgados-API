package org.neiasalgados.domain.dto.response;

import org.springframework.data.domain.Page;

import java.util.List;

public class PageResponseDTO<T> {
    private List<T> items;
    private int currentPage;
    private int totalPages;
    private long totalItems;

    public PageResponseDTO(Page<T> page) {
        this.items = page.getContent();
        this.currentPage = page.getNumber();
        this.totalPages = page.getTotalPages();
        this.totalItems = page.getTotalElements();
    }

    public List<T> getItems() {
        return items;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public long getTotalItems() {
        return totalItems;
    }
}
