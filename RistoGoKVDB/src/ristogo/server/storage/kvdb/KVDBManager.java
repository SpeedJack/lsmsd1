package ristogo.server.storage.kvdb;

import org.iq80.leveldb.*;
import static org.iq80.leveldb.impl.Iq80DBFactory.*;
import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import ristogo.common.entities.Entity;
import ristogo.common.entities.Reservation;
import ristogo.common.entities.User;

import ristogo.server.storage.entities.Restaurant_;
import ristogo.server.storage.entities.Reservation_;
import ristogo.server.storage.entities.Entity_;
import ristogo.server.storage.entities.User_;
import ristogo.server.storage.kvdb.adapter.EntityAdapter;
import ristogo.server.storage.kvdb.config.Configuration;

public class KVDBManager {
	 
	private static KVDBManager istance=null;
	private static DB db = null;
	//currentSnaphsot;

	private KVDBManager() {};

	public static  KVDBManager get() {
		if(istance==null)
			istance = new KVDBManager();
		return istance;
	};

	public boolean open() {
		
		try {
			if(db == null)
				Configuration.getConfig().setLogger(Logger.getLogger(KVDBManager.class.getName()));
				db = factory.open(new File(Configuration.getConfig().getPath()), 
						Configuration.getConfig().getOptions());
			return true;
		} catch (Exception e) {
			e.printStackTrace(); //@TODO: RIMUOVERE
			return false;	
		}
	};

	
	//RestaurantList should work
	public List<Restaurant_> getRestaurantList(){
		if(open()) {
			
			try (DBIterator iteratorRest = db.iterator();
					){
				List<Restaurant_> lr = new ArrayList<>();
				HashMap<String,String> kv = new HashMap<String,String>();
				//Start from the first occurency of restaurant:$rest_id:...:
				for(iteratorRest.seek(bytes("restaurant")); iteratorRest.hasNext(); iteratorRest.next()) {
					String key = asString(iteratorRest.peekNext().getKey());
					String value = asString(iteratorRest.peekNext().getValue());
					// remember key arrangement : restaurant:$restaurant_id:$attribute_name = $value
					//Possible attributes: "owner_id","name","genre", "cost","city","address","opening_hours"
	                String[] keySplit = key.split(":"); // split the key
	                if(!keySplit[0].equals("restaurant")) break;
	                String prefix = keySplit[0]+":"+keySplit[1]+":";
	                kv.put("id", keySplit[1]);// identifica univocamente un risto
	               //Finchè le prime parti della chiave sono uguali a prefix sono sul solito risto
	              
	                //inserisco prima coppia k-v dopo id
	                kv.put(keySplit[keySplit.length - 1], value);
	                
	                //Cerco altre coppie k-v con stesso restaurant_id
	                while(iteratorRest.hasNext()) {
	                	iteratorRest.next();
	                	key = asString(iteratorRest.peekNext().getKey()); 
	                	value = asString(iteratorRest.peekNext().getValue());
	                	if(key.contains(prefix)) {
	                		//Se contiene il prefix allora è lo stesso ristorante
	                		keySplit = key.split(":");
	                		kv.put(keySplit[keySplit.length - 1], value);
	                	} else {
	                		//Altrimenti è un altro
	                		 //Torno indietro di uno (il for mi riporta l'iterator qua
	                		iteratorRest.prev();
	   	                	//e ricomincio a costruire un nuovo risto da zero)
	                		break;
	                	};
	                	lr.add((Restaurant_)EntityAdapter.buildEntity(EntityAdapter.RESTAURANT, kv));
	                };
				} 
				return lr;
			} catch (Exception e) {
				System.out.println(e.getMessage());
				return null;
			}
		};
		return null;
	
	};
	
	
	public List<Restaurant_> getOwnerRestaurantList(int id) {
		if(open()) {
		try (DBIterator iteratorRest = db.iterator();
				){
			List<Restaurant_> lr = new ArrayList<>();
			HashMap<String,String> kv = new HashMap<String,String>();
			//Start from the first occurency of restaurant:$rest_id:...:
			for(iteratorRest.seek(bytes("restaurant")); iteratorRest.hasNext(); iteratorRest.next()) {
				String key = asString(iteratorRest.peekNext().getKey());
				String value = asString(iteratorRest.peekNext().getValue());
				// remember key arrangement : restaurant:$restaurant_id:$attribute_name = $value
				//Possible attributes: "owner_id","name","genre", "cost","city","address","opening_hours"
                String[] keySplit = key.split(":"); // split the key
                if(!keySplit[0].equals("restaurant")) break;
                String prefix = keySplit[0]+":"+keySplit[1]+":";
                kv.put("id", keySplit[1]);// identifica univocamente un risto
               //Finchè le prime parti della chiave sono uguali a prefix sono sul solito risto
              
                //inserisco prima coppia k-v dopo id
                kv.put(keySplit[keySplit.length - 1], value);
                
                //Cerco altre coppie k-v con stesso restaurant_id
                while(iteratorRest.hasNext()) {
                	iteratorRest.next();
                	key = asString(iteratorRest.peekNext().getKey()); 
                	value = asString(iteratorRest.peekNext().getValue());
                	if(key.contains(prefix)) {
                		//Se contiene il prefix allora è lo stesso ristorante
                		keySplit = key.split(":");
                		kv.put(keySplit[keySplit.length - 1], value);
                	} else {
                		//Altrimenti è un altro
                		 //Torno indietro di uno (il for mi riporta l'iterator qua
                		iteratorRest.prev();
   	                	//e ricomincio a costruire un nuovo risto da zero)
                		break;
                	};
                	
                	if (kv.get("owner_id").equals(Integer.toString(id))){
                		lr.add((Restaurant_)EntityAdapter.buildEntity(EntityAdapter.RESTAURANT, kv));
                	}
                };
			} 
			return lr;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return null;
		}
		}
		return null;
	};

	
	public List<Reservation_> getOwnerReservationList(int id){ 
		

		//reservation:$id:$attribute = $value
		
		if(open()) {
			
			try(DBIterator iter = db.iterator()){
				List<Reservation_> lr = new ArrayList<>();
				HashMap<String,String> kv = new HashMap<String,String>();
				String key = null;
				String value = null;
				String[] keySplit = null;
				String restaurant_name = null;
				String restaurant_id = null;
			
				String reservation_id = null;
				String customer_id = null;
				String customer_name = null;
				String seats = null;
				String hour = null;
				String date = null;
				String current_reservation = null;
				
				//Cerco id owner in restaurant
				for(iter.seek(bytes("restaurant")); iter.hasNext(); iter.next()){
					key = asString(iter.peekNext().getKey());
					keySplit = key.split(":");
					if(keySplit[keySplit.length - 1].equals("owner_id")) {
						value = asString(iter.peekNext().getValue());
						if(id == Integer.parseInt(value)) {
							restaurant_id = keySplit[1];
							restaurant_name = asString(db.get(bytes(key+":name")));	
							if(restaurant_name == null) return null;
							break;
						};
					}
				};
				
				//Cerco le prenotazioni relative a quel ristorante
				
				for(iter.seek(bytes("reservation")); iter.hasNext(); iter.next()) {
					
					
					key = asString(iter.peekNext().getKey());
					keySplit = key.split(":");
					
					//Break condition
					if(!keySplit[0].equals("reservatin")) break;
					
					reservation_id = keySplit[1];
					customer_id = null;
					customer_name = null;
					seats = null;
					hour = null;
					date = null;
					current_reservation = "reservation:"+reservation_id;
					
					value = asString(db.get(bytes(current_reservation + ":restaurant_id")));
					
					//Se il restaurant_id è quello giusto inizio a inserire valori nella mappa
					if(value.equals(restaurant_id)) {
						
						kv.put("restaurant_name", restaurant_name);
						
						kv.put("reservation_id", reservation_id);
						
						customer_id = db.get(bytes(current_reservation+":customer_id")).toString();
						if(customer_id == null) return null;
						customer_name = db.get(bytes("user:"+customer_id+":customer_name")).toString(); //customer???
						if(customer_name == null) return null; 
						kv.put("customer_name", customer_name);
						
						seats = db.get(bytes(current_reservation+":seats")).toString();
						if(seats == null) return null;
						kv.put("seats", seats);
						
						hour = db.get(bytes(current_reservation+":hour")).toString();
						if(hour == null) return null;
						kv.put("hour", hour);
						
						date = db.get(bytes(current_reservation+":date")).toString();
						if(date == null) return null;
						if(LocalDate.parse(date).isBefore(LocalDate.now()) ) continue;
						kv.put("date", date);
						
						//creo l'entità e l'aggiungo alla lista 
						lr.add((Reservation_)EntityAdapter.buildEntity(EntityAdapter.RESERVATION,kv));
						kv.clear();
						
					};
				}
					
					return lr;
					
				} catch (Exception e) {
				System.out.println(e.getMessage());
				return null;
			}	
		}
		return null;
	};
		
	
	private List<Reservation_> getReservationListCustomer(int id){
		if(open()) {
			List<Reservation_> lr = null;
			try(DBIterator iter = db.iterator()){
				lr = new ArrayList<>();
				HashMap<String,String> kv = new HashMap<String,String>();
				String key = null;
				String value = null;
				String[] keySplit = null;
				String restaurant_name = null;
				String restaurant_id = null;
				String customer_name = null;
				String reservation_id = null;
				String customer_id = null;
				String seats = null;
				String hour = null;
				String date = null;
				String current_reservation = null;
				
				//cerco nome ristorante e nome cliente (il nome cliente è uno solo)
				customer_name = db.get(bytes("user:"+id)).toString(); //nome cliente
				if(customer_name == null) return null;
				for(iter.seek(bytes("reservation")); iter.hasNext(); iter.next()){
					
					key = asString(iter.peekNext().getKey());
					keySplit = key.split(":");
					
					//Break condition
					if(!keySplit[0].equals("reservation")) break;
					
					reservation_id = keySplit[1];
					customer_id = Integer.toString(id);
					
					seats = null;
					hour = null;
					date = null;
					current_reservation = "reservation:"+reservation_id;
					
					
					value = asString(db.get(bytes(current_reservation + ":customer_id")));
					if(value.isBlank()) return null;
					if(value.equals(customer_id)) {
						kv.put("reservation_id", reservation_id);
						
						restaurant_id = db.get(bytes(current_reservation + ":restaurant_id")).toString();
						if(restaurant_id == null) return null;
						restaurant_name = db.get(bytes("restaurant:"+restaurant_id)).toString();
						if(restaurant_name == null) return null;
						kv.put("restaurant_name", restaurant_name);
						
						customer_name = db.get(bytes("user:"+customer_id+":customer_name")).toString(); //customer???
						if(customer_name == null) return null; 
						kv.put("customer_name", customer_name);
						
						seats = db.get(bytes(current_reservation+":seats")).toString();
						if(seats == null) return null;
						kv.put("seats", seats);
						
						hour = db.get(bytes(current_reservation+":hour")).toString();
						if(hour == null) return null;
						kv.put("hour", hour);
						
						date = db.get(bytes(current_reservation+":date")).toString();
						if(date == null) return null;
						if(LocalDate.parse(date).isBefore(LocalDate.now()) ) continue;
						kv.put("date", date);
						
						
						//creo l'entità e l'aggiungo alla lista 
						lr.add((Reservation_)EntityAdapter.buildEntity(EntityAdapter.RESERVATION,kv));
						kv.clear();
					};
					
					
					
				};
			} catch (Exception e) {
				System.out.println(e.getMessage());
				return null;
			};
			return lr;
		}
		return null;
	}
		
