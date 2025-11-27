package org.example.texteditor.db;

import org.example.texteditor.model.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FileDAO {
    private final Connection connection;

    public FileDAO(Connection connection) {
        this.connection = connection;
    }

    public List<File> getAllFiles() {
        List<File> files = new ArrayList<>();
        String sql = "SELECT * FROM Files";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                File f = new File();
                f.setId(rs.getLong("id"));
                f.setFileName(rs.getString("fileName"));
                f.setFilePath(rs.getString("link"));
                f.setContent(rs.getString("contents"));
                f.setUser(rs.getLong("user_id"));
                f.setLastUpdate(rs.getDate("lastUpdate") != null ? rs.getDate("lastUpdate").toString() : "немає даних");
                files.add(f);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return files;
    }

    public File getFileById(Long id) {
        String sql = "SELECT * FROM Files WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                File f = new File();
                f.setId(rs.getLong("id"));
                f.setFileName(rs.getString("fileName"));
                f.setFilePath(rs.getString("link"));
                f.setContent(rs.getString("contents"));
                f.setUser(rs.getLong("user_id"));
                Date lastUpdate = rs.getDate("lastUpdate");
                f.setLastUpdate(lastUpdate != null ? lastUpdate.toString() : "немає даних");
                return f;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void saveFile(File file) {
        if (file.getId() != null && getFileById(file.getId()) != null) {
            updateFile(file);
        } else {
            insertFile(file);
        }
    }

    private void insertFile(File file) {
        String sql = "INSERT INTO Files (fileName, link, contents, lastUpdate, user_id) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, file.getFileName());
            ps.setString(2, file.getFilePath());
            ps.setString(3, file.getContent());
            ps.setDate(4, new java.sql.Date(System.currentTimeMillis()));
            ps.setLong(5, file.getUser());

            ps.executeUpdate();
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    file.setId(generatedKeys.getLong(1));
                }
            }
            System.out.println("✅ Файл успішно додано (ID: " + file.getId() + ")");

        } catch (SQLException e) {
            System.err.println("❌ Помилка INSERT: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateFile(File file) {
        String sql = "UPDATE Files SET fileName=?, link=?, contents=?, user_id=?, lastUpdate=? WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, file.getFileName());
            ps.setString(2, file.getFilePath());
            ps.setString(3, file.getContent());
            ps.setLong(4, file.getUser());
            ps.setDate(5, new java.sql.Date(System.currentTimeMillis()));
            ps.setLong(6, file.getId());
            ps.executeUpdate();
            System.out.println("♻️ Файл оновлено (id=" + file.getId() + ")");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean deleteFile(Long id) {
        String sql = "DELETE FROM Files WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
