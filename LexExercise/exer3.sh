EXER=exer3 \
&& lex $EXER.l \
&& clang lex.yy.c -lfl -o $EXER \
&& ./$EXER "$EXER"_input.txt "$EXER"_output.txt \
&& cat "$EXER"_output.txt
