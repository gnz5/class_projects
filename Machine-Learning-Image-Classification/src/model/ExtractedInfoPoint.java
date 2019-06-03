package model;

public class ExtractedInfoPoint {

	double[] features;
	int label;


	public ExtractedInfoPoint(InfoPoint a) { // use InfoPoint to make extractedinfopoint
		this.label = a.getLabel();
	}

	public void GenerateDigitCounters(InfoPoint a) {
		int counter=0;
		int[][] pixels = a.getData().getPixels();
		features = new double[pixels.length * pixels[0].length];
		int width = pixels.length;
		int height = pixels[0].length;
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				if (pixels[i][j] != 0) {
					features[counter] = 1;
				} else
					features[counter] = 0;
				counter++;
			}
		}
	}

	public void GenerateFaceCounters(InfoPoint a) // main extraction occurs here
	{
		double temp1 = 0;
		double temp2 = 0;
		int[][] pixels = a.getData().getPixels();
		int width = pixels.length;
		int height = pixels[0].length;
		// OPTION 1 FOR FEATURES, LENGTH IS WIDTH+HEIGHT+1
		features = new double[width + height + 1];

		// density of 1's in each row and column and overall
		for (int i = 0; i < width; i++) // columns
		{
			temp1 = 0;
			for (int j = 0; j < height; j++) {
				if (pixels[i][j] == 1) {
					temp1++;
				}

			}
			features[i] = temp1 / height;
		}
		for (int j = 0; j < height; j++) // rows
		{
			temp1 = 0;
			for (int i = 0; i < width; i++) {
				if (pixels[i][j] == 1) {
					temp1++;
				}

			}
			features[width + j] = temp1 / width;
		}
		temp1 = 0;
		for (int j = 0; j < height; j++) // overall
		{
			for (int i = 0; i < width; i++) {
				if (pixels[i][j] == 1) {
					temp1++;
				}

			}

		}
		features[width + height] = temp1 / (width * height);
	}

	public double[] getFeatures() {return features;}

	public void setFeatures(double[] features) {this.features = features;}

	public int getLabel() {return label;}

	public void setLabel(int label) {this.label = label;}
}
