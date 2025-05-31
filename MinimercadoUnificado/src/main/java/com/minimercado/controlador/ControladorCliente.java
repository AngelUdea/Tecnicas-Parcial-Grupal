/**
 * Controlador que maneja toda la lógica de negocio relacionada con los clientes.
 * Esta clase implementa el patrón Controlador del MVC y coordina las operaciones
 * entre el modelo (Cliente) y la vista.
 * 
 * Funcionalidades principales:
 * - Gestión de la base de clientes
 * - Validación de datos de clientes
 * - Integración con el sistema de ventas
 * 
 * Notas técnicas:
 * - Mantiene una lista de clientes
 * - Coordina la persistencia de datos
 * - Valida la información de contacto
 * - Maneja la relación con las ventas
 */
package com.minimercado.controlador;

import com.minimercado.modelo.Cliente;
import com.minimercado.util.FileManager;
import java.util.ArrayList;
import java.util.List;

public class ControladorCliente {
    // Lista que mantiene todos los clientes del sistema
    private List<Cliente> clientes;
    private List<ClienteChangeListener> listeners;
    
    // Constructor que inicializa el controlador
    public ControladorCliente() {
        // Carga los clientes desde el archivo
        this.clientes = FileManager.cargarClientes();
        this.listeners = new ArrayList<>();
    }
    
    public void addClienteChangeListener(ClienteChangeListener listener) {
        listeners.add(listener);
    }
    
    public void removeClienteChangeListener(ClienteChangeListener listener) {
        listeners.remove(listener);
    }
    
    private void notifyClientesChanged() {
        for (ClienteChangeListener listener : listeners) {
            listener.onClientesChanged();
        }
    }
    
    // Método para agregar un nuevo cliente
    public void agregarCliente(Cliente cliente) {
        // Asigna un nuevo ID si es necesario
        if (cliente.getId() == 0) {
            int nuevoId = clientes.isEmpty() ? 1 : clientes.get(clientes.size() - 1).getId() + 1;
            cliente.setId(nuevoId);
        }
        clientes.add(cliente);
        // Guarda los cambios en el archivo
        FileManager.guardarClientes(clientes);
        notifyClientesChanged();
    }
    
    // Método para eliminar un cliente
    public void eliminarCliente(Cliente cliente) {
        clientes.remove(cliente);
        // Guarda los cambios en el archivo
        FileManager.guardarClientes(clientes);
        notifyClientesChanged();
    }
    
    // Método para actualizar un cliente existente
    public void actualizarCliente(Cliente cliente) {
        int indice = -1;
        // Busca el cliente por su ID
        for (int i = 0; i < clientes.size(); i++) {
            if (clientes.get(i).getId() == cliente.getId()) {
                indice = i;
                break;
            }
        }
        // Si encuentra el cliente, lo actualiza
        if (indice != -1) {
            clientes.set(indice, cliente);
            // Guarda los cambios en el archivo
            FileManager.guardarClientes(clientes);
            notifyClientesChanged();
        }
    }
    
    // Método para obtener todos los clientes
    public List<Cliente> obtenerClientes() {
        return new ArrayList<>(clientes);
    }
    
    // Método para buscar un cliente por su ID
    public Cliente buscarClientePorId(int id) {
        System.out.println("Searching for client with ID: " + id);
        System.out.println("Clientes list size: " + clientes.size());
        return clientes.stream().filter(c -> {
                    boolean found = c.getId() == id;
                    System.out.println("Comparing client ID " + c.getId() + " with " + id + ": " + found);
                    return found;
                }).findFirst().orElse(null);
    }
} 