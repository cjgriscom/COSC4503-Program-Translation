# copy any comments from the source code
 .data
# int s = 123;
s: .word 123
 .text
# s = 456;
 la $t0, s
 li $t8, 456
 sw $t8, 0($t0)
# cout << s;
 lw $a0, 0($t0)
 li $v0, 1
 syscall
# return 0
 li $v0, 10 # system call for exit
 syscall # program terminated.
 