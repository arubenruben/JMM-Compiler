.class public Rectangle
.super Shape


.method public <init>()V
    aload_0
    invokespecial Shape/<init>()V
    return
.end method

.method public static main([Ljava/lang/String;)V
	.limit stack 2
	.limit locals 4

	new Rectangle
	astore_1

	aload_1
	dup
	invokespecial Rectangle.<init>()V
	astore_1

	aload_1
	astore_3

	aload_3
	invokevirtual Rectangle.doThings()Z

	return
.end method

.method public doThings()Z
	.limit stack 2
	.limit locals 12

	iconst_2
	istore_1

	bipush 6
	istore_2

	iload_1
	iload_2
	imul
	istore_3

	aload_0
	iload_3
	putfield Rectangle/area I

	iload_1
	iconst_2
	imul
	istore 4

	iload_2
	iconst_2
	imul
	istore 5

	iload 4
	iload 5
	iadd
	istore 6

	aload_0
	iload 6
	putfield Rectangle/perimeter I

	aload_0
	invokevirtual Rectangle.printStats()Z
	istore 7

	iload 7
	istore 8

	iconst_1
	ireturn

.end method

