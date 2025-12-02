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
        // Setup ComboBox
        cmbSalesFilter.setItems(FXCollections.observableArrayList("Today", "All"));
        cmbSalesFilter.setValue("Today");

        // Setup Sales Table Columns
        setupSalesTableColumns();

        // Setup Low Stock Table Columns
        setupLowStockTableColumns();

        // Setup Inventory Table Columns
        setupInventoryTableColumns();

        loadSummaryCards();
        loadSalesReport(null);
        loadLowStockReport(null);
        loadInventorySummary(null);
    }

    private void setupSalesTableColumns() {
        TableColumn<OrderDto, Long> idCol = (TableColumn<OrderDto, Long>) tblSales.getColumns().get(0);
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<OrderDto, ?> dateCol = (TableColumn<OrderDto, ?>) tblSales.getColumns().get(1);
        dateCol.setCellValueFactory(new PropertyValueFactory<>("orderDate"));

        TableColumn<OrderDto, String> customerCol = (TableColumn<OrderDto, String>) tblSales.getColumns().get(2);
        customerCol.setCellValueFactory(new PropertyValueFactory<>("customerName"));

        TableColumn<OrderDto, Double> amountCol = (TableColumn<OrderDto, Double>) tblSales.getColumns().get(3);
        amountCol.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));

    }

    private void setupLowStockTableColumns() {
        TableColumn<ProductDto, Long> idCol = (TableColumn<ProductDto, Long>) tblLowStock.getColumns().get(0);
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<ProductDto, String> nameCol = (TableColumn<ProductDto, String>) tblLowStock.getColumns().get(1);
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<ProductDto, String> categoryCol = (TableColumn<ProductDto, String>) tblLowStock.getColumns().get(2);
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));

        TableColumn<ProductDto, Integer> qtyCol = (TableColumn<ProductDto, Integer>) tblLowStock.getColumns().get(3);
        qtyCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        TableColumn<ProductDto, Double> priceCol = (TableColumn<ProductDto, Double>) tblLowStock.getColumns().get(4);
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));

        TableColumn<ProductDto, String> supplierCol = (TableColumn<ProductDto, String>) tblLowStock.getColumns().get(5);
        supplierCol.setCellValueFactory(new PropertyValueFactory<>("supplier"));
    }

    private void setupInventoryTableColumns() {
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

        // Total Value column - custom cell factory
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
                    setStyle("-fx-text-fill: #5ba3f5; -fx-font-weight: bold;");
                }
            }
        });

        TableColumn<ProductDto, String> supplierCol = (TableColumn<ProductDto, String>) tblInventory.getColumns().get(6);
        supplierCol.setCellValueFactory(new PropertyValueFactory<>("supplier"));
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