import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.applet.Applet;
import java.applet.AudioClip;
import com.studiohartman.jamepad.*;

public class CaminoUniversidad extends JPanel implements ActionListener, KeyListener, MouseListener {
    private final int WIDTH = 800;
    private final int HEIGHT = 600;
    private SeleccionEscenario.Stage currentStage;

    // Control con Jamepad
    private ControllerManager controllers;
    private ControllerState currState;
    private boolean wasJumpPressed = false;
    private boolean wasStartPressed = false;

    // Imágenes
    private Image personajeImg;
    private Image personajeMuertoImg;
    private Image personajeSaltandoImg;
    private Image obstaculo1Img;
    private Image obstaculo2Img;
    private Image obstaculo3Img;
    private Image fondoDiaImg;
    private Image fondoNocheImg;

    // Sonidos
    private AudioClip sonidoSalto;
    private AudioClip sonidoColision;
    private AudioClip sonidoGanar;
    private AudioClip sonidoBoton;

    // Objetos del juego
    private Block personaje;
    private ArrayList<Block> obstaculos;

    // Física del juego
    private int velocidadX = -12;
    private int velocidadY = 0;
    private final int gravedad = 1;

    // Estado del juego
    private boolean juegoTerminado = false;
    private boolean juegoGanado = false;
    private int puntaje = 0;
    private final Timer gameLoop;
    private final Timer obstaculoTimer;

    // Botones
    private Rectangle botonMenuRect;
    private Rectangle botonReiniciarRect;
    private Rectangle botonSalirRect;
    private boolean mostrarBotones = false;
    private Color colorBotonMenu = new Color(70, 70, 200);
    private Color colorBotonReiniciar = new Color(70, 200, 70);
    private Color colorBotonSalir = new Color(200, 70, 70);

    public CaminoUniversidad(SeleccionEscenario.Stage stage) {
        this.currentStage = stage;
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(stage == SeleccionEscenario.Stage.DIA ? 
            new Color(240, 240, 240) : new Color(30, 30, 50));
        setFocusable(true);
        addKeyListener(this);
        addMouseListener(this);

        // Inicializar Jamepad
        try {
            controllers = new ControllerManager();
            controllers.initSDLGamepad();
        } catch (Exception e) {
            System.err.println("Error inicializando Jamepad: " + e.getMessage());
        }

        // Cargar imágenes
        cargarImagenes();
        
        // Cargar sonidos
        cargarSonidos();

        // Inicializar personaje
        personaje = new Block(50, HEIGHT - 94, 88, 94, personajeImg);

        // Inicializar obstáculos
        obstaculos = new ArrayList<>();

        // Inicializar rectángulos para botones
        botonMenuRect = new Rectangle(WIDTH/2 - 150, HEIGHT/2 + 20, 300, 40);
        botonReiniciarRect = new Rectangle(WIDTH/2 - 150, HEIGHT/2 + 70, 300, 40);
        botonSalirRect = new Rectangle(WIDTH/2 - 150, HEIGHT/2 + 120, 300, 40);

        // Temporizadores del juego
        gameLoop = new Timer(1000/60, this); // 60 FPS
        obstaculoTimer = new Timer(1500, e -> agregarObstaculo());
        
        gameLoop.start();
        obstaculoTimer.start();
    }

    private void cargarImagenes() {
        try {
            personajeImg = new ImageIcon(getClass().getResource("resources/img/personaje.png")).getImage();
            personajeMuertoImg = new ImageIcon(getClass().getResource("resources/img/personaje-muerto.png")).getImage();
            personajeSaltandoImg = new ImageIcon(getClass().getResource("resources/img/personaje-saltando.png")).getImage();
            obstaculo1Img = new ImageIcon(getClass().getResource("resources/img/obstaculo1.png")).getImage();
            obstaculo2Img = new ImageIcon(getClass().getResource("resources/img/obstaculo2.png")).getImage();
            obstaculo3Img = new ImageIcon(getClass().getResource("resources/img/obstaculo3.png")).getImage();
            fondoDiaImg = new ImageIcon(getClass().getResource("resources/img/fondo-dia.png")).getImage();
            fondoNocheImg = new ImageIcon(getClass().getResource("resources/img/fondo-noche.png")).getImage();
        } catch (Exception e) {
            System.err.println("Error cargando imágenes: " + e.getMessage());
        }
    }

