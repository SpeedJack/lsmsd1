package ristogo.ui.graphics;

import java.util.Optional;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Pair;
import ristogo.ui.graphics.config.GUIConfig;



public class RistogoGUI extends Application
{

	private HBox applicationInterface;

	@Override
	public void start(Stage stage) throws Exception
	{
		applicationInterface = new HBox(10);
		
		LoginDialog login = new LoginDialog();
		RegisterDialog register = new RegisterDialog();
		
	/*	Optional<Pair<String, String>> resultLogin = login.showAndWait();
		Optional<Pair<String, String>> resultRegister;
		
		resultLogin.ifPresent(loginReturn -> {
		    if(loginReturn.getKey().equals("register")) {
		    	
		    	//resultRegister = register.showAndWait();
		    }
		    else {
		    	//effettuaLogin
		    	//controlla se � un ristoratore o un cliente
		    	//se � un cliente
		    	buildCostumerInterface();
		    	//se � un ristoratore
		    	buildRestaurantOwnerInterface();
		    	
		    }
		});
		*/
		
		//buildCostumerInterface();
		buildRestaurantOwnerInterface();
		
	  Scene scene = new Scene(new Group(applicationInterface));
	  scene.setFill(GUIConfig.getBgColor());
	  
	  stage.setOnCloseRequest((WindowEvent ev) -> {
									//logout
								});
	  stage.setTitle("RistoGo");
	  stage.setResizable(false);
	  stage.setScene(scene);
	  stage.getIcons().add(new Image("/resources/logo.png"));
	  stage.show(); 
 
	}
	
	public static void launch(String... args)
	{
		Application.launch(args);
	}
	
	
	private void buildCostumerInterface() {
		
		
		
		GridPane title = generateTitle();
		BookForm bookForm = new BookForm();
	
		Label subTitle = new Label("List of Restaurant");
		subTitle.setFont(GUIConfig.getFormTitleFont());
		subTitle.setTextFill(GUIConfig.getFgColor());
		subTitle.setStyle("-fx-underline: true;");
		
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
		
	    restaurant.setOnMouseClicked((e) -> {
	    	try {
	    		bookForm.getDelRes().setDisable(true);
				bookForm.getNameField().setText(restaurant.getSelectionModel().getSelectedItem().getName());
				descriptionField.setText(restaurant.getSelectionModel().getSelectedItem().getDescription());
				bookForm.getHourField().getItems().clear();
				if(restaurant.getSelectionModel().getSelectedItem().getOpening() == "ALWAYS") {
					bookForm.getHourField().getItems().addAll("Lunch", "Dinner");
				}
				else if(restaurant.getSelectionModel().getSelectedItem().getOpening() == "LUNCH") {
					bookForm.getHourField().getItems().add("Lunch");
				}
				else {
					bookForm.getHourField().getItems().add("Dinner");
				}
	    	}catch(NullPointerException npe) {
	    		//do nothing
	    	}	
		});
	  
		HBox descriptionBox = new HBox(20);
		descriptionBox.getChildren().addAll(description,descriptionField);
		
		Label subTitle2 = new Label("My Reservation");
		subTitle2.setFont(GUIConfig.getFormTitleFont());
		subTitle2.setTextFill(GUIConfig.getFgColor());
		subTitle2.setStyle("-fx-underline: true;");
		
	    TableViewReservation reservation = new TableViewReservation(true);
	    reservation.listReservations(true);
	    
	    reservation.setOnMouseClicked((e) -> {
	    							bookForm.getDelRes().setDisable(false);
	    							});
	   
/*
	    bookForm.getBook().setOnAction((ActionEvent ev) -> {
			try {
				String n = bookForm.getNameField().getText();
				String d = bookForm.getDateField().getValue().toString();
				String h = bookForm.getHourField().getValue();
				int s = bookForm.getSeatsField().getValue();
				//boolean res = MANDARE RICHIESTA PRENOTAZIONE
				boolean res = true;
				if(res) {
					reservation.listReservations(true);
				}
				else {
					error.setText("Error: bookForming Failed. Retry");
					error.setVisible(true);
					seats_f.getItems().clear();
					
				}
				bookForm.getBook().setDisable(true);
				
			}catch(NullPointerException e) {
				e.getMessage();
				error.setText("Error: fill out the entire form to be able to bookForm");
				error.setVisible(true);
			}
		});

	bookForm.getDelRes().setOnAction((ActionEvent ev) -> {
			try {
				int idRes = reservation.getSelectionModel().getSelectedItem().getReservation();
				//boolean res = MANDARE CANCELLA PRENOTAZIONE
				boolean res = true;
				if(res) {
					reservation.listReservations(true);
					bookForm.getDelRes().setDisable(true);
				}
				else {
					error.setText("Error: Delection Failed. Retry");
					error.setVisible(true);
					
				}
			}catch(NullPointerException e) {
				e.getMessage();
				bookForm.getDelRes().setDisable(true);
			}
		});
*/
	    
		VBox leftPart = new VBox(10);
		leftPart.getChildren().addAll(title,bookForm);
		leftPart.setPrefSize(400, 600);
		leftPart.setStyle("-fx-padding: 7;" + 
                "-fx-border-width: 2;" +
                "-fx-border-insets: 3;" + 
                "-fx-border-radius: 10;" );
		VBox rightPart = new VBox(10);
		rightPart.getChildren().addAll(subTitle,restaurant, descriptionBox, subTitle2, reservation);
		rightPart.setPrefSize(600, 600);
		rightPart.setStyle("-fx-padding: 7;" + 
	                "-fx-border-width: 2;" +
	                "-fx-border-insets: 3;" + 
	                "-fx-border-radius: 10;" );
		
		applicationInterface.getChildren().addAll(leftPart, rightPart);
		applicationInterface.setPrefSize(1000, 600);
	}
	
