package org.wsp.mybookshelf.global.recommandation.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.wsp.mybookshelf.global.searchApi.dto.BookResponse;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookRecommendationService {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    private static final String API_URL = "https://data4library.kr/api/loanItemSrch";
    private static final String API_KEY = "ac27b83efe6300bdaca35e85cd0dcbb9d25b040859d2cc266f60617e783a9fa2";

    public List<BookResponse> getRecommendedBooks(BookResponse book, int userAge) {
        List<BookResponse> recommendedBooks = new ArrayList<>();

        String url = String.format("%s?authKey=%s&age=%d&format=json", API_URL, API_KEY, userAge);

        try {
            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(response);
            JsonNode books = root.path("response").path("docs");

            for (JsonNode node : books) {
                if (recommendedBooks.size() >= 10) break;
                BookResponse recommendedBook = new BookResponse();
                recommendedBook.setTitle(node.path("doc").path("bookname").asText());
                recommendedBook.setAuthor(node.path("doc").path("authors").asText());
                recommendedBook.setPublisher(node.path("doc").path("publisher").asText());
                recommendedBook.setGenre(book.getGenre());
                recommendedBook.setIsbn(node.path("doc").path("isbn13").asText());
                recommendedBook.setPublicationDate(node.path("doc").path("publication_year").asText());
                recommendedBook.setCover(node.path("doc").path("bookImageURL").asText());
                recommendedBook.setCustomerReviewRank(node.path("doc").path("ranking").asInt());
                recommendedBook.setSource("Library API");
                recommendedBook.setDescription("Recommended based on your book genre.");

                recommendedBooks.add(recommendedBook);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return recommendedBooks;
    }
}
