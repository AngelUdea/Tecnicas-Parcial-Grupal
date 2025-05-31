/**
 * Panel que maneja la interfaz de usuario para la gestión de productos.
 * Este panel permite crear, modificar y eliminar productos del sistema,
 * así como controlar su inventario.
 * 
 * Características principales:
 * - Gestión completa de productos
 * - Control de inventario
 * - Configuración de precios
 * - Manejo de IVA y descuentos
 * 
 * Componentes principales:
 * - Formulario de producto
 * - Tabla de productos
 * - Controles de stock
 * - Campos de precios y descuentos
 * 
 * Notas técnicas:
 * - Implementa un diseño moderno con tema oscuro
 * - Actualiza en tiempo real
 * - Coordina con el controlador de productos
 */
package com.minimercado.gui;

import com.minimercado.modelo.Producto;
import com.minimercado.controlador.ControladorProducto;
import com.minimercado.controlador.ProductoChangeListener;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

public class PanelProductos extends JPanel implements ProductoChangeListener {
    private JTable tablaProductos;
    private DefaultTableModel modelo;
    private JTextField txtNombre, txtDescripcion, txtPrecio, txtStock;
    private JSpinner spnStock, spnIva, spnDescuento;
    private JButton btnNuevo, btnGuardar, btnEliminar, btnLimpiar;
    private ControladorProducto productoController;
    
    // Definimos la configuración regional para Colombia
    private static final Locale LOCALE = Locale.US;
    
