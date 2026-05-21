#include <stdio.h>
#include <string.h>

// Flawfinder: Unsafe functions (strcpy, gets, sprintf)
void vulnerable_function(char *input) {
    char buffer[64];
    
    // Flawfinder will flag strcpy because it doesn't check buffer bounds
    strcpy(buffer, input); 
    
    // Flawfinder will flag gets because it is inherently insecure and deprecated
    char input_gets[10];
    gets(input_gets); 

    // Flawfinder will flag sprintf for possible buffer overflow
    char out_buf[128];
    sprintf(out_buf, "Output: %s", input);
}

int main(int argc, char **argv) {
    if (argc > 1) {
        vulnerable_function(argv[1]);
    }
    return 0;
}
