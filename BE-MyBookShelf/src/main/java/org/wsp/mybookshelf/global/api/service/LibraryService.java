package org.wsp.mybookshelf.global.api.service;

import org.wsp.mybookshelf.global.api.dto.BookResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class LibraryService {
    private final RestTemplate restTemplate;
    private final String API_KEY = "ac27b83efe6300bdaca35e85cd0dcbb9d25b040859d2cc266f60617e783a9fa2"; // API 키 입력

    public LibraryService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    //&isbn13=9788960305762&loaninfoYN=Y&%20displayInfo=age
    public BookResponse searchBooks(String query) {
        String url = "http://data4library.kr/api/srchDtlList?authKey=" + API_KEY + "&isbn13=" + query + "&format=json";
        Map response = restTemplate.getForObject(url, Map.class);
        List<BookResponse.Book> books = new ArrayList<>();

        if (response != null && response.containsKey("docs")) {
            List<Map> docs = (List<Map>) response.get("docs");
            for (Map doc : docs) {
                BookResponse.Book book = new BookResponse.Book();
                book.setTitle((String) doc.get("bookname"));
                book.setAuthor((String) doc.get("authors"));
                book.setPublisher((String) doc.get("publisher"));
                book.setIsbn((String) doc.get("isbn"));
                book.setSource("Library API");
                books.add(book);
            }
        }

        BookResponse bookResponse = new BookResponse();
        bookResponse.setBooks(books);
        return bookResponse;
    }
}