    private void cargarSonidos() {
        try {
            sonidoSalto = Applet.newAudioClip(getClass().getResource("resources/sonidos/salto.wav"));
            sonidoColision = Applet.newAudioClip(getClass().getResource("resources/sonidos/colision.wav"));
            sonidoGanar = Applet.newAudioClip(getClass().getResource("resources/sonidos/ganar.wav"));
            sonidoBoton = Applet.newAudioClip(getClass().getResource("resources/sonidos/boton.wav"));
        } catch (Exception e) {
            System.err.println("Error cargando sonidos: " + e.getMessage());
        }
    }

    private void agregarObstaculo() {
        if (juegoTerminado || juegoGanado) return;

        double chance = Math.random();
        int anchoObstaculo;
        Image imgObstaculo;

        if (chance > 0.90) {
            anchoObstaculo = 102;
            imgObstaculo = obstaculo3Img;
        } else if (chance > 0.70) {
            anchoObstaculo = 69;
            imgObstaculo = obstaculo2Img;
        } else if (chance > 0.50) {
            anchoObstaculo = 34;
            imgObstaculo = obstaculo1Img;
        } else {
            return;
        }

        obstaculos.add(new Block(WIDTH, HEIGHT - 70, anchoObstaculo, 70, imgObstaculo));

        if (obstaculos.size() > 10) {
            obstaculos.remove(0);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Dibujar fondo
        Image fondo = currentStage == SeleccionEscenario.Stage.DIA ? fondoDiaImg : fondoNocheImg;
        if (fondo != null) {
            g.drawImage(fondo, 0, 0, WIDTH, HEIGHT, null);
        }
        
        // Dibujar obstáculos
        for (Block obstaculo : obstaculos) {
            g.drawImage(obstaculo.img, obstaculo.x, obstaculo.y, obstaculo.width, obstaculo.height, null);
        }
        
        // Dibujar personaje
        g.drawImage(personaje.img, personaje.x, personaje.y, personaje.width, personaje.height, null);
        
        // Dibujar puntaje
        g.setColor(currentStage == SeleccionEscenario.Stage.DIA ? Color.BLACK : Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.drawString("Puntaje: " + puntaje, 20, 30);
        
        // Dibujar pantalla de fin de juego
        if (juegoTerminado || juegoGanado) {
            dibujarPantallaFinal(g);
        }
    }

    private void dibujarPantallaFinal(Graphics g) {
        // Fondo semitransparente
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, WIDTH, HEIGHT);
        
        // Mensaje principal
        String mensaje = juegoGanado ? "¡GANASTE!" : "¡PERDISTE!";
        Color color = juegoGanado ? Color.GREEN : Color.RED;
        g.setColor(color);
        g.setFont(new Font("Arial", Font.BOLD, 48));
        
        FontMetrics fm = g.getFontMetrics();
        int x = (WIDTH - fm.stringWidth(mensaje)) / 2;
        int y = HEIGHT / 2 - 50;
        g.drawString(mensaje, x, y);
        
        // Puntaje final
        g.setFont(new Font("Arial", Font.PLAIN, 24));
        g.setColor(Color.WHITE);
        // String textoPuntaje = "Puntaje final: " + puntaje;
        // x = (WIDTH - fm.stringWidth(textoPuntaje)) / 2;
        // g.drawString(textoPuntaje, x, y + 50);
        
        // Dibujar botones
        dibujarBotones(g);
    }

    private void dibujarBotones(Graphics g) {
        // Botón Menú Principal
        g.setColor(colorBotonMenu);
        g.fillRoundRect(botonMenuRect.x, botonMenuRect.y, botonMenuRect.width, botonMenuRect.height, 10, 10);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Menú Principal", WIDTH/2 - 70, HEIGHT/2 + 45);
        
        // Botón Reiniciar
        g.setColor(colorBotonReiniciar);
        g.fillRoundRect(botonReiniciarRect.x, botonReiniciarRect.y, botonReiniciarRect.width, botonReiniciarRect.height, 10, 10);
        g.setColor(Color.WHITE);
        g.drawString("Reiniciar Juego", WIDTH/2 - 70, HEIGHT/2 + 95);
        
        // Botón Salir
        g.setColor(colorBotonSalir);
        g.fillRoundRect(botonSalirRect.x, botonSalirRect.y, botonSalirRect.width, botonSalirRect.height, 10, 10);
        g.setColor(Color.WHITE);
        g.drawString("Salir", WIDTH/2 - 30, HEIGHT/2 + 145);
    }

    private void mover() {
        if (juegoTerminado || juegoGanado) return;
        
        // Leer input del mando PS4
        leerInputMando();
        
        // Física del personaje
        velocidadY += gravedad;
        personaje.y += velocidadY;

        if (personaje.y > HEIGHT - personaje.height) {
            personaje.y = HEIGHT - personaje.height;
            velocidadY = 0;
            personaje.img = personajeImg;
        }

        // Mover obstáculos y detectar colisiones
        for (Block obstaculo : obstaculos) {
            obstaculo.x += velocidadX;
            
            if (colision(personaje, obstaculo)) {
                juegoTerminado = true;
                personaje.img = personajeMuertoImg;
                mostrarBotones = true;
                reproducirSonido(sonidoColision);
            }
        }

        // Incrementar puntaje
        puntaje++;
        
        // Verificar victoria
        if (puntaje >= 1000) {
            juegoGanado = true;
            mostrarBotones = true;
            reproducirSonido(sonidoGanar);
        }
    }

    private boolean colision(Block a, Block b) {
        return a.x < b.x + b.width &&
               a.x + a.width > b.x &&
               a.y < b.y + b.height &&
               a.y + a.height > b.y;
    }

    private void leerInputMando() {
        if (controllers == null || controllers.getNumControllers() == 0) return;
        
        try {
            currState = controllers.getState(0);
            
            if (!currState.isConnected) return;
            
            // Botón X (Saltar/Reiniciar)
            if (currState.x && !wasJumpPressed) {
                if (juegoTerminado || juegoGanado) {
                    reiniciarJuego();
                } else if (personaje.y == HEIGHT - personaje.height) {
                    velocidadY = -17;
                    personaje.img = personajeSaltandoImg;
                    reproducirSonido(sonidoSalto);
                }
            }
            wasJumpPressed = currState.x;
            
            // Botón OPTIONS (Menú Principal)
            if (currState.start && !wasStartPressed && (juegoTerminado || juegoGanado)) {
                volverAlMenu();
            }
            wasStartPressed = currState.start;
            
        } catch (Exception e) {
            System.err.println("Error leyendo mando: " + e.getMessage());
        }
    }

    private void reproducirSonido(AudioClip sonido) {
        if (sonido != null) {
            new Thread(sonido::play).start();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        mover();
        repaint();
        
        if (juegoTerminado || juegoGanado) {
            obstaculoTimer.stop();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (juegoTerminado || juegoGanado) {
                reiniciarJuego();
            } else if (personaje.y == HEIGHT - personaje.height) {
                velocidadY = -17;
                personaje.img = personajeSaltandoImg;
                reproducirSonido(sonidoSalto);
            }
        } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE && (juegoTerminado || juegoGanado)) {
            volverAlMenu();
        }
    }

    private void reiniciarJuego() {
        reproducirSonido(sonidoBoton);
        
        personaje.y = HEIGHT - personaje.height;
        personaje.img = personajeImg;
        velocidadY = 0;
        obstaculos.clear();
        puntaje = 0;
        juegoTerminado = false;
        juegoGanado = false;
        mostrarBotones = false;
        
        if (!gameLoop.isRunning()) gameLoop.start();
        if (!obstaculoTimer.isRunning()) obstaculoTimer.start();
        
        requestFocusInWindow();
    }

    private void volverAlMenu() {
        reproducirSonido(sonidoBoton);
        JFrame frame = (JFrame)SwingUtilities.getWindowAncestor(this);
        frame.dispose();
        new PantallaInicio().setVisible(true);
    }

    // MouseListener methods
    @Override
    public void mouseClicked(MouseEvent e) {
        if (mostrarBotones) {
            if (botonMenuRect.contains(e.getPoint())) {
                volverAlMenu();
            } else if (botonReiniciarRect.contains(e.getPoint())) {
                reiniciarJuego();
            } else if (botonSalirRect.contains(e.getPoint())) {
                System.exit(0);
            }
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        if (mostrarBotones) {
            if (botonMenuRect.contains(e.getPoint())) {
                colorBotonMenu = new Color(100, 100, 255);
            } else if (botonReiniciarRect.contains(e.getPoint())) {
                colorBotonReiniciar = new Color(100, 255, 100);
            } else if (botonSalirRect.contains(e.getPoint())) {
                colorBotonSalir = new Color(255, 100, 100);
            }
            repaint();
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
        if (mostrarBotones) {
            colorBotonMenu = new Color(70, 70, 200);
            colorBotonReiniciar = new Color(70, 200, 70);
            colorBotonSalir = new Color(200, 70, 70);
            repaint();
        }
    }

    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}
    @Override public void keyReleased(KeyEvent e) {}

    private class Block {
        int x, y, width, height;
        Image img;

        Block(int x, int y, int width, int height, Image img) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.img = img;
        }
    }

    public void limpiarRecursos() {
        if (controllers != null) {
            controllers.quitSDLGamepad();
        }
    }
}