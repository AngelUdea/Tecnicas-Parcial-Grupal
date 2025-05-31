/**
 * Panel que maneja la interfaz de usuario para el proceso de ventas.
 * Este panel permite la selección de clientes y productos, y la creación
 * de nuevas ventas en el sistema.
 * 
 * Características principales:
 * - Selección de cliente y productos
 * - Control de cantidades
 * - Cálculo de totales en tiempo real
 * - Validación de operaciones
 * 
 * Componentes principales:
 * - ComboBox para selección de cliente
 * - ComboBox para selección de producto
 * - Spinner para control de cantidades
 * - Tabla de productos en la venta
 * - Botones de control
 * 
 * Notas técnicas:
 * - Implementa un diseño moderno con tema oscuro
 * - Maneja la actualización en tiempo real
 * - Valida el stock disponible
 * - Coordina con el controlador de ventas
 */
package com.minimercado.gui;

import com.minimercado.modelo.Venta;
import com.minimercado.modelo.DetalleVenta;
import com.minimercado.modelo.Producto;
import com.minimercado.modelo.Cliente;
import com.minimercado.controlador.ControladorVenta;
import com.minimercado.controlador.ControladorProducto;
import com.minimercado.controlador.ControladorCliente;
import com.minimercado.controlador.ProductoChangeListener;
import com.minimercado.controlador.ClienteChangeListener;
import com.minimercado.util.FileManager;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PanelVentas extends JPanel implements ProductoChangeListener, ClienteChangeListener {
    // Componentes de la interfaz
    private JTable tablaVentas;          // Tabla para mostrar los productos en la venta
    private DefaultTableModel modelo;    // Modelo de datos para la tabla
    private JComboBox<Cliente> cmbCliente;   // ComboBox para seleccionar cliente
    private JComboBox<Producto> cmbProducto; // ComboBox para seleccionar producto
    private JSpinner spnCantidad;        // Spinner para seleccionar cantidad
    private JButton btnAgregar, btnEliminar, btnFinalizar;  // Botones de control
    
    // Controladores y estado
    private ControladorVenta ventaController;
    private ControladorProducto productoController;
    private ControladorCliente clienteController;
    private Venta ventaActual;           // Venta en proceso
    
    // Constructor del panel
    public PanelVentas(ControladorVenta ventaController, ControladorProducto productoController, ControladorCliente clienteController) {
        this.ventaController = ventaController;
        this.productoController = productoController;
        this.clienteController = clienteController;
        initComponents();
        cargarClientes();
        cargarProductos();
        // Registramos este panel como listener de productos y clientes
        productoController.addProductoChangeListener(this);
        clienteController.addClienteChangeListener(this);
    }
    
    // Inicialización de componentes de la interfaz
    private void initComponents() {
        // Configuración del layout y colores
        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(30, 30, 30));
        
        // Panel del formulario
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(30, 30, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        
        // Configuración de fuentes y colores
        Font labelFont = new Font("Segoe UI", Font.BOLD, 16);
        Font fieldFont = new Font("Segoe UI", Font.PLAIN, 16);
        Color fg = Color.WHITE;
        Color bgField = new Color(50, 50, 50);
        
        // Inicialización de componentes
        cmbCliente = new JComboBox<>(); cmbCliente.setFont(fieldFont); cmbCliente.setBackground(bgField); cmbCliente.setForeground(fg);
        cmbProducto = new JComboBox<>(); cmbProducto.setFont(fieldFont); cmbProducto.setBackground(bgField); cmbProducto.setForeground(fg);
        spnCantidad = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1)); spnCantidad.setFont(fieldFont);
        
        // Agregar campos al formulario
        String[] labels = {"Cliente:", "Producto:", "Cantidad:"};
        Component[] fields = {cmbCliente, cmbProducto, spnCantidad};
        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0; gbc.gridy = i;
            JLabel lbl = new JLabel(labels[i]);
            lbl.setFont(labelFont); lbl.setForeground(fg);
            formPanel.add(lbl, gbc);
            gbc.gridx = 1;
            formPanel.add(fields[i], gbc);
        }
        
        // Panel de botones
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(30, 30, 30));
        btnAgregar = new JButton("Agregar");
        btnEliminar = new JButton("Eliminar");
        btnFinalizar = new JButton("Finalizar");
        
        // Configuración de botones
        for (JButton btn : new JButton[]{btnAgregar, btnEliminar, btnFinalizar}) {
            btn.setFont(labelFont);
            btn.setPreferredSize(new Dimension(130, 40));
            btn.setBackground(new Color(35, 35, 35));
            btn.setForeground(new Color(200, 200, 200));
            buttonPanel.add(btn);
        }
        
        // Agregar panel de botones al formulario
        gbc.gridx = 0; gbc.gridy = labels.length; gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);
        
        // Configuración de la tabla
        String[] columnas = {"ID", "Producto", "Cantidad", "Precio Unit.", "Subtotal", "IVA", "Descuento", "Total"};
        modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // Inicialización y configuración de la tabla
        tablaVentas = new JTable(modelo);
        tablaVentas.setFont(fieldFont);
        tablaVentas.setRowHeight(28);
        tablaVentas.getTableHeader().setFont(labelFont);
        tablaVentas.setBackground(bgField);
        tablaVentas.setForeground(fg);
        tablaVentas.getTableHeader().setBackground(new Color(40, 40, 40));
        tablaVentas.getTableHeader().setForeground(Color.BLACK);
        
        // Agregar tabla con scroll
        JScrollPane scrollPane = new JScrollPane(tablaVentas);
        scrollPane.getViewport().setBackground(bgField);
        
        // Agregar componentes al panel principal
        add(formPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        
        // Agregar listeners a los botones
        btnAgregar.addActionListener(e -> agregarProducto());
        btnEliminar.addActionListener(e -> eliminarProducto());
        btnFinalizar.addActionListener(e -> finalizarVenta());
    }
    
    // Método para actualizar los datos del panel
    public void actualizarDatos() {
        cargarClientes();
        cargarProductos();
    }
    
    // Cargar clientes en el ComboBox
    private void cargarClientes() {
        cmbCliente.removeAllItems();
        List<Cliente> clientes = clienteController.obtenerClientes();
        for (Cliente c : clientes) {
            cmbCliente.addItem(c);
        }
    }
    
    // Cargar productos en el ComboBox
    private void cargarProductos() {
        cmbProducto.removeAllItems();
        List<Producto> productos = productoController.obtenerProductos();
        for (Producto p : productos) {
            cmbProducto.addItem(p);
        }
    }
    
    // Método para agregar un producto a la venta
    private void agregarProducto() {
        // Verificar si hay una venta en proceso
        if (ventaActual == null) {
            Cliente cliente = (Cliente) cmbCliente.getSelectedItem();
            if (cliente == null) {
                JOptionPane.showMessageDialog(this, "Por favor seleccione un cliente", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            ventaActual = ventaController.crearVenta(cliente);
        }
        
        // Validar selección de producto
        Producto producto = (Producto) cmbProducto.getSelectedItem();
        if (producto == null) {
            JOptionPane.showMessageDialog(this, "Por favor seleccione un producto", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Validar cantidad
        int cantidad = (int) spnCantidad.getValue();
        if (cantidad <= 0) {
            JOptionPane.showMessageDialog(this, "La cantidad debe ser mayor a 0", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Agregar producto a la venta
        try {
            ventaController.agregarProductoAVenta(ventaActual, producto, cantidad);
            actualizarTablaVentas();
            spnCantidad.setValue(1);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Método para eliminar un producto de la venta
    private void eliminarProducto() {
        int fila = tablaVentas.getSelectedRow();
        if (fila >= 0 && ventaActual != null) {
            DetalleVenta detalle = ventaActual.getDetalles().get(fila);
            ventaController.eliminarProductoDeVenta(ventaActual, detalle);
            actualizarTablaVentas();
        } else {
            JOptionPane.showMessageDialog(this, "Por favor seleccione un producto", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Método para finalizar la venta
    private void finalizarVenta() {
        if (ventaActual != null && !ventaActual.getDetalles().isEmpty()) {
            int confirmacion = JOptionPane.showConfirmDialog(this,
                    "¿Está seguro de finalizar esta venta?",
                    "Confirmar venta",
                    JOptionPane.YES_NO_OPTION);
            if (confirmacion == JOptionPane.YES_OPTION) {
                try {
                    FileManager.generarFacturaPDF(ventaActual);
                    ventaActual = null;
                    actualizarTablaVentas();
                    JOptionPane.showMessageDialog(this, "Venta finalizada exitosamente");
                    VentanaPrincipal ventana = (VentanaPrincipal) SwingUtilities.getWindowAncestor(this);
                    ventana.ventaFinalizada();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "No hay productos en la venta", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Método para actualizar la tabla de ventas
    private void actualizarTablaVentas() {
        modelo.setRowCount(0);
        if (ventaActual != null) {
            for (DetalleVenta d : ventaActual.getDetalles()) {
                Object[] fila = {
                    d.getProducto().getId(),
                    d.getProducto().getNombre(),
                    d.getCantidad(),
                    String.format("$%.2f", d.getProducto().getPrecio()),
                    String.format("$%.2f", d.getSubtotalBase()),
                    String.format("$%.2f", d.getIvaMonto()),
                    String.format("$%.2f", d.getDescuentoMonto()),
                    String.format("$%.2f", d.getTotalDetalle())
                };
                modelo.addRow(fila);
            }
        }
    }

    // Implementación del listener de clientes
    @Override
    public void onClientesChanged() {
        // Actualizamos la lista de clientes en el combo box
        cargarClientes();
    }
    
    // Implementación del listener de productos
    @Override
    public void onProductosChanged() {
        // Actualizamos la lista de productos cuando hay cambios
        cargarProductos();
    }
} 