import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class CaminoUniversidad extends JPanel implements ActionListener, KeyListener {
    private int boardWidth = 800;
    private int boardHeight = 250;
    private SeleccionEscenario.Stage currentStage;

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
    private Timer gameLoop;
    private Timer placeCactusTimer;

    public CaminoUniversidad(SeleccionEscenario.Stage stage) {
        this.currentStage = stage;
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(stage == SeleccionEscenario.Stage.DIA ? Color.lightGray : new Color(20, 20, 40));
        setFocusable(true);
        addKeyListener(this);

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
        gameLoop = new Timer(1000/60, this);
        gameLoop.start();

        // Cactus spawn timer
        placeCactusTimer = new Timer(1500, e -> placeCactus());
        placeCactusTimer.start();
    }

    private void placeCactus() {
        if (gameOver) return;

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
        g.setColor(currentStage == SeleccionEscenario.Stage.DIA ? Color.BLACK : Color.WHITE);
        g.setFont(new Font("Courier", Font.BOLD, 32));
        if (gameOver) {
            g.drawString("Game Over: " + score, 10, 35);
        } else {
            g.drawString(String.valueOf(score), 10, 35);
        }
    }

    private void move() {
        // Dinosaur physics
        velocityY += gravity;
        dinosaur.y += velocityY;

        if (dinosaur.y > boardHeight - dinosaur.height) {
            dinosaur.y = boardHeight - dinosaur.height;
            velocityY = 0;
            dinosaur.img = dinosaurImg;
        }

        // Move cacti
        for (Block cactus : cactusArray) {
            cactus.x += velocityX;
            if (collision(dinosaur, cactus)) {
                gameOver = true;
                dinosaur.img = dinosaurDeadImg;
            }
        }

        // Increase score
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
        move();
        repaint();
        if (gameOver) {
            placeCactusTimer.stop();
            gameLoop.stop();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (!gameOver && dinosaur.y == boardHeight - dinosaur.height) {
                velocityY = -17;
                dinosaur.img = dinosaurJumpImg;
            } else if (gameOver) {
                resetGame();
            }
        }
    }

    private void resetGame() {
        dinosaur.y = boardHeight - dinosaur.height;
        dinosaur.img = dinosaurImg;
        velocityY = 0;
        cactusArray.clear();
        score = 0;
        gameOver = false;
        gameLoop.start();
        placeCactusTimer.start();
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}

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
}