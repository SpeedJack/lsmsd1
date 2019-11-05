package ristogo.ui.graphics;

import java.util.Optional;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
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
		
		Optional<Pair<String, String>> result = login.showAndWait();
		
		result.ifPresent(loginReturn -> {
			if (loginReturn.getKey().equals("register")) {
				register.show();
			} else {
				// TODO: login
				// If do not has restaurants:
				//buildCostumerInterface();
				// else:
				//buildRestaurantOwnerInterface();
			}
		});
		
	Scene scene = new Scene(new Group(applicationInterface));
	scene.setFill(GUIConfig.getFgColor());
	
	stage.setOnCloseRequest((WindowEvent ev) -> {
		// TODO: logout
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
	
	
	private void buildCostumerInterface()
	{
		GridPane title = generateTitle();
	}
	
	private void buildRestaurantOwnerInterface()
	{
		GridPane title = generateTitle();
	}
	
	private GridPane generateTitle()
	{
		Label title = new Label("RistoGo");
		title.setFont(GUIConfig.getTitleFont());
		title.setTextFill(GUIConfig.getFgColor()); 
		
		ImageView icon = new ImageView("resources/logo.png");
		icon.setFitHeight(30);
		icon.setFitWidth(30);
		
		Label title2 = new Label("Welcome");
		title2.setFont(GUIConfig.getSubtitleFont());
		title2.setTextFill(GUIConfig.getFgColor());
		
		Label nameUser = new Label("AAAAAAAAAAAAAAAAAAAAAAAA");
		nameUser.setFont(GUIConfig.getImportantFont());
		nameUser.setTextFill(GUIConfig.getFgColor());
		
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
