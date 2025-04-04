package org.example.eventticketsystem.di;

import io.github.classgraph.ClassGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public final class InjectionScanner {
    private static final Logger LOGGER = LoggerFactory.getLogger(InjectionScanner.class);


    private InjectionScanner() {
        throw new IllegalStateException("Utility class");
    }

    public static void scan(String basePackage) {
        LOGGER.info("Scanning package: {}", basePackage);

        Injector injector = Injector.getInstance();

        try (var scanResult = new ClassGraph()
                .enableClassInfo()
                .enableAnnotationInfo()
                .acceptPackages(basePackage)
                .scan()) {

            scanResult.getClassesWithAnnotation(Injectable.class.getName())
                    .loadClasses()
                    .forEach(clazz -> {
                        LOGGER.info("Injecting @Injectable class: {}", clazz.getSimpleName());
                        injector.createInstance(clazz);
                    });
        } catch (Exception e) {
            throw new RuntimeException("Injection scan failed", e);
        }
    }
}
