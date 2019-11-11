package ristogo.ui.graphics;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import ristogo.config.Configuration;
import ristogo.ui.graphics.config.GUIConfig;

public class RegisterDialog extends Dialog<Integer> {
	
	public RegisterDialog(){

		
		Configuration config = Configuration.getConfig();
		DialogPane dialogPane = getDialogPane();
		Stage stage = (Stage)dialogPane.getScene().getWindow();
		stage.getIcons().add(new Image(this.getClass().getResource("/resources/logo.png").toString()));
		
		dialogPane.setStyle(GUIConfig.getInvertedCSSBgColor());

		setTitle("RistoGo - Register");
		setHeaderText("Sign in!");

		dialogPane.getStyleClass().remove("alert");

		GridPane header = (GridPane)dialogPane.lookup(".header-panel");
		header.setStyle(GUIConfig.getInvertedCSSBgColor() + 
				GUIConfig.getCSSFontFamily() +
				GUIConfig.getInvertedCSSFgColor() +
				"-fx-wrap-text: true ;");

		header.lookup(".label").setStyle(GUIConfig.getCSSFgColor());

		ImageView img = new ImageView(this.getClass().getResource("/resources/whiteLogo.png").toString());
		img.setFitHeight(50);
		img.setFitWidth(50);
		setGraphic(img);
		
////////////////////////////CONTENUTO//////////////////////////////////////////////////////////////////////
		Label l1 = new Label("Name: ");
		TextField username = new TextField();
		username.setPromptText("Username");
		Label l2 = new Label("Password: ");
		PasswordField password = new PasswordField();
		password.setPromptText("Password");
		Label l3 = new Label("Type of User: ");
		ChoiceBox<String> cb = new ChoiceBox<String>();
		cb.getItems().addAll("Customer", "Restaurant Owner");

		l1.setFont(GUIConfig.getFormTitleFont());
		l1.setTextFill(GUIConfig.getBgColor());
		username.setFont(GUIConfig.getTextFont());
		username.setMaxWidth(200);
		
		l2.setFont(GUIConfig.getFormTitleFont());
		l2.setTextFill(GUIConfig.getBgColor());
		password.setFont(GUIConfig.getTextFont());
		password.setMaxWidth(200);

		l3.setFont(GUIConfig.getFormTitleFont());
		l3.setTextFill(GUIConfig.getBgColor());
		
		Label error = new Label("Error: Login Failed. Retry");
		error.setFont(GUIConfig.getFormTitleFont());
		error.setTextFill(GUIConfig.getBgColor());
		error.setStyle("-fx-background-color:   red;");
		error.setVisible(false);

		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		grid.add(l1, 0, 0);
		grid.add(username, 1, 0);
		grid.add(l2, 0, 1);
		grid.add(password, 1, 1);
		grid.add(l3, 0, 2);
		grid.add(cb, 1, 2);
		
		VBox content = new VBox(10);
		content.getChildren().addAll(grid,error);
		dialogPane.setContent(content);


/////////////////////////////////////////////////////////////////////////////////////////////////////
		
		ButtonType registerButtonType = new ButtonType("Register", ButtonData.OK_DONE);
		dialogPane.getButtonTypes().addAll(registerButtonType, ButtonType.CANCEL);

		ButtonBar buttonBar = (ButtonBar)dialogPane.lookup(".button-bar");
		buttonBar.getButtons().forEach(b -> b.setStyle(GUIConfig.getCSSFontFamily()
														+GUIConfig.getCSSBgColor()
														+GUIConfig.getCSSFgColor()
														+GUIConfig.getCSSFontSizeNormal()));

		Node registerButton = dialogPane.lookupButton(registerButtonType);
		registerButton.setDisable(true);
		username.textProperty().addListener((observable, oldValue, newValue) -> {
			registerButton.setDisable(newValue.trim().isEmpty());
		});

		Platform.runLater(() -> username.requestFocus());
		
		setResultConverter(dialogButton -> {
		    if (dialogButton == registerButtonType) {
		    	//MANDARE COMANDO REGISTER
		    	//SE OK
		        return  0;
		    }
		    else {
		    	return -1;
		    }
		    
		});	
	}
}
