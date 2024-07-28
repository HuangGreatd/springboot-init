package com.juzipi.domain.dto.req.user;

import lombok.Data;

/**
 * @author juzipi
 * @date 2024/7/16 19:59
 */
@Data
public class UserLoginRequest {
    private String userAccount;
    private String userPassword;
}
