package controller;

import dto.OrderDto;
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
import service.OrderService;
import service.ProductService;

import java.io.IOException;

public class ReportsController {

    @FXML private Label lblTodaySales;
    @FXML private Label lblTodayOrders;
    @FXML private Label lblTotalProducts;
    @FXML private Label lblLowStockCount;
    @FXML private Label lblInventoryValue;

    @FXML private ComboBox<String> cmbSalesFilter;
    @FXML private TableView<OrderDto> tblSales;
    @FXML private TableView<ProductDto> tblLowStock;
    @FXML private TableView<ProductDto> tblInventory;

    private final OrderService orderService = new OrderService();
    private final ProductService productService = new ProductService();

    @FXML
    public void initialize() {
        loadSummaryCards();
        cmbSalesFilter.setValue("Today");
        loadSalesReport(null);
        loadLowStockReport(null);
        loadInventorySummary(null);
        setupInventoryValueColumn();
    }

    private void loadSummaryCards() {
        // Today's Sales
        Double todaySales = orderService.getTodayTotalSales();
        lblTodaySales.setText(String.format("$%.2f", todaySales != null ? todaySales : 0.0));

        // Today's Orders
        int todayOrders = orderService.getTodayOrders().size();
        lblTodayOrders.setText(todayOrders + " Orders");

        // Total Products
        int totalProducts = productService.getAllProducts().size();
        lblTotalProducts.setText(String.valueOf(totalProducts));

        // Low Stock Count
        int lowStockCount = productService.getLowStockProducts().size();
        lblLowStockCount.setText(String.valueOf(lowStockCount));

        // Inventory Value
        double inventoryValue = productService.getAllProducts().stream()
                .mapToDouble(p -> p.getPrice() * p.getQuantity())
                .sum();
        lblInventoryValue.setText(String.format("$%.2f", inventoryValue));
    }

    @FXML
    public void loadSalesReport(ActionEvent event) {
        String filter = cmbSalesFilter.getValue();
        ObservableList<OrderDto> orders;

        if ("Today".equals(filter)) {
            orders = FXCollections.observableArrayList(orderService.getTodayOrders());
        } else {
            orders = FXCollections.observableArrayList(orderService.getAllOrders());
        }

        tblSales.setItems(orders);
    }

    @FXML
    public void loadLowStockReport(ActionEvent event) {
        ObservableList<ProductDto> lowStockProducts = FXCollections.observableArrayList(
                productService.getLowStockProducts()
        );
        tblLowStock.setItems(lowStockProducts);
    }

    @FXML
    public void loadInventorySummary(ActionEvent event) {
        ObservableList<ProductDto> products = FXCollections.observableArrayList(
                productService.getAllProducts()
        );
        tblInventory.setItems(products);
    }

    private void setupInventoryValueColumn() {
        TableColumn<ProductDto, Void> valueCol = (TableColumn<ProductDto, Void>)
                tblInventory.getColumns().get(5);

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
                    setStyle("-fx-text-fill: #5ba3f5; -fx-font-weight: bold;");
                }
            }
        });
    }

    @FXML
    public void backToDashboard(ActionEvent event) {
        try {
            Stage stage = (Stage) lblTodaySales.getScene().getWindow();
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
}