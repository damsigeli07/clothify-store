package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import entity.User;

import java.io.IOException;

public class DashboardController {

    @FXML private Label lblUsername;
    @FXML private Label lblTotalSales;
    @FXML private Label lblProductsSold;
    @FXML private Label lblLowStock;
    @FXML private Label lblTotalOrders;

    private User currentUser;

    @FXML
    public void initialize() {
        loadDashboardData();
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        if (lblUsername != null && user != null) {
            lblUsername.setText(user.getFullName() + " (" + user.getRole() + ")");
        }
    }

    private void loadDashboardData() {
        // TODO: Load from database
        if (lblTotalSales != null) lblTotalSales.setText("$2,450.00");
        if (lblProductsSold != null) lblProductsSold.setText("156");
        if (lblLowStock != null) lblLowStock.setText("8");
        if (lblTotalOrders != null) lblTotalOrders.setText("42");
    }

    @FXML
    public void openPOS(ActionEvent event) {
        showAlert("POS", "POS screen coming soon!");
    }

    @FXML
    public void openProducts(ActionEvent event) {
        showAlert("Products", "Products screen coming soon!");
    }

    @FXML
    public void openInventory(ActionEvent event) {
        showAlert("Inventory", "Inventory screen coming soon!");
    }

    @FXML
    public void openSuppliers(ActionEvent event) {
        showAlert("Suppliers", "Suppliers screen coming soon!");
    }

    @FXML
    public void openEmployees(ActionEvent event) {
        showAlert("Employees", "Employees screen coming soon!");
    }

    @FXML
    public void openOrders(ActionEvent event) {
        showAlert("Orders", "Orders screen coming soon!");
    }

    @FXML
    public void openReports(ActionEvent event) {
        showAlert("Reports", "Reports screen coming soon!");
    }

    @FXML
    public void handleLogout(ActionEvent event) {
        try {
            Stage currentStage = (Stage) lblUsername.getScene().getWindow();
            currentStage.close();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login_form.fxml"));
            Parent root = loader.load();
            Stage loginStage = new Stage();
            loginStage.setTitle("Clothify Store - Login");
            loginStage.setScene(new Scene(root));
            loginStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}