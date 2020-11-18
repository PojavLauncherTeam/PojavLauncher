.globl crash1
.globl crashnostack

crash1:
	movl $0xa5a50000, %eax
	movl $0xa5a50001, %ebx
	movl $0xa5a50002, %ecx

	movl $0, %edx
	jmp *%edx


crashnostack:
	movl $0, %ebp
	jmp *%ebp
