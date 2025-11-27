package org.example.texteditor.controllers;

import jakarta.servlet.http.HttpSession;
import org.example.texteditor.model.Bookmark;
import org.example.texteditor.model.User;
import org.example.texteditor.repo.BookmarkRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/bookmarks")
public class BookmarkController {
    private final BookmarkRepository bookmarkRepository;

    public BookmarkController(BookmarkRepository bookmarkRepository) {
        this.bookmarkRepository = bookmarkRepository;
    }

    @PostMapping("/create")
    public String createBookmark(@RequestParam int lineNumber,
                                 @RequestParam String description,
                                 @RequestParam Long fileId,
                                 RedirectAttributes redirectAttributes) {

        Bookmark bookmark = new Bookmark();
        bookmark.setLineNumber(lineNumber);
        bookmark.setDescription(description);
        bookmark.setFileid(fileId);

        bookmarkRepository.save(bookmark);
        redirectAttributes.addFlashAttribute("successMessage", "Закладку додано!");

        return "redirect:/files/" + fileId + "/edit";
    }

    @PostMapping("/{id}/update")
    public String updateBookmark(@PathVariable Long id,
                                 @RequestParam int lineNumber,
                                 @RequestParam String description,
                                 @RequestParam Long fileId,
                                 RedirectAttributes redirectAttributes) {

        Bookmark bookmark = bookmarkRepository.findById(id);
        if (bookmark != null) {
            bookmark.setLineNumber(lineNumber);
            bookmark.setDescription(description);
            bookmarkRepository.save(bookmark);
        }

        redirectAttributes.addFlashAttribute("successMessage", "Закладку оновлено!");
        return "redirect:/files/" + fileId + "/edit";
    }

    @PostMapping("/{id}/delete")
    public String deleteBookmark(@PathVariable Long id,
                                 @RequestParam Long fileId,
                                 RedirectAttributes redirectAttributes) {
        bookmarkRepository.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Закладку видалено!");

        return "redirect:/files/" + fileId + "/edit";
    }
}
