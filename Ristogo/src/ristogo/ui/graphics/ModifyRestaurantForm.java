package ristogo.ui.graphics;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
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
    	
    	commit.setOnAction((ActionEvent ev) -> {
			error.setVisible(false);
	    	try {
				String n = nameField.getText();
				String t = typeField.getValue().toString();
				String c = costField.getValue().toString();
				String ct = cityField.getText();
				String add = addressField.getText();
				String d = descField.getText();
				String s = seatsField.getText();
				String h;
				if(hourField.getValue().equals("Lunch")) {
					h = "LUNCH";
				}
				else if(hourField.getValue().contentEquals("Dinner")) {
					h="DINNER";
				}
				else {
					h = "ALWAYS";
				}
				//boolean res = MANDARE COMMIT
				boolean res = true;
				if(!res) {
					error.setText("Error: Commit Failed. Retry");
					error.setVisible(true);
					//FILL FORM WITH RESTAURANT ?
				}
				
			}catch(NullPointerException e) {
				e.getMessage();
				error.setText("Error: fill out the entire form to be able to commit");
				error.setVisible(true);
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
	
}
