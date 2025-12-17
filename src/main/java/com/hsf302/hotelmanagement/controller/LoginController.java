package com.hsf302.hotelmanagement.controller;

import com.hsf302.hotelmanagement.entity.User;
import com.hsf302.hotelmanagement.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String showLoginPage(
            @RequestParam(required = false) String error,
            Model model) {
        if ("access_denied".equals(error)) {
            model.addAttribute("error", "Bạn không có quyền truy cập trang này. Vui lòng đăng nhập với tài khoản phù hợp.");
        }
        return "login";
    }

    @PostMapping("/login")
    public String processLogin(
            @RequestParam String username,
            @RequestParam String password,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        
        try {
            User user = userService.findByUserName(username);
            
            if (user != null && user.getPassword().equals(password)) {
                // Đăng nhập thành công - lưu user và role vào session
                session.setAttribute("user", user);
                session.setAttribute("role", user.getRole());
                
                // Điều hướng người dùng đến trang tương ứng với role của họ
                String role = user.getRole();
                if ("Admin".equals(role)) {
                    // Admin được điều hướng đến trang quản lý users
                    return "redirect:/users";
                } else if ("Manager".equals(role)) {
                    // Manager được điều hướng đến trang homeManager
                    return "redirect:/manager/homeManager";
                } else if ("Receptionist".equals(role)) {
                    // Receptionist được điều hướng đến trang check-in
                    return "redirect:/receptionist/check-in";
                } else if ("HouseKeeping Staff".equals(role)) {
                    // HouseKeeping Staff được điều hướng đến trang danh sách tasks
                    return "redirect:/tasks";
                } else {
                    // Role khác được điều hướng về trang chủ
                    return "redirect:/";
                }
            } else {
                redirectAttributes.addFlashAttribute("error", "Tên đăng nhập hoặc mật khẩu không đúng.");
                return "redirect:/login";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi đăng nhập: " + e.getMessage());
            return "redirect:/login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
