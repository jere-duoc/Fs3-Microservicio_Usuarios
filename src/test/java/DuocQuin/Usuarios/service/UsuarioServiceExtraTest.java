package DuocQuin.Usuarios.service;

import DuocQuin.Usuarios.model.Rol;
import DuocQuin.Usuarios.model.Usuario;
import DuocQuin.Usuarios.repository.SolicitudArcoRepository;
import DuocQuin.Usuarios.repository.UsuarioRepository;
import DuocQuin.Usuarios.utils.HashUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceExtraTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private SolicitudArcoRepository arcoRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario buildUsuario() {
        Usuario usuario = new Usuario();
        usuario.setRun("12345678-9");
        usuario.setCorreoElectronico("test@example.com");
        usuario.setTelefonoCelular("+56912345678");
        usuario.setContrasena("secret");
        usuario.setPrimerNombre("Juan");
        usuario.setPrimerApellido("Pérez");
        usuario.setFechaNacimiento(LocalDate.of(1990, 1, 1));
        usuario.setGenero("MASCULINO");
        usuario.setAceptoTerminos(true);
        usuario.setOposicion(false);
        usuario.setActivo(true);
        Rol rol = new Rol();
        rol.setNombreRol("USUARIO");
        usuario.setRol(rol);
        return usuario;
    }

    @Test
    void findAll_returnsList() {
        Usuario usuario = buildUsuario();
        when(usuarioRepository.findAll()).thenReturn(List.of(usuario));

        List<Usuario> result = usuarioService.findAll();

        assertEquals(1, result.size());
        assertEquals(usuario, result.get(0));
    }

    @Test
    void findByRun_whenNotFound_returnsEmptyList() {
        when(usuarioRepository.findByRunHash(HashUtil.sha256("12345678"))).thenReturn(Optional.empty());

        List<Usuario> result = usuarioService.findByRun("12345678-9");

        assertTrue(result.isEmpty());
    }

    @Test
    void deleteById_whenNotFound_throwsRuntimeException() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> usuarioService.deleteById(99L));
        assertTrue(ex.getMessage().contains("Usuario no encontrado con ID"));
    }

    @Test
    void save_whenCorreoElectronicoMissing_throwsRuntimeException() {
        Usuario usuario = buildUsuario();
        usuario.setCorreoElectronico(null);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> usuarioService.save(usuario));
        assertTrue(ex.getMessage().contains("El correo es obligatorio"));
    }

    @Test
    void update_whenSamePassword_doesNotEncodeAgain() {
        Usuario existing = buildUsuario();
        existing.setIdUsuario(1L);
        existing.setContrasena("hashedSecret");
        existing.setRunHash(HashUtil.sha256("12345678"));

        Usuario details = buildUsuario();
        details.setContrasena("secret");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(passwordEncoder.matches("secret", "hashedSecret")).thenReturn(true);
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Usuario updated = usuarioService.update(1L, details);

        assertEquals("hashedSecret", updated.getContrasena());
        verify(passwordEncoder, never()).encode(anyString());
    }
}
