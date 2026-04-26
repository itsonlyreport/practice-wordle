package com.wordle.admin;

import com.wordle.mapper.UserMapper;
import com.wordle.model.User;
import com.wordle.user.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {

    @Autowired
    private UserMapper  userMapper;

    @Autowired
    private UserService userService;

    // 유저 목록
    @GetMapping
    public String list(Model model) {
        model.addAttribute("users", userMapper.findAll());
        return "users";
    }

    // 권한 변경 (USER ↔ ADMIN)
    @PostMapping("/role")
    public String updateRole(@RequestParam Long id,
                             @RequestParam String role,
                             HttpSession session) {

        // 자기 자신 권한 변경 방지
        var loginUser = (User) session.getAttribute("loginUser");
        if (loginUser.getId().equals(id)) {
            return "redirect:/admin/users";
        }

        var user = User.builder()
                .id(id)
                .role(role)
                .build();
        userMapper.updateUserRole(user);
        return "redirect:/admin/users";
    }

    // 유저 삭제
    @PostMapping("/delete")
    public String delete(@RequestParam Long id,
                         HttpSession session) {

        // 자기 자신 삭제 방지
        var loginUser = (User) session.getAttribute("loginUser");
        if (loginUser.getId().equals(id)) {
            return "redirect:/admin/users";
        }

        userMapper.deleteUser(id);
        return "redirect:/admin/users";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam Long id,
                                HttpSession session,
                                Model model) {
        var loginUser = (User) session.getAttribute("loginUser");

        try {
            var tempPassword = userService.resetPassword(loginUser.getId(), id);
            model.addAttribute("tempPassword", tempPassword);
            model.addAttribute("resetUserId",  id);
        } catch (IllegalArgumentException | IllegalStateException e) {
            model.addAttribute("errorMsg", e.getMessage());
        }

        model.addAttribute("users", userMapper.findAll());
        return "users";
    }
}