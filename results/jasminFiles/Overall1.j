.class public Overall1
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

	Loop11:
	aload_3
	arraylength
	istore 5

	iload 4
	iload 5
	if_icmplt Body11

	goto EndLoop11

	Body11:
	aload_3
	iload 4
	iaload
	istore 6

	aload_3
	iload 4
	iload 4
	iastore

	iinc 4 1

	goto Loop11

	EndLoop11:
	iconst_0
	istore 7

	iconst_0
	istore 4

	Loop12:
	aload_3
	arraylength
	istore 8

	iload 4
	iload 8
	if_icmplt Body12

	goto EndLoop12

	Body12:
	aload_3
	iload 4
	iaload
	istore 9

	iload 7
	iload 9
	iadd
	istore 10

	iload 10
	istore 7

	iinc 4 1

	goto Loop12

	EndLoop12:
	iload 7
	bipush 100
	if_icmpge else19

	iload 7
	invokestatic io.println(I)V

	goto endif19

	else19:
	iconst_0
	invokestatic io.println(I)V

	endif19:
	iconst_0
	istore 7

	return
.end method

