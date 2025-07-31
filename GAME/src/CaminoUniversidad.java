import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

// Importación para Jamepad
import com.studiohartman.jamepad.*;

public class CaminoUniversidad extends JPanel implements ActionListener, KeyListener {
    private int boardWidth = 800;
    private int boardHeight = 250;
    private SeleccionEscenario.Stage currentStage;

    // Variables para Jamepad
    private ControllerManager controllers;
    private ControllerState currState;
    private boolean wasJumpPressed = false;

    // Images
    private Image dinosaurImg;
    private Image dinosaurDeadImg;
    private Image dinosaurJumpImg;
    private Image cactus1Img;
    private Image cactus2Img;
    private Image cactus3Img;
    private Image backgroundDayImg;
    private Image backgroundNightImg;

    // Game objects
    private Block dinosaur;
    private ArrayList<Block> cactusArray;

    // Game physics
    private int velocityX = -12;
    private int velocityY = 0;
    private int gravity = 1;

    // Game state
    private boolean gameOver = false;
    private int score = 0;
    private boolean gameOver = false;
    private boolean gameWon = false;
    
    // Botones
    private Rectangle botonMenuRect;
    private Rectangle botonReiniciarRect;
    private Rectangle botonSalirRect;
    private boolean mostrarBotones = false;
    private Color colorBotonMenu = Color.BLUE;
    private Color colorBotonReiniciar = Color.BLUE;
    private Color colorBotonSalir = Color.BLUE;

    public CaminoUniversidad(SeleccionEscenario.Stage escenario) {
        this.escenario = escenario;
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(escenario == SeleccionEscenario.Stage.DIA ? 
            new Color(135, 206, 235) : new Color(10, 10, 40));
        
        // Configurar timers
        gameTimer = new Timer(16, this);
        obstacleTimer = new Timer(2000, e -> addObstacle());
        
        // Inicializar rectángulos para los botones
        botonMenuRect = new Rectangle(WIDTH/2 - 150, HEIGHT/2 + 30, 300, 40);
        botonReiniciarRect = new Rectangle(WIDTH/2 - 150, HEIGHT/2 + 80, 300, 40);
        botonSalirRect = new Rectangle(WIDTH/2 - 150, HEIGHT/2 + 130, 300, 40);
        
        // Configurar listeners
        addKeyListener(this);

        // Inicializar Jamepad
        initializeJamepad();

        // Load images (make sure to have these files in your project)
        try {
            dinosaurImg = new ImageIcon(getClass().getResource("./img/dino-run.gif")).getImage();
            dinosaurDeadImg = new ImageIcon(getClass().getResource("./img/dino-dead.png")).getImage();
            dinosaurJumpImg = new ImageIcon(getClass().getResource("./img/dino-jump.png")).getImage();
            cactus1Img = new ImageIcon(getClass().getResource("./img/cactus1.png")).getImage();
            cactus2Img = new ImageIcon(getClass().getResource("./img/cactus2.png")).getImage();
            cactus3Img = new ImageIcon(getClass().getResource("./img/cactus3.png")).getImage();
            backgroundDayImg = new ImageIcon(getClass().getResource("./img/day-background.png")).getImage();
            backgroundNightImg = new ImageIcon(getClass().getResource("./img/night-background.png")).getImage();
        } catch (Exception e) {
            System.err.println("Error loading images: " + e.getMessage());
        }

        // Initialize dinosaur
        int dinosaurWidth = 88;
        int dinosaurHeight = 94;
        int dinosaurX = 50;
        int dinosaurY = boardHeight - dinosaurHeight;
        dinosaur = new Block(dinosaurX, dinosaurY, dinosaurWidth, dinosaurHeight, dinosaurImg);

        // Initialize cactus array
        cactusArray = new ArrayList<>();

        // Game loop
        gameLoop = new Timer(1000 / 60, this);
        gameLoop.start();

        // Cactus spawn timer
        placeCactusTimer = new Timer(1500, e -> placeCactus());
        placeCactusTimer.start();
    }

    private void initializeJamepad() {
        try {
            controllers = new ControllerManager();
            controllers.initSDLGamepad();
            System.out.println("Controladores conectados: " + controllers.getNumControllers());

            if (controllers.getNumControllers() > 0) {
                System.out.println("Mando PS4 detectado y listo para usar!");
            } else {
                System.out.println("No se detectó ningún mando. Puedes usar el teclado.");
            }
        } catch (Exception e) {
            System.out.println("Error inicializando Jamepad: " + e.getMessage());
        }
    }

    // Método para leer input del PS4
    private void readPS4Input() {
        if (controllers == null || controllers.getNumControllers() == 0)
            return;

        try {
            // Obtener estado del primer controlador
            currState = controllers.getState(0);

            if (!currState.isConnected)
                return;

            // Cambiar de currState.a (X) a currState.x (Cuadrado)
            boolean squarePressed = currState.x; // Botón Cuadrado

            // Detectar presión del botón (evitar repetición)
            if (squarePressed && !wasJumpPressed) {
                handleJump();
            }
            wasJumpPressed = squarePressed;

        } catch (Exception e) {
            System.out.println("Error leyendo mando: " + e.getMessage());
        }
    }

