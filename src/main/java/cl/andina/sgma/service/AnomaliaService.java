package cl.andina.sgma.service;

import cl.andina.sgma.model.Activo;
import cl.andina.sgma.model.Anomalia;
import cl.andina.sgma.repository.ActivoRepository;
import cl.andina.sgma.repository.AnomaliaRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class AnomaliaService {

    private static final String ESTADO_INICIAL_ANOMALIA = "Reportada";

    private final AnomaliaRepository anomaliaRepository;
    private final ActivoRepository activoRepository;
    private final Path directorioUploads;

    public AnomaliaService(AnomaliaRepository anomaliaRepository,
                           ActivoRepository activoRepository,
                           @Value("${app.upload-dir}") String uploadDir) throws IOException {
        this.anomaliaRepository = anomaliaRepository;
        this.activoRepository = activoRepository;
        this.directorioUploads = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(this.directorioUploads);
    }

    @Transactional
    public Anomalia crear(Long activoId,
                          String reportadoPor,
                          String sintoma,
                          String descripcion,
                          MultipartFile foto) {

        // ---- Validaciones de negocio ----
        if (activoId == null) {
            throw new IllegalArgumentException("Debe seleccionar un activo.");
        }
        if (reportadoPor == null || reportadoPor.isBlank()) {
            throw new IllegalArgumentException("Falta el usuario que reporta la falla.");
        }
        if (sintoma == null || sintoma.isBlank()) {
            throw new IllegalArgumentException("Debe seleccionar un síntoma.");
        }
        if (descripcion == null || descripcion.isBlank()) {
            throw new IllegalArgumentException("Debe ingresar una descripción de la falla.");
        }
        if (foto == null || foto.isEmpty()) {
            throw new IllegalArgumentException("La fotografía es obligatoria.");
        }
        String tipoContenido = foto.getContentType();
        if (tipoContenido == null || !tipoContenido.toLowerCase(Locale.ROOT).startsWith("image/")) {
            throw new IllegalArgumentException("El archivo debe ser una imagen.");
        }

        Activo activo = activoRepository.findById(activoId)
                .orElseThrow(() -> new NoSuchElementException("No existe el activo con id " + activoId));

        // ---- Código correlativo y archivo ----
        String codigo = generarCodigo();
        String nombreArchivo = UUID.randomUUID() + "." + extensionSegura(foto.getOriginalFilename());
        Path destino = directorioUploads.resolve(nombreArchivo).normalize();
        guardarArchivo(foto, destino);

        try {
            Anomalia anomalia = new Anomalia();
            anomalia.setCodigo(codigo);
            anomalia.setActivoId(activo.getId());
            anomalia.setReportadoPor(reportadoPor.trim());
            anomalia.setSintoma(sintoma.trim());
            anomalia.setDescripcion(descripcion.trim());
            anomalia.setFotoUrl("/uploads/" + nombreArchivo);
            anomalia.setFechaReporte(LocalDateTime.now());
            anomalia.setEstado(ESTADO_INICIAL_ANOMALIA);
            Anomalia guardada = anomaliaRepository.save(anomalia);

            // Cambio automático de estado dentro de la misma transacción
            activo.setEstado(Activo.ESTADO_CON_ANOMALIA);
            activoRepository.save(activo);

            return guardada;
        } catch (RuntimeException e) {
            // Si falla la BD se borra la foto para no dejar archivos huérfanos
            eliminarSilencioso(destino);
            throw e;
        }
    }

    private String generarCodigo() {
        String prefijo = "ANO-" + Year.now().getValue() + "-";
        int siguiente = anomaliaRepository.findTopByCodigoStartingWithOrderByCodigoDesc(prefijo)
                .map(a -> Integer.parseInt(a.getCodigo().substring(prefijo.length())) + 1)
                .orElse(1);
        return prefijo + String.format("%04d", siguiente);
    }

    private String extensionSegura(String nombreOriginal) {
        if (nombreOriginal != null) {
            int punto = nombreOriginal.lastIndexOf('.');
            if (punto >= 0 && punto < nombreOriginal.length() - 1) {
                String extension = nombreOriginal.substring(punto + 1).toLowerCase(Locale.ROOT);
                if (extension.matches("[a-z0-9]{1,5}")) {
                    return extension;
                }
            }
        }
        return "jpg";
    }

    private void guardarArchivo(MultipartFile foto, Path destino) {
        try (InputStream entrada = foto.getInputStream()) {
            Files.copy(entrada, destino, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo guardar la fotografía.", e);
        }
    }

    private void eliminarSilencioso(Path archivo) {
        try {
            Files.deleteIfExists(archivo);
        } catch (IOException ignorada) {
            // Limpieza de mejor esfuerzo: no debe ocultar el error original
        }
    }
}