	public boolean insert(Entity_ entity){
		if(entity == null) return false;
		
		if(entity instanceof Restaurant_) {
			Restaurant_ r = ((Restaurant_) entity);
			return insertRestaurant(r);
		};
		if(entity instanceof User_) {
			User_ u = ((User_) entity);
			return insertUser(u);
		};
		if(entity instanceof Reservation_) {
			Reservation_ r = ((Reservation_) entity);
			return insertReservation(r);
		};
		
		//refresh snapshot
		return false;
		
	};
	
	public boolean insertUser(User_ u) {
		if(open()) {
			String key = EntityAdapter.stringifyKey(u);
			try(WriteBatch batch = db.createWriteBatch()){
				batch.put(bytes(key), bytes(u.getUsername()));
				batch.put(bytes(key), bytes(u.getPasswordHash()));
				db.write(batch);
				return true;
			} catch (Exception e) {
				System.out.println(e.getMessage());
				return false;
			}
		}
		return false;
	};
	public boolean insertRestaurant(Restaurant_ r) {
		if(open()) {
			String owner_id = null;
			String key = EntityAdapter.stringifyKey(r);
			try(DBIterator iter = db.iterator()){
				String userKey = null;
				String[] userKeySplit = null;
				
				//Cerco owner id in owner
				for(iter.seek(bytes("user:")); iter.hasNext(); iter.next()) {
					userKey = iter.peekNext().getKey().toString();
					userKeySplit = userKey.split(":");
					
					//Se raggiungo la fine degli utenti prima di trovare quello giusto non aggiungo (manca l'utente)
					if(!iter.peekNext().toString().equals("user")) return false;
					if(userKeySplit[userKeySplit.length -1] == "username" ) {
						if(iter.peekNext().getValue().toString().equals(r.getOwner().getUsername()) ) {
							owner_id = userKeySplit[1];
							break;
						};
					};
					
				};
			} catch (Exception e) {
				System.out.println(e.getMessage());
				return false;
			};
			
			try(WriteBatch batch = db.createWriteBatch()){
			
				batch.put(bytes(key + ":owner_id"), bytes(owner_id));
				batch.put(bytes(key + ":name"), bytes(owner_id));
				batch.put(bytes(key + ":address"), bytes(r.getAddress()));
				batch.put(bytes(key + ":city"), bytes(r.getCity()));
				batch.put(bytes(key + ":description"), bytes(r.getDescription()));
				batch.put(bytes(key + ":genre"), bytes(r.getGenre().toString()));
				batch.put(bytes(key + ":owner_id"), bytes(Integer.toString(r.getOwner().getId())));
				batch.put(bytes(key + ":opening_hours"), bytes(r.getOpeningHours().toString()));
				batch.put(bytes(key + ":price"), bytes(r.getPrice().toString()));
				batch.put(bytes(key + ":seats"), bytes(Integer.toString(r.getSeats())));
				
				db.write(batch);
			} catch(Exception e) {
				System.out.println(e.getMessage());
				return false;
			}
			
			return true;
		}
		return false;
	};
	
	
	public boolean insertReservation(Reservation_ r) {
		if(open()) {
			String restaurant_id = null;
			String user_id = null;
			String key = EntityAdapter.stringifyKey(r);
			
			try(DBIterator iter = db.iterator()){
				String userKey = null;
				String[] userKeySplit = null;
				
				//Cerco user id in user
				for(iter.seek(bytes("user:")); iter.hasNext(); iter.next()) {
					userKey = iter.peekNext().getKey().toString();
					userKeySplit = userKey.split(":");
					
					//Se raggiungo la fine degli utenti prima di trovare quello giusto non aggiungo (manca l'utente)
					if(!iter.peekNext().toString().equals("user")) return false;
					if(userKeySplit[userKeySplit.length -1] == "username" ) {
						if(iter.peekNext().getValue().toString().equals(r.getUser().getUsername()) ) {
							user_id = userKeySplit[1];
							break;
						};
					};
					
				};
				
				//Cerco restaurant_name in restaurant
				for(iter.seek(bytes("restaurant:")); iter.hasNext(); iter.next()) {
					userKey = iter.peekNext().getKey().toString();
					userKeySplit = userKey.split(":");
					
					//Se raggiungo la fine degli utenti prima di trovare quello giusto non aggiungo (manca l'utente)
					if(!iter.peekNext().toString().equals("restaurant")) return false;
					if(userKeySplit[userKeySplit.length -1] == "name" ) {
						if(iter.peekNext().getValue().toString().equals(r.getRestaurant().getName()) ) {
							restaurant_id = userKeySplit[1];
							break;
						};
					};
				};
				
				
			} catch (Exception e) {
				System.out.println(e.getMessage());
				return false;
			};
			
			try(WriteBatch batch = db.createWriteBatch()){
			
				batch.put(bytes(key + ":user_id"), bytes(user_id));
				batch.put(bytes(key + ":restaurant_id"), bytes(restaurant_id));
				batch.put(bytes(key + ":date"), bytes(r.getDate().toString()));
				batch.put(bytes(key + ":seats"), bytes(Integer.toString(r.getSeats())));
				batch.put(bytes(key + ":hour"), bytes(r.getTime().toString()));
								
				db.write(batch);
			} catch(Exception e) {
				System.out.println(e.getMessage());
				return false;
			}
			
			return true;
		}
		return false;
	};
	
