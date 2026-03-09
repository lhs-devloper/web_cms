package com.lhsdev.cmsproject.service;

import com.lhsdev.cmsproject.domain.menu.MainMenu;
import com.lhsdev.cmsproject.repository.MainMenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MainMenuService {

    private final MainMenuRepository mainMenuRepository;

    public List<MainMenu> getAllMenus() {
        return mainMenuRepository.findAllByOrderBySortOrderAsc();
    }

    public List<MainMenu> getActiveMenus() {
        return mainMenuRepository.findByIsActiveTrueOrderBySortOrderAsc();
    }

    public MainMenu getMenu(Long id) {
        return mainMenuRepository.findById(id).orElse(null);
    }

    @Transactional
    public void saveMenu(MainMenu menu) {
        // If sortOrder is null, append to the end
        if (menu.getSortOrder() == null) {
            long count = mainMenuRepository.count();
            menu.update(menu.getTitle(), menu.getLinkUrl(), (int) count,
                    menu.getIsActive() != null ? menu.getIsActive() : true, menu.getParentId());
        }
        mainMenuRepository.save(menu);
    }

    @Transactional
    public void updateMenu(Long id, MainMenu requestMenu) {
        MainMenu menu = mainMenuRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid menu id"));
        menu.update(requestMenu.getTitle(), requestMenu.getLinkUrl(), requestMenu.getSortOrder(),
                requestMenu.getIsActive(), requestMenu.getParentId());
    }

    @Transactional
    public void deleteMenu(Long id) {
        mainMenuRepository.deleteById(id);
    }

    @Transactional
    public void updateSortOrders(List<Long> ids) {
        for (int i = 0; i < ids.size(); i++) {
            Long id = ids.get(i);
            MainMenu menu = mainMenuRepository.findById(id).orElse(null);
            if (menu != null) {
                menu.update(menu.getTitle(), menu.getLinkUrl(), i, menu.getIsActive(), menu.getParentId());
            }
        }
    }
}
