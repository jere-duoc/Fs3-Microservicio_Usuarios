package DuocQuin.Usuarios.config;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import static org.junit.jupiter.api.Assertions.*;

class CorsConfigTest {

    @Test
    void corsConfigurationSource_returnsExpectedConfiguration() {
        CorsConfig corsConfig = new CorsConfig();
        CorsConfigurationSource source = corsConfig.corsConfigurationSource();

        assertNotNull(source);
        CorsConfiguration configuration = source.getCorsConfiguration(new MockHttpServletRequest());
        assertNotNull(configuration);
        assertEquals(1, configuration.getAllowedOriginPatterns().size());
        assertEquals("*", configuration.getAllowedOriginPatterns().get(0));
        assertTrue(configuration.getAllowedMethods().contains("GET"));
        assertTrue(configuration.getAllowedHeaders().contains("*"));
        assertTrue(configuration.getAllowCredentials());
    }
}
