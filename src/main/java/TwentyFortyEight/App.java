package TwentyFortyEight;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;
import processing.event.KeyEvent;
import processing.event.MouseEvent;
import java.util.ArrayList;
import java.util.Random;

public class App extends PApplet {

    private int gridSize;
    public static final int CELLSIZE = 100;
    public static final int GAP = 10;
    public static final int CELL_BUFFER = 8;
    private int WIDTH;
    private int HEIGHT;
    public static final int FPS = 30;

    private Cell[][] board;
    public static Random random = new Random();

    public PImage eight;
    private boolean gameOver = false;

    private int startTime;
    private int elapsedSeconds;

    public App(int gridSize) {
        this.gridSize = gridSize;
        this.WIDTH = gridSize * (CELLSIZE + GAP) + GAP;
        this.HEIGHT = gridSize * (CELLSIZE + GAP) + GAP;
        board = new Cell[gridSize][gridSize];
    }

    @Override
    public void settings() {
        size(WIDTH, HEIGHT);
    }

    @Override
    public void setup() {
        frameRate(FPS);
        java.net.URL imageURL = this.getClass().getResource("8.png");
        if (imageURL != null) {
            this.eight = loadImage(imageURL.getPath().replace("%20", ""));
        }
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                board[i][j] = new Cell(j, i);
            }
        }
        spawnRandomBlock();
        spawnRandomBlock();
        startTime = millis();
        elapsedSeconds = 0;
    }

    @Override
    public void draw() {
        background(187, 173, 160);
        noStroke();
        fill(205, 193, 180);
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                float cellX = j * (CELLSIZE + GAP) + GAP;
                float cellY = i * (CELLSIZE + GAP) + GAP;
                rect(cellX, cellY, CELLSIZE, CELLSIZE, 5);
            }
        }
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                board[i][j].draw(this);
            }
        }
        if (!gameOver) {
            int currentTime = millis();
            elapsedSeconds = (currentTime - startTime) / 1000;
        }
        pushStyle();
        fill(255);
        textSize(14);
        textAlign(PConstants.RIGHT, PConstants.TOP);
        text("Time: " + elapsedSeconds + "s", width - 10, 10);
        popStyle();
        if (gameOver) {
            pushStyle();
            fill(0);
            textSize(60);
            textAlign(PConstants.CENTER, PConstants.CENTER);
            text("GAME OVER", width / 2, height / 2);
            popStyle();
        }
    }

    @Override
    public void keyPressed(KeyEvent event) {
        if (gameOver) {
            if (event.getKey() == 'r' || event.getKey() == 'R') {
                resetGame();
            }
            return;
        }
        int code = event.getKeyCode();
        boolean moved = false;
        if (code == PConstants.LEFT) {
            compressRowLeft();
            mergeRowLeft();
            compressRowLeft();
            moved = true;
        } else if (code == PConstants.RIGHT) {
            compressRowRight();
            mergeRowRight();
            compressRowRight();
            moved = true;
        } else if (code == PConstants.UP) {
            compressColUp();
            mergeColUp();
            compressColUp();
            moved = true;
        } else if (code == PConstants.DOWN) {
            compressColDown();
            mergeColDown();
            compressColDown();
            moved = true;
        }
        if (moved) {
            spawnRandomBlock();
            if (isGameOver()) {
                gameOver = true;
            }
        }
    }

    @Override
    public void keyReleased() { }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (!gameOver && e.getButton() == PConstants.LEFT) {
            int cellX = e.getX() / (CELLSIZE + GAP);
            int cellY = e.getY() / (CELLSIZE + GAP);
            if (cellX < gridSize && cellY < gridSize && board[cellY][cellX].getValue() == 0) {
                board[cellY][cellX].place();
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) { }

    private void compressRowLeft() {
        for (int row = 0; row < gridSize; row++) {
            for (int col = 1; col < gridSize; col++) {
                if (board[row][col].getValue() != 0) {
                    int currentCol = col;
                    while (currentCol > 0 && board[row][currentCol - 1].getValue() == 0) {
                        board[row][currentCol - 1].setValue(board[row][currentCol].getValue());
                        board[row][currentCol - 1].setDrawPosition(board[row][currentCol].getDrawX(), board[row][currentCol].getDrawY(), this);
                        board[row][currentCol].setValue(0);
                        currentCol--;
                    }
                }
            }
        }
    }

    private void mergeRowLeft() {
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize - 1; col++) {
                if (board[row][col].getValue() != 0 && board[row][col].getValue() == board[row][col + 1].getValue()) {
                    board[row][col].setValue(board[row][col].getValue() * 2);
                    board[row][col].setDrawPosition(board[row][col + 1].getDrawX(), board[row][col + 1].getDrawY(), this);
                    board[row][col + 1].setValue(0);
                }
            }
        }
    }

    private void compressRowRight() {
        for (int row = 0; row < gridSize; row++) {
            for (int col = gridSize - 2; col >= 0; col--) {
                if (board[row][col].getValue() != 0) {
                    int currentCol = col;
                    while (currentCol < gridSize - 1 && board[row][currentCol + 1].getValue() == 0) {
                        board[row][currentCol + 1].setValue(board[row][currentCol].getValue());
                        board[row][currentCol + 1].setDrawPosition(board[row][currentCol].getDrawX(), board[row][currentCol].getDrawY(), this);
                        board[row][currentCol].setValue(0);
                        currentCol++;
                    }
                }
            }
        }
    }

    private void mergeRowRight() {
        for (int row = 0; row < gridSize; row++) {
            for (int col = gridSize - 1; col > 0; col--) {
                if (board[row][col].getValue() != 0 && board[row][col].getValue() == board[row][col - 1].getValue()) {
                    board[row][col].setValue(board[row][col].getValue() * 2);
                    board[row][col].setDrawPosition(board[row][col - 1].getDrawX(), board[row][col - 1].getDrawY(), this);
                    board[row][col - 1].setValue(0);
                }
            }
        }
    }

    private void compressColUp() {
        for (int col = 0; col < gridSize; col++) {
            for (int row = 1; row < gridSize; row++) {
                if (board[row][col].getValue() != 0) {
                    int currentRow = row;
                    while (currentRow > 0 && board[currentRow - 1][col].getValue() == 0) {
                        board[currentRow - 1][col].setValue(board[currentRow][col].getValue());
                        board[currentRow - 1][col].setDrawPosition(board[currentRow][col].getDrawX(), board[currentRow][col].getDrawY(), this);
                        board[currentRow][col].setValue(0);
                        currentRow--;
                    }
                }
            }
        }
    }

    private void mergeColUp() {
        for (int col = 0; col < gridSize; col++) {
            for (int row = 0; row < gridSize - 1; row++) {
                if (board[row][col].getValue() != 0 && board[row][col].getValue() == board[row + 1][col].getValue()) {
                    board[row][col].setValue(board[row][col].getValue() * 2);
                    board[row][col].setDrawPosition(board[row + 1][col].getDrawX(), board[row + 1][col].getDrawY(), this);
                    board[row + 1][col].setValue(0);
                }
            }
        }
    }

    private void compressColDown() {
        for (int col = 0; col < gridSize; col++) {
            for (int row = gridSize - 2; row >= 0; row--) {
                if (board[row][col].getValue() != 0) {
                    int currentRow = row;
                    while (currentRow < gridSize - 1 && board[currentRow + 1][col].getValue() == 0) {
                        board[currentRow + 1][col].setValue(board[currentRow][col].getValue());
                        board[currentRow + 1][col].setDrawPosition(board[currentRow][col].getDrawX(), board[currentRow][col].getDrawY(), this);
                        board[currentRow][col].setValue(0);
                        currentRow++;
                    }
                }
            }
        }
    }

    private void mergeColDown() {
        for (int col = 0; col < gridSize; col++) {
            for (int row = gridSize - 1; row > 0; row--) {
                if (board[row][col].getValue() != 0 && board[row][col].getValue() == board[row - 1][col].getValue()) {
                    board[row][col].setValue(board[row][col].getValue() * 2);
                    board[row][col].setDrawPosition(board[row - 1][col].getDrawX(), board[row - 1][col].getDrawY(), this);
                    board[row - 1][col].setValue(0);
                }
            }
        }
    }

    private void spawnRandomBlock() {
        ArrayList<int[]> emptyCells = new ArrayList<>();
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                if (board[i][j].getValue() == 0) {
                    emptyCells.add(new int[]{i, j});
                }
            }
        }
        if (!emptyCells.isEmpty()) {
            int[] coords = emptyCells.get(random.nextInt(emptyCells.size()));
            board[coords[0]][coords[1]].place();
        }
    }

    private boolean isGameOver() {
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                if (board[i][j].getValue() == 0)
                    return false;
                if (j < gridSize - 1 && board[i][j].getValue() == board[i][j + 1].getValue())
                    return false;
                if (i < gridSize - 1 && board[i][j].getValue() == board[i + 1][j].getValue())
                    return false;
            }
        }
        return true;
    }

    private void resetGame() {
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                board[i][j].setValue(0);
                board[i][j].setDrawPosition(j * (CELLSIZE + GAP) + GAP, i * (CELLSIZE + GAP) + GAP, this);
            }
        }
        spawnRandomBlock();
        spawnRandomBlock();
        gameOver = false;
        startTime = millis();
        elapsedSeconds = 0;
    }

    public static void main(String[] args) {
        int gridSize = 4;
        if (args.length > 0) {
            try {
                gridSize = Integer.parseInt(args[0]);
                if (gridSize <= 0) gridSize = 4;
            } catch (NumberFormatException e) {
                gridSize = 4;
            }
        }
        App app = new App(gridSize);
        PApplet.runSketch(new String[] {"TwentyFortyEight.App"}, app);
    }
}
