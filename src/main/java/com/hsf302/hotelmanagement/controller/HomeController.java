package com.hsf302.hotelmanagement.controller;

import com.hsf302.hotelmanagement.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping({"/", "/home", "/index"})
    public String home(HttpServletRequest request, Model model) {

        HttpSession session = request.getSession(false);
        
        User user = null;
        if (session != null) {
            user = (User) session.getAttribute("user");
        }
        
        model.addAttribute("user", user);
        return "home";
    }
}

