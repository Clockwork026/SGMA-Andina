package cl.andina.sgma.repository;

import cl.andina.sgma.model.Activo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ActivoRepository extends JpaRepository<Activo, Long> {

    /**
     * Búsqueda multicriterio sin distinguir mayúsculas/minúsculas.
     * El parámetro patron llega ya en minúsculas y con formato %texto%.
     */
    @Query("""
            SELECT a FROM Activo a
            WHERE LOWER(a.codigoInterno) LIKE :patron
               OR LOWER(a.nombre) LIKE :patron
               OR LOWER(COALESCE(a.apodoPlanta, '')) LIKE :patron
               OR LOWER(a.ubicacion) LIKE :patron
               OR LOWER(a.tipoEquipo) LIKE :patron
            ORDER BY a.codigoInterno ASC
            """)
    List<Activo> buscarPorTexto(@Param("patron") String patron);

    @Query("""
            SELECT a FROM Activo a
            WHERE a.estado = :estado
              AND (LOWER(a.codigoInterno) LIKE :patron
                OR LOWER(a.nombre) LIKE :patron
                OR LOWER(COALESCE(a.apodoPlanta, '')) LIKE :patron
                OR LOWER(a.ubicacion) LIKE :patron
                OR LOWER(a.tipoEquipo) LIKE :patron)
            ORDER BY a.codigoInterno ASC
            """)
    List<Activo> buscarPorTextoYEstado(@Param("patron") String patron,
                                       @Param("estado") String estado);
}