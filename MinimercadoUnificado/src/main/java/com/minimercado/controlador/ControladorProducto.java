/**
 * Controlador que maneja toda la lógica de negocio relacionada con los productos.
 * Esta clase implementa el patrón Controlador del MVC y coordina las operaciones
 * entre el modelo (Producto) y la vista.
 * 
 * Funcionalidades principales:
 * - Gestión del catálogo de productos
 * - Control de inventario
 * - Validación de operaciones
 * - Cálculo de precios y descuentos
 * 
 * Notas técnicas:
 * - Mantiene una lista de productos
 * - Coordina la persistencia de datos
 * - Maneja la actualización de stock
 * - Valida operaciones de venta
 */
package com.minimercado.controlador;

import com.minimercado.modelo.Producto;
import com.minimercado.util.FileManager;
import java.util.ArrayList;
import java.util.List;

public class ControladorProducto {
    // Lista que mantiene todos los productos del sistema
    private List<Producto> productos;
    // Lista de listeners para notificar cambios
    private List<ProductoChangeListener> listeners;
    
    // Constructor que inicializa el controlador
    public ControladorProducto() {
        // Carga los productos desde el archivo
        this.productos = FileManager.cargarProductos();
        this.listeners = new ArrayList<>();
    }
    
    // Método para registrar un nuevo listener
    public void addProductoChangeListener(ProductoChangeListener listener) {
        listeners.add(listener);
    }
    
    // Método para remover un listener
    public void removeProductoChangeListener(ProductoChangeListener listener) {
        listeners.remove(listener);
    }
    
    // Método para notificar a todos los listeners
    private void notifyProductosChanged() {
        for (ProductoChangeListener listener : listeners) {
            listener.onProductosChanged();
        }
    }
    
    // Método para agregar un nuevo producto
    public void agregarProducto(Producto producto) {
        // Asigna un nuevo ID si es necesario
        if (producto.getId() == 0) {
            int nuevoId = productos.isEmpty() ? 1 : productos.get(productos.size() - 1).getId() + 1;
            producto.setId(nuevoId);
        }
        productos.add(producto);
        // Guarda los cambios en el archivo
        FileManager.guardarProductos(productos);
        // Notifica a los listeners
        notifyProductosChanged();
    }
    
    // Método para eliminar un producto
    public void eliminarProducto(Producto producto) {
        productos.remove(producto);
        // Guarda los cambios en el archivo
        FileManager.guardarProductos(productos);
        // Notifica a los listeners
        notifyProductosChanged();
    }
    
    // Método para actualizar un producto existente
    public void actualizarProducto(Producto producto) {
        int indice = -1;
        // Busca el producto por su ID
        for (int i = 0; i < productos.size(); i++) {
            if (productos.get(i).getId() == producto.getId()) {
                indice = i;
                break;
            }
        }
        // Si encuentra el producto, lo actualiza
        if (indice != -1) {
            productos.set(indice, producto);
            // Guarda los cambios en el archivo
            FileManager.guardarProductos(productos);
            // Notifica a los listeners
            notifyProductosChanged();
        }
    }
    
    // Método para obtener todos los productos
    public List<Producto> obtenerProductos() {
        return new ArrayList<>(productos);
    }
    
    // Método para buscar un producto por su ID
    public Producto buscarProductoPorId(int id) {
        return productos.stream().filter(p -> p.getId() == id).findFirst().orElse(null);
    }
} 