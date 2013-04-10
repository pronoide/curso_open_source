package aplicaciones;

import modelo.Direccion;
import modelo.Factura;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ProgramaXML {
	public static void main(String[] args) {
		ApplicationContext applicationContext = 
			new ClassPathXmlApplicationContext(
				"configuracion.xml");
		
		Direccion direccion =
			applicationContext.getBean(
				"direccion2",
				Direccion.class);
		
		System.out.println(direccion);
	
		Factura factura = 
				applicationContext.getBean(
						Factura.class);
		System.out.println(factura);
	}
}
