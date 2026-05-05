package DuocQuin.Usuarios.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

@Entity
@Table(name = "usuarios")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Usuario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long idUsuario;
    
    @NotBlank(message = "El RUN es obligatorio")
    @Column(name = "run", nullable = false, length = 10, unique = true)
    private String run;
    
    @NotBlank(message = "El dígito verificador es obligatorio")
    @Column(name = "digito_verificador", nullable = false, length = 1)
    private String digitoVerificador;
    
    @Column(name = "primer_nombre", length = 50)
    private String primerNombre;

    @Column(name = "segundo_nombre", length = 50)
    private String segundoNombre;

    @Column(name = "primer_apellido", length = 50)
    private String primerApellido;

    @Column(name = "segundo_apellido", length = 50)
    private String segundoApellido;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @Column(name = "genero", length = 20)
    private String genero;

    @Email(message = "El correo electrónico debe ser válido")
    @Column(name = "correo_electronico", length = 100)
    private String correoElectronico;

    @Column(name = "contrasena", length = 100)
    private String contrasena;

    @Column(name = "telefono_celular", length = 9)
    private String telefonoCelular;

    @Column(name = "acepto_terminos")
    private Boolean aceptoTerminos;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_rol")
    private Rol rol;
}
