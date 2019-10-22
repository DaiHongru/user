package com.freework.user.service;

import com.freework.user.client.vo.UserVo;
import org.springframework.stereotype.Service;

/**
 * @author daihongru
 */
@Service
public interface ClientService {
    /**
     * 查询用户详细信息
     *
     * @param userId
     * @return
     */
    UserVo getUserInfo(Integer userId);
}
