package DuocQuin.Usuarios.controller;

import DuocQuin.Usuarios.model.Rol;
import DuocQuin.Usuarios.model.SolicitudArco;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {

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
    void getAllUsuarios_whenEmpty_returnsNoContent() {
        when(usuarioService.findAll()).thenReturn(List.of());

        ResponseEntity<List<Usuario>> response = usuarioController.getAllUsuarios();

        assertEquals(204, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void getUsuarioById_whenFound_returnsUsuario() {
        Usuario usuario = buildUsuario();
        when(usuarioService.findById(1L)).thenReturn(Optional.of(usuario));

        ResponseEntity<Usuario> response = usuarioController.getUsuarioById(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(usuario, response.getBody());
    }

    @Test
    void createUsuario_whenValid_returnsCreated() {
        Usuario usuario = buildUsuario();
        when(usuarioService.save(usuario)).thenReturn(usuario);

        ResponseEntity<?> response = usuarioController.createUsuario(usuario);

        assertEquals(201, response.getStatusCodeValue());
        assertEquals(usuario, response.getBody());
    }

    @Test
    void createUsuario_whenServiceThrows_returnsBadRequest() {
        Usuario usuario = buildUsuario();
        when(usuarioService.save(usuario)).thenThrow(new RuntimeException("Datos inválidos"));

        ResponseEntity<?> response = usuarioController.createUsuario(usuario);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Datos inválidos", response.getBody());
    }

    @Test
    void updateUsuario_whenFound_registersRectificacionAndReturnsOk() {
        Usuario usuarioActual = buildUsuario();
        Usuario usuarioActualizado = buildUsuario();
        usuarioActualizado.setPrimerNombre("Cambio");

        when(usuarioService.findById(1L)).thenReturn(Optional.of(usuarioActual));
        when(usuarioService.update(eq(1L), any(Usuario.class))).thenReturn(usuarioActualizado);

        ResponseEntity<?> response = usuarioController.updateUsuario(1L, usuarioActualizado);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(usuarioActualizado, response.getBody());
        verify(usuarioService).registrarSolicitudArco(usuarioActualizado, SolicitudArco.TipoDerecho.RECTIFICACION,
                "Actualización de datos personales (Rectificación).");
    }

    @Test
    void deleteUsuario_whenDeleted_returnsNoContent() {
        doNothing().when(usuarioService).deleteById(1L);

        ResponseEntity<?> response = usuarioController.deleteUsuario(1L);

        assertEquals(204, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void getUsuarioByRun_whenInvalid_returnsBadRequest() {
        when(usuarioService.findByRun("123456789")).thenThrow(new RuntimeException("Formato de run invalido"));

        ResponseEntity<?> response = usuarioController.getUsuarioByRun("123456789");

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Formato de run invalido", response.getBody());
    }

    @Test
    void buscarPorNombre_whenFound_returnsUsuarios() {
        Usuario usuario = buildUsuario();
        when(usuarioService.findByNombreContaining("Juan")).thenReturn(List.of(usuario));

        ResponseEntity<List<Usuario>> response = usuarioController.buscarPorNombre("Juan");

        assertEquals(200, response.getStatusCodeValue());
        assertFalse(response.getBody().isEmpty());
    }

    @Test
    void registrarAcceso_whenUsuarioExists_returnsOk() {
        Usuario usuario = buildUsuario();
        when(usuarioService.findById(1L)).thenReturn(Optional.of(usuario));

        ResponseEntity<?> response = usuarioController.registrarAcceso(1L);

        assertEquals(200, response.getStatusCodeValue());
        verify(usuarioService).registrarSolicitudArco(eq(usuario), eq(SolicitudArco.TipoDerecho.ACCESO), anyString());
    }
}
