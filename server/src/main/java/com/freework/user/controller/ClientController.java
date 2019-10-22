package com.freework.user.controller;

import com.freework.user.client.vo.UserVo;
import com.freework.user.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author daihongru
 */
@RestController
@RequestMapping(value = "client")
public class ClientController {
    @Autowired
    private ClientService clientService;

    /**
     * 查询用户详细信息
     *
     * @param userId
     * @return
     */
    @PostMapping("getUserInfo")
    public UserVo getUserInfo(@RequestBody Integer userId) {
        return clientService.getUserInfo(userId);
    }
}
