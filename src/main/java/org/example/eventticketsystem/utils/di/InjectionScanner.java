package org.example.eventticketsystem.utils.di;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import org.example.eventticketsystem.utils.di.exceptions.DependencyInjectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class InjectionScanner {
    private static final Logger LOGGER = LoggerFactory.getLogger(InjectionScanner.class);

    private InjectionScanner() {
        throw new IllegalStateException("Utility class");
    }


    public static void scan(String basePackage) {
        LOGGER.info("🔍 Scanning package: {}", basePackage);
        Injector injector = Injector.getInstance();

        try (ScanResult scanResult = new ClassGraph()
                .enableClassInfo()
                .enableAnnotationInfo()
                .acceptPackages(basePackage)
                .scan()) {

            scanResult.getClassesWithAnnotation(Injectable.class.getName())
                    .loadClasses()
                    .forEach(clazz -> {
                        if (clazz.getConstructors().length == 0) {
                            LOGGER.warn("⏭️ Skipping {} – no public constructor (likely singleton)", clazz.getSimpleName());
                            return;
                        }

                        try {
                            Object instance = injector.createInstance(clazz);
                            injector.register((Class<Object>) clazz, instance);
                            LOGGER.info("✅ Injecting @Injectable class: {}", clazz.getSimpleName());
                        } catch (DependencyInjectionException e) {
                            LOGGER.error("❌ Failed to inject {}: {}", clazz.getSimpleName(), e.getMessage());
                        }
                    });
        } catch (Exception e) {
            throw new RuntimeException("Injection scan failed", e);
        }
    }
}
