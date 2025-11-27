package org.example.texteditor.db;
import org.example.texteditor.model.Macro;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MacroDAO {
    private final Connection connection;

    public MacroDAO(Connection connection) {
        this.connection = connection;
    }

    // Отримати всі макроси
    public List<Macro> getAllMacros() {
        List<Macro> macros = new ArrayList<>();
        String sql = "SELECT * FROM Macros";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Macro macro = new Macro();

                macro.setId(rs.getLong("id"));
                macro.setName(rs.getString("name"));
                macro.setCommands(rs.getString("commands"));
                macro.setUserId(rs.getLong("userId"));

                macros.add(macro);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return macros;
    }

    // Отримати макрос за id
    public Macro getMacroById(Long id) {
        String sql = "SELECT * FROM Macros WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Macro macro = new Macro();

                macro.setId(rs.getLong("id"));
                macro.setName(rs.getString("name"));
                macro.setCommands(rs.getString("commands"));
                macro.setUserId(rs.getLong("userId"));

                return macro;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Зберегти або оновити макрос
    public void saveMacro(Macro macro) {
        if (macro.getId() != null && getMacroById(macro.getId()) != null) {
            updateMacro(macro);
        } else {
            insertMacro(macro);
        }
    }

    // Додати новий макрос
    private void insertMacro(Macro macro) {
        String sql = "INSERT INTO Macros(name, commands, userId) VALUES (?, ?, ?)";

        try (PreparedStatement ps =
                     connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, macro.getName());
            ps.setString(2, macro.getCommands());
            ps.setLong(3, macro.getUserId());
            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                macro.setId(keys.getLong(1));
            }

            System.out.println("✅ Макрос додано");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Оновити існуючий
    private void updateMacro(Macro macro) {
        String sql = "UPDATE Macros SET name=?, commands=? WHERE id=?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, macro.getName());
            ps.setString(2, macro.getCommands());
            ps.setLong(3, macro.getId());
            ps.executeUpdate();

            System.out.println("♻️ Макрос оновлено (id=" + macro.getId() + ")");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Видалити макрос
    public boolean deleteMacro(Long id) {
        String sql = "DELETE FROM Macros WHERE id=?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
