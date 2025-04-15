package com.netharus.domain.dto.response;

import java.util.List;

public record PageContainer<T>(
        List<T> list,
        Integer size,
        Integer currentPage,
        Long totalItems,
        Integer totalPages,
        String sortField,
        String sortDir,
        String reverseSortDir,
        Long startCount,
        Long endCount,
        String keyword
) {
    public static class Builder<T> {
        private List<T> list;
        private Integer size;
        private Integer currentPage;
        private Long totalItems;
        private Integer totalPages;
        private String sortField;
        private String sortDir;
        private String reverseSortDir;
        private Long startCount;
        private Long endCount;
        private String keyword;


        public Builder<T> list(List<T> list) {
            this.list = list;
            return this;
        }

        public Builder<T> size(Integer size) {
            this.size = size;
            return this;
        }

        public Builder<T> currentPage(Integer currentPage) {
            this.currentPage = currentPage;
            return this;
        }

        public Builder<T> totalItems(Long totalItems) {
            this.totalItems = totalItems;
            return this;
        }

        public Builder<T> totalPages(Integer totalPages) {
            this.totalPages = totalPages;
            return this;
        }

        public Builder<T> sortField(String sortField) {
            this.sortField = sortField;
            return this;
        }

        public Builder<T> sortDir(String sortDir) {
            this.sortDir = sortDir;
            return this;
        }

        public Builder<T> reverseSortDir(String reverseSortDir) {
            this.reverseSortDir = reverseSortDir;
            return this;
        }

        public Builder<T> keyword(String keyword) {
            this.keyword = keyword;
            return this;
        }

        public Builder<T> calculateCounts() {
            if (currentPage != null && size != null) {
                this.startCount = (long) (currentPage) * size + 1;
                this.endCount = startCount + size - 1;
                if (totalItems != null && endCount > totalItems) {
                    this.endCount = totalItems;
                }
            }
            return this;
        }

        public Builder<T> setReverseSortDir() {
            this.reverseSortDir = sortDir.equals("ASC") ? "DESC" : "ASC";
            return this;
        }

        public Builder<T> startCount(Long startCount) {
            this.startCount = startCount;
            return this;
        }

        public Builder<T> endCount(Long endCount) {
            this.endCount = endCount;
            return this;
        }

        public PageContainer<T> build() {
            calculateCounts();
            setReverseSortDir();
            return new PageContainer<>(
                    list,
                    size,
                    currentPage,
                    totalItems,
                    totalPages,
                    sortField,
                    sortDir,
                    reverseSortDir,
                    startCount,
                    endCount,
                    keyword
            );
        }
    }

    public static <T> Builder<T> builder() {
        return new Builder<>();
    }
}
