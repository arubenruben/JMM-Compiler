.class public SimpleCalculator
.super java/lang/Object


.method public <init>()V
    aload_0
    invokespecial java/lang/Object/<init>()V
    return
.end method

.method public pow_int_int(II)I
	.limit stack 2
	.limit locals 5

	iconst_1
	istore_3

	iconst_0
	istore 4

	Loop:
	iload 4
	iload_2
	if_icmplt Body

	goto EndLoop

	Body:
	iload_3
	iload_1
	imul
	istore_3

	iinc 4 1

	goto Loop

	EndLoop:
	iload_3
	ireturn

.end method

.method public cubeVolume_int(I)I
	.limit stack 4
	.limit locals 5

	iconst_0
	istore_2

	iload_1
	iconst_0
	if_icmpge else6

	iconst_0
	istore_2

	goto endif6

	else6:
	aload_0
	iload_1
	iconst_3
	invokevirtual SimpleCalculator.pow_int_int(II)I
	istore_3

	iload_3
	istore_2

	endif6:
	iload_2
	ireturn

.end method

.method public sub_int_int(II)I
	.limit stack 2
	.limit locals 4

	iload_1
	iload_2
	isub
	istore_3

	iload_3
	ireturn

.end method

.method public div_int_int(II)I
	.limit stack 2
	.limit locals 4

	iload_1
	iload_2
	idiv
	istore_3

	iload_3
	ireturn

.end method

.method public factorial_int(I)I
	.limit stack 2
	.limit locals 5

	iconst_1
	istore_2

	iconst_1
	istore_3

	iload_1
	iconst_0
	if_icmpge else7

	iconst_1
	istore_2

	goto endif7

	else7:
	Loop1:
	iload_3
	iconst_1
	isub
	istore 4

	iload 4
	iload_1
	if_icmplt Body1

	goto EndLoop1

	Body1:
	iload_2
	iload_3
	imul
	istore_2

	iinc 3 1

	goto Loop1

	EndLoop1:
	endif7:
	iload_2
	ireturn

.end method

.method public combinations_int_int(II)I
	.limit stack 3
	.limit locals 11

	iconst_0
	istore_3

	iload_1
	iconst_1
	if_icmpge else8

	iconst_0
	istore_3

	goto endif8

	else8:
	iload_2
	iconst_1
	if_icmpge else9

	iconst_0
	istore_3

	goto endif9

	else9:
	iload_1
	iload_2
	if_icmpge else10

	iconst_0
	istore_3

	goto endif10

	else10:
	aload_0
	iload_2
	invokevirtual SimpleCalculator.factorial_int(I)I
	istore 4

	iload_1
	iload_2
	isub
	istore 5

	aload_0
	iload 5
	invokevirtual SimpleCalculator.factorial_int(I)I
	istore 6

	iload 4
	iload 6
	imul
	istore 7

	aload_0
	iload_1
	invokevirtual SimpleCalculator.factorial_int(I)I
	istore 8

	iload 8
	iload 7
	idiv
	istore 9

	iload 9
	istore_3

	endif8:
	endif9:
	endif10:
	iload_3
	ireturn

.end method

.method public sum_int_int(II)I
	.limit stack 2
	.limit locals 4

	iload_1
	iload_2
	iadd
	istore_3

	iload_3
	ireturn

.end method

.method public mult_int_int(II)I
	.limit stack 2
	.limit locals 4

	iload_1
	iload_2
	imul
	istore_3

	iload_3
	ireturn

.end method

.method public permutations_int_int(II)I
	.limit stack 3
	.limit locals 9

	iconst_0
	istore_3

	iload_1
	iconst_1
	if_icmpge else11

	iconst_0
	istore_3

	goto endif11

	else11:
	iload_2
	iconst_1
	if_icmpge else12

	iconst_0
	istore_3

	goto endif12

	else12:
	iload_1
	iload_2
	if_icmpge else13

	iconst_0
	istore_3

	goto endif13

	else13:
	aload_0
	iload_1
	invokevirtual SimpleCalculator.factorial_int(I)I
	istore 4

	iload_1
	iload_2
	isub
	istore 5

	aload_0
	iload 5
	invokevirtual SimpleCalculator.factorial_int(I)I
	istore 6

	iload 4
	iload 6
	idiv
	istore 7

	iload 7
	istore_3

	endif11:
	endif12:
	endif13:
	iload_3
	ireturn

.end method

.method public static main([Ljava/lang/String;)V
	.limit stack 4
	.limit locals 15

	new SimpleCalculator
	astore_1

	aload_1
	dup
	invokespecial SimpleCalculator.<init>()V
	astore_1

	aload_1
	astore_3

	aload_3
	bipush 7
	iconst_4
	invokevirtual SimpleCalculator.sum_int_int(II)I
	istore 4

	iload 4
	istore 5

	iload 5
	invokestatic io.println(I)V

	aload_3
	bipush 15
	bipush 9
	invokevirtual SimpleCalculator.sub_int_int(II)I
	istore 7

	iload 7
	istore 5

	iload 5
	invokestatic io.println(I)V

	aload_3
	iconst_3
	bipush 7
	invokevirtual SimpleCalculator.mult_int_int(II)I
	istore 8

	iload 8
	istore 5

	iload 5
	invokestatic io.println(I)V

	aload_3
	bipush 8
	iconst_4
	invokevirtual SimpleCalculator.div_int_int(II)I
	istore 9

	iload 9
	istore 5

	iload 5
	invokestatic io.println(I)V

	aload_3
	iconst_2
	iconst_3
	invokevirtual SimpleCalculator.pow_int_int(II)I
	istore 10

	iload 10
	istore 5

	iload 5
	invokestatic io.println(I)V

	aload_3
	bipush 6
	iconst_3
	invokevirtual SimpleCalculator.permutations_int_int(II)I
	istore 11

	iload 11
	istore 5

	iload 5
	invokestatic io.println(I)V

	aload_3
	bipush 6
	iconst_4
	invokevirtual SimpleCalculator.combinations_int_int(II)I
	istore 12

	iload 12
	istore 5

	iload 5
	invokestatic io.println(I)V

	aload_3
	iconst_4
	bipush 9
	invokevirtual SimpleCalculator.triangleArea_int_int(II)I
	istore 13

	iload 13
	istore 5

	iload 5
	invokestatic io.println(I)V

	aload_3
	iconst_3
	invokevirtual SimpleCalculator.cubeVolume_int(I)I
	istore 14

	iload 14
	istore 5

	iload 5
	invokestatic io.println(I)V

	return
.end method

.method public triangleArea_int_int(II)I
	.limit stack 2
	.limit locals 6

	iconst_0
	istore_3

	iload_1
	iconst_0
	if_icmpge else14

	iconst_0
	istore_3

	goto endif14

	else14:
	iload_2
	iconst_0
	if_icmpge else15

	iconst_0
	istore_3

	goto endif15

	else15:
	iload_1
	iload_2
	imul
	istore 4

	iload 4
	iconst_2
	idiv
	istore 5

	iload 5
	istore_3

	endif15:
	endif14:
	iload_3
	ireturn

.end method

