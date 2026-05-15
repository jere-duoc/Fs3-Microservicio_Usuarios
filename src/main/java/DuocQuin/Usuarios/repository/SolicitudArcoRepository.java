package DuocQuin.Usuarios.repository;

import DuocQuin.Usuarios.model.SolicitudArco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SolicitudArcoRepository extends JpaRepository<SolicitudArco, Long> {
    
    void deleteByUsuarioIdUsuario(Long idUsuario);
}
