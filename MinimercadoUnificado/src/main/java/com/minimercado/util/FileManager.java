/**
 * Clase utilitaria que maneja todas las operaciones de persistencia de datos.
 * Esta clase es responsable de la lectura y escritura de archivos, incluyendo
 * la generación de facturas en PDF.
 * 
 * Funcionalidades principales:
 * - Persistencia de datos en archivos CSV
 * - Generación de facturas en PDF
 * - Manejo de directorios y archivos
 * - Inicialización del sistema
 * 
 * Notas técnicas:
 * - Utiliza iText para la generación de PDFs
 * - Maneja archivos CSV para datos estructurados
 * - Implementa manejo de errores robusto
 * - Mantiene la estructura de directorios
 * 
 * Estructura de archivos:
 * - data/
 *   - clientes.csv
 *   - productos.csv
 *   - ventas.csv
 *   - detalles_venta.csv
 *   - facturas/
 *     - Factura_X.pdf
 */
package com.minimercado.util;

import com.minimercado.modelo.*;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.text.NumberFormat;
import java.text.ParseException;

public class FileManager {
    // Definimos la configuración regional para Colombia
    private static final Locale LOCALE = Locale.US;
    
    private static final String DATA_DIR = "data";
    private static final String FACTURAS_DIR = "facturas";
    private static final String CLIENTES_FILE = "clientes.csv";
    private static final String PRODUCTOS_FILE = "productos.csv";
    private static final String VENTAS_FILE = "ventas.csv";
    private static final String DETALLES_FILE = "detalles_venta.csv";
    
    static {
        createDirectories();
        initializeFiles();
    }
    
