package org.example.texteditor.flyweight;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class FlyweightFactory {

    private final Map<String, Flyweight> flyweightPool = new HashMap<>();

    public Flyweight getFlyweight(String key) {
        return flyweightPool.computeIfAbsent(key, this::createFlyweight);
    }

    private Flyweight createFlyweight(String key) {
        switch (key) {
            case "KEYWORD":
                return new ConcreteFlyweight("#d73a49", true, false);
            case "NUMBER":
                return new ConcreteFlyweight("#005cc5", false, false);
            case "STRING":
                return new ConcreteFlyweight("#4CAF50", false, false);
            case "COMMENT":
                return new ConcreteFlyweight("#6a737d", false, true);
            default:
                return new ConcreteFlyweight("#24292e", false, false);
        }
    }
}
