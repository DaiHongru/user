package com.freework.user.controller;

import com.freework.user.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
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

}
