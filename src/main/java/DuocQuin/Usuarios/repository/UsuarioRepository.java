package DuocQuin.Usuarios.repository;

import DuocQuin.Usuarios.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    Optional<Usuario> findByRun(String run);
    
    List<Usuario> findByPrimerNombreContainingIgnoreCaseOrPrimerApellidoContainingIgnoreCase(String primerNombre, String primerApellido);
    
    boolean existsByRun(String run);
}
