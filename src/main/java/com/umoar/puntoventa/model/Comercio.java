package com.umoar.puntoventa.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "comercios")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Comercio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String nit;
    private String direccion;
    private String telefono;
}
