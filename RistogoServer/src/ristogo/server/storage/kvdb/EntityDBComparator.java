package ristogo.server.storage.kvdb;

import org.iq80.leveldb.DBComparator;

public class EntityDBComparator implements DBComparator
{
	@Override
	public int compare(byte[] arg0, byte[] arg1)
	{
		String[] key1Components = new String(arg0).split(":", 3);
		String[] key2Components = new String(arg1).split(":", 3);
		String entityName1 = key1Components[0];
		String entityName2 = key2Components[0];
		int entityId1 = Integer.parseInt(key1Components[1]);
		int entityId2 = Integer.parseInt(key2Components[1]);
		String attributeName1 = null;
		if (key1Components.length > 2)
			attributeName1 = key1Components[2];
		String attributeName2 = null;
		if (key2Components.length > 2)
		attributeName2 = key2Components[2];
		int nameCompare = entityName1.compareTo(entityName2);
		if (nameCompare != 0)
			return nameCompare;
		int idCompare = entityId1 - entityId2;
		if (idCompare != 0)
			return idCompare;
		if (attributeName1 == null)
			return Integer.MIN_VALUE;
		if (attributeName2 == null)
			return Integer.MAX_VALUE;
		return attributeName1.compareTo(attributeName2);
	}

	@Override
	public String name()
	{
		return "ristogo.server.storage.kvdb.EntityDBComparator";
	}

	@Override
	public byte[] findShortestSeparator(byte[] start, byte[] limit)
	{
		return start;
	}

	@Override
	public byte[] findShortSuccessor(byte[] key)
	{
		return key;
	}
}
