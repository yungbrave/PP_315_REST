package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.security.UserDetailsImpl;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;



@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;

    private final RoleService roleService;

    @Autowired
    public AdminController(UserService userService, @Lazy RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping()
    public String showUsers(Model model, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        model.addAttribute("roles", roleService.findAll()); //все роли из БД
        model.addAttribute("users", userService.findAll()); //все юзеры из БД
        model.addAttribute("user", userDetails);
        model.addAttribute("emptyUser", new User());
        System.out.println("test");
        return "admin/all-users";
    }

    @GetMapping("/new")
    public String newUser(@ModelAttribute("userNew") User user,
                          @AuthenticationPrincipal UserDetailsImpl userDetails,
                          Model model) {
        model.addAttribute("user", userDetails);
        model.addAttribute("roles", roleService.findAll());
        return "admin/new";
    }

    @PostMapping
    public String create(@ModelAttribute("user") User user) {
        userService.save(user);
        return "redirect:/admin";
    }


    @GetMapping("/{id}/edit")
    public String edit(@PathVariable("id") long id,
                       Model model) {
        model.addAttribute("user", userService.findOne(id));
        model.addAttribute("roles", roleService.findAll());
        return "admin/edit";
    }

    @PatchMapping("/{id}")
    public String update(@ModelAttribute("user") User updatedUser,
                         @PathVariable("id") long id) {
        System.out.println("test");
        userService.update(id, updatedUser);
        return "redirect:/admin";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") long id) {
        userService.delete(id);
        return "redirect:/admin";
    }

}
