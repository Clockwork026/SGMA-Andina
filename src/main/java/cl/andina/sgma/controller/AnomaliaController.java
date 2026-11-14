package cl.andina.sgma.controller;

import cl.andina.sgma.model.Anomalia;
import cl.andina.sgma.service.AnomaliaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.UncheckedIOException;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/anomalias")
@RequiredArgsConstructor
public class AnomaliaController {

    private final AnomaliaService anomaliaService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Anomalia> crear(
            @RequestParam("activoId") Long activoId,
            @RequestParam("reportadoPor") String reportadoPor,
            @RequestParam("sintoma") String sintoma,
            @RequestParam("descripcion") String descripcion,
            @RequestParam(value = "foto", required = false) MultipartFile foto) {

        Anomalia creada = anomaliaService.crear(activoId, reportadoPor, sintoma, descripcion, foto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> solicitudInvalida(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("mensaje", e.getMessage()));
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Map<String, String>> noEncontrado(NoSuchElementException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", e.getMessage()));
    }

    @ExceptionHandler(UncheckedIOException.class)
    public ResponseEntity<Map<String, String>> errorArchivo(UncheckedIOException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("mensaje", e.getMessage()));
    }
}