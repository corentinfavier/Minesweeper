package edu.wit.comp1050;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class Controller {
    @FXML public AnchorPane root;
    @FXML public Button ButtonPlay, ButtonHelp, ButtonExit;

    public void play() throws IOException {
        root.getScene().setRoot(FXMLLoader.load(getClass().getResource("/minesweeper/Game.fxml")));
    }

    public void exit(){
        System.exit(0);
    }
}
