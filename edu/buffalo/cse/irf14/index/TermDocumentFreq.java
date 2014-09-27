package edu.buffalo.cse.irf14.index;

public class TermDocumentFreq {
	
	private String fileId;
	private Integer frequency = new Integer(0);
	
	public TermDocumentFreq(String fileId, Integer frequency) {
		this.fileId = fileId;
		this.frequency = frequency;
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
}
