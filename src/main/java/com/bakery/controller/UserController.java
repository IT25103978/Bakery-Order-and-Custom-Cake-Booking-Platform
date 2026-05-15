package com.bakery.controller;

import jakarta.servlet.http.HttpSession;
import com.bakery.model.User;
import com.bakery.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.bakery.service.UserService;
import com.bakery.repository.UserRepository;
import com.bakery.model.User;

// ─────────────────────────────────────────────────────────────────
//  OOP Concept → ABSTRACTION (via Service layer)
//  The controller no longer talks to the database directly.
//  It only calls UserService methods and handles HTTP responses.
//
//  Before:  Controller → Repository → Database
//  After:   Controller → Service → Repository → Database
//
//  This is cleaner and matches real-world Spring Boot architecture.
// ─────────────────────────────────────────────────────────────────

@Controller
public class UserController {

    @Autowired
    private UserService userService;          // ← service, NOT repository

    @Autowired
    private UserRepository userRepository;    // ← only used for admin list

    
    //  LOGIN
   

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String handleLogin(@RequestParam String username,
                              @RequestParam String password,
                              HttpSession session,
                              Model model) {

        // Ask the SERVICE to check credentials and return the role
        String result = userService.login(username, password);

        if (UserService.RESULT_INVALID.equals(result)) {
            model.addAttribute("error", "Invalid username or password!");
            return "login";
        }

        // Valid login — get full user object and store in session
        User user = userService.getUserByUsername(username);
        session.setAttribute("loggedInUser", user.getUsername());
        session.setAttribute("userRole",     user.getRole());
        session.setAttribute("userId",       user.getId());
        session.setAttribute("fullName",
                user.getFirstName() + " " + user.getLastName());

        // ── Role-based redirect ──────────────────────────────────
        if (UserService.RESULT_ADMIN.equals(result)) {
            return "redirect:/admin/dashboard";   // → admin page
        } else {
            return "redirect:/products";           // → customer page
        }
    }

   
    //  REGISTER
    

    @GetMapping("/register")
    public String showRegisterPage() {
        return "register";
    }

    @PostMapping("/register")
    public String handleRegister(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String email,
            @RequestParam(defaultValue = "") String telephone,
            @RequestParam(defaultValue = "") String firstName,
            @RequestParam(defaultValue = "") String lastName,
            Model model) {

        // Ask the SERVICE to validate and save the new user
        String error = userService.register(
                username, password, email, telephone, firstName, lastName
        );

        if (error != null) {
            // Service returned an error message — show it on the form
            model.addAttribute("error", error);
            return "register";
        }

        // Registration successful
        model.addAttribute("success", "Registration successful! Please login.");
        return "login";
    }

    //  DASHBOARD (customer profile)

    @GetMapping("/dashboard")
    public String showDashboard(HttpSession session, Model model) {
        if (session.getAttribute("loggedInUser") == null) {
            return "redirect:/login";
        }
        Long id = (Long) session.getAttribute("userId");
        User user = userRepository.findById(id).orElse(null);
        model.addAttribute("user", user);
        return "dashboard";
    }

    //  UPDATE profile

    @PostMapping("/update")
    public String handleUpdate(
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam(defaultValue = "") String telephone,
            @RequestParam(defaultValue = "") String firstName,
            @RequestParam(defaultValue = "") String lastName,
            HttpSession session,
            Model model) {

        if (session.getAttribute("loggedInUser") == null) {
            return "redirect:/login";
        }

        Long id = (Long) session.getAttribute("userId");

        // Ask the SERVICE to update
        String error = userService.updateUser(
                id, email, password, telephone, firstName, lastName
        );

        // Refresh user object to show updated values
        User user = userRepository.findById(id).orElse(null);
        model.addAttribute("user", user);

        if (error != null) {
            model.addAttribute("error", error);
        } else {
            // Update full name in session too
            if (user != null) {
                session.setAttribute("fullName",
                        user.getFirstName() + " " + user.getLastName());
            }
            model.addAttribute("success", "Profile updated successfully!");
        }
        return "dashboard";
    }


    //  DELETE account


    @PostMapping("/delete")
    public String handleDelete(HttpSession session) {
        if (session.getAttribute("loggedInUser") == null) {
            return "redirect:/login";
        }
        Long id = (Long) session.getAttribute("userId");
        userService.deleteUser(id);
        session.invalidate();
        return "redirect:/login?deleted=true";
    }


    //  LOGOUT


    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }


    //  ADMIN DASHBOARD


    @GetMapping("/admin/dashboard")
    public String adminDashboard(HttpSession session, Model model) {
        if (!"ADMIN".equals(session.getAttribute("userRole"))) {
            return "redirect:/login";   // block non-admins
        }
        model.addAttribute("users",    userRepository.findAll());
        model.addAttribute("fullName", session.getAttribute("fullName"));
        return "admin-dashboard";
    }

    // Admin: delete any user by id
    @PostMapping("/admin/delete-user")
    public String adminDeleteUser(@RequestParam Long id, HttpSession session) {
        if (!"ADMIN".equals(session.getAttribute("userRole"))) {
            return "redirect:/login";
        }
        userService.deleteUser(id);
        return "redirect:/admin/dashboard?deleted=true";
    }

    //  PRODUCTS PAGE (customer landing after login)


    @GetMapping("/products")
    public String productsPage(HttpSession session, Model model) {
        if (session.getAttribute("loggedInUser") == null) {
            return "redirect:/login";
        }
        model.addAttribute("fullName", session.getAttribute("fullName"));
        return "products";
    }
}