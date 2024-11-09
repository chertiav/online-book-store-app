package com.achdev.onlinebookstoreapp.dto;

public record BookSearchParameters(String[] titles,
                                   String[] authors,
                                   String[] isbns,
                                   String[] descriptions) {
}
