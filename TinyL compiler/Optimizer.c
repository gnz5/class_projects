#include <assert.h>
#include <stdio.h>
#include <stdlib.h>
#include "InstrUtils.h"
#include "Utils.h"

int main()
{
	Instruction *head;

	head = ReadInstructionList(stdin);
	if (!head) {
		WARNING("No instructions\n");
		exit(EXIT_FAILURE);
	}
	/* YOUR CODE GOES HERE */
	Instruction *ptr  = head;
	char optimization_completed = 't';
	while(1){
		if(ptr == NULL){
			if(optimization_completed == 'f'){
				ptr = head;
				optimization_completed = 't';
				continue;
			}
			else{
				break;
			}
		}
		// check for instruction pattern
		if(ptr->opcode == LOADI){
			int reg1 = ptr->field1;
			if(ptr->next != NULL && ptr->next->opcode == LOADI){
				int reg2 = ptr->next->field1;
				if(ptr->next->next != NULL){
					int left_reg = ptr->next->next->field2;
					int right_reg = ptr->next->next->field3;
					int op = ptr->next->next->opcode;
					if(((reg1 == left_reg && reg2 == right_reg) || (reg1 == right_reg && reg2 == left_reg)) 
						&& (op == ADD || op == SUB || op == MUL)){
						// optimize
						Instruction *optimal = (Instruction *) malloc(sizeof(Instruction)); 
						optimal->opcode = LOADI;
						optimal->field1 = ptr->next->next->field1;
						optimal->field3 = 0xFFFF;
						if(op == 3)	
							optimal->field2 = ptr->field2 + ptr->next->field2;
 						else if(op == 4)
							optimal->field2 = ptr->field2 - ptr->next->field2;
						else
							optimal->field2 = ptr->field2 * ptr->next->field2;
						// substitute optimal instruction
						   // attach optimal to node after the pattern
						Instruction *next = ptr->next->next->next;
						if(next != NULL){
							next->prev = optimal;
							optimal->next = next;
						}
						else{
							optimal->next = NULL;
						}
						   // attach optimal to node before the pattern
						if(ptr->prev != NULL){
							ptr->prev->next = optimal;
							optimal->prev = ptr->prev;
						}
						else{
							head = optimal;
							optimal->prev = NULL;
						}
						// free replaced instructions
						free(ptr->next->next);
						free(ptr->next);
						free(ptr);
						//
						optimization_completed = 'f'; 
					}
				}
			}
		}
		ptr = ptr->next;
	}
	if (head) {
		PrintInstructionList(stdout, head);
		DestroyInstructionList(head);
	}
	return EXIT_SUCCESS;

}

