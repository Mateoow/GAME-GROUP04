package main;
import com.studiohartman.jamepad.*;

public class ControlPS4 {
    public static void mando() throws Exception {
        ControllerManager controllers = new ControllerManager();
        controllers.initSDLGamepad();

        // Obtener el índice del primer mando conectado
        ControllerIndex ps4 = controllers.getControllerIndex(0);
        
        // Verificar si el mando está conectado
        if (!ps4.isConnected()) {
            System.err.println("¡No hay mando conectado!");
            controllers.quitSDLGamepad();
            return;
        }

        // Mostrar información del mando detectado
        System.out.println("✓ Mando detectado: " + ps4.getName());
        System.out.println("Presiona botones para ver su mapeo (Ctrl+C para salir)");
        System.out.println("=".repeat(60));
    }
}
