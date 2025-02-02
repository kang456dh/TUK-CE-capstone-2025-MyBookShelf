package org.wsp.mybookshelf.global.api.controller;

import org.wsp.mybookshelf.global.api.dto.BookResponse;
import org.wsp.mybookshelf.global.api.service.AladinService;
import org.wsp.mybookshelf.global.api.service.GoogleBooksService;
import org.wsp.mybookshelf.global.api.service.LibraryService;
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
    public Mono<Map<String, Object>> searchBooks(@RequestParam String query) {
        return Mono.fromCallable(() -> aladinService.searchBooks(query))
                .subscribeOn(Schedulers.boundedElastic())  // ✅ 동기 API를 비동기로 변환
                .map(aladinBooks -> Map.of("aladinAPI", aladinBooks)); // ✅ 알라딘 API 응답만 반환
    }

    // 알라딘 + GoogleAPI
//    @GetMapping("/search")
//    public Mono<Map<String, Object>> searchBooks(@RequestParam String query) {
//        Mono<BookResponse> googleBooks = googleBooksService.searchBooks(query);
//        Mono<BookResponse> libraryBooks = Mono.fromCallable(() -> libraryService.searchBooks(query)).subscribeOn(Schedulers.boundedElastic());
//        Mono<BookResponse> aladinBooks = Mono.fromCallable(() -> aladinService.searchBooks(query)).subscribeOn(Schedulers.boundedElastic());
//
//        return Mono.zip(aladinBooks, googleBooks)
//                .map(tuple -> Map.of(
////                        "libraryAPI", tuple.getT1(),
//                        "aladinAPI", tuple.getT1(),
//                        "googleBooksAPI", tuple.getT2()
//                ));
//    }

}
