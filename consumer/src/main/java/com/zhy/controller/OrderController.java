package com.zhy.controller;

import com.zhy.service.OrderService;
import com.zhy.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


public class OrderController {

    @DubboReference
    private OrderService orderService;


    @GetMapping("demo1")
    public String createOrder(){
        orderService.createOrder(1,1,5);
        return "success";
    }
    @GetMapping("demo2")
    public String cal(){
        String ans = orderService.caculater(4,5);
        return ans;
    }

}
