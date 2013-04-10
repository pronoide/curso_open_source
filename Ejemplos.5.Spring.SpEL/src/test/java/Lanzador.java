import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import ejemplo.anotaciones.modelo.Coche;
import java.util.*;

public class Lanzador {
	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext("persistencia.xml");
		List<Coche> coche = context.getBean("coches2",List.class);
		for (Coche c : coche) {
			System.out.println(c);
		}
	}
}
