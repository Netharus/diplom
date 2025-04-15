package com.netharus.service;

import com.netharus.domain.dto.response.PageContainer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class UtilClass {

    public static String getSortField(Pageable pageable) {
        return pageable.getSort()
                .stream()
                .findFirst()
                .map(Sort.Order::getProperty)
                .orElse(null);
    }

    public static String getSortDir(Pageable pageable) {
        return pageable.getSort()
                .stream()
                .findFirst()
                .map(order -> order.getDirection().name())
                .orElse(null);
    }

    public static <T> PageContainer<T> getPageContainerFromPage(Page<T> page, Pageable pageable, String keyword) {
        return PageContainer.<T>builder()
                .list(page.getContent())
                .size(page.getSize())
                .currentPage(pageable.getPageNumber())
                .totalItems(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .sortField(getSortField(pageable))
                .sortDir(getSortDir(pageable))
                .keyword(keyword)
                .build();
    }
}
