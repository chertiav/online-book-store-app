package com.achdev.onlinebookstoreapp.dto.page;

public record PageMetadata(
        int currentPage,
        int pageSize,
        int totalPageCount,
        long totalElementCount) {
}
