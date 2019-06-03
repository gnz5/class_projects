package model;

import java.util.ArrayList;

public class NaiveBayes implements ClassificationMethod {
	double [] confidence=null;
	int numObservation=0;
	int step=1;
	double[] pFeatures,pLabels;
	ProbFeatures[] pFeaturesOfLabels;
	float binaryThreshold=.3f;
	
	void InitializeProbFeatures(ExtractedInfoPoint[] trainingInfo)
	{
		
		for (int i=0;i<pFeaturesOfLabels.length;i++) {//generate pFeaturesOfLabels
			pFeaturesOfLabels[i]=new ProbFeatures();
			pFeaturesOfLabels[i].binaryThreshold=binaryThreshold;
			pFeaturesOfLabels[i].label=i;
		}
		for (int i=0;i<trainingInfo.length;i++) {
			pFeaturesOfLabels[trainingInfo[i].label].infoPoints.add(trainingInfo[i]);
		}
		for (ProbFeatures a:pFeaturesOfLabels) {
			a.GenerateProbabilitiesFromInfoPoints();
			
		}
		for (int i=0;i<pLabels.length;i++) {//generate pLabels
			pLabels[i]= (double)(pFeaturesOfLabels[i].infoPoints.size()*1.0/(trainingInfo.length));
			//System.out.println(pLabels[i]);
		}
		
		for (int i=0;i<pFeatures.length;i++) {//generate pFeatures
			pFeatures[i]=0;
			
			
			for (int j=0;j<pFeaturesOfLabels.length;j++) {
				if (pFeaturesOfLabels[j].probFeatures!=null) 
					pFeatures[i]+=pLabels[j]*pFeaturesOfLabels[j].probFeatures[i];
			}
			
		}
		
	}
	public void trainFaces(ExtractedInfoPoint[] trainingInfo,ExtractedInfoPoint[] validationInfo)
	{
		pLabels=new double[2];
		pFeaturesOfLabels=new ProbFeatures[2];
		InitializeProbFeatures(trainingInfo);//gives us P features given label, PLabels, 
		for (int i=0;i<trainingInfo.length;i++) {
			
		}
		
	}
	public void trainDigits(ExtractedInfoPoint[] trainingInfo,ExtractedInfoPoint[] validationInfo)
	{
		pLabels=new double[10];
		pFeaturesOfLabels=new ProbFeatures[10];
		InitializeProbFeatures(trainingInfo);//gives us P features given label
		
		
	}
	@Override
	public void train(ExtractedInfoPoint[] trainingInfo, ExtractedInfoPoint[] validationInfo) {
		boolean digits=false;
		pFeatures=new double[trainingInfo[0].getFeatures().length];
		
		for(int i=0; i<trainingInfo.length; i++){
			if (trainingInfo[i].label>1) {
				trainDigits(trainingInfo,validationInfo);
				digits=true;
				break;
			}
		}
		if (!digits)
			trainFaces(trainingInfo,validationInfo);
			
		//	validationInfo=trainingInfo[i].currentValue(); 
		//	confidence[i].add(trainingInfo); // confidence isn't declared (?)
		
		
		for(int i=0; i < validationInfo.length; i++){
		//	confidence[i].computeStats(); // confidence isnt declared
		}
	}

	@Override
	public int[] classify(ExtractedInfoPoint[] points) {
		//System.out.println(points.length);
		int[] classifies=new int[points.length];
		double[] probabilities=new double[pFeaturesOfLabels.length];
		double tempProduct=1;
		for (int k=0;k<points.length;k++) {
			for (int i=0;i<probabilities.length;i++) { //go through each label
				tempProduct=1;
				probabilities[i]=pLabels[i];//pLabel
				for (int j=0;j<pFeatures.length;j++) {
					//System.out.println(points[k].getFeatures()[i]);
					if (points[k].getFeatures()[j]>binaryThreshold) { //it's 1
						//System.out.println( pFeaturesOfLabels[i].probFeatures[j]);
						probabilities[i]*=pFeaturesOfLabels[i].probFeatures[j];//*P(label given features)
						probabilities[i]/=pFeatures[j];// divide by P(feature)
					}

					else { //it's 0
						//System.out.println(1-pFeaturesOfLabels[i].probFeatures[j]);
						probabilities[i]*=(1-pFeaturesOfLabels[i].probFeatures[j]);//*P(label given features)
						probabilities[i]/=(1-pFeatures[j]);
					}
				}
			}
			//probabilities[1]=1-probabilities[0];
			classifies[k]=findMax(probabilities);
			//System.out.println("blank space");
		}
		return classifies;
		
		// ### but it should return one of these dot products for every ExtractedInfoPoint in points
	}


	public double dotProduct (double[] a, double[] b) {
		double temp = 0;
		for (int i = 0; i < a.length; i++) {
			temp += a[i] * b[i];
		}
		return temp;
	}
	public int findMax(double[] arr)
	{
		double temp=0;
		int index=0;
		for (int i=0;i<arr.length;i++)
		{
			if (arr[i]>temp) {
				index=i;
				temp=arr[i];
			}
		}
		return index;
	}
}
