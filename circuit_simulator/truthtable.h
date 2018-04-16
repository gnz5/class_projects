#ifndef truthtable
#define truthtable
struct variable{
	char name[25]; int value; int wasSet;
	struct variable *next; char type;
}; typedef struct variable var;
extern var *inputs; extern var *outputs; extern var *one; extern var *zero; extern int *powerOfTwo;
// returns base raised to the power exp
int power(int base, int exp){
	int temp = 1, i;
	for(i = 0; i < exp; i++)
		temp *= base;
	return temp;
}
// adds new var to linked list of outputs
void appendToOutputs(var *A){
	if(A == NULL){
		printf("appendToOutputs() received NULL argument \n");
		return;
	}
	var *ptr = outputs;
	while(ptr->next != NULL){
		ptr = ptr->next;
	}
	ptr->next = A;
}
void NOT(var *A, var *out){ out->value = 1-(A->value); out->wasSet = 1; }
void AND(var *A, var *B, var *out){ out->value = A->value & B->value; out->wasSet = 1; }
void OR(var *A, var *B, var *out){ out->value = A->value | B->value; out->wasSet = 1; }
void NAND(var *A, var *B, var *out){ out->value = 1-(A->value & B->value); out->wasSet = 1; }
void NOR(var *A, var *B, var *out){ out->value = 1-(A->value | B->value); out->wasSet = 1; }
void XOR(var *A, var *B, var *out){ out->value = A->value ^ B->value; out->wasSet = 1; } 
void PASS(var *A, var *out){ out->value = A->value; out->wasSet = 1; }
// returns 1 if the var is an unset temp var, otherwise returns 0
int isUnsetTemp(var *A){
	if(A == NULL)
		return 0;
	if(A->type == 't' && A->wasSet == 0)
		return 1;
	return 0;
}
// returns var if found, otherwise returns NULL
var *getVar(char *name){
	if(name == NULL){ return NULL; }
	if(strcmp(name, "1") == 0)
		return one;
	else if(strcmp(name, "0") == 0)
		return zero;
	var *ptr  = inputs;
	while(ptr != NULL){
		if(strcmp(ptr->name, name) == 0)
			return ptr;
		ptr = ptr->next;		
	}
	ptr = outputs;
	while(ptr != NULL){
		if(strcmp(ptr->name, name) == 0)
			return ptr;
		ptr = ptr->next;
	}
	return NULL;
}
// allocates memory for and initializes a new var struct
var *newTempVar(char *name){
	var *newVar = (var *) malloc(sizeof(var)); strcpy(newVar->name, name); newVar->wasSet = 0;
	newVar->type = 't'; newVar->next = NULL; appendToOutputs(newVar); return newVar;
}
// returns 0 if all inputs are defined and the output can be computed
// otherwise returns -1
int callOneInputFunc(char *func, char *in, char *out){
	//printf("calling %s with arguments: %s, %s \n", func, in, out);
	var *input = getVar(in), *output = getVar(out);
	// define a new temp variable
	if(output == NULL)
		output = newTempVar(out);
	if(input == NULL || input->type == 'o' || output->wasSet == 1)
		return -1;
	else if(output->wasSet == 1)
		return -1;
	if(strcmp(func, "NOT") == 0)
		NOT(input, output);
	else if(strcmp(func, "PASS") == 0)
		PASS(input, output);
	else{
		printf("invalid logical function: %s \n", func);
		return -1;
	}
	return 0;
}
// returns 0 if all inputs are defined and the output can be computed
// otherwise returns -1
int callTwoInputFunc(char *func, char *in1, char *in2, char *out){
	var *input1 = getVar(in1), *input2 = getVar(in2), *output = getVar(out);
	if(input1 == NULL || input2 == NULL)
		return -1;
	if( (input1->type == 't' && input1->wasSet == 0) || (input2->type == 't' && input2->wasSet == 0))
		return -1;
	if(output == NULL)
		output = newTempVar(out);
	else if(output->wasSet == 1)
		return -1;
	else if(strcmp(func, "AND") == 0)
		AND(input1, input2, output);
	else if(strcmp(func, "OR") == 0)
		OR(input1, input2, output);
	else if(strcmp(func, "NAND") == 0)
		NAND(input1, input2, output);
	else if(strcmp(func, "NOR") == 0)
		NOR(input1, input2, output);
	else if(strcmp(func, "XOR") == 0)
		XOR(input1, input2, output);
	return 0;
}
// takes n inputs and yields 2^n outputs
void DECODER(int n, int twoToTheN, char **inputs, char **out){
	int i, encoded_number = 0;
	var **dec_inputs, **dec_outputs;
	dec_inputs = (var **) malloc(n*sizeof(var *));
	dec_outputs = (var **) malloc(twoToTheN*sizeof(var *));
	// put inputs into an array of var *
	for(i = 0; i < n; i++){
		dec_inputs[i] = getVar(inputs[i]);
		// an unset temp variable was passed as input
		if(isUnsetTemp(dec_inputs[i]) == 1){
			free(dec_inputs); free(dec_outputs); return;	
		}
	}
	// put outputs into an array of var *
	for(i = 0; i < twoToTheN; i++){
		dec_outputs[i] = getVar(out[i]);
		if(dec_outputs[i] == NULL)
			dec_outputs[i] = newTempVar(out[i]);
	}
	// compute number encoded by inputs (most significant bit at index 0)
	for(i = 0; i < n; i++){
		if( (dec_inputs[i])->value == 1){
			encoded_number += powerOfTwo[n-1-i];
		}
	}
	// set the output corresponding to computed number to 1, all others 0
	for(i = 0; i < twoToTheN; i++){
		if(i == encoded_number)
			(dec_outputs[i])->value = 1;
		else
			(dec_outputs[i])->value = 0;
		(dec_outputs[i])->wasSet = 1;
	}
	free(dec_inputs);
	free(dec_outputs);	
}
// takes n select bits, twoToTheN input bits, and yields a single output
void MULTIPLEXER(int n, int twoToTheN, var **mul_select, var **mul_inputs, var *output){
	//printf("calling MULTIPLEXER with arguments %d %d select = ", n, twoToTheN);
	int i, encoded_number = 0;
	for(i = 0; i < n; i++){
		if( mul_select[i]->value == 1)
			encoded_number += powerOfTwo[n-1-i];
	}
	// set the output to the input corresponding to encoded_number
	output->value = mul_inputs[encoded_number]->value;
	output->wasSet = 1;
}
#endif







