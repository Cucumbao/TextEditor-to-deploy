package org.example.texteditor.repo;

import org.example.texteditor.db.UserDAO;
import org.example.texteditor.model.User;
import java.util.List;

public class UserRepository implements Repository<User> {
    private final UserDAO userDAO;

    public UserRepository(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    public List<User> findAll() {
        return userDAO.getAllUsers();
    }

    @Override
    public User findById(Long id) {
        User user = userDAO.getUserById(id);
        if (user != null) {
            System.out.println(user);
        } else {
            System.out.println("❌ Користувача з id=" + id + " не знайдено.");
        }
        return user;
    }
    @Override
    public void save(User user) {
        userDAO.saveUser(user);
        System.out.println("💾 Користувача збережено або оновлено.");
    }
    @Override
    public boolean delete(Long id) {
        boolean deleted = userDAO.deleteUser(id);
        if (deleted) {
            System.out.println("🗑️ Користувача з id=" + id + " видалено.");
        } else {
            System.out.println("❌ Користувача з id=" + id + " не знайдено.");
        }
        return deleted;
    }
    public User findByUsername(String username) {
        return userDAO.getUserByUsername(username);
    }
}

