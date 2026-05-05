package DuocQuin.Usuarios.service;

import DuocQuin.Usuarios.model.Usuario;
import DuocQuin.Usuarios.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UsuarioService {
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }
    
    public Optional<Usuario> findById(Long id) {
        return usuarioRepository.findById(id);
    }
    
    public Usuario save(Usuario usuario) {
        if (usuarioRepository.existsByRun(usuario.getRun())) {
            throw new RuntimeException("Ya existe un usuario con el RUN: " + usuario.getRun());
        }
        return usuarioRepository.save(usuario);
    }
    
    public Usuario update(Long id, Usuario usuarioDetails) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
        
        if (!usuario.getRun().equals(usuarioDetails.getRun()) && 
            usuarioRepository.existsByRun(usuarioDetails.getRun())) {
            throw new RuntimeException("Ya existe un usuario con el RUN: " + usuarioDetails.getRun());
        }
        usuario.setRun(usuarioDetails.getRun());
        usuario.setDigitoVerificador(usuarioDetails.getDigitoVerificador());
        usuario.setPrimerNombre(usuarioDetails.getPrimerNombre());
        usuario.setSegundoNombre(usuarioDetails.getSegundoNombre());
        usuario.setPrimerApellido(usuarioDetails.getPrimerApellido());
        usuario.setSegundoApellido(usuarioDetails.getSegundoApellido());
        usuario.setFechaNacimiento(usuarioDetails.getFechaNacimiento());
        usuario.setGenero(usuarioDetails.getGenero());
        usuario.setCorreoElectronico(usuarioDetails.getCorreoElectronico());
        usuario.setContrasena(usuarioDetails.getContrasena());
        usuario.setTelefonoCelular(usuarioDetails.getTelefonoCelular());
        usuario.setAceptoTerminos(usuarioDetails.getAceptoTerminos());
        usuario.setRol(usuarioDetails.getRol());
        
        return usuarioRepository.save(usuario);
    }
    
    public void deleteById(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Usuario no encontrado con ID: " + id);
        }
        usuarioRepository.deleteById(id);
    }
    
    public List<Usuario> findByRun(String run) {
        return usuarioRepository.findByRun(run).map(List::of).orElse(List.of());
    }
    
    public List<Usuario> findByNombreContaining(String nombre) {
        return usuarioRepository.findByPrimerNombreContainingIgnoreCaseOrPrimerApellidoContainingIgnoreCase(nombre, nombre);
    }
}
