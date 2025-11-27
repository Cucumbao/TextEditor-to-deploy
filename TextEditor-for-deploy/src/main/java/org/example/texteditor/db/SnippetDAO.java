package org.example.texteditor.db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.example.texteditor.model.Snippet;

public class SnippetDAO {
    private final Connection connection;

    public SnippetDAO(Connection connection) {
        this.connection = connection;
    }

    public List<Snippet> getAllSnippets() {
        List<Snippet> snippets = new ArrayList<>();
        String sql = "SELECT * FROM Snippets";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Snippet snippet = new Snippet(); // Створюємо новий об'єкт через безпараметричний конструктор

                // Встановлюємо значення за допомогою сеттерів
                snippet.setId(rs.getLong("id"));
                snippet.setName(rs.getString("name"));
                snippet.setContent(rs.getString("content"));
                snippet.setUserId(rs.getLong("userId"));

                snippets.add(snippet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return snippets;
    }

    public Snippet getSnippetById(Long id) {
        String sql = "SELECT * FROM Snippets WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Snippet snippet = new Snippet();

                snippet.setId(rs.getLong("id"));
                snippet.setName(rs.getString("name"));
                snippet.setContent(rs.getString("content"));
                snippet.setUserId(rs.getLong("userId"));

                return snippet;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Зберегти або оновити сніппет
    public void saveSnippet(Snippet snippet) {
        if (snippet.getId() != null && getSnippetById(snippet.getId()) != null) {
            updateSnippet(snippet);
        } else {
            insertSnippet(snippet);
        }
    }

    private void insertSnippet(Snippet snippet) {
        String sql = "INSERT INTO Snippets(name, content, userId) VALUES (?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, snippet.getName());
            ps.setString(2, snippet.getContent());
            ps.setLong(3, snippet.getUserId());
            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                snippet.setId(keys.getLong(1));
            }
            System.out.println("✅ Сніппет додано");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateSnippet(Snippet snippet) {
        String sql = "UPDATE Snippets SET name=?, content=? WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, snippet.getName());
            ps.setString(2, snippet.getContent());
            ps.setLong(3, snippet.getId());
            ps.executeUpdate();
            System.out.println("♻️ Сніппет оновлено (id=" + snippet.getId() + ")");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Видалити сніппет
    public boolean deleteSnippet(Long id) {
        String sql = "DELETE FROM Snippets WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
