package org.example.texteditor.db;
import org.example.texteditor.model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    private final Connection connection;

    public UserDAO(Connection connection) {
        this.connection = connection;
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM Users";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                User user = new User(
                        rs.getLong("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("email")
                );
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }
    public User getUserByUsername(String username) {
        String sql = "SELECT * FROM Users WHERE username = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, username);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getLong("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("email")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Отримати користувача за id
    public User getUserById(Long id) {
        String sql = "SELECT * FROM Users WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getLong("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("email")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Зберегти або оновити користувача
    public void saveUser(User user) {
        if (user.getId() != null && getUserById(user.getId()) != null) {
            updateUser(user);
        } else {
            insertUser(user);
        }
    }

    private void insertUser(User user) {
        String sql = "INSERT INTO Users(username, email, password) VALUES (?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                user.setId(keys.getLong(1));
            }
            System.out.println("✅ Користувача додано");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateUser(User user) {
        String sql = "UPDATE Users SET username=?, email=?, password=? WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.setLong(4, user.getId());
            ps.executeUpdate();
            System.out.println("♻️ Користувача оновлено (id=" + user.getId() + ")");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean deleteUser(Long id) {
        String sql = "DELETE FROM Users WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
