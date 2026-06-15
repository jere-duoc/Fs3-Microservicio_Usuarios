package DuocQuin.Usuarios.controller;

import DuocQuin.Usuarios.model.Rol;
import DuocQuin.Usuarios.model.Usuario;
import DuocQuin.Usuarios.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioControllerExtraTest {

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private UsuarioController usuarioController;

    private Usuario buildUsuario() {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1L);
        usuario.setRun("12345678-9");
        usuario.setDigitoVerificador("9");
        usuario.setCorreoElectronico("test@example.com");
        usuario.setTelefonoCelular("+56912345678");
        usuario.setContrasena("secret");
        usuario.setPrimerNombre("Juan");
        usuario.setPrimerApellido("Pérez");
        usuario.setAceptoTerminos(true);
        usuario.setOposicion(false);
        usuario.setActivo(true);
        Rol rol = new Rol();
        rol.setNombreRol("USUARIO");
        usuario.setRol(rol);
        return usuario;
    }

    @Test
    void getAllUsuarios_whenNotEmpty_returnsOk() {
        Usuario usuario = buildUsuario();
        when(usuarioService.findAll()).thenReturn(List.of(usuario));

        ResponseEntity<List<Usuario>> response = usuarioController.getAllUsuarios();

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void getUsuarioById_whenNotFound_returnsNotFound() {
        when(usuarioService.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<Usuario> response = usuarioController.getUsuarioById(1L);

        assertEquals(404, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void updateUsuario_whenNotFound_returnsBadRequest() {
        Usuario usuario = buildUsuario();
        when(usuarioService.findById(1L)).thenReturn(Optional.empty());
        when(usuarioService.update(eq(1L), any(Usuario.class))).thenThrow(new RuntimeException("Usuario no encontrado con ID: 1"));

        ResponseEntity<?> response = usuarioController.updateUsuario(1L, usuario);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Usuario no encontrado con ID: 1", response.getBody());
    }

    @Test
    void deleteUsuario_whenNotFound_returnsNotFound() {
        doThrow(new RuntimeException("Usuario no encontrado con ID: 3")).when(usuarioService).deleteById(3L);

        ResponseEntity<?> response = usuarioController.deleteUsuario(3L);

        assertEquals(404, response.getStatusCodeValue());
        assertEquals("Usuario no encontrado con ID: 3", response.getBody());
    }

    @Test
    void buscarPorNombre_whenEmpty_returnsNoContent() {
        when(usuarioService.findByNombreContaining("Juan")).thenReturn(List.of());

        ResponseEntity<List<Usuario>> response = usuarioController.buscarPorNombre("Juan");

        assertEquals(204, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void registrarAcceso_whenUsuarioNotFound_returnsNotFound() {
        when(usuarioService.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = usuarioController.registrarAcceso(1L);

        assertEquals(404, response.getStatusCodeValue());
        assertNull(response.getBody());
    }
}
