#ifndef learn
#define learn

void rowScale(double **A, int i, double k, int len);
void rowAddition(double **A, int i, int j, double k, int len);

// dynamically allocates and returns the transpose of A
// if A is m x n, A_T will be n x m
double ** transpose(double **A, int m, int n){
	//printf("transpose\n");
	if(A == NULL || m < 1 || n < 1)
		return NULL;
	int i, j;
	double **A_T = (double **) malloc(n*sizeof(double *));
	for(i = 0; i < n; i++)
		A_T[i] = (double *) malloc(m*sizeof(double));
	for(i = 0; i < n; i++){
		for(j = 0; j < m; j++){
			A_T[i][j] = A[j][i];
		}
	}
	return A_T;
}

// dynamically allocates and returns the product of m x n matrix  A 
// and n x p matrix B
double ** multiply(double **A, double **B, int m, int n, int p){
	if(A == NULL || B == NULL || m < 1 || n < 1 || p < 1)
		return NULL;
	double **C = (double **) malloc(m*sizeof(double *));
	int i, j, k;
	for(i = 0; i < m; i++)
		C[i] = (double *) malloc(p*sizeof(double));
	for(i = 0; i < m; i++){
		for(j = 0; j < p; j++){
			for(k = 0; k < n; k++){
				C[i][j] += A[i][k] * B[k][j];
			}
		}
	}
	return C;
}

// dynamically allocates and returns the pseudo-inserve of A
// both A and A^-1 will be n x n
double ** pInverse(double **A, int n){
	if(A == NULL){
		printf("Null matrix passed to pInverse()\n");
		return NULL;
	}
	int i, j;
	double **A_copy = (double **) malloc(n*sizeof(double *));
	double **A_inv = (double **) malloc(n*sizeof(double *));
	for(i = 0; i < n; i++){
		A_copy[i] = (double *) malloc(n*sizeof(double));
		A_inv[i] = (double *) malloc(n*sizeof(double));
	}
	// initialize A to the identity matrix
	for(i = 0; i < n; i++){
		for(j = 0; j < n; j++){
			if(i == j)
				A_inv[i][j] = 1;
			else
				A_inv[i][j] = 0;
			A_copy[i][j] = A[i][j];
		}
	}
	// turn matrix into upper triangular
	for(i = 0; i < n; i++){ // 0,1,2,3,...,n-2
		if(A_copy[i][i] != 1){
			double temp = 1/A_copy[i][i];
			rowScale(A_copy, i, temp, n);
			rowScale(A_inv,  i, temp, n);
		}
		for(j = i+1; j < n; j++){ // 1,2,3,4,...,n-1
			if(A_copy[j][i] != 0){	
				double temp = -A_copy[j][i];
				rowAddition(A_copy, i, j, temp, n);
				rowAddition(A_inv,  i, j, temp, n);
			}			
		}
	}	
	
	// turn matrix into lower triangular/diagonal
	for(i = n-1; i >= 0; i--){ // n-1,...,2,1
        	for(j = i-1; j >= 0; j--){ // n-2,...,1,0
			if(A_copy[j][i] != 0){
				double temp = -A_copy[j][i];	
				rowAddition(A_copy, i, j, temp, n);	
				rowAddition(A_inv,  i, j, temp, n);	
			}
		}
	}
	// free the copy of A
	for(i = 0; i < n; i++)
		free(A_copy[i]);
	free(A_copy);
	return A_inv;
}

// multiplies each element of row i with length len in matrix A by k
void rowScale(double **A, int i, double k, int len){
	int x;
	for(x = 0; x < len; x++){
		A[i][x] *=  k;
	}
}

// adds k times row i to row j of matrix A both of whom have length len
void rowAddition(double **A, int i, int j, double k, int len){
	int x;
	for(x = 0; x < len; x++)
		A[j][x] += k*A[i][x];
}

// print an mxn matrix (for debugging)
void printMatrix(double **A, int m, int n){
	if(A == NULL){
		return;
	}
	int i, j;
	for(i = 0; i < m; i++){
		for(j = 0; j < n; j++){
			printf("%.0f", A[i][j]);
		}
		printf("\n");
	}
}

#endif
