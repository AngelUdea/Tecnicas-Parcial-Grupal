/**
 * Clase que representa un producto en el sistema.
 * Un producto contiene información sobre su nombre, descripción, precio,
 * impuestos, descuentos y stock disponible.
 * 
 * Características principales:
 * - Maneja precios con soporte para decimales
 * - Calcula precios finales incluyendo IVA y descuentos
 * - Mantiene control de inventario (stock)
 * - Permite configurar descuentos e impuestos
 * 
 * Notas técnicas:
 * - Los porcentajes de IVA y descuento se almacenan como decimales (0.19 = 19%)
 * - El precio final se calcula automáticamente al modificar el precio base
 */
package com.minimercado.modelo;

public class Producto {
    // Atributos de la clase Producto
    private int id;                  // Identificador único del producto
    private String codigo;           // Código de barras o identificador externo
    private String nombre;           // Nombre del producto
    private String descripcion;      // Descripción detallada
    private double precio;           // Precio base del producto
    private double iva;              // Porcentaje de IVA aplicable
    private double descuento;        // Porcentaje de descuento aplicable
    private int stock;               // Cantidad disponible en inventario

    // Constructor por defecto
    public Producto() {
        this.iva = 0.19;            // IVA por defecto 19%
        this.descuento = 0.0;       // Sin descuento por defecto
    }

    // Constructor con datos básicos
    public Producto(String codigo, String nombre, double precio, int stock) {
        this();                     // Llama al constructor por defecto
        this.codigo = codigo;
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public double getIva() {
        return iva;
    }

    public void setIva(double iva) {
        this.iva = iva;
    }

    public double getDescuento() {
        return descuento;
    }

    public void setDescuento(double descuento) {
        this.descuento = descuento;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    // Método para calcular el precio con IVA (mantener por si se usa)
    public double getPrecioConIva() {
        return precio * (1 + iva);
    }

    // Método para calcular el precio con descuento (mantener por si se usa)
    public double getPrecioConDescuento() {
        return precio * (1 - descuento);
    }

    // Método para calcular el precio final (consistente con DetalleVenta)
    public double getPrecioFinal() {
        // Precio base + monto IVA - monto Descuento
        return precio + (precio * iva) - (precio * descuento);
    }

    // Método para representar el producto como String
    @Override
    public String toString() {
        return nombre;
    }
} 