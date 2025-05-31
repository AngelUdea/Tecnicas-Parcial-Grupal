/**
 * Clase que representa un cliente en el sistema.
 * Un cliente contiene información personal como nombre, apellido,
 * email, teléfono y dirección.
 * 
 * Características principales:
 * - Identificación única para cada cliente
 * - Información de contacto completa
 * - Relación con las ventas realizadas
 * 
 * Notas técnicas:
 * - Los IDs son generados automáticamente
 * - Se mantiene un registro de todas las ventas asociadas
 */
package com.minimercado.modelo;

import java.util.Objects;

public class Cliente {
    // Atributos de la clase Cliente
    private int id;                  // Identificador único del cliente
    private String nombre;           // Nombre del cliente
    private String apellido;         // Apellido del cliente
    private String documento;        // Número de documento de identidad
    private String telefono;         // Número de teléfono de contacto
    private String email;            // Correo electrónico
    private String direccion;        // Dirección física

    // Constructor por defecto
    public Cliente() {
    }

    // Constructor con datos básicos
    public Cliente(String nombre, String apellido, String documento, String telefono, String email) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.documento = documento;
        this.telefono = telefono;
        this.email = email;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    // Método para representar el cliente como String
    @Override
    public String toString() {
        return nombre + (apellido != null ? (" " + apellido) : "");
    }

    // Método para comparar clientes por ID
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cliente cliente = (Cliente) o;
        return id == cliente.id;
    }

    // Método para generar hash basado en ID
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
} 