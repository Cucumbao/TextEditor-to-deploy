package org.example.texteditor.repo;

import org.example.texteditor.db.SnippetDAO;
import org.example.texteditor.model.Snippet;

import java.util.List;

public class SnippetRepository implements Repository<Snippet> {
    private final SnippetDAO snippetDAO;

    public SnippetRepository(SnippetDAO snippetDAO) {
        this.snippetDAO = snippetDAO;
    }

    public List<Snippet> findAll() {
        List<Snippet> snippets = snippetDAO.getAllSnippets();
        System.out.println(snippets);
        return snippets;
    }

    public Snippet findById(Long id) {
        return snippetDAO.getSnippetById(id);
    }

    public void save(Snippet snippet) {
        snippetDAO.saveSnippet(snippet);
    }

    public boolean delete(Long id) {
        return snippetDAO.deleteSnippet(id);
    }
}
