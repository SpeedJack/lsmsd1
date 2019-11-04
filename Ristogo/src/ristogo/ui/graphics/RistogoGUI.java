package ristogo.ui.graphics;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import ristogo.config.Configuration;



public class RistogoGUI extends Application
{

	private HBox applicationInterface;
	
	@Override
	public void start(Stage stage) throws Exception
	{
		applicationInterface = new HBox(10);
		
	/*	LoginDialog login = new LoginDialog();
		RegisterDialog register = new RegisterDialog();
		
		Optional<Pair<String, String>> result = login.showAndWait();
		
		result.ifPresent(loginReturn -> {
		    if(loginReturn.getKey().equals("register")) {
		    	
		    	register.show();
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
		GridPane title = generateTitle();
	}
	
	private void buildRestaurantOwnerInterface() {
		GridPane title = generateTitle();
	}
	
	private GridPane generateTitle() {
		
		String font = Configuration.getConfig().getFont();
		double dimC = Configuration.getConfig().getDimCharacter();
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
