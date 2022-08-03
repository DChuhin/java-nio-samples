package internalServer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.function.Predicate.not;

public class StaticContent {

    private final Map<String, String> cache = new HashMap<>();

    public void init() {
        try {
            Files.list(Path.of("./static"))
                .filter(not(Files::isDirectory))
                .forEach(file -> {
                    try {
                        String fileContent = Files.lines(file)
                            .map(String::trim)
                            .collect(Collectors.joining(""));
                        cache.put(file.getFileName().toString(), fileContent);
                    } catch (IOException e) {
                        System.out.println("Could not read file: " + file.toAbsolutePath());
                    }
                });
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    public String get(String path) {
        String finalPath = path;
        if (path.startsWith("/")) {
            finalPath = path.substring(1);
        }
        if (path.equals("/")) {
            finalPath = "index.html";
        }
        return cache.get(finalPath);
    }


}
