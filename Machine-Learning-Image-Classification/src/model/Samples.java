package model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Samples {
	
	public InfoPoint[] LoadFiles(String labelsfilename, String datafilename, int numImages, int width, int height)
			throws IOException {

		InfoPoint[] info = new InfoPoint[numImages];
		Data[] datas = new Data[numImages];
		File file = new File(datafilename);
		FileReader fileReader = new FileReader(file);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		String line;
		int[][] arr;

		for (int i = 0; i < numImages; i++) {
			arr = new int[width][height];
			for (int j = 0; j < height; j++) {
				line = bufferedReader.readLine();
				for (int k = 0; k < width; k++) {
					if (line.charAt(k) == '#') // if its an edge
					{
						arr[k][height - j - 1] = 1;
					} else if (line.charAt(k) == '+') {
						arr[k][height - j - 1] = 2;
					} else
						arr[k][height - j - 1] = 0;
				}

			}
			Data temp = new Data(arr);
			datas[i] = temp;
		}

		bufferedReader.close();

		// labels
		int[] labels = new int[numImages];
		file = new File(labelsfilename);
		fileReader = new FileReader(file);
		bufferedReader = new BufferedReader(fileReader);
		for (int i = 0; i < numImages; i++) {
			labels[i] = Character.getNumericValue(bufferedReader.readLine().charAt(0));
		}
		bufferedReader.close();
		for (int i = 0; i < datas.length; i++) {
			info[i] = new InfoPoint(datas[i], labels[i]);
		}
		return info;
		// test by
	}
}
