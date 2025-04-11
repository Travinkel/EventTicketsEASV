package org.example.eventticketsystem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Launcher {
    public static void main(String[] args) throws IOException {
        // This launches the JavaFX app via the real Application class
        //scanStyleClasses("src/main/resources/views");
        Set<String>
                usedClasses =
                scanStyleClasses("src/main/resources/views");

        pruneCSS(
                "src/main/resources/styles/global-style.css",
                usedClasses,
                "src/main/resources/styles/global-style.cleaned.css"
        );
        javafx.application.Application.launch(EventTicketSystemApp.class,
                args);
    }

    public static Set<String> scanStyleClasses(String baseDir) {
        Set<String>
                usedClasses =
                new HashSet<>();

        try {
            Files.walk(Paths.get(baseDir))
                    .filter(path -> path.toString()
                            .endsWith(".fxml"))
                    .forEach(path -> {
                        try {
                            List<String>
                                    lines =
                                    Files.readAllLines(path);
                            for (String line : lines) {
                                Matcher
                                        matcher =
                                        Pattern.compile("styleClass\\s*=\\s*\"([^\"]+)\"")
                                                .matcher(line);
                                while (matcher.find()) {
                                    String[]
                                            classes =
                                            matcher.group(1)
                                                    .split("\\s+");
                                    usedClasses.addAll(Arrays.asList(classes));
                                }

                                Matcher
                                        inlineMatcher =
                                        Pattern.compile("style\\s*=\\s*\"([^\"]+)\"")
                                                .matcher(line);
                                while (inlineMatcher.find()) {
                                    usedClasses.add("[inline-style]");
                                }
                            }
                        } catch (IOException e) {
                            System.err.println("Error reading file: " +
                                               path +
                                               " - " +
                                               e.getMessage());
                        }
                    });

            System.out.println("✅ Used style classes:");
            usedClasses.forEach(System.out::println);

        } catch (IOException e) {
            System.err.println("❌ Failed to scan directory: " +
                               e.getMessage());
        }

        return usedClasses;
    }


    public static void pruneCSS(String cssPath,
                                Set<String> usedClasses,
                                String outputPath) throws IOException {
        List<String>
                lines =
                Files.readAllLines(Paths.get(cssPath));
        List<String>
                retained =
                new ArrayList<>();

        Pattern
                selectorPattern =
                Pattern.compile("^\\s*(\\.[a-zA-Z0-9\\-]+)\\s*\\{");

        boolean
                keep =
                false;
        for (String line : lines) {
            Matcher
                    matcher =
                    selectorPattern.matcher(line);
            if (matcher.find()) {
                String
                        selector =
                        matcher.group(1)
                                .substring(1); // remove the dot
                keep =
                        usedClasses.contains(selector);
            }

            if (keep ||
                line.trim()
                        .isEmpty()) {
                retained.add(line);
            }

            if (line.contains("}")) {
                keep =
                        false;
            }
        }

        Files.write(Paths.get(outputPath),
                retained);
        System.out.println("Pruned CSS written to " +
                           outputPath);
    }
}
