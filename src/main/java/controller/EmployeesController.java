package controller;

import dto.EmployeeDto;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import service.EmployeeService;

import java.io.IOException;

public class EmployeesController {

    @FXML private TextField txtName;
    @FXML private TextField txtPosition;
    @FXML private TextField txtEmail;
    @FXML private TextField txtPhone;
    @FXML private TextField txtSalary;
    @FXML private TextField txtSearch;
    @FXML private Button btnSave;
    @FXML private TableView<EmployeeDto> tblEmployees;

    private final EmployeeService employeeService = new EmployeeService();
    private EmployeeDto selectedEmployee = null;

    @FXML
    public void initialize() {
        loadEmployees();
        setupTableActions();

        tblEmployees.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    if (newVal != null) fillForm(newVal);
                }
        );
    }

    private void loadEmployees() {
        ObservableList<EmployeeDto> employees = FXCollections.observableArrayList(
                employeeService.getAllEmployees()
        );
        tblEmployees.setItems(employees);
    }

    private void setupTableActions() {
        TableColumn<EmployeeDto, Void> actionCol = (TableColumn<EmployeeDto, Void>)
                tblEmployees.getColumns().get(6);

        actionCol.setCellFactory(param -> new TableCell<>() {
            private final Button btnDelete = new Button("Delete");

            {
                btnDelete.setOnAction(event -> {
                    EmployeeDto employee = getTableView().getItems().get(getIndex());
                    deleteEmployee(employee);
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

    private void fillForm(EmployeeDto employee) {
        selectedEmployee = employee;
        txtName.setText(employee.getName());
        txtPosition.setText(employee.getPosition());
        txtEmail.setText(employee.getEmail());
        txtPhone.setText(employee.getPhone());
        txtSalary.setText(employee.getSalary() != null ? String.valueOf(employee.getSalary()) : "");
        btnSave.setText("Update Employee");
    }

    @FXML
    public void saveEmployee(ActionEvent event) {
        if (txtName.getText().trim().isEmpty() || txtPosition.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Name and Position are required!");
            return;
        }

        EmployeeDto dto = new EmployeeDto();
        dto.setName(txtName.getText().trim());
        dto.setPosition(txtPosition.getText().trim());
        dto.setEmail(txtEmail.getText().trim());
        dto.setPhone(txtPhone.getText().trim());

        try {
            if (!txtSalary.getText().trim().isEmpty()) {
                dto.setSalary(Double.parseDouble(txtSalary.getText().trim()));
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Invalid salary amount!");
            return;
        }

        try {
            if (selectedEmployee == null) {
                employeeService.addEmployee(dto);
                showAlert(Alert.AlertType.INFORMATION, "Success", "Employee added successfully!");
            } else {
                dto.setId(selectedEmployee.getId());
                dto.setActive(selectedEmployee.getActive());
                employeeService.updateEmployee(dto);
                showAlert(Alert.AlertType.INFORMATION, "Success", "Employee updated successfully!");
            }

            clearForm(null);
            loadEmployees();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to save employee: " + e.getMessage());
        }
    }

    private void deleteEmployee(EmployeeDto employee) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setContentText("Delete employee: " + employee.getName() + "?");

        if (confirm.showAndWait().get() == ButtonType.OK) {
            employeeService.deleteEmployee(employee.getId());
            showAlert(Alert.AlertType.INFORMATION, "Success", "Employee deleted!");
            loadEmployees();
        }
    }

    @FXML
    public void clearForm(ActionEvent event) {
        txtName.clear();
        txtPosition.clear();
        txtEmail.clear();
        txtPhone.clear();
        txtSalary.clear();
        selectedEmployee = null;
        btnSave.setText("Add Employee");
        tblEmployees.getSelectionModel().clearSelection();
    }

    @FXML
    public void searchEmployees(ActionEvent event) {
        String query = txtSearch.getText().trim().toLowerCase();
        if (query.isEmpty()) {
            loadEmployees();
            return;
        }

        ObservableList<EmployeeDto> filtered = FXCollections.observableArrayList(
                employeeService.getAllEmployees().stream()
                        .filter(e -> e.getName().toLowerCase().contains(query) ||
                                e.getPosition().toLowerCase().contains(query))
                        .toList()
        );
        tblEmployees.setItems(filtered);
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