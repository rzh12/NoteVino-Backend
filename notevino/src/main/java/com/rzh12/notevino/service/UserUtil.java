package com.rzh12.notevino.service;

import com.rzh12.notevino.dto.UserDetailDTO;
import org.springframework.security.core.context.SecurityContextHolder;

public class UserUtil {

    public static Integer getCurrentUserId() {
        UserDetailDTO currentUser = (UserDetailDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return currentUser.getUserId();
    }
}
