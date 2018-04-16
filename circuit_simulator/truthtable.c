#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "truthtable.h"
// skips number of lines in file equal to skip
void skipLines(FILE *file, int skip);
// reopen the file and skip first two lines
void reopenFile(FILE *file, char *fileName);
// prints the next row in the truth table
void printNextRowInTruthTable(var *inputs, var *outputs, int dash_h_option);
// verifies all outputs have been computed and program has completed
int checkAllOutputsDefined(var *out);
// sets next inputs
void nextInputs(var *inputs, var *outputs, int i, int numInputs);
// returns the nth bit of i
int returnIthBit(int i, int n);
// write contents of stdin to temp file
FILE *storeUserInputInTempFile();
// free all malloc calls
var *inputs, *outputs, *one, *zero; int *powerOfTwo;

int main(int argc, char *argv[]){	
	// setup and error checking
	FILE *file; char *fileName, t[25]; int i, numInputs, numOutputs, dash_h_option = 0;
	var **mul_inputs = NULL, **mul_select = NULL;// **dec_inputs = NULL, **dec_outputs = NULL;
	inputs = NULL; outputs = NULL;
	if(argc == 1){
		file = storeUserInputInTempFile();
		fileName = "userInput.txt";	
	}
	else if(argc == 2){
		if(strcmp(argv[1], "-h") == 0){
			dash_h_option = 1;
			file = storeUserInputInTempFile();
			fileName = "userInput.txt";
		}
		else{	
			fileName = argv[1];
			file = fopen(fileName, "r");
		}
	}
	else if(argc == 3){
		dash_h_option = 1;
		fileName = argv[2];
		file = fopen(fileName, "r");
	}
	else { return 0; }

	if(file == NULL) { return 0; }
	if(fscanf(file, "%s", t) == EOF)
		return 0; 
	fscanf(file, "%d", &numInputs);
	// set up array containing powers of two	
	powerOfTwo = (int *) malloc(32*sizeof(int));
	powerOfTwo[0] = 1;
	for(i = 1; i < 32; i++)
		powerOfTwo[i] = 2 * powerOfTwo[i-1];

	// setup linked list of input vars
	var *ptr = NULL;
	for(i = 0; i < numInputs; i++){
		var *temp = (var *) malloc(sizeof(var));
		if(inputs == NULL){
			inputs = temp; ptr = inputs;
		}
		else{
			ptr->next = temp; ptr = ptr->next;
		}
		temp->value = 0; temp->wasSet = 0; temp->type = 'i';
		temp->next = NULL; fscanf(file, "%s", temp->name);
	}
	if(inputs == NULL)
		return 1;
	fscanf(file, "%s", t); fscanf(file, "%d", &numOutputs);
	// setup linked list of output vars (will also hold temp vars)
	for(i = 0; i < numOutputs; i++){
		var *temp = (var *) malloc(sizeof(var));
		if(outputs == NULL){
			outputs = temp; ptr = outputs;
		}
		else{
			ptr->next = temp; ptr = ptr->next;
		}
		temp->wasSet = 0; temp->type = 'o';
		temp->next = NULL; fscanf(file, "%s", temp->name);
	}
	one = (var *) malloc(sizeof(var)); zero = (var *) malloc(sizeof(var));
	strcpy(one->name, "1"); one->value = 1; one->wasSet = 0; one->type = 'i'; one->next = NULL;
	strcpy(zero->name, "0"); zero->value = 0; zero->wasSet = 0; zero->type = 'i'; zero->next = NULL;
	///////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////
	if(dash_h_option == 1){
		// print truthtable header
		var *ptr = inputs;
		while(ptr != NULL){
			printf("%s ", ptr->name);
			ptr = ptr->next;
		}
		printf("|");
		ptr = outputs;
		while(ptr != NULL){
			printf(" %s", ptr->name);
			ptr = ptr->next;
		}
		printf("\n");
	}
	for(i = 0; i < powerOfTwo[numInputs]; i++){
		nextInputs(inputs, outputs, i, numInputs);
		while(1){
		while( fscanf(file, "%s", t) != EOF ){
			// 1 input functions
			if( strlen(t) < 7){
			if( strcmp(t,"NOT") == 0 || strcmp(t,"PASS") == 0 ){
				char in[25], out[25];
				fscanf(file, "%s", in); fscanf(file, "%s", out);
				if(strcmp(out, ":") == 0)
					fscanf(file, "%s", out);
				callOneInputFunc(t, in, out); 		
			}
			// 2 input functions
			else if( strcmp(t,"AND") == 0 || strcmp(t,"OR") == 0 || strcmp(t,"NAND") == 0 
				|| strcmp(t,"NOR") == 0 || strcmp(t,"XOR") == 0 ){
				char in1[25], in2[25], out[25];
				fscanf(file, "%s", in1); fscanf(file, "%s", in2); fscanf(file, "%s", out);
				if(strcmp(out, ":") == 0)
					fscanf(file, "%s", out);
				callTwoInputFunc(t, in1, in2, out);		
			}
			}
			else{
			// variable input functions	
			if( strcmp(t,"DECODER") == 0){
				int i, n, twoToTheN;
				fscanf(file, "%d", &n);
				char **dec_inputs = (char **) malloc(n*sizeof(char *));
				for(i = 0; i < n; i++){
					dec_inputs[i] = (char *) malloc(25*sizeof(char));
					fscanf(file, "%s", dec_inputs[i]);
					if(strcmp(dec_inputs[i], ":") == 0)
						fscanf(file, "%s", dec_inputs[i]);
				}
				twoToTheN = powerOfTwo[n];
				char **dec_outputs = (char **) malloc(twoToTheN*sizeof(char *));
				for(i = 0; i < twoToTheN; i++){
					dec_outputs[i] = (char *) malloc(25*sizeof(char));
					fscanf(file, "%s", dec_outputs[i]);
					if(strcmp(dec_outputs[i], ":") == 0)
						fscanf(file, "%s", dec_outputs[i]);
				}
				DECODER(n, twoToTheN, dec_inputs, dec_outputs);	
				for(i = 0; i < n; i++)
					free(dec_inputs[i]);
				for(i = 0; i < twoToTheN; i++)
					free(dec_outputs[i]);
				free(dec_inputs); free(dec_outputs);	
			}
			else if( strcmp(t, "MULTIPLEXER") == 0 ){
				int i, n, twoToTheN;
				fscanf(file, "%d", &n);
				twoToTheN = powerOfTwo[n];
				if(mul_inputs == NULL)
					mul_inputs = (var **) malloc(twoToTheN*sizeof(var *));
				if(mul_select == NULL)
					mul_select = (var **) malloc(n*sizeof(var *));
				for(i = 0; i < twoToTheN; i++){
					fscanf(file, "%s", t);
					if(strcmp(t, ":") == 0)
						fscanf(file, "%s", t);
					mul_inputs[i] = getVar(t);
					if(mul_inputs[i] == NULL){
						skipLines(file, 1);
						continue;
					}
				}
				for(i = 0; i < n; i++){
					fscanf(file, "%s", t);
					if(strcmp(t, ":") == 0)
						fscanf(file, "%s", t);
					mul_select[i] = getVar(t);
					if(mul_select[i] == NULL){
						skipLines(file, 1);
						continue;
					}
				}
				fscanf(file, "%s", t);
				if(strcmp(t, ":") == 0)
					fscanf(file, "%s", t);
				var *output = getVar(t);
				if(output == NULL)
					output = newTempVar(t);
				MULTIPLEXER(n, twoToTheN, mul_select, mul_inputs, output);
			}
			}	
		}
			if(checkAllOutputsDefined(outputs) == 0)
				break;
			else
				reopenFile(file, fileName);
		}
		printNextRowInTruthTable(inputs, outputs, dash_h_option);
	}
	fclose(file);
	return 0;
}

