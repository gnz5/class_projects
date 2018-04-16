all: truthtable

truthtable: truthtable.c truthtable.h
	gcc -g -Wall -Werror -fsanitize=address -o truthtable truthtable.c

clean:
	rm -f truthtable
