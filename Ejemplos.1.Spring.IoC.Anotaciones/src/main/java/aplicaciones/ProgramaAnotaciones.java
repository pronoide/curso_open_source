package aplicaciones;

import modelo.Factura;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import config.Configuracion;

public class ProgramaAnotaciones {
	public static void main(String[] args) {
		ApplicationContext applicationContext = 
			new AnnotationConfigApplicationContext(
					Configuracion.class);
		
		Factura factura = 
				applicationContext.getBean(Factura.class);
		
		System.out.println(factura);
	}
}
