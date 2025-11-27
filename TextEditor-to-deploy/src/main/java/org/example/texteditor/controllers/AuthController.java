package org.example.texteditor.controllers;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.texteditor.config.JwtUtil;
import org.example.texteditor.model.User;
import org.example.texteditor.repo.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthController(UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    // --- Сторінка логіну ---
    @GetMapping("/")
    public String loginPage(Model model) {
        model.addAttribute("user", new User(null, "", "", ""));
        return "login";
    }

    // --- Логіка входу (Login) ---
    @PostMapping("/login")
    public String loginUser(@RequestParam String username,
                            @RequestParam String password,
                            HttpServletResponse response, // Щоб записати Куку
                            Model model) {


        User user = userRepository.findByUsername(username);


        if (user != null && passwordEncoder.matches(password, user.getPassword())) {


            String token = jwtUtil.generateToken(username);


            Cookie cookie = new Cookie("JWT_TOKEN", token);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(10 * 60 * 60);

            response.addCookie(cookie);

            return "redirect:/files";
        } else {
            model.addAttribute("errorMessage", "Невірний username або пароль");
            return "login";
        }
    }

    // --- Сторінка реєстрації ---
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User(null, "", "", ""));
        return "register";
    }

    // --- Логіка реєстрації ---
    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user, Model model) {
        // Перевірка, чи такий юзер вже є
        if (userRepository.findByUsername(user.getUsername()) != null) {
            model.addAttribute("errorMessage", "Користувач з таким ім'ям вже існує!");
            return "register";
        }

        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);


        userRepository.save(user);

        model.addAttribute("message", "Користувача зареєстровано! Увійдіть.");
        return "login";
    }

    // --- Логіка виходу (Logout) ---
    @GetMapping("/logout")
    public String logout(HttpServletResponse response, HttpSession session) {

        session.invalidate();

        Cookie cookie = new Cookie("JWT_TOKEN", null);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        return "redirect:/";
    }
}
