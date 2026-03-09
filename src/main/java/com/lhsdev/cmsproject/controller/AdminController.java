package com.lhsdev.cmsproject.controller;

import com.lhsdev.cmsproject.config.SessionListener;
import com.lhsdev.cmsproject.entity.Menu;

import com.lhsdev.cmsproject.repository.BoardMetaRepository;
import com.lhsdev.cmsproject.repository.MenuRepository;
import com.lhsdev.cmsproject.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    @Autowired
    private BoardService boardService;
    @Autowired
    private BoardMetaRepository boardMetaRepository;
    @Autowired
    private MenuRepository menuRepository;

    private final com.lhsdev.cmsproject.service.VisitorService visitorService;

    @GetMapping({ "", "/" })
    public ResponseEntity<?> main() {
        Map<String, Object> response = new HashMap<>();
        response.put("activeSessionCount", SessionListener.getActiveSessions());
        response.put("todayVisitorCount", visitorService.getTodayVisitorCount());
        response.put("totalVisitorCount", visitorService.getTotalVisitorCount());
        response.put("latestPosts", boardService.getLatestPostsFromCheckedBoards());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/board")
    public ResponseEntity<?> boardManage() {
        Map<String, Object> response = new HashMap<>();
        response.put("boards", boardMetaRepository.findAll());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/board/create")
    public ResponseEntity<?> createBoard(@RequestParam("boardId") String boardId,
            @RequestParam(value = "readPermission", defaultValue = "ALL") String readPermission,
            @RequestParam(value = "writePermission", defaultValue = "ALL") String writePermission,
            @RequestParam(value = "checkUpdate", defaultValue = "false") boolean checkUpdate) {
        try {
            boardService.createBoardTable(boardId, readPermission, writePermission, checkUpdate);
            return ResponseEntity
                    .ok(Map.of("success", true, "message", "게시판 'cms_board_" + boardId + "'이(가) 성공적으로 생성되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "게시판 생성 실패: " + e.getMessage()));
        }
    }

    @GetMapping("/board/edit/{boardId}")
    public ResponseEntity<?> editBoardForm(@PathVariable("boardId") String boardId) {
        var boardOpt = boardMetaRepository.findByBoardId(boardId);
        if (boardOpt.isEmpty())
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(Map.of("board", boardOpt.get()));
    }

    @PostMapping("/board/update")
    public ResponseEntity<?> updateBoard(@RequestParam("boardId") String boardId,
            @RequestParam("readPermission") String readPermission,
            @RequestParam("writePermission") String writePermission,
            @RequestParam(value = "checkUpdate", defaultValue = "false") boolean checkUpdate) {
        try {
            boardService.updateBoard(boardId, readPermission, writePermission, checkUpdate);
            return ResponseEntity.ok(Map.of("success", true, "message", "게시판 설정이 업데이트되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "업데이트 실패: " + e.getMessage()));
        }
    }

    @PostMapping("/board/delete")
    public ResponseEntity<?> deleteBoard(@RequestParam("boardId") String boardId) {
        try {
            boardService.deleteBoard(boardId);
            return ResponseEntity.ok(Map.of("success", true, "message", "게시판이 성공적으로 삭제되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "삭제 실패: " + e.getMessage()));
        }
    }

    @GetMapping("/menu")
    public ResponseEntity<?> menuManage() {
        Map<String, Object> response = new HashMap<>();
        response.put("menus", menuRepository.findAllByOrderBySortOrderAscMain1DepthAscMain2DepthAsc());
        response.put("existing1Depths", menuRepository.findDistinctMain1Depth());
        response.put("existingLinks", menuRepository.findDistinctLinks());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/menu/reorder")
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
            return ResponseEntity.internalServerError().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/menu/save")
    public ResponseEntity<?> saveMenu(@ModelAttribute Menu menu) {
        try {
            menuRepository.save(menu);
            return ResponseEntity.ok(Map.of("success", true, "message", "메뉴가 성공적으로 저장되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "메뉴 저장 실패: " + e.getMessage()));
        }
    }

    @PostMapping("/menu/delete")
    public ResponseEntity<?> deleteMenu(@RequestParam("id") Long id) {
        try {
            menuRepository.deleteById(id);
            return ResponseEntity.ok(Map.of("success", true, "message", "메뉴가 삭제되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "메뉴 삭제 실패: " + e.getMessage()));
        }
    }
}