package com.lhsdev.cmsproject.repository;

import com.lhsdev.cmsproject.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {

    // Order by user-defined sortOrder, then by hierarchy
    List<Menu> findAllByOrderBySortOrderAscMain1DepthAscMain2DepthAsc();

    @Query("SELECT DISTINCT m.main1Depth FROM Menu m")
    List<String> findDistinctMain1Depth();

    @Query("SELECT DISTINCT m.link FROM Menu m")
    List<String> findDistinctLinks();
}
