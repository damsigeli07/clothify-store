package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import entity.User;
import util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;

public class LoginFormController {

    @FXML
    private TextField txtUsername;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private Button btnLogin;

    @FXML
    private Label lblError;

    @FXML
    public void initialize() {
        System.out.println("Login form initialized");
        // Hide error label initially
        if (lblError != null) {
            lblError.setVisible(false);
        }
    }

    @FXML
    public void handleLogin(ActionEvent event) {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText();

        // Validation
        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter username and password");
            return;
        }

        // Authenticate user
        User user = authenticateUser(username, password);

        if (user != null) {
            if (!user.getActive()) {
                showError("Your account is inactive. Please contact admin.");
                return;
            }

            // Login successful
            System.out.println("✅ Login successful: " + user.getFullName());
            lblError.setVisible(false);

            try {
                // Close login window
                Stage loginStage = (Stage) btnLogin.getScene().getWindow();
                loginStage.close();

                // Open dashboard
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/dashboard_form.fxml"));
                Parent root = loader.load();

                // Pass user data to dashboard controller (optional)
                DashboardController dashboardController = loader.getController();
                dashboardController.setCurrentUser(user);

                Stage dashboardStage = new Stage();
                dashboardStage.setTitle("Clothify Store - Dashboard");
                dashboardStage.setScene(new Scene(root));
                dashboardStage.setMaximized(true);  // Add this line
                dashboardStage.show();

            } catch (IOException e) {
                e.printStackTrace();
                showError("Error loading dashboard: " + e.getMessage());
            }

        } else {
            showError("Invalid username or password");
        }
    }

    private User authenticateUser(String username, String password) {
        Session session = null;
        User user = null;

        try {
            session = HibernateUtil.getSessionFactory().openSession();

            // Query to find user by username
            Query<User> query = session.createQuery(
                    "FROM User WHERE username = :username",
                    User.class
            );
            query.setParameter("username", username);
            user = query.uniqueResult();

            // Check if user exists and password matches
            if (user != null && BCrypt.checkpw(password, user.getPassword())) {
                return user;
            }

        } catch (Exception e) {
            System.err.println("❌ Authentication error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (session != null) {
                session.close();
            }
        }

        return null;
    }

    private void showError(String message) {
        lblError.setText(message);
        lblError.setVisible(true);
    }
}