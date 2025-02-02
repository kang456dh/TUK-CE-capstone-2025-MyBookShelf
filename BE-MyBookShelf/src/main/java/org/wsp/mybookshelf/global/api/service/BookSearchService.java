package org.wsp.mybookshelf.global.api.service;

import org.wsp.mybookshelf.global.api.dto.BookResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class BookSearchService {
    private final LibraryService libraryService;
    private final AladinService aladinService;
    private final GoogleBooksService googleBooksService;

    public BookSearchService(LibraryService libraryService, AladinService aladinService, GoogleBooksService googleBooksService) {
        this.libraryService = libraryService;
        this.aladinService = aladinService;
        this.googleBooksService = googleBooksService;
    }


    public Mono<List<BookResponse.Book>> searchAllSources(String query, String sortBy) {
        Mono<List<BookResponse.Book>> libraryMono = Mono.fromCallable(() -> libraryService.searchBooks(query).getBooks());
        //Mono<List<BookResponse.Book>> aladinMono = Mono.fromCallable(() -> aladinService.searchBooks(query).getBooks());
        //Mono<BookResponse> googleMono = googleBooksService.searchBooks(query);

        return libraryMono;
//        return Mono.zip(libraryMono, aladinMono, googleMono)
//                .map(tuple -> {
//                    List<BookResponse.Book> results = new ArrayList<>();
//                    results.addAll(tuple.getT1());  // 도서관 정보나루 결과
//                    //results.addAll(tuple.getT2());  // 알라딘 API 결과
//                    //results.addAll((Collection<? extends BookResponse.Book>) tuple.getT3());  // 구글 북스 API 결과
//
//                    BookResponse response = new BookResponse();
//                    response.setBooks(results);
//                    response.sortBooks(sortBy);
//                    return response.getBooks();
//                });
    }

}
