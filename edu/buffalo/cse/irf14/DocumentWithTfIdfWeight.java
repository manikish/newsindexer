package edu.buffalo.cse.irf14;

public class DocumentWithTfIdfWeight {

	private String fileId;
	private Double tfIdf = new Double(0.0);
	
	public DocumentWithTfIdfWeight() {}
	
	public DocumentWithTfIdfWeight(String fileId, Double tfIdf) {
		this.fileId = fileId;
		this.tfIdf = tfIdf;
	}
	
	public String getFileId() {
		return fileId;
	}
	public void setFileId(String fileId) {
		this.fileId = fileId;
	}
	public Double getTfIdf() {
		return tfIdf;
	}
	public void setFrequency(Double tfIdf) {
		this.tfIdf = tfIdf;
	}	
}
