package com.zhy.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;

@DubboService
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Override
    public void createOrder(Integer uid, Integer skuid, Integer skunum) {
        log.info("createOrder()执行：参数为："+uid+"..."+skuid+"..."+skunum);
    }

    @Override
    public String caculater(Integer a, Integer b) {
        return ("a="+a+",b="+b);
    }

}