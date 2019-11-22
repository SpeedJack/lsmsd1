package ristogo.ui.graphics;

import java.io.IOException;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import ristogo.common.entities.Restaurant;
import ristogo.common.entities.User;
import ristogo.common.entities.enums.OpeningHours;
import ristogo.net.Protocol;
import ristogo.ui.graphics.config.GUIConfig;

public class RistogoGUI extends Application
{
	private HBox applicationInterface;
	private static User loggedUser;
	private static Restaurant myRestaurant;

	@Override
	public void start(Stage stage) throws Exception
	{
		applicationInterface = new HBox(10);
		LoginDialog login = new LoginDialog();
		login.showAndWait();

		while (login.getResult() == null || login.getResult().getId() == -1)
			login.showAndWait();
		loggedUser = login.getResult();
		if (loggedUser.isOwner())
			buildRestaurantOwnerInterface();
		else
			buildCostumerInterface();
		Scene scene = new Scene(new Group(applicationInterface));
		scene.setFill(GUIConfig.getBgColor());

		stage.setOnCloseRequest((WindowEvent ev) -> {
			try {
					Protocol.getInstance().performLogout();
				} catch (IOException e) {
					e.printStackTrace();
				}
		});
		stage.setTitle("RistoGo");
		stage.setResizable(true);
		stage.setScene(scene);
		stage.getIcons().add(new Image("/resources/logo.png"));
		stage.show();
	}

	public static void launch(String... args)
	{
		Application.launch(args);
	}

	private void buildCostumerInterface()
	{

		GridPane title = generateTitle();

		TableViewReservation reservation = new TableViewReservation(true);
		reservation.listReservations(true);

		BookForm bookForm = new BookForm(reservation::listReservations);

		Label subTitle = new Label("List of Restaurant: you can find restaurants searching by city");
		subTitle.setFont(GUIConfig.getFormTitleFont());
		subTitle.setTextFill(GUIConfig.getFgColor());
		subTitle.setStyle("-fx-underline: true;");

		TextField findCityField = new TextField();
		findCityField.setPromptText("insert a name of a city");
		findCityField.setMinSize(200, 30);
		findCityField.setMaxSize(200, 30);
		Button find = new Button("Find");
		find.setFont(GUIConfig.getButtonFont());
		find.setTextFill(GUIConfig.getInvertedFgColor());
		find.setStyle(GUIConfig.getInvertedCSSBgColorButton());

		HBox findBox = new HBox(10);
		findBox.getChildren().addAll(findCityField, find);


		TableViewRestaurant restaurant = new TableViewRestaurant();
		restaurant.listRestaurants();

		Label description = new Label("Description: ");
		TextArea descriptionField = new TextArea();
		description.setFont(GUIConfig.getBoldVeryTinyTextFont());
		description.setTextFill(GUIConfig.getFgColor());
		descriptionField.setWrapText(true);
		descriptionField.setEditable(false);
		descriptionField.setMinSize(480, 100);
		descriptionField.setMaxSize(480, 100);

		HBox descriptionBox = new HBox(20);
		descriptionBox.getChildren().addAll(description, descriptionField);

		Label subTitle2 = new Label("My Reservation");
		subTitle2.setFont(GUIConfig.getFormTitleFont());
		subTitle2.setTextFill(GUIConfig.getFgColor());
		subTitle2.setStyle("-fx-underline: true;");

		restaurant.setOnMouseClicked((e) -> {
			try {
				bookForm.getDelRes().setDisable(true);
				bookForm.fillOutForm(restaurant.getSelectionName(), restaurant.getSelectionHours());
				descriptionField.setText(restaurant.getSelectionDescription());
				bookForm.setIdResToReserve(restaurant.getSelectionModel().getSelectedItem().getId());
			} catch (NullPointerException ex) {
				// do nothing
			}
		});

		reservation.setOnMouseClicked((e) -> {
			try {
				bookForm.getDelRes().setDisable(false);
				bookForm.fillOutForm("", OpeningHours.BOTH);
				bookForm.setIdResToDelete(reservation.getSelectionModel().getSelectedItem().getId());
			} catch (NullPointerException ex) {
				// do nothing
			}
		});

		find.setOnAction((ActionEvent ev) -> {
			try {
				String c = findCityField.getText();
				restaurant.listRestaurants(c);
			} catch (NullPointerException ex) {
				restaurant.listRestaurants(null);
			}

		});

		VBox leftPart = new VBox(10);
		leftPart.getChildren().addAll(title, bookForm);
		leftPart.setPrefSize(400, 600);
		leftPart.setStyle("-fx-padding: 7;" + "-fx-border-width: 2;" + "-fx-border-insets: 3;" +
			"-fx-border-radius: 10;");
		VBox rightPart = new VBox(10);
		rightPart.getChildren().addAll(subTitle,findBox, restaurant, descriptionBox, subTitle2, reservation);
		rightPart.setPrefSize(600, 600);
		rightPart.setStyle("-fx-padding: 7;" + "-fx-border-width: 2;" + "-fx-border-insets: 3;" +
			"-fx-border-radius: 10;");

		applicationInterface.getChildren().addAll(leftPart, rightPart);
		applicationInterface.setPrefSize(1000, 600);
	}

