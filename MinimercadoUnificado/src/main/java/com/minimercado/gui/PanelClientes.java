/**
 * Panel que maneja la interfaz de usuario para la gestión de clientes.
 * Este panel permite crear, modificar y eliminar clientes del sistema,
 * así como ver su historial de compras.
 * 
 * Características principales:
 * - Gestión completa de clientes
 * - Historial de compras
 * 
 * Componentes principales:
 * - Formulario de cliente
 * - Tabla de clientes
 * - Campos de contacto
 * - Botones de control
 * 
 * Notas técnicas:
 * - Implementa un diseño moderno con tema oscuro
 * - Actualiza en tiempo real
 * - Coordina con el controlador de clientes
 */
package com.minimercado.gui;

import com.minimercado.modelo.Cliente;
import com.minimercado.controlador.ControladorCliente;
import com.minimercado.controlador.ClienteChangeListener;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import javax.swing.table.JTableHeader;

public class PanelClientes extends JPanel implements ClienteChangeListener {
    private JTable tablaClientes;
    private DefaultTableModel modelo;
    private JTextField txtNombre, txtApellido, txtEmail, txtTelefono;
    private JButton btnNuevo, btnGuardar, btnEliminar, btnLimpiar;
    private ControladorCliente clienteController;
    
    public PanelClientes(ControladorCliente clienteController) {
        this.clienteController = clienteController;
        initComponents();
        cargarClientes();
        clienteController.addClienteChangeListener(this);
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
        txtApellido = new JTextField(20); txtApellido.setFont(fieldFont); txtApellido.setBackground(bgField); txtApellido.setForeground(fg);
        txtEmail = new JTextField(20); txtEmail.setFont(fieldFont); txtEmail.setBackground(bgField); txtEmail.setForeground(fg);
        txtTelefono = new JTextField(20); txtTelefono.setFont(fieldFont); txtTelefono.setBackground(bgField); txtTelefono.setForeground(fg);
        
        String[] labels = {"Nombre:", "Apellido:", "Email:", "Teléfono:"};
        Component[] fields = {txtNombre, txtApellido, txtEmail, txtTelefono};
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
        
        String[] columnas = {"ID", "Nombre", "Apellido", "Email", "Teléfono"};
        modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaClientes = new JTable(modelo);
        tablaClientes.setFont(fieldFont);
        tablaClientes.setRowHeight(28);
        tablaClientes.setBackground(bgField);
        tablaClientes.setForeground(fg);
        
        JTableHeader tableHeader = tablaClientes.getTableHeader();
        tableHeader.setFont(labelFont);
        tableHeader.setBackground(new Color(40, 40, 40));
        tableHeader.setForeground(Color.BLACK);
        
        JScrollPane scrollPane = new JScrollPane(tablaClientes);
        scrollPane.getViewport().setBackground(bgField);
        
        add(formPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        
        btnNuevo.addActionListener(e -> limpiarFormulario());
        btnGuardar.addActionListener(e -> guardarCliente());
        btnEliminar.addActionListener(e -> eliminarCliente());
        btnLimpiar.addActionListener(e -> limpiarFormulario());
        
        tablaClientes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) seleccionarCliente();
        });
    }
    
    public void actualizarDatos() {
        cargarClientes();
    }
    
    private void cargarClientes() {
        modelo.setRowCount(0);
        List<Cliente> clientes = clienteController.obtenerClientes();
        for (Cliente c : clientes) {
            Object[] fila = {
                c.getId(),
                c.getNombre(),
                c.getApellido(),
                c.getEmail(),
                c.getTelefono()
            };
            modelo.addRow(fila);
        }
    }
    
    private void guardarCliente() {
        try {
            Cliente cliente = obtenerClienteFormulario();
            clienteController.agregarCliente(cliente);
            cargarClientes();
            limpiarFormulario();
            JOptionPane.showMessageDialog(this, "Cliente guardado exitosamente");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void eliminarCliente() {
        int fila = tablaClientes.getSelectedRow();
        if (fila >= 0) {
            int id = (int) modelo.getValueAt(fila, 0);
            int confirmacion = JOptionPane.showConfirmDialog(this,
                    "¿Está seguro de eliminar este cliente?",
                    "Confirmar eliminación",
                    JOptionPane.YES_NO_OPTION);
            if (confirmacion == JOptionPane.YES_OPTION) {
                try {
                    Cliente cliente = clienteController.buscarClientePorId(id);
                    if (cliente != null) {
                        clienteController.eliminarCliente(cliente);
                        cargarClientes();
                        limpiarFormulario();
                        JOptionPane.showMessageDialog(this, "Cliente eliminado exitosamente");
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Por favor seleccione un cliente", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void seleccionarCliente() {
        int fila = tablaClientes.getSelectedRow();
        if (fila >= 0) {
            int id = (int) modelo.getValueAt(fila, 0);
            Cliente cliente = clienteController.buscarClientePorId(id);
            
            if (cliente != null) {
                mostrarCliente(cliente);
            }
        }
    }
    
    private void limpiarFormulario() {
        txtNombre.setText("");
        txtApellido.setText("");
        txtEmail.setText("");
        txtTelefono.setText("");
        tablaClientes.clearSelection();
    }
    
    private void mostrarCliente(Cliente cliente) {
        txtNombre.setText(cliente.getNombre());
        txtApellido.setText(cliente.getApellido());
        txtEmail.setText(cliente.getEmail());
        txtTelefono.setText(cliente.getTelefono());
    }
    
    private Cliente obtenerClienteFormulario() {
        Cliente cliente = new Cliente();
        cliente.setNombre(txtNombre.getText());
        cliente.setApellido(txtApellido.getText());
        cliente.setEmail(txtEmail.getText());
        cliente.setTelefono(txtTelefono.getText());
        return cliente;
    }
    
    @Override
    public void onClientesChanged() {
        cargarClientes();
    }
} 