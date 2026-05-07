package DuocQuin.Usuarios.model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

import DuocQuin.Usuarios.utils.EncryptedStringConverter;

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
    
    @Convert(converter = EncryptedStringConverter.class)//Encriptado con un convertidor para que se pueda comparar con el run original
    @NotBlank(message = "El RUN es obligatorio")
    @Column(name = "run", nullable = false, length = 255)
    private String run;
    
    @Convert(converter = EncryptedStringConverter.class)//Encriptado con el mismo convertidor
    @NotBlank(message = "El dígito verificador es obligatorio")
    @Column(name = "digito_verificador", nullable = false, length = 255)
    private String digitoVerificador;

    @Column(name = "run_hash", length = 255, unique = true)// Esto es para la validación del rut para ver si esta en la base
    private String runHash;
    
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

    @Convert(converter = EncryptedStringConverter.class)//Encriptado con un convertidor
    @Email(message = "El correo electrónico debe ser válido")
    @Column(name = "correo_electronico", length = 255)
    private String correoElectronico;

    @Column(name = "contrasena", length = 255)
    private String contrasena;

    @Convert(converter = EncryptedStringConverter.class)//Encriptado con un convertidor
    @Column(name = "telefono_celular", length = 255)
    private String telefonoCelular;

    @Column(name = "acepto_terminos")
    private Boolean aceptoTerminos;

    @Column(name = "oposicion")
    private Boolean oposicion = false;

    @Column(name = "activo")
    private Boolean activo = true;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_rol")
    private Rol rol;
}
