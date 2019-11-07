package ristogo.common.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.security.NoTypePermission;
import com.thoughtworks.xstream.security.NullPermission;
import com.thoughtworks.xstream.security.PrimitiveTypePermission;

import ristogo.common.entities.Entity;

public class Message implements Serializable
{
	private static final long serialVersionUID = -5181705765357502182L;
	
	private final List<Entity> entities = new ArrayList<>();
	
	public Message(Entity... entities)
	{
		if (entities != null)
			for (Entity entity: entities)
				this.entities.add(entity);
	}

	public String toXML()
	{
		XStream xs = new XStream();
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + xs.toXML(this);
	}
	
	public static Message fromXML(String xml)
	{
		XStream xs = new XStream();
		xs.addPermission(NoTypePermission.NONE);
		xs.addPermission(NullPermission.NULL);
		xs.addPermission(PrimitiveTypePermission.PRIMITIVES);
		xs.allowTypeHierarchy(Collection.class);
		xs.allowTypesByWildcard(new String[] {
		    "ristogo.common.entities.**",
		    "ristogo.common.net.**"
		});
		return (Message)xs.fromXML(xml);
	}
	
	public void send(DataOutputStream output)
	{
		String xml = this.toXML();
		System.err.println("SENDING:\n" + xml);
		try {
			output.writeUTF(xml);
		} catch (IOException ex) {
			Logger.getLogger(Message.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
	public static Message receive(DataInputStream input)
	{
		String xml;
		try {
			xml = input.readUTF();
			System.err.println("RECEIVED:\n" + xml);
			return fromXML(xml);
		} catch (IOException ex) {
			Logger.getLogger(Message.class.getName()).log(Level.SEVERE, null, ex);
			return null;
		}
	}
	
	public List<Entity> getEntities()
	{
		return entities;
	}
	
	public Entity getEntity(int index)
	{
		if (index < 0 || index >= getEntityCount())
			return null;
		return entities.get(index);
	}
	
	public Entity getEntity()
	{
		return getEntity(0);
	}
	
	public void addEntity(Entity entity)
	{
		entities.add(entity);
	}
	
	public int getEntityCount()
	{
		return entities.size();
	}
}
