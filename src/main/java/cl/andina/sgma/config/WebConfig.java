package cl.andina.sgma.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final Path directorioUploads;
    private final String origenPermitido;

    public WebConfig(@Value("${app.upload-dir}") String uploadDir,
                     @Value("${app.cors.allowed-origin}") String origenPermitido) throws IOException {
        this.directorioUploads = Paths.get(uploadDir).toAbsolutePath().normalize();
        this.origenPermitido = origenPermitido;
        // Crea uploads/ automáticamente al iniciar si no existe
        Files.createDirectories(this.directorioUploads);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(origenPermitido)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // /uploads/** -> carpeta física uploads/ (toUri agrega "/" final al ser directorio)
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(directorioUploads.toUri().toString());
    }
}