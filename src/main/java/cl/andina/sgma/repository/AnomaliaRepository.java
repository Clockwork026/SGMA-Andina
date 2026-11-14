package cl.andina.sgma.repository;

import cl.andina.sgma.model.Anomalia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AnomaliaRepository extends JpaRepository<Anomalia, Long> {

    List<Anomalia> findByActivoIdOrderByFechaReporteDesc(Long activoId);

    // Último código del año (el correlativo va con ceros a la izquierda, por eso ordena bien)
    Optional<Anomalia> findTopByCodigoStartingWithOrderByCodigoDesc(String prefijo);
}