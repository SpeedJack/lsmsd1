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
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;
import ristogo.ui.graphics.config.GUIConfig;

public class RegisterDialog extends Dialog<Pair<String, String>>
{
	public RegisterDialog()
	{
		DialogPane dialogPane = getDialogPane();
		dialogPane.setStyle(GUIConfig.getCSSBgColor());

		setTitle("RistoGo - Register");
		setHeaderText("Sign in!");

		dialogPane.getStyleClass().remove("alert");

		GridPane header = (GridPane)dialogPane.lookup(".header-panel");
		header.setStyle(GUIConfig.getCSSDialogHeaderStyle());

		header.lookup(".label").setStyle(GUIConfig.getCSSFgColor());

		ImageView img = new ImageView(this.getClass().getResource("/resources/whiteLogo.png").toString());
		img.setFitHeight(50);
		img.setFitWidth(50);
		setGraphic(img);

		ButtonType registerButtonType = new ButtonType("Register", ButtonData.OK_DONE);
		dialogPane.getButtonTypes().addAll(registerButtonType, ButtonType.PREVIOUS, ButtonType.CANCEL);

		ButtonBar buttonBar = (ButtonBar)dialogPane.lookup(".button-bar");
		buttonBar.getButtons().forEach(b -> b.setStyle(GUIConfig.getCSSButtonStyle()));

		Label l1 = new Label("Name: ");
		TextField username = new TextField();
		username.setPromptText("Username");
		Label l2 = new Label("Password: ");
		PasswordField password = new PasswordField();
		password.setPromptText("Password");
		Label l3 = new Label("Type of User: ");
		ChoiceBox<String> cb = new ChoiceBox<String>();
		cb.getItems().addAll("Customer", "Restaurant Owner");

		l1.setFont(GUIConfig.getTextFont());
		l1.setTextFill(GUIConfig.getFgColor());
		username.setFont(GUIConfig.getInputFont());
		username.setMaxWidth(200);

		l2.setFont(GUIConfig.getTextFont());
		l2.setTextFill(GUIConfig.getFgColor());
		password.setFont(GUIConfig.getInputFont());
		password.setMaxWidth(200);

		l3.setFont(GUIConfig.getTextFont());
		l3.setTextFill(GUIConfig.getFgColor());

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

		dialogPane.setContent(grid);

		Node registerButton = dialogPane.lookupButton(registerButtonType);
		registerButton.setDisable(true);

		username.textProperty().addListener((observable, oldValue, newValue) -> {
			registerButton.setDisable(newValue.trim().isEmpty());
		});

		Platform.runLater(() -> username.requestFocus());

	}
}
