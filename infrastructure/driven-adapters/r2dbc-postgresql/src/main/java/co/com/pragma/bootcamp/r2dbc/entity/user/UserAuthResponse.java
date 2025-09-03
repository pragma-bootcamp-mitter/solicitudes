package co.com.pragma.bootcamp.r2dbc.entity.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAuthResponse {
    private String code;
    private String message;
    private String title;
    private UserAuth data;
}