#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "learn.h"

void freeMem();

int main(int argc, char *argv[]){

	// setup and error checking
	FILE *train, *data;
	char *fileName1, *fileName2, str1[6], str2[5];
	double **X, **X_T, **X_2, **W, **Y;
	int i, j, k, n, m;
	if(argc < 3){
		printf("error");
		return 1;
	}
	fileName1 = argv[1];
	fileName2 = argv[2];
	train = fopen(fileName1, "r");
	data = fopen(fileName2, "r");
	if(train == NULL || data == NULL){
		printf("error");
		return 1;
	}
	fscanf(train, "%s", str1);
	fscanf(data, "%s", str2);
	if(str1 == NULL || str2 == NULL || strcmp(str1, "train") != 0 || strcmp(str2, "data") != 0){
		printf("error");
		return 1;
	}
	if(fscanf(train, "%d", &k) == EOF || fscanf(train, "%d", &n) == EOF){
		printf("error");
		return 1;
	}
	// allocate memory for n x (k+1) training data matrix
	// and extract data from file
	X = (double **) malloc(n*sizeof(double *));
	Y = (double **) malloc(n*sizeof(double)); 
	if(X == NULL || Y == NULL){
		printf("error\n");
		return 1;
	}
	for(i = 0; i < n; i++){
		// X is n x (k+1)
		X[i] = (double *) malloc((k+1)*sizeof(double));
		for(j = 0; j < (k+1); j++)
			X[i][j] = 0;
		// Y is n x 1
		Y[i] = (double *) malloc(1*sizeof(double));
		Y[i][0] = 0;
		if(X[i] == NULL){
			printf("error\n");
			return 1;
		}
	}
	for(i = 0; i < n; i++){
		for(j = 0; j < (k+1); j++){
			if(j == 0){
				X[i][j] = 1;
			}
			else if(j == k){
				if(fscanf(train, "%lf", &X[i][j]) == EOF 
				    || fscanf(train, "%lf", &Y[i][0]) == EOF){
					printf("error\n");
					return 1;
				}
			}
			else{
				if(fscanf(train, "%lf", &X[i][j]) == EOF){
					printf("error\n");
					return 1;
				}
			}	
		}
	}
	X_T = transpose(X, n, (k+1));
	if(X_T == NULL){ printf("error"); return 1; }
	double **A = multiply(X_T, X, (k+1), n, (k+1));
	if(A == NULL){ printf("error"); return 1; }
	double **B = pInverse(A, (k+1));
	if(B == NULL){ printf("error"); return 1; }
	double **C = multiply(B, X_T, (k+1), (k+1), n);
	if(C == NULL){ printf("error"); return 1; }
	W = multiply(C, Y, (k+1), n, 1); 
	if(W == NULL){ printf("error"); return 1; }
	// extracts values from data and stores in X_2	
	if(fscanf(data, "%d", &k) == EOF || fscanf(data, "%d", &m) == EOF){
		printf("error\n");
		return 1;
	}
	// allocate memory for m x k matrix
	X_2 = (double **) malloc(m*sizeof(double *));
	if(X_2 == NULL){ printf("error"); return 1; }
	for(i = 0; i < m; i++)
		X_2[i] = (double *) malloc((k+1)*sizeof(double));
	for(i = 0; i < m; i++){
		X_2[i][0] = 1;
		for(j = 1; j < (k+1); j++){
			if(fscanf(data, "%lf", &X_2[i][j]) == EOF){
				printf("error\n");
				return 1;
			}
		}
	}
	double **out = multiply(X_2, W, m, (k+1), 1);
	printMatrix(out, m, 1);
	// cleanup and free memory
	freeMem(X, Y, n, X_2, m, X_T, k+1, A, B, C, W, out);	
	fclose(train);
	fclose(data);
	return 0;
}

void freeMem(double **X, double **Y, int n, double **X_2, 
		int m, double **X_T, int k_plus_1, double **A, 
		double **B, double **C, double **W, double **out){
	int i;
	for(i = 0; i < n; i++){
		free(X[i]);
		free(Y[i]);
	}
	free(X);
	free(Y);
	for(i = 0; i < m; i++){
		free(X_2[i]);
		free(out[i]);
	}		
	free(X_2);
	free(out);
	for(i = 0; i < k_plus_1; i++){
		free(X_T[i]);
		free(A[i]);
		free(B[i]);
		free(C[i]);
		free(W[i]);
	}
	free(X_T);
	free(A);
	free(B);
	free(C);	
	free(W);
}











