package org.example.texteditor.flyweight;

import org.springframework.web.util.HtmlUtils;

public class ConcreteFlyweight implements Flyweight {
    private final String styleAttribute;

    public ConcreteFlyweight(String color, boolean bold, boolean italic) {
        StringBuilder sb = new StringBuilder("color:").append(color).append(";");
        if (bold) sb.append("font-weight:bold;");
        if (italic) sb.append("font-style:italic;");

        this.styleAttribute = sb.toString();
    }

    @Override
    public String render(String text) {
        return "<span style='" + styleAttribute + "'>" + HtmlUtils.htmlEscape(text) + "</span>";
    }
}