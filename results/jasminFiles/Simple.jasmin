.class public Simple
.super java/lang/Object


.method public <init>()V
    aload_0
    invokespecial java/lang/Object/<init>()V
    return
.end method

.method public constInstr()I
	.limit stack 1
	.limit locals 2

	iconst_0
	istore_1

	iconst_4
	istore_1

	bipush 8
	istore_1

	bipush 14
	istore_1

	sipush 250
	istore_1

	sipush 400
	istore_1

	sipush 1000
	istore_1

	ldc 100474650
	istore_1

	bipush 10
	istore_1

	iload_1
	ireturn

.end method

.method public add_int_int(II)I
	.limit stack 2
	.limit locals 7

	aload_0
	invokevirtual Simple.constInstr()I
	istore_3

	iload_1
	iload_3
	iadd
	istore 4

	iload 4
	istore 5

	iload 5
	ireturn

.end method

.method public static main([Ljava/lang/String;)V
	.limit stack 4
	.limit locals 9

	bipush 20
	istore_1

	bipush 10
	istore_2

	new Simple
	astore_3

	aload_3
	dup
	invokespecial Simple.<init>()V
	astore_3

	aload_3
	astore 5

	aload 5
	iload_1
	iload_2
	invokevirtual Simple.add_int_int(II)I
	istore 6

	iload 6
	istore 7

	iload 7
	invokestatic io.println(I)V

	return
.end method

