package com.lhsdev.cmsproject.repository;

import com.lhsdev.cmsproject.domain.board.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByBoardIdAndPostIdOrderByCreatedAtAsc(String boardId, Long postId);
}
