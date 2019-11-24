package ristogo.ui.graphics;

import java.time.LocalDate;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ristogo.common.entities.Reservation;
import ristogo.common.entities.Restaurant;
import ristogo.common.entities.enums.OpeningHours;
import ristogo.common.entities.enums.ReservationTime;
import ristogo.common.net.ResponseMessage;
import ristogo.net.Protocol;
import ristogo.ui.graphics.config.GUIConfig;
import ristogo.ui.graphics.controls.FormButton;
import ristogo.ui.graphics.controls.FormLabel;

public class BookForm extends VBox
{
	private TextField nameField;
	private DatePicker dateField;
	private ChoiceBox<String> hourField;
	private ChoiceBox<Integer> seatsField;
	private FormButton checkButton;
	private FormButton bookButton;
	private FormButton deleteButton;
	
	private Label errorLabel = new Label();

	private Restaurant restaurant;
	private Reservation reservation;

	public BookForm(Runnable onAction)
	{

		super(20);

		Label title = new Label("Book a table");
		Label subTitle = new Label("To select a restaurant click on the table on the side");
		title.setStyle(GUIConfig.getCSSFormTitleStyle());
		title.setFont(GUIConfig.getFormTitleFont());
		title.setTextFill(GUIConfig.getFgColor());
		subTitle.setFont(GUIConfig.getFormSubtitleFont());
		subTitle.setTextFill(GUIConfig.getFgColor());

		FormLabel nameLabel = new FormLabel("Name of Restaurant: ");
		FormLabel dateLabel = new FormLabel("Date of Reservation: ");
		FormLabel hourLabel = new FormLabel("Booking Time: ");
		FormLabel seatsLabel = new FormLabel("Seats: ");
		
		errorLabel.setFont(GUIConfig.getTextFont());
		errorLabel.setTextFill(GUIConfig.getInvertedFgColor());
		errorLabel.setStyle("-fx-background-color: red;");
		errorLabel.setVisible(false);
		
		nameField = new TextField();
		dateField = new DatePicker();
		hourField = new ChoiceBox<String>();
		
		checkButton = new FormButton("Check");
		bookButton = new FormButton("Book");
		deleteButton = new FormButton("Delete");
		
		nameField.setEditable(false);
		dateField.setDayCellFactory(picker -> new DateCell() { // Disable all past dates
			@Override
			public void updateItem(LocalDate date, boolean empty)
			{
				super.updateItem(date, empty);
				LocalDate today = LocalDate.now();

				setDisable(empty || date.compareTo(today) < 0);
			}
		});
		hourField.setMinSize(70, 25);
		hourField.setMaxSize(70, 25);
		seatsField = new ChoiceBox<Integer>();
		seatsField.setDisable(true);
		bookButton.setDisable(true);
		deleteButton.setDisable(true);

		HBox nameBox = new HBox(20);
		HBox dateBox = new HBox(20);
		HBox hourBox = new HBox(20);
		HBox seatsBox = new HBox(20);
		HBox buttonBox = new HBox(20);
		
		nameBox.getChildren().addAll(nameLabel, nameField);
		dateBox.getChildren().addAll(dateLabel, dateField);
		hourBox.getChildren().addAll(hourLabel, hourField);
		seatsBox.getChildren().addAll(seatsLabel, seatsField);
		buttonBox.getChildren().addAll(bookButton, deleteButton);
		
		getChildren().addAll(title, subTitle, nameBox, dateBox, hourBox,
			checkButton, seatsBox, errorLabel, buttonBox);
		setStyle(GUIConfig.getCSSFormBoxStyle());
		
		
		
		checkButton.setOnAction(this::handleCheckButtonAction);
		bookButton.setOnAction((ActionEvent ev) -> {
			try {
				String n = nameField.getText();
				LocalDate d = dateField.getValue();
				ReservationTime h = ReservationTime.valueOf(hourField.getValue().toUpperCase());
				int s = seatsField.getValue();
				Reservation reserv = new Reservation(RistogoGUI.getLoggedUser().getUsername(), n, d, h,
					s);
				Restaurant rest = new Restaurant(idRestoReserve);
				ResponseMessage res = Protocol.getInstance().reserve(reserv, rest);
				if (res.isSuccess()) {
					onAction.run();
				} else {
					errorLabel.setText("Error: " + res.getErrorMsg());
					errorLabel.setVisible(true);
					seatsField.getItems().clear();

				}
				bookButton.setDisable(true);

			} catch (NullPointerException e) {
				e.getMessage();
				errorLabel.setText("Error: fill out the entire form to be able to book");
				errorLabel.setVisible(true);
			}
		});

		deleteButton.setOnAction((ActionEvent ev) -> {
			try {

				ResponseMessage res = Protocol.getInstance()
					.deleteReservation(new Reservation(idRestoDelete));
				if (res.isSuccess()) {
					onAction.run();
					deleteButton.setDisable(true);
				} else {
					errorLabel.setText("Error: " + res.getErrorMsg());
					errorLabel.setVisible(true);

				}
			} catch (NullPointerException e) {
				e.getMessage();
				deleteButton.setDisable(true);
			}
		});

		dateField.setOnAction((ActionEvent ev) -> {
			seatsField.setDisable(true);
			bookButton.setDisable(true);
		});

		hourField.setOnAction((ActionEvent ev) -> {
			seatsField.setDisable(true);
			bookButton.setDisable(true);
		});

	}
	
