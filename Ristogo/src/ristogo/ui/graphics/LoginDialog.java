package ristogo.ui.graphics;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Pair;
import ristogo.config.Configuration;
import ristogo.ui.graphics.config.GUIConfig;

public class LoginDialog extends Dialog<Pair<String, String>>
{
	public LoginDialog()
	{
		Configuration config = Configuration.getConfig();
		DialogPane dialogPane = getDialogPane();
		Stage stage = (Stage)dialogPane.getScene().getWindow();
		stage.getIcons().add(new Image(this.getClass().getResource("/resources/logo.png").toString()));
		
		dialogPane.setStyle(GUIConfig.getCSSBgColor());
		
		setTitle("RistoGo - Login");
		setHeaderText("Welcome to Ristogo!\n" +
			"The application that allows you to book tables\n" +
			"at your favorite restaurants!");
		
		dialogPane.getStyleClass().remove("alert");

		GridPane header = (GridPane)dialogPane.lookup(".header-panel");
		header.setStyle(GUIConfig.getCSSDialogHeaderStyle());

		header.lookup(".label").setStyle("-fx-text-fill: " + config.getBgColorName() + ";");
		
		ImageView img = new ImageView(this.getClass().getResource("/resources/whiteLogo.png").toString());
		img.setFitHeight(50);
		img.setFitWidth(50);
		setGraphic(img);
		
		ButtonType loginButtonType = new ButtonType("Login", ButtonData.OK_DONE);
		ButtonType registerButtonType = new ButtonType("Register", ButtonData.OK_DONE);
		dialogPane.getButtonTypes().addAll(loginButtonType, registerButtonType, ButtonType.CANCEL);
		
		ButtonBar buttonBar = (ButtonBar)dialogPane.lookup(".button-bar");
		buttonBar.getButtons().forEach(b -> b.setStyle(GUIConfig.getCSSButtonStyle()));
	
		
		Label l1 = new Label("Name: ");
		TextField username = new TextField();
		username.setPromptText("Username");
		Label l2 = new Label("Password: ");
		PasswordField password = new PasswordField();
		password.setPromptText("Password");
		
		l1.setFont(GUIConfig.getTextFont());
		l1.setTextFill(GUIConfig.getFgColor());
		username.setFont(GUIConfig.getInputFont());
		username.setMaxWidth(200);
		
		l2.setFont(GUIConfig.getTextFont());
		l2.setTextFill(GUIConfig.getFgColor());
		password.setFont(GUIConfig.getInputFont());
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
			if (dialogButton == loginButtonType)
				return new Pair<String, String>(username.getText(), password.getText());
			else if(dialogButton == registerButtonType)
				return new Pair<String, String>("register","");
			return null;
		});
	}
}
