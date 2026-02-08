package com.lhsdev.cmsproject.controller;

import com.lhsdev.cmsproject.repository.BoardMetaRepository;
import com.lhsdev.cmsproject.service.BoardService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {
    @Autowired
    private BoardService boardService;
    @Autowired
    private BoardMetaRepository boardMetaRepository;

    private final com.lhsdev.cmsproject.service.CommentService commentService;

    @GetMapping({ "", "/" })
    public String index(Model model) {
        model.addAttribute("boards", boardMetaRepository.findAll());
        return "board/main_list";
    }

    @GetMapping("/{boardId}")
    public String list(@PathVariable String boardId, Model model,
            @org.springframework.security.core.annotation.AuthenticationPrincipal com.lhsdev.cmsproject.config.auth.PrincipalDetails principal) {
        var board = boardMetaRepository.findByBoardId(boardId)
                .orElseThrow(() -> new IllegalArgumentException("Board not found"));

        if (!isAllowed(board.getReadPermission(), principal)) {
            return "redirect:/login?error=access_denied";
        }

        model.addAttribute("board", board);
        model.addAttribute("posts", boardService.getPosts(boardId));
        return "board/list";
    }

    @GetMapping("/{boardId}/view/{id}")
    public String view(@PathVariable String boardId, @PathVariable Long id, Model model,
            @org.springframework.security.core.annotation.AuthenticationPrincipal com.lhsdev.cmsproject.config.auth.PrincipalDetails principal) {
        var board = boardMetaRepository.findByBoardId(boardId)
                .orElseThrow(() -> new IllegalArgumentException("Board not found"));

        if (!isAllowed(board.getReadPermission(), principal)) {
            return "redirect:/login?error=access_denied";
        }

        model.addAttribute("board", board);
        model.addAttribute("post", boardService.getPost(boardId, id));
        model.addAttribute("comments", commentService.getComments(boardId, id));
        return "board/view";
    }

    @GetMapping("/{boardId}/write")
    public String writeForm(@PathVariable String boardId, Model model,
            @org.springframework.security.core.annotation.AuthenticationPrincipal com.lhsdev.cmsproject.config.auth.PrincipalDetails principal) {
        var board = boardMetaRepository.findByBoardId(boardId)
                .orElseThrow(() -> new IllegalArgumentException("Board not found"));

        if (!isAllowed(board.getWritePermission(), principal)) {
            return "redirect:/login?error=access_denied";
        }

        model.addAttribute("board", board);
        return "board/write";
    }

    @PostMapping("/{boardId}/save")
    public String save(@PathVariable String boardId,
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String password,
            @org.springframework.security.core.annotation.AuthenticationPrincipal com.lhsdev.cmsproject.config.auth.PrincipalDetails principal,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {

        var board = boardMetaRepository.findByBoardId(boardId).orElseThrow();
        if (!isAllowed(board.getWritePermission(), principal)) {
            redirectAttributes.addFlashAttribute("error", "권한이 없습니다.");
            return "redirect:/board/" + boardId;
        }

        try {
            String authorName = author;
            String authorId = null;
            String postPassword = password;

            if (principal != null) {
                authorName = principal.getUser().getName();
                authorId = String.valueOf(principal.getUser().getId());
                // Logged in users don't need password
            } else {
                if (author == null || author.trim().isEmpty()) {
                    throw new IllegalArgumentException("작성자 입력은 필수입니다.");
                }
                if (password == null || password.trim().isEmpty()) {
                    throw new IllegalArgumentException("비밀번호 입력은 필수입니다.");
                }
            }

            boardService.savePost(boardId, title, content, authorName, authorId, postPassword, request.getRemoteAddr());
            redirectAttributes.addFlashAttribute("message", "게시글이 성공적으로 등록되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "등록 실패: " + e.getMessage());
            return "redirect:/board/" + boardId + "/write";
        }
        return "redirect:/board/" + boardId;
    }

    @PostMapping("/{boardId}/comment/save")
    public String saveComment(@PathVariable String boardId,
            @RequestParam Long postId,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String password,
            @RequestParam String content,
            @org.springframework.security.core.annotation.AuthenticationPrincipal com.lhsdev.cmsproject.config.auth.PrincipalDetails principal,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {

        var board = boardMetaRepository.findByBoardId(boardId).orElseThrow();
        if (!isAllowed(board.getWritePermission(), principal)) {
            redirectAttributes.addFlashAttribute("error", "댓글 작성 권한이 없습니다.");
            return "redirect:/board/" + boardId + "/view/" + postId;
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
                    throw new IllegalArgumentException("작성자는 필수입니다.");
                if (password == null || password.trim().isEmpty())
                    throw new IllegalArgumentException("비밀번호는 필수입니다.");
            }

            commentService.saveComment(boardId, postId, authorName, authorId, content, commentPassword,
                    request.getRemoteAddr());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "댓글 등록 실패: " + e.getMessage());
        }
        return "redirect:/board/" + boardId + "/view/" + postId;
    }

    @PostMapping("/{boardId}/comment/delete")
    public String deleteComment(@PathVariable String boardId,
            @RequestParam Long postId,
            @RequestParam Long commentId,
            @RequestParam(required = false) String password,
            @org.springframework.security.core.annotation.AuthenticationPrincipal com.lhsdev.cmsproject.config.auth.PrincipalDetails principal,
            RedirectAttributes redirectAttributes) {
        try {
            Long userId = (principal != null) ? principal.getUser().getId() : null;
            commentService.deleteComment(commentId, password, userId);
            redirectAttributes.addFlashAttribute("message", "댓글이 삭제되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "댓글 삭제 실패: " + e.getMessage());
        }
        return "redirect:/board/" + boardId + "/view/" + postId;
    }

    private boolean isAllowed(String permission, com.lhsdev.cmsproject.config.auth.PrincipalDetails principal) {
        if ("ALL".equals(permission))
            return true;
        if ("USER".equals(permission))
            return principal != null;
        if ("ADMIN".equals(permission)) {
            return principal != null && "ADMIN".equals(principal.getUser().getRole().name());
        }
        return false;
    }
}
