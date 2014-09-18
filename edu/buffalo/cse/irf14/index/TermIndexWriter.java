package edu.buffalo.cse.irf14.index;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import edu.buffalo.cse.irf14.analysis.TokenStream;

/**
 * @author sumanth
*/
public class TermIndexWriter implements Writer{
	private TermBean myBean;
	private ObjectInputStream oistream;
	private ObjectOutputStream oostream;
	
	public TermBean getMyBean() {
		return myBean;
	}

	public void setMyBean(TermBean myBean) {
		this.myBean = myBean;
	}

	public TermIndexWriter() {
		// TODO Auto-generated constructor stub
		myBean = new TermBean();
		myBean.setData(new HashMap<String, List<String>>(27)); // initial capacity of 27
	}
	
	@Override
	public void write(TokenStream stream, String fileId) {
		// TODO Auto-generated method stub
		try {
			oistream = new ObjectInputStream(new FileInputStream(IndexWriter.indexDir+File.separator+"termindex"));
			oostream = new ObjectOutputStream() {
				protected void writeStreamHeader() throws IOException {};
			};
		} catch (FileNotFoundException e) {
			try {
				oostream = new ObjectOutputStream(new FileOutputStream(IndexWriter.indexDir+File.separator+"termindex"));
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		populateBean(stream, fileId);
		checkForMemory();
	}
	
	public void checkForMemory() {
		long totalMem = Runtime.getRuntime().totalMemory();
		long freeMem = Runtime.getRuntime().freeMemory();
		while(true) {
			if(freeMem == (20*totalMem/100)) {
				synchronized (this) {
					//write the Hashmap to DISK
					try {
						oostream = new ObjectOutputStream(new FileOutputStream(IndexWriter.indexDir+File.separator+"termindex"));
						oostream.writeObject(myBean);
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}

	public synchronized void populateBean(TokenStream stream, String fileId) {
		HashMap<String, List<String>> mydata = myBean.getData();
		LinkedList<String> myList = null;
		while(stream.hasNext()) {
			myList = (LinkedList<String>) mydata.get(stream.getCurrent().toString());
			if(myList == null) {
				myList = new LinkedList<String>();
			}
			myList.add(fileId);
			mydata.put(stream.getCurrent().toString(), myList);
		}
	}

}
