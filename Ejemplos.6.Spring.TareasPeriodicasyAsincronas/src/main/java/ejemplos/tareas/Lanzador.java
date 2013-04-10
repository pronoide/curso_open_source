package ejemplos.tareas;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Lanzador {
	public static void main(String[] args) {
		new ClassPathXmlApplicationContext("tareasporJSEyQuartz.xml");
		//new ClassPathXmlApplicationContext("tareasporXML.xml");
		//new ClassPathXmlApplicationContext("tareasporAnotaciones.xml");
		
	}
}
