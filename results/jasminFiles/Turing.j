.class public Turing
.super java/lang/Object

.field protected curPos I
.field protected MTABLE [I
.field protected R I
.field protected NUM_SYMBOLS I
.field protected WTABLE [I
.field protected TAPE [I
.field protected NTABLE [I
.field protected H I
.field protected L I
.field protected curState I
.field protected NUM_STATES I

.method public <init>()V
    aload_0
    invokespecial java/lang/Object/<init>()V
    return
.end method

.method public ss2i_int_int(II)I
	.limit stack 2
	.limit locals 8

	aload_0
	getfield Turing/NUM_STATES I
	istore_3

	iload_1
	iload_3
	imul
	istore 4

	iload 4
	iload_2
	iadd
	istore 5

	iload 5
	ireturn

.end method

.method public setTrans_int_int_int_int_int(IIIII)Z
	.limit stack 4
	.limit locals 19

	aload_0
	getfield Turing/WTABLE [I
	astore 6

	aload_0
	iload_1
	iload_2
	invokevirtual Turing.ss2i_int_int(II)I
	istore 7

	aload 6
	iload 7
	iaload
	istore 8

	aload 6
	iload 7
	iload_3
	iastore

	aload_0
	getfield Turing/MTABLE [I
	astore 9

	aload_0
	iload_1
	iload_2
	invokevirtual Turing.ss2i_int_int(II)I
	istore 10

	aload 9
	iload 10
	iaload
	istore 11

	aload 9
	iload 10
	iload 4
	iastore

	aload_0
	getfield Turing/NTABLE [I
	astore 12

	aload_0
	iload_1
	iload_2
	invokevirtual Turing.ss2i_int_int(II)I
	istore 13

	aload 12
	iload 13
	iaload
	istore 14

	aload 12
	iload 13
	iload 5
	iastore

	iconst_1
	ireturn

.end method

.method public init_bb_4s2sy()Z
	.limit stack 14
	.limit locals 16

	aload_0
	iconst_4
	iconst_2
	bipush 20
	invokevirtual Turing.initGeneric_int_int_int(III)[I
	astore_1

	aload_0
	aload_1
	putfield Turing/TAPE [I

	aload_0
	getfield Turing/R I
	istore_2

	aload_0
	iconst_0
	iconst_0
	iconst_1
	iload_2
	iconst_1
	invokevirtual Turing.setTrans_int_int_int_int_int(IIIII)Z

	aload_0
	getfield Turing/L I
	istore_3

	aload_0
	iconst_0
	iconst_1
	iconst_1
	iload_3
	iconst_0
	invokevirtual Turing.setTrans_int_int_int_int_int(IIIII)Z

	aload_0
	getfield Turing/R I
	istore 4

	aload_0
	getfield Turing/H I
	istore 5

	aload_0
	iconst_0
	iconst_2
	iconst_1
	iload 4
	iload 5
	invokevirtual Turing.setTrans_int_int_int_int_int(IIIII)Z

	aload_0
	getfield Turing/R I
	istore 6

	aload_0
	iconst_0
	iconst_3
	iconst_1
	iload 6
	iconst_3
	invokevirtual Turing.setTrans_int_int_int_int_int(IIIII)Z

	aload_0
	getfield Turing/L I
	istore 7

	aload_0
	iconst_1
	iconst_0
	iconst_1
	iload 7
	iconst_1
	invokevirtual Turing.setTrans_int_int_int_int_int(IIIII)Z

	aload_0
	getfield Turing/L I
	istore 8

	aload_0
	iconst_1
	iconst_1
	iconst_0
	iload 8
	iconst_2
	invokevirtual Turing.setTrans_int_int_int_int_int(IIIII)Z

	aload_0
	getfield Turing/L I
	istore 9

	aload_0
	iconst_1
	iconst_2
	iconst_1
	iload 9
	iconst_3
	invokevirtual Turing.setTrans_int_int_int_int_int(IIIII)Z

	aload_0
	getfield Turing/R I
	istore 10

	aload_0
	iconst_1
	iconst_3
	iconst_0
	iload 10
	iconst_0
	invokevirtual Turing.setTrans_int_int_int_int_int(IIIII)Z

	iconst_1
	ireturn

.end method

.method public initGeneric_int_int_int(III)[I
	.limit stack 2
	.limit locals 29

	aload_0
	iload_2
	putfield Turing/NUM_SYMBOLS I

	aload_0
	iload_1
	putfield Turing/NUM_STATES I

	aload_0
	getfield Turing/NUM_SYMBOLS I
	istore 4

	aload_0
	getfield Turing/NUM_STATES I
	istore 5

	iload 4
	iload 5
	imul
	istore 6

	iload 6
	istore 7

	iconst_0
	iconst_1
	isub
	istore 8

	aload_0
	iload 8
	putfield Turing/H I

	iconst_0
	iconst_1
	isub
	istore 9

	aload_0
	iload 9
	putfield Turing/L I

	aload_0
	iconst_1
	putfield Turing/R I

	iload 7
	newarray int
	astore 10

	aload_0
	aload 10
	putfield Turing/WTABLE [I

	iload 7
	newarray int
	astore 12

	aload_0
	aload 12
	putfield Turing/MTABLE [I

	iload 7
	newarray int
	astore 13

	aload_0
	aload 13
	putfield Turing/NTABLE [I

	iload_3
	newarray int
	astore 14

	aload 14
	astore 15

	aload_0
	iconst_0
	putfield Turing/curState I

	aload 15
	arraylength
	istore 16

	iload 16
	iconst_2
	idiv
	istore 17

	aload_0
	iload 17
	putfield Turing/curPos I

	aload 15
	areturn

.end method

.method public static main([Ljava/lang/String;)V
	.limit stack 3
	.limit locals 4

	new Turing
	astore_1

	aload_1
	dup
	invokespecial Turing.<init>()V
	astore_1

	aload_1
	astore_3

	aload_3
	invokevirtual Turing.init_bb_3s2sy()Z

	aload_3
	invokevirtual Turing.run()Z

	return
.end method

.method public init_bb_3s2sy()Z
	.limit stack 12
	.limit locals 14

	aload_0
	iconst_3
	iconst_2
	bipush 18
	invokevirtual Turing.initGeneric_int_int_int(III)[I
	astore_1

	aload_0
	aload_1
	putfield Turing/TAPE [I

	aload_0
	getfield Turing/R I
	istore_2

	aload_0
	iconst_0
	iconst_0
	iconst_1
	iload_2
	iconst_1
	invokevirtual Turing.setTrans_int_int_int_int_int(IIIII)Z

	aload_0
	getfield Turing/L I
	istore_3

	aload_0
	iconst_0
	iconst_1
	iconst_1
	iload_3
	iconst_0
	invokevirtual Turing.setTrans_int_int_int_int_int(IIIII)Z

	aload_0
	getfield Turing/L I
	istore 4

	aload_0
	iconst_0
	iconst_2
	iconst_1
	iload 4
	iconst_1
	invokevirtual Turing.setTrans_int_int_int_int_int(IIIII)Z

	aload_0
	getfield Turing/L I
	istore 5

	aload_0
	iconst_1
	iconst_0
	iconst_1
	iload 5
	iconst_2
	invokevirtual Turing.setTrans_int_int_int_int_int(IIIII)Z

	aload_0
	getfield Turing/R I
	istore 6

	aload_0
	iconst_1
	iconst_1
	iconst_1
	iload 6
	iconst_1
	invokevirtual Turing.setTrans_int_int_int_int_int(IIIII)Z

	aload_0
	getfield Turing/R I
	istore 7

	aload_0
	getfield Turing/H I
	istore 8

	aload_0
	iconst_1
	iconst_2
	iconst_1
	iload 7
	iload 8
	invokevirtual Turing.setTrans_int_int_int_int_int(IIIII)Z

	iconst_1
	ireturn

.end method

.method public run()Z
	.limit stack 3
	.limit locals 9

	aload_0
	astore_1

	iconst_0
	istore_2

	Loop:
	iload_2
	iconst_1
	ixor
	ifne Body

	goto EndLoop

	Body:
	aload_0
	invokevirtual Turing.printTape()Z

	invokestatic io.read()I
	istore_3

	iload_3
	istore 5

	aload_1
	invokevirtual Turing.trans()Z
	istore 6

	iload 6
	iconst_1
	ixor
	istore 7

	iload 7
	istore_2

	pop
	goto Loop

	EndLoop:
	aload_0
	invokevirtual Turing.printTape()Z

	iconst_1
	ireturn

.end method

.method public printTape()Z
	.limit stack 2
	.limit locals 22

	iconst_0
	istore_1

	Loop1:
	aload_0
	getfield Turing/TAPE [I
	astore_2

	aload_2
	arraylength
	istore_3

	iload_1
	iload_3
	if_icmplt Body1

	goto EndLoop1

	Body1:
	aload_0
	getfield Turing/curPos I
	istore 4

	iload_1
	iload 4
	if_icmpge LabelFalse7
	iconst_1
	goto LabelContinue7
	LabelFalse7:
	iconst_0
	LabelContinue7:
	istore 5

	iload 5
	iconst_1
	ixor
	istore 6

	aload_0
	getfield Turing/curPos I
	istore 7

	iload 7
	iload_1
	if_icmpge LabelFalse10
	iconst_1
	goto LabelContinue10
	LabelFalse10:
	iconst_0
	LabelContinue10:
	istore 8

	iload 8
	iconst_1
	ixor
	istore 9

	iload 6
	iload 9
	iand
	istore 10

	iload 10
	iload 10
	iand
	ifne else

	iconst_0
	invokestatic io.print(I)V

	goto endif

	else:
	aload_0
	getfield Turing/curState I
	istore 12

	iload 12
	iconst_1
	iadd
	istore 13

	iload 13
	invokestatic io.print(I)V

	endif:
	iinc 1 1

	goto Loop1

	EndLoop1:
	invokestatic io.println()V

	iconst_0
	istore_1

	Loop2:
	aload_0
	getfield Turing/TAPE [I
	astore 14

	aload 14
	arraylength
	istore 15

	iload_1
	iload 15
	if_icmplt Body2

	goto EndLoop2

	Body2:
	aload_0
	getfield Turing/TAPE [I
	astore 16

	aload 16
	iload_1
	iaload
	istore 17

	iload 17
	invokestatic io.print(I)V

	iinc 1 1

	goto Loop2

	EndLoop2:
	invokestatic io.println()V

	invokestatic io.println()V

	iconst_1
	ireturn

.end method

.method public trans()Z
	.limit stack 4
	.limit locals 44

	aload_0
	getfield Turing/TAPE [I
	astore_1

	aload_0
	getfield Turing/curPos I
	istore_2

	aload_1
	iload_2
	iaload
	istore_3

	iload_3
	istore 4

	aload_0
	getfield Turing/WTABLE [I
	astore 5

	aload_0
	getfield Turing/curState I
	istore 6

	aload_0
	iload 4
	iload 6
	invokevirtual Turing.ss2i_int_int(II)I
	istore 7

	aload 5
	iload 7
	iaload
	istore 8

	iload 8
	istore 9

	aload_0
	getfield Turing/MTABLE [I
	astore 10

	aload_0
	getfield Turing/curState I
	istore 11

	aload_0
	iload 4
	iload 11
	invokevirtual Turing.ss2i_int_int(II)I
	istore 12

	aload 10
	iload 12
	iaload
	istore 13

	iload 13
	istore 14

	aload_0
	getfield Turing/NTABLE [I
	astore 15

	aload_0
	getfield Turing/curState I
	istore 16

	aload_0
	iload 4
	iload 16
	invokevirtual Turing.ss2i_int_int(II)I
	istore 17

	aload 15
	iload 17
	iaload
	istore 18

	iload 18
	istore 19

	aload_0
	getfield Turing/TAPE [I
	astore 20

	aload_0
	getfield Turing/curPos I
	istore 21

	aload 20
	iload 21
	iaload
	istore 22

	aload 20
	iload 21
	iload 9
	iastore

	aload_0
	getfield Turing/curPos I
	istore 23

	iload 23
	iload 14
	iadd
	istore 24

	aload_0
	iload 24
	putfield Turing/curPos I

	aload_0
	iload 19
	putfield Turing/curState I

	aload_0
	getfield Turing/H I
	istore 25

	aload_0
	getfield Turing/curState I
	istore 26

	iload 25
	iload 26
	if_icmpge LabelFalse30
	iconst_1
	goto LabelContinue30
	LabelFalse30:
	iconst_0
	LabelContinue30:
	istore 27

	iload 27
	iconst_1
	ixor
	istore 28

	aload_0
	getfield Turing/curState I
	istore 29

	aload_0
	getfield Turing/H I
	istore 30

	iload 29
	iload 30
	if_icmpge LabelFalse34
	iconst_1
	goto LabelContinue34
	LabelFalse34:
	iconst_0
	LabelContinue34:
	istore 31

	iload 31
	iconst_1
	ixor
	istore 32

	iload 28
	iconst_1
	ixor
	istore 33

	iload 32
	iconst_1
	ixor
	istore 34

	iload 33
	iload 34
	ior
	ifne else1

	iconst_0
	istore 35

	goto endif1

	else1:
	iconst_1
	istore 35

	endif1:
	iload 35
	ireturn

.end method

