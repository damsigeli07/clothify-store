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
import javafx.scene.control.cell.PropertyValueFactory;
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
        setupProductTable();
        loadProducts();
        setupCartTable();

        // Add "Add to Cart" button to each row
        addButtonToTable();

        // Update datetime every second
        startDateTimeUpdater();
    }

    private void setupProductTable() {
        // Ensure columns are properly set up
        TableColumn<ProductDto, Integer> idCol = (TableColumn<ProductDto, Integer>) tblProducts.getColumns().get(0);
        TableColumn<ProductDto, String> nameCol = (TableColumn<ProductDto, String>) tblProducts.getColumns().get(1);
        TableColumn<ProductDto, String> categoryCol = (TableColumn<ProductDto, String>) tblProducts.getColumns().get(2);
        TableColumn<ProductDto, Double> priceCol = (TableColumn<ProductDto, Double>) tblProducts.getColumns().get(3);
        TableColumn<ProductDto, Integer> stockCol = (TableColumn<ProductDto, Integer>) tblProducts.getColumns().get(4);

        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        stockCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        // Format price column
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

        // Color-code stock column
        stockCol.setCellFactory(col -> new TableCell<ProductDto, Integer>() {
            @Override
            protected void updateItem(Integer stock, boolean empty) {
                super.updateItem(stock, empty);
                if (empty || stock == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(String.valueOf(stock));
                    if (stock < 10) {
                        setStyle("-fx-text-fill: #EF4444; -fx-font-weight: bold;"); // Red for low stock
                    } else if (stock < 20) {
                        setStyle("-fx-text-fill: #F59E0B;"); // Orange for medium stock
                    } else {
                        setStyle("-fx-text-fill: #10B981;"); // Green for high stock
                    }
                }
            }
        });
    }

    private void updateDateTime() {
        String dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMMM d, yyyy - hh:mm a"));
        lblDateTime.setText(dateTime);
    }

    private void startDateTimeUpdater() {
        // Update date/time every second
        Thread thread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000);
                    javafx.application.Platform.runLater(this::updateDateTime);
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    private void loadProducts() {
        ObservableList<ProductDto> products = FXCollections.observableArrayList(
                productService.getAllProducts()
        );
        tblProducts.setItems(products);
    }

    private void setupCartTable() {
        tblCart.setItems(cartItems);

        // Setup cart table columns
        TableColumn<CartItemDto, String> nameCol = (TableColumn<CartItemDto, String>) tblCart.getColumns().get(0);
        TableColumn<CartItemDto, Integer> qtyCol = (TableColumn<CartItemDto, Integer>) tblCart.getColumns().get(1);
        TableColumn<CartItemDto, Double> priceCol = (TableColumn<CartItemDto, Double>) tblCart.getColumns().get(2);
        TableColumn<CartItemDto, Double> totalCol = (TableColumn<CartItemDto, Double>) tblCart.getColumns().get(3);

        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        qtyCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        totalCol.setCellValueFactory(new PropertyValueFactory<>("total"));

        // Format price columns
        priceCol.setCellFactory(col -> new TableCell<CartItemDto, Double>() {
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

        totalCol.setCellFactory(col -> new TableCell<CartItemDto, Double>() {
            @Override
            protected void updateItem(Double total, boolean empty) {
                super.updateItem(total, empty);
                if (empty || total == null) {
                    setText(null);
                } else {
                    setText(String.format("$%.2f", total));
                }
            }
        });
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
                btn.setStyle("-fx-background-color: linear-gradient(to right, #6366F1, #8B5CF6); " +
                        "-fx-text-fill: white; -fx-background-radius: 8; " +
                        "-fx-font-weight: bold; -fx-cursor: hand;");
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
                if (item.getQuantity() >= product.getQuantity()) {
                    showAlert("Not Enough Stock", "Only " + product.getQuantity() + " items available!");
                    return;
                }
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
        if (cartItems.isEmpty()) {
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Clear Cart");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Clear all items from cart?");

        if (confirmAlert.showAndWait().get() == ButtonType.OK) {
            cartItems.clear();
            updateTotal();
        }
    }

    @FXML
    public void processPayment(ActionEvent event) {
        if (cartItems.isEmpty()) {
            showAlert("Empty Cart", "Please add items to cart!");
            return;
        }

        String customerName = txtCustomerName.getText().trim();
        if (customerName.isEmpty()) {
            showAlert("Customer Name Required", "Please enter customer name!");
            return;
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

        showAlert("Success", String.format("Sale completed!\nCustomer: %s\nTotal: $%.2f\n\nThank you for your purchase!",
                customerName, total));
        clearCartWithoutConfirmation();
        txtCustomerName.clear();
        loadProducts();
    }

    private void clearCartWithoutConfirmation() {
        cartItems.clear();
        updateTotal();
    }

    @FXML
    public void backToDashboard(ActionEvent event) {
        if (!cartItems.isEmpty()) {
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirm");
            confirmAlert.setHeaderText(null);
            confirmAlert.setContentText("You have items in cart. Are you sure you want to go back?");

            if (confirmAlert.showAndWait().get() != ButtonType.OK) {
                return;
            }
        }

        try {
            Stage stage = (Stage) lblTotal.getScene().getWindow();
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