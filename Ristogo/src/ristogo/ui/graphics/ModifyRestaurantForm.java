package ristogo.ui.graphics;

import javafx.collections.*;

import javafx.scene.control.*;

import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.text.*;

import ristogo.config.Configuration;

public class ModifyRestaurantForm extends VBox {
	
	private TextField nameField;
	private ChoiceBox<String> typeField;
	private ChoiceBox<Integer> costField;
	private TextField cityField;
	private TextField addressField;
	private TextArea descField;
	private TextField seatsField;
	private ChoiceBox<String> hourField;
	
	
	public ModifyRestaurantForm () {
		
		super(20);
		
		String font = Configuration.getConfig().getFontName();
		double dimC = Configuration.getConfig().getFontSize();
		String textColor = Configuration.getConfig().getFgColorName();
		String backgroundColor = Configuration.getConfig().getBgColorName();
		
		Label title = new Label("Modify your Restaurant");
		title.setStyle("-fx-underline: true;");
		title.setFont(Font.font(font, FontWeight.BOLD, dimC+3));
		title.setTextFill(Color.web(textColor));

		Label name = new Label("Name: ");
		TextField nameField = new TextField();
		name.setFont(Font.font(font, FontWeight.BOLD, dimC));
		name.setTextFill(Color.web(textColor));
		
		HBox nameBox = new HBox(20);
		nameBox.getChildren().addAll(name,nameField);
 
		    
		Label type = new Label("Type: ");
		type.setFont(Font.font(font, FontWeight.BOLD, dimC));
		type.setTextFill(Color.web(textColor));
		typeField = new ChoiceBox<String>();
		typeField.getItems().addAll("Pizza", "Chinese", "Mexican", "Italian", "SteakHouse");

		HBox typeBox = new HBox(20);
		typeBox.getChildren().addAll(type,typeField);

		
		Label cost = new Label("Cost:");
		cost.setFont(Font.font(font, FontWeight.BOLD, dimC));
		cost.setTextFill(Color.web(textColor));

		costField = new ChoiceBox<Integer>();
		costField.getItems().addAll(1, 2, 3, 4, 5);
		
		HBox costBox = new HBox(20);
		costBox.getChildren().addAll(cost,costField);
		
		Label city = new Label("City: ");
		cityField = new TextField();
		city.setFont(Font.font(font, FontWeight.BOLD, dimC));
		city.setTextFill(Color.web(textColor));
		
		HBox cityBox = new HBox(20);
		cityBox.getChildren().addAll(city,cityField);
		
		Label address = new Label("Address: ");
		addressField = new TextField();
		address.setFont(Font.font(font, FontWeight.BOLD, dimC));
		address.setTextFill(Color.web(textColor));
		
		HBox addressBox = new HBox(20);
		addressBox.getChildren().addAll(address,addressField);
		
		Label desc = new Label("Description: ");
		descField = new TextArea();
		desc.setFont(Font.font(font, FontWeight.BOLD, dimC));
		desc.setTextFill(Color.web(textColor));
		descField.setWrapText(true);
		descField.setMinSize(480, 100);
		descField.setMaxSize(480, 100);
		
		Label seats = new Label("Seats: ");
		seatsField = new TextField();
		seats.setFont(Font.font(font, FontWeight.BOLD, dimC));
		seats.setTextFill(Color.web(textColor));
		
		HBox seatsBox = new HBox(20);
		seatsBox.getChildren().addAll(seats,seatsField);
		
		Label hour = new Label("Hour:");
		hour.setFont(Font.font(font, FontWeight.BOLD, dimC));
		hour.setTextFill(Color.web(textColor));

		hourField = new ChoiceBox<String>(FXCollections.observableArrayList("Lunch", "Dinner", "Lunch/Dinner"));
		//hourField.getItems().addAll("Lunch", "Dinner", "Lunch&Dinner");
		
		HBox hourBox = new HBox(20);
		hourBox.getChildren().addAll(hour,hourField);

//////////////////error/////////////////////////////////////////////////////     
		Label error = new Label();
		error.setFont(Font.font(font, FontWeight.NORMAL, dimC+3));
		error.setTextFill(Color.web(backgroundColor));
		error.setStyle("-fx-background-color:   red;");
		error.setVisible(false);
///////////////////////////////////////////////////////////////////////////////
    	
    	Button commit = new Button("Commit");
    	commit.setFont(Font.font(font, FontWeight.BOLD, dimC+2));
    	commit.setTextFill(Color.web(backgroundColor));
    	commit.setStyle("-fx-base: " + textColor );
    	
    	/*FILL FORM WITH RESTAURANT
    	try {
			RestaurantBean r = (RestaurantBean)MessageHandler.sendRequest(MessageHandler.RESTAURANT_INFO);
			if(r.getName() != null) {
				nameField.setText(r.getName());
				typeField.getSelectionModel().select(r.getType());
				costField.setValue(r.getPrice());
				cityField.setText(r.getCity());
				addressField.setText(r.getAddress());
				descField.setText(r.getDescription());
				seatsField.setText(new Integer(r.getSeats()).toString());
			}
			if(r.getOpening().equals("LUNCH")) {
				hourField.getSelectionModel().select("Lunch");
			}
			else if(r.getOpening().equals("DINNER")) {
				hourField.getSelectionModel().select("Dinner");
			}
			else {
				hourField.getSelectionModel().select("Lunch/Dinner");
			}
    	}catch(NullPointerException e) {
    		
    	}
    	*/
		
		VBox boxModify = new VBox(20);
		boxModify.getChildren().addAll(title,nameBox, typeBox, costBox, cityBox, addressBox, desc, descField, seatsBox, hourBox, error, commit);
		boxModify.setStyle("-fx-padding: 7;" + 
                "-fx-border-style: solid inside;" + 
                "-fx-border-width: 2;" +
                "-fx-border-insets: 3;" + 
                "-fx-border-radius: 10;" + 
                "-fx-border-color: "+ textColor +";");
		

		this.getChildren().addAll(boxModify);
		this.setPrefSize(400, 600);
        this.setStyle("-fx-padding: 7;" + 
                "-fx-border-width: 2;" +
                "-fx-border-insets: 3;" + 
                "-fx-border-radius: 10;" );
		
	}
	
	public TextField getNameField() {
		return nameField;
	}


	public void setNameField(TextField nameField) {
		this.nameField = nameField;
	}


	public ChoiceBox<String> getTypeField() {
		return typeField;
	}


	public void setTypeField(ChoiceBox<String> typeField) {
		this.typeField = typeField;
	}


	public ChoiceBox<Integer> getCostField() {
		return costField;
	}


	public void setCostField(ChoiceBox<Integer> costField) {
		this.costField = costField;
	}


	public TextField getCityField() {
		return cityField;
	}


	public void setCityField(TextField cityField) {
		this.cityField = cityField;
	}


	public TextField getAddressField() {
		return addressField;
	}


	public void setAddressField(TextField addressField) {
		this.addressField = addressField;
	}


	public TextArea getDescField() {
		return descField;
	}


	public void setDescField(TextArea descField) {
		this.descField = descField;
	}


	public TextField getSeatsField() {
		return seatsField;
	}


	public void setSeatsField(TextField seatsField) {
		this.seatsField = seatsField;
	}


	public ChoiceBox<String> getHourField() {
		return hourField;
	}


	public void setHourField(ChoiceBox<String> hourField) {
		this.hourField = hourField;
	}



}
