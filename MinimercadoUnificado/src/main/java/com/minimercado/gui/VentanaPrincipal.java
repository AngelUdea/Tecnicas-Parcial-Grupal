/**
 * Clase principal de la interfaz gráfica que implementa el patrón Singleton.
 * Esta clase es la ventana principal de la aplicación y contiene todos los paneles
 * de la interfaz de usuario.
 * 
 * Características principales:
 * - Implementa el patrón Singleton para una única instancia
 * - Maneja la navegación entre paneles
 * - Coordina la comunicación entre paneles
 * - Mantiene la consistencia de la interfaz
 * 
 * Estructura de paneles:
 * - PanelProductos: Gestión de productos
 * - PanelClientes: Gestión de clientes
 * - PanelVentas: Proceso de ventas
 * - PanelFacturacion: Historial de facturas
 * 
 * Notas técnicas:
 * - Utiliza JTabbedPane para la navegación
 * - Implementa un diseño moderno y responsivo
 * - Maneja la actualización de datos en tiempo real
 */
package com.minimercado.gui;

import com.minimercado.controlador.ControladorCliente;
import com.minimercado.controlador.ControladorProducto;
import com.minimercado.controlador.ControladorVenta;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class VentanaPrincipal extends JFrame {
    private static VentanaPrincipal instance;
    private JTabbedPane tabbedPane;
    private PanelProductos panelProductos;
    private PanelClientes panelClientes;
    private PanelVentas panelVentas;
    private PanelFacturacion panelFacturacion;
    private ControladorProducto productoController;
    private ControladorCliente clienteController;
    private ControladorVenta ventaController;
    
    private VentanaPrincipal() {
        initComponents();
    }
    
    public static VentanaPrincipal getInstance() {
        if (instance == null) {
            instance = new VentanaPrincipal();
        }
        return instance;
    }
    
    public void inicializarControladores(ControladorProducto productoController, 
                                       ControladorCliente clienteController,
                                       ControladorVenta ventaController) {
        this.productoController = productoController;
        this.clienteController = clienteController;
        this.ventaController = ventaController;
        panelProductos = new PanelProductos(productoController);
        panelClientes = new PanelClientes(clienteController);
        panelVentas = new PanelVentas(ventaController, productoController, clienteController);
        panelFacturacion = new PanelFacturacion(ventaController, this);
        tabbedPane.removeAll();
        tabbedPane.addTab("Productos", panelProductos);
        tabbedPane.addTab("Clientes", panelClientes);
        tabbedPane.addTab("Ventas", panelVentas);
        tabbedPane.addTab("Facturación", panelFacturacion);
    }
    
    private void initComponents() {
        setTitle("Sistema de Minimercado");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        add(tabbedPane);
    }
    
    public void actualizarPanelVentas() {
        panelVentas.actualizarDatos();
    }
    
    public void ventaFinalizada() {
        panelFacturacion.actualizarFacturas();
    }
} 