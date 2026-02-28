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
        try {
            Stage stage = (Stage) lblUsername.getScene().getWindow();
            stage.close();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/pos_form.fxml"));
            Parent root = loader.load();
            Stage posStage = new Stage();
            posStage.setTitle("Clothify Store - POS");
            posStage.setScene(new Scene(root));
            posStage.setMaximized(true);
            posStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void openProducts(ActionEvent event) {
        try {
            Stage stage = (Stage) lblUsername.getScene().getWindow();
            stage.close();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/products_form.fxml"));
            Parent root = loader.load();
            Stage productsStage = new Stage();
            productsStage.setTitle("Clothify Store - Products");
            productsStage.setScene(new Scene(root));
            productsStage.setMaximized(true);
            productsStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void openReports(ActionEvent event) {
        try {
            Stage stage = (Stage) lblUsername.getScene().getWindow();
            stage.close();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/reports_form.fxml"));
            Parent root = loader.load();
            Stage reportsStage = new Stage();
            reportsStage.setTitle("Clothify Store - Reports");
            reportsStage.setScene(new Scene(root));
            reportsStage.setMaximized(true);
            reportsStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void openSuppliers(ActionEvent event) {
        try {
            Stage stage = (Stage) lblUsername.getScene().getWindow();
            stage.close();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/suppliers_form.fxml"));
            Parent root = loader.load();
            Stage suppliersStage = new Stage();
            suppliersStage.setTitle("Satine - Suppliers");
            suppliersStage.setScene(new Scene(root));
            suppliersStage.setMaximized(true);
            suppliersStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void openEmployees(ActionEvent event) {
        try {
            Stage stage = (Stage) lblUsername.getScene().getWindow();
            stage.close();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/employees_form.fxml"));
            Parent root = loader.load();
            Stage employeesStage = new Stage();
            employeesStage.setTitle("Satine - Employees");
            employeesStage.setScene(new Scene(root));
            employeesStage.setMaximized(true);
            employeesStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void openInventory(ActionEvent event) {
        showAlert("Inventory", "Inventory screen coming soon!");
    }


    @FXML
    public void openOrders(ActionEvent event) {
        showAlert("Orders", "Orders screen coming soon!");
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