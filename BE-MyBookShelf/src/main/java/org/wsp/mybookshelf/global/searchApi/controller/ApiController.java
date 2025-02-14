package org.wsp.mybookshelf.global.searchApi.controller;


import org.wsp.mybookshelf.global.searchApi.dto.BookResponse;
import org.wsp.mybookshelf.global.searchApi.service.AladinService;
import org.wsp.mybookshelf.global.searchApi.service.GoogleBooksService;
import org.wsp.mybookshelf.global.searchApi.service.LibraryService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/books")
public class ApiController {
    private final LibraryService libraryService;
    private final AladinService aladinService;
    private final GoogleBooksService googleBooksService;

    public ApiController(LibraryService libraryService, AladinService aladinService, GoogleBooksService googleBooksService) {
        this.libraryService = libraryService;
        this.aladinService = aladinService;
        this.googleBooksService = googleBooksService;
    }

    // 알라딘 OpenAPI만 사용한 코드
    @GetMapping("/search")
    public Mono<Map<@org.jetbrains.annotations.NotNull String, @org.jetbrains.annotations.NotNull List<BookResponse>>> searchBooks(
            @RequestParam String query,
            @RequestParam(defaultValue = "Keyword") String queryType,
            @RequestParam(defaultValue = "1") String start,
            @RequestParam(defaultValue = "6") int maxResults,
            @RequestParam(defaultValue = "true") boolean showDetail
    ) {
        return Mono.fromCallable(() -> {
            //List<BookResponse> books = aladinService.searchBooks(query, queryType, start, maxResults, showDetail);
            List<BookResponse> books = LibraryService.searchBooks(query);
            return Map.of("books", books);
        }).subscribeOn(Schedulers.boundedElastic());
    }


    // 알라딘 + GoogleAPI
//    @GetMapping("/search")
//    public Mono<Map<String, Object>> searchBooks(@RequestParam String query) {
//        Mono<BookResponse> googleBooks = googleBooksService.searchBooks(query);
//        Mono<BookResponse> libraryBooks = Mono.fromCallable(() -> libraryService.searchBooks(query)).subscribeOn(Schedulers.boundedElastic());
//        //Mono<BookResponse> aladinBooks = Mono.fromCallable(() -> aladinService.searchBooks(query)).subscribeOn(Schedulers.boundedElastic());
//
//        return Mono.zip(libraryBooks, googleBooks)
//                .map(tuple -> Map.of(
//                        "libraryAPI", tuple.getT1(),
////                        "aladinAPI", tuple.getT1()
//                        "googleBooksAPI", tuple.getT2()
//                ));
//    }

}
