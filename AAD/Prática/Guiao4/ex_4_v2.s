	.data
	.text
values:	.word 1, 2, 3, 4, 5, 6, 7, 8, 9, 10
nelem:	.word 10
	.global main

main:
	addi	r1, r0, nelem	; r1 = &nelem
	nop
	nop
	lw	r1, 0(r1)	; r1 = nelem
	addi	r2, r0, values	; r2 = &values[i]
	addi	r3, r0, 0	; r3 = 0 (counter)
	addi	r8, r1, -1	; r8 = nelem-1
	nop
	nop

for1:	slt	r9, r3, r8	; r9 = (i < nelem-1)
	nop
	nop
	beqz	r9, end		; jump to end if (i >= nelem-1)
	nop
	nop
	nop
				; for (; i < nelem-1 ;) { 
	addi	r6, r2, 4	; 	r6 = &values[j]
	lw	r4, 0(r2)	; 	r2 = values[i]
	addi	r5, r3, 1	; 	r5 = i+1 (counter)
				
				;	for (; j < nelem ;) { 	
for2:	lw	r7, 0(r6)	; 		r7 = values[j];
	nop
	nop
	slt	r9, r4, r7	; 		r9 = (values[i]) < values[j])
	nop
	nop
	beqz	r9, endfor	; 		jump to endfor if (values[i] >= values[j])
				;		if (values[i]) < values[j]) jump endfor
	nop
	nop
	nop
	add	r9, r4, r0	; 		r9 = values[i]
	add	r4, r7, r0  	; 		values[i] = values[j]
	nop
	add	r7, r9, r0	; 		values[j] = tmp
	sw 	0(r2), r4	; 		values[i] = r4
	nop
	sw	0(r6), r7	; 		values[j] = r7

endfor:	addi	r5, r5, 1	; 		j++
	addi	r6, r6, 4	; 		r6 = &values[j]
	nop
	slt	r9, r5, r1	; 		r9 = (j < nelem)
	bnez	r9, for2	; 	}	jump to for2 if (j >= nelem)
	nop
	nop
	addi	r3, r3, 1	;	i++
	addi	r2, r2, 4	;	r2 = &values[i]
	j	for1		; }
	nop
	nop
	nop

end:	trap	0		; end of program
