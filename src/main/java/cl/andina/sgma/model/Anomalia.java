package cl.andina.sgma.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "anomalias")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Anomalia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Formato ANO-2026-0001
    @Column(nullable = false, unique = true, length = 20)
    private String codigo;

    @Column(name = "activo_id", nullable = false)
    private Long activoId;

    @Column(name = "reportado_por", nullable = false, length = 120)
    private String reportadoPor;

    @Column(nullable = false, length = 60)
    private String sintoma;

    @Column(nullable = false, length = 1000)
    private String descripcion;

    // Ruta pública, por ejemplo /uploads/5f3c...a1.jpg
    @Column(name = "foto_url", nullable = false, length = 255)
    private String fotoUrl;

    @Column(name = "fecha_reporte", nullable = false)
    private LocalDateTime fechaReporte;

    @Column(nullable = false, length = 30)
    private String estado;
}
