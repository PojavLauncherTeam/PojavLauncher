.globl crash1
.type crash1, %function
crash1:
	ldr x0, =0xa5a50000
	ldr x1, =0xa5a50001
	ldr x2, =0xa5a50002
	ldr x3, =0xa5a50003
	ldr x4, =0xa5a50004
	ldr x5, =0xa5a50005
	ldr x6, =0xa5a50006
	ldr x7, =0xa5a50007
	ldr x8, =0xa5a50008
	ldr x9, =0xa5a50009
	ldr x10, =0xa5a50010
	ldr x11, =0xa5a50011
	ldr x12, =0xa5a50012
	ldr x13, =0xa5a50013
	ldr x14, =0xa5a50014
	ldr x15, =0xa5a50015
	ldr x16, =0xa5a50016
	ldr x17, =0xa5a50017
	ldr x18, =0xa5a50018
	ldr x19, =0xa5a50019
	ldr x20, =0xa5a50020
	ldr x21, =0xa5a50021
	ldr x22, =0xa5a50022
	ldr x23, =0xa5a50023
	ldr x24, =0xa5a50024
	ldr x25, =0xa5a50025
	ldr x26, =0xa5a50026
	ldr x27, =0xa5a50027
	ldr x28, =0xa5a50028
	ldr x29, =0xa5a50029

	fmov   d0, -1.0  // -1 is more convincing than 0.
	fmov   d1, 1.0
	fmov   d2, 2.0
	fmov   d3, 3.0
	fmov   d4, 4.0
	fmov   d5, 5.0
	fmov   d6, 6.0
	fmov   d7, 7.0
	fmov   d8, 8.0
	fmov   d9, 9.0
	fmov   d10, 10.0
	fmov   d11, 11.0
	fmov   d12, 12.0
	fmov   d13, 13.0
	fmov   d14, 14.0
	fmov   d15, 15.0
	fmov   d16, 16.0
	fmov   d17, 17.0
	fmov   d18, 18.0
	fmov   d19, 19.0
	fmov   d20, 20.0
	fmov   d21, 21.0
	fmov   d22, 22.0
	fmov   d23, 23.0
	fmov   d24, 24.0
	fmov   d25, 25.0
	fmov   d26, 26.0
	fmov   d27, 27.0
	fmov   d28, 28.0
	fmov   d29, 29.0
	fmov   d30, 30.0
	fmov   d31, 31.0

	mov x30, xzr
	ldr x30, [x30]
	b .


.globl crashnostack
.type crashnostack, %function
crashnostack:
	mov x0, xzr
	add sp, x0, xzr
	ldr x0, [x0]
	b .
