package model;

//combines data and label info for ease of use

public class InfoPoint { // to be used with test and validation
	Data data;
	int label;

	public InfoPoint(Data infoData, int infoLabel) {
		this.label = infoLabel;
		this.data = infoData;
	}

	public InfoPoint() {}

	// getters and setters
	public int getLabel() { return this.label;}

	public void setLabel(int label) {this.label = label;}

	public Data getData() {return this.data;}

	public void setData(Data data) {this.data = data;}

	public String getAsciiData() {return this.data.getAsciiData();}

}
