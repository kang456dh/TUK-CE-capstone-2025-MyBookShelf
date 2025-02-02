package org.wsp.mybookshelf.global.api.service;

import org.wsp.mybookshelf.global.api.dto.BookResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class GoogleBooksService {
    private final WebClient webClient;
    private final String API_KEY = "AIzaSyDOvwaQ1kO4N9BEYOot76HNGT2ghzi6Cxc"; // API 키 입력

    public GoogleBooksService(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<BookResponse> searchBooks(String query) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host("www.googleapis.com")
                        .path("/books/v1/volumes")
                        .queryParam("q", query)
                        //.queryParam("key", API_KEY)
                        .build())
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> {
                    List<BookResponse.Book> books = new ArrayList<>();
                    if (response.containsKey("items")) {
                        List<Map> items = (List<Map>) response.get("items");
                        for (Map item : items) {
                            Map volumeInfo = (Map) item.get("volumeInfo");
                            BookResponse.Book book = new BookResponse.Book();
                            book.setTitle((String) volumeInfo.get("title"));
                            book.setAuthor(volumeInfo.get("authors") != null ? ((List<String>) volumeInfo.get("authors")).get(0) : "Unknown");
                            book.setPublisher((String) volumeInfo.get("publisher"));
                            book.setIsbn(volumeInfo.get("industryIdentifiers") != null ? ((List<Map>) volumeInfo.get("industryIdentifiers")).get(0).get("identifier").toString() : "N/A");
                            book.setSource("Google Books API");
                            books.add(book);
                        }
                    }

                    BookResponse bookResponse = new BookResponse();
                    bookResponse.setBooks(books);
                    return bookResponse;
                });
    }
}
