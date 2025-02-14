package org.wsp.mybookshelf.global.searchApi.dto;

import lombok.Data;

import java.util.Comparator;
import java.util.List;

@Data
public class BookResponse {
    private String title;
    private String author;
    private String publisher;
    private String genre;
    private String isbn;
    private String publicationDate;  // 출판일 추가
    private String Cover; // 책 표지
    private Integer customerReviewRank;
    private String source;  // 데이터 출처
    private String description;
}