void skipLines(FILE *file, int skip){
	int i;
	char c;
	if(file == NULL)
		return;
	for(i = 0; i < skip; i++){
		while(fscanf(file, "%c", &c) != EOF){
			if(c == '\n')
				break;
		}
	}
}

void reopenFile(FILE *file, char *fileName){
	file = freopen(fileName, "r", file);
	int i; char c;
	for(i = 0; i < 2; i++){
		while(fscanf(file, "%c", &c) != EOF){
			if(c == '\n')
				break;
		}
	}
}

void printNextRowInTruthTable(var *inputs, var *outputs, int dash_h_option){
	var *ptr = inputs;
	// print input vars
	if(dash_h_option != 1){
		while(ptr != NULL){
			printf("%d ", ptr->value);
			ptr = ptr->next;
		}
		//if(outputs == NULL)
		//	return;
		printf("|");
		// print output var(s)
		ptr = outputs;
		while(ptr != NULL){
			if(ptr->type == 'o'){
				printf(" %d", ptr->value);
				ptr = ptr->next;
			}
			else
				break;
		}
	}
	else{
		int i;
		while(ptr != NULL){
			for(i = 0; i < strlen(ptr->name)-1; i++)
				printf(" ");
			printf("%d ", ptr->value);
			ptr = ptr->next;
		}
		//if(outputs == NULL)
		//	return;
		printf("|");
		ptr = outputs;
		while(ptr != NULL){
			if(ptr->type == 'o'){
				for(i = 0; i < strlen(ptr->name)-1; i++)
					printf(" ");
				printf(" %d", ptr->value);
				ptr = ptr->next;
			}
			else
				break;
		}
	}
	printf("\n");
}

int checkAllOutputsDefined(var *out){
	var *ptr = out;
	while(ptr != NULL){
		if(ptr->type == 't')
			return 0;
		if(ptr->wasSet == 0)
			return -1;
		ptr = ptr->next;
	}
	return 0;
}

void nextInputs(var *inputs, var *outputs, int i, int numInputs){
	int k;
	var *ptr = inputs;
	for(k = numInputs-1; k >= 0; k--){
		if( (i & powerOfTwo[k]) == powerOfTwo[k] )
			ptr->value = 1;
		else
			ptr->value = 0;
		ptr = ptr->next;
	}
	ptr = outputs;
	while(ptr != NULL){
		ptr->wasSet = 0;
		ptr = ptr->next;
	}
}

int returnIthBit(int i, int k){
	int pow = powerOfTwo[k];
	if((i & pow) == pow)
		return 0x1;
	else
		return 0x0;
}

FILE *storeUserInputInTempFile(){
	char str[26];
	FILE *file = fopen("userInput.txt", "w+");
	while(1){
		fgets(str, 25, stdin);
		if(feof(stdin) != 0)
			break;
		if(str != NULL)
			fputs(str, file);
	}
	fclose(file);
	FILE *temp = fopen("userInput.txt", "r");
	return temp;
}
