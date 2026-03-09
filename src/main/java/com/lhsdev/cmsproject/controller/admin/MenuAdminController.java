package com.lhsdev.cmsproject.controller.admin;

import com.lhsdev.cmsproject.domain.menu.MainMenu;
import com.lhsdev.cmsproject.service.MainMenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/menus")
@RequiredArgsConstructor
public class MenuAdminController {

    private final MainMenuService mainMenuService;

    @GetMapping
    public ResponseEntity<?> index() {
        return ResponseEntity.ok(Map.of("menus", mainMenuService.getAllMenus()));
    }

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

    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestParam Long id) {
        try {
            mainMenuService.deleteMenu(id);
            return ResponseEntity.ok(Map.of("success", true, "message", "Menu deleted successfully."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Error deleting menu: " + e.getMessage()));
        }
    }

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
