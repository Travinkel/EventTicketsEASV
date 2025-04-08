package org.example.eventticketsystem.utils.di;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Utility class for scanning and processing classes annotated for dependency injection.
 */
public final class InjectionScanner {
    private static final Logger LOGGER = LoggerFactory.getLogger(InjectionScanner.class);

    // Use LinkedHashMap to maintain scan order: Service -> Repository -> Component -> Injectable
    private static final Map<String, AnnotationHandler> HANDLERS = new LinkedHashMap<>();

    static {
        HANDLERS.put(Service.class.getName(), new ServiceHandler());       // Must go first
        HANDLERS.put(Repository.class.getName(), new RepositoryHandler()); // Then repositories
        HANDLERS.put(Component.class.getName(), new ComponentHandler());   // Then components
        HANDLERS.put(Injectable.class.getName(), new InjectableHandler()); // Finally injectables
    }

    private InjectionScanner() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Scans the specified package for classes annotated with dependency injection annotations.
     *
     * @param basePackage the base package to scan
     * @throws RuntimeException if the scanning process fails
     */
    public static void scan(String basePackage) {
        LOGGER.info("🔍 Scanning package: {}", basePackage);
        Injector injector = Injector.getInstance();

        try (ScanResult scanResult = new ClassGraph()
                .enableClassInfo()
                .enableAnnotationInfo()
                .acceptPackages(basePackage)
                .scan()) {

            HANDLERS.forEach((annotation, handler) -> {
                scanResult.getClassesWithAnnotation(annotation)
                        .loadClasses()
                        .forEach(clazz -> handler.handle(clazz, injector));
            });

        } catch (Exception e) {
            LOGGER.error("❌ Injection scan failed: {}", e.getMessage(), e);
            throw new RuntimeException("Injection scan failed", e);
        }
    }
}
