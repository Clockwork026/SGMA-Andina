package cl.andina.sgma.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "activos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Activo {

    public static final String ESTADO_OPERATIVO = "Operativo";
    public static final String ESTADO_EN_MANTENCION = "En Mantención";
    public static final String ESTADO_DETENIDO = "Detenido";
    public static final String ESTADO_CON_ANOMALIA = "Con anomalía reportada";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo_interno", nullable = false, unique = true, length = 20)
    private String codigoInterno;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(name = "apodo_planta", length = 80)
    private String apodoPlanta;

    @Column(name = "tipo_equipo", nullable = false, length = 60)
    private String tipoEquipo;

    @Column(nullable = false, length = 60)
    private String marca;

    @Column(nullable = false, length = 60)
    private String modelo;

    @Column(nullable = false, length = 100)
    private String ubicacion;

    // CRITICA, ALTA, MEDIA, BAJA
    @Column(nullable = false, length = 10)
    private String criticidad;

    @Column(nullable = false, length = 30)
    private String estado;

    // No se guarda en la tabla: solo se llena en GET /api/activos/{id}
    @Transient
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<Anomalia> anomalias;
}