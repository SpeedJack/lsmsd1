package ristogo.ui.graphics;

import java.io.IOException;
import java.time.LocalDate;
import java.util.function.Consumer;

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


public class BookForm extends VBox {
	
	private TextField nameField;
	private DatePicker dateField;
	private ChoiceBox<String> hourField;
	private ChoiceBox<Integer> seatsField;
	private Button check;
	private Button book;
	private Button delRes;

	private int idRestoDelete;
	private int idRestoReserve;
	
	public BookForm (Consumer<Boolean> listReservation) {
		
			super(20);
		
			Label title = new Label("Book a table");
			Label subTitle = new Label("To select a restaurant click on the table on the side" );
			title.setStyle("-fx-underline: true;");
			title.setFont(GUIConfig.getFormTitleFont());
			title.setTextFill(GUIConfig.getFgColor());
			subTitle.setFont(GUIConfig.getFormSubtitleFont());
			subTitle.setTextFill(GUIConfig.getFgColor());
	
			
			Label name = new Label("Name of Restaurant: ");
			nameField = new TextField();
			name.setFont(GUIConfig.getBoldTextFont());
			name.setTextFill(GUIConfig.getFgColor());
			nameField.setEditable(false);
			
			HBox nameBox = new HBox(20);
			nameBox.getChildren().addAll(name,nameField);
	 
			    
			Label date = new Label("Date of Reservation: ");
			date.setFont(GUIConfig.getBoldTextFont());
			date.setTextFill(GUIConfig.getFgColor());
			dateField = new DatePicker();
			dateField.setDayCellFactory(picker -> new DateCell() {   //Disable all past dates
		        public void updateItem(LocalDate date, boolean empty) {
		            super.updateItem(date, empty);
		            LocalDate today = LocalDate.now();
	
		            setDisable(empty || date.compareTo(today) < 0 );
		        }
		    });
			
			HBox dateBox = new HBox(20);
			dateBox.getChildren().addAll(date,dateField);
	
			 
			
			Label hour = new Label("Booking Time: ");
			hour.setFont(GUIConfig.getBoldTextFont());
			hour.setTextFill(GUIConfig.getFgColor());
			hourField = new ChoiceBox<String>();
			hourField.setMinSize(70, 25);
			hourField.setMaxSize(70, 25);
			
			
			HBox hourBox = new HBox(20);
			hourBox.getChildren().addAll(hour,hourField);
	
	    	
	    	check = new Button("Check");
	    	check.setFont(GUIConfig.getButtonFont());
	    	check.setTextFill(GUIConfig.getInvertedFgColor());
	    	check.setStyle(GUIConfig.getInvertedCSSBgColorButton() );
			
			
			Label seats = new Label("Seats: ");
			seats.setFont(GUIConfig.getBoldTextFont());
			seats.setTextFill(GUIConfig.getFgColor());
			seatsField = new ChoiceBox<Integer>();
			seatsField.setDisable(true);
			
			HBox seatsBox = new HBox(20);
			seatsBox.getChildren().addAll(seats,seatsField);
			
			//////////////////error/////////////////////////////////////////////////////     
			Label error = new Label();
			error.setFont(GUIConfig.getTextFont());
			error.setTextFill(GUIConfig.getInvertedFgColor());
			error.setStyle("-fx-background-color:   red;");
			error.setVisible(false);
			///////////////////////////////////////////////////////////////////////////////
	    
		
			book = new Button("Book");
			book.setFont(GUIConfig.getButtonFont());
			book.setTextFill(GUIConfig.getInvertedFgColor());
			book.setStyle(GUIConfig.getInvertedCSSBgColorButton() );
			book.setDisable(true);
			
			delRes = new Button("Del. Res.");
			delRes.setFont(GUIConfig.getButtonFont());
			delRes.setTextFill(GUIConfig.getInvertedFgColor());
			delRes.setStyle(GUIConfig.getInvertedCSSBgColorButton() );
			delRes.setDisable(true);
			
			HBox buttonBox = new HBox(20);
			buttonBox.getChildren().addAll(book,delRes);
			
	    	check.setOnAction((ActionEvent ev) -> {
													try {
														String n = nameField.getText();
														LocalDate d = dateField.getValue();
														ReservationTime h = ReservationTime.valueOf(hourField.getValue().toUpperCase());
														Reservation reserv = new Reservation(RistogoGUI.getLoggedUser().getUsername(), n, d, h, 0);
														Restaurant rest = new Restaurant(idRestoReserve);
														ResponseMessage res = Protocol.getProtocol().checkSeats(reserv,rest);
														if(res.isSuccess()) {
															Restaurant r = (Restaurant)res.getEntity();
															if(r.getSeats()>0) {
																for(int i=1; i<=r.getSeats(); i++) {
																	seatsField.getItems().add(i);
																	seatsField.setDisable(false);
																	book.setDisable(false);
																}
															}
															else {
																error.setText("Error: No more seats for this date/hour");
																error.setVisible(true);
															}
														}
														else {
															System.out.println(res.getErrorMsg());
														}

													}catch(NullPointerException | IOException e) {
														e.getMessage();
													}
						    					});
			book.setOnAction((ActionEvent ev) -> {
													try {
														String n = nameField.getText();
														LocalDate d = dateField.getValue();
														ReservationTime h = ReservationTime.valueOf(hourField.getValue().toUpperCase());
														int s = seatsField.getValue();
														Reservation reserv = new Reservation(RistogoGUI.getLoggedUser().getUsername(), n, d, h, s);
														Restaurant rest = new Restaurant(idRestoReserve);
														ResponseMessage res = Protocol.getProtocol().reserve(reserv, rest );
														if(res.isSuccess()) {
															listReservation.accept(true);
														}
														else {
															error.setText("Error: " + res.getErrorMsg());
															error.setVisible(true);
															seatsField.getItems().clear();
															
														}
														book.setDisable(true);
														
													}catch(NullPointerException | IOException e) {
														e.getMessage();
														error.setText("Error: fill out the entire form to be able to book");
														error.setVisible(true);
													}
												});

			delRes.setOnAction((ActionEvent ev) -> {
												try {

													ResponseMessage res = Protocol.getProtocol().deleteReservation(new Reservation(idRestoDelete));
													if(res.isSuccess()) {
														listReservation.accept(true);
														delRes.setDisable(true);
													}
													else {
														error.setText("Error: " + res.getErrorMsg());
														error.setVisible(true);
														
													}
												}catch(NullPointerException | IOException e) {
													e.getMessage();
													delRes.setDisable(true);
												}
											});
			
			dateField.setOnAction((ActionEvent ev) -> { 
														seatsField.setDisable(true);
														book.setDisable(true);
													});
			
			hourField.setOnAction((ActionEvent ev) -> { 
														seatsField.setDisable(true);
														book.setDisable(true);
													});
			
			this.getChildren().addAll(title,subTitle,nameBox, dateBox, hourBox, check, seatsBox, error, buttonBox);
	        this.setStyle("-fx-padding: 7;" + 
	                "-fx-border-style: solid inside;" + 
	                "-fx-border-width: 2;" +
	                "-fx-border-insets: 3;" + 
	                "-fx-border-radius: 10;" + 
	                "-fx-border-color: "+ "D9561D" +";");
					
	}
	
	
	public void fillOutForm(String name, OpeningHours hour) {
    	try {
    		delRes.setDisable(true);
			nameField.setText(name);
			hourField.getItems().clear();
			if(hour.equals(OpeningHours.BOTH)) {
				hourField.getItems().addAll("Lunch", "Dinner");
			}
			else if(hour.equals(OpeningHours.LUNCH)) {
				hourField.getItems().add("Lunch");
			}
			else {
				hourField.getItems().add("Dinner");
			}
    	}catch(NullPointerException npe) {
    		//do nothing
    	}
	}
	
	
	public Button getDelRes() {
		return delRes;
	}
	
	public void setIdResToDelete(int id) {
		this.idRestoDelete = id;
	}
	
	public void setIdResToReserve(int id) {
		this.idRestoReserve = id;
	}

}
