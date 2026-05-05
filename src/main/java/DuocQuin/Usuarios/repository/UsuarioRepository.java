package DuocQuin.Usuarios.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import DuocQuin.Usuarios.model.Usuario;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    // Métodos personalizados para buscar por run o por hash del run y para buscar por nombre o apellido

    Optional<Usuario> findByRun(String run);
    boolean existsByRun(String run);

    boolean existsByRunHash(String runHash);

    Optional<Usuario> findByRunHash(String runHash);

    List<Usuario> findByPrimerNombreContainingIgnoreCaseOrPrimerApellidoContainingIgnoreCase(
        String primerNombre , String primerApellido
    );
}
