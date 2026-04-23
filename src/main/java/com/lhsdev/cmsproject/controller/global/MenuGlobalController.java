package com.lhsdev.cmsproject.controller.global;

import com.lhsdev.cmsproject.domain.menu.MainMenu;
import com.lhsdev.cmsproject.service.MainMenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@RestController
@RequestMapping("/api/global/menus")
@RequiredArgsConstructor
@Tag(name = "공통메뉴", description = "공개 메뉴 조회 API")
public class MenuGlobalController {

    private final MainMenuService mainMenuService;

    @Operation(summary = "활성 메뉴 조회", description = "현재 활성화된 메인 메뉴 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<MainMenu>> getActiveMenus() {
        return ResponseEntity.ok(mainMenuService.getActiveMenus());
    }
}
