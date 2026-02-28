package entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "employees")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 50)
    private String position;

    @Column(length = 100)
    private String email;

    @Column(length = 20)
    private String phone;

    @Column
    private Double salary;

    @Column(nullable = false)
    private Boolean active = true;
}

