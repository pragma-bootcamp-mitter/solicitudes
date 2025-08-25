package co.com.pragma.bootcamp.api;

import co.com.pragma.bootcamp.api.webclient.AuthClient;
import co.com.pragma.bootcamp.model.user.User;
import co.com.pragma.bootcamp.model.user.gateways.AuthRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class AuthRepositoryAdapter implements AuthRepository {

    private final AuthClient client;

    public AuthRepositoryAdapter(AuthClient client) {
        this.client = client;
    }

    @Override
    public Mono<User> getUserByDocumento(String documento) {
        return client.getUserByDocumento(documento)
                .map(dto -> User.builder()
                        .id(dto.getId())
                        .documentoIdentidad(dto.getDocumentoIdentidad())
                        .nombres(dto.getNombres())
                        .apellidos(dto.getApellidos())
                        .fechaNacimiento(dto.getFechaNacimiento())
                        .direccion(dto.getDireccion())
                        .telefono(dto.getTelefono())
                        .correoElectronico(dto.getCorreoElectronico())
                        .salarioBase(dto.getSalarioBase())
                        .build());
    }
}
