.class public IfCondition
.super java/lang/Object


.method public <init>()V
    aload_0
    invokespecial java/lang/Object/<init>()V
    return
.end method

.method public static main([Ljava/lang/String;)V
	.limit stack 2
	.limit locals 9

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
	ineg
	ifeq else1

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
	ineg
	istore 7

	iload 4
	ineg
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
	if_icmpeq else3

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
	ineg
	ifeq else4

	iconst_5
	istore 5

	goto endif4

	else4:
	iconst_0
	istore 5

	endif4:
	iload 5
	invokestatic io.println(I)V

	return
.end method

