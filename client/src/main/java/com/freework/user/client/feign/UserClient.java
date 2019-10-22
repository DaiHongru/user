package com.freework.user.client.feign;


import com.freework.user.client.vo.UserVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author daihongru
 */
@FeignClient(name = "USER", fallback = UserClientFallback.class)
public interface UserClient {
    /**
     * 查询用户详细信息
     *
     * @param userId
     * @return
     */
    @PostMapping("client/getUserInfo")
    UserVo getUserInfo(@RequestBody Integer userId);
}
