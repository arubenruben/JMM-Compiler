.class public WhileAndIF
.super java/lang/Object


.method public <init>()V
    aload_0
    invokespecial java/lang/Object/<init>()V
    return
.end method

.method public static main([Ljava/lang/String;)V
	.limit stack 3
	.limit locals 13

	bipush 20
	istore_1

	bipush 10
	istore_2

	bipush 10
	newarray int
	astore_3

	aload_3
	astore 5

	iload_1
	iload_2
	if_icmpge else16

	iload_1
	iconst_1
	isub
	istore 6

	goto endif16

	else16:
	iload_2
	iconst_1
	isub
	istore 6

	endif16:
	Loop2:
	iconst_0
	iconst_1
	isub
	istore 7

	iload 7
	iload 6
	if_icmplt Body2

	goto EndLoop2

	Body2:
	aload 5
	iload 6
	iaload
	istore 8

	iload_1
	iload_2
	isub
	istore 9

	aload 5
	iload 6
	iload 9
	iastore

	iload 6
	iconst_1
	isub
	istore 6

	iload_1
	iconst_1
	isub
	istore_1

	iload_2
	iconst_1
	isub
	istore_2

	goto Loop2

	EndLoop2:
	iconst_0
	istore 6

	Loop3:
	aload 5
	arraylength
	istore 10

	iload 6
	iload 10
	if_icmplt Body3

	goto EndLoop3

	Body3:
	aload 5
	iload 6
	iaload
	istore 11

	iload 11
	invokestatic io.println(I)V

	iinc 6 1

	goto Loop3

	EndLoop3:
	iconst_0
	istore 6

	return
.end method

