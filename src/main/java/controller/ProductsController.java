package controller;

import dto.ProductDto;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
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
        setupTableColumns();
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

        // Real-time search
        txtSearch.textProperty().addListener((obs, oldVal, newVal) -> {
            searchProducts(null);
        });
    }

    private void setupCategories() {
        ObservableList<String> categories = FXCollections.observableArrayList(
                "Tops", "Bottoms", "Dresses", "Outerwear", "Footwear", "Accessories"
        );
        cmbCategory.setItems(categories);
    }

    private void setupTableColumns() {
        // Get columns and set cell value factories
        TableColumn<ProductDto, Integer> idCol = (TableColumn<ProductDto, Integer>) tblProducts.getColumns().get(0);
        TableColumn<ProductDto, String> nameCol = (TableColumn<ProductDto, String>) tblProducts.getColumns().get(1);
        TableColumn<ProductDto, String> categoryCol = (TableColumn<ProductDto, String>) tblProducts.getColumns().get(2);
        TableColumn<ProductDto, Double> priceCol = (TableColumn<ProductDto, Double>) tblProducts.getColumns().get(3);
        TableColumn<ProductDto, Integer> quantityCol = (TableColumn<ProductDto, Integer>) tblProducts.getColumns().get(4);
        TableColumn<ProductDto, String> supplierCol = (TableColumn<ProductDto, String>) tblProducts.getColumns().get(5);

        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        supplierCol.setCellValueFactory(new PropertyValueFactory<>("supplier"));

        // Format price column with $ symbol
        priceCol.setCellFactory(col -> new TableCell<ProductDto, Double>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText(String.format("$%.2f", price));
                }
            }
        });

        // Show "-" for null suppliers
        supplierCol.setCellFactory(col -> new TableCell<ProductDto, String>() {
            @Override
            protected void updateItem(String supplier, boolean empty) {
                super.updateItem(supplier, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(supplier == null || supplier.trim().isEmpty() ? "-" : supplier);
                }
            }
        });
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
            private final HBox actionButtons = new HBox(8);
            private final Button btnEdit = new Button("Edit");
            private final Button btnDelete = new Button("Delete");

            {
                // Style Edit button - Purple gradient
                btnEdit.setStyle(
                        "-fx-background-color: linear-gradient(to right, #8B5CF6, #A78BFA); " +
                                "-fx-text-fill: white; " +
                                "-fx-background-radius: 6; " +
                                "-fx-font-weight: bold; " +
                                "-fx-font-size: 12px; " +
                                "-fx-cursor: hand; " +
                                "-fx-padding: 6 12 6 12;"
                );

                btnEdit.setOnAction(event -> {
                    ProductDto product = getTableView().getItems().get(getIndex());
                    fillForm(product);
                    txtName.requestFocus();
                });

                // Style Delete button - Red gradient
                btnDelete.setStyle(
                        "-fx-background-color: linear-gradient(to right, #EF4444, #DC2626); " +
                                "-fx-text-fill: white; " +
                                "-fx-background-radius: 6; " +
                                "-fx-font-weight: bold; " +
                                "-fx-font-size: 12px; " +
                                "-fx-cursor: hand; " +
                                "-fx-padding: 6 12 6 12;"
                );

                btnDelete.setOnAction(event -> {
                    ProductDto product = getTableView().getItems().get(getIndex());
                    deleteProduct(product);
                });

                actionButtons.setAlignment(Pos.CENTER);
                actionButtons.getChildren().addAll(btnEdit, btnDelete);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(actionButtons);
                }
            }
        });
    }

    private void fillForm(ProductDto product) {
        selectedProduct = product;
        txtName.setText(product.getName());
        cmbCategory.setValue(product.getCategory());
        txtPrice.setText(String.valueOf(product.getPrice()));
        txtQuantity.setText(String.valueOf(product.getQuantity()));
        txtSupplier.setText(product.getSupplier() != null ? product.getSupplier() : "");
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

        String supplier = txtSupplier.getText().trim();
        dto.setSupplier(supplier.isEmpty() ? null : supplier);

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
        confirm.setContentText("Are you sure you want to delete this product?\n\n" + product.getName());

        if (confirm.showAndWait().get() == ButtonType.OK) {
            try {
                productService.deleteProduct(product.getId());
                showAlert(Alert.AlertType.INFORMATION, "Success", "Product deleted successfully!");

                // Clear form if the deleted product was selected
                if (selectedProduct != null && selectedProduct.getId().equals(product.getId())) {
                    clearForm(null);
                }

                loadProducts();
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete product: " + e.getMessage());
            }
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
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please enter product name");
            txtName.requestFocus();
            return false;
        }
        if (cmbCategory.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please select a category");
            cmbCategory.requestFocus();
            return false;
        }

        String priceText = txtPrice.getText().trim();
        if (priceText.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please enter price");
            txtPrice.requestFocus();
            return false;
        }
        try {
            double price = Double.parseDouble(priceText);
            if (price <= 0) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", "Price must be greater than 0");
                txtPrice.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please enter a valid price");
            txtPrice.requestFocus();
            return false;
        }

        String qtyText = txtQuantity.getText().trim();
        if (qtyText.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please enter quantity");
            txtQuantity.requestFocus();
            return false;
        }
        try {
            int quantity = Integer.parseInt(qtyText);
            if (quantity < 0) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", "Quantity cannot be negative");
                txtQuantity.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please enter a valid quantity");
            txtQuantity.requestFocus();
            return false;
        }

        return true;
    }

    @FXML
    public void backToDashboard(ActionEvent event) {
        // Warn about unsaved changes
        if (!txtName.getText().trim().isEmpty() || cmbCategory.getValue() != null ||
                !txtPrice.getText().trim().isEmpty() || !txtQuantity.getText().trim().isEmpty()) {

            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirm");
            confirm.setHeaderText(null);
            confirm.setContentText("Are you sure you want to go back? Unsaved changes will be lost.");

            if (confirm.showAndWait().get() != ButtonType.OK) {
                return;
            }
        }

        try {
            Stage stage = (Stage) txtName.getScene().getWindow();
            stage.close();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/dashboard_form.fxml"));
            Parent root = loader.load();
            Stage dashboardStage = new Stage();
            dashboardStage.setTitle("SATINE - Dashboard");
            dashboardStage.setScene(new Scene(root));
            dashboardStage.setMaximized(true);
            dashboardStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to return to dashboard");
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