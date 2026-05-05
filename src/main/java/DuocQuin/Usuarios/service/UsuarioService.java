package DuocQuin.Usuarios.service;


import DuocQuin.Usuarios.model.Usuario;
import DuocQuin.Usuarios.utils.HashUtil;

import DuocQuin.Usuarios.repository.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> findById(Long id) {
        return usuarioRepository.findById(id);
    }

    public Usuario save(Usuario usuario) {

        if(usuario.getRun()== null || !usuario.getRun().contains("-")){
            throw new RuntimeException("El run que ingreso no cumple con el formato,el formato debe ser 12345678-9");
        }
        if (usuario.getCorreoElectronico() == null || usuario.getCorreoElectronico().isEmpty()) {
        throw new RuntimeException("El correo es obligatorio");
        }

        if (usuario.getTelefonoCelular() == null || usuario.getTelefonoCelular().isEmpty()) {
            throw new RuntimeException("El telefono es obligatorio");
        }

        if (usuario.getContrasena() == null || usuario.getContrasena().isEmpty()) {
            throw new RuntimeException("La contraseña es obligatoria");
        }

        String[] partes = usuario.getRun().split("-"); //Separa el rut y el digito verificador para guardarlo por primera vez en la base
        String runOriginal = partes[0];
        String dvOriginal = partes[1];

        //Hash del run para validar que no se repita el run en la base de datos, ya que el run esta encriptado y no se puede comparar con el run original
        String runHash = HashUtil.sha256(runOriginal);

        if (usuarioRepository.existsByRunHash(runHash)) {
            throw new RuntimeException("Ya existe un usuario con el RUN: " + usuario.getRun());
        }

        //Encriptacion del Run y del digito verificador
        usuario.setRun(runOriginal);
        usuario.setDigitoVerificador(dvOriginal);
        usuario.setRunHash(runHash);

        String hash = passwordEncoder.encode(usuario.getContrasena());
        usuario.setContrasena(hash);

        return usuarioRepository.save(usuario);
    }

    public Usuario update(Long id, Usuario usuarioDetails) {

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
        
        //Valida que el rut que se ingresa para actualizar cumpla con el formato
        if (usuarioDetails.getRun()== null || !usuarioDetails.getRun().contains("-")) {
            throw new RuntimeException("Formato de run invalido");
        }
        //Separa del rut actualizado ,el run y el digito verificador
        String[] partes = usuarioDetails.getRun().split("-");
        String runOriginal = partes[0];
        String dvOriginal = partes[1];

        String runHash = HashUtil.sha256(runOriginal);

        if (!usuario.getRunHash().equals(runHash) &&
            usuarioRepository.existsByRunHash(runHash)) {
            throw new RuntimeException("Ya existe un usuario con ese RUN");
        }

        //Encriptacion del Run y del digito verificador cuando se actualiza el usuario
        usuario.setRun(runOriginal);
        usuario.setDigitoVerificador(dvOriginal);
        usuario.setRunHash(runHash);

        usuario.setPrimerNombre(usuarioDetails.getPrimerNombre());
        usuario.setSegundoNombre(usuarioDetails.getSegundoNombre());
        usuario.setPrimerApellido(usuarioDetails.getPrimerApellido());
        usuario.setSegundoApellido(usuarioDetails.getSegundoApellido());
        usuario.setFechaNacimiento(usuarioDetails.getFechaNacimiento());
        usuario.setGenero(usuarioDetails.getGenero());

        if (usuarioDetails.getCorreoElectronico() != null) {
        usuario.setCorreoElectronico(usuarioDetails.getCorreoElectronico());
        }

        if (usuarioDetails.getTelefonoCelular() != null) {
            usuario.setTelefonoCelular(usuarioDetails.getTelefonoCelular());
        }
        
        if (usuarioDetails.getContrasena() != null && !usuarioDetails.getContrasena().isEmpty()) {
            String hash = passwordEncoder.encode(usuarioDetails.getContrasena());
            usuario.setContrasena(hash);
        }


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

        if(run == null || !run.contains("-")){
            throw new RuntimeException("Formato de run invalido");
        }

        // Separa para buscar el run en la base de datos
        String[] partes = run.split("-");
        String runHash = HashUtil.sha256(partes[0]);

        return usuarioRepository.findByRunHash(runHash)
                .map(List::of)
                .orElse(List.of());
    }

    public List<Usuario> findByNombreContaining(String nombre) {
        return usuarioRepository
                .findByPrimerNombreContainingIgnoreCaseOrPrimerApellidoContainingIgnoreCase(nombre, nombre);
    }
}
