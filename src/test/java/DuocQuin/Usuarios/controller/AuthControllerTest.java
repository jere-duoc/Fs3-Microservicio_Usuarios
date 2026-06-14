package DuocQuin.Usuarios.controller;

import DuocQuin.Usuarios.dto.LoginRequest;
import DuocQuin.Usuarios.dto.LoginResponse;
import DuocQuin.Usuarios.model.Rol;
import DuocQuin.Usuarios.model.Usuario;
import DuocQuin.Usuarios.service.UsuarioService;
import DuocQuin.Usuarios.utils.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthController authController;

    @Test
    void login_withValidCredentials_returnsLoginResponse() {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(5L);
        usuario.setCorreoElectronico("login@example.com");
        usuario.setPrimerNombre("Ana");
        usuario.setPrimerApellido("García");
        Rol rol = new Rol();
        rol.setNombreRol("USUARIO");
        usuario.setRol(rol);

        when(usuarioService.login("login@example.com", "password")).thenReturn(Optional.of(usuario));
        when(jwtService.generateToken(usuario)).thenReturn("jwt-token");

        ResponseEntity<?> response = authController.login(new LoginRequest("login@example.com", "password"));

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof LoginResponse);

        LoginResponse body = (LoginResponse) response.getBody();
        assertEquals(5L, body.getId());
        assertEquals("jwt-token", body.getToken());
        assertEquals("Ana", body.getNombre());
        assertEquals("García", body.getApellido());
        assertEquals("USUARIO", body.getRol());
        assertEquals("login@example.com", body.getCorreo());
    }

    @Test
    void login_withInvalidCredentials_returnsUnauthorized() {
        when(usuarioService.login("bad@example.com", "badpass")).thenReturn(Optional.empty());

        ResponseEntity<?> response = authController.login(new LoginRequest("bad@example.com", "badpass"));

        assertEquals(401, response.getStatusCodeValue());
        assertEquals("Credenciales inválidas", response.getBody());
    }
}
