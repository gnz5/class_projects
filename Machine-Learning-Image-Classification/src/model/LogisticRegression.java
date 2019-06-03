package model;

public class LogisticRegression implements ClassificationMethod {
	
	private double learning_rate;
	private double[] weights;
	private double[][] weights_multinomial;
	private int num_iterations;
	private String type;

	public LogisticRegression() {
		this.type = "binomial";
		this.num_iterations = 400;
	}

	public LogisticRegression(String type) {
		this.type = "multinomial";
		this.num_iterations = 500;
	}

	private int sigmoid(double x){ 
		//System.out.println(1.0/(1.0 + Math.exp(-1*x)));
		if (1.0/(1.0 + Math.exp(-1*x)) >= 0.5)
			return 1;
		return 0; 
	}

	private int getIndexOfMin(double[] arr){
		double max = arr[0];
		int index = 0;
		for(int i = 0; i < arr.length; i++)
			if(arr[i] < max){
				max = arr[i];
				index = i;
			}
		return index;
	}

	@Override
	public void train(ExtractedInfoPoint[] trainingInfo, ExtractedInfoPoint[] validationInfo){
		if(this.type.compareTo("binomial") == 0)
			this.train_binomial(trainingInfo, validationInfo);
		else
			this.train_multinomial(trainingInfo, validationInfo);
	}

	private void train_binomial(ExtractedInfoPoint[] trainingInfo, ExtractedInfoPoint[] validationInfo) {
		double[] features;
		double prediction = 0;
		int label;

		this.learning_rate = 0.02;
		weights = new double[trainingInfo[0].features.length];
		for (int i = 0; i < this.weights.length; i++)
			this.weights[i] = 0;

		for(int i = 0; i < this.num_iterations; i++){
			for(int j = 0; j < trainingInfo.length; j++){
				features = trainingInfo[j].getFeatures();
				label = trainingInfo[j].getLabel();
				prediction = this.dotProduct(features, this.weights);
				prediction = this.sigmoid(prediction);

				for(int k = 0; k < this.weights.length; k++)
					this.weights[k] = this.weights[k] + this.learning_rate * (label-prediction) * features[k];
			}
		}
	}

	private void train_multinomial(ExtractedInfoPoint[] trainingInfo, ExtractedInfoPoint[] validationInfo) {
		double[] features;
		double prediction = 0;
		int label;

		this.learning_rate = 0.1;
		this.weights_multinomial = new double[10][trainingInfo[0].features.length];
		for(int i = 0; i < weights_multinomial.length; i++)
			for(int j = 0; j < this.weights_multinomial[0].length; j++)
				this.weights_multinomial[i][j] = 0;

		for(int target = 0; target < 10; target++){
			for(int i = 0; i < this.num_iterations; i++){
				for(int j = 0; j < trainingInfo.length; j++){
					features = trainingInfo[j].getFeatures();
					label = trainingInfo[j].getLabel();
					if (label == target)
						label = 1;
					else
						label = 0;
					prediction = this.dotProduct(features, this.weights_multinomial[target]);
					prediction = this.sigmoid(prediction);
					for(int k = 0; k < this.weights_multinomial[target].length; k++)
						this.weights_multinomial[target][k] = this.weights_multinomial[target][k] + this.learning_rate * (label-prediction) * features[k];
				}
			}
		}
	}

	@Override
	public int[] classify(ExtractedInfoPoint[] points) {
		if(this.type.compareTo("binomial") == 0)
			return this.classify_binomial(points);
		else
			return this.classify_multinomial(points);
	}

	public int[] classify_binomial(ExtractedInfoPoint[] points) {
		double[] features;
		double dot;
		int[] classifications=new int[points.length];

		for (int i = 0; i < classifications.length; i++)
			classifications[i] = 0;
		
		for(int i = 0; i < points.length; i++){
			features = points[i].getFeatures();
			dot = dotProduct(features, this.weights);
			classifications[i] = this.sigmoid(dot);
		}
		return classifications;
	}

	public int[] classify_multinomial(ExtractedInfoPoint[] points) {
		int count = 0;
		double[] features;
		double[] dot = new double[10];
		int[] classifications=new int[points.length];

		for (int i = 0; i < classifications.length; i++)
			classifications[i] = 0;
		
		for(int i = 0; i < points.length; i++){
			features = points[i].getFeatures();
			for(int j = 0; j < 10; j++){
				// compute the dot product and then subtract the correct label
				dot[j] = Math.abs(this.dotProduct(features, this.weights_multinomial[j]) - j);
			}
			classifications[i] = this.getIndexOfMin(dot);
		}
		return classifications;
	}

	public double dotProduct(double[] a, double[] b) {
		double temp = 0;
		for (int i = 0; i < a.length; i++) {
			temp += a[i] * b[i];
		}
		return temp;
	}

}
