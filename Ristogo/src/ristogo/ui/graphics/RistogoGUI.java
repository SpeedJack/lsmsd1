package ristogo.ui.graphics;

import java.util.Optional;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.util.Pair;

public class RistogoGUI extends Application
{

	@Override
	public void start(Stage arg0) throws Exception
	{
		
		LoginDialog login = new LoginDialog();

		
		Optional<Pair<String, String>> result = login.showAndWait();

		result.ifPresent(usernamePassword -> {
		    System.out.println("Username=" + usernamePassword.getKey() + ", Password=" + usernamePassword.getValue());
		});
	}
	
	public static void launch(String... args)
	{
		Application.launch(args);
	}

}
