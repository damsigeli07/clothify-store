package org.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;

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
        // This method is called automatically after FXML is loaded
        System.out.println("Login form initialized");
    }

    @FXML
    public void handleLogin(ActionEvent event) {
        String username = txtUsername.getText();
        String password = txtPassword.getText();

        // Simple validation for now
        if (username.isEmpty() || password.isEmpty()) {
            lblError.setText("Please enter username and password");
            lblError.setVisible(true);
            return;
        }

        // TODO: Add actual authentication logic here
        if (username.equals("admin") && password.equals("admin123")) {
            lblError.setVisible(false);
            System.out.println("Login successful!");
            // Navigate to dashboard here
        } else {
            lblError.setText("Invalid username or password");
            lblError.setVisible(true);
        }
    }

    @FXML
    public void handleLogin(ActionEvent event) {
        String username = txtUsername.getText();
        String password = txtPassword.getText();

        // Simple validation
        if (username.isEmpty() || password.isEmpty()) {
            lblError.setText("Please enter username and password");
            lblError.setVisible(true);
            return;
        }

        // TODO: Add actual authentication logic here
        if (username.equals("admin") && password.equals("admin123")) {
            lblError.setVisible(false);

            try {
                // Close login window
                Stage loginStage = (Stage) btnLogin.getScene().getWindow();
                loginStage.close();

                // Open dashboard
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/dashboard_form.fxml"));
                Parent root = loader.load();
                Stage dashboardStage = new Stage();
                dashboardStage.setTitle("Clothify Store - Dashboard");
                dashboardStage.setScene(new Scene(root));
                dashboardStage.setMaximized(true); // Open maximized
                dashboardStage.show();

            } catch (IOException e) {
                e.printStackTrace();
                lblError.setText("Error loading dashboard");
                lblError.setVisible(true);
            }

        } else {
            lblError.setText("Invalid username or password");
            lblError.setVisible(true);
        }
    }
}