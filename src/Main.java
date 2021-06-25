import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Random;


public class Main extends Application {
    private static int mines=0;
    private static final int tileSize=40;
    private static String mode="Easy";  //default


    private static ArrayList<Tile> tiles = new ArrayList<>();
    private static Scene menuScene;
    private static Scene gameScene;
    private static Stage stage;

    @Override
    public void start(final Stage primaryStage){
        stage = primaryStage;
        stage.setTitle("MineSweeper");
        stage.setResizable(false);
        Text tDifficulty = new Text("Difficulty: ");
        ObservableList<String> difficulties = FXCollections.observableArrayList("Easy", "Medium", "Hard");
        final ComboBox<String> cbDifficulty = new ComboBox<>(difficulties);
        cbDifficulty.setValue("Easy");
        Button bStartGame = new Button("Start Game");
        Button bExit = new Button("Exit");

        bStartGame.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                mode = cbDifficulty.getValue();
                gameScene= startGame();
                primaryStage.setScene(gameScene);
                primaryStage.show();
            }});

        bExit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                primaryStage.close();
            }});


        GridPane layout = new GridPane();
        layout.setMinSize(200,200);
        layout.setAlignment(Pos.CENTER);
        layout.setVgap(5);
        layout.setHgap(5);

        layout.add(tDifficulty, 0,0);
        layout.add(cbDifficulty,1,0);
        layout.add(bStartGame,1,1);
        layout.add(bExit,1,2);

        menuScene = new Scene(layout, 200, 200);

        stage.setScene(menuScene);
        stage.show();
    }

    public static Scene startGame(){
        Tile.hasClicked=false;
        int tilesX=0, tilesY=0;
        switch (mode){
            case "Easy": {tilesX=10; tilesY=10; mines=20;} break;
            case "Medium":{tilesX=15; tilesY=15; mines=50;} break;
            case "Hard":{tilesX=20; tilesY=15; mines=100;} break;
            default: break;
        }
        int windowX=tilesX*tileSize;
        int windowY=tilesY*tileSize;

        GridPane gamePane = new GridPane();
        gamePane.setMinSize(windowX, windowY);
        tiles.clear();
        for(int y=0; y<tilesY; y++){
            for(int x=0; x<tilesX; x++){
                tiles.add(new Tile(x,y));
            }
        }

        for(Tile t:tiles){
            ArrayList<Tile> neighbors = new ArrayList<>();
            for(Tile other:tiles){
                if(!other.equals(t) && Math.abs(t.x- other.x)<=1 && Math.abs(t.y- other.y)<=1 ) {
                    neighbors.add(other);
                }
            }
            t.setNeighbors(neighbors);
        }

        for(Tile t:tiles){
            Button b = new Button();
            b.setMinSize(40,40);
            b.setOnAction(new MyHandler(t));
            gamePane.add(b, t.x, t.y);
            t.setButton(b);
        }


        Scene gameScene = new Scene(gamePane, windowX, windowY);
        return gameScene;
    }

    public static void fillMines(Tile clicked){
        int randomCtr = mines;
        Random rand = new Random();
        while (randomCtr>0){
                int randIndex=rand.nextInt(tiles.size());
                if(!tiles.get(randIndex).hasMine && !tiles.get(randIndex).equals(clicked)) {
                    tiles.get(randIndex).setHasMine(true);
                    randomCtr--;
                }
        }
    }

    public static void gameOver(int code){
        final Stage goWindow= new Stage();  //GameOver popup window
        goWindow.initModality(Modality.APPLICATION_MODAL);
        goWindow.setResizable(false);

        GridPane layout = new GridPane();
        layout.setMinSize(200,200);
        layout.setAlignment(Pos.CENTER);
        layout.setVgap(5);
        layout.setHgap(5);

        Text text = new Text();
        if(code == 1) text.setText("Game Over, You Won");
        if(code == 2) text.setText("Game Over, You Lost");
        Button bRetry = new Button("Retry");
        Button bMenu = new Button("Main Menu");
        Button bExit = new Button("Exit");



        bRetry.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                goWindow.close();
                gameScene = startGame();
                stage.setScene(gameScene);
                stage.show();
            }});

        bMenu.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                goWindow.close();
                stage.setScene(menuScene);
                stage.show();
            }});

        bExit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                goWindow.close();
                stage.close();
            }});

        layout.add(text ,1,0);
        layout.add(bRetry,1,1);
        layout.add(bMenu,1,2);
        layout.add(bExit,1,3);

        Scene goScene = new Scene(layout,200,200);
        goWindow.setScene(goScene);
        goWindow.showAndWait();

    }

    public static void check(){
        int counter = 0;
        for(Tile t: tiles){
            if(!t.isRevealed) counter++;
        }
        if(counter==mines) gameOver(1);
    }

    public static class MyHandler implements EventHandler<ActionEvent> {
        private final Tile tile;
        public MyHandler(Tile tile){
            this.tile=tile; }
        @Override
        public void handle(ActionEvent event) {
            tile.onClick();
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}
