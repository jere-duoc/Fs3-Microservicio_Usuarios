package DuocQuin.Usuarios.utils;

import DuocQuin.Usuarios.model.Rol;
import DuocQuin.Usuarios.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "jwtSecret", "01234567890123456789012345678901");
        ReflectionTestUtils.setField(jwtService, "jwtExpirationMs", 3600000L);
    }

    @Test
    void generateToken_andValidateToken_shouldReturnTrue() {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1L);
        usuario.setCorreoElectronico("test@example.com");
        Rol rol = new Rol();
        rol.setNombreRol("ADMIN");
        usuario.setRol(rol);

        String token = jwtService.generateToken(usuario);

        assertNotNull(token);
        assertTrue(jwtService.validateToken(token));
        assertEquals("1", jwtService.getSubject(token));
        assertEquals("test@example.com", jwtService.getClaims(token).get("email", String.class));
        assertEquals("ADMIN", jwtService.getClaims(token).get("role", String.class));
    }

    @Test
    void validateToken_withMalformedToken_shouldReturnFalse() {
        assertFalse(jwtService.validateToken("invalid.token.value"));
    }
}
