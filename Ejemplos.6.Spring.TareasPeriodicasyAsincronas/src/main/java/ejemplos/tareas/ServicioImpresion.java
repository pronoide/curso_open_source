package ejemplos.tareas;

import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ServicioImpresion{
	private AtomicInteger contador=new AtomicInteger();
	
	@Scheduled(fixedRate=1000)
	@Async
	public void imprimir(){
		System.out.println("entrada de trabajo");
		for(int i=0;i<10;i++){
			Thread currentThread = Thread.currentThread();
			System.out.println(currentThread.getName()+".contador:"+contador.getAndIncrement());
			try {
				currentThread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
}
