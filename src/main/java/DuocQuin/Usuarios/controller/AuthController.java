package DuocQuin.Usuarios.controller;

import DuocQuin.Usuarios.dto.LoginRequest;
import DuocQuin.Usuarios.dto.LoginResponse;
import DuocQuin.Usuarios.model.Usuario;
import DuocQuin.Usuarios.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Optional<Usuario> usuarioOpt = usuarioService.login(
                loginRequest.getCorreoElectronico(), 
                loginRequest.getContrasena()
        );

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            
            // Generar un token ficticio (puedes implementar JWT después)
            String token = UUID.randomUUID().toString();
            
            LoginResponse response = LoginResponse.builder()
                    .id(usuario.getIdUsuario())
                    .token(token)
                    .nombre(usuario.getPrimerNombre())
                    .apellido(usuario.getPrimerApellido())
                    .rol(usuario.getRol() != null ? usuario.getRol().getNombreRol() : "Externo")
                    .correo(usuario.getCorreoElectronico())
                    .build();
            
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas");
        }
    }
}
