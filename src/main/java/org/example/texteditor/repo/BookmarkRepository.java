package org.example.texteditor.repo;

import org.example.texteditor.db.BookmarkDAO;
import org.example.texteditor.model.Bookmark;

import java.util.ArrayList;
import java.util.List;

public class BookmarkRepository implements Repository<Bookmark> {

    private final BookmarkDAO bookmarkDAO;

    public BookmarkRepository(BookmarkDAO bookmarkDAO) {
        this.bookmarkDAO = bookmarkDAO;
    }

    @Override
    public List<Bookmark> findAll() {
        return new ArrayList<>();
    }
    public List<Bookmark> findByFileId(Long fileId) {
        return bookmarkDAO.getBookmarksByFileId(fileId);
    }
    @Override
    public Bookmark findById(Long id) {
        return bookmarkDAO.findById(id);
    }
    @Override
    public void save(Bookmark bookmark) {
        bookmarkDAO.save(bookmark);
    }
    @Override
    public boolean delete(Long id) {
        return bookmarkDAO.delete(id);
    }

    public void deleteByFileId(Long fileId) {
        bookmarkDAO.deleteByFileId(fileId);
    }
    public void cleanupInvalidBookmarks(Long fileId, String content) {
        if (content == null) return;
        long totalLines = content.lines().count();
        if (totalLines == 0 && !content.isEmpty()) {
            totalLines = 1;
        }
        List<Bookmark> bookmarks = bookmarkDAO.getBookmarksByFileId(fileId);

        for (Bookmark bm : bookmarks) {
            if (bm.getLineNumber() > totalLines) {
                System.out.println("🗑️ Видалення неактуальної закладки ID=" + bm.getId() + " (Рядок " + bm.getLineNumber() + ")");
                bookmarkDAO.delete(bm.getId());
            }
        }
    }
}
