package org.example.texteditor.controllers;

import jakarta.servlet.http.HttpSession;
import org.example.texteditor.Service.command.*;
import org.example.texteditor.Service.templatemethod.TextCaseProcessor;
import org.example.texteditor.flyweight.JavaSyntaxHighlighter;
import org.example.texteditor.model.*;
import org.example.texteditor.repo.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.example.texteditor.repo.FileRepository;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/files")
public class FileController {

    private final SnippetRepository snippetRepository;
    private final FileRepository fileRepository;
    private final MacroRepository macroRepository;
    private final CommandHistory commandHistory;
    private final BookmarkRepository bookmarkRepository;
    private final JavaSyntaxHighlighter syntaxHighlighter;
    private final UserRepository userRepository;
    private final Map<String, TextCaseProcessor> processors;


    public FileController(SnippetRepository snippetRepository,
                          FileRepository fileRepository,
                          MacroRepository macroRepository,
                          CommandHistory commandHistory,
                          BookmarkRepository bookmarkRepository,
                          JavaSyntaxHighlighter syntaxHighlighter,
                          UserRepository userRepository,
                          Map<String, TextCaseProcessor> processors) {
        this.snippetRepository = snippetRepository;
        this.fileRepository = fileRepository;
        this.macroRepository = macroRepository;
        this.commandHistory = commandHistory;
        this.bookmarkRepository = bookmarkRepository;
        this.syntaxHighlighter = syntaxHighlighter;
        this.userRepository = userRepository;
        this.processors = processors;
    }

    // --- показати файли користувача ---
    @GetMapping
    public String showFiles(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User currentUser = userRepository.findByUsername(userDetails.getUsername());

        List<File> files = fileRepository.findAll().stream()
                .filter(f -> f.getUser().equals(currentUser.getId()))
                .collect(Collectors.toList());

        model.addAttribute("files", files);
        return "files";
    }

    // --- новий файл ---
    @GetMapping("/new")
    public String newFileForm(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User currentUser = userRepository.findByUsername(userDetails.getUsername());

        File file = new File();
        file.setUser(currentUser.getId());
        file.setFilePath("uploads/");
        model.addAttribute("file", file);
        return "files-new";
    }

    @PostMapping("/new")
    public String createFile(@ModelAttribute File file, @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userRepository.findByUsername(userDetails.getUsername());

        file.setUser(currentUser.getId());
        fileRepository.save(file);
        return "redirect:/files";
    }

    // --- перегляд файлу ---
    @GetMapping("/{id}")
    public String viewFile(@PathVariable Long id, Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userRepository.findByUsername(userDetails.getUsername());

        File file = fileRepository.findById(id);
        if (file == null || !file.getUser().equals(currentUser.getId())) {
            model.addAttribute("errorMessage", "Файл не знайдено або доступ заборонено!");
            return "files";
        }
        String fileName = file.getFileName().toLowerCase();

        if (fileName.endsWith(".java")) {
            String highlightedHtml = syntaxHighlighter.highlightToHtml(file.getContent());
            model.addAttribute("highlightedContent", highlightedHtml);
        } else {
            model.addAttribute("highlightedContent", null);
        }
        model.addAttribute("file", file);
        return "file-view";
    }

    // --- редагування ---
    @GetMapping("/{id}/edit")
    public String editFile(@PathVariable Long id, HttpSession session, Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userRepository.findByUsername(userDetails.getUsername());

        File file = fileRepository.findById(id);
        if (file == null || !file.getUser().equals(currentUser.getId())) {
            model.addAttribute("errorMessage", "Файл не знайдено");
            return "files";
        }
        List<Snippet> allSnippets = snippetRepository.findAll();
        List<Snippet> userSnippets = allSnippets.stream()
                .filter(s -> s.getUserId().equals(currentUser.getId()))
                .toList();

        model.addAttribute("snippets", userSnippets);

        List<Macro> allMacros = macroRepository.findAll();
        List<Macro> userMacros = allMacros.stream()
                .filter(s -> s.getUserId().equals(currentUser.getId()))
                .toList();

        model.addAttribute("macros", userMacros);



        List<Bookmark> allBookmarks = bookmarkRepository.findByFileId(id);
        model.addAttribute("bookmarks", allBookmarks);

        String tempContent = (String) session.getAttribute("tempContent_" + id);
        if (tempContent != null) {
            model.addAttribute("fileContent", tempContent);
        } else {
            model.addAttribute("fileContent", file.getContent());
        }

        model.addAttribute("file", file);
        model.addAttribute("file", file);
        return "file-edit";
    }

    @PostMapping("/{id}/edit")
    public String updateFile(@PathVariable Long id,
                             @RequestParam String content,
                             @RequestParam String name, @AuthenticationPrincipal UserDetails userDetails ) {
        User currentUser = userRepository.findByUsername(userDetails.getUsername());

        File file = fileRepository.findById(id);
        if (file != null && file.getUser().equals(currentUser.getId())) {
            file.setFileName(name);
            file.setContent(content);
            fileRepository.save(file);
        }
        return "redirect:/files/" + id;
    }

    @PostMapping("/{id}/storage")
    @ResponseBody
    public ResponseEntity<String> saveTempContent(
            @PathVariable Long id,
            @RequestParam String content,
            HttpSession session) {

        String sessionKey = "tempContent_" + id;
        String currentContent = getSafeContent(session, sessionKey, id);
        if (currentContent.equals(content)) {
            return ResponseEntity.ok("Content not changed");
        }

        UpdateContentCommand command = new UpdateContentCommand(content);
        String newContent = commandHistory.execute(command, currentContent);
        session.setAttribute(sessionKey, newContent);
        bookmarkRepository.cleanupInvalidBookmarks(id, newContent);

        return ResponseEntity.ok("Content updated with Undo history");
    }

    // --- видалення ---
    @PostMapping("/{id}/delete")
    public String deleteFile(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userRepository.findByUsername(userDetails.getUsername());

        File file = fileRepository.findById(id);
        if (file != null && file.getUser().equals(currentUser.getId())) {
            fileRepository.delete(id);
            bookmarkRepository.deleteByFileId(id);
        }
        return "redirect:/files";
    }

    private String getSafeContent(HttpSession session, String key, Long fileId) {
        String content = (String) session.getAttribute(key);
        if (content == null) {
            File file = fileRepository.findById(fileId);
            content = (file != null && file.getContent() != null) ? file.getContent() : "";
        }
        return content;
    }
    @PostMapping("/transform")
    @ResponseBody
    public ResponseEntity<String> transformText(@RequestParam String text,
                                                @RequestParam String type) {
        TextCaseProcessor processor = processors.get(type);
        if (processor == null) {
            return ResponseEntity.badRequest().body("Невідомий тип трансформації");
        }
        String result = processor.processText(text);
        return ResponseEntity.ok(result);
    }
}
