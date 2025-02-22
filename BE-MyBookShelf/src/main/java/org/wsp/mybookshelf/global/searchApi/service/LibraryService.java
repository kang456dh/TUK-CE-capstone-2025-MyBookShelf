package org.wsp.mybookshelf.global.searchApi.service;

import org.wsp.mybookshelf.domain.book.entity.Book;
import org.wsp.mybookshelf.global.searchApi.dto.BookResponse;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

@Service
public class LibraryService {
    private static RestTemplate restTemplate = null;
    private static final String API_KEY = "ac27b83efe6300bdaca35e85cd0dcbb9d25b040859d2cc266f60617e783a9fa2";
    private static final String BASE_URL = "https://data4library.kr/api/srchDtlList";

    public LibraryService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public static List<BookResponse> searchBooks(String query) {
//        String url = BASE_URL + "?authKey=" + API_KEY + "&isbn13=" + query + "&format=json";
//
//        // HTTP 헤더 설정 (Accept: application/json)
//        HttpHeaders headers = new HttpHeaders();
//        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
//
//        HttpEntity<String> entity = new HttpEntity<>(headers);
//
//        // API 호출
//        ResponseEntity<Map> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
//
//        List<BookResponse> books = new ArrayList<>();
//        if (responseEntity.getBody() != null && responseEntity.getBody().containsKey("docs")) {
//            List<Map> docs = (List<Map>) responseEntity.getBody().get("docs");
//            for (Map doc : docs) {
//                Map bookData = (Map) doc.get("book");
//                BookResponse book = new BookResponse();
//                book.setTitle((String) bookData.get("bookname"));
//                book.setAuthor((String) bookData.get("authors"));
//                book.setPublisher((String) bookData.get("publisher"));
//                book.setIsbn((String) bookData.get("isbn13")); // ISBN13 사용
//                book.setSource("Library API");
//                books.add(book);
//            }
//        }
        String uri = BASE_URL + "?authKey=" + API_KEY + "&isbn13=" + query + "&format=json";
        String line = null;

        List<BookResponse> books = new ArrayList<>();
//        try{
//
//        }


        return books;
    }
}
