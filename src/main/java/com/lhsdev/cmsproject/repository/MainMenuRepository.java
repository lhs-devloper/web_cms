package com.lhsdev.cmsproject.repository;

import com.lhsdev.cmsproject.domain.menu.MainMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MainMenuRepository extends JpaRepository<MainMenu, Long> {
    List<MainMenu> findAllByOrderBySortOrderAsc();

    List<MainMenu> findByIsActiveTrueOrderBySortOrderAsc();
}
