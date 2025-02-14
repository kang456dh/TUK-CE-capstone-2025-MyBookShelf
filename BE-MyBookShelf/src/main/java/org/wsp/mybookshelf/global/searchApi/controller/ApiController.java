package org.wsp.mybookshelf.global.searchApi.controller;

import org.springframework.http.ResponseEntity;
import org.wsp.mybookshelf.global.response.ApiResponse;
import org.wsp.mybookshelf.global.searchApi.dto.BookResponse;
import org.wsp.mybookshelf.global.searchApi.service.AladinService;
import org.wsp.mybookshelf.global.searchApi.service.GoogleBooksService;
import org.wsp.mybookshelf.global.searchApi.service.LibraryService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

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
    public Mono<Map<String, Object>> searchBooks(
            @RequestParam(required = true) String query,
            @RequestParam(required = false, defaultValue = "Keyword") String queryType,
            /*
            Keyword (기본값) : 제목 + 저자
            Title : 제목
            Author : 저자
            Publisher : 출판사
            */
            @RequestParam(required = true, defaultValue = "1") String start
    ) {
        return Mono.fromCallable(() -> aladinService.searchBooks(query, queryType, start))
                .subscribeOn(Schedulers.boundedElastic())
                .map(aladinBooks -> Map.of("aladinAPI", aladinBooks));
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
