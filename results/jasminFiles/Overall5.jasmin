.class public Overall5
.super java/lang/Object

.field protected a [I
.field protected b [I
.field protected c [I
.field protected i I

.method public <init>()V
    aload_0
    invokespecial java/lang/Object/<init>()V
    return
.end method

.method public classFieldAccess()I
	.limit stack 3
	.limit locals 39

	iconst_5
	newarray int
	astore_1

	aload_0
	aload_1
	putfield Overall5/a [I

	iconst_5
	newarray int
	astore_3

	aload_0
	aload_3
	putfield Overall5/b [I

	iconst_5
	newarray int
	astore 4

	aload_0
	aload 4
	putfield Overall5/c [I

	aload_0
	iconst_0
	putfield Overall5/i I

	Loop15:
	aload_0
	getfield Overall5/i I
	istore 5

	iload 5
	iconst_5
	if_icmplt Body15

	goto EndLoop15

	Body15:
	aload_0
	getfield Overall5/a [I
	astore 6

	aload_0
	getfield Overall5/i I
	istore 7

	aload 6
	iload 7
	iaload
	istore 8

	aload 6
	iload 7
	iconst_1
	iastore

	aload_0
	getfield Overall5/b [I
	astore 9

	aload_0
	getfield Overall5/i I
	istore 10

	aload 9
	iload 10
	iaload
	istore 11

	aload 9
	iload 10
	iconst_2
	iastore

	aload_0
	getfield Overall5/i I
	istore 12

	iload 12
	iconst_1
	iadd
	istore 13

	aload_0
	iload 13
	putfield Overall5/i I

	goto Loop15

	EndLoop15:
	aload_0
	iconst_0
	putfield Overall5/i I

	Loop16:
	aload_0
	getfield Overall5/i I
	istore 14

	iload 14
	iconst_5
	if_icmplt Body16

	goto EndLoop16

	Body16:
	aload_0
	getfield Overall5/c [I
	astore 15

	aload_0
	getfield Overall5/i I
	istore 16

	aload 15
	iload 16
	iaload
	istore 17

	aload_0
	getfield Overall5/a [I
	astore 18

	aload_0
	getfield Overall5/i I
	istore 19

	aload 18
	iload 19
	iaload
	istore 20

	aload_0
	getfield Overall5/b [I
	astore 21

	aload_0
	getfield Overall5/i I
	istore 22

	aload 21
	iload 22
	iaload
	istore 23

	iload 20
	iload 23
	iadd
	istore 24

	aload 15
	iload 16
	iload 24
	iastore

	aload_0
	getfield Overall5/i I
	istore 25

	iload 25
	iconst_1
	iadd
	istore 26

	aload_0
	iload 26
	putfield Overall5/i I

	goto Loop16

	EndLoop16:
	aload_0
	iconst_0
	putfield Overall5/i I

	Loop17:
	aload_0
	getfield Overall5/i I
	istore 27

	iload 27
	iconst_5
	if_icmplt Body17

	goto EndLoop17

	Body17:
	aload_0
	getfield Overall5/c [I
	astore 28

	aload_0
	getfield Overall5/i I
	istore 29

	aload 28
	iload 29
	iaload
	istore 30

	iload 30
	invokestatic io.println(I)V

	aload_0
	getfield Overall5/i I
	istore 32

	iload 32
	iconst_1
	iadd
	istore 33

	aload_0
	iload 33
	putfield Overall5/i I

	goto Loop17

	EndLoop17:
	iconst_0
	ireturn

.end method

.method public static main([Ljava/lang/String;)V
	.limit stack 2
	.limit locals 6

	new Overall5
	astore_1

	aload_1
	dup
	invokespecial Overall5.<init>()V
	astore_1

	aload_1
	astore_3

	aload_3
	invokevirtual Overall5.classFieldAccess()I
	istore 4

	iload 4
	istore 5

	return
.end method

