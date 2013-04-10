package ejemplos.tareas;

import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ServicioContador{
	private AtomicInteger contador=new AtomicInteger();
	
	@Scheduled(fixedRate=15000)
	public void contar(){
		for(int i=0;i<10;i++)
			System.out.println("contador:"+contador.getAndIncrement());
	}
	
}
