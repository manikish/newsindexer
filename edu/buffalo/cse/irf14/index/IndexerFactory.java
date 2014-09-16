package edu.buffalo.cse.irf14.index;

/**
 * 
 * @author sumanth
 *
 */
public class IndexerFactory {
	
	private static IndexerFactory instance = null;
	
	public static final IndexerFactory getInstance() {
		if(instance==null)
			instance = new IndexerFactory();
		return instance;
	}
	
	public Writer getClassForIndex(IndexType type) {
		Writer myWriter = null;
		switch(type) {
		case AUTHOR: myWriter = null;
		case CATEGORY: myWriter = null;
		case PLACE: myWriter = null;
		default: myWriter = new TermIndexWriter();
		}
		return myWriter;
	}
}
