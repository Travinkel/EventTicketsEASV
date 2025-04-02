package org.example.eventticketsystem.di;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public final class InjectionScanner {
    private static final Logger logger = LoggerFactory.getLogger(InjectionScanner.class);


    private InjectionScanner() {
        throw new IllegalStateException("Utility class");
    }

    public static void scan(String basePackage) {
        Injector injector = Injector.getInstance();

        try (ScanResult scanResult = new ClassGraph()
                .enableClassInfo()
                .enableAnnotationInfo()
                .acceptPackages(basePackage)
                .scan()) {
            scanResult.getClassesWithAnnotation(Injectable.class.getName())
                    .loadClasses()
                    .forEach(injector::createInstance);
        }
    }
}
