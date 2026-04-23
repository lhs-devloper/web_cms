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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "관리자일반", description = "관리자 대시보드 및 게시판/메뉴 관리 API")
public class AdminController {
    @Autowired
    private BoardService boardService;
    @Autowired
    private BoardMetaRepository boardMetaRepository;
    @Autowired
    private MenuRepository menuRepository;

    private final com.lhsdev.cmsproject.service.VisitorService visitorService;

    @Operation(summary = "관리자 대시보드", description = "관리자 대시보드 데이터를 조회합니다.")
    @GetMapping({ "", "/" })
    public ResponseEntity<?> main() {
        Map<String, Object> response = new HashMap<>();
        response.put("activeSessionCount", SessionListener.getActiveSessions());
        response.put("todayVisitorCount", visitorService.getTodayVisitorCount());
        response.put("totalVisitorCount", visitorService.getTotalVisitorCount());
        response.put("latestPosts", boardService.getLatestPostsFromCheckedBoards());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "게시판 관리 목록", description = "관리 가능한 게시판 목록을 조회합니다.")
    @GetMapping("/board")
    public ResponseEntity<?> boardManage() {
        Map<String, Object> response = new HashMap<>();
        response.put("boards", boardMetaRepository.findAll());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "게시판 생성", description = "새 게시판을 생성합니다.")
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

    @Operation(summary = "게시판 수정 폼", description = "게시판 수정을 위한 데이터를 조회합니다.")
    @GetMapping("/board/edit/{boardId}")
    public ResponseEntity<?> editBoardForm(@PathVariable("boardId") String boardId) {
        var boardOpt = boardMetaRepository.findByBoardId(boardId);
        if (boardOpt.isEmpty())
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(Map.of("board", boardOpt.get()));
    }

    @Operation(summary = "게시판 수정", description = "게시판 설정을 수정합니다.")
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

    @Operation(summary = "게시판 삭제", description = "게시판을 삭제합니다.")
    @PostMapping("/board/delete")
    public ResponseEntity<?> deleteBoard(@RequestParam("boardId") String boardId) {
        try {
            boardService.deleteBoard(boardId);
            return ResponseEntity.ok(Map.of("success", true, "message", "게시판이 성공적으로 삭제되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "삭제 실패: " + e.getMessage()));
        }
    }

    @Operation(summary = "메뉴 관리 목록", description = "관리 가능한 메뉴 목록을 조회합니다.")
    @GetMapping("/menu")
    public ResponseEntity<?> menuManage() {
        Map<String, Object> response = new HashMap<>();
        response.put("menus", menuRepository.findAllByOrderBySortOrderAscMain1DepthAscMain2DepthAsc());
        response.put("existing1Depths", menuRepository.findDistinctMain1Depth());
        response.put("existingLinks", menuRepository.findDistinctLinks());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "메뉴 순서 변경", description = "메뉴의 표시 순서를 변경합니다.")
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

    @Operation(summary = "메뉴 저장", description = "메뉴를 등록 또는 수정합니다.")
    @PostMapping("/menu/save")
    public ResponseEntity<?> saveMenu(@ModelAttribute Menu menu) {
        try {
            menuRepository.save(menu);
            return ResponseEntity.ok(Map.of("success", true, "message", "메뉴가 성공적으로 저장되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "메뉴 저장 실패: " + e.getMessage()));
        }
    }

    @Operation(summary = "메뉴 삭제", description = "메뉴를 삭제합니다.")
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