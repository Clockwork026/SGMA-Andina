package cl.andina.sgma.service;

import cl.andina.sgma.model.Activo;
import cl.andina.sgma.repository.ActivoRepository;
import cl.andina.sgma.repository.AnomaliaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ActivoService {

    private static final String FILTRO_TODOS = "Todos";

    private final ActivoRepository activoRepository;
    private final AnomaliaRepository anomaliaRepository;

    @Transactional(readOnly = true)
    public List<Activo> listar(String q, String estado) {
        String texto = (q == null) ? "" : q.trim().toLowerCase(Locale.ROOT);
        String patron = "%" + texto + "%";

        boolean sinFiltroEstado = estado == null
                || estado.isBlank()
                || FILTRO_TODOS.equalsIgnoreCase(estado.trim());

        if (sinFiltroEstado) {
            return activoRepository.buscarPorTexto(patron);
        }
        return activoRepository.buscarPorTextoYEstado(patron, estado.trim());
    }

    @Transactional(readOnly = true)
    public Activo obtenerDetalle(Long id) {
        Activo activo = activoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No existe el activo con id " + id));
        activo.setAnomalias(anomaliaRepository.findByActivoIdOrderByFechaReporteDesc(id));
        return activo;
    }
}