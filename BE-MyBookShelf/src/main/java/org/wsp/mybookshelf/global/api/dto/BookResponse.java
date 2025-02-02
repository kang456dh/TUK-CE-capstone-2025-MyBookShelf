package org.wsp.mybookshelf.global.api.dto;

import lombok.Data;

import java.util.Comparator;
import java.util.List;

@Data
public class BookResponse {
    private List<Book> books;

    @Data
    public static class Book {
        private String title;
        private String author;
        private String publisher;
        private String isbn;
        private String publicationDate;  // 출판일 추가
        private String source;  // 데이터 출처
    }

    public void sortBooks(String sortBy) {
        if (books == null) return;

        switch (sortBy) {
            case "title":
                books.sort(Comparator.comparing(Book::getTitle));
                break;
            case "publisher":
                books.sort(Comparator.comparing(Book::getPublicationDate, Comparator.nullsLast(Comparator.reverseOrder())));
                break;
            case "source":
                books.sort(Comparator.comparing(Book::getSource));
                break;
        }
    }
}
