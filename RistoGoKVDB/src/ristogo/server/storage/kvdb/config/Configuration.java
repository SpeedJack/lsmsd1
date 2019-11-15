package ristogo.server.storage.kvdb.config;

import org.iq80.leveldb.CompressionType;
import org.iq80.leveldb.Logger;
import org.iq80.leveldb.Options;

import ristogo.server.RistogoServer;


public class Configuration {
	private static Configuration singletonObj = null;
	
	private Options opt;
	private String path;
	private Logger logger = null;
	
	private Configuration() {
		this.opt = new Options();
		this.opt.cacheSize(100 * 1048576);
		this.opt.compressionType(CompressionType.SNAPPY);
		this.opt.logger(logger);
		this.opt.createIfMissing(true);
		this.path = "KVDB";
	}
	
	public static Configuration getConfig(){
		if (singletonObj == null)
			singletonObj = new Configuration();
		return singletonObj;
	}
	public Options getOptions() {return this.opt;};
	public String getPath() {return this.path;};
	public void setLogger(java.util.logging.Logger l) {this.logger = (Logger)l;}
}
