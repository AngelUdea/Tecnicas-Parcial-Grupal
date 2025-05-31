/**
 * Controlador que maneja toda la lógica de negocio relacionada con las ventas.
 * Esta clase implementa el patrón Controlador del MVC y coordina las operaciones
 * entre el modelo (Venta, DetalleVenta) y la vista.
 * 
 * Funcionalidades principales:
 * - Creación y gestión de ventas
 * - Cálculo de totales e impuestos
 * - Validación de operaciones
 * - Integración con otros controladores (Producto, Cliente)
 * 
 * Notas técnicas:
 * - Mantiene una lista de ventas activas
 * - Coordina la persistencia de datos
 * - Maneja la generación de facturas
 */
package com.minimercado.controlador;

import com.minimercado.modelo.Venta;
import com.minimercado.modelo.DetalleVenta;
import com.minimercado.modelo.Producto;
import com.minimercado.modelo.Cliente;
import com.minimercado.util.FileManager;
import java.util.ArrayList;
import java.util.List;

public class ControladorVenta {
    // Lista que mantiene todas las ventas del sistema
    private List<Venta> ventas;
    // Referencias a otros controladores necesarios
    private ControladorProducto productoController;
    private ControladorCliente clienteController;
    
    // Constructor que inicializa el controlador
    public ControladorVenta(ControladorProducto productoController, ControladorCliente clienteController) {
        this.productoController = productoController;
        this.clienteController = clienteController;
        // Carga las ventas desde el archivo
        this.ventas = FileManager.cargarVentas();
    }
    
    // Getters para los controladores
    public ControladorProducto getProductoController() {
        return productoController;
    }
    
    public ControladorCliente getClienteController() {
        return clienteController;
    }
    
    // Método para crear una nueva venta
    public Venta crearVenta(Cliente cliente) {
        Venta venta = new Venta(cliente);
        // Asigna un nuevo ID si es necesario
        if (venta.getId() == 0) {
            int nuevoId = ventas.isEmpty() ? 1 : ventas.get(ventas.size() - 1).getId() + 1;
            venta.setId(nuevoId);
        }
        ventas.add(venta);
        // Guarda la venta en el archivo
        FileManager.guardarVenta(venta);
        return venta;
    }
    
    // Método para agregar un producto a una venta
    public void agregarProductoAVenta(Venta venta, Producto producto, int cantidad) {
        // Verifica que haya stock suficiente
        if (producto.getStock() >= cantidad) {
            DetalleVenta detalle = new DetalleVenta(producto, cantidad);
            venta.agregarDetalle(detalle);
            // Actualiza el stock del producto
            producto.setStock(producto.getStock() - cantidad);
            // Guarda los cambios
            FileManager.guardarVenta(venta);
        }
    }
    
    // Método para eliminar un producto de una venta
    public void eliminarProductoDeVenta(Venta venta, DetalleVenta detalle) {
        venta.eliminarDetalle(detalle);
        // Devuelve el stock al producto
        Producto producto = detalle.getProducto();
        producto.setStock(producto.getStock() + detalle.getCantidad());
        // Guarda los cambios
        FileManager.guardarVenta(venta);
    }
    
    // Método para agregar una venta existente
    public void agregarVenta(Venta venta) {
        ventas.add(venta);
        FileManager.guardarVenta(venta);
    }
    
    // Método para eliminar una venta
    public void eliminarVenta(Venta venta) {
        ventas.remove(venta);
        FileManager.guardarVentas(ventas);
    }
    
    // Método para obtener todas las ventas
    public List<Venta> obtenerVentas() {
        return new ArrayList<>(ventas);
    }
    
    // Método para buscar una venta por su ID
    public Venta buscarVentaPorId(int id) {
        return ventas.stream().filter(v -> v.getId() == id).findFirst().orElse(null);
    }
    
    // Método para buscar un producto por su ID
    public Producto buscarProductoPorId(int id) {
        return productoController.buscarProductoPorId(id);
    }
    
    // Método para buscar un cliente por su ID
    public Cliente buscarClientePorId(int id) {
        return clienteController.buscarClientePorId(id);
    }
    
    // Método para calcular el total de una venta
    public double calcularTotalVenta(Venta venta) {
        // Sumamos los totales finales de cada detalle
        return venta.getDetalles().stream()
                .mapToDouble(DetalleVenta::getTotalDetalle)
                .sum();
    }
} 