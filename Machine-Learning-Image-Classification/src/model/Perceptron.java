package model;

public class Perceptron implements ClassificationMethod {
	
	int maxIterations;
	double netSum;
	double threshold = 0;
	double bias = -.5;
	double learningRate = .04;
	double[] weights;
	double[] confidence;
	public int goalLabel=1;
	int maxTests=10000;
	int currTests=0;
	double validationAcc=10000;
	
	public Perceptron() {}

	@Override
	public void train(ExtractedInfoPoint[] trainingInfo, ExtractedInfoPoint[] validationInfo) {
		//System.out.println(trainingInfo.length);
		//System.out.println(validationAcc);
		int counter = 10000;
		double[] features;
		double result;
		weights = new double[trainingInfo[0].features.length];
		
		for (int i = 0; i < weights.length; i++) // initial equal weights
		{
			// weights[i]=1/weights.length;
			weights[i] = 0;
		}
		//for (int k = 0; k < maxTests; k++)// iterate twice
		do  {
			validationAcc=counter;
			//System.out.println(validationAcc);
		while (currTests<maxTests){
			
			
			counter = 0;
			for (int i = 0; i < trainingInfo.length; i++) // iterate through each traininginfo
			{
				netSum = 0;
				features = trainingInfo[i].getFeatures();
				result = dotProduct(features, weights) +bias- threshold;
				if (result > 0) {
					result = goalLabel;
				} else
					result = -1;
				//correctness = trainingInfo[i].label - result; // will be 1 if weights are too low and -1 if too high
				if (!(trainingInfo[i].label==goalLabel&&result==goalLabel||trainingInfo[i].label!=goalLabel&&result!=goalLabel)) {//wrong
				//{
				//if (correctness!=0) {
					// System.out.println(i);
					counter++;
					for (int j = 0; j < weights.length; j++) {
						if (result==goalLabel) //false positive
							weights[j] += learningRate * features[j] * -1;
						else
							weights[j] += learningRate * features[j] * 1;
					}
				}
				currTests++;
			}
			
			
		}
		
		counter = 0;
		for (int i = 0; i < validationInfo.length; i++) // iterate through each validationInfo
		{
			netSum = 0;
			features = validationInfo[i].getFeatures();
			if(features.length != weights.length){
				System.out.println("mismatch at i = " + i);
				System.out.println("features.length = " + features.length);
				System.out.println("weights.length = " + weights.length);
			}
			result = dotProduct(features, weights) + bias - threshold;
			if (result > 0) {
				result = goalLabel;
			} else
				result = -1;
			//correctness = validationInfo[i].label - result; // will be 1 if weights are too low and -1 if too high
			//if (correctness != 0) {// wrong
			if (!(validationInfo[i].label==goalLabel&&result==goalLabel||validationInfo[i].label!=goalLabel&&result!=goalLabel)){
				// System.out.println(i);
				counter++;
			}

		}
		
		
		//System.out.println(validationAcc);
		//System.out.println(counter);
		}while (validationAcc>((counter)));
		/*if (validationAcc>(counter-5)) {
			//System.out.println(validationAcc);
			//System.out.println(counter);
			validationAcc=counter;
			train(trainingInfo,validationInfo);
		}*/
		//System.out.println(counter);
	}

	@Override
	public int[] classify(ExtractedInfoPoint[] points) {
		confidence=new double[points.length];
		int counter = 0;
		double[] features;
		double result;
		double correctness;
		int[] classifications=new int[points.length];
		for (int i = 0; i < points.length; i++) // iterate through each validationInfo
		{
			features = points[i].getFeatures();
			confidence[i] = dotProduct(features, weights)+bias - threshold;
			if (confidence[i] > 0) {
				result = goalLabel;
			} else
				result = -1;
			classifications[i]=(int)result;
			
			correctness = points[i].label - classifications[i]; // will be 1 if weights are too low and -1 if too high
			if (!(points[i].label==goalLabel&&result==goalLabel||points[i].label!=goalLabel&&result!=goalLabel)){// wrong
			
				// System.out.println(i);
				counter++;
			}
		

		}

		//System.out.println(counter);
		//System.out.println(counter+" out of "+points.length +" wrong");
		return classifications;
	}

	// helper method
	public double dotProduct(double[] a, double[] b) {
		double temp = 0;
		for (int i = 0; i < a.length; i++) {
			temp += a[i] * b[i];
		}
		return temp;
	}

}
