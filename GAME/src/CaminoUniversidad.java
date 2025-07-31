package main;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import java.io.InputStream;
import com.studiohartman.jamepad.*;
//import resources.VentanaPuntajes;

public class CaminoUniversidad extends JPanel implements ActionListener, KeyListener, MouseListener {
    private final int WIDTH = 800;
    private final int HEIGHT = 600;
    private SeleccionEscenario.Stage currentStage;

    // Controller support
    private ControllerManager controllers;
    private ControllerState currState;
    private boolean wasJumpPressed = false;
    private boolean wasStartPressed = false;

    // Images
    private BufferedImage personajeImg;
    private BufferedImage personajeMuertoImg;
    private BufferedImage personajeSaltandoImg;
    private BufferedImage obstaculo1Img;
    private BufferedImage obstaculo2Img;
    private BufferedImage obstaculo3Img;
    private BufferedImage fondoDiaImg;
    private BufferedImage fondoNocheImg;

    // Game objects
    private Block personaje;
    private ArrayList<Block> obstaculos;

    // Physics
    private int velocidadX = -12;
    private int velocidadY = 0;
    private final int gravedad = 1;

    // Game state
    private boolean juegoTerminado = false;
    private boolean juegoGanado = false;
    private int puntaje = 0;
    private final Timer gameLoop;
    private final Timer obstaculoTimer;

    // Buttons
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
        setFocusable(true);
        addKeyListener(this);
        addMouseListener(this);

        // Initialize controller
        try {
            controllers = new ControllerManager();
            controllers.initSDLGamepad();
        } catch (Exception e) {
            System.err.println("Controller error: " + e.getMessage());
        }

        // Load resources
        cargarImagenes();
        
        // Initialize game objects
        personaje = new Block(50, HEIGHT - 94, 88, 94, personajeImg);
        obstaculos = new ArrayList<>();

        // Setup buttons (now centered)
        int buttonWidth = 300;
        int buttonHeight = 40;
        int buttonY = HEIGHT/2 + 50;
        int buttonSpacing = 60;
        
        botonMenuRect = new Rectangle((WIDTH - buttonWidth)/2, buttonY, buttonWidth, buttonHeight);
        botonReiniciarRect = new Rectangle((WIDTH - buttonWidth)/2, buttonY + buttonSpacing, buttonWidth, buttonHeight);
        botonSalirRect = new Rectangle((WIDTH - buttonWidth)/2, buttonY + 2*buttonSpacing, buttonWidth, buttonHeight);

        // Game timers
        gameLoop = new Timer(1000/60, this); // 60 FPS
        obstaculoTimer = new Timer(1500, e -> agregarObstaculo());
        
