package DuocQuin.Usuarios.controller;

import DuocQuin.Usuarios.model.Usuario;
import DuocQuin.Usuarios.model.SolicitudArco;
import DuocQuin.Usuarios.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {
    
    @Autowired
    private UsuarioService usuarioService;
    
    @GetMapping
    public ResponseEntity<List<Usuario>> getAllUsuarios() {
        List<Usuario> usuarios = usuarioService.findAll();
        
        if (usuarios.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        
        return ResponseEntity.ok(usuarios);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> getUsuarioById(@PathVariable Long id) {
        Optional<Usuario> usuario = usuarioService.findById(id);
        
        return usuario.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<?> createUsuario(@Valid @RequestBody Usuario usuario) {
        try {
            Usuario nuevoUsuario = usuarioService.save(usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoUsuario);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUsuario(@PathVariable Long id, @Valid @RequestBody Usuario usuarioDetails) {
        try {
            Optional<Usuario> actualOpt = usuarioService.findById(id);
            Usuario usuarioActualizado = usuarioService.update(id, usuarioDetails);
            
            // Determinar qué derecho se ejerció
            if (actualOpt.isPresent()) {
                Usuario actual = actualOpt.get();
                if (!actual.getOposicion().equals(usuarioActualizado.getOposicion())) {
                    usuarioService.registrarSolicitudArco(usuarioActualizado, 
                        SolicitudArco.TipoDerecho.OPOSICION, 
                        "Cambio de estado de oposición a: " + usuarioActualizado.getOposicion());
                } else {
                    usuarioService.registrarSolicitudArco(usuarioActualizado, 
                        SolicitudArco.TipoDerecho.RECTIFICACION, 
                        "Actualización de datos personales (Rectificación).");
                }
            }
            
            return ResponseEntity.ok(usuarioActualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUsuario(@PathVariable Long id) {
        try {
            usuarioService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    
    @GetMapping("/run/{run}")
    public ResponseEntity<?> getUsuarioByRun(@PathVariable String run) {
        try {
            List<Usuario> usuarios = usuarioService.findByRun(run);
            return ResponseEntity.ok(usuarios);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    
    @GetMapping("/buscar")
    public ResponseEntity<List<Usuario>> buscarPorNombre(@RequestParam String nombre) {
        List<Usuario> usuarios = usuarioService.findByNombreContaining(nombre);
        
        if (usuarios.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        
        return ResponseEntity.ok(usuarios);
    }

    @PostMapping("/{id}/arco/acceso")
    public ResponseEntity<?> registrarAcceso(@PathVariable Long id) {
        try {
            return usuarioService.findById(id)
                .map(u -> {
                    usuarioService.registrarSolicitudArco(u, 
                        SolicitudArco.TipoDerecho.ACCESO, 
                        "El usuario descargó o consultó sus datos personales.");
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}