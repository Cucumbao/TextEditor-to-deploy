package org.example.texteditor.controllers;
import org.example.texteditor.model.User;
import org.example.texteditor.repo.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelConfig {

    private final UserRepository userRepository;

    public GlobalModelConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @ModelAttribute("currentUser")
    public User populateUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            String username = auth.getName();
            return userRepository.findByUsername(username);
        }
        return null;
    }
}
