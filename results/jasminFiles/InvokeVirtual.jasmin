.class public InvokeVirtual
.super java/lang/Object


.method public <init>()V
    aload_0
    invokespecial java/lang/Object/<init>()V
    return
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

.method public static main([Ljava/lang/String;)V
	.limit stack 4
	.limit locals 7

	new InvokeVirtual
	astore_1

	aload_1
	dup
	invokespecial InvokeVirtual.<init>()V
	astore_1

	aload_1
	astore_3

	aload_3
	iconst_5
	iconst_3
	invokevirtual InvokeVirtual.sum_int_int(II)I
	istore 4

	iload 4
	istore 5

	iload 5
	invokestatic io.print(I)V

	return
.end method

