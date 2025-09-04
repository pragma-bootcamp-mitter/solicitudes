package co.com.pragma.bootcamp.model.token;

import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Token {
    private String subject;
    private String role;
    private List<String> permissions;
    private String accessToken;
}