	private void buildRestaurantOwnerInterface() {
		
		
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
	   	refresh.setStyle(GUIConfig.getInvertedCSSBgColor() );
	   
	   	refresh.setOnAction((ActionEvent ev) -> {
	   											reservation.listReservations(false);
	   											});
	   	
		VBox leftPart = new VBox(10);
		leftPart.getChildren().addAll(title,modifyForm);
		leftPart.setPrefSize(400, 600);
		leftPart.setStyle("-fx-padding: 7;" + 
                "-fx-border-width: 2;" +
                "-fx-border-insets: 3;" + 
                "-fx-border-radius: 10;" );
		VBox rightPart = new VBox(10);
		rightPart.getChildren().addAll(subTitle, reservation, refresh);
		rightPart.setAlignment(Pos.CENTER);
		rightPart.setPrefSize(600, 600);
		rightPart.setStyle("-fx-padding: 7;" + 
	                "-fx-border-width: 2;" +
	                "-fx-border-insets: 3;" + 
	                "-fx-border-radius: 10;" );
		
		applicationInterface.getChildren().addAll(leftPart, rightPart);
		applicationInterface.setPrefSize(1000, 600);
		
	}
	
	private GridPane generateTitle() {
		
		
		Label title = new Label("RistoGo");
		title.setFont(GUIConfig.getTitleFont());
		title.setTextFill(GUIConfig.getFgColor()); 
		
		ImageView icon = new ImageView("resources/logo.png");
		icon.setFitHeight(30);
		icon.setFitWidth(30);
		
		Label title2 = new Label("Welcome ");
		title2.setFont(GUIConfig.getSubtitleFont());
		title2.setTextFill(GUIConfig.getFgColor());
		
		Label nameUser = new Label("AAAAAAAAAAAAAAAAAAAAAAAA");
		nameUser.setFont(GUIConfig.getBoldSubtitleFont());
		nameUser.setTextFill(GUIConfig.getFgColor());
		
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(30);
		grid.setPadding(new Insets(1, 1, 40, 1));
		
		grid.add(title, 0, 0);
		grid.add(icon, 1, 0);
		grid.add(title2, 0, 1);
		grid.add(nameUser, 1, 1);
		
		return grid;
	}

}
