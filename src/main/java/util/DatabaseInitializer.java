package util;

import entity.User;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.mindrot.jbcrypt.BCrypt;

public class DatabaseInitializer {

    public static void initializeDatabase() {
        Session session = null;
        Transaction transaction = null;

        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();

            // Check if admin user already exists
            Query<User> query = session.createQuery("FROM User WHERE username = :username", User.class);
            query.setParameter("username", "admin");
            User existingAdmin = query.uniqueResult();

            if (existingAdmin == null) {
                // Create default admin user
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(BCrypt.hashpw("admin123", BCrypt.gensalt())); // Hashed password
                admin.setFullName("System Administrator");
                admin.setRole("ADMIN");
                admin.setActive(true);

                session.persist(admin);
                System.out.println("✅ Default admin user created successfully!");
                System.out.println("   Username: admin");
                System.out.println("   Password: admin123");
            } else {
                System.out.println("✅ Admin user already exists");
            }

            transaction.commit();

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.err.println("❌ Error initializing database: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
}