.class public List
.super java/lang/Object


.method public <init>()V
    aload_0
    invokespecial java/lang/Object/<init>()V
    return
.end method

.method public print_int_arr([I)Z
	.limit stack 2
	.limit locals 6

	iconst_0
	istore_2

	Loop4:
	aload_1
	arraylength
	istore_3

	iload_2
	iload_3
	if_icmplt Body4

	goto EndLoop4

	Body4:
	aload_1
	iload_2
	iaload
	istore 4

	iload 4
	invokestatic io.print(I)V

	iinc 2 1

	goto Loop4

	EndLoop4:
	iconst_1
	ireturn

.end method

.method public sort_int_arr_int_int([III)Z
	.limit stack 6
	.limit locals 13

	aload_1
	astore 4

	iload_2
	istore 5

	iload_3
	istore 6

	iload 5
	iload 6
	if_icmpge else17

	aload_0
	aload 4
	iload 5
	iload 6
	invokevirtual List.partition_int_arr_int_int([III)I
	istore 7

	iload 7
	istore 8

	iload 8
	iconst_1
	isub
	istore 9

	aload_0
	aload 4
	iload 5
	iload 9
	invokevirtual List.sort_int_arr_int_int([III)Z

	iload 8
	iconst_1
	iadd
	istore 10

	aload_0
	aload 4
	iload 10
	iload 6
	invokevirtual List.sort_int_arr_int_int([III)Z

	pop
	pop
	goto endif17

	else17:
	iconst_0
	istore 11

	endif17:
	iconst_1
	ireturn

.end method

.method public partition_int_arr_int_int([III)I
	.limit stack 3
	.limit locals 18

	aload_1
	iload_3
	iaload
	istore 4

	iload 4
	istore 5

	iload_2
	istore 6

	iload_2
	istore 7

	Loop5:
	iload 7
	iload_3
	if_icmplt Body5

	goto EndLoop5

	Body5:
	aload_1
	iload 7
	iaload
	istore 8

	iload 8
	iload 5
	if_icmpge else18

	aload_1
	iload 6
	iaload
	istore 9

	iload 9
	istore 10

	aload_1
	iload 6
	iaload
	istore 11

	aload_1
	iload 7
	iaload
	istore 12

	aload_1
	iload 6
	iload 12
	iastore

	aload_1
	iload 7
	iaload
	istore 13

	aload_1
	iload 7
	iload 10
	iastore

	iinc 6 1

	goto endif18

	else18:
	endif18:
	iinc 7 1

	goto Loop5

	EndLoop5:
	aload_1
	iload 6
	iaload
	istore 14

	iload 14
	istore 10

	aload_1
	iload 6
	iaload
	istore 15

	aload_1
	iload_3
	iaload
	istore 16

	aload_1
	iload 6
	iload 16
	iastore

	aload_1
	iload_3
	iaload
	istore 17

	aload_1
	iload_3
	iload 10
	iastore

	iload 6
	ireturn

.end method

.method public slice_int_arr_int_int([III)[I
	.limit stack 3
	.limit locals 12

	iload_3
	iload_2
	isub
	istore 4

	iload 4
	newarray int
	astore 5

	aload 5
	astore 7

	iload_2
	istore 8

	iconst_0
	istore 9

	Loop6:
	iload 8
	iload_3
	if_icmplt Body6

	goto EndLoop6

	Body6:
	aload 7
	iload 9
	iaload
	istore 10

	aload_1
	iload 8
	iaload
	istore 11

	aload 7
	iload 9
	iload 11
	iastore

	iinc 8 1

	iinc 9 1

	goto Loop6

	EndLoop6:
	aload 7
	areturn

.end method

.method public static main([Ljava/lang/String;)V
	.limit stack 8
	.limit locals 25

	new List
	astore_1

	aload_1
	dup
	invokespecial List.<init>()V
	astore_1

	aload_1
	astore_3

	bipush 10
	newarray int
	astore 4

	aload 4
	astore 6

	bipush 10
	newarray int
	astore 7

	aload 7
	astore 8

	bipush 10
	newarray int
	astore 9

	aload 9
	astore 10

	iconst_0
	istore 11

	Loop7:
	iload 11
	bipush 10
	if_icmplt Body7

	goto EndLoop7

	Body7:
	aload 6
	iload 11
	iaload
	istore 12

	aload 6
	iload 11
	iload 11
	iastore

	aload 8
	iload 11
	iaload
	istore 13

	aload 8
	iload 11
	iload 11
	iastore

	aload 10
	iload 11
	iaload
	istore 14

	bipush 9
	iload 11
	isub
	istore 15

	aload 10
	iload 11
	iload 15
	iastore

	iinc 11 1

	goto Loop7

	EndLoop7:
	aload_3
	aload 6
	aload 8
	invokevirtual List.concat_int_arr_int_arr([I[I)[I
	astore 16

	aload 16
	astore 17

	aload_3
	aload 17
	invokevirtual List.print_int_arr([I)Z

	invokestatic io.println()V

	aload_3
	aload 6
	iconst_1
	invokevirtual List.at_int_arr_int([II)I
	istore 19

	iload 19
	istore 20

	iload 20
	invokestatic io.println(I)V

	aload_3
	aload 6
	iconst_3
	invokevirtual List.slice_int_arr_int([II)[I
	astore 21

	aload 21
	astore 17

	aload_3
	aload 17
	invokevirtual List.print_int_arr([I)Z

	invokestatic io.println()V

	aload_3
	aload 6
	iconst_3
	iconst_4
	invokevirtual List.slice_int_arr_int_int([III)[I
	astore 22

	aload 22
	astore 17

	aload_3
	aload 17
	invokevirtual List.print_int_arr([I)Z

	invokestatic io.println()V

	aload 10
	arraylength
	istore 23

	iload 23
	iconst_1
	isub
	istore 24

	aload_3
	aload 10
	iconst_0
	iload 24
	invokevirtual List.sort_int_arr_int_int([III)Z

	aload_3
	aload 10
	invokevirtual List.print_int_arr([I)Z

	return
.end method

.method public slice_int_arr_int([II)[I
	.limit stack 3
	.limit locals 9

	iload_2
	newarray int
	astore_3

	aload_3
	astore 5

	iconst_0
	istore 6

	Loop8:
	iload 6
	iload_2
	if_icmplt Body8

	goto EndLoop8

	Body8:
	aload 5
	iload 6
	iaload
	istore 7

	aload_1
	iload 6
	iaload
	istore 8

	aload 5
	iload 6
	iload 8
	iastore

	iinc 6 1

	goto Loop8

	EndLoop8:
	aload 5
	areturn

.end method

.method public concat_int_arr_int_arr([I[I)[I
	.limit stack 3
	.limit locals 17

	aload_1
	arraylength
	istore_3

	aload_2
	arraylength
	istore 4

	iload_3
	iload 4
	iadd
	istore 5

	iload 5
	newarray int
	astore 6

	aload 6
	astore 8

	iconst_0
	istore 9

	Loop9:
	aload_1
	arraylength
	istore 10

	iload 9
	iload 10
	if_icmplt Body9

	goto EndLoop9

	Body9:
	aload 8
	iload 9
	iaload
	istore 11

	aload_1
	iload 9
	iaload
	istore 12

	aload 8
	iload 9
	iload 12
	iastore

	iinc 9 1

	goto Loop9

	EndLoop9:
	iconst_0
	istore 13

	Loop10:
	aload_2
	arraylength
	istore 14

	iload 13
	iload 14
	if_icmplt Body10

	goto EndLoop10

	Body10:
	aload 8
	iload 9
	iaload
	istore 15

	aload_2
	iload 13
	iaload
	istore 16

	aload 8
	iload 9
	iload 16
	iastore

	iinc 9 1

	iinc 13 1

	goto Loop10

	EndLoop10:
	aload 8
	areturn

.end method

.method public at_int_arr_int([II)I
	.limit stack 2
	.limit locals 4

	aload_1
	iload_2
	iaload
	istore_3

	iload_3
	ireturn

.end method

