package modelo;

import java.io.Serializable;

import org.springframework.stereotype.Component;

@Component("direccion1")
public class Direccion implements Serializable {
	private String nombre;
	private int numero;
	
	public Direccion() {
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public int getNumero() {
		return numero;
	}

	public void setNumero(int numero) {
		this.numero = numero;
	}

	@Override
	public String toString() {
		return "Direccion [nombre=" + nombre + ", numero=" + numero + "]";
	}
	
	
}
