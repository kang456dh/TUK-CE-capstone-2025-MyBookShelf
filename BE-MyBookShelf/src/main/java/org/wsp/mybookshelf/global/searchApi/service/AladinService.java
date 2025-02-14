package org.wsp.mybookshelf.global.searchApi.service;

import org.wsp.mybookshelf.global.searchApi.dto.BookResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class AladinService {
    private final RestTemplate restTemplate;
    private final String API_KEY = "ttbjjojin71682109001"; // API 키 입력

    public AladinService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public BookResponse searchBooks(String query, String queryType, String start) {
        String url = "https://www.aladin.co.kr/ttb/api/ItemSearch.aspx?TTBKey=" + API_KEY +
                "&QueryType=" + queryType +
                "&Query=" + query +
                "&MaxResults=6" +
                "&Start=" + start +
                "&SearchTarget=Book&Output=JS&Version=20131101";
        Map response = restTemplate.getForObject(url, Map.class);
        List<BookResponse.Book> books = new ArrayList<>();

        if (response != null && response.containsKey("item")) {
            List<Map> items = (List<Map>) response.get("item");
            for (Map item : items) {
                BookResponse.Book book = new BookResponse.Book();
                book.setTitle((String) item.get("title"));
                book.setAuthor((String) item.get("author"));
                book.setPublisher((String) item.get("publisher"));
                book.setIsbn((String) item.get("isbn"));
                book.setCover((String) item.get("cover"));
                book.setCustomerReviewRank((Integer) item.get("customerReviewRank"));
                book.setSource("Aladin API");
                books.add(book);
            }
        }

        BookResponse bookResponse = new BookResponse();
        bookResponse.setBooks(books);
        return bookResponse;
    }


    public BookResponse getBook(String isbn) {
        String url = "https://www.aladin.co.kr/ttb/api/ItemLookUp.aspx?TTBKey=" + API_KEY +
                "&itemIdType=ISBN&ItemId=" + isbn +
                "&output=JS&Version=20131101";
        Map response = restTemplate.getForObject(url, Map.class);
        BookResponse.Book book = new BookResponse.Book();

        if (response != null && response.containsKey("item")) {
            Map item = (Map) response.get("item");
            book.setTitle((String) item.get("title"));
            book.setAuthor((String) item.get("author"));
            book.setPublisher((String) item.get("publisher"));
            book.setIsbn((String) item.get("isbn"));
            book.setCover((String) item.get("cover"));
            book.setCustomerReviewRank((Integer) item.get("customerReviewRank"));
            book.setSource("Aladin API");
        }

        BookResponse bookResponse = new BookResponse();
        List<BookResponse.Book> books = new ArrayList<>();
        books.add(book);
        bookResponse.setBooks(books);
        return bookResponse;
    }

}
