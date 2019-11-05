package ristogo.ui.graphics;

import java.time.*;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.text.*;
import ristogo.config.Configuration;
import ristogo.ui.graphics.config.GUIConfig;

public class BookForm extends VBox
{
	private TextField nameField;
	private DatePicker dateField;
	private ChoiceBox<String> hourField;
	private ChoiceBox<Integer> seatsField;
	private Button check;
	private Button book;
	private Button delRes;

	public BookForm()
	{
		super(20);

		/*String font = Configuration.getConfig().getFontName();
		double dimC = Configuration.getConfig().getFontSize();
		String textColor = Configuration.getConfig().getFgColorName();
		String backgroundColor = Configuration.getConfig().getBgColorName();*/

		Label title = new Label("Book a table");
		Label subTitle = new Label("To select a restaurant click on the table on the side");
		title.setStyle("-fx-underline: true;");
		title.setFont(GUIConfig.getFormTitleFont());
		title.setTextFill(GUIConfig.getFgColor());
		subTitle.setFont(GUIConfig.getFormSubtitleFont());
		subTitle.setTextFill(GUIConfig.getFgColor());

		Label name = new Label("Name of Restaurant: ");
		nameField = new TextField();
		name.setFont(GUIConfig.getImportantFont());
		name.setTextFill(GUIConfig.getFgColor());
		nameField.setEditable(false);

		HBox nameBox = new HBox(20);
		nameBox.getChildren().addAll(name, nameField);

		Label date = new Label("Date of Reservation: ");
		date.setFont(GUIConfig.getImportantFont());
		date.setTextFill(GUIConfig.getFgColor());
		dateField = new DatePicker();
		dateField.setDayCellFactory(picker -> new DateCell() { // Disable all past dates
			public void updateItem(LocalDate date, boolean empty)
			{
				super.updateItem(date, empty);
				LocalDate today = LocalDate.now();

				setDisable(empty || date.compareTo(today) < 0);
			}
		});

		HBox dateBox = new HBox(20);
		dateBox.getChildren().addAll(date, dateField);

		Label hour = new Label("Booking Time: ");
		hour.setFont(GUIConfig.getImportantFont());
		hour.setTextFill(GUIConfig.getFgColor());
		hourField = new ChoiceBox<String>();
		hourField.setMinSize(70, 25);
		hourField.setMaxSize(70, 25);

		HBox hourBox = new HBox(20);
		hourBox.getChildren().addAll(hour, hourField);

		check = new Button("Check");
		check.setFont(GUIConfig.getButtonFont());
		check.setTextFill(GUIConfig.getInvertedFgColor());
		//check.setStyle("-fx-base: " + textColor);

		Label seats = new Label("Seats: ");
		seats.setFont(GUIConfig.getInputFont());
		seats.setTextFill(GUIConfig.getFgColor());
		seatsField = new ChoiceBox<Integer>();
		seatsField.setDisable(true);

		HBox seatsBox = new HBox(20);
		seatsBox.getChildren().addAll(seats, seatsField);

		////////////////// error/////////////////////////////////////////////////////
		Label error = new Label();
		error.setFont(GUIConfig.getTextFont());
		error.setTextFill(GUIConfig.getInvertedFgColor());
		error.setStyle("-fx-background-color: red;");
		error.setVisible(false);
		///////////////////////////////////////////////////////////////////////////////

		book = new Button("Book");
		book.setFont(GUIConfig.getButtonFont());
		book.setTextFill(GUIConfig.getInvertedFgColor());
		//book.setStyle("-fx-base: " + textColor);
		book.setDisable(true);

		delRes = new Button("Del. Res.");
		delRes.setFont(GUIConfig.getButtonFont());
		delRes.setTextFill(GUIConfig.getInvertedFgColor());
		//delRes.setStyle("-fx-base: " + textColor);
		delRes.setDisable(true);

		HBox buttonBox = new HBox(20);
		buttonBox.getChildren().addAll(book, delRes);

		this.getChildren().addAll(title, subTitle, nameBox, dateBox, hourBox, check, seatsBox, error,
			buttonBox);
		//this.setStyle("-fx-padding: 7;" + "-fx-border-style: solid inside;" + "-fx-border-width: 2;" +
		//	"-fx-border-insets: 3;" + "-fx-border-radius: 10;" + "-fx-border-color: " + textColor + ";");

	}

	public TextField getNameField()
	{
		return nameField;
	}

	public void setNameField(TextField nameField)
	{
		this.nameField = nameField;
	}

	public DatePicker getDateField()
	{
		return dateField;
	}

	public void setDateField(DatePicker dateField)
	{
		this.dateField = dateField;
	}

	public ChoiceBox<String> getHourField()
	{
		return hourField;
	}

	public void setHourField(ChoiceBox<String> hourField)
	{
		this.hourField = hourField;
	}

	public ChoiceBox<Integer> getSeatsField()
	{
		return seatsField;
	}

	public void setSeatsField(ChoiceBox<Integer> seatsField)
	{
		this.seatsField = seatsField;
	}

	public Button getCheck()
	{
		return check;
	}

	public void setCheck(Button check)
	{
		this.check = check;
	}

	public Button getBook()
	{
		return book;
	}

	public void setBook(Button book)
	{
		this.book = book;
	}

	public Button getDelRes()
	{
		return delRes;
	}

	public void setDelRes(Button delRes)
	{
		this.delRes = delRes;
	}
}
