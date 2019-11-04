package ristogo.ui.graphics;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

public class LoginDialog extends Dialog<Pair<String, String>>  {
	
	public LoginDialog() {
		
		setTitle("RistoGo - Login");
		setHeaderText("Welcome to Ristogo!\n "
				+ "The application that allows you to book tables\n "
				+ "at your favorite restaurants!");

		ImageView img = new ImageView(this.getClass().getResource("/resources/whiteLogo.png").toString());
		img.setFitHeight(50);
		img.setFitWidth(50);
		setGraphic(img);
		
		ButtonType loginButtonType = new ButtonType("Login", ButtonData.OK_DONE);
		ButtonType registerButtonType = new ButtonType("Register", ButtonData.OK_DONE);
		getDialogPane().getButtonTypes().addAll(loginButtonType, registerButtonType, ButtonType.CANCEL);
		
		/*loginButtonType.setFont(Font.font(Configuration.getConfig().getFont(), FontWeight.BOLD, Configuration.getConfig().getDimCharacter()+3));
		loginButtonType.setTextFill(Color.web(Configuration.getConfig().getTextColor()));
		loginButtonType.setStyle("-fx-base: " + Configuration.getConfig().getBackgroundColor() ); 
		
		registerButtonType.setFont(Font.font(Configuration.getConfig().getFont(), FontWeight.BOLD, Configuration.getConfig().getDimCharacter()+3));
		registerButtonType.setTextFill(Color.web(Configuration.getConfig().getTextColor()));
		registerButtonType.setStyle("-fx-base: " + Configuration.getConfig().getBackgroundColor());
		 */
		Label l1 = new Label("Name: ");
		TextField username = new TextField();
		username.setPromptText("Username");
		Label l2 = new Label("Password: ");
		PasswordField password = new PasswordField();
		password.setPromptText("Password");
		
	/*	l1.setFont(Font.font(Configuration.getConfig().getFont(), FontWeight.NORMAL, Configuration.getConfig().getDimCharacter()+3));
		l1.setTextFill(Color.web(Configuration.getConfig().getBackgroundColor()));
		username.setFont(Font.font(Configuration.getConfig().getFont(), Configuration.getConfig().getDimCharacter()));
		username.setMaxWidth(200);
		
		l2.setFont(Font.font(Configuration.getConfig().getFont(), FontWeight.NORMAL, Configuration.getConfig().getDimCharacter()+3));
		l2.setTextFill(Color.web(Configuration.getConfig().getBackgroundColor()));
		password.setFont(Font.font(Configuration.getConfig().getFont(), Configuration.getConfig().getDimCharacter()));
		password.setMaxWidth(200);
	*/	
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		grid.add(l1, 0, 0);
		grid.add(username, 1, 0);
		grid.add(l2, 0, 1);
		grid.add(password, 1, 1);

		
		Node loginButton = getDialogPane().lookupButton(loginButtonType);
		loginButton.setDisable(true);

		username.textProperty().addListener((observable, oldValue, newValue) -> {
		    loginButton.setDisable(newValue.trim().isEmpty());
		});

		getDialogPane().setContent(grid);

		Platform.runLater(() -> username.requestFocus());

		setResultConverter(dialogButton -> {
		    if (dialogButton == loginButtonType) {
		        return new Pair<>(username.getText(), password.getText());
		    }
		    return null;
		});
		
		DialogPane dialogPane = getDialogPane();
		dialogPane.getStylesheets().add(getClass().getResource("/resources/MyDialog.css").toExternalForm());
		dialogPane.getStyleClass().add("myDialog");
		//dialogPane.setStyle(".dialog-pane { -fx-background-color: "+ Configuration.getConfig().getBackgroundColor() + ";};");
		
	
		dialogPane.setStyle(".dialog-pane{\r\n" + 
				"  -fx-border-color:black;\r\n" + 
				"  -fx-border-width:2.0px;\r\n" + 
				" }\r\n" + 
				"\r\n" + 
				"/**Costumization of The Bar where the buttons are located**/\r\n" + 
				".dialog-pane > .button-bar > .container {\r\n" + 
				"  -fx-background-color:black;\r\n" + 
				"}\r\n" + 
				"\r\n" + 
				".dialog-pane > .content.label {\r\n" + 
				"   -fx-padding: 0.5em 0.5em 0.5em 0.5em;\r\n" + 
				"   -fx-background-color: yellow;\r\n" + 
				"   -fx-text-fill:black;\r\n" + 
				"   -fx-font-size:15.0px;\r\n" + 
				"}\r\n" + 
				"\r\n" + 
				"/**Costumization of DialogPane Header**/\r\n" + 
				".dialog-pane:header .header-panel {\r\n" + 
				"  -fx-background-color: black;\r\n" + 
				"}\r\n" + 
				"\r\n" + 
				".dialog-pane:header .header-panel .label{\r\n" + 
				"  -fx-background-color: yellow;\r\n" + 
				"  -fx-background-radius:10px;\r\n" + 
				"  -fx-text-fill:black;\r\n" + 
				"  -fx-font-size:15.0px;\r\n" + 
				"}\r\n" + 
				"\r\n" + 
				"\r\n" + 
				"/**Costumization of Buttons**/\r\n" + 
				".dialog-pane .button{\r\n" + 
				"   -fx-background-color:black;\r\n" + 
				"   -fx-text-fill:white;\r\n" + 
				"   -fx-wrap-text: true;\r\n" + 
				"   -fx-effect: dropshadow( three-pass-box, yellow, 10.0, 0.0, 0.0, 0.0);\r\n" + 
				"   -fx-cursor:hand;\r\n" + 
				" }\r\n" + 
				"\r\n" + 
				".dialog-pane .button:hover{     \r\n" + 
				"  -fx-background-color:white;\r\n" + 
				"  -fx-text-fill:black;\r\n" + 
				"  -fx-font-weight:bold; \r\n" + 
				" }\r\n" + 
				"");
		
		
	}
}
