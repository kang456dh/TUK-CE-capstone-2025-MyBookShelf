package org.wsp.mybookshelf.global.recommandation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.wsp.mybookshelf.global.searchApi.dto.BookResponse;
import org.wsp.mybookshelf.global.recommandation.service.BookRecommendationService;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class recommandController {
    private final BookRecommendationService bookRecommendationService;

    @GetMapping("/recommendations")
    public ResponseEntity<List<BookResponse>> getRecommendations(@RequestParam String title,
                                                                 @RequestParam String author,
                                                                 @RequestParam String genre,
                                                                 @RequestParam int userAge) {
        BookResponse book = new BookResponse();
        book.setTitle(title);
        book.setAuthor(author);
        book.setGenre(genre);

        List<BookResponse> recommendations = bookRecommendationService.getRecommendedBooks(book, userAge);
        return ResponseEntity.ok(recommendations);
    }
}
