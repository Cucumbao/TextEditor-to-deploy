package org.example.texteditor.controllers;

import jakarta.servlet.http.HttpSession;
import org.example.texteditor.Service.command.CommandHistory;
import org.example.texteditor.Service.command.InsertSnippetCommand;
import org.example.texteditor.model.File;
import org.example.texteditor.model.Snippet;
import org.example.texteditor.model.User;
import org.example.texteditor.repo.FileRepository;
import org.example.texteditor.repo.SnippetRepository;
import org.example.texteditor.repo.UserRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("/snippets")
public class SnippetController {

    private final SnippetRepository snippetRepository;
    private final CommandHistory commandHistory;
    private final FileRepository fileRepository;
    private final UserRepository userRepository;


    public SnippetController(SnippetRepository snippetRepository,
                             CommandHistory commandHistory,
                             FileRepository fileRepository,
                             UserRepository userRepository) {
        this.snippetRepository = snippetRepository;
        this.commandHistory = commandHistory;
        this.fileRepository = fileRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/create")
    public String createSnippet(@RequestParam String name,
                                @RequestParam String content,
                                @RequestParam Long fileId,
                                @AuthenticationPrincipal UserDetails userDetails,
                                RedirectAttributes redirectAttributes) {

        User currentUser = userRepository.findByUsername(userDetails.getUsername());
        Snippet newSnippet = new Snippet();
        newSnippet.setName(name);
        newSnippet.setContent(content);
        newSnippet.setUserId(currentUser.getId());

        snippetRepository.save(newSnippet);

        redirectAttributes.addFlashAttribute("successMessage", "Сніпет успішно створено!");

        return "redirect:/files/" + fileId + "/edit";
    }
    @PostMapping("/{id}/update")
    public String updateSnippet(@PathVariable Long id,
                                @RequestParam String name,
                                @RequestParam String content,
                                @RequestParam Long fileId,
                                @AuthenticationPrincipal UserDetails userDetails,
                                RedirectAttributes redirectAttributes) {

        User currentUser = userRepository.findByUsername(userDetails.getUsername());

        Snippet snippet = snippetRepository.findById(id);
        if (snippet == null || !snippet.getUserId().equals(currentUser.getId())) {
            return "redirect:/files/" + fileId + "/edit";
        }

        snippet.setName(name);
        snippet.setContent(content);
        snippetRepository.save(snippet);

        redirectAttributes.addFlashAttribute("successMessage", "Сніпет оновлено!");
        return "redirect:/files/" + fileId + "/edit"; // ← тут редірект як у create
    }
    @PostMapping("/{id}/insert")
    public String insertSnippet(@PathVariable Long id,
                                @RequestParam Long fileId,
                                @RequestParam(required = false) Integer cursorPosition,
                                HttpSession session,
                                @AuthenticationPrincipal UserDetails userDetails,
                                RedirectAttributes redirectAttributes) {

        User currentUser = userRepository.findByUsername(userDetails.getUsername());

        Snippet snippet = snippetRepository.findById(id);
        if (snippet == null || !snippet.getUserId().equals(currentUser.getId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Сніпет не знайдено");
            return "redirect:/files/" + fileId + "/edit";
        }
        String sessionKey = "tempContent_" + fileId;
        String currentContent = getSafeContent(session, sessionKey, fileId);

        InsertSnippetCommand command = new InsertSnippetCommand(snippet.getContent(), cursorPosition);
        String newContent = commandHistory.execute(command, currentContent);
        session.setAttribute(sessionKey, newContent);

        redirectAttributes.addFlashAttribute("successMessage", "Сніпет вставлено!");
        return "redirect:/files/" + fileId + "/edit";
    }
    private String getSafeContent(HttpSession session, String key, Long fileId) {
        String content = (String) session.getAttribute(key);
        if (content == null) {
            File file = fileRepository.findById(fileId);
            content = (file != null && file.getContent() != null) ? file.getContent() : "";
        }
        return content;
    }


    @PostMapping("/{id}/delete")
    public String deleteSnippet(@PathVariable Long id,
                                @RequestParam Long fileId,
                                @AuthenticationPrincipal UserDetails userDetails,
                                RedirectAttributes redirectAttributes) {

        User currentUser = userRepository.findByUsername(userDetails.getUsername());
        Snippet snippet_delete = snippetRepository.findById(id);
        if (snippet_delete != null && snippet_delete.getUserId().equals(currentUser.getId())) {
            snippetRepository.delete(id);
        }
        redirectAttributes.addFlashAttribute("successMessage", "Сніпет успішно видалено!");
        return "redirect:/files/" + fileId + "/edit";
    }
}
