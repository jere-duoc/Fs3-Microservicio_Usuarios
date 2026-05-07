package DuocQuin.Usuarios.service;


import DuocQuin.Usuarios.model.Usuario;
import DuocQuin.Usuarios.utils.HashUtil;

import DuocQuin.Usuarios.repository.UsuarioRepository;
import DuocQuin.Usuarios.repository.SolicitudArcoRepository;
import DuocQuin.Usuarios.model.SolicitudArco;

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

    @Autowired
    private SolicitudArcoRepository arcoRepository;

    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> findById(Long id) {
        return usuarioRepository.findById(id);
    }

    public Usuario save(Usuario usuario) {
        String runOriginal;
        String dvOriginal;

        if (usuario.getRun() != null && usuario.getRun().contains("-")) {
            // Formato con guión: "12345678-9"
            String[] partes = usuario.getRun().split("-");
            runOriginal = partes[0];
            dvOriginal = partes[1];
        } else if (usuario.getRun() != null && usuario.getDigitoVerificador() != null) {
            // Formato separado: run="12345678", digitoVerificador="9"
            runOriginal = usuario.getRun();
            dvOriginal = usuario.getDigitoVerificador();
        } else {
            throw new RuntimeException("El RUN es obligatorio y debe tener un formato válido (12345678-9 o campos separados)");
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

        // Hash del run para validar que no se repita
        String runHash = HashUtil.sha256(runOriginal);

        if (usuarioRepository.existsByRunHash(runHash)) {
            throw new RuntimeException("Ya existe un usuario con el RUN: " + runOriginal + "-" + dvOriginal);
        }

        // Encriptacion del Run y del digito verificador
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
        
        String runOriginal;
        String dvOriginal;

        if (usuarioDetails.getRun() != null && usuarioDetails.getRun().contains("-")) {
            String[] partes = usuarioDetails.getRun().split("-");
            runOriginal = partes[0];
            dvOriginal = partes[1];
        } else if (usuarioDetails.getRun() != null && usuarioDetails.getDigitoVerificador() != null) {
            runOriginal = usuarioDetails.getRun();
            dvOriginal = usuarioDetails.getDigitoVerificador();
        } else {
            throw new RuntimeException("Formato de RUN inválido");
        }

        String runHash = HashUtil.sha256(runOriginal);

        if (!usuario.getRunHash().equals(runHash) &&
            usuarioRepository.existsByRunHash(runHash)) {
            throw new RuntimeException("Ya existe un usuario con ese RUN");
        }

        // Encriptacion del Run y del digito verificador cuando se actualiza el usuario
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
            // Solo encriptar si es una nueva contraseña (no es igual al hash actual)
            if (!passwordEncoder.matches(usuarioDetails.getContrasena(), usuario.getContrasena())) {
                String hash = passwordEncoder.encode(usuarioDetails.getContrasena());
                usuario.setContrasena(hash);
            }
        }


        usuario.setAceptoTerminos(usuarioDetails.getAceptoTerminos());
        usuario.setOposicion(usuarioDetails.getOposicion());
        
        if (usuarioDetails.getRol() != null) {
            usuario.setRol(usuarioDetails.getRol());
        }

        return usuarioRepository.save(usuario);
    }

    public void deleteById(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
        
        // Borrado lógico (Ley ARCO - Derecho de Cancelación)
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
        
        registrarSolicitudArco(usuario, SolicitudArco.TipoDerecho.CANCELACION, 
            "El usuario ha ejercido su derecho de cancelación (borrado lógico).");
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

    public Optional<Usuario> login(String identifier, String password) {
        Optional<Usuario> usuarioOpt;

        // Intentar buscar por RUT si tiene el formato
        if (identifier.contains("-")) {
            String[] partes = identifier.split("-");
            String runHash = HashUtil.sha256(partes[0]);
            usuarioOpt = usuarioRepository.findByRunHash(runHash);
        } else {
            // Intentar buscar por correo electrónico
            usuarioOpt = usuarioRepository.findByCorreoElectronico(identifier);
        }

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            if (!usuario.getActivo()) {
                throw new RuntimeException("Cuenta desactivada. Contacte al administrador.");
            }
            if (passwordEncoder.matches(password, usuario.getContrasena())) {
                return Optional.of(usuario);
            }
        }

        return Optional.empty();
    }
    public void registrarSolicitudArco(Usuario usuario, SolicitudArco.TipoDerecho tipo, String detalles) {
        SolicitudArco solicitud = new SolicitudArco();
        solicitud.setUsuario(usuario);
        solicitud.setTipoDerecho(tipo);
        solicitud.setDetalles(detalles);
        arcoRepository.save(solicitud);
    }
}
