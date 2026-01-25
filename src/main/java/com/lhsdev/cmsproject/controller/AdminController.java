package com.lhsdev.cmsproject.controller;

import com.lhsdev.cmsproject.config.SessionListener;
import com.lhsdev.cmsproject.entity.Menu;
import com.lhsdev.cmsproject.entity.SiteSetting;
import com.lhsdev.cmsproject.repository.BoardMetaRepository;
import com.lhsdev.cmsproject.repository.MenuRepository;
import com.lhsdev.cmsproject.service.BoardService;
import com.lhsdev.cmsproject.service.SettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    @Autowired
    private BoardService boardService;
    @Autowired
    private BoardMetaRepository boardMetaRepository;
    @Autowired
    private SettingService settingService;
    @Autowired
    private MenuRepository menuRepository;

    @GetMapping({ "", "/" })
    public String main(Model model) {
        model.addAttribute("activeSessionCount", SessionListener.getActiveSessions());
        model.addAttribute("latestPosts", boardService.getLatestPostsFromCheckedBoards());
        return "admin/main";
    }

    @GetMapping("/setting")
    public String setting(Model model) {
        model.addAttribute("siteSetting", settingService.getSetting());
        return "admin/setting";
    }

    @PostMapping("/setting/save")
    public String saveSetting(@ModelAttribute SiteSetting siteSetting, RedirectAttributes redirectAttributes) {
        try {
            settingService.saveSetting(siteSetting);
            redirectAttributes.addFlashAttribute("message", "설정이 성공적으로 저장되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "저장 실패: " + e.getMessage());
        }
        return "redirect:/admin/setting";
    }

    @GetMapping("/board")
    public String boardManage(Model model) {
        model.addAttribute("boards", boardMetaRepository.findAll());
        return "admin/board";
    }

    @GetMapping("/board/new")
    public String boardCreateForm() {
        return "admin/board_create";
    }

    @PostMapping("/board/create")
    public String createBoard(@RequestParam("boardId") String boardId,
            @RequestParam(value = "readPermission", defaultValue = "ALL") String readPermission,
            @RequestParam(value = "writePermission", defaultValue = "ALL") String writePermission,
            @RequestParam(value = "checkUpdate", defaultValue = "false") boolean checkUpdate,
            RedirectAttributes redirectAttributes) {
        try {
            boardService.createBoardTable(boardId, readPermission, writePermission, checkUpdate);
            redirectAttributes.addFlashAttribute("message", "게시판 'cms_board_" + boardId + "'이(가) 성공적으로 생성되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "게시판 생성 실패: " + e.getMessage());
        }
        return "redirect:/admin/board";
    }

    @GetMapping("/board/edit/{boardId}")
    public String editBoardForm(@PathVariable("boardId") String boardId, Model model) {
        boardMetaRepository.findByBoardId(boardId).ifPresent(meta -> model.addAttribute("board", meta));
        return "admin/board_edit";
    }

    @PostMapping("/board/update")
    public String updateBoard(@RequestParam("boardId") String boardId,
            @RequestParam("readPermission") String readPermission,
            @RequestParam("writePermission") String writePermission,
            @RequestParam(value = "checkUpdate", defaultValue = "false") boolean checkUpdate,
            RedirectAttributes redirectAttributes) {
        try {
            boardService.updateBoard(boardId, readPermission, writePermission, checkUpdate);
            redirectAttributes.addFlashAttribute("message", "게시판 설정이 업데이트되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "업데이트 실패: " + e.getMessage());
        }
        return "redirect:/admin/board";
    }

    @PostMapping("/board/delete")
    public String deleteBoard(@RequestParam("boardId") String boardId, RedirectAttributes redirectAttributes) {
        try {
            boardService.deleteBoard(boardId);
            redirectAttributes.addFlashAttribute("message", "게시판이 성공적으로 삭제되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "삭제 실패: " + e.getMessage());
        }
        return "redirect:/admin/board";
    }

    // --- Menu Management ---
    @GetMapping("/menu")
    public String menuManage(Model model) {
        model.addAttribute("menus", menuRepository.findAllByOrderBySortOrderAscMain1DepthAscMain2DepthAsc());
        model.addAttribute("existing1Depths", menuRepository.findDistinctMain1Depth());
        model.addAttribute("existingLinks", menuRepository.findDistinctLinks());
        return "admin/menu";
    }

    @PostMapping("/menu/reorder")
    @ResponseBody
    public ResponseEntity<?> reorderMenus(@RequestBody List<Long> menuIds) {
        try {
            for (int i = 0; i < menuIds.size(); i++) {
                Long id = menuIds.get(i);
                Menu menu = menuRepository.findById(id).orElseThrow();
                menu.setSortOrder(i);
                menuRepository.save(menu);
            }
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping("/menu/save")
    public String saveMenu(@ModelAttribute Menu menu, RedirectAttributes redirectAttributes) {
        try {
            menuRepository.save(menu);
            redirectAttributes.addFlashAttribute("message", "메뉴가 성공적으로 저장되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "메뉴 저장 실패: " + e.getMessage());
        }
        return "redirect:/admin/menu";
    }

    @PostMapping("/menu/delete")
    public String deleteMenu(@RequestParam("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            menuRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("message", "메뉴가 삭제되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "메뉴 삭제 실패: " + e.getMessage());
        }
        return "redirect:/admin/menu";
    }
}