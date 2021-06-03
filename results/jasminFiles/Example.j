.class public Example
.super java/lang/Object


.method public <init>()V
    aload_0
    invokespecial java/lang/Object/<init>()V
    return
.end method

.method public static main([Ljava/lang/String;)V
	.limit stack 2
	.limit locals 7

	iconst_0
	istore_1

	iconst_0
	istore_2

	iconst_0
	istore_3

	iconst_0
	istore 4

	iconst_0
	iconst_0
	iadd
	istore 5

	iconst_0
	iconst_0
	iadd
	istore 5

	iload 5
	invokestatic io.println(I)V

	return
.end method

