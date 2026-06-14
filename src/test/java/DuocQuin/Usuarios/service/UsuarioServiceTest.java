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
class UsuarioServiceTest {

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
        usuario.setRol(new Rol());
        usuario.getRol().setNombreRol("USUARIO");
        return usuario;
    }

    @Test
    void save_validUsuario_shouldEncodePasswordAndSave() {
        Usuario usuario = buildUsuario();
        String expectedHash = HashUtil.sha256("12345678");

        when(usuarioRepository.existsByRunHash(expectedHash)).thenReturn(false);
        when(passwordEncoder.encode("secret")).thenReturn("hashedSecret");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Usuario saved = usuarioService.save(usuario);

        assertEquals("hashedSecret", saved.getContrasena());
        assertEquals(expectedHash, saved.getRunHash());
        assertEquals("12345678", saved.getRun());
        assertEquals("9", saved.getDigitoVerificador());
        verify(usuarioRepository).save(saved);
    }

    @Test
    void save_missingRun_shouldThrow() {
        Usuario usuario = buildUsuario();
        usuario.setRun(null);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> usuarioService.save(usuario));
        assertTrue(ex.getMessage().contains("RUN es obligatorio"));
    }

    @Test
    void update_withNewPassword_shouldEncodeNewPassword() {
        Usuario existing = buildUsuario();
        existing.setIdUsuario(1L);
        existing.setContrasena("oldHash");
        existing.setRunHash(HashUtil.sha256("12345678"));

        Usuario details = buildUsuario();
        details.setContrasena("newSecret");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(passwordEncoder.matches("newSecret", "oldHash")).thenReturn(false);
        when(passwordEncoder.encode("newSecret")).thenReturn("newHash");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Usuario updated = usuarioService.update(1L, details);

        assertEquals("newHash", updated.getContrasena());
        assertEquals(HashUtil.sha256("12345678"), updated.getRunHash());
        verify(passwordEncoder).encode("newSecret");
    }

    @Test
    void login_withValidRunAndPassword_shouldReturnUsuario() {
        Usuario usuario = buildUsuario();
        usuario.setIdUsuario(2L);
        usuario.setRunHash(HashUtil.sha256("12345678"));
        usuario.setContrasena("hashValue");
        usuario.setActivo(true);

        when(usuarioRepository.findByRunHash(HashUtil.sha256("12345678")))
                .thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("secret", "hashValue")).thenReturn(true);

        Optional<Usuario> result = usuarioService.login("12345678-9", "secret");

        assertTrue(result.isPresent());
        assertEquals(2L, result.get().getIdUsuario());
    }

    @Test
    void login_withDisabledAccount_shouldThrow() {
        Usuario usuario = buildUsuario();
        usuario.setIdUsuario(2L);
        usuario.setRunHash(HashUtil.sha256("12345678"));
        usuario.setContrasena("hashValue");
        usuario.setActivo(false);

        when(usuarioRepository.findByRunHash(HashUtil.sha256("12345678")))
                .thenReturn(Optional.of(usuario));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> usuarioService.login("12345678-9", "secret"));
        assertTrue(ex.getMessage().contains("Cuenta desactivada"));
    }

    @Test
    void deleteById_shouldRegisterSolicitudArcoAndDeleteUsuario() {
        Usuario usuario = buildUsuario();
        usuario.setIdUsuario(3L);

        when(usuarioRepository.findById(3L)).thenReturn(Optional.of(usuario));

        usuarioService.deleteById(3L);

        verify(arcoRepository).save(any());
        verify(arcoRepository).deleteByUsuarioIdUsuario(3L);
        verify(usuarioRepository).deleteById(3L);
    }

    @Test
    void findByRun_withInvalidFormat_shouldThrow() {
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> usuarioService.findByRun("123456789"));
        assertTrue(ex.getMessage().contains("Formato de run invalido"));
    }

    @Test
    void findByNombreContaining_shouldReturnList() {
        Usuario usuario = buildUsuario();
        when(usuarioRepository.findByPrimerNombreContainingIgnoreCaseOrPrimerApellidoContainingIgnoreCase("Juan", "Juan"))
                .thenReturn(List.of(usuario));

        List<Usuario> result = usuarioService.findByNombreContaining("Juan");

        assertFalse(result.isEmpty());
        assertEquals("Juan", result.get(0).getPrimerNombre());
    }
}
