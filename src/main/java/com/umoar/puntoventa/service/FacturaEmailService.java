package com.umoar.puntoventa.service;

import com.umoar.puntoventa.model.DetalleVenta;
import com.umoar.puntoventa.model.Venta;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;

/**
 * Servicio para la generación y envío por correo electrónico de Facturas y Comprobantes de Pago.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class FacturaEmailService {

    private final ObjectProvider<JavaMailSender> mailSenderProvider;

    @Value("${spring.mail.username:}")
    private String remitente;

    /**
     * Envía la factura por correo de manera asíncrona para no bloquear el proceso de cobro.
     */
    public void enviarFacturaAsync(Venta venta, String emailDestinatario) {
        CompletableFuture.runAsync(() -> {
            try {
                enviarFactura(venta, emailDestinatario);
            } catch (Exception e) {
                log.warn("No se pudo enviar la factura por correo para la venta {}: {}", 
                        venta.getNumeroFactura(), e.getMessage());
            }
        });
    }

    /**
     * Envía la factura detallada en formato HTML al correo especificado.
     */
    public boolean enviarFactura(Venta venta, String emailDestinatario) {
        String destino = emailDestinatario;
        if (destino == null || destino.isBlank()) {
            if (venta.getCliente() != null && venta.getCliente().getEmail() != null) {
                destino = venta.getCliente().getEmail().trim();
            }
        }

        if (destino == null || destino.isBlank()) {
            log.info("Venta {} cobrada, pero el cliente no tiene correo registrado.", venta.getNumeroFactura());
            return false;
        }

        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
        if (mailSender == null || remitente == null || remitente.isBlank()) {
            log.info("Servidor de correo no configurado (SPRING_MAIL_USERNAME vacío). Factura para {} simulada correctamente.", destino);
            return false;
        }

        try {
            MimeMessage mensaje = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");

            helper.setFrom(remitente, "La Taberna de los Morenazos");
            helper.setTo(destino);
            helper.setSubject("🧾 Factura Electrónica - " + venta.getNumeroFactura() + " | La Taberna de los Morenazos");

            String html = generarHtmlFactura(venta, destino);
            helper.setText(html, true);

            mailSender.send(mensaje);
            log.info("Factura {} enviada exitosamente al correo: {}", venta.getNumeroFactura(), destino);
            return true;
        } catch (Exception e) {
            log.error("Error al enviar la factura electrónica por correo a {}: {}", destino, e.getMessage());
            return false;
        }
    }

    /**
     * Construye la plantilla HTML visual para la factura electrónica usando StringBuilder.
     */
    public String generarHtmlFactura(Venta venta, String emailDestino) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        String fecha = venta.getFechaVenta() != null ? venta.getFechaVenta().format(formatter) : "Fecha actual";

        String nombreCliente = "Consumidor Final";
        String duiCliente = "00000000-0";
        String direccionCliente = "Entrega en Local";

        if (venta.getCliente() != null) {
            if (venta.getCliente().getNombre() != null && !venta.getCliente().getNombre().isBlank()) {
                nombreCliente = venta.getCliente().getNombre();
                if (venta.getCliente().getApellido() != null) {
                    nombreCliente += " " + venta.getCliente().getApellido();
                }
            }
            if (venta.getCliente().getDocumento() != null && !venta.getCliente().getDocumento().isBlank()) {
                duiCliente = venta.getCliente().getDocumento();
            }
            if (venta.getCliente().getDireccion() != null && !venta.getCliente().getDireccion().isBlank()) {
                direccionCliente = venta.getCliente().getDireccion();
            }
        }

        if (venta.getDireccionEntrega() != null && !venta.getDireccionEntrega().isBlank()) {
            direccionCliente = venta.getDireccionEntrega();
        }

        BigDecimal subtotal = venta.getSubtotal() != null ? venta.getSubtotal().setScale(2) : BigDecimal.ZERO;
        BigDecimal iva = venta.getIva() != null ? venta.getIva().setScale(2) : BigDecimal.ZERO;
        BigDecimal total = venta.getTotal() != null ? venta.getTotal().setScale(2) : BigDecimal.ZERO;
        String metodoPago = venta.getMetodoPago() != null ? venta.getMetodoPago() : "Efectivo";

        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html><html><head><meta charset='utf-8'>");
        sb.append("<style>");
        sb.append("body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif; background-color: #0b0717; color: #f3f4f6; margin: 0; padding: 24px; }");
        sb.append(".container { max-width: 580px; margin: 0 auto; background: #16112a; border-radius: 16px; border: 1px solid #3b2d66; padding: 28px; box-shadow: 0 10px 25px rgba(0,0,0,0.5); }");
        sb.append(".header { text-align: center; border-bottom: 2px dashed #3b2d66; padding-bottom: 20px; margin-bottom: 20px; }");
        sb.append(".badge { display: inline-block; padding: 4px 12px; background: #a855f7; color: white; border-radius: 20px; font-size: 12px; font-weight: bold; margin-bottom: 10px; text-transform: uppercase; }");
        sb.append("h1 { margin: 6px 0; color: #ffffff; font-size: 24px; }");
        sb.append(".subtitle { color: #a78bfa; font-size: 14px; margin: 0; }");
        sb.append(".info-box { background: #1c1536; border-radius: 10px; padding: 14px; margin-bottom: 20px; font-size: 13px; line-height: 1.6; }");
        sb.append("table { width: 100%; border-collapse: collapse; margin-bottom: 20px; font-size: 13px; }");
        sb.append("th { text-align: left; padding: 10px; background: #241b44; color: #c4b5fd; text-transform: uppercase; font-size: 11px; }");
        sb.append(".totals { margin-top: 15px; border-top: 2px dashed #3b2d66; padding-top: 15px; }");
        sb.append(".footer { text-align: center; font-size: 12px; color: #6b7280; margin-top: 25px; border-top: 1px solid #2d2645; padding-top: 15px; }");
        sb.append("</style></head><body>");

        sb.append("<div class='container'>");
        sb.append("<div class='header'>");
        sb.append("<span class='badge'>Factura Electrónica Pagada</span>");
        sb.append("<h1>La Taberna de los Morenazos</h1>");
        sb.append("<p class='subtitle'>Folio: <strong>").append(venta.getNumeroFactura()).append("</strong></p>");
        sb.append("<p style='font-size: 12px; color: #9ca3af; margin-top: 4px;'>Fecha: ").append(fecha).append("</p>");
        sb.append("</div>");

        sb.append("<div class='info-box'>");
        sb.append("<div><strong>Cliente:</strong> ").append(nombreCliente).append("</div>");
        sb.append("<div><strong>DUI Oficial:</strong> <span style='color: #a78bfa; font-weight: bold;'>").append(duiCliente).append("</span></div>");
        sb.append("<div><strong>Correo:</strong> ").append(emailDestino).append("</div>");
        sb.append("<div><strong>Dirección:</strong> ").append(direccionCliente).append("</div>");
        sb.append("<div><strong>Método de Pago:</strong> ").append(metodoPago).append("</div>");
        sb.append("</div>");

        sb.append("<table><thead><tr><th>Descripción</th><th style='text-align: right;'>Precio</th><th style='text-align: right;'>Total</th></tr></thead><tbody>");
        if (venta.getDetalles() != null && !venta.getDetalles().isEmpty()) {
            for (DetalleVenta d : venta.getDetalles()) {
                sb.append("<tr>");
                sb.append("<td style='padding: 10px; border-bottom: 1px solid #2d2645; color: #f3f4f6;'>");
                sb.append(d.getCantidad()).append("x ").append(d.getNombreProducto());
                sb.append("</td>");
                sb.append("<td style='padding: 10px; border-bottom: 1px solid #2d2645; text-align: right; color: #9ca3af;'>$");
                sb.append(d.getPrecioUnitario() != null ? d.getPrecioUnitario().setScale(2).toString() : "0.00");
                sb.append("</td>");
                sb.append("<td style='padding: 10px; border-bottom: 1px solid #2d2645; text-align: right; font-weight: bold; color: #f3f4f6;'>$");
                sb.append(d.getSubtotal() != null ? d.getSubtotal().setScale(2).toString() : "0.00");
                sb.append("</td>");
                sb.append("</tr>");
            }
        }
        sb.append("</tbody></table>");

        sb.append("<div class='totals'><table style='width: 100%;'>");
        sb.append("<tr><td style='color: #9ca3af;'>Subtotal:</td><td style='text-align: right; font-weight: bold;'>$").append(subtotal).append("</td></tr>");
        sb.append("<tr><td style='color: #9ca3af;'>IVA (13%):</td><td style='text-align: right; font-weight: bold;'>$").append(iva).append("</td></tr>");
        sb.append("<tr style='font-size: 18px; color: #34d399;'><td style='padding-top: 8px; font-weight: bold;'>TOTAL COBRADO:</td><td style='padding-top: 8px; text-align: right; font-weight: bold;'>$").append(total).append("</td></tr>");
        sb.append("</table></div>");

        sb.append("<div class='footer'>");
        sb.append("<p>¡Gracias por tu preferencia!</p>");
        sb.append("<p>Este comprobante es tu respaldo de compra y factura electrónica autorizada.</p>");
        sb.append("</div></div></body></html>");

        return sb.toString();
    }
}
