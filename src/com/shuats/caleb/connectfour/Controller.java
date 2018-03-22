package com.shuats.caleb.connectfour;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Controller implements Initializable {
	private static final int COLUMNS = 7;
	private static final int ROWS = 6;
	private static final int CIRCLE_DIAMETER = 120;
	private static final String DISC_COLOR1 = "#24303E";
	private static final String DISC_COLOR2 = "#4CAA88";

	private static String playerOne = "Player One";
	private static String playerTwo = "Player Two";

	private boolean isPlayerOneTurn = true;
	private boolean  isAllowedToInsert = true;

	private Disc[][] insertedDiscsArray = new Disc[ROWS][COLUMNS];

	@FXML
	public GridPane rootGridPane;

	@FXML
	public Pane insertedDiscPane, playerDetails;

	@FXML
	public Label playerNameLabel;

	@FXML
	public TextField playerOneName, playerTwoName;

	@FXML
	public Button submitButton;

	public void createPlayground() {

		Shape rectangleWithHoles = createGameStructuralGrid();
		rootGridPane.add(rectangleWithHoles,0,1);

		List<Rectangle> rectangleList = createClickableRectangle();
		for (Rectangle rectangle: rectangleList) {

			rootGridPane.add(rectangle, 0,1);
		}

		submitButton.setOnAction(event -> {
			playerOne = playerOneName.getText();
			playerTwo = playerTwoName.getText();
		});
	}

	public Shape createGameStructuralGrid() {

		Shape rectangleWithHoles = new Rectangle((COLUMNS+1) * CIRCLE_DIAMETER, (ROWS+1) * CIRCLE_DIAMETER);

		for (int row = 0; row < ROWS; row++) {
			for (int col = 0; col < COLUMNS; col++) {

				Circle circle = new Circle();
				circle.setRadius(CIRCLE_DIAMETER / 2);
				circle.setCenterX(CIRCLE_DIAMETER / 2);
				circle.setCenterY(CIRCLE_DIAMETER / 2);

				circle.setTranslateX(col * (CIRCLE_DIAMETER + 5)+(CIRCLE_DIAMETER / 4));
				circle.setTranslateY(row * (CIRCLE_DIAMETER + 5)+(CIRCLE_DIAMETER / 4));

				rectangleWithHoles = Shape.subtract(rectangleWithHoles, circle);
			}

		}
		rectangleWithHoles.setFill(Color.WHITE);

		return rectangleWithHoles;
	}

	private List<Rectangle> createClickableRectangle() {

		List<Rectangle> rectangleList = new ArrayList<>();

		for (int col = 0; col < COLUMNS ; col++) {
			Rectangle rectangle = new Rectangle(CIRCLE_DIAMETER, CIRCLE_DIAMETER * (ROWS + 1));
			rectangle.setTranslateX(col * (CIRCLE_DIAMETER + 5) + (CIRCLE_DIAMETER/4));
			rectangle.setFill(Color.TRANSPARENT);

			rectangle.setOnMouseEntered(event -> rectangle.setFill(Color.valueOf("bbbbbb22")));
			rectangle.setOnMouseExited(event -> rectangle.setFill(Color.TRANSPARENT));

			final int column = col;
			rectangle.setOnMouseClicked(event -> {
				if (isAllowedToInsert){
					isAllowedToInsert = false;
					insertDisc(new Disc(isPlayerOneTurn), column);
				}
			});

			rectangleList.add(rectangle);
		}
		return rectangleList;
	}

	private void insertDisc(Disc disc, int column) {

		int row = ROWS - 1;

		while(row > 0) {
			if(insertedDiscsArray[row][column] == null) break;
			row--;
		}

		if(row < 0) return;

		insertedDiscsArray[row][column]=disc;
		insertedDiscPane.getChildren().addAll(disc);

		disc.setTranslateX(column * (CIRCLE_DIAMETER + 5) + (CIRCLE_DIAMETER / 4));

		int currentRow = row;
		TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.5),disc);
		translateTransition.setToY(row * (CIRCLE_DIAMETER + 5) + (CIRCLE_DIAMETER / 4));
		translateTransition.setOnFinished(event -> {

			isAllowedToInsert = true;
			if(gameEnded(currentRow, column))gameOver();

			isPlayerOneTurn = !isPlayerOneTurn;
			playerNameLabel.setText(isPlayerOneTurn? playerOne: playerTwo);
		});

		translateTransition.play();
		}

	private boolean gameEnded(int row, int column) {

			//Vertical Points
			List<Point2D> verticalPoints = IntStream.rangeClosed(row - 3,row + 3)
					.mapToObj(r -> new Point2D(r,column))
					.collect(Collectors.toList());

			//Horizontal Points
			List<Point2D> horizontalPoints = IntStream.rangeClosed(column - 3, column + 3)
					.mapToObj(col -> new Point2D(row, col))
					.collect(Collectors.toList());

			//Diagonal 1
			Point2D startPoint1 = new Point2D(row - 3, column + 3);
			List<Point2D> diagonal1Points = IntStream.rangeClosed(0, 6)
					.mapToObj(i -> startPoint1.add(i, -i))
					.collect(Collectors.toList());

			//Diagonal 2
			Point2D startPoint2 = new Point2D(row - 3, column - 3);
			List<Point2D> diagonal2Points = IntStream.rangeClosed(0, 6)
					.mapToObj(i -> startPoint2.add(i, i))
					.collect(Collectors.toList());

			boolean isEnded = checkCombinations(verticalPoints) || checkCombinations(horizontalPoints)
					|| checkCombinations(diagonal1Points) || checkCombinations(diagonal2Points);

			return isEnded;
		}

	private boolean checkCombinations(List<Point2D> points)
	{
		int chain = 0;
			for (Point2D point: points)
			{
				int rowIndexForArray = (int) point.getX();
				int columnIndexForArray = (int) point.getY();

				Disc disc = getDiscIfPresent(rowIndexForArray,columnIndexForArray);
				if (disc != null && disc.isPlayerOneMove == isPlayerOneTurn)
				{
					chain++;
					if (chain == 4) return true;
				}
				else chain = 0;
			}
		return false;
	}

	private Disc getDiscIfPresent(int row, int column) {    // To prevent ArrayIndexOutOfBoundException

		if (row >= ROWS || row < 0 || column >= COLUMNS || column < 0)  // If row or column index is invalid
			return null;

		return insertedDiscsArray[row][column];
	}

	private void gameOver() {
		String winner = isPlayerOneTurn ? playerOne: playerTwo;
		System.out.println("Winner is: " + winner);

		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Connect Four");
		alert.setHeaderText("The Winner is " + winner);
		alert.setContentText("Want to play again? ");

		ButtonType yesBtn = new ButtonType("Yes");
		ButtonType noBtn = new ButtonType("No, Exit");
		alert.getButtonTypes().setAll(yesBtn, noBtn);

		Platform.runLater(() -> { // Helps us to resolve IllegalStateException.

			Optional<ButtonType> btnClicked = alert.showAndWait();
			if (btnClicked.isPresent() && btnClicked.get() == yesBtn ) {
				// ... user chose YES so RESET the game
				resetGame();
			} else {
				// ... user chose NO .. so Exit the Game
				Platform.exit();
				System.exit(0);
			}
		});
	}

	public void resetGame() {

		insertedDiscPane.getChildren().clear();    // Remove all Inserted Disc from Pane

		for (int row = 0; row < insertedDiscsArray.length; row++) { // Structurally, Make all elements of insertedDiscsArray[][] to null
			for (int col = 0; col < insertedDiscsArray[row].length; col++) {
				insertedDiscsArray[row][col] = null;
			}
		}

		isPlayerOneTurn = true; // Let player start the game
		playerNameLabel.setText(playerOne);

		createPlayground(); // Prepare a fresh playground
	}

	private static class Disc extends Circle {

			private final boolean isPlayerOneMove;

			public Disc(boolean isPlayerOneMove){
				this.isPlayerOneMove = isPlayerOneMove;
				setRadius(CIRCLE_DIAMETER/2);
				setCenterX(CIRCLE_DIAMETER/2);
				setCenterY(CIRCLE_DIAMETER/2);
				setFill(isPlayerOneMove?Color.valueOf(DISC_COLOR1):Color.valueOf(DISC_COLOR2));
			}
		}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}
}