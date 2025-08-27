package co.com.pragma.bootcamp.api.mapper;

import co.com.pragma.bootcamp.api.dto.UsuarioAuth;
import co.com.pragma.bootcamp.model.user.Usuario;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MapeadorAuthUsuario {
    Usuario aDominio(UsuarioAuth dto);
}
