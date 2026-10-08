package com.umoar.puntoventa.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "detalle_ventas")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class DetalleVenta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;

    @Column(name = "nombre_producto")
    private String nombreProducto;

    @ManyToOne
    @JoinColumn(name = "venta_id")
    @JsonIgnoreProperties("detalles")
    private Venta venta;

    @ManyToOne
    @JoinColumn(name = "producto_id")
    private Producto producto;
}
