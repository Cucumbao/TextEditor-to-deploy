package org.example.texteditor.Service;

import org.example.texteditor.model.Macro;
import org.example.texteditor.repo.MacroRepository;
import org.codehaus.commons.compiler.CompileException;
import org.codehaus.janino.ExpressionEvaluator;
import org.codehaus.janino.ScriptEvaluator;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import java.lang.reflect.InvocationTargetException;
@Service
public class MacrosExecutor {

    private final MacroRepository macroRepo;

    public MacrosExecutor(MacroRepository macroRepo) {
        this.macroRepo = macroRepo;
    }

    public String executeAndReturnResult(Long id) throws CompileException, InvocationTargetException {
        Macro macro = macroRepo.findById(id);
        if (macro == null) return "";

        String command = macro.getCommands();
        if (command == null || command.isBlank()) return "";

        command = command.trim();
        if (command.startsWith("\"") && command.endsWith("\"")) {
            command = command.substring(1, command.length() - 1);
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(baos));

        Object result = null;

        try {
            if (command.contains(";")) {
                ScriptEvaluator se = new ScriptEvaluator();
                se.setDefaultImports(new String[]{"java.time.*", "java.util.*", "java.math.*"});
                se.cook(command);
                result = se.evaluate(null);
            } else {
                ExpressionEvaluator ee = new ExpressionEvaluator();
                ee.setExpressionType(Object.class);
                ee.setDefaultImports(new String[]{"java.time.*", "java.util.*", "java.math.*"});
                ee.cook(command);
                result = ee.evaluate(null);
            }
        } finally {
            System.setOut(originalOut);
        }

        String output = baos.toString().trim();

        if (!output.isEmpty()) {
            return output;
        } else if (result != null) {
            return result.toString();
        } else {
            return "";
        }
    }
}
