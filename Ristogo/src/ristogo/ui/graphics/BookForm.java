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
import ristogo.ui.graphics.config.GUIConfig;


public class BookForm extends VBox {
	
	private TextField nameField;
	private DatePicker dateField;
	private ChoiceBox<String> hourField;
	private ChoiceBox<Integer> seatsField;
	private Button check;
	private Button book;
	private Button delRes;
	
	public BookForm () {
		
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
	    	check.setStyle(GUIConfig.getInvertedCSSBgColor() );
			
			
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
			book.setStyle(GUIConfig.getInvertedCSSBgColor() );
			book.setDisable(true);
			
			delRes = new Button("Del. Res.");
			delRes.setFont(GUIConfig.getButtonFont());
			delRes.setTextFill(GUIConfig.getInvertedFgColor());
			delRes.setStyle(GUIConfig.getInvertedCSSBgColor() );
			delRes.setDisable(true);
			
			HBox buttonBox = new HBox(20);
			buttonBox.getChildren().addAll(book,delRes);
			
	    	check.setOnAction((ActionEvent ev) -> {
													try {
														String n = nameField.getText();
														String d = dateField.getValue().toString();
														String h = hourField.getValue();
														//int s = MANDARE RICHIESTA CHECK
														int s = 10;
														if(s>0) {
															for(int i=1; i<=s; i++) {
																seatsField.getItems().add(i);
																seatsField.setDisable(false);
																book.setDisable(false);
															}
														}
													}catch(NullPointerException e) {
														e.getMessage();
													}
						    					});
			book.setOnAction((ActionEvent ev) -> {
													try {
														String n = nameField.getText();
														String d = dateField.getValue().toString();
														String h = hourField.getValue();
														int s = seatsField.getValue();
														//boolean res = MANDARE RICHIESTA RESERVE
														boolean res = true;
														if(res) {
															//CONSUMER LISTA PRENOTAZIONI
														}
														else {
															error.setText("Error: Booking Failed. Retry");
															error.setVisible(true);
															seatsField.getItems().clear();
															
														}
														book.setDisable(true);
														
													}catch(NullPointerException e) {
														e.getMessage();
														error.setText("Error: fill out the entire form to be able to book");
														error.setVisible(true);
													}
												});

			delRes.setOnAction((ActionEvent ev) -> {
												try {
													//int idRes = CONSUMER ID PRENOTAZIONE
													//boolean res = MANDARE RICHIESTA DELETE_RESERVE
													boolean res = true;
													if(res) {
														//CONSUMER LISTA PRENOTAZIONI
														delRes.setDisable(true);
													}
													else {
														error.setText("Error: Delection Failed. Retry");
														error.setVisible(true);
														
													}
												}catch(NullPointerException e) {
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
	
	
	public void fillOutForm(String name, String hour) {
    	try {
    		delRes.setDisable(true);
			nameField.setText(name);
			hourField.getItems().clear();
			if(hour.equals("BOTH")) {
				hourField.getItems().addAll("Lunch", "Dinner");
			}
			else if(hour.equals("LUNCH")) {
				hourField.getItems().add("Lunch");
			}
			else {
				hourField.getItems().add("Dinner");
			}
    	}catch(NullPointerException npe) {
    		//do nothing
    	}
	}
	
	public TextField getNameField() {
		return nameField;
	}

	public void setNameField(TextField nameField) {
		this.nameField = nameField;
	}

	public DatePicker getDateField() {
		return dateField;
	}

	public void setDateField(DatePicker dateField) {
		this.dateField = dateField;
	}

	public ChoiceBox<String> getHourField() {
		return hourField;
	}

	public void setHourField(ChoiceBox<String> hourField) {
		this.hourField = hourField;
	}

	public ChoiceBox<Integer> getSeatsField() {
		return seatsField;
	}

	public void setSeatsField(ChoiceBox<Integer> seatsField) {
		this.seatsField = seatsField;
	}

	public Button getCheck() {
		return check;
	}

	public void setCheck(Button check) {
		this.check = check;
	}

	public Button getBook() {
		return book;
	}

	public void setBook(Button book) {
		this.book = book;
	}

	public Button getDelRes() {
		return delRes;
	}

	public void setDelRes(Button delRes) {
		this.delRes = delRes;
	}

}
