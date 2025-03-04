package org.wsp.mybookshelf.global.searchApi.service;

import org.wsp.mybookshelf.global.searchApi.dto.BookResponse;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class LibraryService {
    private final RestTemplate restTemplate;
    private static final String API_KEY = "ac27b83efe6300bdaca35e85cd0dcbb9d25b040859d2cc266f60617e783a9fa2";
    private static final String BASE_URL = "https://data4library.kr/api";

    public LibraryService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<BookResponse> searchBooks() {
        String url = BASE_URL + "/loanItemSrch?authKey=" + API_KEY +
                "&startDt=2025-01-01&endDt=2025-03-02&format=json";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json"); // 헤더 추가

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

        List<BookResponse> books = new ArrayList<>();

        if (response.getBody() != null && response.getBody().containsKey("response")) {
            Map responseBody = (Map) response.getBody().get("response");
            if (responseBody.containsKey("docs")) {
                List<Map> items = (List<Map>) responseBody.get("docs");
                for (Map itemWrapper : items) {
                    Map item = (Map) itemWrapper.get("doc");
                    BookResponse book = new BookResponse();
                    book.setTitle((String) item.get("bookname"));
                    book.setAuthor((String) item.get("authors"));
                    book.setPublisher((String) item.get("publisher"));
                    book.setIsbn((String) item.get("isbn13"));
                    book.setPublicationDate((String) item.get("publication_year"));
                    book.setCategoryName((String) item.get("class_no"));
                    book.setSource("Library Data API");
                    books.add(book);
                }
            }
        }
        return books;
    }


    public List<Map<String, String>> getSimilarGenreBooks(String genre) {
        String url = BASE_URL + "/bookRecommendation?authKey=" + API_KEY + "&genre=" + genre + "&format=json";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

        return response.getBody() != null ? (List<Map<String, String>>) response.getBody().get("docs") : new ArrayList<>();
    }
}