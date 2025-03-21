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
    private static RestTemplate restTemplate = null;
    private static String API_KEY = "ac27b83efe6300bdaca35e85cd0dcbb9d25b040859d2cc266f60617e783a9fa2";

    public LibraryService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public static List<BookResponse> searchBooks(String query) {
        String url = "https://data4library.kr/api/srchDtlList?authKey=" + API_KEY + "&isbn13=" + query + "&format=json";

        // HTTP Headers 설정
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("Accept", "application/json"); // JSON 형식 요청 명시
//        HttpEntity<String> entity = new HttpEntity<>(headers);

        // API 호출
        Map response = restTemplate.getForObject(url, Map.class);
        List<BookResponse> books = new ArrayList<>();

        if (response != null && response.containsKey("docs")) {
            List<Map> docs = (List<Map>) response.get("docs");
            for (Map doc : docs) {
                BookResponse book = new BookResponse();
                book.setTitle((String) doc.get("bookname"));
                book.setAuthor((String) doc.get("authors"));
                book.setPublisher((String) doc.get("publisher"));
                book.setIsbn((String) doc.get("isbn"));
                book.setSource("Library API");
                books.add(book);
            }
        }
        return books;
    }
}
