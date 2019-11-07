package ristogo.ui.graphics;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import ristogo.config.Configuration;
import ristogo.ui.graphics.beans.RestaurantBean;



public class TableViewRestaurant extends TableView<RestaurantBean> {
	
	final ObservableList<RestaurantBean> restaurantList;
	private TableColumn<RestaurantBean, String> nameColumn;
	private TableColumn<RestaurantBean, String> typeColumn;
	private TableColumn<RestaurantBean, String> priceColumn;
	private TableColumn<RestaurantBean, String> cityColumn;
	private TableColumn<RestaurantBean, String> addressColumn;
	 
		 
	@SuppressWarnings("unchecked")
	public TableViewRestaurant()
	{
		setEditable(true);
		setFixedCellSize(35);
		setMaxHeight(Configuration.getConfig().getnumberRowsDisplayable() * getFixedCellSize());
		setMinWidth(600);
		setMaxWidth(600);
		String font = Configuration.getConfig().getFontName();
		double dimC = Configuration.getConfig().getFontSize();
		String textColor = Configuration.getConfig().getFgColorName();
		    
		    
		nameColumn = new TableColumn<RestaurantBean, String>("Name");
		nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
		nameColumn.setStyle(
			"-fx-font-family: " + font + "; -fx-font-size: " + dimC + "; -fx-color: " + textColor + ";");

		typeColumn = new TableColumn<RestaurantBean, String>("Type");
		typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
		typeColumn.setStyle(" -fx-alignment: CENTER; -fx-font-family: " + font + "; -fx-font-size: " + dimC +
			"; -fx-color: " + textColor + ";");

		priceColumn = new TableColumn<RestaurantBean, String>("Price");
		priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
		priceColumn.setStyle("-fx-alignment: CENTER; -fx-font-family: " + font + "; -fx-font-size: " + dimC +
			"; -fx-color: " + textColor + ";");

		cityColumn = new TableColumn<RestaurantBean, String>("City");
		cityColumn.setCellValueFactory(new PropertyValueFactory<>("city"));
		cityColumn.setStyle("-fx-alignment: CENTER; -fx-font-family: " + font + "; -fx-font-size: " + dimC +
			"; -fx-color: " + textColor + ";");

		addressColumn = new TableColumn<RestaurantBean, String>("Address");
		addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
		addressColumn.setStyle("-fx-alignment: CENTER; -fx-font-family: " + font + "; -fx-font-size: " + dimC +
			"; -fx-color: " + textColor + ";");

		nameColumn.setMinWidth(150);
		nameColumn.setMaxWidth(150);
		typeColumn.setMinWidth(100);
		typeColumn.setMaxWidth(100);
		priceColumn.setMinWidth(50);
		priceColumn.setMaxWidth(50);
		cityColumn.setMinWidth(100);
		cityColumn.setMaxWidth(100);
		addressColumn.setMinWidth(200);
		addressColumn.setMaxWidth(200);

		getColumns().addAll(nameColumn, typeColumn, priceColumn, cityColumn, addressColumn);
		restaurantList = FXCollections.observableArrayList();
		setItems(restaurantList);
		    
	}	
	
 	public String getSelectionName() {
 		return this.getSelectionModel().getSelectedItem().getName();
 	}
 	
 	public String getSelectionDescription() {
 		return this.getSelectionModel().getSelectedItem().getDescription();
 	}
 	
 	public String getSelectionHours() {
 		return this.getSelectionModel().getSelectedItem().getOpening();
 	}
 	
	   public void listRestaurants(){
		   
	   }
	  
}