    private void handleJump() {
        if (gameOver) {
            // Reiniciar completamente el juego
            resetGame();
            // Forzar un repintado inmediato
            repaint();
            // Reiniciar los timers si no están corriendo
            if (!gameLoop.isRunning()) {
                gameLoop.start();
            }
            if (!placeCactusTimer.isRunning()) {
                placeCactusTimer.start();
            }
        } else if (dinosaur.y == boardHeight - dinosaur.height) {
            // Saltar normal
            velocityY = -17;
            dinosaur.img = dinosaurJumpImg;
        }
    }

    private void placeCactus() {
        if (gameOver)
            return;

        double chance = Math.random();
        int cactusWidth;
        Image cactusImg;

        if (chance > 0.90) {
            cactusWidth = 102;
            cactusImg = cactus3Img;
        } else if (chance > 0.70) {
            cactusWidth = 69;
            cactusImg = cactus2Img;
        } else if (chance > 0.50) {
            cactusWidth = 34;
            cactusImg = cactus1Img;
        } else {
            return;
        }

        int cactusHeight = 70;
        int cactusX = boardWidth;
        int cactusY = boardHeight - cactusHeight;
        cactusArray.add(new Block(cactusX, cactusY, cactusWidth, cactusHeight, cactusImg));

        if (cactusArray.size() > 10) {
            cactusArray.remove(0);
        }
    }
    
    private void addObstacle() {
        if (gameOver || gameWon) return;
        
        int height = 30 + (int)(Math.random() * 50);
        obstacles.add(new Rectangle(WIDTH, HEIGHT - height, 30, height));
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw background
        Image bgImage = currentStage == SeleccionEscenario.Stage.DIA ? backgroundDayImg : backgroundNightImg;
        if (bgImage != null) {
            g.drawImage(bgImage, 0, 0, boardWidth, boardHeight, null);
        }

        // Draw dinosaur
        g.drawImage(dinosaur.img, dinosaur.x, dinosaur.y, dinosaur.width, dinosaur.height, null);

        // Draw cacti
        for (Block cactus : cactusArray) {
            g.drawImage(cactus.img, cactus.x, cactus.y, cactus.width, cactus.height, null);
        }

        // Draw score
        Color textColor = currentStage == SeleccionEscenario.Stage.DIA ? Color.BLACK : Color.WHITE;
        g.setColor(textColor);
        g.setFont(new Font("Courier", Font.BOLD, 32));
        if (gameOver) {
            g.drawString("Game Over: " + score, 10, 35);
            g.setFont(new Font("Courier", Font.PLAIN, 16));
            g.drawString("Presiona ESPACIO o ▢ (Cuadrado) para reiniciar", 10, 60);
        } else {
            g.drawString(String.valueOf(score), 10, 35);
        }

    }

    private void move() {
        // Física del dinosaurio (sin comprobación de pausa)
        velocityY += gravity;
        dinosaur.y += velocityY;

        if (dinosaur.y > boardHeight - dinosaur.height) {
            dinosaur.y = boardHeight - dinosaur.height;
            velocityY = 0;
            dinosaur.img = dinosaurImg;
        }

        // Mover cactus
        for (Block cactus : cactusArray) {
            cactus.x += velocityX;
            if (collision(dinosaur, cactus)) {
                gameOver = true;
                dinosaur.img = dinosaurDeadImg;
            }
        }

        // Aumentar puntuación
        score++;
    }

    private boolean collision(Block a, Block b) {
        return a.x < b.x + b.width &&
                a.x + a.width > b.x &&
                a.y < b.y + b.height &&
                a.y + a.height > b.y;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        readPS4Input();
        move();
        repaint();

        if (gameOver) {
            placeCactusTimer.stop();
            gameLoop.stop();
        }
        repaint();
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            handleJump();
        }

    }

    private void resetGame() {
        // Resetear posición y apariencia del dinosaurio
        dinosaur.y = boardHeight - dinosaur.height;
        dinosaur.img = dinosaurImg;
        velocityY = 0;

        // Limpiar todos los cactus
        cactusArray.clear();

        // Resetear puntuación y estado
        score = 0;
        gameOver = false;

        // Asegurarse de que los timers estén activos
        if (!gameLoop.isRunning()) {
            gameLoop.start();
        }
        if (!placeCactusTimer.isRunning()) {
            placeCactusTimer.start();
        }

        // Forzar focus para asegurar que recibe inputs
        requestFocusInWindow();
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    // Limpiar recursos al cerrar
    public void cleanup() {
        if (controllers != null) {
            controllers.quitSDLGamepad();
        }
    }

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

    @Override
    public void mouseExited(MouseEvent e) {
        if (mostrarBotones) {
            colorBotonMenu = Color.BLUE;
            colorBotonReiniciar = Color.BLUE;
            colorBotonSalir = Color.BLUE;
            repaint();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}
    
    private void volverAlMenu() {
        JFrame frame = (JFrame)SwingUtilities.getWindowAncestor(this);
        frame.dispose();
        new PantallaInicio().setVisible(true);
    }
    
    @Override public void keyTyped(KeyEvent e) {}
    @Override public void keyReleased(KeyEvent e) {}
}