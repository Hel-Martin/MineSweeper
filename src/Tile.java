import javafx.scene.control.Button;

import java.util.List;

public class Tile {
    public static boolean hasClicked=false;     //first click

    public int x, y;
    public boolean hasMine = false;
    private List<Tile> neighbors;
    public boolean isRevealed;
    private Button button;

    public Tile(int x, int y){
        this.x=x;
        this.y=y;
        isRevealed =false;
    }

    public void setNeighbors(List<Tile> neighbors) {
        this.neighbors = neighbors;
    }

    public void setHasMine(boolean hasMine) {
        this.hasMine = hasMine;
    }

    public void setButton(Button button) {
        this.button = button;
    }

    public void onClick(){
        if(!hasClicked){
            hasClicked=true;
            Main.fillMines(this);
        }
        if(!isRevealed) {
            button.setDisable(true);
            isRevealed = true;
            if (hasMine) {
                button.setText("X");
                Main.gameOver(2);
            }
            else {
                int mineCtr = 0;
                for (Tile t : neighbors) {
                    if (t.hasMine) mineCtr++;
                }
                if (mineCtr != 0) button.setText(String.valueOf(mineCtr));
                else {
                    for (Tile t : neighbors) {
                        t.onClick();
                    }
                }
                Main.check();
            }
        }
    }

}
