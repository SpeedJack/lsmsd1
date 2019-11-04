package ristogo.ui.graphics;

import java.util.*;

import javafx.application.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.text.*;
import javafx.stage.*;
import javafx.util.*;

import ristogo.config.Configuration;

public class LoginDialog extends Dialog<Pair<String, String>>  {
	
	
	public LoginDialog() {
		
		Stage stage = (Stage) getDialogPane().getScene().getWindow();
		stage.getIcons().add(new Image(this.getClass().getResource("/resources/logo.png").toString()));
		
		DialogPane dialogPane = getDialogPane();
		dialogPane.setStyle("-fx-background-color: "+ Configuration.getConfig().getTextColor()+" ;");
		
		setTitle("RistoGo - Login");
		setHeaderText("Welcome to Ristogo!\n "
				+ "The application that allows you to book tables\n "
				+ "at your favorite restaurants!");
		
		dialogPane.getStyleClass().remove("alert");

	    GridPane header = (GridPane)dialogPane.lookup(".header-panel"); 
	    header.setStyle("-fx-background-color: "+Configuration.getConfig().getTextColor()+" ; "
	            + "-fx-font-family: "+Configuration.getConfig().getFont()+" ;"
	            + "-fx-wrap-text: true ;"
	            + "-fx-text-fill: "+Configuration.getConfig().getBackgroundColor()+" ;");
	    
	    header.lookup(".label").setStyle("-fx-text-fill: "+Configuration.getConfig().getBackgroundColor()+" ;");
		
		ImageView img = new ImageView(this.getClass().getResource("/resources/whiteLogo.png").toString());
		img.setFitHeight(50);
		img.setFitWidth(50);
		setGraphic(img);
		
		ButtonType loginButtonType = new ButtonType("Login", ButtonData.OK_DONE);
		ButtonType registerButtonType = new ButtonType("Register", ButtonData.OK_DONE);
		getDialogPane().getButtonTypes().addAll(loginButtonType, registerButtonType, ButtonType.CANCEL);
		
		ButtonBar buttonBar = (ButtonBar)this.getDialogPane().lookup(".button-bar");
	    buttonBar.getButtons().forEach(b->b.setStyle("-fx-font-family: "+Configuration.getConfig().getFont()+";"
	    										+"-fx-background-color: "+Configuration.getConfig().getBackgroundColor() +" ;"
	    										+"-fx-text-fill: "+Configuration.getConfig().getTextColor() +" ;"
	    										+"-fx-font-size: "+(Configuration.getConfig().getDimCharacter() +2)+"px ;"));
	  
		
		Label l1 = new Label("Name: ");
		TextField username = new TextField();
		username.setPromptText("Username");
		Label l2 = new Label("Password: ");
		PasswordField password = new PasswordField();
		password.setPromptText("Password");
		
		l1.setFont(Font.font(Configuration.getConfig().getFont(), FontWeight.NORMAL, Configuration.getConfig().getDimCharacter()+3));
		l1.setTextFill(Color.web(Configuration.getConfig().getBackgroundColor()));
		username.setFont(Font.font(Configuration.getConfig().getFont(), Configuration.getConfig().getDimCharacter()));
		username.setMaxWidth(200);
		
		l2.setFont(Font.font(Configuration.getConfig().getFont(), FontWeight.NORMAL, Configuration.getConfig().getDimCharacter()+3));
		l2.setTextFill(Color.web(Configuration.getConfig().getBackgroundColor()));
		password.setFont(Font.font(Configuration.getConfig().getFont(), Configuration.getConfig().getDimCharacter()));
		password.setMaxWidth(200);
		
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		grid.add(l1, 0, 0);
		grid.add(username, 1, 0);
		grid.add(l2, 0, 1);
		grid.add(password, 1, 1);
		
		getDialogPane().setContent(grid);
		
		Node loginButton = getDialogPane().lookupButton(loginButtonType);
		loginButton.setDisable(true);

		username.textProperty().addListener((observable, oldValue, newValue) -> {
		    	loginButton.setDisable(newValue.trim().isEmpty());
		});

		Platform.runLater(() -> username.requestFocus());

		setResultConverter(dialogButton -> {
		    if (dialogButton == loginButtonType) {
		        return new Pair<>(username.getText(), password.getText());
		    }
		    else if(dialogButton == registerButtonType) {
		    	return new Pair<>("register","");
		    }
		    return null;
		});
		
	}
}
