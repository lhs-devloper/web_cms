package com.lhsdev.cmsproject.controller;

import com.lhsdev.cmsproject.repository.BoardMetaRepository;
import com.lhsdev.cmsproject.service.BoardService;
import com.lhsdev.cmsproject.config.auth.PrincipalDetails;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/board")
@RequiredArgsConstructor
@Tag(name = "게시판", description = "게시판 CRUD 및 댓글 API")
public class BoardController {
    @Autowired
    private BoardService boardService;
    @Autowired
    private BoardMetaRepository boardMetaRepository;

    private final com.lhsdev.cmsproject.service.CommentService commentService;

    @Operation(summary = "게시판 목록", description = "전체 게시판 목록을 조회합니다.")
    @GetMapping({ "", "/" })
    public ResponseEntity<?> index() {
        return ResponseEntity.ok(boardMetaRepository.findAll());
    }

    @Operation(summary = "게시글 목록", description = "특정 게시판의 게시글 목록을 조회합니다.")
    @GetMapping("/{boardId}")
    public ResponseEntity<?> list(@PathVariable String boardId,
            @AuthenticationPrincipal PrincipalDetails principal) {
        var boardOpt = boardMetaRepository.findByBoardId(boardId);
        if (boardOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var board = boardOpt.get();

        if (!isAllowed(board.getReadPermission(), principal)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "access_denied"));
        }

