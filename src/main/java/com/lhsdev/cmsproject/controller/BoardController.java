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

    @GetMapping({ "", "/" })
    public String index(Model model) {
        model.addAttribute("boards", boardMetaRepository.findAll());
        return "board/main_list";
    }

    @GetMapping("/{boardId}")
    public String list(@PathVariable String boardId, Model model) {
        model.addAttribute("board", boardMetaRepository.findByBoardId(boardId).orElseThrow());
        model.addAttribute("posts", boardService.getPosts(boardId));
        return "board/list";
    }

    @GetMapping("/{boardId}/view/{id}")
    public String view(@PathVariable String boardId, @PathVariable Long id, Model model) {
        model.addAttribute("board", boardMetaRepository.findByBoardId(boardId).orElseThrow());
        model.addAttribute("post", boardService.getPost(boardId, id));
        return "board/view";
    }

    @GetMapping("/{boardId}/write")
    public String writeForm(@PathVariable String boardId, Model model) {
        model.addAttribute("board", boardMetaRepository.findByBoardId(boardId).orElseThrow());
        return "board/write";
    }

    @PostMapping("/{boardId}/save")
    public String save(@PathVariable String boardId,
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam String author,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {
        try {
            boardService.savePost(boardId, title, content, author, request.getRemoteAddr());
            redirectAttributes.addFlashAttribute("message", "게시글이 성공적으로 등록되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "등록 실패: " + e.getMessage());
            return "redirect:/board/" + boardId + "/write";
        }
        return "redirect:/board/" + boardId;
    }
}
