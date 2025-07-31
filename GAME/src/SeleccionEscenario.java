package main;
public class SeleccionEscenario {

    public enum Stage {
        DIA, NOCHE
    }

    private Stage stageSeleccionado;

    public Stage getStageSeleccionado() {
        return stageSeleccionado;
    }

    public void setStageSeleccionado(Stage stage) {
        this.stageSeleccionado = stage;
    }
}

