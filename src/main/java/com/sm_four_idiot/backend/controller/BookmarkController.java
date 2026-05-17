package com.sm_four_idiot.backend.controller;

import com.sm_four_idiot.backend.dto.WordResponse;
import com.sm_four_idiot.backend.service.BookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 북마크 API 컨트롤러
 */
@RestController
@RequestMapping("/api/bookmarks")
@RequiredArgsConstructor
public class BookmarkController {

    private final BookmarkService bookmarkService;

    /**
     * 북마크 추가
     * POST /api/bookmarks/{wordId}
     */
    @PostMapping("/{wordId}")
    public ResponseEntity<Void> addBookmark(
            @AuthenticationPrincipal String email,
            @PathVariable Long wordId) {
        bookmarkService.addBookmark(email, wordId);
        return ResponseEntity.ok().build();
    }

    /**
     * 북마크 삭제
     * DELETE /api/bookmarks/{wordId}
     */
    @DeleteMapping("/{wordId}")
    public ResponseEntity<Void> removeBookmark(
            @AuthenticationPrincipal String email,
            @PathVariable Long wordId) {
        bookmarkService.removeBookmark(email, wordId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 북마크 목록 조회
     * GET /api/bookmarks
     */
    @GetMapping
    public ResponseEntity<List<WordResponse>> getBookmarks(
            @AuthenticationPrincipal String email) {
        return ResponseEntity.ok(bookmarkService.getBookmarks(email));
    }
}