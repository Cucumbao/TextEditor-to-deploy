package org.example.texteditor.flyweight;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;

@Service
public class JavaSyntaxHighlighter {

    private final FlyweightFactory factory;

    public JavaSyntaxHighlighter(FlyweightFactory factory) {
        this.factory = factory;
    }

    public String highlightToHtml(String code) {
        if (code == null || code.isEmpty()) return "";

        StringBuilder html = new StringBuilder();

        String regex = "\"([^\"]*)\"|//[^\n]*|/\\*.*?\\*/|\\b(if|for|while|switch|public|class|void|static|return|new|int|boolean|String)\\b|\\d+|\\s+|.";

        Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(code);

        while (matcher.find()) {
            String token = matcher.group();
            String type = "DEFAULT";
            if (token.matches("\\b(if|for|while|switch|public|class|void|static|return|new|int|boolean|String)\\b")) {
                type = "KEYWORD";
            } else if (token.matches("\\d+")) {
                type = "NUMBER";
            } else if (token.startsWith("\"")) {
                type = "STRING";
            } else if (token.startsWith("//") || token.startsWith("/*")) {
                type = "COMMENT";
            }
            Flyweight flyweight = factory.getFlyweight(type);
            html.append(flyweight.render(token));
        }

        return html.toString();
    }
}

