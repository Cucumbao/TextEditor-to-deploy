package org.example.texteditor.controllers;

import jakarta.servlet.http.HttpSession;
import org.example.texteditor.Service.MacrosExecutor;
import org.example.texteditor.Service.command.CommandHistory;
import org.example.texteditor.Service.command.InsertMacroCommand;
import org.example.texteditor.model.File;
import org.example.texteditor.model.Macro;
import org.example.texteditor.model.User;
import org.example.texteditor.repo.FileRepository;
import org.example.texteditor.repo.MacroRepository;


import org.example.texteditor.repo.UserRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/macros")
public class MacroController {

    private final MacroRepository macroRepository;
    private final CommandHistory commandHistory;
    private final FileRepository fileRepository;
    private final UserRepository userRepository;


    public MacroController(MacroRepository macroRepository, CommandHistory commandHistory, FileRepository fileRepository, UserRepository userRepository) {
        this.macroRepository = macroRepository;
        this.commandHistory = commandHistory;
        this.fileRepository = fileRepository;
        this.userRepository = userRepository;
    }
    @PostMapping("/{id}/insert")
    public String insertMacro(@PathVariable Long id,
                              @RequestParam Long fileId,
                              @RequestParam(required = false) Integer cursorPosition,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        String sessionKey = "tempContent_" + fileId;
        String currentContent = getSafeContent(session, sessionKey, fileId);

        try {
            InsertMacroCommand command = new InsertMacroCommand(id, cursorPosition);
            String newContent = commandHistory.execute(command, currentContent);
            session.setAttribute(sessionKey, newContent);
            redirectAttributes.addFlashAttribute("successMessage", "Макрос виконано успішно!");

        } catch (RuntimeException e) {
            e.printStackTrace();
            String errorMsg = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
            redirectAttributes.addFlashAttribute("errorMessage", "Помилка: " + errorMsg);
        }

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

    @PostMapping("/create")
    public String createMacro(@RequestParam String name,
                              @RequestParam String commands,
                              @RequestParam Long fileId,
                              @AuthenticationPrincipal UserDetails userDetails,
                              RedirectAttributes redirectAttributes) {

        User currentUser = userRepository.findByUsername(userDetails.getUsername());

        Macro newMacro = new Macro();
        newMacro.setName(name);
        newMacro.setCommands(commands);
        newMacro.setUserId(currentUser.getId());

        macroRepository.save(newMacro);
        redirectAttributes.addFlashAttribute("successMessage", "Макрос успішно створено!");

        return "redirect:/files/" + fileId + "/edit";
    }

    @PostMapping("/{id}/update")
    public String updateMacro(@PathVariable Long id,
                              @RequestParam String name,
                              @RequestParam String commands,
                              @RequestParam Long fileId,
                              @AuthenticationPrincipal UserDetails userDetails,
                              RedirectAttributes redirectAttributes) {

        User currentUser = userRepository.findByUsername(userDetails.getUsername());

        Macro macro = macroRepository.findById(id);
        if (macro == null || !macro.getUserId().equals(currentUser.getId())) {
            return "redirect:/files/" + fileId + "/edit";
        }

        macro.setName(name);
        macro.setCommands(commands);
        macroRepository.save(macro);

        redirectAttributes.addFlashAttribute("successMessage", "Макрос оновлено!");
        return "redirect:/files/" + fileId + "/edit";
    }

    @PostMapping("/{id}/delete")
    public String deleteMacro(@PathVariable Long id,
                              @RequestParam Long fileId,
                              RedirectAttributes redirectAttributes,  @AuthenticationPrincipal UserDetails userDetails) {

        User currentUser = userRepository.findByUsername(userDetails.getUsername());

        Macro macro = macroRepository.findById(id);
        if (macro != null && macro.getUserId().equals(currentUser.getId())) {
            macroRepository.delete(id);
        }

        redirectAttributes.addFlashAttribute("successMessage", "Макрос успішно видалено!");
        return "redirect:/files/" + fileId + "/edit";
    }
}

