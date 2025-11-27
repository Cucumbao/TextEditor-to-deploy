package org.example.texteditor.repo;

import org.example.texteditor.db.MacroDAO;
import org.example.texteditor.model.Macro;
import java.util.List;

public class MacroRepository implements Repository<Macro> {
    private final MacroDAO macroDAO;

    public MacroRepository(MacroDAO macroDAO) {
        this.macroDAO = macroDAO;
    }

    @Override
    public List<Macro> findAll() {
        List<Macro> macros = macroDAO.getAllMacros();
        System.out.println(macros);
        return macros;
    }

    @Override
    public Macro findById(Long id) {
        return macroDAO.getMacroById(id);
    }

    @Override
    public void save(Macro macro) {
        macroDAO.saveMacro(macro);
    }

    @Override
    public boolean delete(Long id) {
        return macroDAO.deleteMacro(id);
    }
}
