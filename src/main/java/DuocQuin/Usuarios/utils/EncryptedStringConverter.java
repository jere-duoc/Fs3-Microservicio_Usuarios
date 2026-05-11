package DuocQuin.Usuarios.utils;

import org.jasypt.encryption.StringEncryptor;
import org.springframework.stereotype.Component;

import jakarta.persistence.AttributeConverter;


@Component
public class EncryptedStringConverter implements AttributeConverter<String, String> {

    private final StringEncryptor encryptor;

    public EncryptedStringConverter(StringEncryptor encryptor) {
        this.encryptor = encryptor;
    }

    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return attribute;
        }
        return encryptor.encrypt(attribute);
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return dbData;
        }
        try {
            return encryptor.decrypt(dbData);
        } catch (Exception e) {
            // Si la desencriptación falla, asumimos que el dato no está cifrado (dato legado)
            return dbData;
        }
    }

}
