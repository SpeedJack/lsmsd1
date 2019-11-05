package ristogo.ui.graphics;

import java.util.*;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.*;
import javafx.util.*;

import ristogo.config.Configuration;



public class RistogoGUI extends Application
{

	private HBox applicationInterface;
	
	@Override
	public void start(Stage stage) throws Exception
	{
		applicationInterface = new HBox(10);
		
		LoginDialog login = new LoginDialog();
		RegisterDialog register = new RegisterDialog();
		
		Optional<Pair<String, String>> resultLogin = login.showAndWait();
		Optional<Pair<String, String>> resultRegister;
		
		resultLogin.ifPresent(loginReturn -> {
		    if(loginReturn.getKey().equals("register")) {
		    	
		    	resultRegister = register.showAndWait();
		    }
		    else {
		    	//effettuaLogin
		    	//controlla se è un ristoratore o un cliente
		    	//se è un cliente
		    	buildCostumerInterface();
		    	//se è un ristoratore
		    	buildRestaurantOwnerInterface();
		    	
		    }
		});
		
	  Scene scene = new Scene(new Group(applicationInterface));
	  scene.setFill(Color.web(Configuration.getConfig().getTextColor()));
	  
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
		
		String font = Configuration.getConfig().getFont();
		int dimC = Configuration.getConfig().getDimCharacter();
		String textColor = Configuration.getConfig().getTextColor();
		
		
		GridPane title = generateTitle();
		BookForm bookForm = new BookForm();
	
		Label subTitle = new Label("List of Restaurant");
		subTitle.setFont(Font.font(font, FontWeight.BOLD, dimC+2));
		subTitle.setTextFill(Color.web(textColor));
		subTitle.setStyle("-fx-underline: true;");
		
		TableViewRestaurant restaurant = new TableViewRestaurant();
		restaurant.listRestaurants();
		
	    Label description = new Label("Description: ");
		TextArea descriptionField = new TextArea();
		description.setFont(Font.font(font, FontWeight.BOLD, dimC-2));
		description.setTextFill(Color.web(textColor));
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
		subTitle2.setFont(Font.font(font, FontWeight.BOLD, dimC+2));
		subTitle2.setTextFill(Color.web(textColor));
		subTitle2.setStyle("-fx-underline: true;");
		
	    TableViewReservation reservation = new TableViewReservation(true);
	    reservation.listReservations(true);
	    
	    reservation.setOnMouseClicked((e) -> {
	    							bookForm.getDelRes().setDisable(false);
	    							});
	   

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

	    
		VBox leftPart = new VBox(10);
		leftPart.getChildren().addAll(title,bookForm);
		VBox rightPart = new VBox(10);
		rightPart.getChildren().addAll(subTitle,restaurant, descriptionBox, subTitle2, reservation);
		
		applicationInterface.getChildren().addAll(leftPart, rightPart);
	}
	
	private void buildRestaurantOwnerInterface() {
		
		String font = Configuration.getConfig().getFont();
		int dimC = Configuration.getConfig().getDimCharacter();
		String textColor = Configuration.getConfig().getTextColor();
		String backgroundColor = Configuration.getConfig().getBackgroundColor();
		
		GridPane title = generateTitle();
		ModifyRestaurantForm modifyForm = new ModifyRestaurantForm();
		
		Label subTitle = new Label("List of Reservations at your restaurant");
		subTitle.setFont(Font.font(font, FontWeight.BOLD, dimC+1));
		subTitle.setTextFill(Color.web(textColor));
		subTitle.setStyle("-fx-underline: true;");
		
		
	    TableViewReservation reservation = new TableViewReservation(false);
	    reservation.listReservations(false);
	    
	   	Button refresh = new Button("Refresh");
	   	refresh.setFont(Font.font(font, FontWeight.BOLD, dimC+2));
	   	refresh.setTextFill(Color.web(backgroundColor));
	   	refresh.setStyle("-fx-base: " + textColor );
	   
	   	refresh.setOnAction((ActionEvent ev) -> {
	   											reservation.listReservations(false);
	   											});
	   	
		VBox leftPart = new VBox(10);
		leftPart.getChildren().addAll(title,modifyForm);
		VBox rightPart = new VBox(10);
		rightPart.getChildren().addAll(subTitle, reservation, refresh);
		
		applicationInterface.getChildren().addAll(leftPart, rightPart);
		
	}
	
	private GridPane generateTitle() {
		
		String font = Configuration.getConfig().getFont();
		int dimC = Configuration.getConfig().getDimCharacter();
		String textColor = Configuration.getConfig().getTextColor();
		
		Label title = new Label("RistoGo");
		title.setFont(Font.font(font, FontWeight.BOLD, dimC+7));
		title.setTextFill(Color.web(textColor)); 
		
		ImageView icon = new ImageView("resources/logo.png");
		icon.setFitHeight(30);
		icon.setFitWidth(30);
		
		Label title2 = new Label("Welcome ");
		title2.setFont(Font.font(font, FontWeight.NORMAL, dimC+4));
		title2.setTextFill(Color.web(textColor));
		
		Label nameUser = new Label("AAAAAAAAAAAAAAAAAAAAAAAA");
		nameUser.setFont(Font.font(font, FontWeight.BOLD, dimC+4));
		nameUser.setTextFill(Color.web(textColor));
		
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));
		
		grid.add(title, 0, 0);
		grid.add(icon, 1, 0);
		grid.add(title2, 0, 1);
		grid.add(nameUser, 1, 1);
		
		return grid;
	}

}
