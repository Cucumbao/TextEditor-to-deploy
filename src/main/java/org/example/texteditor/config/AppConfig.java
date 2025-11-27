package org.example.texteditor.config;

import org.example.texteditor.db.*;
import org.example.texteditor.repo.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.Connection;

@Configuration
public class AppConfig {

    @Bean
    public DatabaseConnection databaseConnection() {
        return new DatabaseConnection();
    }

    @Bean
    public FileDAO fileDAO(DatabaseConnection databaseConnection) {
        return new FileDAO(databaseConnection.getConnection());
    }

    @Bean
    public FileRepository fileRepository(FileDAO fileDAO) {
        return new FileRepository(fileDAO);
    }

    @Bean
    public UserDAO userDAO(DatabaseConnection databaseConnection) {
        return new UserDAO(databaseConnection.getConnection());
    }

    @Bean
    public UserRepository userRepository(UserDAO userDAO) {
        return new UserRepository(userDAO);
    }
    @Bean
    public SnippetDAO snippetDAO(DatabaseConnection databaseConnection) {
        return new SnippetDAO(databaseConnection.getConnection());
    }
    @Bean
    public SnippetRepository snippetRepository(SnippetDAO snippetDAO) {
        return new SnippetRepository(snippetDAO);
    }
    @Bean
    public MacroDAO MacroDAO(DatabaseConnection databaseConnection) {
        return new MacroDAO(databaseConnection.getConnection());
    }
    @Bean
    public MacroRepository MacroRepository(MacroDAO macroDAO) {
        return new MacroRepository(macroDAO);
    }
    @Bean
    public BookmarkDAO bookmarkDAO(DatabaseConnection databaseConnection) {
        return new BookmarkDAO(databaseConnection.getConnection());
    }

    @Bean
    public BookmarkRepository bookmarkRepository(BookmarkDAO bookmarkDAO) {
        return new BookmarkRepository(bookmarkDAO);
    }
}
