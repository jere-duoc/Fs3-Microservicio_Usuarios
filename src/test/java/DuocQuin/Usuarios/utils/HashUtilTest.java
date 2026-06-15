package DuocQuin.Usuarios.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HashUtilTest {

    @Test
    void sha256_shouldReturnKnownHashForInput() {
        String expected = "ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad";

        String actual = HashUtil.sha256("abc");

        assertEquals(expected, actual);
    }

    @Test
    void sha256_shouldBeDeterministic() {
        String first = HashUtil.sha256("repeat");
        String second = HashUtil.sha256("repeat");

        assertEquals(first, second);
        assertFalse(first.isEmpty());
    }
}
