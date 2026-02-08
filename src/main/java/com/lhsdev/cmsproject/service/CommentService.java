package com.lhsdev.cmsproject.service;

import com.lhsdev.cmsproject.domain.board.Comment;
import com.lhsdev.cmsproject.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;

    @Transactional
    public Comment saveComment(String boardId, Long postId, String author, Long authorId, String content,
            String password, String ip) {
        Comment comment = Comment.builder()
                .boardId(boardId)
                .postId(postId)
                .author(author)
                .authorId(authorId)
                .content(content)
                .password(password)
                .ipAddress(ip)
                .build();
        return commentRepository.save(comment);
    }

    @Transactional(readOnly = true)
    public List<Comment> getComments(String boardId, Long postId) {
        return commentRepository.findByBoardIdAndPostIdOrderByCreatedAtAsc(boardId, postId);
    }

    @Transactional
    public void deleteComment(Long id, String password, Long userId) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다."));

        // If authorId matches, allow deletion
        if (userId != null && userId.equals(comment.getAuthorId())) {
            commentRepository.delete(comment);
            return;
        }

        if (password == null || !password.equals(comment.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        commentRepository.delete(comment);
    }
}
