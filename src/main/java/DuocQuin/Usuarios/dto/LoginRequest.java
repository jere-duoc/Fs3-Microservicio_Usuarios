package DuocQuin.Usuarios.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
    private String correoElectronico; // Este campo se usa para RUT o Correo según el Front
    private String contrasena;
}
