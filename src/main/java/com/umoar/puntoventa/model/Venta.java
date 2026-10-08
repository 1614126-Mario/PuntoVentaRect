package com.umoar.puntoventa.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ventas")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Venta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fecha_venta")
    @Builder.Default
    private LocalDateTime fechaVenta = LocalDateTime.now();

    private BigDecimal total;
    private BigDecimal subtotal;
    private BigDecimal iva;

    @Column(name = "numero_factura")
    private String numeroFactura;

    @Column(name = "tipo_pedido")
    private String tipoPedido; // "LOCAL", "EN_LINEA", "LLEVAR"

    @Column(name = "numero_mesa")
    private String numeroMesa; // Ej: "Mesa 4"

    @Column(name = "direccion_entrega")
    private String direccionEntrega;

    @Column(name = "metodo_pago")
    private String metodoPago;

    private String estado;

    @Column(length = 500)
    private String notas;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("venta")
    @Builder.Default
    private List<DetalleVenta> detalles = new ArrayList<>();
}