	public boolean delete(Entity_ entity){
		if(entity == null) return false;
		if(entity instanceof Restaurant_) {
			Restaurant_ r = ((Restaurant_) entity);
			return deleteRestaurant(r);
		};
		if(entity instanceof User_) {
			User_ u = ((User_) entity);
			return deleteUser(u);
		};
		if(entity instanceof Reservation_) {
			Reservation_ r = ((Reservation_) entity);
			return deleteReservation(r);
		};
		
		//refresh snapshot
		
		return false;};
	
		//NON USATA
	public boolean deleteUser(User_ u) {
		
		if(open()) {
			String key = EntityAdapter.stringifyKey(u);
			List<Reservation_> lr = null;
			List<Restaurant_> lrst = null;
			
			if(u.isOwner()) {
				lr = getOwnerReservationList(u.getId());
				lrst = getOwnerRestaurantList(u.getId());
				if (lr==null || lrst == null) return false;
				
				try(WriteBatch batch = db.createWriteBatch()) {
					
					batch.delete(bytes(key + ":username"));
					batch.delete(bytes(key + ":password"));
					
					for(Reservation_ r: lr) {
						if(!deleteReservation(r)) return false;
					};
					
					for(Restaurant_ r: lrst) {
						if(!deleteRestaurant(r)) return false;
					};
					
					db.write(batch);
					return true;
								
				} catch (Exception e) {
					System.out.println(e.getMessage());
					return false;
				}
		} else {
			lr = getReservationListCustomer(u.getId());
			if(lr == null) return false;
			try(WriteBatch batch = db.createWriteBatch()) {
				
				batch.delete(bytes(key + ":username"));
				batch.delete(bytes(key + ":password"));
				
				for(Reservation_ r: lr) {
					if(!deleteReservation(r)) return false;
				};
								
				db.write(batch);
				return true;
							
			} catch (Exception e) {
				System.out.println(e.getMessage());
				return false;
			}
		}
		}
		return false;
	}
		
		
		//NON USATA
	public boolean deleteRestaurant(Restaurant_ r) {
		if(open()) {
			String key = EntityAdapter.stringifyKey(r);
			try(WriteBatch batch = db.createWriteBatch()){
				batch.delete(bytes(key + ":owner_id"));
				batch.delete(bytes(key + ":opening_hours"));
				batch.delete(bytes(key + ":seats"));
				batch.delete(bytes(key + ":genre"));
				batch.delete(bytes(key + ":address"));
				batch.delete(bytes(key + ":city"));
				batch.delete(bytes(key + ":description"));
				batch.delete(bytes(key + ":price"));
				batch.delete(bytes(key + ":name"));
				
				db.write(batch);
			} catch (Exception e) {
				System.out.println(e.getMessage());
				return false;
			}
			
		};
		return false;
	};
		
		
	public boolean deleteReservation(Reservation_ r) {
		if(open()) {
			String key = EntityAdapter.stringifyKey(r);
			try(WriteBatch batch = db.createWriteBatch()){
				batch.delete(bytes(key + ":seats"));
				batch.delete(bytes(key + ":hour"));
				batch.delete(bytes(key + ":seats"));
				batch.delete(bytes(key + ":restaurant_id"));
				batch.delete(bytes(key + ":user_id"));
				
				db.write(batch);
			} catch (Exception e) {
				System.out.println(e.getMessage());
				return false;
			}
			
		};
		return false;
	};
	
	
	
	//UPDATE e INSERT sono la medesima operazione
	public boolean update(Entity_ entity){
		
		if(entity == null) return false;
		
		if(entity instanceof Restaurant_) {
			Restaurant_ r = ((Restaurant_) entity);
			return insertRestaurant(r);
		};
		
		if(entity instanceof User_) {
			User_ u = ((User_) entity);
			return insertUser(u);
		};
		
		if(entity instanceof Reservation_) {
			Reservation_ r = ((Reservation_) entity);
			return insertReservation(r);
		};
		
		//refresh snapshot
		return false;
	};	
	
//	public Restaurant_ checkSeats(Restaurant_ r) {
//		if(r != null) {
//			if(open()) {
//				//List<Restaurant_> lr = getReservationList()
//			}
//		}
//	}
//	
	public boolean close(){
		try {
			db.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			return false;
		}
	}



}
