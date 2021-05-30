.class public Duck
.super java/lang/Object


.method public <init>()V
    aload_0
    invokespecial java/lang/Object/<init>()V
    return
.end method

.method public quack_boolean(Z)Z
	.limit stack 1
	.limit locals 3

	iconst_0
	invokestatic io.println(I)V

	iconst_0
	ireturn

.end method

.method public static main([Ljava/lang/String;)V
	.limit stack 6
	.limit locals 8

	new Duck
	astore_1

	aload_1
	dup
	invokespecial Duck.<init>()V
	astore_1

	aload_1
	astore_3

	new Duck
	astore 4

	aload 4
	dup
	invokespecial Duck.<init>()V
	astore 4

	aload 4
	astore 5

	iconst_0
	istore 6

	iconst_1
	istore 7

	aload_3
	aload_3
	invokevirtual Duck.quack_Duck(LDuck;)Z

	aload_3
	aload 5
	invokevirtual Duck.quack_Duck(LDuck;)Z

	aload_3
	iload 6
	invokevirtual Duck.quack_int(I)Z

	aload_3
	iload 7
	invokevirtual Duck.quack_boolean(Z)Z

	return
.end method

.method public quack_Duck(LDuck;)Z
	.limit stack 1
	.limit locals 3

	iconst_1
	invokestatic io.println(I)V

	iconst_1
	ireturn

.end method

.method public quack_int(I)Z
	.limit stack 1
	.limit locals 3

	iconst_0
	invokestatic io.println(I)V

	iconst_0
	ireturn

.end method

