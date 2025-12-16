package service;

import dto.SupplierDto;
import entity.Supplier;
import repository.SupplierRepository;
import java.util.List;
import java.util.stream.Collectors;

public class SupplierService {

    private final SupplierRepository supplierRepository = new SupplierRepository();

    public void addSupplier(SupplierDto dto) {
        Supplier supplier = new Supplier();
        supplier.setName(dto.getName());
        supplier.setEmail(dto.getEmail());
        supplier.setPhone(dto.getPhone());
        supplier.setAddress(dto.getAddress());
        supplier.setActive(true);

        supplierRepository.save(supplier);
    }

    public List<SupplierDto> getAllSuppliers() {
        return supplierRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public SupplierDto getSupplierById(Long id) {
        Supplier supplier = supplierRepository.findById(id);
        return supplier != null ? convertToDto(supplier) : null;
    }

    public void updateSupplier(SupplierDto dto) {
        Supplier supplier = new Supplier();
        supplier.setId(dto.getId());
        supplier.setName(dto.getName());
        supplier.setEmail(dto.getEmail());
        supplier.setPhone(dto.getPhone());
        supplier.setAddress(dto.getAddress());
        supplier.setActive(dto.getActive());

        supplierRepository.update(supplier);
    }

    public void deleteSupplier(Long id) {
        supplierRepository.delete(id);
    }

    private SupplierDto convertToDto(Supplier supplier) {
        return new SupplierDto(
                supplier.getId(),
                supplier.getName(),
                supplier.getEmail(),
                supplier.getPhone(),
                supplier.getAddress(),
                supplier.getActive()
        );
    }
}