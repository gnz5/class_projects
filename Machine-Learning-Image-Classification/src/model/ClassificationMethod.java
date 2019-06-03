package model;

public interface ClassificationMethod {

	public void train (ExtractedInfoPoint[] trainingInfo, ExtractedInfoPoint[] validationInfo); 
	public int[] classify(ExtractedInfoPoint[] points); 

}
