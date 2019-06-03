package model;

import java.util.ArrayList;

public class ProbFeatures {
	public int label=-1;
	public double[] probFeatures;
	public ArrayList<ExtractedInfoPoint> infoPoints=new ArrayList<ExtractedInfoPoint>();
	float binaryThreshold=.3f; //affects faces which arent binary features to begin with
	
	

	public ProbFeatures() {
		// TODO Auto-generated constructor stub
	}
	void GenerateProbabilitiesFromInfoPoints()
	{
		
		double[] temp;
		int size=infoPoints.size();
		if (infoPoints.size()>0) {
		probFeatures=new double[infoPoints.get(0).getFeatures().length];
		for (int i=0;i<size;i++) {
			
			temp=infoPoints.get(i).getFeatures();
			for (int j=0;j<probFeatures.length;j++) {
				if (temp[j]>binaryThreshold) {
					probFeatures[j]++;
				}
			}
		}
		for (int j=0;j<probFeatures.length;j++)
		{
			probFeatures[j]/=size;
		}
		}
		else {
			//System.out.println("shouldn't be here");
			//probFeatures=new double[10000];
		}
	}

}