	private void handleCheckButtonAction(ActionEvent event)
	{
		ResponseMessage resMsg;
		if (restaurant == null)
			resMsg = Protocol.getInstance().checkSeats(reservation);
		else
			resMsg = Protocol.getInstance().checkSeats(reservation, restaurant);
		if (!resMsg.isSuccess()) {
			showError(resMsg.getErrorMsg());
			return;
		}
		int maxSeats = ((Restaurant)resMsg.getEntity()).getSeats();
		seatsField.getItems().clear();
		if (maxSeats > 0) {
			for (int i = 1; i < maxSeats; i++)
				seatsField.getItems().add(i);
			seatsField.setDisable(false);
			bookButton.setDisable(false);
		}
	}
	
	private void showError(String message)
	{
		errorLabel.setText(message);
		errorLabel.setVisible(true);
	}
	
	private void hideError()
	{
		errorLabel.setVisible(false);
	}
	
	private Reservation getReservation()
	{
		if (reservation == null)
			reservation = new Reservation();
		reservation.setRestaurantName(nameField.getText());
		reservation.setDate(dateField.getValue());
		reservation.setTime(ReservationTime.valueOf(hourField.getValue().toUpperCase()));
		Integer seats = seatsField.getValue();
		if (seats != null)
			reservation.setSeats(seats);
		return reservation;
	}
	
	public void fillForm(Restaurant restaurant)
	{
		this.restaurant = restaurant;
		reservation = null;
		nameField.setText(restaurant.getName());
		dateField.setValue(null);
		hourField.getItems().clear();
		switch(restaurant.getOpeningHours()) {
		case BOTH:
			hourField.getItems().addAll(OpeningHours.LUNCH.toString(), OpeningHours.DINNER.toString());
			break;
		default:
			hourField.getItems().add(restaurant.getOpeningHours().toString());
		}
		seatsField.getSelectionModel().clearSelection();
		seatsField.setValue(null);
		bookButton.setText("Book");
		deleteButton.setDisable(true);
		bookButton.setDisable(true);
	}
	
	public void fillForm(Reservation reservation)
	{
		this.reservation = reservation;
		restaurant = null;
		nameField.setText(reservation.getRestaurantName());
		dateField.setValue(reservation.getDate());
		hourField.setValue(reservation.getTime().toString());
		seatsField.setValue(reservation.getSeats());
		bookButton.setText("Save");
		deleteButton.setDisable(false);
		bookButton.setDisable(false);
	}
}