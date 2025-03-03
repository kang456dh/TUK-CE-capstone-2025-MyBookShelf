package org.wsp.mybookshelf.domain.recommendation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.wsp.mybookshelf.domain.bookshelf.service.BookShelfService;
import org.wsp.mybookshelf.global.response.ApiResponse;
import org.wsp.mybookshelf.global.searchApi.dto.BookResponse;
import org.wsp.mybookshelf.global.searchApi.service.AladinService;

import java.util.List;

@RestController
@RequestMapping("/api/recommend")
public class RecommendationController {

    @Autowired
    BookShelfService bookShelfService;

    @Autowired
    AladinService aladinService;


    // 책장 카테고리 기반 추천 도서 조회
    @GetMapping("/rating/{bookshelfId}")
    public ResponseEntity<ApiResponse<List<BookResponse>>> getRecommendedBooksByCategory(@PathVariable Long bookshelfId) {
        System.out.println("[API 요청] 추천 도서 조회 - 책장 ID: " + bookshelfId);

        List<BookResponse> recommendedBooks = bookShelfService.getRecommendedBooksByCategory(bookshelfId);

        System.out.println("[API 응답] 추천 도서 개수: " + recommendedBooks.size());
        return ResponseEntity.ok(ApiResponse.onSuccess(recommendedBooks));
    }




}
