package org.example.texteditor.controllers;

import jakarta.servlet.http.HttpSession;
import org.example.texteditor.Service.*;
import org.example.texteditor.Service.command.CommandHistory;
import org.example.texteditor.model.*;
import org.example.texteditor.repo.FileRepository;
import org.example.texteditor.repo.UserRepository;
import org.example.texteditor.strategy.*;
import org.springframework.core.io.*;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@Controller
public class DefaultController {
    private final CommandHistory commandHistory;
    private final DocxService docxService;
    private final EncodingService encodingService;
    private final FileRepository fileRepository;
    private final UserRepository userRepository;

    public DefaultController(CommandHistory commandHistory,
                             DocxService docxService,
                             EncodingService encodingService,
                             FileRepository fileRepository,
                             UserRepository userRepository) {
        this.commandHistory = commandHistory;
        this.docxService = docxService;
        this.encodingService = encodingService;
        this.fileRepository = fileRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("files/open_from")
    public String openFromComputer(@RequestParam("file") MultipartFile uploadedFile,
                                   @AuthenticationPrincipal UserDetails userDetails, Model model) throws IOException {
        User currentUser = userRepository.findByUsername(userDetails.getUsername());

        if (uploadedFile.isEmpty()) {
            model.addAttribute("error", "Файл не вибрано");
            return "files";
        }

        String content;

        String originalName = uploadedFile.getOriginalFilename();

        if (originalName != null && originalName.endsWith(".docx")) {
            content = docxService.readDocx(uploadedFile.getInputStream());
            System.out.println("📂 Відкрито Word документ: " + originalName);

        } else {
            byte[] bytes = uploadedFile.getBytes();
            String detectedEncoding = encodingService.detectEncoding(bytes);
            content = new String(bytes, Charset.forName(detectedEncoding));
            System.out.println("📂 Відкрито текстовий файл (" + detectedEncoding + "): " + originalName);
        }


        String filePath = "uploads/" + originalName;
        File file = new File();
        file.setFileName(originalName);
        file.setContent(content);
        file.setFilePath(filePath);
        file.setUser(currentUser.getId());
        file.setLastUpdate("");

        fileRepository.save(file);

        return "redirect:/files";
    }
    @GetMapping("files/{id}/download")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id, HttpSession session,@AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userRepository.findByUsername(userDetails.getUsername());
        if (currentUser == null) {
            return ResponseEntity.status(401).build();
        }
        String sessionKey = "tempContent_" + id;
        String content = getSafeContent(session, sessionKey, id);

        File fileEntity = fileRepository.findById(id);
        if (fileEntity == null) return ResponseEntity.notFound().build();

        fileEntity.setContent(content);
        String fileName = fileEntity.getFileName();
        String lowerName = fileName.toLowerCase();

        SaveStrategy strategy;
        MediaType mediaType;

        if (lowerName.endsWith(".json")) {
            strategy = new SaveAsJson();
            mediaType = MediaType.APPLICATION_JSON;
        } else if (lowerName.endsWith(".xml")) {
            strategy = new SaveAsXml();
            mediaType = MediaType.APPLICATION_XML;
        } else {
            strategy = new SaveAsTxt();
            mediaType = MediaType.TEXT_PLAIN;
        }
        String formattedContent = strategy.formatContent(fileEntity);
        byte[] data = formattedContent.getBytes(StandardCharsets.UTF_8);
        ByteArrayResource resource = new ByteArrayResource(data);

        ContentDisposition contentDisposition = ContentDisposition.builder("attachment")
                .filename(fileName, StandardCharsets.UTF_8)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
                .contentType(mediaType)
                .contentLength(data.length)
                .body(resource);
    }

    @PostMapping("files/{id}/undo")
    public String undo(@PathVariable Long id, HttpSession session) {
        String sessionKey = "tempContent_" + id;
        String currentContent = getSafeContent(session, sessionKey, id);
        String newContent = commandHistory.undo(currentContent);
        session.setAttribute(sessionKey, newContent);

        return "redirect:/files/" + id + "/edit";
    }

    @PostMapping("files/{id}/redo")
    public String redo(@PathVariable Long id, HttpSession session) {
        String sessionKey = "tempContent_" + id;
        String currentContent = getSafeContent(session, sessionKey, id);
        String newContent = commandHistory.redo(currentContent);
        session.setAttribute(sessionKey, newContent);

        return "redirect:/files/" + id + "/edit";
    }

    private String getSafeContent(HttpSession session, String key, Long fileId) {
        String content = (String) session.getAttribute(key);
        if (content == null) {
            File file = fileRepository.findById(fileId);
            content = (file != null && file.getContent() != null) ? file.getContent() : "";
        }
        return content;
    }
}
