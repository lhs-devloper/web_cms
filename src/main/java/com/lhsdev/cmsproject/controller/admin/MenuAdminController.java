package com.lhsdev.cmsproject.controller.admin;

import com.lhsdev.cmsproject.domain.menu.MainMenu;
import com.lhsdev.cmsproject.service.MainMenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/menus")
@RequiredArgsConstructor
@Tag(name = "관리자메뉴", description = "메인 메뉴 관리 API (관리자 전용)")
public class MenuAdminController {

    private final MainMenuService mainMenuService;

    @Operation(summary = "메뉴 목록 조회", description = "모든 메인 메뉴를 조회합니다.")
    @GetMapping
    public ResponseEntity<?> index() {
        return ResponseEntity.ok(Map.of("menus", mainMenuService.getAllMenus()));
    }

    @Operation(summary = "메뉴 저장", description = "메뉴를 등록 또는 수정합니다.")
    @PostMapping("/save")
    public ResponseEntity<?> save(@RequestBody MainMenu menu) {
        try {
            if (menu.getId() != null) {
                mainMenuService.updateMenu(menu.getId(), menu);
            } else {
                mainMenuService.saveMenu(menu);
            }
            return ResponseEntity.ok(Map.of("success", true, "message", "Menu saved successfully."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Error saving menu: " + e.getMessage()));
        }
    }

    @Operation(summary = "메뉴 삭제", description = "메뉴를 삭제합니다.")
    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestParam Long id) {
        try {
            mainMenuService.deleteMenu(id);
            return ResponseEntity.ok(Map.of("success", true, "message", "Menu deleted successfully."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Error deleting menu: " + e.getMessage()));
        }
    }

    @Operation(summary = "메뉴 순서 변경", description = "메뉴의 표시 순서를 변경합니다.")
    @PostMapping("/reorder")
    public ResponseEntity<?> reorder(@RequestBody List<Long> ids) {
        try {
            mainMenuService.updateSortOrders(ids);
            return ResponseEntity.ok(Map.of("success", true, "message", "Menu order updated."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Error updating order: " + e.getMessage()));
        }
    }
}
