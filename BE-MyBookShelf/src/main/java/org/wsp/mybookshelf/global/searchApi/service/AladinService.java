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

    public List<BookResponse> searchBooks(String query, String queryType, String start, int maxResults, boolean showDetail) {
        String url = "https://www.aladin.co.kr/ttb/api/ItemSearch.aspx?TTBKey=" + API_KEY +
                "&QueryType=" + queryType +
                "&Query=" + query +
                "&MaxResults=" + maxResults +
                "&Start=" + start +
                "&SearchTarget=Book&Output=JS&Version=20131101";

        Map response = restTemplate.getForObject(url, Map.class);
        List<BookResponse> books = new ArrayList<>();

        if (response != null && response.containsKey("item")) {
            List<Map> items = (List<Map>) response.get("item");
            for (Map item : items) {
                BookResponse book = new BookResponse();
                book.setTitle((String) item.get("title"));
                book.setAuthor((String) item.get("author"));
                book.setPublisher((String) item.get("publisher"));
                book.setGenre((String) item.get("categoryName")); // 장르 추가
                book.setIsbn((String) item.get("isbn"));
                book.setCover((String) item.get("cover"));
                book.setPublicationDate((String) item.get("pubDate"));
                book.setSource("Aladin API");

                if (showDetail) {
                    book.setDescription((String) item.get("description")); // 상세 검색 시만 포함
                }

                books.add(book);
            }
        }
        return books;
    }

    public BookResponse searchBookDetail(String isbn13){
        String url = "https://www.aladin.co.kr/ttb/api/ItemSearch.aspx?TTBKey=" + API_KEY +
                "&Query=" + isbn13 +
                "&SearchTarget=Book&Output=JS&Version=20131101";

        Map response = restTemplate.getForObject(url, Map.class);

        if (response != null && response.containsKey("item")) {
            Map item = ((List<Map>) response.get("item")).get(0);
            BookResponse bookResponse = new BookResponse();
            bookResponse.setTitle((String) item.get("title"));
            bookResponse.setAuthor((String) item.get("author"));
            bookResponse.setPublisher((String) item.get("publisher"));
            bookResponse.setIsbn((String) item.get("isbn"));
            bookResponse.setCover((String) item.get("cover"));
            bookResponse.setCustomerReviewRank((Integer) item.get("customerReviewRank"));
            bookResponse.setSource("Aladin API");
            return bookResponse;
        }

        return null;
    }
}