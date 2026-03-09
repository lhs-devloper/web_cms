package com.lhsdev.cmsproject.controller.global;

import com.lhsdev.cmsproject.domain.menu.MainMenu;
import com.lhsdev.cmsproject.service.MainMenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/global/menus")
@RequiredArgsConstructor
public class MenuGlobalController {

    private final MainMenuService mainMenuService;

    @GetMapping
    public ResponseEntity<List<MainMenu>> getActiveMenus() {
        return ResponseEntity.ok(mainMenuService.getActiveMenus());
    }
}
