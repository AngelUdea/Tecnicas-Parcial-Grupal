package com.minimercado.util;

import com.minimercado.modelo.Cliente;
import com.minimercado.modelo.Producto;
import java.util.ArrayList;
import java.util.List;

public class InicializadorDatos {
    
    public static void inicializarDatosPorDefecto() {
        List<Cliente> clientes = new ArrayList<>();
        
        Cliente cliente1 = new Cliente();
        cliente1.setId(1);
        cliente1.setNombre("Juan Pérez");
        cliente1.setEmail("juan@email.com");
        cliente1.setTelefono("123456789");
        cliente1.setDireccion("Calle Principal 123");
        clientes.add(cliente1);
        
        Cliente cliente2 = new Cliente();
        cliente2.setId(2);
        cliente2.setNombre("María García");
        cliente2.setEmail("maria@email.com");
        cliente2.setTelefono("987654321");
        cliente2.setDireccion("Avenida Central 456");
        clientes.add(cliente2);
        
        Cliente cliente3 = new Cliente();
        cliente3.setId(3);
        cliente3.setNombre("Carlos López");
        cliente3.setEmail("carlos@email.com");
        cliente3.setTelefono("456789123");
        cliente3.setDireccion("Plaza Mayor 789");
        clientes.add(cliente3);
        
        FileManager.guardarClientes(clientes);
        List<Producto> productos = new ArrayList<>();
        
        Producto producto1 = new Producto();
        producto1.setId(1);
        producto1.setNombre("Arroz");
        producto1.setDescripcion("Arroz blanco premium");
        producto1.setPrecio(2.50);
        producto1.setIva(0.19);
        producto1.setDescuento(0.0);
        producto1.setStock(100);
        productos.add(producto1);
        
        Producto producto2 = new Producto();
        producto2.setId(2);
        producto2.setNombre("Leche");
        producto2.setDescripcion("Leche entera 1L");
        producto2.setPrecio(1.80);
        producto2.setIva(0.19);
        producto2.setDescuento(0.0);
        producto2.setStock(50);
        productos.add(producto2);
        
        Producto producto3 = new Producto();
        producto3.setId(3);
        producto3.setNombre("Pan");
        producto3.setDescripcion("Pan blanco fresco");
        producto3.setPrecio(1.20);
        producto3.setIva(0.19);
        producto3.setDescuento(0.0);
        producto3.setStock(30);
        productos.add(producto3);
        
        FileManager.guardarProductos(productos);
    }
} 