	private void buildRestaurantOwnerInterface()
	{

		GridPane title = generateTitle();
		ModifyRestaurantForm modifyForm = new ModifyRestaurantForm();

		Label subTitle = new Label("List of Reservations at your restaurant");
		subTitle.setStyle("-fx-underline: true;");
		subTitle.setFont(GUIConfig.getFormTitleFont());
		subTitle.setTextFill(GUIConfig.getFgColor());

		TableViewReservation reservation = new TableViewReservation(false);
		reservation.listReservations(false);

		Button refresh = new Button("Refresh");
		refresh.setFont(GUIConfig.getButtonFont());
		refresh.setTextFill(GUIConfig.getInvertedFgColor());
		refresh.setStyle(GUIConfig.getInvertedCSSBgColorButton());

		refresh.setOnAction((ActionEvent ev) -> {
			reservation.listReservations(false);
		});

		VBox leftPart = new VBox(10);
		leftPart.getChildren().addAll(title, modifyForm);
		leftPart.setPrefSize(400, 600);
		leftPart.setStyle("-fx-padding: 7;" + "-fx-border-width: 2;" + "-fx-border-insets: 3;" +
			"-fx-border-radius: 10;");
		VBox rightPart = new VBox(10);
		rightPart.getChildren().addAll(subTitle, reservation, refresh);
		rightPart.setAlignment(Pos.CENTER);
		rightPart.setPrefSize(600, 600);
		rightPart.setStyle("-fx-padding: 7;" + "-fx-border-width: 2;" + "-fx-border-insets: 3;" +
			"-fx-border-radius: 10;");

		applicationInterface.getChildren().addAll(leftPart, rightPart);
		applicationInterface.setPrefSize(1000, 600);

	}

	private GridPane generateTitle()
	{

		Label title = new Label("RistoGo");
		title.setFont(GUIConfig.getTitleFont());
		title.setTextFill(GUIConfig.getFgColor());

		ImageView icon = new ImageView("resources/logo.png");
		icon.setFitHeight(30);
		icon.setFitWidth(30);

		Label title2 = new Label("Welcome ");
		title2.setFont(GUIConfig.getSubtitleFont());
		title2.setTextFill(GUIConfig.getFgColor());

		Label nameUser = new Label(loggedUser.getUsername());
		nameUser.setFont(GUIConfig.getBoldSubtitleFont());
		nameUser.setTextFill(GUIConfig.getFgColor());

		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(30);
		grid.setPadding(new Insets(1, 1, 5, 1));

		grid.add(title, 0, 0);
		grid.add(icon, 1, 0);
		grid.add(title2, 0, 1);
		grid.add(nameUser, 1, 1);

		return grid;
	}

	public static User getLoggedUser()
	{
		return loggedUser;
	}

	public static void setLoggedUser(User loggedUser)
	{
		RistogoGUI.loggedUser = loggedUser;
	}

	public static Restaurant getMyRestaurant()
	{
		return myRestaurant;
	}

	public static void setMyRestaurant(Restaurant myRestaurant)
	{
		RistogoGUI.myRestaurant = myRestaurant;
	}
}
