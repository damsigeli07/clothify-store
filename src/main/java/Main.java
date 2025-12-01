import util.DatabaseInitializer;

public class Main {
    public static void main(String[] args) {
        // Initialize database and create default admin user
        System.out.println("Initializing database...");
        DatabaseInitializer.initializeDatabase();

        // Start the JavaFX application
        Starter.main(args);
    }
}