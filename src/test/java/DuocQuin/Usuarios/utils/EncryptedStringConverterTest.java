package DuocQuin.Usuarios.utils;

import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EncryptedStringConverterTest {

    private EncryptedStringConverter converter;

    @BeforeEach
    void setUp() {
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword("testSecret123");
        converter = new EncryptedStringConverter(encryptor);
    }

    @Test
    void convertToDatabaseColumn_encryptsValue() {
        String original = "HolaMundo";

        String encrypted = converter.convertToDatabaseColumn(original);

        assertNotNull(encrypted);
        assertNotEquals(original, encrypted);
        assertFalse(encrypted.isEmpty());
    }

    @Test
    void convertToEntityAttribute_decryptsValue() {
        String original = "HolaMundo";
        String encrypted = converter.convertToDatabaseColumn(original);

        String decrypted = converter.convertToEntityAttribute(encrypted);

        assertEquals(original, decrypted);
    }

    @Test
    void convertToEntityAttribute_onLegacyPlainText_returnsPlainText() {
        String legacy = "plainText";

        assertEquals(legacy, converter.convertToEntityAttribute(legacy));
    }

    @Test
    void nullInput_returnsNull() {
        assertNull(converter.convertToDatabaseColumn(null));
        assertNull(converter.convertToEntityAttribute(null));
    }
}
