package dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupplierDto {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String address;
    private Boolean active;
}