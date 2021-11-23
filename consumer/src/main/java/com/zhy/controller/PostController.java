package com.zhy.controller;


import com.zhy.service.PostService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;


@Controller
public class PostController {

    @DubboReference
    private PostService postService;

    @PostMapping("/post")
    public String createOrder(String ip, String query, Integer type, Model model){
       String ans = postService.query(ip,query,type);
       model.addAttribute("msg",ans);
       return "result";
    }
    @GetMapping("/index")
    public String index(){
        return"postlist";
    }
}
