package com.lhsdev.cmsproject.repository;

import com.lhsdev.cmsproject.entity.BoardMeta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface BoardMetaRepository extends JpaRepository<BoardMeta, Long> {
    Optional<BoardMeta> findByBoardId(String boardId);

    List<BoardMeta> findByCheckUpdateTrue();
}
