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
@RequestMapping("/api/books")
public class ApiController {
    private final AladinService aladinService;

    public ApiController(AladinService aladinService) {
        this.aladinService = aladinService;
    }

    // 알라딘 OpenAPI만 사용한 코드
    @GetMapping("/search")
    public Mono<Map<@org.jetbrains.annotations.NotNull String, @org.jetbrains.annotations.NotNull List<BookResponse>>> searchBooks(
            @RequestParam String query,
            @RequestParam(defaultValue = "Keyword") String queryType,
            @RequestParam(defaultValue = "1") String start,
            @RequestParam(defaultValue = "100") int maxResults,
            @RequestParam(defaultValue = "true") boolean showDetail
    ) {
        return Mono.fromCallable(() -> {
            List<BookResponse> books = aladinService.searchBooks(query, queryType, start, maxResults, showDetail);
            return Map.of("books", books);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    // 검색하여 상세 정보 리턴
    @GetMapping("/search/register")
    public BookResponse searchBookDetail(@RequestParam String isbn13) {
        BookResponse book = aladinService.searchBookDetail(isbn13);

        return book;
    }
}