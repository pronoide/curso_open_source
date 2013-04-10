package modelo;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("singleton")
@Lazy(true)
public class Factura implements Serializable{
	private String concepto;
	private double importe;
	
	@Autowired
	@Qualifier("direccion1")
	//JSR-250 es como el @Autowired
	//@Resource(name="direccion1")
	private Direccion direccion;
	
	public Factura() {
	}

	//Spring entiende la anotaciones del JSR-250
	@PostConstruct
	public void inicializar(){
		System.out.println("factura creada");
	}
	
	//JSR-250
	@PreDestroy
	public void limpiar(){
		System.out.println("factura destruida");
	}
	
	
	
	
	public String getConcepto() {
		return concepto;
	}

	public void setConcepto(String concepto) {
		this.concepto = concepto;
	}

	public double getImporte() {
		return importe;
	}

	public void setImporte(double importe) {
		this.importe = importe;
	}

	public Direccion getDireccion() {
		return direccion;
	}

	public void setDireccion(Direccion direccion) {
		this.direccion = direccion;
	}

	@Override
	public String toString() {
		return "Factura [concepto=" + concepto + ", importe=" + importe
				+ ", direccion=" + direccion + "]";
	}
	
	
}
