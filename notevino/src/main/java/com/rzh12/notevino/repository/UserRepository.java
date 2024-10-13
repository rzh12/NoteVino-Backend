package com.rzh12.notevino.repository;

import com.rzh12.notevino.model.User;

public interface UserRepository {
    void save(User user);
    boolean existsByEmail(String email);
    User findByEmail(String email);

    // 更新用戶的大頭貼 URL
    void updateUserAvatar(Integer userId, String imageUrl);
}
