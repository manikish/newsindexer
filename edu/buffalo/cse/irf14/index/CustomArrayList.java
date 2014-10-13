package edu.buffalo.cse.irf14.index;

import java.io.Serializable;
import java.util.ArrayList;

public class CustomArrayList extends ArrayList<TermDocumentFreq> implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1600378599893502851L;
	
	@Override
	public boolean contains(Object o) {
		// TODO Auto-generated method stub
		TermDocumentFreq term = null;        			
		for(int i=0;i<this.size();i++) {
			if((term=this.get(i)).getFileId()==o) {
				term.setFrequency(term.getFrequency()+1);
				return true;        					
			}
		}
		return false;
	}
}
