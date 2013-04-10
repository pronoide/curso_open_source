package ejemplos.tareas;

import java.util.Date;
import java.util.TimerTask;

public class SaludosTareaJSE extends TimerTask {
	public void saludar() {
		System.out.println(new Date()+" Hola");
	}

	@Override
	public void run() {
		saludar();
		
	}
}
