package controller;

import dto.ProductDto;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import service.ProductService;

import java.io.IOException;

public class ProductsController {

    @FXML private TextField txtName;
    @FXML private ComboBox<String> cmbCategory;
    @FXML private TextField txtPrice;
    @FXML private TextField txtQuantity;
    @FXML private TextField txtSupplier;
    @FXML private TextField txtSearch;
    @FXML private Button btnSave;
    @FXML private TableView<ProductDto> tblProducts;

    private final ProductService productService = new ProductService();
    private ProductDto selectedProduct = null;

    @FXML
    public void initialize() {
        setupCategories();
        loadProducts();
        setupTableActions();

        // Table selection listener
        tblProducts.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    if (newVal != null) {
                        fillForm(newVal);
                    }
                }
        );
    }

    private void setupCategories() {
        ObservableList<String> categories = FXCollections.observableArrayList(
                "Men's Clothing", "Women's Clothing", "Kids Clothing",
                "Accessories", "Footwear", "Sportswear"
        );
        cmbCategory.setItems(categories);
    }

    private void loadProducts() {
        ObservableList<ProductDto> products = FXCollections.observableArrayList(
                productService.getAllProducts()
        );
        tblProducts.setItems(products);
    }

    private void setupTableActions() {
        TableColumn<ProductDto, Void> actionCol = (TableColumn<ProductDto, Void>)
                tblProducts.getColumns().get(6);

        actionCol.setCellFactory(param -> new TableCell<>() {
            private final Button btnDelete = new Button("Delete");

            {
                btnDelete.setOnAction(event -> {
                    ProductDto product = getTableView().getItems().get(getIndex());
                    deleteProduct(product);
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

    private void fillForm(ProductDto product) {
        selectedProduct = product;
        txtName.setText(product.getName());
        cmbCategory.setValue(product.getCategory());
        txtPrice.setText(String.valueOf(product.getPrice()));
        txtQuantity.setText(String.valueOf(product.getQuantity()));
        txtSupplier.setText(product.getSupplier());
        btnSave.setText("Update Product");
    }

    @FXML
    public void saveProduct(ActionEvent event) {
        if (!validateInputs()) {
            return;
        }

        ProductDto dto = new ProductDto();
        dto.setName(txtName.getText().trim());
        dto.setCategory(cmbCategory.getValue());
        dto.setPrice(Double.parseDouble(txtPrice.getText().trim()));
        dto.setQuantity(Integer.parseInt(txtQuantity.getText().trim()));
        dto.setSupplier(txtSupplier.getText().trim());

        try {
            if (selectedProduct == null) {
                productService.addProduct(dto);
                showAlert(Alert.AlertType.INFORMATION, "Success", "Product added successfully!");
            } else {
                dto.setId(selectedProduct.getId());
                productService.updateProduct(dto);
                showAlert(Alert.AlertType.INFORMATION, "Success", "Product updated successfully!");
            }

            clearForm(null);
            loadProducts();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to save product: " + e.getMessage());
        }
    }

    private void deleteProduct(ProductDto product) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText(null);
        confirm.setContentText("Are you sure you want to delete: " + product.getName() + "?");

        if (confirm.showAndWait().get() == ButtonType.OK) {
            productService.deleteProduct(product.getId());
            showAlert(Alert.AlertType.INFORMATION, "Success", "Product deleted successfully!");
            loadProducts();
        }
    }

    @FXML
    public void clearForm(ActionEvent event) {
        txtName.clear();
        cmbCategory.setValue(null);
        txtPrice.clear();
        txtQuantity.clear();
        txtSupplier.clear();
        selectedProduct = null;
        btnSave.setText("Add Product");
        tblProducts.getSelectionModel().clearSelection();
    }

    @FXML
    public void searchProducts(ActionEvent event) {
        String query = txtSearch.getText().trim().toLowerCase();
        if (query.isEmpty()) {
            loadProducts();
            return;
        }

        ObservableList<ProductDto> filtered = FXCollections.observableArrayList(
                productService.getAllProducts().stream()
                        .filter(p -> p.getName().toLowerCase().contains(query) ||
                                p.getCategory().toLowerCase().contains(query) ||
                                (p.getSupplier() != null && p.getSupplier().toLowerCase().contains(query)))
                        .toList()
        );
        tblProducts.setItems(filtered);
    }

    private boolean validateInputs() {
        if (txtName.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please enter product name!");
            return false;
        }
        if (cmbCategory.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please select category!");
            return false;
        }
        try {
            Double.parseDouble(txtPrice.getText().trim());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please enter valid price!");
            return false;
        }
        try {
            Integer.parseInt(txtQuantity.getText().trim());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please enter valid quantity!");
            return false;
        }
        return true;
    }

    @FXML
    public void backToDashboard(ActionEvent event) {
        try {
            Stage stage = (Stage) txtName.getScene().getWindow();
            stage.close();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/dashboard_form.fxml"));
            Parent root = loader.load();
            Stage dashboardStage = new Stage();
            dashboardStage.setTitle("Clothify Store - Dashboard");
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