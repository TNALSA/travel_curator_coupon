package com.travelcurator.couponapi.controller;

import com.travelcurator.couponcore.model.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class LoginController {
        @RequestMapping("/login")
        public String login(HttpServletRequest request){
            System.out.println("loginController");
            String username = request.getParameter("username");
            String password = request.getParameter("password");


            System.out.println("[LoginController]username: "+username+" Password: "+password);
            return "couponIssue.html";
        }

}
