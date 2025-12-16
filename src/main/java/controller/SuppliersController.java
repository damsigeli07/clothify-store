package controller;

import dto.SupplierDto;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import service.SupplierService;

import java.io.IOException;

public class SuppliersController {

    @FXML private TextField txtName;
    @FXML private TextField txtEmail;
    @FXML private TextField txtPhone;
    @FXML private TextArea txtAddress;
    @FXML private TextField txtSearch;
    @FXML private Button btnSave;
    @FXML private TableView<SupplierDto> tblSuppliers;

    private final SupplierService supplierService = new SupplierService();
    private SupplierDto selectedSupplier = null;

    @FXML
    public void initialize() {
        loadSuppliers();
        setupTableActions();

        tblSuppliers.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    if (newVal != null) fillForm(newVal);
                }
        );
    }

    private void loadSuppliers() {
        ObservableList<SupplierDto> suppliers = FXCollections.observableArrayList(
                supplierService.getAllSuppliers()
        );
        tblSuppliers.setItems(suppliers);
    }

    private void setupTableActions() {
        TableColumn<SupplierDto, Void> actionCol = (TableColumn<SupplierDto, Void>)
                tblSuppliers.getColumns().get(5);

        actionCol.setCellFactory(param -> new TableCell<>() {
            private final Button btnDelete = new Button("Delete");

            {
                btnDelete.setOnAction(event -> {
                    SupplierDto supplier = getTableView().getItems().get(getIndex());
                    deleteSupplier(supplier);
                });
                btnDelete.setStyle("-fx-background-color: #F87171; -fx-text-fill: white;");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btnDelete);
            }
        });
    }

    private void fillForm(SupplierDto supplier) {
        selectedSupplier = supplier;
        txtName.setText(supplier.getName());
        txtEmail.setText(supplier.getEmail());
        txtPhone.setText(supplier.getPhone());
        txtAddress.setText(supplier.getAddress());
        btnSave.setText("Update Supplier");
    }

    @FXML
    public void saveSupplier(ActionEvent event) {
        if (txtName.getText().trim().isEmpty() || txtPhone.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Name and Phone are required!");
            return;
        }

        SupplierDto dto = new SupplierDto();
        dto.setName(txtName.getText().trim());
        dto.setEmail(txtEmail.getText().trim());
        dto.setPhone(txtPhone.getText().trim());
        dto.setAddress(txtAddress.getText().trim());

        try {
            if (selectedSupplier == null) {
                supplierService.addSupplier(dto);
                showAlert(Alert.AlertType.INFORMATION, "Success", "Supplier added successfully!");
            } else {
                dto.setId(selectedSupplier.getId());
                dto.setActive(selectedSupplier.getActive());
                supplierService.updateSupplier(dto);
                showAlert(Alert.AlertType.INFORMATION, "Success", "Supplier updated successfully!");
            }

            clearForm(null);
            loadSuppliers();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to save supplier: " + e.getMessage());
        }
    }

    private void deleteSupplier(SupplierDto supplier) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setContentText("Delete supplier: " + supplier.getName() + "?");

        if (confirm.showAndWait().get() == ButtonType.OK) {
            supplierService.deleteSupplier(supplier.getId());
            showAlert(Alert.AlertType.INFORMATION, "Success", "Supplier deleted!");
            loadSuppliers();
        }
    }

    @FXML
    public void clearForm(ActionEvent event) {
        txtName.clear();
        txtEmail.clear();
        txtPhone.clear();
        txtAddress.clear();
        selectedSupplier = null;
        btnSave.setText("Add Supplier");
        tblSuppliers.getSelectionModel().clearSelection();
    }

    @FXML
    public void searchSuppliers(ActionEvent event) {
        String query = txtSearch.getText().trim().toLowerCase();
        if (query.isEmpty()) {
            loadSuppliers();
            return;
        }

        ObservableList<SupplierDto> filtered = FXCollections.observableArrayList(
                supplierService.getAllSuppliers().stream()
                        .filter(s -> s.getName().toLowerCase().contains(query) ||
                                (s.getEmail() != null && s.getEmail().toLowerCase().contains(query)))
                        .toList()
        );
        tblSuppliers.setItems(filtered);
    }

    @FXML
    public void backToDashboard(ActionEvent event) {
        try {
            Stage stage = (Stage) txtName.getScene().getWindow();
            stage.close();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/dashboard_form.fxml"));
            Parent root = loader.load();
            Stage dashboardStage = new Stage();
            dashboardStage.setTitle("Satine - Dashboard");
            dashboardStage.setScene(new Scene(root));
            dashboardStage.setMaximized(true);
            dashboardStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}