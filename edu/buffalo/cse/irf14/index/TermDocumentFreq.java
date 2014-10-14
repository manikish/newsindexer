package edu.buffalo.cse.irf14.index;

import java.io.Serializable;

public class TermDocumentFreq implements Serializable {
	
	private String fileId;
	private Integer frequency = new Integer(0);
	private Integer length  = new Integer(0);
	
	public TermDocumentFreq() {}
	
	public TermDocumentFreq(String fileId, Integer frequency, Integer length) {
		this.fileId = fileId;
		this.frequency = frequency;
		this.length = length;
	}
	
	public String getFileId() {
		return fileId;
	}
	public void setFileId(String fileId) {
		this.fileId = fileId;
	}
	public Integer getFrequency() {
		return frequency;
	}
	public void setFrequency(Integer frequency) {
		this.frequency = frequency;
	}	
	public void setLength(Integer length) {
		this.length = length;
	}
	public Integer getLength() {
		return length;
	}

}
