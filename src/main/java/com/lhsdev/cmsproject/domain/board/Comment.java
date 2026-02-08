package com.lhsdev.cmsproject.domain.board;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "cms_comments", indexes = {
        @Index(name = "idx_comment_board_post", columnList = "boardId, postId")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String boardId;

    @Column(nullable = false)
    private Long postId;

    @Column(nullable = false, length = 50)
    private String author;

    @Column(length = 255)
    private String password;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    private String ipAddress;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @Column
    private Long authorId;
}
