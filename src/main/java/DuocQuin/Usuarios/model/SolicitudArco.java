package DuocQuin.Usuarios.model;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "solicitudes_arco")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SolicitudArco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_solicitud")
    private Long idSolicitud;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_derecho")
    private TipoDerecho tipoDerecho;

    @Column(name = "fecha_solicitud")
    private LocalDateTime fechaSolicitud = LocalDateTime.now();

    @Column(name = "detalles")
    private String detalles;

    public enum TipoDerecho {
        ACCESO, RECTIFICACION, CANCELACION, OPOSICION
    }
}
