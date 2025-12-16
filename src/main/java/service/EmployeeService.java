package service;

import dto.EmployeeDto;
import entity.Employee;
import repository.EmployeeRepository;
import java.util.List;
import java.util.stream.Collectors;

public class EmployeeService {

    private final EmployeeRepository employeeRepository = new EmployeeRepository();

    public void addEmployee(EmployeeDto dto) {
        Employee employee = new Employee();
        employee.setName(dto.getName());
        employee.setPosition(dto.getPosition());
        employee.setEmail(dto.getEmail());
        employee.setPhone(dto.getPhone());
        employee.setSalary(dto.getSalary());
        employee.setActive(true);

        employeeRepository.save(employee);
    }

    public List<EmployeeDto> getAllEmployees() {
        return employeeRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public EmployeeDto getEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(id);
        return employee != null ? convertToDto(employee) : null;
    }

    public void updateEmployee(EmployeeDto dto) {
        Employee employee = new Employee();
        employee.setId(dto.getId());
        employee.setName(dto.getName());
        employee.setPosition(dto.getPosition());
        employee.setEmail(dto.getEmail());
        employee.setPhone(dto.getPhone());
        employee.setSalary(dto.getSalary());
        employee.setActive(dto.getActive());

        employeeRepository.update(employee);
    }

    public void deleteEmployee(Long id) {
        employeeRepository.delete(id);
    }

    private EmployeeDto convertToDto(Employee employee) {
        return new EmployeeDto(
                employee.getId(),
                employee.getName(),
                employee.getPosition(),
                employee.getEmail(),
                employee.getPhone(),
                employee.getSalary(),
                employee.getActive()
        );
    }
}