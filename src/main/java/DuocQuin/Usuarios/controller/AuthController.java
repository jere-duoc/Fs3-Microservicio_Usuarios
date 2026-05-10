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
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            System.out.println("Intento de login para: " + loginRequest.getCorreoElectronico());
            
            Optional<Usuario> usuarioOpt = usuarioService.login(
                    loginRequest.getCorreoElectronico(), 
                    loginRequest.getContrasena()
            );

            if (usuarioOpt.isPresent()) {
                Usuario usuario = usuarioOpt.get();
                System.out.println("Usuario encontrado: " + usuario.getCorreoElectronico());
                
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
                System.out.println("Credenciales inválidas para: " + loginRequest.getCorreoElectronico());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas");
            }
        } catch (Exception e) {
            System.err.println("Error en login: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno: " + e.getMessage());
        }
    }
}
