package model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Collections;
import java.util.concurrent.TimeUnit;;

public class DataClassifier {

	void Analysis() {
	}

	// helper/visualization methods, to be moved later to an actual GUI if necessary
	void PrintImage(InfoPoint a) // print image with pixels stored in info point data
	{
		System.out.println(a.getAsciiData());
	}

	void PrintImage(int[][] pixels) // print image with just pixel info
	{
		for (int i = 0; i < pixels.length; i++) {
			for (int j = 0; j < pixels.length; j++) {
				if (pixels[i][j] == 0)
					System.out.print(" ");
				else
					System.out.print("#");
			}
			System.out.println("");
		}
	}

	public static void main(String[] args) throws IOException {
		int FACE_DATA_WIDTH = 60;
		int FACE_DATA_HEIGHT = 70;
		int DIGIT_DATA_WIDTH = 28;
		int DIGIT_DATA_HEIGHT = 28;
		InfoPoint[] rawTrainingInfo, rawTestInfo, rawValidationInfo;
		ExtractedInfoPoint[] trainingInfo, testInfo, validationInfo;
		// load data
		Samples sampler = new Samples();
		int numTraining = 451;
		int numValidation = 300;
		int numTest = 150;
		int amtWrong = 0;
		rawTrainingInfo = new InfoPoint[numTraining];
		rawValidationInfo = new InfoPoint[numValidation];
		rawTestInfo = new InfoPoint[numTest];
		int[] guesses = new int[numTest];

		// do loading and basic feature extractors
		rawTrainingInfo = sampler.LoadFiles("facedatatrainlabels", "facedatatrain", numTraining, FACE_DATA_WIDTH,
				FACE_DATA_HEIGHT);
		rawValidationInfo = sampler.LoadFiles("facedatavalidationlabels", "facedatavalidation", numValidation,
				FACE_DATA_WIDTH, FACE_DATA_HEIGHT);
		rawTestInfo = sampler.LoadFiles("facedatatestlabels", "facedatatest", numTest, FACE_DATA_WIDTH,
				FACE_DATA_HEIGHT);

		// extraction
		trainingInfo = new ExtractedInfoPoint[rawTrainingInfo.length];
		validationInfo = new ExtractedInfoPoint[rawValidationInfo.length];
		testInfo = new ExtractedInfoPoint[rawTestInfo.length];
		for (int i = 0; i < rawTrainingInfo.length; i++) {
			trainingInfo[i] = new ExtractedInfoPoint(rawTrainingInfo[i]);
			trainingInfo[i].GenerateFaceCounters(rawTrainingInfo[i]);
		}
		for (int i = 0; i < rawValidationInfo.length; i++) {
			validationInfo[i] = new ExtractedInfoPoint(rawValidationInfo[i]);
			validationInfo[i].GenerateFaceCounters(rawValidationInfo[i]);
		}
		for (int i = 0; i < rawTestInfo.length; i++) {
			testInfo[i] = new ExtractedInfoPoint(rawTestInfo[i]);
			testInfo[i].GenerateFaceCounters(rawTestInfo[i]);
		}

		// create an array of training data where training[i] contains 10i% of the
		// training data
		// {0%, 10%, ..., 90%, 100%}
		// **** training[0] does not contain anything ****

		ArrayList<Integer> list = new ArrayList<Integer>();
		int percent;
		// make a list of unique random numbers
		for (int i = 0; i < rawTrainingInfo.length; i++) {
			list.add(Integer.valueOf(i));
		}

		ExtractedInfoPoint[] traingInfo_holder = Arrays.copyOf(trainingInfo, trainingInfo.length);
		ExtractedInfoPoint[] training;

		Perceptron perceptron;
		NaiveBayes nBayes;
		LogisticRegression logReg;
		int correct;
		float error_per_data;
		long startTime, endTime, timeElapsed;

		ExtractedInfoPoint[] trainingInformation;
		double[][] accuracies;
		double[] accuracyColumn;
		double perceptronAcc, bayesAcc, logisticAcc;
		double perceptronTime, bayesTime, logisticTime, perceptronDev, bayesDev, logisticDev;

		ExtractedInfoPoint[][] trainingInfos;

		System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
		System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
		for (int p = 1; p < 11; p++) {
			perceptronTime = 0;
			bayesTime = 0;
			logisticTime = 0;
			perceptronAcc = 0;
			bayesAcc = 0;
			logisticAcc = 0;

			percent = (rawTrainingInfo.length * p) / 10;
			trainingInfos = new ExtractedInfoPoint[][] { new ExtractedInfoPoint[percent],
					new ExtractedInfoPoint[percent], new ExtractedInfoPoint[percent], new ExtractedInfoPoint[percent],
					new ExtractedInfoPoint[percent], new ExtractedInfoPoint[percent] };
			accuracies = new double[trainingInfos.length][3];
			System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
			System.out.println("Now training FACES with " + percent + " data points (" + 10 * p + "%)");
			for (int k = 0; k < trainingInfos.length; k++) {
				trainingInformation = trainingInfos[k];
				Collections.shuffle(list);

				for (int q = 0; q < trainingInformation.length; q++)
					trainingInformation[q] = traingInfo_holder[list.get(q)];

				///////////////////////// Algorithm 1: Perceptron /////////////////////////

				perceptron = new Perceptron();
				startTime = System.nanoTime();
				perceptron.train(trainingInformation, validationInfo);
				endTime = System.nanoTime();
				// time elapsed in microseconds
				timeElapsed = (endTime - startTime) / 1000;
				perceptronTime += timeElapsed;
				
				guesses = perceptron.classify(testInfo);
				amtWrong = 0;
				for (int i = 0; i < numTest; i++) {
					if (!(guesses[i] == 1 && testInfo[i].label == 1 || guesses[i] != 1 && testInfo[i].label != 1))
						amtWrong++;
				}
				accuracies[k][0] = ((double) (numTest - amtWrong) / numTest) * 100.0;

				// System.out.println("Perceptron --> " + correct + "% (" + timeElapsed + "
				// microseconds, " + error_per_data + "% error, per data point)");

				///////////////////////// Algorithm 2: Naive Bayes /////////////////////////

				nBayes = new NaiveBayes();
				startTime = System.nanoTime();
				nBayes.train(trainingInformation, validationInfo);
				endTime = System.nanoTime();
				// time elapsed in microseconds
				timeElapsed = (endTime - startTime) / 1000;
				bayesTime += timeElapsed;
				// get time elapsed as a function of the amount of data used
				timeElapsed /= trainingInformation.length;
				guesses = nBayes.classify(testInfo);
				amtWrong = 0;
				for (int i = 0; i < numTest; i++) {
					if (guesses[i] != testInfo[i].label)
						amtWrong++;
				}
				accuracies[k][1] = ((double) (numTest - amtWrong) / numTest) * 100.0;
				// System.out.println("Naive Bayes --> " + correct + "% (" + timeElapsed + "
				// microseconds, " + error_per_data + "% error, per data point)");

				////////////////// Algoirthm 3: Logistic Regression /////////////////////////

				logReg = new LogisticRegression();
				startTime = System.nanoTime();
				logReg.train(trainingInformation, validationInfo);
				endTime = System.nanoTime();
				// time elapsed in microseconds
				timeElapsed = (endTime - startTime) / 1000;
				logisticTime += timeElapsed;
				// get time elapsed as a function of the amount of data used
				timeElapsed /= trainingInformation.length;
				guesses = logReg.classify(testInfo);
				amtWrong = 0;
				for (int i = 0; i < numTest; i++) {
					if (guesses[i] != testInfo[i].label)
						amtWrong++;
				}
				accuracies[k][2] = ((double) (numTest - amtWrong) / numTest) * 100.0;
				// System.out.println("Logistic Regression --> " + correct + "% (" + timeElapsed
				// + " microseconds, " + error_per_data + "% error, per data point)");
			}

			// calculations for avg accuracy/standard deviation/avg time
			for (int i = 0; i < accuracies.length; i++) {
				perceptronAcc += accuracies[i][0];
				bayesAcc += accuracies[i][1];
				logisticAcc += accuracies[i][2];
			}

			perceptronAcc /= trainingInfos.length;
			bayesAcc /= trainingInfos.length;
			logisticAcc /= trainingInfos.length;
			accuracyColumn = new double[trainingInfos.length];
			perceptronDev = standardDeviation(getColumn(accuracies, 0));
			bayesDev = standardDeviation(getColumn(accuracies, 1));
			logisticDev = standardDeviation(getColumn(accuracies, 2));
			perceptronTime /= trainingInfos.length;
			bayesTime /= trainingInfos.length;
			logisticTime /= trainingInfos.length;

			System.out.println("Perceptron --> " + (int) perceptronAcc + "% (" + (int) perceptronTime + " microseconds,"
					+ (float) perceptronDev + "% standard deviation)");
			System.out.println("Naive Bayes --> " + (int) bayesAcc + "% (" + (int) bayesTime + " microseconds,"
					+ (float) bayesDev + "% standard deviation)");
			System.out.println("Logistic Regression --> " + (int) logisticAcc + "% (" + (int) logisticTime
					+ " microseconds," + (float) logisticDev + "% standard deviation)");

		}

		// Now REPEAT THE WHOLE THING WITH DIGITS

		// loading
		numTraining = 499;
		numValidation = 300;
		numTest = 150;
		rawTrainingInfo = sampler.LoadFiles("traininglabels", "trainingimages", 499, DIGIT_DATA_WIDTH,
				DIGIT_DATA_HEIGHT);
		rawValidationInfo = sampler.LoadFiles("validationlabels", "validationimages", numValidation, DIGIT_DATA_WIDTH,
				DIGIT_DATA_HEIGHT);
		rawTestInfo = sampler.LoadFiles("testlabels", "testimages", numTest, DIGIT_DATA_WIDTH, DIGIT_DATA_HEIGHT);

		// extraction
		trainingInfo = new ExtractedInfoPoint[rawTrainingInfo.length];
		validationInfo = new ExtractedInfoPoint[rawValidationInfo.length];
		testInfo = new ExtractedInfoPoint[rawTestInfo.length];
		for (int i = 0; i < rawTrainingInfo.length; i++) {
			trainingInfo[i] = new ExtractedInfoPoint(rawTrainingInfo[i]);
			trainingInfo[i].GenerateDigitCounters(rawTrainingInfo[i]);
		}
		for (int i = 0; i < rawValidationInfo.length; i++) {
			validationInfo[i] = new ExtractedInfoPoint(rawValidationInfo[i]);
			validationInfo[i].GenerateDigitCounters(rawValidationInfo[i]);
		}
		for (int i = 0; i < rawTestInfo.length; i++) {
			testInfo[i] = new ExtractedInfoPoint(rawTestInfo[i]);
			testInfo[i].GenerateDigitCounters(rawTestInfo[i]);
		}

		list = new ArrayList<Integer>();
		// make a list of unique random numbers
		for (int i = 0; i < rawTrainingInfo.length; i++) {
			list.add(Integer.valueOf(i));
		}

		traingInfo_holder = Arrays.copyOf(trainingInfo, trainingInfo.length);

		System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
		System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");

		for (int p = 1; p < 11; p++) {

			perceptronTime = 0;
			bayesTime = 0;
			logisticTime = 0;
			perceptronAcc = 0;
			bayesAcc = 0;
			logisticAcc = 0;
			percent = (rawTrainingInfo.length * p) / 10;

			trainingInfos = new ExtractedInfoPoint[][] { new ExtractedInfoPoint[percent],
					new ExtractedInfoPoint[percent], new ExtractedInfoPoint[percent], new ExtractedInfoPoint[percent],
					new ExtractedInfoPoint[percent], new ExtractedInfoPoint[percent] };
			accuracies = new double[trainingInfos.length][3];
			System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
			System.out.println("Now training DIGITS with " + percent + " data points (" + 10 * p + "%)");

			for (int k = 0; k < trainingInfos.length; k++) {
				trainingInformation = trainingInfos[k];
				Collections.shuffle(list);

				for (int q = 0; q < trainingInformation.length; q++)
					trainingInformation[q] = traingInfo_holder[list.get(q)];

				///////////////////////// Algorithm 1: Perceptron /////////////////////////

				Perceptron[] numberPerceptrons = new Perceptron[10];
				int[][] numberClassifications = new int[numberPerceptrons.length][testInfo.length];
				startTime = System.nanoTime();
				for (int i = 0; i < numberPerceptrons.length; i++) {
					numberPerceptrons[i] = new Perceptron();
					numberPerceptrons[i].goalLabel = i;
					numberPerceptrons[i].train(trainingInformation, validationInfo);
					numberClassifications[i] = numberPerceptrons[i].classify(testInfo);
				}
				endTime = System.nanoTime();
				// time elapsed in microseconds
				timeElapsed = (endTime - startTime) / 1000;
				perceptronTime+=timeElapsed;

				ArrayList<int[]> potentialAnswers = new ArrayList<int[]>();
				double guessHigh = -1; // most likely answer in case of two rights
				double guessLow = 100;// most likely answer in case of everything being negated
				int guessLabel = 0;
				int guessLowLabel = 0;

				int secondCounter = 0;
				Random rand = new Random();
				amtWrong = 0;
				for (int j = 0; j < testInfo.length; j++) {
					potentialAnswers.clear();
					guessHigh = -1;
					for (int i = 0; i < numberClassifications.length; i++) {
						if (numberClassifications[i][j] != -1) {
							if (guessHigh == -1 || guessHigh < numberPerceptrons[i].confidence[j]) {
								guessHigh = numberPerceptrons[i].confidence[j];
								guessLabel = numberClassifications[i][j];
							}
							if (guessLow == 100 || guessLow > numberPerceptrons[i].confidence[j]) {
								guessLow = numberPerceptrons[i].confidence[j];
								guessLowLabel = numberClassifications[i][j];
							}
							// potentialAnswers.add(numberClassifications[i]);
						}
					}
					if (guessHigh == -1) // nothing is right
					{
						guessLabel = guessLowLabel;
					}
					if (guessLabel != testInfo[j].label)
						amtWrong++;

				}
				accuracies[k][0] = ((double) (numTest - amtWrong) / numTest) * 100.0;

				///////////////////////// Algorithm 2: Naive Bayes /////////////////////////

				nBayes = new NaiveBayes();
				startTime = System.nanoTime();
				nBayes.train(trainingInformation, validationInfo);
				endTime = System.nanoTime();
				// time elapsed in microseconds
				timeElapsed = (endTime - startTime) / 1000;
				bayesTime+=timeElapsed;
				// get time elapsed as a function of the amount of data used

				guesses = nBayes.classify(testInfo);
				amtWrong = 0;
				for (int i = 0; i < numTest; i++) {
					if (guesses[i] != testInfo[i].label)
						amtWrong++;
				}
				accuracies[k][1] = ((double) (numTest - amtWrong) / numTest) * 100.0;

				////////////////// Algoirthm 3: Logistic Regression /////////////////////////

				logReg = new LogisticRegression("multinomial");
				startTime = System.nanoTime();
				logReg.train(trainingInformation, validationInfo);
				endTime = System.nanoTime();
				// time elapsed in microseconds
				timeElapsed = (endTime - startTime) / 1000;
				logisticTime+=timeElapsed;
				// get time elapsed as a function of the amount of data used
				timeElapsed /= trainingInfo.length;
				// System.out.println(timeElapsed);
				guesses = logReg.classify(testInfo);
				// for(int u = 0; u < 20; u++)
				// System.out.println("guess = " + guesses[u] + ", validation = " +
				// testInfo[u].label);
				amtWrong = 0;
				for (int i = 0; i < numTest; i++) {
					if (guesses[i] != testInfo[i].label)
						amtWrong++;
				}
				accuracies[k][2] = ((double) (numTest - amtWrong) / numTest) * 100.0;
			}
			// calculations for avg accuracy/standard deviation/avg time
			for (int i = 0; i < accuracies.length; i++) {
				perceptronAcc += accuracies[i][0];
				bayesAcc += accuracies[i][1];
				logisticAcc += accuracies[i][2];
			}

			perceptronAcc /= trainingInfos.length;
			bayesAcc /= trainingInfos.length;
			logisticAcc /= trainingInfos.length;
			accuracyColumn = new double[trainingInfos.length];
			perceptronDev = standardDeviation(getColumn(accuracies, 0));
			bayesDev = standardDeviation(getColumn(accuracies, 1));
			logisticDev = standardDeviation(getColumn(accuracies, 2));
			perceptronTime /= trainingInfos.length;
			bayesTime /= trainingInfos.length;
			logisticTime /= trainingInfos.length;

			System.out.println("Perceptron --> " + (int) perceptronAcc + "% (" + (int) perceptronTime + " microseconds,"
					+ (float) perceptronDev + "% standard deviation)");
			System.out.println("Naive Bayes --> " + (int) bayesAcc + "% (" + (int) bayesTime + " microseconds,"
					+ (float) bayesDev + "% standard deviation)");
			System.out.println("Logistic Regression --> " + (int) logisticAcc + "% (" + (int) logisticTime
					+ " microseconds," + (float) logisticDev + "% standard deviation)");
		}

	}

	static double standardDeviation(double[] numbers) {
		double mean = 0;
		double deviation = 0;
		for (double a : numbers)
			mean += a;
		mean /= numbers.length;
		for (double a : numbers)
			deviation += Math.pow(a - mean, 2);
		deviation /= numbers.length;

		return Math.sqrt(deviation);

	}

	static double[] getColumn(double[][] numbers, int column) {
		double[] temp = new double[numbers.length];
		for (int i = 0; i < temp.length; i++) {
			temp[i] = numbers[i][column];
		}
		return temp;
	}

}
