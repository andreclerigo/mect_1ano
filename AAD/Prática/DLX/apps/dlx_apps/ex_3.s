	.data
	.text
arr:	.word 1, 2, 3, 4, 5, 6, 7, 8, 9, 10
	.global main

main:
	addi	r1, r0, arr	; r1 = &arr;
	addi	r2, r0, 10	; r2 = 10;
	addi	r3, r0, 0	; r3 (sum) = 0;

for:	lw	r4, 0(r1)	; r4 = arr[i];
	add	r3, r3, r4	; sum += r3;
	subi	r2, r2, 1	; r2--;
	addi	r1, r1, 4	; arr++;
	bnez	r2, for		;

	trap	0		; end of program