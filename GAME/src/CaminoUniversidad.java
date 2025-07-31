import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class CaminoUniversidad extends JPanel implements ActionListener, KeyListener, MouseListener {
    private final int WIDTH = 800;
    private final int HEIGHT = 400;
    private final SeleccionEscenario.Stage escenario;
    
    // Jugador
    private int playerX = 100;
    private int playerY = 300;
    private int playerWidth = 60;
    private int playerHeight = 80;
    private int playerSpeedY = 0;
    private boolean isJumping = false;
    
    // Obstáculos
    private ArrayList<Rectangle> obstacles = new ArrayList<>();
    private int obstacleSpeed = -5;
    private Timer obstacleTimer;
    
    // Juego
    private Timer gameTimer;
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
        addMouseListener(this);
        setFocusable(true);
        
        // Iniciar juego
        startGame();
    }
    
    private void startGame() {
        // Reiniciar variables del juego
        playerX = 100;
        playerY = 300;
        playerSpeedY = 0;
        isJumping = false;
        obstacles.clear();
        score = 0;
        gameOver = false;
        gameWon = false;
        mostrarBotones = false;
        
        // Reiniciar timers
        gameTimer.start();
        obstacleTimer.start();
    }
    
    private void addObstacle() {
        if (gameOver || gameWon) return;
        
        int height = 30 + (int)(Math.random() * 50);
        obstacles.add(new Rectangle(WIDTH, HEIGHT - height, 30, height));
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Dibujar jugador
        g.setColor(Color.RED);
        g.fillRect(playerX, playerY, playerWidth, playerHeight);
        
        // Dibujar obstáculos
        g.setColor(Color.GREEN);
        for (Rectangle obstacle : obstacles) {
            g.fillRect(obstacle.x, obstacle.y, obstacle.width, obstacle.height);
        }
        
        // Dibujar puntaje
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.drawString("Puntaje: " + score, 20, 30);
        
        // Mensajes de fin de juego
        if (gameOver) {
            drawCenteredText(g, "¡PERDISTE!", Color.RED, 48);
            // drawCenteredText(g, "Puntaje final: " + score, Color.BLACK, 24);
            drawButtons(g);
            mostrarBotones = true;
        } else if (gameWon) {
            drawCenteredText(g, "¡GANASTE!", Color.GREEN, 48);
            // drawCenteredText(g, "Puntaje final: " + score, Color.BLACK, 24);
            drawButtons(g);
            mostrarBotones = true;
        }
    }
    
    private void drawCenteredText(Graphics g, String text, Color color, int size) {
        g.setColor(color);
        g.setFont(new Font("Arial", Font.BOLD, size));
        FontMetrics fm = g.getFontMetrics();
        int x = (WIDTH - fm.stringWidth(text)) / 2;
        int y = HEIGHT / 2 - (size / 2);
        g.drawString(text, x, y);
    }
    
    private void drawButtons(Graphics g) {
        // Botón Menú Principal
        g.setColor(colorBotonMenu);
        g.fillRect(botonMenuRect.x, botonMenuRect.y, botonMenuRect.width, botonMenuRect.height);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Menú Principal", WIDTH/2 - 70, HEIGHT/2 + 55);
        
        // Botón Reiniciar
        g.setColor(colorBotonReiniciar);
        g.fillRect(botonReiniciarRect.x, botonReiniciarRect.y, botonReiniciarRect.width, botonReiniciarRect.height);
        g.setColor(Color.WHITE);
        g.drawString("Reiniciar Juego", WIDTH/2 - 70, HEIGHT/2 + 105);
        
        // Botón Salir
        g.setColor(colorBotonSalir);
        g.fillRect(botonSalirRect.x, botonSalirRect.y, botonSalirRect.width, botonSalirRect.height);
        g.setColor(Color.WHITE);
        g.drawString("Salir", WIDTH/2 - 30, HEIGHT/2 + 155);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver && !gameWon) {
            // Movimiento del jugador
            playerY += playerSpeedY;
            
            // Gravedad
            if (playerY < HEIGHT - playerHeight) {
                playerSpeedY += 1;
            } else {
                playerY = HEIGHT - playerHeight;
                playerSpeedY = 0;
                isJumping = false;
            }
            
            // Mover obstáculos
            for (int i = 0; i < obstacles.size(); i++) {
                Rectangle obstacle = obstacles.get(i);
                obstacle.x += obstacleSpeed;
                
                // Detectar colisiones
                if (obstacle.intersects(playerX, playerY, playerWidth, playerHeight)) {
                    gameOver = true;
                    gameTimer.stop();
                    obstacleTimer.stop();
                }
                
                // Eliminar obstáculos fuera de pantalla
                if (obstacle.x + obstacle.width < 0) {
                    obstacles.remove(i);
                    i--;
                    score++;
                }
            }
            
            // Verificar victoria
            if (score >= 20) {
                gameWon = true;
                gameTimer.stop();
                obstacleTimer.stop();
            }
        }
        repaint();
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE && !isJumping && !gameOver && !gameWon) {
            playerSpeedY = -15;
            isJumping = true;
        }
    }
    
    // Métodos del MouseListener
    @Override
    public void mouseClicked(MouseEvent e) {
        if (mostrarBotones) {
            if (botonMenuRect.contains(e.getPoint())) {
                volverAlMenu();
            } else if (botonReiniciarRect.contains(e.getPoint())) {
                startGame();
            } else if (botonSalirRect.contains(e.getPoint())) {
                System.exit(0);
            }
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        if (mostrarBotones) {
            if (botonMenuRect.contains(e.getPoint())) {
                colorBotonMenu = new Color(0, 0, 200);
            } else if (botonReiniciarRect.contains(e.getPoint())) {
                colorBotonReiniciar = new Color(0, 0, 200);
            } else if (botonSalirRect.contains(e.getPoint())) {
                colorBotonSalir = new Color(0, 0, 200);
            }
            repaint();
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