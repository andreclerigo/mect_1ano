	.data
	.text
values:	.word 1, 2, 3, 4, 5, 6, 7, 8, 9, 10	; values to be added
nelem:	.word 10				; arrays size
sum:	.space 4				; sum of the array elements
	.global main

main:	addi 	r1, r0, nelem 			; r1 = add(nelem)
	nop
	nop
	lw 	r1, 0(r1) 			; r1 = val(nelem)
	addi 	r2, r0, values 			; r2 = add(values[0])
	addi 	r3, r0, 0 			; r3 = sum = 0 (partial sum of
 						; the array elements)
	addi 	r4, r0, 0 			; r4 = i = 0 (counting variable)
loop: 	lw 	r5, 0(r2) 			; r5 = val(values[i])
	nop
	nop
	add 	r3,  r3, r5 			; r3 = sum + val(values[i])
	addi 	r4, r4, 1			; r4 = i = i + 1
	addi 	r2, r2, 4 			; r2 = add(values[i])
	nop
	sub 	r7, r1, r4 			; r7 = val(nelem) - i
	nop
	nop
	bnez 	r7,	loop 			; are all array elements summed?
	nop
	nop
	nop
	addi 	r6, r0, sum 			; r6 = add(sum)
	nop
	nop
	sw 	0(r6), r3 			; save sum of the array elements
	trap	0				; end of program