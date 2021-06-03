.class public Convertor
.super java/lang/Object


.method public <init>()V
    aload_0
    invokespecial java/lang/Object/<init>()V
    return
.end method

.method public metersToDecimeters_int(I)I
	.limit stack 2
	.limit locals 3

	iload_1
	bipush 10
	imul
	istore_2

	iload_2
	ireturn

.end method

.method public metersToMillimeters_int(I)I
	.limit stack 2
	.limit locals 3

	iload_1
	sipush 1000
	imul
	istore_2

	iload_2
	ireturn

.end method

.method public metersToCentimeters_int(I)I
	.limit stack 2
	.limit locals 3

	iload_1
	bipush 100
	imul
	istore_2

	iload_2
	ireturn

.end method

.method public millimetersToMeters_int(I)I
	.limit stack 2
	.limit locals 3

	iload_1
	sipush 1000
	idiv
	istore_2

	iload_2
	ireturn

.end method

.method public millimetersToCentimeters_int(I)I
	.limit stack 2
	.limit locals 3

	iload_1
	bipush 10
	idiv
	istore_2

	iload_2
	ireturn

.end method

.method public static main([Ljava/lang/String;)V
	.limit stack 3
	.limit locals 11

	new Convertor
	astore_1

	aload_1
	dup
	invokespecial Convertor.<init>()V
	astore_1

	aload_1
	astore_3

	aload_3
	iconst_1
	invokevirtual Convertor.metersToDecimeters_int(I)I
	istore 4

	iload 4
	invokestatic io.println(I)V

	aload_3
	iconst_1
	invokevirtual Convertor.metersToCentimeters_int(I)I
	istore 6

	iload 6
	invokestatic io.println(I)V

	aload_3
	iconst_1
	invokevirtual Convertor.metersToMillimeters_int(I)I
	istore 7

	iload 7
	invokestatic io.println(I)V

	aload_3
	bipush 10
	invokevirtual Convertor.millimetersToCentimeters_int(I)I
	istore 8

	iload 8
	invokestatic io.println(I)V

	aload_3
	bipush 100
	invokevirtual Convertor.millimetersToDecimeters_int(I)I
	istore 9

	iload 9
	invokestatic io.println(I)V

	aload_3
	sipush 1000
	invokevirtual Convertor.millimetersToMeters_int(I)I
	istore 10

	iload 10
	invokestatic io.println(I)V

	return
.end method

.method public millimetersToDecimeters_int(I)I
	.limit stack 2
	.limit locals 3

	iload_1
	bipush 100
	idiv
	istore_2

	iload_2
	ireturn

.end method

