package io.aisstream.example_app.schema;

import org.flywaydb.core.Flyway;

public class FlywayInitializer {
    public static void migrate() {
        Flyway flyway = Flyway.configure()
            .dataSource("jdbc:postgresql://localhost:5432/test", "postgres", "root")
            .load();
        flyway.migrate();
    }
}
