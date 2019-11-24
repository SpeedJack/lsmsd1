package ristogo.ui.graphics;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import ristogo.common.entities.Entity;
import ristogo.common.entities.Restaurant;
import ristogo.common.entities.enums.Genre;
import ristogo.common.entities.enums.OpeningHours;
import ristogo.common.entities.enums.Price;
import ristogo.common.net.ResponseMessage;
import ristogo.net.Protocol;
import ristogo.ui.graphics.beans.RestaurantBean;
import ristogo.ui.graphics.config.GUIConfig;

public class TableViewRestaurant extends TableView<RestaurantBean>
{
	private final ObservableList<RestaurantBean> restaurantList;

	@SuppressWarnings("unchecked")
	public TableViewRestaurant()
	{
		restaurantList = FXCollections.observableArrayList();
		
		setEditable(false);
		setFixedCellSize(35);
		setMinWidth(600);
		setMaxWidth(600);
		setMaxHeight(GUIConfig.getMaxRowDisplayable() * getFixedCellSize());

		TableColumn<RestaurantBean, String> nameColumn = new TableColumn<RestaurantBean, String>("Name");
		nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
		nameColumn.setStyle(GUIConfig.getCSSTableColumnStyle(false));
		nameColumn.setMinWidth(150);
		nameColumn.setMaxWidth(150);
		
		TableColumn<RestaurantBean, Genre> typeColumn = new TableColumn<RestaurantBean, Genre>("Type");
		typeColumn.setCellValueFactory(new PropertyValueFactory<>("genre"));
		typeColumn.setStyle(GUIConfig.getCSSTableColumnStyle());
		typeColumn.setMinWidth(100);
		typeColumn.setMaxWidth(100);

		TableColumn<RestaurantBean, Price> priceColumn = new TableColumn<RestaurantBean, Price>("Price");
		priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
		priceColumn.setStyle(GUIConfig.getCSSTableColumnStyle());
		priceColumn.setMinWidth(100);
		priceColumn.setMaxWidth(100);

		TableColumn<RestaurantBean, String> cityColumn = new TableColumn<RestaurantBean, String>("City");
		cityColumn.setCellValueFactory(new PropertyValueFactory<>("city"));
		cityColumn.setStyle(GUIConfig.getCSSTableColumnStyle());
		cityColumn.setMinWidth(100);
		cityColumn.setMaxWidth(100);

		TableColumn<RestaurantBean, String> addressColumn = new TableColumn<RestaurantBean, String>("Address");
		addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
		addressColumn.setStyle(GUIConfig.getCSSTableColumnStyle());
		addressColumn.setMinWidth(200);
		addressColumn.setMaxWidth(200);

		getColumns().addAll(nameColumn, typeColumn, priceColumn, cityColumn, addressColumn);
		setItems(restaurantList);

	}

	public String getSelectionName()
	{
		return this.getSelectionModel().getSelectedItem().getName();
	}

	public String getSelectionDescription()
	{
		return this.getSelectionModel().getSelectedItem().getDescription();
	}

	public OpeningHours getSelectionHours()
	{
		return this.getSelectionModel().getSelectedItem().getOpeningHours();
	}

	public void listRestaurants()
	{
		listRestaurants(null);
	}

	public void listRestaurants(String findCity)
	{
		restaurantList.clear();
		ResponseMessage res = null;
		if(findCity == null) {
			res = Protocol.getInstance().getRestaurants();
		} else {
			Restaurant restaurant = new Restaurant();
			restaurant.setCity(findCity);
			res = Protocol.getInstance().getRestaurants(restaurant);
		}
		if (res.isSuccess())
			for (Entity entity : res.getEntities())
				restaurantList.add(RestaurantBean.fromEntity((Restaurant)entity));
	}
}
