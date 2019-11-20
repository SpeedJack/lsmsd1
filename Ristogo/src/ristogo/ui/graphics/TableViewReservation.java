package ristogo.ui.graphics;

import java.io.IOException;
import java.time.LocalDate;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import ristogo.common.entities.Entity;
import ristogo.common.entities.Reservation;
import ristogo.common.entities.enums.ReservationTime;
import ristogo.common.net.ResponseMessage;
import ristogo.config.Configuration;
import ristogo.net.Protocol;
import ristogo.ui.graphics.beans.ReservationBean;


public class TableViewReservation extends TableView<ReservationBean>  {

	 private final ObservableList<ReservationBean> reservationList;
	 private TableColumn<ReservationBean, String> nameColumn;
	 private TableColumn<ReservationBean, LocalDate> dateColumn;
	 private TableColumn<ReservationBean, ReservationTime> hourColumn;
	 private TableColumn<ReservationBean, String> seatsColumn;


	 @SuppressWarnings("unchecked")
		public TableViewReservation(boolean isCustomer){

			String font = Configuration.getConfig().getFontName();
			double dimC = Configuration.getConfig().getFontSize();
			String textColor = Configuration.getConfig().getFgColorName();

			setEditable(false);
			setFixedCellSize(35);
			setMinWidth(600);
			setMaxWidth(600);

			if (isCustomer) {
				setMaxHeight((Configuration.getConfig().getnumberRowsDisplayable() - 2) * getFixedCellSize());
			} else {
				setMaxHeight(Configuration.getConfig().getnumberRowsDisplayable() * getFixedCellSize());
			}

			nameColumn = new TableColumn<ReservationBean, String>("Name");
			if (isCustomer) {

				nameColumn.setCellValueFactory(new PropertyValueFactory<>("restaurantName"));
			} else {
				nameColumn.setCellValueFactory(new PropertyValueFactory<>("userName"));
			}
			nameColumn.setStyle(
				"-fx-font-family: " + font + "; -fx-font-size: " + dimC + "; -fx-color: " + textColor + ";");

			dateColumn = new TableColumn<ReservationBean, LocalDate>("Date");
			dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
			dateColumn.setStyle(" -fx-alignment: CENTER; -fx-font-family: " + font + "; -fx-font-size: " + dimC +
				"; -fx-color: " + textColor + ";");

			hourColumn = new TableColumn<ReservationBean, ReservationTime>("Hour");
			hourColumn.setCellValueFactory(new PropertyValueFactory<>("time"));
			hourColumn.setStyle("-fx-alignment: CENTER; -fx-font-family: " + font + "; -fx-font-size: " + dimC +
				"; -fx-color: " + textColor + ";");

			seatsColumn = new TableColumn<ReservationBean, String>("Seats");
			seatsColumn.setCellValueFactory(new PropertyValueFactory<>("seats"));
			seatsColumn.setStyle("-fx-alignment: CENTER; -fx-font-family: " + font + "; -fx-font-size: " + dimC +
				"; -fx-color: " + textColor + ";");

			nameColumn.setMinWidth(300);
			nameColumn.setMaxWidth(300);
			dateColumn.setMinWidth(150);
			dateColumn.setMaxWidth(150);
			hourColumn.setMinWidth(100);
			hourColumn.setMaxWidth(100);
			seatsColumn.setMinWidth(50);
			seatsColumn.setMaxWidth(50);

			getColumns().addAll(nameColumn, dateColumn, hourColumn, seatsColumn);
			reservationList = FXCollections.observableArrayList();
			setItems(reservationList);
		}


	  	public void listReservations(boolean isCostumer) {
	  		reservationList.clear();
		   try {
			ResponseMessage res;
			if(isCostumer) {
				res = Protocol.getProtocol().getOwnActiveReservations();
			}
			else {
				res = Protocol.getProtocol().getReservations(RistogoGUI.getMyRestaurant());
			}
			if(res.isSuccess()) {
				for (Entity entity: res.getEntities()) {
					Reservation reservation = (Reservation)entity;
					reservationList.add(ReservationBean.fromEntity(reservation));
				}
			}
		   } catch (IOException e) {
			   e.printStackTrace();
		   }
	  	}

}
