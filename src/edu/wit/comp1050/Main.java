package edu.wit.comp1050;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;


public class Main extends Application {

    private static int TILE_SIZE = 40;
    private static int WIDTH = 360;
    private static int HEIGHT = 440;
    private static int X_TILES;
    private static int Y_TILES;
    private int BOMB_COUNT = 0;
    private int TILE_COUNT = 0;
    private static double BOMB_CHANCE = 0.2;
    private Tile[][] grid = new Tile[X_TILES][Y_TILES];
    private int firstClick = 0;
    private boolean lost = false;
    private long startTime = System.currentTimeMillis();
    Face Smiley = new Face();
    Pane root = new Pane();
    Scene scene = new Scene(root, WIDTH, HEIGHT);
    Text timer = new Text();
    Text win = new Text();
    private static int timerX = 0;
    private static int timerY = 0;
    private static int timerFont = 0;
    private static int winX = 0;


    //Creating the Face to restart
    private class Face extends StackPane{
        Rectangle rect = new Rectangle(50, 50);
        Text smiley = new Text(	"\uD83D\uDE03"); //Winning Face
        Text sad = new Text("\uD83D\uDE23"); //Losing Face
        public Face(){
            rect.setStyle("-fx-fill: lightgray; -fx-stroke: black; -fx-stroke-width: 5;");
            smiley.setStyle("-fx-font: 40 arial;");
            sad.setStyle("-fx-font: 40 arial;");
            smiley.setVisible(true);
            sad.setVisible(false);
            setTranslateX((WIDTH-40)/2 - 10);
            setTranslateY(20);
            getChildren().addAll(rect, smiley, sad);


            setOnMouseClicked(event -> { //Click to reset board and remove timer + win message
                makeTiles();
                lost = false;
                switchFace(Smiley, 0);
                win.setVisible(false);
                timer.setVisible(false);
            });
        }
    }

    //Creating individual Tile
    private class Tile extends StackPane {
        int x, y;
        boolean hasBomb;
        boolean clicked = false;

        Rectangle rect = new Rectangle(TILE_SIZE - 2, TILE_SIZE - 2);
        Text text = new Text();

        public Tile(int x, int y, boolean hasBomb) {
            TILE_COUNT++;
            this.x = x;
            this.y = y;
            this.hasBomb = hasBomb;

            rect.setFill(Color.LIGHTGRAY);
            rect.setStroke(Color.BLACK);
            text.setStyle("-fx-font: 24 arial;");
            text.setText(hasBomb ? "X" : ""); //Setting bomb
            if(text.getText().equals("X"))
                BOMB_COUNT++;
            text.setVisible(false);

            getChildren().addAll(rect, text);
            setTranslateX(x * TILE_SIZE);
            setTranslateY(y * TILE_SIZE);

            setOnMouseClicked(e -> click()); //Run click() when you click on a Tile

        }

        public void click() {
            if (clicked || lost) //Do not do anything if you already clicked on the Tile or lost the game
                return;
            firstClick++;
            if(TILE_COUNT - firstClick == BOMB_COUNT){ //Check if there are only bombs left
                System.out.println("You win!");
                win.setX(winX);
                win.setY(timerY);
                win.setStyle("-fx-font: " + String.valueOf(timerFont) + " arial;");
                win.setText("You Win!"); //Show You Win! text
                win.setVisible(true);
                timer.setX(timerX);
                timer.setY(timerY);
                timer.setStyle("-fx-font: " + String.valueOf(timerFont) + " arial;");
                int timeS = (int) (System.currentTimeMillis() - startTime)/1000;
                int timeM = timeS/60;
                timeS = timeS - timeM*60;
                timer.setText("Time spent: " + String.valueOf(timeM) + "m " + String.valueOf(timeS) + "s"); //Show Timer text
                timer.setVisible(true);
                lost = true;
            }
            if(firstClick == 1){ //First Tile opened
                int bombNeighbors = getNeighborCount(this);
                if (text.getText().equals("X")) { //Check if first Tile is a bomb
                    if(bombNeighbors == 0){
                        text.setText(""); //Change text to 0 if no surrounding bombs
                    }
                    else {
                        text.setText(String.valueOf(bombNeighbors)); //Change text to number of surrounding bombs
                    }
                    BOMB_COUNT--;
                }
                startTime = System.currentTimeMillis();
            }
            clicked = true; //Set Tile to clicked -> line 105
            text.setVisible(true); //Show surrounding bombs
            rect.setFill(Color.rgb(189, 189, 189));
            if (text.getText().equals("")) {
                openNeighbors(this); //Open neighbors of empty Tile : openNeighbors reruns click -> all surrounding empty Tiles get opened recursively
            }
            if (text.getText().equals("X")) { //If you click a bomb
                System.out.println("Game Over!");
                openAll(); //Reveal all bombs
                lost = true;
                switchFace(Smiley, 1); //Switch face to losing face
                timer.setX(timerX);
                timer.setY(timerY);
                timer.setStyle("-fx-font: " + String.valueOf(timerFont) + " arial;");
                int timeS = (int) (System.currentTimeMillis() - startTime)/1000;
                int timeM = timeS/60;
                timeS = timeS - timeM*60;
                timer.setText("Time spent: " + String.valueOf(timeM) + "m " + String.valueOf(timeS) + "s"); //Show Timer text
                timer.setVisible(true);
            }

        }
    }


