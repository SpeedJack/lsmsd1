package ristogo.ui.graphics;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ristogo.common.entities.Restaurant;
import ristogo.common.entities.User;
import ristogo.common.entities.enums.Genre;
import ristogo.common.entities.enums.OpeningHours;
import ristogo.common.entities.enums.Price;
import ristogo.common.net.ResponseMessage;
import ristogo.net.Protocol;
import ristogo.ui.graphics.config.GUIConfig;

public class ModifyRestaurantForm extends VBox
{
	private TextField nameField;
	private ChoiceBox<Genre> genreField;
	private ChoiceBox<Price> costField;
	private TextField cityField;
	private TextField addressField;
	private TextArea descriptionField;
	private Spinner<Integer> seatsField;
	private ChoiceBox<String> hourField;

	private int idOwnRestaurant = 0;
	
	private Restaurant restaurant;
	private User loggedUser;

	public ModifyRestaurantForm(User loggedUser, Restaurant restaurant)
	{

		super(20);
		this.restaurant = restaurant;
		this.loggedUser = loggedUser;

		Label title = new Label("Modify your Restaurant");
		title.setStyle("-fx-underline: true;");
		title.setFont(GUIConfig.getFormTitleFont());
		title.setTextFill(GUIConfig.getFgColor());

		Label name = new Label("Name: ");
		nameField = new TextField();
		name.setFont(GUIConfig.getBoldTextFont());
		name.setTextFill(GUIConfig.getFgColor());

		HBox nameBox = new HBox(20);
		nameBox.getChildren().addAll(name, nameField);

		Label type = new Label("Type: ");
		type.setFont(GUIConfig.getBoldTextFont());
		type.setTextFill(GUIConfig.getFgColor());
		genreField = new ChoiceBox<Genre>();
		genreField.getItems().addAll(Genre.PIZZA, Genre.ITALIAN, Genre.MEXICAN, Genre.JAPANESE,
			Genre.STEAKHOUSE);

		HBox typeBox = new HBox(20);
		typeBox.getChildren().addAll(type, genreField);

		Label cost = new Label("Cost:");
		cost.setFont(GUIConfig.getBoldTextFont());
		cost.setTextFill(GUIConfig.getFgColor());

		costField = new ChoiceBox<Price>();
		costField.getItems().addAll(Price.ECONOMIC, Price.LOW, Price.MIDDLE, Price.HIGH, Price.LUXURY);

		HBox costBox = new HBox(20);
		costBox.getChildren().addAll(cost, costField);

		Label city = new Label("City: ");
		cityField = new TextField();
		city.setFont(GUIConfig.getBoldTextFont());
		city.setTextFill(GUIConfig.getFgColor());

		HBox cityBox = new HBox(20);
		cityBox.getChildren().addAll(city, cityField);

		Label address = new Label("Address: ");
		addressField = new TextField();
		address.setFont(GUIConfig.getBoldTextFont());
		address.setTextFill(GUIConfig.getFgColor());

		HBox addressBox = new HBox(20);
		addressBox.getChildren().addAll(address, addressField);

		Label desc = new Label("Description: ");
		descriptionField = new TextArea();
		desc.setFont(GUIConfig.getBoldTextFont());
		desc.setTextFill(GUIConfig.getFgColor());
		descriptionField.setWrapText(true);
		descriptionField.setMinSize(480, 100);
		descriptionField.setMaxSize(480, 100);

		Label seats = new Label("Seats: ");
		seatsField = new Spinner<Integer>(1, Integer.MAX_VALUE, restaurant.getSeats(), 1);
		seats.setFont(GUIConfig.getBoldTextFont());
		seats.setTextFill(GUIConfig.getFgColor());

		HBox seatsBox = new HBox(20);
		seatsBox.getChildren().addAll(seats, seatsField);

		Label hour = new Label("Hour:");
		hour.setFont(GUIConfig.getBoldTextFont());
		hour.setTextFill(GUIConfig.getFgColor());

		hourField = new ChoiceBox<String>(FXCollections.observableArrayList("Lunch", "Dinner", "Lunch/Dinner"));

		HBox hourBox = new HBox(20);
		hourBox.getChildren().addAll(hour, hourField);

//////////////////error/////////////////////////////////////////////////////
		Label error = new Label();
		error.setFont(GUIConfig.getTextFont());
		error.setTextFill(GUIConfig.getInvertedFgColor());
		error.setStyle("-fx-background-color:   red;");
		error.setVisible(false);
///////////////////////////////////////////////////////////////////////////////

		Button commit = new Button("Commit");
		commit.setFont(GUIConfig.getButtonFont());
		commit.setTextFill(GUIConfig.getInvertedFgColor());
		commit.setStyle(GUIConfig.getInvertedCSSBgColorButton());

		setRestaurant(restaurant);

		commit.setOnAction((ActionEvent ev) -> {
			error.setVisible(false);
			try {
				String n = nameField.getText();
				Genre g = genreField.getValue();
				Price p = costField.getValue();
				String ct = cityField.getText();
				String add = addressField.getText();
				String d = descriptionField.getText();
				int s = seatsField.getValue();
				OpeningHours h;
				if (hourField.getValue().equals("Lunch")) {
					h = OpeningHours.LUNCH;
				} else if (hourField.getValue().contentEquals("Dinner")) {
					h = OpeningHours.DINNER;
				} else {
					h = OpeningHours.BOTH;
				}

				ResponseMessage res = Protocol.getInstance()
					.editRestaurant(new Restaurant(restaurant.getId(), n,
						loggedUser.getUsername(), g, p, ct, add, d, s, h));
				if (!res.isSuccess()) {
					error.setText("Error: " + res.getErrorMsg());
					error.setVisible(true);
				}

			} catch (NullPointerException e) {
				e.getMessage();
				error.setText("Error: fill out the entire form to be able to commit");
				error.setVisible(true);
			}
		});

		VBox boxModify = new VBox(20);
		boxModify.getChildren().addAll(title, nameBox, typeBox, costBox, cityBox, addressBox, desc, descriptionField,
			seatsBox, hourBox, error, commit);
		boxModify.setStyle("-fx-padding: 7;" + "-fx-border-style: solid inside;" + "-fx-border-width: 2;" +
			"-fx-border-insets: 3;" + "-fx-border-radius: 10;" + "-fx-border-color: " + "D9561D" + ";");

		this.getChildren().addAll(boxModify);
		this.setPrefSize(400, 600);
		this.setStyle("-fx-padding: 7;" + "-fx-border-width: 2;" + "-fx-border-insets: 3;" +
			"-fx-border-radius: 10;");

	}
	
	public void setRestaurant(Restaurant restaurant)
	{
		this.restaurant = restaurant;
		nameField.setText(restaurant.getName());
		genreField.setValue(restaurant.getGenre());
		costField.setValue(restaurant.getPrice());
		cityField.setText(restaurant.getCity());
		addressField.setText(restaurant.getAddress());
		descriptionField.setText(restaurant.getDescription());
		seatsField.getValueFactory().setValue(restaurant.getSeats());
		hourField.getItems().clear();
		switch(restaurant.getOpeningHours()) {
		case BOTH:
			hourField.getItems().addAll(OpeningHours.LUNCH.toString(), OpeningHours.DINNER.toString());
			break;
		default:
			hourField.getItems().add(restaurant.getOpeningHours().toString());
		}
	}
}
