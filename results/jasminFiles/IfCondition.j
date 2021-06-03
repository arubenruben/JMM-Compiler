.class public IfCondition
.super java/lang/Object


.method public <init>()V
    aload_0
    invokespecial java/lang/Object/<init>()V
    return
.end method

.method public static main([Ljava/lang/String;)V
	.limit stack 2
	.limit locals 14

	bipush 10
	istore_1

	bipush 20
	istore_2

	iconst_1
	istore_3

	iconst_0
	istore 4

	iload_1
	bipush 15
	if_icmpge else

	iload_1
	istore 5

	goto endif

	else:
	iload_2
	istore 5

	endif:
	iload 5
	invokestatic io.println(I)V

	iconst_1
	iconst_1
	ixor
	ifne else1

	iconst_5
	istore 5

	goto endif1

	else1:
	bipush 6
	istore 5

	endif1:
	iload 5
	invokestatic io.println(I)V

	iload_3
	iconst_1
	ixor
	istore 7

	iload 4
	iconst_1
	ixor
	istore 8

	iload 7
	iload 8
	ior
	ifne else2

	iconst_2
	istore 5

	goto endif2

	else2:
	iconst_0
	istore 5

	endif2:
	iload 5
	invokestatic io.println(I)V

	iload_3
	iload_3
	iand
	ifne else3

	iconst_0
	istore 5

	goto endif3

	else3:
	iconst_2
	istore 5

	endif3:
	iload 5
	invokestatic io.println(I)V

	iload_3
	iconst_1
	ixor
	ifne else4

	iconst_5
	istore 5

	goto endif4

	else4:
	iconst_0
	istore 5

	endif4:
	iload 5
	invokestatic io.println(I)V

	iconst_5
	bipush 10
	if_icmpge LabelFalse32
	iconst_1
	goto LabelContinue32
	LabelFalse32:
	iconst_0
	LabelContinue32:
	istore 9

	iload 9
	iconst_1
	ixor
	istore 10

	bipush 6
	bipush 20
	if_icmpge LabelFalse34
	iconst_1
	goto LabelContinue34
	LabelFalse34:
	iconst_0
	LabelContinue34:
	istore 11

	iload 11
	iconst_1
	ixor
	istore 12

	iload 10
	iload 12
	iand
	istore 13

	iload 13
	iload 13
	iand
	ifne else5

	iconst_5
	istore 5

	goto endif5

	else5:
	iconst_0
	istore 5

	endif5:
	iload 5
	invokestatic io.println(I)V

	return
.end method

