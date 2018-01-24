/*
Chandler Griscom
Program Translation
For Dr. Baas
Lex Exercise 1
23 Jan 2017

#1
Write a lex program that copies a file, replacing each non-empty sequence of white space by a single blank.  Replace multiple blank lines with a single blank line.
*/

%{
#include <stdio.h>
#include <string.h>
FILE *src, *dst;
%}
white [ \t]+
eol   [\r\n]+
%%
{white}   { fprintf(dst, " "); }
{eol}     { fprintf(dst, "\r\n"); }
.         { fprintf(dst, "%s", yytext); }
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
