package edu.buffalo.cse.irf14.index;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class TermBean implements Serializable {

	/**
	 * generated serialVersionUID
	 */
	private static final long serialVersionUID = 6387894170982670880L;
	private HashMap<String, List<String>> data;
	
	public HashMap<String, List<String>> getData() {
		return data;
	}
	
	public void setData(HashMap<String, List<String>> data) {
		this.data = data;
	}
	
}
