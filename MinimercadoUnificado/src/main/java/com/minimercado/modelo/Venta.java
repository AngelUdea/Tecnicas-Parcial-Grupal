/**
 * Clase que representa una venta en el sistema.
 * Una venta contiene información sobre el cliente, la fecha, los productos vendidos
 * y los cálculos de totales, IVA y descuentos.
 * 
 * Características principales:
 * - Mantiene una lista de detalles de venta (productos vendidos)
 * - Calcula automáticamente subtotales, IVA y totales
 * - Asocia cada venta con un cliente
 * - Registra la fecha y hora de la venta
 */
package com.minimercado.modelo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Venta {
    // Atributos de la clase Venta
    private int id;                  // Identificador único de la venta
    private Cliente cliente;         // Cliente que realiza la compra
    private Date fecha;              // Fecha y hora de la venta
    private List<DetalleVenta> detalles;  // Lista de productos vendidos
    private double subtotalVenta;    // Suma de los subtotales base de cada detalle
    private double ivaVenta;         // Monto total de IVA de la venta
    private double descuentoVenta;   // Monto total de descuento de la venta
    private double totalVenta;       // Monto total final de la venta

    // Constructor por defecto
    public Venta() {
        this.fecha = new Date();     // Fecha actual
        this.detalles = new ArrayList<>();  // Lista vacía de detalles
        // Eliminamos el iva fijo, ahora se suma el iva de cada detalle
        // this.iva = 0.19;
    }

    // Constructor con cliente
    public Venta(Cliente cliente) {
        this();                      // Llama al constructor por defecto
        this.cliente = cliente;      // Asigna el cliente
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }
    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }
    public List<DetalleVenta> getDetalles() { return detalles; }
    
    public void setDetalles(List<DetalleVenta> detalles) {
        this.detalles = detalles;
        recalcularTotales();            // Recalcula los totales al cambiar los detalles
    }

    // Método para agregar un detalle a la venta
    public void agregarDetalle(DetalleVenta detalle) {
        detalle.setVenta(this);     // Establece la relación bidireccional
        detalles.add(detalle);       // Agrega el detalle a la lista
        recalcularTotales();            // Recalcula los totales
    }

    // Método para eliminar un detalle de la venta
    public void eliminarDetalle(DetalleVenta detalle) {
        detalles.remove(detalle);    // Elimina el detalle de la lista
        recalcularTotales();            // Recalcula los totales
    }

    // Getters para los totales de la venta
    public double getSubtotal() { return subtotalVenta; } // getSubtotal ahora devuelve subtotalBase
    public double getIva() { return ivaVenta; } // getIva ahora devuelve el monto total de IVA
    public double getDescuento() { return descuentoVenta; } // Nuevo getter para el monto total de descuento
    public double getTotal() { return totalVenta; } // getTotal ahora devuelve totalVenta

    // Método para calcular todos los totales de la venta
    public void recalcularTotales() {
        // Calcula el subtotal sumando los subtotales base de cada detalle
        this.subtotalVenta = detalles.stream()
                                 .mapToDouble(DetalleVenta::getSubtotalBase)
                                 .sum();
        // Calcula el monto total de IVA sumando los montos de IVA de cada detalle
        this.ivaVenta = detalles.stream()
                            .mapToDouble(DetalleVenta::getIvaMonto)
                            .sum();
        // Calcula el monto total de descuento sumando los montos de descuento de cada detalle
        this.descuentoVenta = detalles.stream()
                                  .mapToDouble(DetalleVenta::getDescuentoMonto)
                                  .sum();
        // El total de la venta es la suma de los totales de cada detalle
        this.totalVenta = detalles.stream()
                               .mapToDouble(DetalleVenta::getTotalDetalle)
                               .sum();
        
        // Aseguramos que el total también sea subtotal base + iva total - descuento total
        // Aunque la suma de totales de detalles debería ser igual
        // this.totalVenta = this.subtotalVenta + this.ivaVenta - this.descuentoVenta;
    }

    // Método para representar la venta como String
    @Override
    public String toString() {
        return String.format("Venta #%d - %s - $%.2f", id, cliente.getNombre(), totalVenta);
    }
} 