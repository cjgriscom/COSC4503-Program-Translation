/*
Chandler Griscom
Program Translation
For Dr. Baas
Lex Exercise 2
23 Jan 2017

#2
Write a lex program that copies a C program, replacing each instance of the keywords short or int with long

I'm not sure if it was necessary but I make it avoid matching keywords in variable names.
*/

%{
#include <stdio.h>
#include <string.h>
FILE *src, *dst;
int lastCharWasVar = 0;
%}
short_kwd short
int_kwd   int
kwd       ({short_kwd}|{int_kwd})
kwd_break {kwd}[^_a-zA-Z0-9]
var       [_a-zA-Z0-9]
%%
{kwd_break}   { 
   if (lastCharWasVar == 0) fprintf(dst, "long%.1s", yytext+yyleng-1);
   else fprintf(dst, "%s", yytext);
   lastCharWasVar = 0;
}
{var}         { fprintf(dst, "%s", yytext); lastCharWasVar = 1; }
.             { fprintf(dst, "%s", yytext); lastCharWasVar = 0; }
\n            { fprintf(dst, "%s", yytext); lastCharWasVar = 0; }
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
