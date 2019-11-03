package ristogo.ui.graphics;

import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class RistogoGUI extends Application
{

	@Override
	public void start(Stage arg0) throws Exception
	{
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Information Dialog");
		alert.setHeaderText("Look, an Information Dialog");
		alert.setContentText("This is a test!");

		alert.showAndWait();
	}
	
	public static void launch(String... args)
	{
		Application.launch(args);
	}

}
