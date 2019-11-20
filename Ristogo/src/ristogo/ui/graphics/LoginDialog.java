package ristogo.ui.graphics;

import java.io.IOException;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import ristogo.common.entities.*;

import ristogo.common.net.ResponseMessage;
import ristogo.config.Configuration;
import ristogo.net.Protocol;
import ristogo.ui.graphics.config.GUIConfig;

public class LoginDialog extends Dialog<User>
{
	public LoginDialog()
	{

//////////////////////HEADER/////////////////////////////////////////////////////////////////////////////

		Configuration config = Configuration.getConfig();
		DialogPane dialogPane = getDialogPane();

		Stage stage = (Stage)dialogPane.getScene().getWindow();
		stage.getIcons().add(new Image(this.getClass().getResource("/resources/logo.png").toString()));

		dialogPane.setStyle(GUIConfig.getInvertedCSSBgColor());

		setTitle("RistoGo - Login");
		setHeaderText("Welcome to Ristogo!\n" + "The application that allows you to book tables\n" +
			"at your favorite restaurants!");

		dialogPane.getStyleClass().remove("alert");

		GridPane header = (GridPane)dialogPane.lookup(".header-panel");
		header.setStyle(GUIConfig.getInvertedCSSBgColor() + GUIConfig.getCSSFontFamily() +
			GUIConfig.getInvertedCSSFgColor() + "-fx-wrap-text: true ;");

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

		Button registerButton = new Button("Register");
		registerButton.setStyle(GUIConfig.getCSSFontFamily() + GUIConfig.getCSSBgColor() +
			GUIConfig.getCSSFgColor() + GUIConfig.getCSSFontSizeNormal());
		HBox registerBox = new HBox();
		registerBox.getChildren().add(registerButton);
		registerBox.setAlignment(Pos.BASELINE_RIGHT);

		VBox content = new VBox(10);
		content.getChildren().addAll(grid, error, registerBox);
		dialogPane.setContent(content);
//////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////BUTTONS////////////////////////////////////////////////////////
		ButtonType loginButtonType = new ButtonType("Login", ButtonData.OK_DONE);
		dialogPane.getButtonTypes().addAll(loginButtonType, ButtonType.CLOSE);

		ButtonBar buttonBar = (ButtonBar)dialogPane.lookup(".button-bar");
		buttonBar.getButtons().forEach(b -> b.setStyle(GUIConfig.getCSSFontFamily() +
			GUIConfig.getCSSBgColor() + GUIConfig.getCSSFgColor() + GUIConfig.getCSSFontSizeNormal()));

		Node closeButton = getDialogPane().lookupButton(ButtonType.CLOSE);
		closeButton.managedProperty().bind(closeButton.visibleProperty());
		closeButton.setVisible(false);

		Node loginButton = getDialogPane().lookupButton(loginButtonType);
		loginButton.setDisable(true);
		username.textProperty().addListener((observable, oldValue, newValue) -> {
			loginButton.setDisable(newValue.trim().isEmpty());
		});
		Platform.runLater(() -> username.requestFocus());

		registerButton.setOnAction((ActionEvent ev) -> {
			RegisterDialog register = new RegisterDialog();
			register.showAndWait();
			while (register.getResult() != 0) {
				register.showAndWait();
			}
		});

		setResultConverter(dialogButton -> {
			if (dialogButton == loginButtonType) {
				try {
					ResponseMessage res = Protocol.getProtocol()
						.performLogin(new Customer(username.getText(), password.getText()));
					if (!res.isSuccess()) {
						error.setVisible(true);
						return new Customer(-1, "");
					} else {
						return (User)res.getEntity();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return null;
		});
/////////////////////////////////////////////////////////////////////////////////////////////////////////////
	}
}
