package com.hsf302.hotelmanagement.controller;

import com.hsf302.hotelmanagement.entity.User;
import com.hsf302.hotelmanagement.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public String listUsers(@RequestParam(name = "name", required = false) String name,
                            @RequestParam(name = "role", required = false) String role,
                            @RequestParam(name = "page", defaultValue = "0") int page,
                            Model model) {
        Pageable pageable = PageRequest.of(page, 10);
        Page<User> usersPage = userService.searchUsersPage(name, role, pageable);

        model.addAttribute("users", usersPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", usersPage.getTotalPages());
        model.addAttribute("totalItems", usersPage.getTotalElements());
        model.addAttribute("searchName", name != null ? name : "");
        model.addAttribute("searchRole", role != null ? role : "");
        model.addAttribute("view", "user-list");

        // Return the main layout
        return "dashboard-layout";
    }

    @GetMapping("/add")
    public String showAddUserForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("view", "user-form");

        // Return the main layout
        return "dashboard-layout";
    }

    @PostMapping
    public String saveUser(@Valid @ModelAttribute("user") User user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "user-form";
        }
        try {
            userService.save(user);
        } catch (Exception e) {
            bindingResult.rejectValue("userName", "error.user", "An account already exists for this username.");
            return "user-form";
        }
        return "redirect:/users";
    }

    @GetMapping("/edit/{id}")
    public String showEditUserForm(@PathVariable("id") int id, Model model) {
        User user = userService.findById(id);
        model.addAttribute("user", user);
        return "user-form";
    }

    @PostMapping("/edit")
    public String updateUser(@Valid @ModelAttribute("user") User user, BindingResult bindingResult) {
        if (bindingResult.hasFieldErrors("userName") || bindingResult.hasFieldErrors("role")) {
            return "user-form";
        }
        try {
            userService.update(user);
        } catch (Exception e) {
            bindingResult.rejectValue("userName", "error.user", "An account already exists for this username.");
            return "user-form";
        }
        return "redirect:/users";
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") int id) {
        userService.deleteById(id);
        return "redirect:/users";
    }
}
