package model;

public class Data {
	int[][] pixels;

	public Data(int[][] pixelData) {
		this.pixels = pixelData;
	}

	// getters and setters
	public String getAsciiData() {
		int height = pixels[0].length;
		String temp = "";
		for (int i = 0; i < pixels[0].length; i++) {
			for (int j = 0; j < pixels.length; j++) {
				temp += (char) (pixels[j][height - i - 1] + '0');
			}
			temp += '\n';
		}
		return temp;
	}

	public int[][] getPixels() {return this.pixels;}

	public void setPixels(int[][] pixels) {this.pixels = pixels;}

	public int getPixel(int x, int y) {return this.pixels[x][y];}

}
