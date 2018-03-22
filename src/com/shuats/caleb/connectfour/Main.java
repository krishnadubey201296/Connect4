package com.shuats.caleb.connectfour;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application {
    private Controller controller;

    @Override
    public void start(Stage primaryStage) throws Exception{

        FXMLLoader loader = new FXMLLoader(getClass().getResource("gameUI.fxml"));
        GridPane rootGridPane = loader.load();

        controller = loader.getController();
        controller.createPlayground();

        MenuBar menuBar = createMenu();
        menuBar.prefWidthProperty().bind(primaryStage.widthProperty());

        Pane menuPane = (Pane) rootGridPane.getChildren().get(0);
        menuPane.getChildren().addAll(menuBar);

//        Pane playerDetails = controller.playerDetails;

//        TextField playerOneName = controller.playerOneName;
//        TextField playerTwoName = controller.playerTwoName;
//
//        Button submitButton = controller.submitButton;
//        submitButton.setOnAction(event -> {
//            controller.setPlayerOne(playerOneName.getText());
//            controller.setPlayerTwo(playerTwoName.getText());
//        });

        Scene scene = new Scene(rootGridPane);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Connect 4");
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public MenuBar createMenu(){

        //File Menu Items
        MenuItem newGame = new MenuItem("New Game");
        newGame.setOnAction(event -> controller.resetGame());

        MenuItem restartGame = new MenuItem("Restart Game");
        restartGame.setOnAction(event -> controller.resetGame());

        SeparatorMenuItem separatorMenuItem1 = new SeparatorMenuItem();
        MenuItem exitGame = new MenuItem("Exit Game");
        exitGame.setOnAction(event -> exitGame());

        //Help Menu Items
        MenuItem aboutGame = new MenuItem("About Connect 4");
        aboutGame.setOnAction(event -> aboutGame());

        SeparatorMenuItem separatorMenuItem2 = new SeparatorMenuItem();
        MenuItem aboutMe = new MenuItem("About Me");
        aboutMe.setOnAction(event -> aboutMe());

        //File Menu
        Menu fileMenu = new Menu("File");
        fileMenu.getItems().addAll(newGame, restartGame, separatorMenuItem1, exitGame);

        //Help Menu
        Menu helpMenu = new Menu("Help");
        helpMenu.getItems().addAll(aboutGame,separatorMenuItem2, aboutMe);

        //Menu Bar
        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(fileMenu, helpMenu);

        return menuBar;
    }

    private void aboutMe() {
        Alert aboutMe = new Alert(Alert.AlertType.INFORMATION);
        aboutMe.setTitle("About The Developer");
        aboutMe.setHeaderText("Caleb Jebadurai");
        aboutMe.setContentText("I am learning to build many more awesome Java applications. Thank you for playing!");
        aboutMe.show();
    }

    private void aboutGame() {
        Alert aboutGame = new Alert(Alert.AlertType.INFORMATION);
        aboutGame.setTitle("Connect 4");
        aboutGame.setHeaderText("How to play?");
        aboutGame.setContentText("Drop the discs turn by turn and try to get 4 discs" +
                " in continuous line diagonally, vertically or horizontally." +
                "The player who gets 4 discs in line wins the GAME!");
        aboutGame.show();
    }

    private void exitGame() {
        Platform.exit();
        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
