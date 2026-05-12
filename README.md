# Microservicio de Usuarios - Hospital DuocQuin 👤

Este microservicio se encarga de la gestión centralizada de los funcionarios del hospital, incluyendo la autenticación y el cumplimiento de la Ley 21.719 (Derechos ARCO).

## 🛠️ Tecnologías
- **Java 17**
- **Spring Boot 3.x**
- **Spring Security + JWT**
- **MySQL**
- **Maven**

## 📋 Funcionalidades
- **Gestión de Personal**: Registro, actualización y eliminación (anonimización) de funcionarios.
- **Roles y Permisos**: Soporte para roles de Médico, TENS, Administrativo, Asist. Médico y Externo.
- **Módulo ARCO**: Endpoints especializados para el ejercicio de los derechos de Acceso, Rectificación, Cancelación y Oposición.
- **Seguridad**: Autenticación basada en tokens JWT y encriptación de datos sensibles.

## ⚙️ Configuración y Ejecución
1. Configurar la base de datos MySQL usando el archivo `.sql` incluido en la carpeta raíz.
2. Actualizar el archivo `src/main/resources/application.properties` con sus credenciales de base de datos.
3. Ejecutar el servicio:
```bash
./mvnw spring-boot:run
```
El servicio estará disponible en `http://localhost:8081`.

## 📡 API Endpoints Principales
- `POST /api/usuarios`: Crear nuevo usuario.
- `GET /api/usuarios`: Listar todos los usuarios.
- `PUT /api/usuarios/{id}`: Actualizar usuario (Derecho de Rectificación).
- `DELETE /api/usuarios/{id}`: Eliminar/Anonimizar usuario (Derecho de Cancelación).
- `POST /api/usuarios/{id}/arco/acceso`: Registrar auditoría de acceso a datos.
