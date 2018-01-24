/*
Chandler Griscom
Program Translation
For Dr. Baas
Lex Exercise 3
23 Jan 2017

#3
Write a lex program that converts a file to “Ubbi Dubbi” Assume the file is a sequence of words separated by whitespace. Every time you encounter a word:
     Add “ub” before each series of vowels.
     For example “Hi, how are you?” becomes “Hubi, hubow ubarube yubou?”
*/

%{
#include <stdio.h>
#include <string.h>
FILE *src, *dst;
%}
capsvowels [AEIOU]+[aAeEiIoOuU]*
vowels     [aAeEiIoOuU]+
%%
{capsvowels}  { fprintf(dst, "Ub%c%s", yytext[0] + ('a' - 'A'), yytext+1); }
{vowels}      { fprintf(dst, "ub%s", yytext); }
.             { fprintf(dst, "%s", yytext); }
\n            { fprintf(dst, "%s", yytext); }
%%

int main(int argc, char** argv) {
  if (argc != 3) {
    fprintf(stderr, "Expected two arguments.\n");
    fprintf(stderr, "Usage: ./{program} {inputFile} {outputFile}\n");
    return 1;
  }
  src=fopen(argv[1], "r+");
  dst=fopen(argv[2], "w+");
  yyin=src;
  yylex();
  return 0;
}
