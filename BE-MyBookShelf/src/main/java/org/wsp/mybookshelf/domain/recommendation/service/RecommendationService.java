package org.wsp.mybookshelf.domain.recommendation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.wsp.mybookshelf.global.searchApi.dto.BookRatingResponse;
import org.wsp.mybookshelf.global.searchApi.service.AladinService;

import java.util.Comparator;
import java.util.List;

@Service
public class RecommendationService {

    @Autowired
    private AladinService aladinService;

    private static final int M = 10;  // 최소 평가 개수 (예: 10개 이상 평가된 책만 반영)

    public List<BookRatingResponse> applyWeightedRating(List<BookRatingResponse> ratings) {
        double C = calculateGlobalAverageRating(ratings); // 전체 평균 평점 계산

        // 각 책에 대해 가중 평점 적용
        for (BookRatingResponse rating : ratings) {
            double weightedRating = calculateWeightedRating(rating.getRatingScore(), rating.getRatingCount(), C);
            rating.setRatingScore(weightedRating);  // 기존 평점 필드에 덮어쓰기
        }

        // 가중 평점 기준 정렬 (내림차순)
        ratings.sort(Comparator.comparing(BookRatingResponse::getRatingScore).reversed());

        return ratings;
    }

    private double calculateWeightedRating(double R, int v, double C) {
        return (v / (double) (v + M)) * R + (M / (double) (v + M)) * C;
    }

    private double calculateGlobalAverageRating(List<BookRatingResponse> ratings) {
        return ratings.stream()
                .mapToDouble(BookRatingResponse::getRatingScore)
                .average()
                .orElse(0.0);  // 모든 데이터가 없으면 0 반환
    }
}