    private static void createDirectories() {
        try {
            Files.createDirectories(Paths.get(DATA_DIR));
            Files.createDirectories(Paths.get(DATA_DIR, FACTURAS_DIR));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static void initializeFiles() {
        Path productosFile = Paths.get(DATA_DIR, PRODUCTOS_FILE);
        Path clientesFile = Paths.get(DATA_DIR, CLIENTES_FILE);
        
        if (!Files.exists(productosFile) || !Files.exists(clientesFile)) {
            InicializadorDatos.inicializarDatosPorDefecto();
        }
    }
    
    public static List<Cliente> cargarClientes() {
        List<Cliente> clientes = new ArrayList<>();
        Path file = Paths.get(DATA_DIR, CLIENTES_FILE);
        
        if (!Files.exists(file)) {
            return clientes;
        }
        
        try (BufferedReader reader = Files.newBufferedReader(file)) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    String[] data = line.split(",");
                    if (data.length >= 5) {
                        Cliente cliente = new Cliente();
                        cliente.setId(Integer.parseInt(data[0]));
                        cliente.setNombre(data[1]);
                        cliente.setEmail(data[2]);
                        cliente.setTelefono(data[3]);
                        cliente.setDireccion(data[4]);
                        clientes.add(cliente);
                    }
                } catch (Exception e) {
                    System.err.println("Error parsing client line: " + line);
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return clientes;
    }
    
    public static void guardarClientes(List<Cliente> clientes) {
        Path file = Paths.get(DATA_DIR, CLIENTES_FILE);
        try (BufferedWriter writer = Files.newBufferedWriter(file)) {
            for (Cliente c : clientes) {
                writer.write(String.format("%d,%s,%s,%s,%s\n",
                    c.getId(), c.getNombre(), c.getEmail(), c.getTelefono(), c.getDireccion()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static List<Producto> cargarProductos() {
        List<Producto> productos = new ArrayList<>();
        Path file = Paths.get(DATA_DIR, PRODUCTOS_FILE);
        
        if (!Files.exists(file)) {
            return productos;
        }
        
        // Usamos NumberFormat para manejar correctamente los decimales según el LOCALE
        NumberFormat nf = NumberFormat.getInstance(LOCALE);
        
        try (BufferedReader reader = Files.newBufferedReader(file)) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    String[] data = line.split(",");
                    if (data.length >= 7) { // Esperamos al menos 7 columnas (ID, Nombre, Desc, Precio, IVA, Descuento, Stock)
                        Producto producto = new Producto();
                        producto.setId(Integer.parseInt(data[0]));
                        producto.setNombre(data[1]);
                        producto.setDescripcion(data[2]);
                        
                        // Leemos Precio, IVA y Descuento usando NumberFormat
                        producto.setPrecio(nf.parse(data[3]).doubleValue());
                        
                        // IVA y Descuento se guardan como porcentajes en el CSV (ej: 15,00)
                        double iva = nf.parse(data[4]).doubleValue();
                        producto.setIva(iva / 100.0); // Convertimos de porcentaje a decimal
                        
                        double desc = nf.parse(data[5]).doubleValue();
                        producto.setDescuento(desc / 100.0); // Convertimos de porcentaje a decimal
                        
                        producto.setStock(Integer.parseInt(data[6]));
                        
                        productos.add(producto);
                    } else {
                         System.err.println("Línea de producto incompleta en " + PRODUCTOS_FILE + ", se esperaban 7 columnas pero se encontraron " + data.length + ": " + line);
                    }
                } catch (NumberFormatException | ParseException e) { // Capturamos también ParseException
                    System.err.println("Error de formato numérico o parseo al procesar línea de producto en " + PRODUCTOS_FILE + ": " + line + ". Error: " + e.getMessage());
                    e.printStackTrace();
                } catch (Exception e) {
                    System.err.println("Error inesperado al procesar línea de producto en " + PRODUCTOS_FILE + ": " + line + ". Error: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return productos;
    }
    
    public static void guardarProductos(List<Producto> productos) {
        Path file = Paths.get(DATA_DIR, PRODUCTOS_FILE);
        try (BufferedWriter writer = Files.newBufferedWriter(file)) {
            for (Producto p : productos) {
                // Guardamos IVA y Descuento como porcentajes usando el LOCALE para consistencia
                writer.write(String.format(LOCALE, "%d,%s,%s,%.2f,%.2f,%.2f,%d\n",
                    p.getId(), p.getNombre(), p.getDescripcion(), p.getPrecio(),
                    p.getIva() * 100.0, // Convertimos de decimal a porcentaje (ej: 0.15 -> 15.00)
                    p.getDescuento() * 100.0, // Convertimos de decimal a porcentaje (ej: 0.05 -> 5.00)
                    p.getStock()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Venta> cargarVentas() {
        List<Venta> ventas = new ArrayList<>();
        Path ventasFile = Paths.get(DATA_DIR, VENTAS_FILE);
        Path detallesFile = Paths.get(DATA_DIR, DETALLES_FILE);
        
        if (!Files.exists(ventasFile) || !Files.exists(detallesFile)) {
            return ventas;
        }
        
        try {
            Map<Integer, Venta> ventasMap = new HashMap<>();
            try (BufferedReader reader = Files.newBufferedReader(ventasFile)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    try {
                        String[] data = line.split(",");
                        if (data.length >= 6) { // ID, Fecha, ClienteID, Subtotal, IVA%, Total
                            Venta venta = new Venta();
                            venta.setId(Integer.parseInt(data[0]));
                            venta.setFecha(new Date(Long.parseLong(data[1])));
                            // Asignamos el cliente (se busca por ID)
                            venta.setCliente(buscarCliente(Integer.parseInt(data[2])));
                            // No asignamos subtotal, IVA% ni total directamente al cargar, se recalcularán
                            // venta.setSubtotal(Double.parseDouble(data[3]));
                            // venta.setIva(Double.parseDouble(data[4])); // Si quisieramos guardar el IVA% de la venta
                            // venta.setTotal(Double.parseDouble(data[5]));
                            ventasMap.put(venta.getId(), venta);
                        } else {
                            System.err.println("Línea de venta incompleta, se esperaban 6 columnas pero se encontraron " + data.length + ": " + line);
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("Error de formato numérico al procesar línea de venta: " + line);
                        e.printStackTrace();
                    } catch (Exception e) {
                        System.err.println("Error al procesar línea de venta: " + line);
                        e.printStackTrace();
                    }
                }
            }
            try (BufferedReader reader = Files.newBufferedReader(detallesFile)) {
                String line;
                NumberFormat nf = NumberFormat.getInstance(LOCALE);
                while ((line = reader.readLine()) != null) {
                    try {
                        String[] data = line.split(",");
                        if (data.length >= 6) { // ID, VentaID, ProductoID, Cantidad, PrecioUnitario, SubtotalDetalle
                            DetalleVenta detalle = new DetalleVenta();
                            detalle.setId(Integer.parseInt(data[0]));
                            int ventaId = Integer.parseInt(data[1]);
                            Producto producto = buscarProducto(Integer.parseInt(data[2]));
                            if (producto != null) {
                                detalle.setProducto(producto); // Esto debería establecer precioUnitario y recalcular totales del detalle
                                detalle.setCantidad(Integer.parseInt(data[3]));
                                // No asignamos precioUnitario ni subtotal directamente al cargar, se calculan en DetalleVenta
                                // detalle.setPrecioUnitario(nf.parse(data[4]).doubleValue());
                                // detalle.setSubtotalBase(nf.parse(data[5]).doubleValue());
                                Venta venta = ventasMap.get(ventaId);
                                if (venta != null) {
                                    venta.agregarDetalle(detalle); // Agregar detalle recalculará los totales de la venta
                                }
                            } else {
                                System.err.println("Producto no encontrado para el detalle: " + line);
                            }
                        } else {
                             System.err.println("Línea de detalle de venta incompleta, se esperaban 6 columnas pero se encontraron " + data.length + ": " + line);
                        }
                    } catch (NumberFormatException e) {
                         System.err.println("Error de formato numérico al procesar línea de detalle: " + line);
                         e.printStackTrace();
                    } catch (Exception e) {
                        System.err.println("Error al procesar línea de detalle: " + line);
                        e.printStackTrace();
                    }
                }
            }
            
            ventas.addAll(ventasMap.values());
            // Recalcular totales de venta después de cargar todos los detalles
            for (Venta venta : ventas) {
                venta.recalcularTotales();
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ventas;
    }
    
    public static void guardarVenta(Venta venta) {
        List<Venta> ventas = cargarVentas();
        boolean found = false;
        for (int i = 0; i < ventas.size(); i++) {
            if (ventas.get(i).getId() == venta.getId()) {
                ventas.set(i, venta);
                found = true;
                break;
            }
        }
        if (!found) {
            ventas.add(venta);
        }
        Path ventasFile = Paths.get(DATA_DIR, VENTAS_FILE);
        try (BufferedWriter writer = Files.newBufferedWriter(ventasFile)) {
            for (Venta v : ventas) {
                // Guardamos ID, Fecha, ClienteID, Subtotal Base Venta, Monto IVA Venta, Monto Descuento Venta, Total Venta
                writer.write(String.format(LOCALE, "%d,%d,%d,%.2f,%.2f,%.2f,%.2f\n", 
                        v.getId(), 
                        v.getFecha().getTime(), 
                        v.getCliente().getId(), 
                        v.getSubtotal(), // Subtotal Base Venta
                        v.getIva(), // Monto IVA Venta
                        v.getDescuento(), // Monto Descuento Venta
                        v.getTotal())); // Total Venta
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Path detallesFile = Paths.get(DATA_DIR, DETALLES_FILE);
        try (BufferedWriter writer = Files.newBufferedWriter(detallesFile)) {
            for (Venta v : ventas) {
                for (DetalleVenta detalle : v.getDetalles()) {
                    // Guardamos ID Detalle, VentaID, ProductoID, Cantidad, PrecioUnitario(Final), SubtotalBase Detalle
                    writer.write(String.format(LOCALE, "%d,%d,%d,%d,%.2f,%.2f\n",
                        detalle.getId(), 
                        v.getId(), 
                        detalle.getProducto().getId(),
                        detalle.getCantidad(), 
                        detalle.getPrecioUnitario(), // Precio unitario final al momento de la venta
                        detalle.getSubtotalBase())); // Subtotal base del detalle
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // La generación de PDF se mantiene igual, usa los getters de Venta
        generarFacturaPDF(venta);
    }
    
     public static void guardarVentas(List<Venta> ventas) {
        Path ventasFile = Paths.get(DATA_DIR, VENTAS_FILE);
        try (BufferedWriter writer = Files.newBufferedWriter(ventasFile)) {
            for (Venta v : ventas) {
                 // Guardamos ID, Fecha, ClienteID, Subtotal Base Venta, Monto IVA Venta, Monto Descuento Venta, Total Venta
                writer.write(String.format(LOCALE, "%d,%d,%d,%.2f,%.2f,%.2f,%.2f\n", 
                        v.getId(), 
                        v.getFecha().getTime(), 
                        v.getCliente().getId(), 
                        v.getSubtotal(), // Subtotal Base Venta
                        v.getIva(), // Monto IVA Venta
                        v.getDescuento(), // Monto Descuento Venta
                        v.getTotal())); // Total Venta
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
         // No generamos PDF al guardar la lista completa de ventas
    }
    
    public static void generarFacturaPDF(Venta venta) {
        try {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, new FileOutputStream(DATA_DIR + "/" + FACTURAS_DIR + "/Factura_" + venta.getId() + ".pdf"));
            document.open();
            Font fontTitle = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Font fontRegular = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);
            Font fontBold = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);

            Paragraph title = new Paragraph("Factura No. " + venta.getId(), fontTitle);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            document.add(new Paragraph("Fecha: " + new SimpleDateFormat("dd 'de' MMMM 'de' yyyy", LOCALE).format(venta.getFecha()), fontRegular));
            document.add(new Paragraph("Cliente: " + venta.getCliente().getNombre() + " " + venta.getCliente().getApellido(), fontRegular));
            document.add(Chunk.NEWLINE);

            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);
            table.addCell(new PdfPCell(new Phrase("Producto", fontBold)));
            table.addCell(new PdfPCell(new Phrase("Cantidad", fontBold)));
            table.addCell(new PdfPCell(new Phrase("Precio Unitario", fontBold)));
            table.addCell(new PdfPCell(new Phrase("Subtotal", fontBold))); // Subtotal por item en PDF
            for (DetalleVenta detalle : venta.getDetalles()) {
                table.addCell(new Phrase(detalle.getProducto().getNombre(), fontRegular));
                table.addCell(new Phrase(String.valueOf(detalle.getCantidad()), fontRegular));
                // Mostramos el precio unitario final del producto al momento de la venta
                table.addCell(new Phrase(String.format(LOCALE, "%.2f", detalle.getPrecioUnitario()), fontRegular));
                // Mostramos el total del detalle (subtotal base + iva - descuento del producto)
                table.addCell(new Phrase(String.format(LOCALE, "%.2f", detalle.getTotalDetalle()), fontRegular));
            }
            document.add(table);
            
            // Mostramos el subtotal base de la venta, el monto total de IVA y Descuento, y el Total final
            document.add(new Paragraph("Subtotal (Base): " + String.format(LOCALE, "%.2f", venta.getSubtotal()), fontBold));
            document.add(new Paragraph("IVA (Total): " + String.format(LOCALE, "%.2f", venta.getIva()), fontBold));
            document.add(new Paragraph("Descuento (Total): " + String.format(LOCALE, "%.2f", venta.getDescuento()), fontBold));
            document.add(new Paragraph("Total: " + String.format(LOCALE, "%.2f", venta.getTotal()), fontBold));

            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static Cliente buscarCliente(int id) {
        // Recargamos clientes cada vez para asegurar datos actualizados
        List<Cliente> clientes = cargarClientes();
        return clientes.stream()
                .filter(c -> c.getId() == id)
                .findFirst()
                .orElse(null);
    }
    
    private static Producto buscarProducto(int id) {
        // Recargamos productos cada vez para asegurar datos actualizados
        List<Producto> productos = cargarProductos();
        return productos.stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .orElse(null);
    }
    
    // Método para cargar ventas, crea datos por defecto si los archivos no existen
    public static void inicializarArchivosSiNoExisten() {
         Path productosFile = Paths.get(DATA_DIR, PRODUCTOS_FILE);
         Path clientesFile = Paths.get(DATA_DIR, CLIENTES_FILE);

         if (!Files.exists(productosFile) || !Files.exists(clientesFile)) {
             InicializadorDatos.inicializarDatosPorDefecto();
         }
     }
} 