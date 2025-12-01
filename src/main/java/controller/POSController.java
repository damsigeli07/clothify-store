package controller;

import dto.CartItemDto;
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
import javafx.stage.Stage;
import service.OrderService;
import service.ProductService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class POSController {

    @FXML private Label lblDateTime;
    @FXML private TextField txtSearch;
    @FXML private TableView<ProductDto> tblProducts;
    @FXML private TableView<CartItemDto> tblCart;
    @FXML private TextField txtCustomerName;
    @FXML private Label lblTotal;

    private final ProductService productService = new ProductService();
    private final OrderService orderService = new OrderService();
    private final ObservableList<CartItemDto> cartItems = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        updateDateTime();
        loadProducts();
        setupCartTable();

        // Add "Add to Cart" button to each row
        addButtonToTable();
    }

    private void updateDateTime() {
        String dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        lblDateTime.setText(dateTime);
    }

    private void loadProducts() {
        ObservableList<ProductDto> products = FXCollections.observableArrayList(
                productService.getAllProducts()
        );
        tblProducts.setItems(products);
    }

    private void setupCartTable() {
        tblCart.setItems(cartItems);
    }

    private void addButtonToTable() {
        TableColumn<ProductDto, Void> actionCol = (TableColumn<ProductDto, Void>) tblProducts.getColumns().get(5);

        actionCol.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("Add to Cart");

            {
                btn.setOnAction(event -> {
                    ProductDto product = getTableView().getItems().get(getIndex());
                    addToCart(product);
                });
                btn.setStyle("-fx-background-color: #5BA3F5; -fx-text-fill: white;");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });
    }

    private void addToCart(ProductDto product) {
        if (product.getQuantity() <= 0) {
            showAlert("Out of Stock", "This product is out of stock!");
            return;
        }

        // Check if product already in cart
        for (CartItemDto item : cartItems) {
            if (item.getProductId().equals(product.getId())) {
                item.setQuantity(item.getQuantity() + 1);
                item.setTotal(item.getPrice() * item.getQuantity());
                tblCart.refresh();
                updateTotal();
                return;
            }
        }

        // Add new item to cart
        CartItemDto cartItem = new CartItemDto(
                product.getId(),
                product.getName(),
                1,
                product.getPrice()
        );
        cartItems.add(cartItem);
        updateTotal();
    }

    private void updateTotal() {
        double total = cartItems.stream()
                .mapToDouble(CartItemDto::getTotal)
                .sum();
        lblTotal.setText(String.format("$%.2f", total));
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
                                p.getCategory().toLowerCase().contains(query))
                        .toList()
        );
        tblProducts.setItems(filtered);
    }

    @FXML
    public void clearCart(ActionEvent event) {
        cartItems.clear();
        updateTotal();
    }

    @FXML
    public void processPayment(ActionEvent event) {
        if (cartItems.isEmpty()) {
            showAlert("Empty Cart", "Please add items to cart!");
            return;
        }

        String customerName = txtCustomerName.getText().trim();
        if (customerName.isEmpty()) {
            customerName = "Walk-in Customer";
        }

        // Create order
        double total = cartItems.stream().mapToDouble(CartItemDto::getTotal).sum();
        OrderDto orderDto = new OrderDto();
        orderDto.setTotalAmount(total);
        orderDto.setCustomerName(customerName);

        orderService.createOrder(orderDto);

        // Update product quantities
        for (CartItemDto item : cartItems) {
            ProductDto product = productService.getProductById(item.getProductId());
            product.setQuantity(product.getQuantity() - item.getQuantity());
            productService.updateProduct(product);
        }

        showAlert("Success", "Sale completed successfully!");
        clearCart(null);
        txtCustomerName.clear();
        loadProducts();
    }

    @FXML
    public void backToDashboard(ActionEvent event) {
        try {
            Stage stage = (Stage) lblTotal.getScene().getWindow();
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

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}