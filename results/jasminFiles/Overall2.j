.class public Overall2
.super java/lang/Object


.method public <init>()V
    aload_0
    invokespecial java/lang/Object/<init>()V
    return
.end method

.method public static main([Ljava/lang/String;)V
	.limit stack 3
	.limit locals 12

	bipush 10
	newarray int
	astore_1

	aload_1
	astore_3

	iconst_0
	istore 4

	iconst_0
	istore 5

	Loop13:
	aload_3
	arraylength
	istore 6

	iload 4
	iload 6
	if_icmplt Body13

	goto EndLoop13

	Body13:
	aload_3
	iload 4
	iaload
	istore 7

	aload_3
	iload 4
	iload 4
	iastore

	iinc 4 1

	goto Loop13

	EndLoop13:
	iload 4
	iconst_0
	if_icmpge else20

	iconst_0
	istore 4

	goto endif20

	else20:
	iconst_0
	istore 4

	Loop14:
	endif20:
	aload_3
	arraylength
	istore 8

	iload 4
	iload 8
	if_icmplt Body14

	goto EndLoop14

	Body14:
	aload_3
	iload 4
	iaload
	istore 9

	iload 5
	iload 9
	iadd
	istore 10

	iload 10
	istore 5

	iload 4
	iconst_0
	if_icmpge else21

	iconst_0
	istore 5

	goto endif21

	else21:
	iload 5
	iconst_0
	imul
	istore 5

	endif21:
	iinc 4 1

	goto Loop14

	EndLoop14:
	iload 5
	invokestatic io.println(I)V

	return
.end method

