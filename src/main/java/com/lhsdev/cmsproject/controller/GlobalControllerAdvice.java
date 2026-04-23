package com.lhsdev.cmsproject.controller;

import com.lhsdev.cmsproject.entity.Menu;
import com.lhsdev.cmsproject.repository.MenuRepository;
import com.lhsdev.cmsproject.service.SettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/global")
@RequiredArgsConstructor
@Tag(name = "공통설정", description = "글로벌 설정 API")
public class GlobalControllerAdvice {

    @Autowired
    private SettingService settingService;
    @Autowired
    private MenuRepository menuRepository;

    @Operation(summary = "글로벌 설정 조회", description = "사이트 글로벌 설정 및 메뉴 정보를 조회합니다.")
    @GetMapping("/config")
    public ResponseEntity<?> getGlobalConfig(jakarta.servlet.http.HttpServletRequest request) {
        request.getSession(true);
        Map<String, Object> response = new HashMap<>();

        response.put("siteSetting", settingService.getSetting());

        List<Menu> allMenus = menuRepository.findAllByOrderBySortOrderAscMain1DepthAscMain2DepthAsc();
        Map<String, List<Menu>> groupedMenus = allMenus.stream()
                .collect(Collectors.groupingBy(Menu::getMain1Depth, LinkedHashMap::new, Collectors.toList()));

        response.put("allMenus", allMenus);
        response.put("groupedMenus", groupedMenus);

        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()
                && !(auth instanceof org.springframework.security.authentication.AnonymousAuthenticationToken)) {
            Object principal = auth.getPrincipal();
            if (principal instanceof com.lhsdev.cmsproject.config.auth.PrincipalDetails) {
                response.put("user", ((com.lhsdev.cmsproject.config.auth.PrincipalDetails) principal).getUser());
            }
        }

        return ResponseEntity.ok(response);
    }
}
