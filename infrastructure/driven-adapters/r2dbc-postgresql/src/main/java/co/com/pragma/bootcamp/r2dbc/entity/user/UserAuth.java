package co.com.pragma.bootcamp.r2dbc.entity.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAuth {
    private String id;
    private String identificationDocument;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String address;
    private String phoneNumber;
    private String email;
    private BigDecimal baseSalary;
    private String password;
    private Integer roleId;
}
