package org.wsp.mybookshelf.global.searchApi.service;

import org.springframework.web.bind.annotation.RequestParam;
import org.wsp.mybookshelf.domain.book.dto.BookDTO;
import org.wsp.mybookshelf.global.searchApi.dto.BookRatingResponse;
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
    private static final int MIN_REVIEWS = 0;

    public AladinService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<BookResponse> searchBooks(String query, String queryType, String start, int maxResults, boolean showDetail) {
        String url = "https://www.aladin.co.kr/ttb/api/ItemSearch.aspx?TTBKey=" + API_KEY +
                "&QueryType=" + queryType +
                "&Query=" + query +
                "&MaxResults=" + maxResults +
                "&Cover=Big" +
                //"&Start=" + start +
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

                book.setCategoryName((String) item.get("categoryName"));
                book.setCategoryId((Integer) item.get("categoryId"));

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
                "&Cover=Big" +
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
            bookResponse.setCategoryId((Integer) item.get("categoryId"));
            bookResponse.setDescription((String) item.get("description"));
            bookResponse.setCategoryName((String) item.get("categoryName"));
            return bookResponse;
        }

        return null;
    }

    public List<BookResponse> searchBooksByCategory(int categoryId, int maxResults) {
        String url = "https://www.aladin.co.kr/ttb/api/ItemList.aspx?TTBKey=" + API_KEY +
                "&QueryType=" + "Bestseller" +
                "&CategoryId=" + categoryId +
                "&MaxResults=" + maxResults +
                "&SearchTarget=Book&Output=JS&Version=20131101";

        System.out.println("[API 요청 URL]: " + url);

        Map response = restTemplate.getForObject(url, Map.class);
        List<BookResponse> books = new ArrayList<>();

        System.out.println("[API 응답]: " + response);

        if (response != null && response.containsKey("item")) {
            List<Map> items = (List<Map>) response.get("item");

            System.out.println("[응답 아이템 개수]: " + items.size());

            for (Map item : items) {
                int rating = (Integer) item.getOrDefault("customerReviewRank", 0); // 평점이 없으면 기본값 0

                // 평점이 7 이상인 도서만 추가
                if (rating >= 7) {
                    System.out.println("[도서 정보] 제목: " + item.get("title") + ", 평점: " + rating);

                    BookResponse book = new BookResponse();
                    book.setTitle((String) item.get("title"));
                    book.setAuthor((String) item.get("author"));
                    book.setPublisher((String) item.get("publisher"));
                    book.setCategoryName((String) item.get("categoryName"));
                    book.setCategoryId((Integer) item.get("categoryId"));
                    book.setIsbn((String) item.get("isbn"));
                    book.setCover((String) item.get("cover"));
                    book.setPublicationDate((String) item.get("pubDate"));
                    book.setCustomerReviewRank(rating);
                    book.setSource("Aladin API");

                    books.add(book);
                }
            }
        }

        System.out.println("[최종 반환 도서 개수]: " + books.size());
        return books;
    }

    public List<BookRatingResponse> fetchBookRatings(List<String> isbns) {
        List<BookRatingResponse> ratings = new ArrayList<>();

        for (String isbn : isbns) {
            String url = "https://www.aladin.co.kr/ttb/api/ItemLookUp.aspx?TTBKey=" + API_KEY +
                    "&ItemIdType=ISBN" +
                    "&ItemId=" + isbn +
                    "&Output=JS" +
                    "&Version=20131101" +
                    "&OptResult=ratingInfo";  // 평점 정보 포함

            try {
                Map response = restTemplate.getForObject(url, Map.class);
                System.out.println("알라딘 API 응답 (ISBN: " + isbn + ") : " + response);

                if (response != null && response.containsKey("item")) {
                    List<Map> items = (List<Map>) response.get("item");

                    if (!items.isEmpty()) {
                        Map item = items.get(0);

                        // subInfo 안에 ratingInfo가 존재하는지 확인
                        Map subInfo = (Map) item.get("subInfo");
                        Map ratingInfo = subInfo != null ? (Map) subInfo.get("ratingInfo") : null;

                        double ratingScore = ratingInfo != null && ratingInfo.containsKey("ratingScore")
                                ? ((Number) ratingInfo.get("ratingScore")).doubleValue()
                                : 0.0;

                        int ratingCount = ratingInfo != null && ratingInfo.containsKey("ratingCount")
                                ? ((Number) ratingInfo.get("ratingCount")).intValue()
                                : 0;

                        // 결과 저장
                        BookRatingResponse ratingResponse = new BookRatingResponse();
                        ratingResponse.setIsbn(isbn);
                        ratingResponse.setRatingScore(ratingScore);
                        ratingResponse.setRatingCount(ratingCount);

                        ratings.add(ratingResponse);
                    }
                }
            } catch (Exception e) {
                System.err.println("알라딘 API 요청 실패 (ISBN: " + isbn + ") - " + e.getMessage());
            }
        }
        return ratings;
    }



}