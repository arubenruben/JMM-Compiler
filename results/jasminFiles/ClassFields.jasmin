.class public ClassFields
.super java/lang/Object

.field protected a I

.method public <init>()V
    aload_0
    invokespecial java/lang/Object/<init>()V
    return
.end method

.method public classFieldAccess()I
	.limit stack 2
	.limit locals 5

	aload_0
	iconst_5
	putfield ClassFields/a I

	aload_0
	getfield ClassFields/a I
	istore_1

	iload_1
	invokestatic io.println(I)V

	iconst_0
	ireturn

.end method

.method public static main([Ljava/lang/String;)V
	.limit stack 2
	.limit locals 6

	new ClassFields
	astore_1

	aload_1
	dup
	invokespecial ClassFields.<init>()V
	astore_1

	aload_1
	astore_3

	aload_3
	invokevirtual ClassFields.classFieldAccess()I
	istore 4

	iload 4
	istore 5

	return
.end method

