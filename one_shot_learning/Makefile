all: learn

learn: learn.c learn.h
	gcc -Wall -Werror -fsanitize=address learn.c -o learn

clean:
	rm -rf learn
