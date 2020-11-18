.globl crash1
.type crash1, %function
crash1:
	ldr r0, =0xa5a50000
	ldr r1, =0xa5a50001
	ldr r2, =0xa5a50002
	ldr r3, =0xa5a50003
	ldr r4, =0xa5a50004
	ldr r5, =0xa5a50005
	ldr r6, =0xa5a50006
	ldr r7, =0xa5a50007
	ldr r8, =0xa5a50008
	ldr r9, =0xa5a50009
	ldr r10, =0xa5a50010
	ldr r11, =0xa5a50011
	ldr r12, =0xa5a50012


	fconstd   d0, #0
	fconstd   d1, #1
	fconstd   d2, #2
	fconstd   d3, #3
	fconstd   d4, #4
	fconstd   d5, #5
	fconstd   d6, #6
	fconstd   d7, #7
	fconstd   d8, #8
	fconstd   d9, #9
	fconstd   d10, #10
	fconstd   d11, #11
	fconstd   d12, #12
	fconstd   d13, #13
	fconstd   d14, #14
	fconstd   d15, #15
#if defined(HAS_VFP_D32)
	fconstd   d16, #16
	fconstd   d17, #17
	fconstd   d18, #18
	fconstd   d19, #19
	fconstd   d20, #20
	fconstd   d21, #21
	fconstd   d22, #22
	fconstd   d23, #23
	fconstd   d24, #24
	fconstd   d25, #25
	fconstd   d26, #26
	fconstd   d27, #27
	fconstd   d28, #28
	fconstd   d29, #29
	fconstd   d30, #30
	fconstd   d31, #31
#endif

	mov lr, #0
	ldr lr, [lr]
	b .

.globl crashnostack
.type crashnostack, %function
crashnostack:
	mov sp, #0
	mov r0, #0
	ldr r0, [r0]
	b .
