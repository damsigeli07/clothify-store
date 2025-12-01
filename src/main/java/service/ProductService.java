package service;

import dto.ProductDto;
import entity.Product;
import repository.ProductRepository;
import java.util.List;
import java.util.stream.Collectors;

public class ProductService {

    private final ProductRepository productRepository = new ProductRepository();

    public void addProduct(ProductDto dto) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setCategory(dto.getCategory());
        product.setPrice(dto.getPrice());
        product.setQuantity(dto.getQuantity());
        product.setSupplier(dto.getSupplier());

        productRepository.save(product);
    }

    public List<ProductDto> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public ProductDto getProductById(Long id) {
        Product product = productRepository.findById(id);
        return product != null ? convertToDto(product) : null;
    }

    public void updateProduct(ProductDto dto) {
        Product product = new Product();
        product.setId(dto.getId());
        product.setName(dto.getName());
        product.setCategory(dto.getCategory());
        product.setPrice(dto.getPrice());
        product.setQuantity(dto.getQuantity());
        product.setSupplier(dto.getSupplier());

        productRepository.update(product);
    }

    public void deleteProduct(Long id) {
        productRepository.delete(id);
    }

    public List<ProductDto> getLowStockProducts() {
        return productRepository.findAll().stream()
                .filter(p -> p.getQuantity() < 10)
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private ProductDto convertToDto(Product product) {
        return new ProductDto(
                product.getId(),
                product.getName(),
                product.getCategory(),
                product.getPrice(),
                product.getQuantity(),
                product.getSupplier()
        );
    }
}