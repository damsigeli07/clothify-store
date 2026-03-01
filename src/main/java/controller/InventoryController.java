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

public class InventoryController {

    @FXML private Label lblTotalProducts;
    @FXML private Label lblLowStock;
    @FXML private Label lblStockValue;
    @FXML private Label lblOutOfStock;
    @FXML private ComboBox<String> cmbFilter;
    @FXML private TextField txtSearch;
    @FXML private TableView<ProductDto> tblInventory;

    private final ProductService productService = new ProductService();

    @FXML
    public void initialize() {
        setupFilter();
        setupTableColumns();
        loadInventory();
        updateStats();
    }

    private void setupFilter() {
        cmbFilter.setItems(FXCollections.observableArrayList(
                "All Products", "Low Stock", "Out of Stock", "In Stock"
        ));
        cmbFilter.setValue("All Products");
    }

    private void setupTableColumns() {
        TableColumn<ProductDto, Long> idCol = (TableColumn<ProductDto, Long>) tblInventory.getColumns().get(0);
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<ProductDto, String> nameCol = (TableColumn<ProductDto, String>) tblInventory.getColumns().get(1);
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<ProductDto, String> categoryCol = (TableColumn<ProductDto, String>) tblInventory.getColumns().get(2);
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));

        TableColumn<ProductDto, Integer> qtyCol = (TableColumn<ProductDto, Integer>) tblInventory.getColumns().get(3);
        qtyCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        TableColumn<ProductDto, Double> priceCol = (TableColumn<ProductDto, Double>) tblInventory.getColumns().get(4);
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));

        // Total Value column
        TableColumn<ProductDto, Void> valueCol = (TableColumn<ProductDto, Void>) tblInventory.getColumns().get(5);
        valueCol.setCellFactory(param -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    ProductDto product = getTableView().getItems().get(getIndex());
                    double value = product.getPrice() * product.getQuantity();
                    setText(String.format("$%.2f", value));
                    setStyle("-fx-text-fill: #10B981; -fx-font-weight: bold;");
                }
            }
        });

        TableColumn<ProductDto, String> supplierCol = (TableColumn<ProductDto, String>) tblInventory.getColumns().get(6);
        supplierCol.setCellValueFactory(new PropertyValueFactory<>("supplier"));

        // Status column
        TableColumn<ProductDto, Void> statusCol = (TableColumn<ProductDto, Void>) tblInventory.getColumns().get(7);
        statusCol.setCellFactory(param -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                    setStyle("");
                } else {
                    ProductDto product = getTableView().getItems().get(getIndex());
                    int qty = product.getQuantity();
                    if (qty == 0) {
                        setText("Out of Stock");
                        setStyle("-fx-text-fill: #EF4444; -fx-font-weight: bold;");
                    } else if (qty < 10) {
                        setText("Low Stock");
                        setStyle("-fx-text-fill: #F59E0B; -fx-font-weight: bold;");
                    } else {
                        setText("In Stock");
                        setStyle("-fx-text-fill: #10B981; -fx-font-weight: bold;");
                    }
                }
            }
        });
    }

    private void loadInventory() {
        ObservableList<ProductDto> products = FXCollections.observableArrayList(
                productService.getAllProducts()
        );
        tblInventory.setItems(products);
    }

    private void updateStats() {
        var products = productService.getAllProducts();

        lblTotalProducts.setText(String.valueOf(products.size()));

        long lowStock = products.stream().filter(p -> p.getQuantity() > 0 && p.getQuantity() < 10).count();
        lblLowStock.setText(String.valueOf(lowStock));

        double stockValue = products.stream()
                .mapToDouble(p -> p.getPrice() * p.getQuantity())
                .sum();
        lblStockValue.setText(String.format("$%.2f", stockValue));

        long outOfStock = products.stream().filter(p -> p.getQuantity() == 0).count();
        lblOutOfStock.setText(String.valueOf(outOfStock));
    }

    @FXML
    public void filterInventory(ActionEvent event) {
        String filter = cmbFilter.getValue();
        var allProducts = productService.getAllProducts();

        ObservableList<ProductDto> filtered = switch (filter) {
            case "Low Stock" -> FXCollections.observableArrayList(
                    allProducts.stream().filter(p -> p.getQuantity() > 0 && p.getQuantity() < 10).toList()
            );
            case "Out of Stock" -> FXCollections.observableArrayList(
                    allProducts.stream().filter(p -> p.getQuantity() == 0).toList()
            );
            case "In Stock" -> FXCollections.observableArrayList(
                    allProducts.stream().filter(p -> p.getQuantity() >= 10).toList()
            );
            default -> FXCollections.observableArrayList(allProducts);
        };

        tblInventory.setItems(filtered);
    }

    @FXML
    public void searchInventory(ActionEvent event) {
        String query = txtSearch.getText().trim().toLowerCase();
        if (query.isEmpty()) {
            loadInventory();
            return;
        }

        ObservableList<ProductDto> filtered = FXCollections.observableArrayList(
                productService.getAllProducts().stream()
                        .filter(p -> p.getName().toLowerCase().contains(query) ||
                                p.getCategory().toLowerCase().contains(query))
                        .toList()
        );
        tblInventory.setItems(filtered);
    }

    @FXML
    public void backToDashboard(ActionEvent event) {
        try {
            Stage stage = (Stage) txtSearch.getScene().getWindow();
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
}