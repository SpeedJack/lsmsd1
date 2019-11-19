package ristogo.server.storage.kvdb.config;

import org.iq80.leveldb.CompressionType;
import org.iq80.leveldb.Logger;
import org.iq80.leveldb.Options;

import ristogo.server.storage.kvdb.EntityDBComparator;
import ristogo.server.storage.kvdb.KVDBManager;
// TODO: remove this class. initialize configuration directly in KVDBManager.
public class Configuration
{
	private static Configuration singletonObj = null;

	private Options opt;
	private String path;
	private Logger logger = null;

	private Configuration()
	{
		this.opt = new Options();
		this.opt.cacheSize(100 * 1048576);
		this.opt.compressionType(CompressionType.NONE);
		this.opt.comparator(new EntityDBComparator());
		// this.opt.logger = new Logger(System.out);
		this.opt.createIfMissing(true);
		this.path = "KVDB";
		this.logger = new Logger() {
			public void log(String m)
			{
				java.util.logging.Logger.getLogger(KVDBManager.class.getName()).info(m);
			}
		};
		this.opt.logger(this.logger);

	}

	public static Configuration getConfig()
	{
		if (singletonObj == null)
			singletonObj = new Configuration();
		return singletonObj;
	}

	public Options getOptions()
	{
		return this.opt;
	};

	public String getPath()
	{
		return this.path;
	};
}