    //Switch between winning and losing faces
    public void switchFace(Face smiley, int tf){
        this.Smiley = smiley;
        if(tf == 1){
            smiley.smiley.setVisible(false);
            smiley.sad.setVisible(true);
        }
        else {
            smiley.smiley.setVisible(true);
            smiley.sad.setVisible(false);
        }
    }
    //Open all bomb Tiles
    public void openAll(){
        for (int y = 2; y < Y_TILES; y++) {
            for (int x = 0; x < X_TILES; x++) {
                Tile tile = grid[x][y];
                if(tile.text.getText().equals("X")) {
                    tile.text.setVisible(true);
                    tile.rect.setFill(Color.rgb(189, 189, 189));
                }
            }
        }
    }
    //Open surrounding 3x3 Tiles and run click() to open other empty Tiles
    public void openNeighbors(Tile tile) {
        int[] surroundings = new int[]{
                -1, -1,
                -1, 0,
                -1, 1,
                0, -1,
                0, 1,
                1, -1,
                1, 0,
                1, 1
        };
        for (int i = 0; i < surroundings.length; i++) {
            int surroundingX = tile.x + surroundings[i];
            int surroundingY = tile.y + surroundings[++i];
            if (surroundingX >= 0 && surroundingX < X_TILES && surroundingY >= 2 && surroundingY < Y_TILES) {
                grid[surroundingX][surroundingY].click();
            }
        }
    }
    //Return number of surrounding bombs to set number on Tile
    public int getNeighborCount(Tile tile){
        int bombNeighbors = 0;
        int[] surroundings = new int[] {
                -1, -1,
                -1,  0,
                -1,  1,
                0, -1,
                0,  1,
                1, -1,
                1,  0,
                1,  1
        };
        for(int i = 0; i< surroundings.length; i++){
            int surroundingX = tile.x + surroundings[i];
            int surroundingY = tile.y + surroundings[++i];
            if(surroundingX >= 0 && surroundingX < X_TILES && surroundingY >= 2 && surroundingY < Y_TILES){
                if(grid[surroundingX][surroundingY].hasBomb)
                    bombNeighbors++;
            }

        }
        return bombNeighbors;
    }
    //Reset the board and remake new tiles
    private void makeTiles() {
        BOMB_COUNT = 0;
        TILE_COUNT = 0;
        firstClick = 0;
        for (int y = 2; y < Y_TILES; y++){
            for(int x = 0; x < X_TILES; x++){
                Tile tile = new Tile(x, y, Math.random() < BOMB_CHANCE);
                grid[x][y] = tile;
                root.getChildren().add(tile);
            }
        }
        for (int y = 2; y < Y_TILES; y++){
            for(int x = 0; x < X_TILES; x++){
                Tile tile = grid[x][y];
                int bombNeighbors = getNeighborCount(tile);
                if(!tile.hasBomb && bombNeighbors>0)
                    tile.text.setText(String.valueOf(bombNeighbors));
            }
        }
    }


    @Override //Make stage
    public void start(Stage primaryStage) throws Exception {
        makeTiles();
        root.getChildren().addAll(Smiley, timer, win);
        timer.setVisible(false);
        primaryStage.setTitle("Minesweeper");
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    //Difficulty arguments
    public static void main(String[] args) {
        if(args.length>1){
            System.out.println("Only print out difficulty: easy, medium, difficult");
            System.exit(0);
        }
        if(args.length == 0){
            System.out.println("Error! Please enter the difficulty at the end: easy, medium, difficult");
            System.exit(0);
        }
        else if(args[0].equalsIgnoreCase("easy")){
            WIDTH = 40*9;
            HEIGHT = 40*9 + 80;
            X_TILES = 9;
            Y_TILES = 11;
            BOMB_CHANCE = 10.0/81;
            timerX = 215;
            timerY = 50;
            timerFont = 15;
            winX = 35;

        }
        else if(args[0].equalsIgnoreCase("medium")){
            WIDTH = 40*16;
            HEIGHT = 40*16 + 80;
            X_TILES = 16;
            Y_TILES = 18;
            BOMB_CHANCE = 40.0/196;
            timerX = 383;
            timerY = 53;
            timerFont = 25;
            winX = 80;
        }
        else if(args[0].equalsIgnoreCase("difficult")){
            WIDTH = 40*30;
            HEIGHT = 40*16 + 80;
            X_TILES = 30;
            Y_TILES = 18;
            BOMB_CHANCE = 99.0/480;
            timerX = 725;
            timerY = 57;
            timerFont = 35;
            winX = 210;
        }
        launch(args);
    }
}


