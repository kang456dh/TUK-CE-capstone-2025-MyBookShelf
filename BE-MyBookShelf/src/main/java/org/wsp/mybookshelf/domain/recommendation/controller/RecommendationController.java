package org.wsp.mybookshelf.domain.recommendation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.wsp.mybookshelf.domain.book.dto.BookRecommendDTO;
import org.wsp.mybookshelf.domain.bookshelf.service.BookShelfService;
import org.wsp.mybookshelf.domain.recommendation.service.RecommendationService;
import org.wsp.mybookshelf.global.response.ApiResponse;
import org.wsp.mybookshelf.global.searchApi.dto.BookRatingResponse;
import org.wsp.mybookshelf.global.searchApi.dto.BookResponse;
import org.wsp.mybookshelf.global.searchApi.service.AladinService;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/recommend")
public class RecommendationController {

    @Autowired
    BookShelfService bookShelfService;

    @Autowired
    AladinService aladinService;

    @Autowired
    RecommendationService recommendationService;


//    // 책장 카테고리 기반 추천 도서 조회
//    @GetMapping("/rating/{bookshelfId}")
//    public ResponseEntity<ApiResponse<List<BookResponse>>> getRecommendedBooksByCategory(@PathVariable Long bookshelfId) {
//        System.out.println("[API 요청] 추천 도서 조회 - 책장 ID: " + bookshelfId);
//
//        List<BookResponse> recommendedBooks = bookShelfService.getRecommendedBooksByCategory(bookshelfId);
//        System.out.println("[API 응답] 추천 도서 개수: " + recommendedBooks.size());
//
//        return ResponseEntity.ok(ApiResponse.onSuccess(recommendedBooks));
//    }

    @GetMapping("/rating/{bookshelfId}")
    public ResponseEntity<ApiResponse<List<BookRecommendDTO>>> getRecommendedBooksByCategory(@PathVariable Long bookshelfId) {
        System.out.println("[API 요청] 추천 도서 조회 - 책장 ID: " + bookshelfId);

        // 1. 추천 도서 조회
        List<BookResponse> recommendedBooks = bookShelfService.getRecommendedBooksByCategory(bookshelfId);
        System.out.println("[API 응답] 추천 도서 개수: " + recommendedBooks.size());

        // 2. 책장에 이미 있는 도서의 ISBN 가져오기
        List<String> existingIsbns = bookShelfService.getIsbn(bookshelfId);
        Set<String> existingIsbnSet = new HashSet<>(existingIsbns);
        System.out.println("[책장 내 도서] ISBN 목록: " + existingIsbnSet);

        // 3. 책장에 없는 도서만 필터링
        List<BookResponse> filteredRecommendedBooks = recommendedBooks.stream()
                .filter(book -> !existingIsbnSet.contains(book.getIsbn()))
                .toList();

        System.out.println("[필터링 후 추천 도서 개수]: " + filteredRecommendedBooks.size());

        // 4. ISBN 목록 추출
        Set<String> recommendedIsbns = filteredRecommendedBooks.stream()
                .map(BookResponse::getIsbn)
                .collect(Collectors.toSet());

        // 5. 평점 및 평가 개수 조회
        List<BookRatingResponse> ratings = aladinService.fetchBookRatings(new ArrayList<>(recommendedIsbns));

        // 6. 가중 평점 적용
        List<BookRatingResponse> weightedRatings = recommendationService.applyWeightedRating(ratings);

        // 7. 상위 5개 도서 선정
        List<BookRatingResponse> top5Ratings = weightedRatings.subList(0, Math.min(5, weightedRatings.size()));

        // 8. 최종 추천 도서 DTO 변환
        List<BookRecommendDTO> recommendedBookDTOs = top5Ratings.stream()
                .map(rating -> {
                    BookResponse book = filteredRecommendedBooks.stream()
                            .filter(b -> b.getIsbn().equals(rating.getIsbn()))
                            .findFirst()
                            .orElse(null);

                    if (book == null) return null;

                    return new BookRecommendDTO(
                            book.getTitle(),
                            book.getAuthor(),
                            book.getIsbn(),
                            rating.getRatingScore(),
                            book.getCover(),
                            book.getCategoryName()
                    );
                })
                .filter(Objects::nonNull) // Null 값 제거
                .toList();

        System.out.println("[최종 추천 도서]: " + recommendedBookDTOs);

        return ResponseEntity.ok(ApiResponse.onSuccess(recommendedBookDTOs));
    }


}
