package ristogo.ui.graphics;

import javafx.application.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.*;

import ristogo.common.entities.User;
import ristogo.config.Configuration;
import ristogo.ui.graphics.config.GUIConfig;

public class LoginDialog extends Dialog<User>{
	public LoginDialog()
	{
		
//////////////////////HEADER/////////////////////////////////////////////////////////////////////////////
	
		Configuration config = Configuration.getConfig();
		DialogPane dialogPane = getDialogPane();
		Stage stage = (Stage)dialogPane.getScene().getWindow();
		stage.getIcons().add(new Image(this.getClass().getResource("/resources/logo.png").toString()));
		
		dialogPane.setStyle(GUIConfig.getInvertedCSSBgColor());
		
		setTitle("RistoGo - Login");
		setHeaderText("Welcome to Ristogo!\n" +
			"The application that allows you to book tables\n" +
			"at your favorite restaurants!");
		
		dialogPane.getStyleClass().remove("alert");

		GridPane header = (GridPane)dialogPane.lookup(".header-panel");
		header.setStyle(GUIConfig.getInvertedCSSBgColor() + 
						GUIConfig.getCSSFontFamily() +
						GUIConfig.getInvertedCSSFgColor() +
						"-fx-wrap-text: true ;");
		
		header.lookup(".label").setStyle("-fx-text-fill: " + config.getBgColorName() + ";");
		
		ImageView img = new ImageView(this.getClass().getResource("/resources/whiteLogo.png").toString());
		img.setFitHeight(50);
		img.setFitWidth(50);
		setGraphic(img);
/////////////////////////////////////////////////////////////////////////////////////////////////////////7		
////////////////////////////CONTENUTO//////////////////////////////////////////////////////////////////	
		
		Label l1 = new Label("Name: ");
		TextField username = new TextField();
		username.setPromptText("Username");
		Label l2 = new Label("Password: ");
		PasswordField password = new PasswordField();
		password.setPromptText("Password");
		
		l1.setFont(GUIConfig.getFormTitleFont());
		l1.setTextFill(GUIConfig.getBgColor());
		username.setFont(GUIConfig.getTextFont());
		username.setMaxWidth(200);
		
		l2.setFont(GUIConfig.getFormTitleFont());
		l2.setTextFill(GUIConfig.getBgColor());
		password.setFont(GUIConfig.getTextFont());
		password.setMaxWidth(200);
		
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		grid.add(l1, 0, 0);
		grid.add(username, 1, 0);
		grid.add(l2, 0, 1);
		grid.add(password, 1, 1);
		
		Label error = new Label("Error: Login Failed. Retry");
		error.setFont(GUIConfig.getFormTitleFont());
		error.setTextFill(GUIConfig.getBgColor());
		error.setStyle("-fx-background-color:   red;");
		error.setVisible(false);
		
		getDialogPane().setContent(grid);
		//getDialogPane().setContent(error);
//////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////BUTTONS////////////////////////////////////////////////////////		
		ButtonType loginButtonType = new ButtonType("Login", ButtonData.OK_DONE);
		ButtonType registerButtonType = new ButtonType("Register", ButtonData.OK_DONE);
		dialogPane.getButtonTypes().addAll(loginButtonType, registerButtonType, ButtonType.CANCEL);
		
		ButtonBar buttonBar = (ButtonBar)dialogPane.lookup(".button-bar");
		buttonBar.getButtons().forEach(b -> b.setStyle(GUIConfig.getCSSFontFamily()
														+GUIConfig.getCSSBgColor()
														+GUIConfig.getCSSFgColor()
														+GUIConfig.getCSSFontSizeNormal()));
		Node loginButton = getDialogPane().lookupButton(loginButtonType);
		loginButton.setDisable(true);
		username.textProperty().addListener((observable, oldValue, newValue) -> {
			loginButton.setDisable(newValue.trim().isEmpty());
		});
		Platform.runLater(() -> username.requestFocus());
		
		setResultConverter(dialogButton -> {
		    if (dialogButton == loginButtonType) {
		    	//MANDARE COMANDO LOGIN
		        return new User("ciao", "ciao");
		    }
		    else if(dialogButton == registerButtonType) {
		    	RegisterDialog register = new RegisterDialog();
		    }
		    return null;
		    
		});	
/////////////////////////////////////////////////////////////////////////////////////////////////////////////
	}
}
