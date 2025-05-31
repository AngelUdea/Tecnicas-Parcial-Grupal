/**
 * Panel que maneja la interfaz de usuario para la gestión de facturas.
 * Este panel permite visualizar el historial de ventas, ver detalles
 * de facturas y generar reportes.
 * 
 * Características principales:
 * - Visualización de historial de ventas
 * - Detalle de facturas individuales
 * - Generación de facturas PDF
 * - Eliminación de facturas
 * 
 * Componentes principales:
 * - Tabla de facturas
 * - Botones de control
 * - Diálogo de detalle de factura
 * 
 * Notas técnicas:
 * - Implementa un diseño moderno con tema oscuro
 * - Maneja la generación de PDFs
 * - Coordina con el controlador de ventas
 * - Mantiene la consistencia de datos
 */
package com.minimercado.gui;

import com.minimercado.modelo.Venta;
import com.minimercado.modelo.DetalleVenta;
import com.minimercado.controlador.ControladorVenta;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.text.SimpleDateFormat;

public class PanelFacturacion extends JPanel {
    private JTable tablaFacturas;
    private DefaultTableModel modelo;
    private JButton btnVerDetalle;
    private JButton btnEliminarFactura;
    private ControladorVenta ventaController;
    private VentanaPrincipal ventanaPrincipal;
    
    public PanelFacturacion(ControladorVenta ventaController, VentanaPrincipal ventanaPrincipal) {
        this.ventaController = ventaController;
        this.ventanaPrincipal = ventanaPrincipal;
        initComponents();
        cargarFacturas();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(30, 30, 30));
        
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(new Color(30, 30, 30));
        btnVerDetalle = new JButton("Ver Detalle");
        btnVerDetalle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnVerDetalle.setPreferredSize(new Dimension(130, 40));
        btnVerDetalle.setBackground(new Color(35, 35, 35));
        btnVerDetalle.setForeground(new Color(200, 200, 200));
        headerPanel.add(btnVerDetalle);
        
        btnEliminarFactura = new JButton("Eliminar Factura");
        btnEliminarFactura.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnEliminarFactura.setPreferredSize(new Dimension(150, 40));
        btnEliminarFactura.setBackground(new Color(35, 35, 35));
        btnEliminarFactura.setForeground(new Color(200, 200, 200));
        headerPanel.add(btnEliminarFactura);
        
        String[] columnas = {"ID", "Cliente", "Fecha", "Total"};
        modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaFacturas = new JTable(modelo);
        tablaFacturas.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tablaFacturas.setRowHeight(28);
        tablaFacturas.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tablaFacturas.setBackground(new Color(50, 50, 50));
        tablaFacturas.setForeground(Color.WHITE);
        tablaFacturas.getTableHeader().setBackground(new Color(40, 40, 40));
        tablaFacturas.getTableHeader().setForeground(Color.BLACK);
        
        JScrollPane scrollPane = new JScrollPane(tablaFacturas);
        scrollPane.getViewport().setBackground(new Color(50, 50, 50));
        
        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        
        btnVerDetalle.addActionListener(e -> mostrarDetalleFactura());
        btnEliminarFactura.addActionListener(e -> eliminarFactura());
    }
    
    private void cargarFacturas() {
        modelo.setRowCount(0);
        List<Venta> ventas = ventaController.obtenerVentas();
        for (Venta v : ventas) {
            Object[] fila = {
                v.getId(),
                v.getCliente().getNombre() + " " + v.getCliente().getApellido(),
                new SimpleDateFormat("dd 'de' MMMM 'de' yyyy").format(v.getFecha()),
                String.format("$%.2f", v.getTotal())
            };
            modelo.addRow(fila);
        }
    }
    
    private void mostrarDetalleFactura() {
        int fila = tablaFacturas.getSelectedRow();
        if (fila >= 0) {
            int id = (int) modelo.getValueAt(fila, 0);
            List<Venta> ventas = ventaController.obtenerVentas();
            Venta venta = ventas.stream().filter(v -> v.getId() == id).findFirst().orElse(null);
            
            if (venta != null) {
                StringBuilder detalle = new StringBuilder();
                detalle.append("Factura #").append(venta.getId()).append("\n");
                detalle.append("Cliente: ").append(venta.getCliente().getNombre())
                        .append(" ").append(venta.getCliente().getApellido()).append("\n");
                detalle.append("Fecha: ").append(new SimpleDateFormat("dd 'de' MMMM 'de' yyyy").format(venta.getFecha())).append("\n\n");
                detalle.append("Detalle de Productos:\n");
                
                for (DetalleVenta d : venta.getDetalles()) {
                    detalle.append(String.format("%s x%d $%.2f = $%.2f\n",
                            d.getProducto().getNombre(),
                            d.getCantidad(),
                            d.getPrecioUnitario(),
                            d.getTotalDetalle()));
                }
                
                detalle.append("\nSubtotal (Base): $").append(String.format("%.2f", venta.getSubtotal())).append("\n");
                detalle.append("IVA (Total): $").append(String.format("%.2f", venta.getIva())).append("\n");
                detalle.append("Descuento (Total): $").append(String.format("%.2f", venta.getDescuento())).append("\n");
                detalle.append("Total: $").append(String.format("%.2f", venta.getTotal()));
                
                JTextArea textArea = new JTextArea(detalle.toString());
                textArea.setEditable(false);
                textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
                
                JScrollPane scrollPane = new JScrollPane(textArea);
                scrollPane.setPreferredSize(new Dimension(400, 300));
                
                JOptionPane.showMessageDialog(this,
                        scrollPane,
                        "Detalle de Factura",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "Por favor seleccione una factura",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void eliminarFactura() {
        int fila = tablaFacturas.getSelectedRow();
        if (fila >= 0) {
            int id = (int) modelo.getValueAt(fila, 0);
            Venta ventaToDelete = ventaController.obtenerVentas().stream().filter(v -> v.getId() == id).findFirst().orElse(null);
            
            if (ventaToDelete != null) {
                ventaController.eliminarVenta(ventaToDelete);
                cargarFacturas();
                ventanaPrincipal.actualizarPanelVentas();
            } else {
                JOptionPane.showMessageDialog(this, "No se encontró la factura seleccionada", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "Por favor seleccione una factura",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void actualizarFacturas() {
        cargarFacturas();
    }
} 