    public PanelProductos(ControladorProducto productoController) {
        this.productoController = productoController;
        initComponents();
        cargarProductos();
        productoController.addProductoChangeListener(this);
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(30, 30, 30));
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(30, 30, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        
        Font labelFont = new Font("Segoe UI", Font.BOLD, 16);
        Font fieldFont = new Font("Segoe UI", Font.PLAIN, 16);
        Color fg = Color.WHITE;
        Color bgField = new Color(50, 50, 50);
        
        txtNombre = new JTextField(20); txtNombre.setFont(fieldFont); txtNombre.setBackground(bgField); txtNombre.setForeground(fg);
        txtDescripcion = new JTextField(20); txtDescripcion.setFont(fieldFont); txtDescripcion.setBackground(bgField); txtDescripcion.setForeground(fg);
        txtPrecio = new JTextField(20); txtPrecio.setFont(fieldFont); txtPrecio.setBackground(bgField); txtPrecio.setForeground(fg);
        txtStock = new JTextField(20); txtStock.setFont(fieldFont); txtStock.setBackground(bgField); txtStock.setForeground(fg);
        
        // Configuramos los spinners para IVA y Descuento
        spnStock = new JSpinner(new SpinnerNumberModel(0, 0, 10000, 1)); spnStock.setFont(fieldFont);
        spnIva = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 100.0, 1.0)); spnIva.setFont(fieldFont);
        spnDescuento = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 100.0, 1.0)); spnDescuento.setFont(fieldFont);
        
        String[] labels = {"Nombre:", "Descripción:", "Precio:", "Stock:", "IVA (%):", "Descuento (%):"};
        Component[] fields = {txtNombre, txtDescripcion, txtPrecio, txtStock, spnIva, spnDescuento};
        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0; gbc.gridy = i;
            JLabel lbl = new JLabel(labels[i]);
            lbl.setFont(labelFont); lbl.setForeground(fg);
            formPanel.add(lbl, gbc);
            gbc.gridx = 1;
            formPanel.add(fields[i], gbc);
        }
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(30, 30, 30));
        btnNuevo = new JButton("Nuevo");
        btnGuardar = new JButton("Guardar");
        btnEliminar = new JButton("Eliminar");
        btnLimpiar = new JButton("Limpiar");
        for (JButton btn : new JButton[]{btnNuevo, btnGuardar, btnEliminar, btnLimpiar}) {
            btn.setFont(labelFont);
            btn.setPreferredSize(new Dimension(130, 40));
            btn.setBackground(new Color(35, 35, 35));
            btn.setForeground(new Color(200, 200, 200));
            buttonPanel.add(btn);
        }
        gbc.gridx = 0; gbc.gridy = labels.length; gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);
        String[] columnas = {"ID", "Nombre", "Descripción", "Precio", "IVA", "Descuento", "Stock", "Precio Final"};
        modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaProductos = new JTable(modelo);
        tablaProductos.setFont(fieldFont);
        tablaProductos.setRowHeight(28);
        tablaProductos.getTableHeader().setFont(labelFont);
        tablaProductos.setBackground(bgField);
        tablaProductos.setForeground(fg);
        tablaProductos.getTableHeader().setBackground(new Color(40, 40, 40));
        tablaProductos.getTableHeader().setForeground(Color.BLACK);
        JScrollPane scrollPane = new JScrollPane(tablaProductos);
        scrollPane.getViewport().setBackground(bgField);
        add(formPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        btnNuevo.addActionListener(e -> limpiarFormulario());
        btnGuardar.addActionListener(e -> guardarProducto());
        btnEliminar.addActionListener(e -> eliminarProducto());
        btnLimpiar.addActionListener(e -> limpiarFormulario());
        tablaProductos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) seleccionarProducto();
        });
    }
    
    public void actualizarDatos() {
        cargarProductos();
    }
    
    private void cargarProductos() {
        modelo.setRowCount(0);
        List<Producto> productos = productoController.obtenerProductos();
        for (Producto p : productos) {
            Object[] fila = {
                p.getId(),
                p.getNombre(),
                p.getDescripcion(),
                // Usamos String.format con LOCALE para mostrar el precio con el separador decimal correcto
                String.format(LOCALE, "%.2f", p.getPrecio()),
                String.format(LOCALE, "%.0f%%", p.getIva() * 100),
                String.format(LOCALE, "%.0f%%", p.getDescuento() * 100),
                p.getStock(),
                // Usamos String.format con LOCALE para mostrar el precio final
                String.format(LOCALE, "%.2f", p.getPrecioFinal())
            };
            modelo.addRow(fila);
        }
    }
    
    @Override
    public void onProductosChanged() {
        cargarProductos();
    }
    
    private void guardarProducto() {
        try {
            Producto producto = obtenerProductoFormulario();
            
            // Verificar si hay un producto seleccionado en la tabla para actualizar
            int filaSeleccionada = tablaProductos.getSelectedRow();
            if (filaSeleccionada >= 0) {
                // Si hay una fila seleccionada, obtenemos el ID del producto existente
                int idProductoExistente = (int) modelo.getValueAt(filaSeleccionada, 0);
                producto.setId(idProductoExistente); // Asignamos el ID al producto del formulario
                productoController.actualizarProducto(producto); // Llamamos al método de actualizar
                JOptionPane.showMessageDialog(this, "Producto actualizado exitosamente");
            } else {
                // Si no hay fila seleccionada, agregamos como producto nuevo
                productoController.agregarProducto(producto);
                 JOptionPane.showMessageDialog(this, "Producto guardado exitosamente");
            }
            
            limpiarFormulario();
            // No es necesario actualizar el panel de ventas aquí, ya se actualiza vía listener
            // VentanaPrincipal ventana = (VentanaPrincipal) SwingUtilities.getWindowAncestor(this);
            // ventana.actualizarPanelVentas(); 
           
        } catch (NumberFormatException | ParseException e) { // Capturamos NumberFormatException y ParseException
            JOptionPane.showMessageDialog(this, "Por favor ingrese valores numéricos válidos para precio, stock, IVA y descuento. Use el formato numérico de su región.",
                "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void eliminarProducto() {
        int fila = tablaProductos.getSelectedRow();
        if (fila >= 0) {
            int id = (int) modelo.getValueAt(fila, 0);
            int confirmacion = JOptionPane.showConfirmDialog(this,
                    "¿Está seguro de eliminar este producto?",
                    "Confirmar eliminación",
                    JOptionPane.YES_NO_OPTION);
            if (confirmacion == JOptionPane.YES_OPTION) {
                try {
                    Producto producto = productoController.buscarProductoPorId(id);
                    if (producto != null) {
                        productoController.eliminarProducto(producto);
                        limpiarFormulario();
                        JOptionPane.showMessageDialog(this, "Producto eliminado exitosamente");
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Por favor seleccione un producto", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void seleccionarProducto() {
        int fila = tablaProductos.getSelectedRow();
        if (fila >= 0) {
            int id = (int) modelo.getValueAt(fila, 0);
            Producto producto = productoController.buscarProductoPorId(id);
            
            if (producto != null) {
                mostrarProducto(producto);
            }
        }
    }
    
    private void limpiarFormulario() {
        txtNombre.setText("");
        txtDescripcion.setText("");
        txtPrecio.setText("");
        txtStock.setText("");
        // Restablecer spinners a valores por defecto o vacíos si aplica
        spnStock.setValue(0);
        spnIva.setValue(0.0);
        spnDescuento.setValue(0.0);
        tablaProductos.clearSelection();
    }
    
    private void mostrarProducto(Producto producto) {
        txtNombre.setText(producto.getNombre());
        txtDescripcion.setText(producto.getDescripcion());
        // Usamos String.format con LOCALE para mostrar el precio con el separador decimal correcto
        txtPrecio.setText(String.format(LOCALE, "%.2f", producto.getPrecio()));
        // Mostramos IVA y Descuento como porcentajes en los spinners
        spnIva.setValue(producto.getIva() * 100.0);
        spnDescuento.setValue(producto.getDescuento() * 100.0);
        txtStock.setText(String.valueOf(producto.getStock()));
    }
    
    private Producto obtenerProductoFormulario() throws ParseException, NumberFormatException {
        Producto producto = new Producto();
        producto.setNombre(txtNombre.getText());
        producto.setDescripcion(txtDescripcion.getText());
        
        // Usamos NumberFormat para leer los valores numéricos según el LOCALE
        NumberFormat nf = NumberFormat.getInstance(LOCALE);
        
        producto.setPrecio(nf.parse(txtPrecio.getText()).doubleValue());
        producto.setStock(nf.parse(txtStock.getText()).intValue()); // Leer stock como entero
        
        // Obtenemos los valores directamente de los spinners y los convertimos a decimal
        double iva = ((Number) spnIva.getValue()).doubleValue();
        producto.setIva(iva / 100.0); // Convertimos de porcentaje a decimal
        
        double desc = ((Number) spnDescuento.getValue()).doubleValue();
        producto.setDescuento(desc / 100.0); // Convertimos de porcentaje a decimal
        
        return producto;
    }
} 