package cl.andina.sgma.service;

import cl.andina.sgma.model.Usuario;
import cl.andina.sgma.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;

    /**
     * Valida credenciales contra PostgreSQL.
     * Hito 1: comparación directa (demo). Mejora futura: BCrypt.
     */
    public Optional<Usuario> login(String correo, String password) {
        if (correo == null || correo.isBlank() || password == null) {
            return Optional.empty();
        }
        return usuarioRepository.findByCorreoIgnoreCase(correo.trim())
                .filter(usuario -> usuario.getPassword().equals(password));
    }
}
