package com.codewithdondamzy.onlinestore.Controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class HomeController {
    @PreAuthorize("hasRole('USER')")
    public String home() {
        return "home.html";
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/user")
    public String hello() {
        return "hello and welcome";
    }

}
