package com.lhsdev.cmsproject.controller;

import com.lhsdev.cmsproject.entity.Menu;
import com.lhsdev.cmsproject.repository.MenuRepository;
import com.lhsdev.cmsproject.service.SettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerAdvice {
    @Autowired
    private SettingService settingService;
    @Autowired
    private MenuRepository menuRepository;

    @ModelAttribute
    public void addAttributes(org.springframework.ui.Model model, jakarta.servlet.http.HttpServletRequest request) {
        request.getSession(true);

        model.addAttribute("siteSetting", settingService.getSetting());
        model.addAttribute("currentUri", request.getRequestURI());

        // Group menus by 1st depth for 2-depth rendering
        // Use LinkedHashMap to preserve order of Main1Depth if wanted
        List<Menu> allMenus = menuRepository.findAllByOrderBySortOrderAscMain1DepthAscMain2DepthAsc();
        Map<String, List<Menu>> groupedMenus = allMenus.stream()
                .collect(Collectors.groupingBy(Menu::getMain1Depth, LinkedHashMap::new, Collectors.toList()));

        model.addAttribute("allMenus", allMenus);
        model.addAttribute("groupedMenus", groupedMenus);
    }
}
