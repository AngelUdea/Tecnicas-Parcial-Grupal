/**
 * Clase principal que inicia la aplicación del Sistema de Minimercado.
 * Esta clase implementa el patrón Singleton para asegurar una única instancia
 * de la aplicación y sus controladores.
 * 
 * La aplicación sigue una arquitectura MVC (Modelo-Vista-Controlador):
 * - Modelo: Clases en el paquete modelo/
 * - Vista: Clases en el paquete gui/
 * - Controlador: Clases en el paquete controlador/
 */
package com.minimercado;

import com.minimercado.controlador.ControladorProducto;
import com.minimercado.controlador.ControladorCliente;
import com.minimercado.controlador.ControladorVenta;
import com.minimercado.gui.VentanaPrincipal;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        // Configuración del Look and Feel del sistema
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Inicialización de la aplicación en el hilo de eventos de Swing
        SwingUtilities.invokeLater(() -> {
            // Creación de los controladores (parte del MVC)
            ControladorProducto controladorProducto = new ControladorProducto();
            ControladorCliente controladorCliente = new ControladorCliente();
            // El controlador de ventas necesita acceso a productos y clientes
            ControladorVenta controladorVenta = new ControladorVenta(controladorProducto, controladorCliente);
            
            // Obtención de la instancia única de la ventana principal (Singleton)
            VentanaPrincipal ventana = VentanaPrincipal.getInstance();
            // Inicialización de los controladores en la ventana principal
            ventana.inicializarControladores(controladorProducto, controladorCliente, controladorVenta);
            // Mostrar la ventana principal
            ventana.setVisible(true);
        });
    }
} 