package com.rzh12.notevino.repository.impl;

import com.rzh12.notevino.model.User;
import com.rzh12.notevino.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class UserRepositoryImpl implements UserRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // 儲存用戶資料
    @Override
    public void save(User user) {
        String sql = "INSERT INTO users (provider, username, email, password_hash, picture) VALUES (?, ?, ?, ?, ?)";

        jdbcTemplate.update(sql,
                user.getProvider(),
                user.getUsername(),
                user.getEmail(),
                user.getPasswordHash(),
                user.getPicture() != null ? user.getPicture() : null  // 處理 picture 為 null 的情況
        );
    }

    // 檢查用戶 email 是否已存在
    @Override
    public boolean existsByEmail(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email);
        return count != null && count > 0;
    }

    // 查詢用戶是否存在（根據 email）
    @Override
    public User findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new UserRowMapper(), email);
        } catch (Exception e) {
            return null;  // 如果沒有找到對應的用戶，返回 null
        }
    }

    // 更新用戶的大頭貼 URL
    @Override
    public void updateUserProfilePicture(Integer userId, String imageUrl) {
        String sql = "UPDATE users SET picture = ? WHERE user_id = ?";
        jdbcTemplate.update(sql, imageUrl, userId);
    }

    // 自定義 RowMapper，用來將查詢結果轉換為 User 物件
    private static class UserRowMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setUserId(rs.getInt("user_id"));
            user.setProvider(rs.getString("provider"));
            user.setUsername(rs.getString("username"));
            user.setEmail(rs.getString("email"));
            user.setPasswordHash(rs.getString("password_hash"));
            user.setPicture(rs.getString("picture"));
            user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());  // 從資料庫取得 created_at
            return user;
        }
    }

}