        Map<String, Object> response = new HashMap<>();
        response.put("board", board);
        response.put("posts", boardService.getPosts(boardId));
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "게시글 상세", description = "게시글 상세 내용을 조회합니다.")
    @GetMapping("/{boardId}/view/{id}")
    public ResponseEntity<?> view(@PathVariable String boardId, @PathVariable Long id,
            @AuthenticationPrincipal PrincipalDetails principal) {
        var boardOpt = boardMetaRepository.findByBoardId(boardId);
        if (boardOpt.isEmpty())
            return ResponseEntity.notFound().build();
        var board = boardOpt.get();

        if (!isAllowed(board.getReadPermission(), principal)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "access_denied"));
        }

        Map<String, Object> response = new HashMap<>();
        response.put("board", board);
        response.put("post", boardService.getPost(boardId, id));
        response.put("comments", commentService.getComments(boardId, id));
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "게시글 작성", description = "새 게시글을 작성합니다.")
    @PostMapping("/{boardId}/save")
    public ResponseEntity<?> save(@PathVariable String boardId,
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String password,
            @AuthenticationPrincipal PrincipalDetails principal,
            HttpServletRequest request) {

        var boardOpt = boardMetaRepository.findByBoardId(boardId);
        if (boardOpt.isEmpty())
            return ResponseEntity.notFound().build();
        var board = boardOpt.get();

        if (!isAllowed(board.getWritePermission(), principal)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "권한이 없습니다."));
        }

        try {
            String authorName = author;
            String authorId = null;
            String postPassword = password;

            if (principal != null) {
                authorName = principal.getUser().getName();
                authorId = String.valueOf(principal.getUser().getId());
            } else {
                if (author == null || author.trim().isEmpty()) {
                    return ResponseEntity.badRequest().body(Map.of("message", "작성자 입력은 필수입니다."));
                }
                if (password == null || password.trim().isEmpty()) {
                    return ResponseEntity.badRequest().body(Map.of("message", "비밀번호 입력은 필수입니다."));
                }
            }

            boardService.savePost(boardId, title, content, authorName, authorId, postPassword, request.getRemoteAddr());
            return ResponseEntity.ok(Map.of("success", true, "message", "게시글이 성공적으로 등록되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "등록 실패: " + e.getMessage()));
        }
    }

    @Operation(summary = "게시글 수정", description = "게시글을 수정합니다.")
    @PostMapping("/{boardId}/update")
    public ResponseEntity<?> updatePost(@PathVariable String boardId,
            @RequestParam Long id,
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam(required = false) String password,
            @AuthenticationPrincipal PrincipalDetails principal) {

        var boardOpt = boardMetaRepository.findByBoardId(boardId);
        if (boardOpt.isEmpty())
            return ResponseEntity.notFound().build();
        var board = boardOpt.get();

        if (!isAllowed(board.getWritePermission(), principal)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "권한이 없습니다."));
        }

        try {
            String userId = (principal != null) ? String.valueOf(principal.getUser().getId()) : null;
            boolean isAdmin = principal != null && "ADMIN".equals(principal.getUser().getRole().name());
            boardService.updatePost(boardId, id, title, content, password, userId, isAdmin);
            return ResponseEntity.ok(Map.of("success", true, "message", "게시글이 성공적으로 수정되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "수정 실패: " + e.getMessage()));
        }
    }

    @Operation(summary = "게시글 삭제", description = "게시글을 삭제합니다.")
    @PostMapping("/{boardId}/delete")
    public ResponseEntity<?> deletePost(@PathVariable String boardId,
            @RequestParam Long id,
            @RequestParam(required = false) String password,
            @AuthenticationPrincipal PrincipalDetails principal) {

        try {
            String userId = (principal != null) ? String.valueOf(principal.getUser().getId()) : null;
            boolean isAdmin = principal != null && "ADMIN".equals(principal.getUser().getRole().name());
            boardService.deletePost(boardId, id, password, userId, isAdmin);
            return ResponseEntity.ok(Map.of("success", true, "message", "게시글이 삭제되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "삭제 실패: " + e.getMessage()));
        }
    }

    @Operation(summary = "댓글 작성", description = "게시글에 댓글을 작성합니다.")
    @PostMapping("/{boardId}/comment/save")
    public ResponseEntity<?> saveComment(@PathVariable String boardId,
            @RequestParam Long postId,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String password,
            @RequestParam String content,
            @AuthenticationPrincipal PrincipalDetails principal,
            HttpServletRequest request) {

        var boardOpt = boardMetaRepository.findByBoardId(boardId);
        if (boardOpt.isEmpty())
            return ResponseEntity.notFound().build();
        var board = boardOpt.get();

        if (!isAllowed(board.getWritePermission(), principal)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "댓글 작성 권한이 없습니다."));
        }

        try {
            String authorName = author;
            String commentPassword = password;
            Long authorId = null;

            if (principal != null) {
                authorName = principal.getUser().getName();
                authorId = principal.getUser().getId();
            } else {
                if (author == null || author.trim().isEmpty())
                    return ResponseEntity.badRequest().body(Map.of("message", "작성자는 필수입니다."));
                if (password == null || password.trim().isEmpty())
                    return ResponseEntity.badRequest().body(Map.of("message", "비밀번호는 필수입니다."));
            }

            commentService.saveComment(boardId, postId, authorName, authorId, content, commentPassword,
                    request.getRemoteAddr());
            return ResponseEntity.ok(Map.of("success", true, "message", "댓글 등록 완료"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "댓글 등록 실패: " + e.getMessage()));
        }
    }

    @Operation(summary = "댓글 삭제", description = "댓글을 삭제합니다.")
    @PostMapping("/{boardId}/comment/delete")
    public ResponseEntity<?> deleteComment(@PathVariable String boardId,
            @RequestParam Long postId,
            @RequestParam Long commentId,
            @RequestParam(required = false) String password,
            @AuthenticationPrincipal PrincipalDetails principal) {
        try {
            Long userId = (principal != null) ? principal.getUser().getId() : null;
            commentService.deleteComment(commentId, password, userId);
            return ResponseEntity.ok(Map.of("success", true, "message", "댓글이 삭제되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "댓글 삭제 실패: " + e.getMessage()));
        }
    }

    private boolean isAllowed(String permission, PrincipalDetails principal) {
        if ("ALL".equals(permission))
            return true;
        if ("USER".equals(permission))
            return principal != null;
        if ("ADMIN".equals(permission))
            return principal != null && "ADMIN".equals(principal.getUser().getRole().name());
        return false;
    }
}
