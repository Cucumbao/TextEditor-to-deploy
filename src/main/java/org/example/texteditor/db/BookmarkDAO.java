package org.example.texteditor.db;

import org.example.texteditor.model.Bookmark;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookmarkDAO {

    private final Connection connection;

    public BookmarkDAO(Connection connection) {
        this.connection = connection;
    }

    public List<Bookmark> getBookmarksByFileId(Long fileId) {
        List<Bookmark> bookmarks = new ArrayList<>();
        String sql = "SELECT * FROM Bookmarks WHERE file_id = ? ORDER BY line_number ASC";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, fileId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Bookmark bookmark = new Bookmark();
                bookmark.setId(rs.getLong("id"));
                bookmark.setLineNumber(rs.getInt("line_number"));
                bookmark.setDescription(rs.getString("description"));
                bookmark.setFileid(rs.getLong("file_id"));

                bookmarks.add(bookmark);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookmarks;
    }

    public Bookmark findById(Long id) {
        String sql = "SELECT * FROM Bookmarks WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Bookmark bookmark = new Bookmark();
                bookmark.setId(rs.getLong("id"));
                bookmark.setLineNumber(rs.getInt("line_number"));
                bookmark.setDescription(rs.getString("description"));
                bookmark.setFileid(rs.getLong("file_id"));
                return bookmark;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void save(Bookmark bookmark) {
        if (bookmark.getId() != null && findById(bookmark.getId()) != null) {
            update(bookmark);
        } else {
            insert(bookmark);
        }
    }

    private void insert(Bookmark bookmark) {
        String sql = "INSERT INTO Bookmarks(line_number, description, file_id) VALUES (?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, bookmark.getLineNumber());
            ps.setString(2, bookmark.getDescription());
            ps.setLong(3, bookmark.getFileid());

            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                bookmark.setId(keys.getLong(1));
            }
            System.out.println("✅ Закладку додано на рядок " + bookmark.getLineNumber());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void update(Bookmark bookmark) {
        String sql = "UPDATE Bookmarks SET line_number=?, description=?, file_id=? WHERE id=?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, bookmark.getLineNumber());
            ps.setString(2, bookmark.getDescription());
            ps.setLong(3, bookmark.getFileid());
            ps.setLong(4, bookmark.getId());

            ps.executeUpdate();
            System.out.println("♻️ Закладку оновлено (id=" + bookmark.getId() + ")");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean delete(Long id) {
        String sql = "DELETE FROM Bookmarks WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void deleteByFileId(Long fileId) {
        String sql = "DELETE FROM Bookmarks WHERE file_id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, fileId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
