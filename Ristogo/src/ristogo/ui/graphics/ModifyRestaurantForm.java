package ristogo.ui.graphics;

import java.io.IOException;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import ristogo.common.entities.*;
import ristogo.common.entities.enums.*;
import ristogo.common.entities.enums.OpeningHours;
import ristogo.common.entities.enums.Price;
import ristogo.common.net.ResponseMessage;
import ristogo.net.Protocol;
import ristogo.ui.graphics.beans.RestaurantBean;
import ristogo.ui.graphics.config.GUIConfig;

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
		
		Label title = new Label("Modify your Restaurant");
		title.setStyle("-fx-underline: true;");
		title.setFont(GUIConfig.getFormTitleFont());
		title.setTextFill(GUIConfig.getFgColor());

		Label name = new Label("Name: ");
		nameField = new TextField();
		name.setFont(GUIConfig.getBoldTextFont());
		name.setTextFill(GUIConfig.getFgColor());
		
		HBox nameBox = new HBox(20);
		nameBox.getChildren().addAll(name,nameField);
 
		    
		Label type = new Label("Type: ");
		type.setFont(GUIConfig.getBoldTextFont());
		type.setTextFill(GUIConfig.getFgColor());
		typeField = new ChoiceBox<String>();
		typeField.getItems().addAll("Pizza", "Chinese", "Mexican", "Italian", "SteakHouse");

		HBox typeBox = new HBox(20);
		typeBox.getChildren().addAll(type,typeField);

		
		Label cost = new Label("Cost:");
		cost.setFont(GUIConfig.getBoldTextFont());
		cost.setTextFill(GUIConfig.getFgColor());

		costField = new ChoiceBox<Integer>();
		costField.getItems().addAll(1, 2, 3, 4, 5);
		
		HBox costBox = new HBox(20);
		costBox.getChildren().addAll(cost,costField);
		
		Label city = new Label("City: ");
		cityField = new TextField();
		city.setFont(GUIConfig.getBoldTextFont());
		city.setTextFill(GUIConfig.getFgColor());
		
		HBox cityBox = new HBox(20);
		cityBox.getChildren().addAll(city,cityField);
		
		Label address = new Label("Address: ");
		addressField = new TextField();
		address.setFont(GUIConfig.getBoldTextFont());
		address.setTextFill(GUIConfig.getFgColor());
		
		HBox addressBox = new HBox(20);
		addressBox.getChildren().addAll(address,addressField);
		
		Label desc = new Label("Description: ");
		descField = new TextArea();
		desc.setFont(GUIConfig.getBoldTextFont());
		desc.setTextFill(GUIConfig.getFgColor());
		descField.setWrapText(true);
		descField.setMinSize(480, 100);
		descField.setMaxSize(480, 100);
		
		Label seats = new Label("Seats: ");
		seatsField = new TextField();
		seats.setFont(GUIConfig.getBoldTextFont());
		seats.setTextFill(GUIConfig.getFgColor());
		
		HBox seatsBox = new HBox(20);
		seatsBox.getChildren().addAll(seats,seatsField);
		
		Label hour = new Label("Hour:");
		hour.setFont(GUIConfig.getBoldTextFont());
		hour.setTextFill(GUIConfig.getFgColor());

		hourField = new ChoiceBox<String>(FXCollections.observableArrayList("Lunch", "Dinner", "Lunch/Dinner"));
		//hourField.getItems().addAll("Lunch", "Dinner", "Lunch&Dinner");
		
		HBox hourBox = new HBox(20);
		hourBox.getChildren().addAll(hour,hourField);

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
    	commit.setStyle(GUIConfig.getInvertedCSSBgColorButton() );

    	
    	getOwnRestaurant();
    	
    	commit.setOnAction((ActionEvent ev) -> {
			error.setVisible(false);
	    	try {
				String n = nameField.getText();
				Genre g = Genre.valueOf(typeField.getValue().toUpperCase());
				Price p = Price.valueOf(costField.getValue().toString());
				String ct = cityField.getText();
				String add = addressField.getText();
				String d = descField.getText();
				int s = Integer.parseInt(seatsField.getText());
				OpeningHours h;
				if(hourField.getValue().equals("Lunch")) {
					h = OpeningHours.LUNCH;
				}
				else if(hourField.getValue().contentEquals("Dinner")) {
					h= OpeningHours.DINNER;
				}
				else {
					h = OpeningHours.BOTH;
				}
				
				ResponseMessage res = Protocol.getProtocol().editRestaurant(new Restaurant(n, g, p, ct, add, d, s, h));
				if(!res.isSuccess()) {
					error.setText("Error: Commit Failed. Retry");
					error.setVisible(true);
					
				}
				
			}catch(NullPointerException | IOException e) {
				e.getMessage();
				error.setText("Error: fill out the entire form to be able to commit");
				error.setVisible(true);
				getOwnRestaurant();
			}
		});
		
		VBox boxModify = new VBox(20);
		boxModify.getChildren().addAll(title,nameBox, typeBox, costBox, cityBox, addressBox, desc, descField, seatsBox, hourBox, error, commit);
		boxModify.setStyle("-fx-padding: 7;" + 
                "-fx-border-style: solid inside;" + 
                "-fx-border-width: 2;" +
                "-fx-border-insets: 3;" + 
                "-fx-border-radius: 10;" + 
                "-fx-border-color: "+ "D9561D" +";");
		

		this.getChildren().addAll(boxModify);
		this.setPrefSize(400, 600);
        this.setStyle("-fx-padding: 7;" + 
                "-fx-border-width: 2;" +
                "-fx-border-insets: 3;" + 
                "-fx-border-radius: 10;" );
		
	}
	
	public void getOwnRestaurant() {
		try {
       		ResponseMessage res = Protocol.getProtocol().getOwnRestaurant();
       		if(res.isSuccess()) {
    				Restaurant restaurant = (Restaurant)res.getEntity();
    				RestaurantBean r = RestaurantBean.fromEntity(restaurant);
    			if(r.getName() != null) {
    				nameField.setText(r.getName());
    				typeField.getSelectionModel().select(r.getGenre().toString());
    				costField.setValue(r.getPrice().ordinal());
    				cityField.setText(r.getCity());
    				addressField.setText(r.getAddress());
    				descField.setText(r.getDescription());
    				seatsField.setText(Integer.toString(r.getSeats()));
    			}
    			if(r.getOpeningHours().equals(OpeningHours.LUNCH)) {
    				hourField.getSelectionModel().select("Lunch");
    			}
    			else if(r.getOpeningHours().equals(OpeningHours.DINNER)) {
    				hourField.getSelectionModel().select("Dinner");
    			}
    			else {
    				hourField.getSelectionModel().select("Lunch/Dinner");
    			}
       		}
    	}catch(NullPointerException | IOException e) {
    		
    	}	
	}
	
}
