package com.travelcurator.couponapi.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class LoginController {
        @RequestMapping("/login")
        public String login(Model model, HttpServletRequest request){
            String userId = request.getParameter("userId");
            String password = request.getParameter("password");
            System.out.println("[LoginController]userId: "+userId+" Password: "+password);

            model.addAttribute("userId", userId);
            model.addAttribute("password", password);

            return "couponIssue";
        }

}
