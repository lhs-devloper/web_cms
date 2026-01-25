package com.lhsdev.cmsproject.service;

import com.lhsdev.cmsproject.entity.BoardMeta;
import com.lhsdev.cmsproject.repository.BoardMetaRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;
import java.time.Duration;
import java.util.ArrayList;
import java.sql.Timestamp;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardService {
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private BoardMetaRepository boardMetaRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Transactional
    public void createBoardTable(String boardId, String readPermission, String writePermission, boolean checkUpdate) {
        if (!boardId.matches("^[a-zA-Z0-9_]+$")) {
            throw new IllegalArgumentException("게시판 ID는 영문, 숫자, 언더바(_)만 가능합니다.");
        }

        if (boardMetaRepository.findByBoardId(boardId).isPresent()) {
            throw new RuntimeException("이미 존재하는 게시판 ID입니다.");
        }

        String tableName = "cms_board_" + boardId;

        String sql = "CREATE TABLE " + tableName + " (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                "title VARCHAR(255) NOT NULL, " +
                "content TEXT, " +
                "author VARCHAR(100), " +
                "author_id VARCHAR(100), " +
                "password VARCHAR(255), " +
                "is_secret BOOLEAN DEFAULT FALSE, " +
                "ip_address VARCHAR(45), " +
                "is_notice BOOLEAN DEFAULT FALSE, " +
                "view_count INT DEFAULT 0, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci";

        try {
            entityManager.createNativeQuery(sql).executeUpdate();
            BoardMeta meta = new BoardMeta(boardId, tableName, readPermission, writePermission);
            meta.setCheckUpdate(checkUpdate);
            boardMetaRepository.save(meta);
            log.info("Successfully created advanced table and meta for: {}", tableName);
        } catch (Exception e) {
            log.error("Failed to create board: {}", boardId, e);
            throw new RuntimeException("게시판 테이블 생성 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @Transactional
    public void updateBoard(String boardId, String readPermission, String writePermission, boolean checkUpdate) {
        BoardMeta meta = boardMetaRepository.findByBoardId(boardId)
                .orElseThrow(() -> new RuntimeException("게시판을 찾을 수 없습니다: " + boardId));

        meta.setReadPermission(readPermission);
        meta.setWritePermission(writePermission);
        meta.setCheckUpdate(checkUpdate);
        boardMetaRepository.save(meta);
        log.info("Successfully updated settings for board: {}", boardId);
    }

    @Transactional
    public void deleteBoard(String boardId) {
        BoardMeta meta = boardMetaRepository.findByBoardId(boardId)
                .orElseThrow(() -> new RuntimeException("게시판을 찾을 수 없습니다: " + boardId));

        String tableName = meta.getTableName();

        try {
            entityManager.createNativeQuery("DROP TABLE IF EXISTS " + tableName).executeUpdate();
            boardMetaRepository.delete(meta);
            log.info("Successfully deleted board table and meta for: {}", tableName);
        } catch (Exception e) {
            log.error("Failed to delete board: {}", boardId, e);
            throw new RuntimeException("게시판 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getPosts(String boardId) {
        BoardMeta meta = boardMetaRepository.findByBoardId(boardId)
                .orElseThrow(() -> new RuntimeException("게시판을 찾을 수 없습니다."));

        String sql = "SELECT * FROM " + meta.getTableName() + " ORDER BY is_notice DESC, id DESC";
        // Using JdbcTemplate as it handles Map conversion naturally for non-entity
        // queries
        return jdbcTemplate.queryForList(sql);
    }

    @Transactional
    public Map<String, Object> getPost(String boardId, Long id) {
        BoardMeta meta = boardMetaRepository.findByBoardId(boardId)
                .orElseThrow(() -> new RuntimeException("게시판을 찾을 수 없습니다."));

        // Update view count using JdbcTemplate
        String updateSql = "UPDATE " + meta.getTableName() + " SET view_count = view_count + 1 WHERE id = ?";
        jdbcTemplate.update(updateSql, id);

        // Fetch post
        String selectSql = "SELECT * FROM " + meta.getTableName() + " WHERE id = ?";
        return jdbcTemplate.queryForMap(selectSql, id);
    }

    @Transactional
    public void savePost(String boardId, String title, String content, String author, String ip) {
        BoardMeta meta = boardMetaRepository.findByBoardId(boardId)
                .orElseThrow(() -> new RuntimeException("게시판을 찾을 수 없습니다."));

        String sql = "INSERT INTO " + meta.getTableName() + " (title, content, author, ip_address) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, title, content, author, ip);
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getLatestPostsFromCheckedBoards() {
        List<BoardMeta> checkedBoards = boardMetaRepository.findByCheckUpdateTrue();
        if (checkedBoards.isEmpty()) {
            return List.of();
        }

        StringBuilder sqlBuilder = new StringBuilder();
        for (int i = 0; i < checkedBoards.size(); i++) {
            BoardMeta meta = checkedBoards.get(i);
            sqlBuilder.append("SELECT '")
                    .append(meta.getBoardId())
                    .append("' as board_id, id, title, content, author, created_at FROM ")
                    .append(meta.getTableName());
            if (i < checkedBoards.size() - 1) {
                sqlBuilder.append(" UNION ALL ");
            }
        }
        sqlBuilder.append(" ORDER BY created_at DESC LIMIT 10");

        List<Map<String, Object>> posts = jdbcTemplate.queryForList(sqlBuilder.toString());
        List<Map<String, Object>> formattedPosts = new ArrayList<>();

        for (Map<String, Object> post : posts) {
            Map<String, Object> formattedPost = new java.util.HashMap<>(post);
            Object createdAtObj = post.get("created_at");
            if (createdAtObj instanceof Timestamp) {
                LocalDateTime createdAt = ((Timestamp) createdAtObj).toLocalDateTime();
                formattedPost.put("relative_time", getRelativeTime(createdAt));
            } else if (createdAtObj instanceof LocalDateTime) {
                formattedPost.put("relative_time", getRelativeTime((LocalDateTime) createdAtObj));
            }

            // Strip HTML from content
            String rawContent = (String) post.get("content");
            if (rawContent != null) {
                String plainText = rawContent.replaceAll("<[^>]*>", " ").replaceAll("\\s+", " ").trim();
                formattedPost.put("plain_content", plainText);
            } else {
                formattedPost.put("plain_content", "");
            }

            formattedPosts.add(formattedPost);
        }

        return formattedPosts;
    }

    private String getRelativeTime(LocalDateTime dateTime) {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(dateTime, now);
        long seconds = duration.getSeconds();

        if (seconds < 0)
            return "방금 전";
        if (seconds < 60)
            return seconds + "초 전";

        long minutes = duration.toMinutes();
        if (minutes < 60)
            return minutes + "분 전";

        long hours = duration.toHours();
        if (hours < 24)
            return hours + "시간 전";

        long days = duration.toDays();
        if (days < 7)
            return days + "일 전";

        return "오래된 글";
    }
}