        startGame();
    }

    private void cargarImagenes() {
        try {
            // Load character images
            personajeImg = cargarImagen("/img/personaje.png");
            personajeMuertoImg = cargarImagen("/img/personaje-muerto.png");
            personajeSaltandoImg = cargarImagen("/img/personaje-saltando.png");
            
            // Load obstacle images
            obstaculo1Img = cargarImagen("/img/obstaculo1.png");
            obstaculo2Img = cargarImagen("/img/obstaculo2.png");
            obstaculo3Img = cargarImagen("/img/obstaculo3.png");
            
            // Load backgrounds
            fondoDiaImg = cargarImagen("/img/fondo-dia.png");
            fondoNocheImg = cargarImagen("/img/fondo-noche.png");
            
        } catch (Exception e) {
            System.err.println("Error loading images: " + e.getMessage());
            crearImagenesPorDefecto();
        }
    }

    private BufferedImage cargarImagen(String ruta) throws Exception {
        try (InputStream is = getClass().getResourceAsStream(ruta)) {
            if (is == null) {
                throw new Exception("Image not found: " + ruta);
            }
            return ImageIO.read(is);
        }
    }

    private void crearImagenesPorDefecto() {
        // Create default colored images if loading fails
        personajeImg = crearImagenColor(Color.RED, 88, 94);
        personajeMuertoImg = crearImagenColor(Color.GRAY, 88, 94);
        personajeSaltandoImg = crearImagenColor(Color.BLUE, 88, 94);
        obstaculo1Img = crearImagenColor(Color.GREEN, 34, 70);
        obstaculo2Img = crearImagenColor(Color.GREEN, 69, 70);
        obstaculo3Img = crearImagenColor(Color.GREEN, 102, 70);
        fondoDiaImg = crearImagenColor(new Color(135, 206, 235), WIDTH, HEIGHT);
        fondoNocheImg = crearImagenColor(new Color(10, 10, 50), WIDTH, HEIGHT);
    }

    private BufferedImage crearImagenColor(Color color, int width, int height) {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setColor(color);
        g2d.fillRect(0, 0, width, height);
        g2d.dispose();
        return img;
    }

    private void startGame() {
        // Reset game state
        personaje.y = HEIGHT - personaje.height;
        personaje.img = personajeImg;
        velocidadY = 0;
        obstaculos.clear();
        puntaje = 0;
        juegoTerminado = false;
        juegoGanado = false;
        mostrarBotones = false;
        
        // Start timers
        gameLoop.start();
        obstaculoTimer.start();
    }

    private void agregarObstaculo() {
        if (juegoTerminado || juegoGanado) return;

        double chance = Math.random();
        Block nuevoObstaculo;

        if (chance > 0.90) {
            nuevoObstaculo = new Block(WIDTH, HEIGHT - 70, 102, 70, obstaculo3Img);
        } else if (chance > 0.70) {
            nuevoObstaculo = new Block(WIDTH, HEIGHT - 70, 69, 70, obstaculo2Img);
        } else if (chance > 0.50) {
            nuevoObstaculo = new Block(WIDTH, HEIGHT - 70, 34, 70, obstaculo1Img);
        } else {
            return;
        }

        obstaculos.add(nuevoObstaculo);

        if (obstaculos.size() > 10) {
            obstaculos.remove(0);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Draw background
        BufferedImage fondo = currentStage == SeleccionEscenario.Stage.DIA ? fondoDiaImg : fondoNocheImg;
        g.drawImage(fondo, 0, 0, WIDTH, HEIGHT, null);
        
        // Draw obstacles
        for (Block obstaculo : obstaculos) {
            g.drawImage(obstaculo.img, obstaculo.x, obstaculo.y, obstaculo.width, obstaculo.height, null);
        }
        
        // Draw character
        g.drawImage(personaje.img, personaje.x, personaje.y, personaje.width, personaje.height, null);
        
        // Draw score
        g.setColor(currentStage == SeleccionEscenario.Stage.DIA ? Color.BLACK : Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.drawString("Puntaje: " + puntaje, 20, 30);
        
        // Draw end game screen
        if (juegoTerminado || juegoGanado) {
            dibujarPantallaFinal(g);            
        }
    }

    private void dibujarPantallaFinal(Graphics g) {
        // Semi-transparent overlay
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, WIDTH, HEIGHT);
        
        // Main message
        String mensaje = juegoGanado ? "¡GANASTE!" : "¡PERDISTE!";
        Color color = juegoGanado ? Color.GREEN : Color.RED;
        g.setColor(color);
        g.setFont(new Font("Arial", Font.BOLD, 48));
        
        FontMetrics fm = g.getFontMetrics();
        int x = (WIDTH - fm.stringWidth(mensaje)) / 2;
        int y = HEIGHT / 2 - 50;
        g.drawString(mensaje, x, y);
        
        // Score
        g.setFont(new Font("Arial", Font.PLAIN, 24));
        g.setColor(Color.WHITE);
        String textoPuntaje = "Puntaje: " + puntaje;
        x = (WIDTH - fm.stringWidth(textoPuntaje)) / 2;
        g.drawString(textoPuntaje, x, y + 50);
        
        // Buttons
        dibujarBotones(g);
    }

    private void dibujarBotones(Graphics g) {
        // Botón Menú Principal
        g.setColor(colorBotonMenu);
        g.fillRoundRect(botonMenuRect.x, botonMenuRect.y, botonMenuRect.width, botonMenuRect.height, 10, 10);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        
        FontMetrics fm = g.getFontMetrics();
        String menuText = "Menú Principal";
        int menuTextX = botonMenuRect.x + (botonMenuRect.width - fm.stringWidth(menuText)) / 2;
        g.drawString(menuText, menuTextX, botonMenuRect.y + 27);
        
        // Botón Reiniciar
        g.setColor(colorBotonReiniciar);
        g.fillRoundRect(botonReiniciarRect.x, botonReiniciarRect.y, botonReiniciarRect.width, botonReiniciarRect.height, 10, 10);
        g.setColor(Color.WHITE);
        String reiniciarText = "Reiniciar Juego";
        int reiniciarTextX = botonReiniciarRect.x + (botonReiniciarRect.width - fm.stringWidth(reiniciarText)) / 2;
        g.drawString(reiniciarText, reiniciarTextX, botonReiniciarRect.y + 27);
        
        // Botón Salir
        g.setColor(colorBotonSalir);
        g.fillRoundRect(botonSalirRect.x, botonSalirRect.y, botonSalirRect.width, botonSalirRect.height, 10, 10);
        g.setColor(Color.WHITE);
        String salirText = "Salir";
        int salirTextX = botonSalirRect.x + (botonSalirRect.width - fm.stringWidth(salirText)) / 2;
        g.drawString(salirText, salirTextX, botonSalirRect.y + 27);
    }

    private void updateGame() {
        if (juegoTerminado || juegoGanado) return;        
        
        // Handle controller input
        handleControllerInput();
        
        // Apply gravity
        velocidadY += gravedad;
        personaje.y += velocidadY;

        // Ground collision
        if (personaje.y > HEIGHT - personaje.height) {
            personaje.y = HEIGHT - personaje.height;
            velocidadY = 0;
            personaje.img = personajeImg;
        }

        // Move obstacles and check collisions
        for (Block obstaculo : obstaculos) {
            obstaculo.x += velocidadX;
            
            if (checkCollision(personaje, obstaculo)) {
                juegoTerminado = true;
                personaje.img = personajeMuertoImg;
                mostrarBotones = true;
                ScoreDAO.guardarScore(puntaje);
                new VentanaPuntajes().setVisible(true); // Mostrar tabla
            }
        }

        // Increase score
        puntaje++;
        
        // Check win condition (now 3000 points instead of 1000)
        if (puntaje >= 3000) {
            juegoGanado = true;
            mostrarBotones = true;
            ScoreDAO.guardarScore(puntaje);
            new VentanaPuntajes().setVisible(true); // Mostrar tabla
        }
    }

    private void handleControllerInput() {
        if (controllers == null || controllers.getNumControllers() == 0) return;
        
        try {
            currState = controllers.getState(0);
            if (!currState.isConnected) return;
            
            // Jump/restart with X button
            if (currState.x && !wasJumpPressed) {
                if (juegoTerminado || juegoGanado) {
                    reiniciarJuego();
                } else if (personaje.y == HEIGHT - personaje.height) {
                    velocidadY = -17;
                    personaje.img = personajeSaltandoImg;
                }
            }
            wasJumpPressed = currState.x;
            
            // Menu with OPTIONS button
            if (currState.start && !wasStartPressed && (juegoTerminado || juegoGanado)) {
                volverAlMenu();
            }
            wasStartPressed = currState.start;
            
        } catch (Exception e) {
            System.err.println("Controller error: " + e.getMessage());
        }
    }

    private boolean checkCollision(Block a, Block b) {
        return a.x < b.x + b.width &&
               a.x + a.width > b.x &&
               a.y < b.y + b.height &&
               a.y + a.height > b.y;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        updateGame();
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
            }
        } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE && (juegoTerminado || juegoGanado)) {
            volverAlMenu();
        }
    }

    private void reiniciarJuego() {
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
        JFrame frame = (JFrame)SwingUtilities.getWindowAncestor(this);
        frame.dispose();
        new PantallaInicio().setVisible(true);
    }

    // Mouse events
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

    private static class Block {
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

    public void cleanup() {
        if (controllers != null) {
            controllers.quitSDLGamepad();
        }
    }
}