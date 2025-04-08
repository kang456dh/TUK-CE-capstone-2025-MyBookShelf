package org.wsp.mybookshelf.domain.recommendation.service;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.wsp.mybookshelf.global.searchApi.dto.BookResponse;
import org.wsp.mybookshelf.domain.bookshelf.dto.BookShelfDTO;
import org.wsp.mybookshelf.domain.bookshelf.service.BookShelfService;
import org.wsp.mybookshelf.domain.user.service.UserService;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

@Service
public class CategoryRecommendService {

    private static final Logger logger = LoggerFactory.getLogger(CategoryRecommendService.class);

    private final Map<Integer, String[]> categoryMap = new HashMap<>();
    private final BookShelfService bookShelfService;

    public CategoryRecommendService(BookShelfService bookShelfService, LoanBookService loanBookService, UserService userService) {
        this.bookShelfService = bookShelfService;
    }

    @PostConstruct
    public void loadCategoryMappings() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("AladinToLibrary.csv"))))) {

            String line;
            boolean isFirstLine = true;

            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    try {
                        int categoryId = Integer.parseInt(parts[0].replaceAll("[^0-9]", ""));
                        categoryMap.put(categoryId, parts);
                    } catch (NumberFormatException e) {
                        logger.warn("잘못된 숫자 형식: {}", parts[0]);
                    }
                }
            }

        } catch (Exception e) {
            logger.error("CSV 로딩 중 오류 발생", e);
        }
    }

    public String getKdcByCategoryId(Integer categoryId) {
        String[] row = categoryMap.get(categoryId);
        if (row != null && row.length >= 4) {
            String kdc = row[2];
            // 숫자로 시작하는 것만 유효한 KDC로 간주
            if (kdc.matches("\\d+")) {
                return kdc;
            }
        }
        return null; // 유효한 KDC가 아닌 경우 null 반환
    }

    public void printCategoryRow(BookResponse bookResponse) {
        Integer categoryId = bookResponse.getCategoryId();
        String[] row = categoryMap.get(categoryId);
        if (row != null) {
            logger.info("카테고리 행 정보:");
            for (int i = 0; i < Math.min(row.length, 4); i++) {
                logger.info("{}열: {}", (i + 1), row[i]);
            }
        } else {
            logger.info("해당 categoryId에 대한 정보가 없습니다: {}", categoryId);
        }
    }

    // 책장 정보만 반환하도록 수정 (추천 도서 조회 로직은 제거)
    public List<BookShelfDTO> getUserBookshelves(Long userId) {
        List<BookShelfDTO> userBookshelves = bookShelfService.getBookShelf(userId);
        logger.info("[사용자 ID: {}]의 책장 수: {}", userId, userBookshelves.size());
        return userBookshelves;
    }
}