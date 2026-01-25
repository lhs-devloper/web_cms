package com.lhsdev.cmsproject.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "cms_board_meta")
@Getter
@Setter
@NoArgsConstructor
public class BoardMeta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String boardId;

    @Column(nullable = false)
    private String tableName;

    // Permissions: ALL (everyone), MEMBER (logged in), ADMIN (admin only)
    @Column(nullable = false)
    private String readPermission = "ALL";

    @Column(nullable = false)
    private String writePermission = "ALL";

    @Column(nullable = false)
    private boolean checkUpdate = false;

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    public BoardMeta(String boardId, String tableName) {
        this.boardId = boardId;
        this.tableName = tableName;
    }

    public BoardMeta(String boardId, String tableName, String readPermission, String writePermission) {
        this.boardId = boardId;
        this.tableName = tableName;
        this.readPermission = readPermission;
        this.writePermission = writePermission;
    }
}
