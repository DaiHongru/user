package com.freeowork.user.client.feign;


import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author daihongru
 */
@FeignClient(name = "USER", fallback = UserClientFallback.class)
public interface UserClient {

}
