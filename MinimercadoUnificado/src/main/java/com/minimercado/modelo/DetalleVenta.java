/**
 * Clase que representa un detalle individual de una venta.
 * Cada detalle de venta contiene información sobre un producto específico
 * que fue vendido, incluyendo cantidad, precio unitario y cálculos de subtotales.
 * 
 * Características principales:
 * - Mantiene la relación entre una venta y un producto
 * - Calcula subtotales basados en cantidad y precio
 * - Aplica IVA y descuentos del producto
 * - Mantiene el precio unitario histórico de la venta
 */
package com.minimercado.modelo;

public class DetalleVenta {
    // Atributos de la clase DetalleVenta
    private int id;                  // Identificador único del detalle
    private Venta venta;             // Referencia a la venta padre
    private Producto producto;       // Producto vendido
    private int cantidad;            // Cantidad vendida
    private double precioUnitario;   // Precio unitario al momento de la venta
    private double subtotalBase;     // Subtotal basado en precio base (cantidad * precio base)
    private double ivaMonto;         // Monto de IVA
    private double descuentoMonto;     // Monto de descuento
    private double totalDetalle;     // Total final del detalle

    // Constructor por defecto
    public DetalleVenta() {
    }

    // Constructor con producto y cantidad
    public DetalleVenta(Producto producto, int cantidad) {
        this.producto = producto;
        this.cantidad = cantidad;
        calcularTotales();
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Venta getVenta() {
        return venta;
    }

    public void setVenta(Venta venta) {
        this.venta = venta;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
        if (producto != null) {
            // precioUnitario se establece con el precio final del producto al momento de la venta
            this.precioUnitario = producto.getPrecioFinal(); 
            calcularTotales(); // Recalcular totales cuando cambia el producto
        }
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
        calcularTotales();
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    // Getters para los totales calculados
    public double getSubtotalBase() { return subtotalBase; }
    public double getIvaMonto() { return ivaMonto; }
    public double getDescuentoMonto() { return descuentoMonto; }
    public double getTotalDetalle() { return totalDetalle; }
    
    // Método para calcular los totales del detalle
    private void calcularTotales() {
        // Calculamos el subtotal basado en el precio base del producto
        this.subtotalBase = producto.getPrecio() * cantidad;
        // Calculamos el monto de IVA y Descuento basado en el subtotal base
        this.ivaMonto = subtotalBase * producto.getIva();
        this.descuentoMonto = subtotalBase * producto.getDescuento();
        // El total del detalle es el subtotal base + monto IVA - monto Descuento
        this.totalDetalle = subtotalBase + ivaMonto - descuentoMonto;
        
        // Actualizamos el precio unitario si cambia el producto o cantidad (aunque no deberia cambiar con cantidad)
        // this.precioUnitario = producto.getPrecioFinal(); 
    }

    // Método para representar el detalle como String
    @Override
    public String toString() {
        // Mostramos el precio unitario final y el total del detalle
        return String.format("%s x%d $%.2f = $%.2f", producto.getNombre(), cantidad, precioUnitario, totalDetalle);
    }
} 