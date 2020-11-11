/* include/config.h.  Generated from config.h.in by configure.  */
/* include/config.h.in.  Generated from configure.ac by autoheader.  */

/* Block signals before mutex operations */
/* #undef CONFIG_BLOCK_SIGNALS */

/* Enable Debug Frame */
#define CONFIG_DEBUG_FRAME 1

/* Support for Microsoft ABI extensions */
/* This is required to understand floating point registers on x86-64 */
#define CONFIG_MSABI_SUPPORT 1

/* Define to 1 if you want every memory access validated */
#define CONSERVATIVE_CHECKS 1

/* Allocate large structures rather than place them on the stack. */
#define CONSERVE_STACK /**/

/* Define to 1 if you have the <asm/ptrace_offsets.h> header file. */
/* #undef HAVE_ASM_PTRACE_OFFSETS_H */

/* Define to 1 if you have the <atomic_ops.h> header file. */
/* #undef HAVE_ATOMIC_OPS_H */

/* Define to 1 if you have the <byteswap.h> header file. */
#define HAVE_BYTESWAP_H 1

/* Define to 1 if you have the declaration of `PTRACE_CONT', and to 0 if you
   don't. */
#define HAVE_DECL_PTRACE_CONT 1

/* Define to 1 if you have the declaration of `PTRACE_POKEDATA', and to 0 if
   you don't. */
#define HAVE_DECL_PTRACE_POKEDATA 1

/* Define to 1 if you have the declaration of `PTRACE_POKEUSER', and to 0 if
   you don't. */
#if defined(__aarch64__) || defined(__mips__)
#define HAVE_DECL_PTRACE_POKEUSER 0
#else
#define HAVE_DECL_PTRACE_POKEUSER 1
#endif

/* Define to 1 if you have the declaration of `PTRACE_SINGLESTEP', and to 0 if
   you don't. */
#define HAVE_DECL_PTRACE_SINGLESTEP 1

/* Define to 1 if you have the declaration of `PTRACE_SYSCALL', and to 0 if
   you don't. */
#define HAVE_DECL_PTRACE_SYSCALL 1

/* Define to 1 if you have the declaration of `PTRACE_TRACEME', and to 0 if
   you don't. */
#define HAVE_DECL_PTRACE_TRACEME 1

/* Define to 1 if you have the declaration of `PT_CONTINUE', and to 0 if you
   don't. */
#define HAVE_DECL_PT_CONTINUE 0

/* Define to 1 if you have the declaration of `PT_GETFPREGS', and to 0 if you
   don't. */
#define HAVE_DECL_PT_GETFPREGS 0

/* Define to 1 if you have the declaration of `PT_GETREGS', and to 0 if you
   don't. */
#if defined(__mips__)
#define HAVE_DECL_PT_GETREGS 1
#else
#define HAVE_DECL_PT_GETREGS 0
#endif

/* Define to 1 if you have the declaration of `PT_GETREGSET', and to 0 if you
   don't. */
#if defined(__aarch64__)
#define HAVE_DECL_PT_GETREGSET 1
#else
#define HAVE_DECL_PT_GETREGSET 0
#endif

/* Define to 1 if you have the declaration of `PT_IO', and to 0 if you don't.
   */
#define HAVE_DECL_PT_IO 0

/* Define to 1 if you have the declaration of `PT_STEP', and to 0 if you
   don't. */
#define HAVE_DECL_PT_STEP 0

/* Define to 1 if you have the declaration of `PT_SYSCALL', and to 0 if you
   don't. */
#define HAVE_DECL_PT_SYSCALL 0

/* Define to 1 if you have the declaration of `PT_TRACE_ME', and to 0 if you
   don't. */
#define HAVE_DECL_PT_TRACE_ME 0

/* Define to 1 if you have the <dlfcn.h> header file. */
#define HAVE_DLFCN_H 1

/* Define to 1 if you have the `dlmodinfo' function. */
#define HAVE_DLMODINFO 1

/* Define to 1 if you have the `dl_iterate_phdr' function. */
#define HAVE_DL_ITERATE_PHDR 1

/* Define to 1 if you have the `dl_phdr_removals_counter' function. */
#define HAVE_DL_PHDR_REMOVALS_COUNTER 1

/* Define to 1 if you have the <elf.h> header file. */
#define HAVE_ELF_H 1

/* Define to 1 if you have the <endian.h> header file. */
#define HAVE_ENDIAN_H 1

/* Define to 1 if you have the <execinfo.h> header file. */
/* #undef HAVE_EXECINFO_H */

/* Define to 1 if you have the `getunwind' function. */
#define HAVE_GETUNWIND 1

/* Define to 1 if you have the <ia64intrin.h> header file. */
/* #undef HAVE_IA64INTRIN_H */

/* Define to 1 if you have the <inttypes.h> header file. */
#define HAVE_INTTYPES_H 1

/* Define to 1 if you have the `uca' library (-luca). */
/* #undef HAVE_LIBUCA */

/* Define to 1 if you have the <link.h> header file. */
#define HAVE_LINK_H 1

/* Define if you have liblzma */
#define HAVE_LZMA 1

/* Define to 1 if you have the <memory.h> header file. */
#define HAVE_MEMORY_H 1

/* Define to 1 if you have the `mincore' function. */
#define HAVE_MINCORE 1

/* Define to 1 if you have the <signal.h> header file. */
#define HAVE_SIGNAL_H 1

/* Define to 1 if you have the <stdint.h> header file. */
#define HAVE_STDINT_H 1

/* Define to 1 if you have the <stdlib.h> header file. */
#define HAVE_STDLIB_H 1

/* Define to 1 if you have the <strings.h> header file. */
#define HAVE_STRINGS_H 1

/* Define to 1 if you have the <string.h> header file. */
#define HAVE_STRING_H 1

/* Define to 1 if `dlpi_subs' is a member of `struct dl_phdr_info'. */
/* #undef HAVE_STRUCT_DL_PHDR_INFO_DLPI_SUBS */

/* Define to 1 if the system has the type `struct elf_prstatus'. */
/* #undef HAVE_STRUCT_ELF_PRSTATUS */

/* Define to 1 if the system has the type `struct prstatus'. */
/* #undef HAVE_STRUCT_PRSTATUS */

/* Defined if __sync atomics are available */
#define HAVE_SYNC_ATOMICS 1

/* Define to 1 if you have the <sys/elf.h> header file. */
/* #undef HAVE_SYS_ELF_H */

/* Define to 1 if you have the <sys/endian.h> header file. */
#define HAVE_SYS_ENDIAN_H 1

/* Define to 1 if you have the <sys/link.h> header file. */
/* #undef HAVE_SYS_LINK_H */

/* Define to 1 if you have the <sys/procfs.h> header file. */
/* #undef HAVE_SYS_PROCFS_H */

/* Define to 1 if you have the <sys/ptrace.h> header file. */
#define HAVE_SYS_PTRACE_H 1

/* Define to 1 if you have the <sys/stat.h> header file. */
#define HAVE_SYS_STAT_H 1

/* Define to 1 if you have the <sys/types.h> header file. */
#define HAVE_SYS_TYPES_H 1

/* Define to 1 if you have the <sys/uc_access.h> header file. */
/* #undef HAVE_SYS_UC_ACCESS_H */

/* Define to 1 if you have the `ttrace' function. */
/* #undef HAVE_TTRACE */

/* Define to 1 if you have the <unistd.h> header file. */
#define HAVE_UNISTD_H 1

/* Defined if __builtin_unreachable() is available */
#define HAVE__BUILTIN_UNREACHABLE 1

/* Defined if __builtin___clear_cache() is available */
#define HAVE__BUILTIN___CLEAR_CACHE 1

/* Define to 1 if __thread keyword is supported by the C compiler. */
#define HAVE___THREAD 1

/* Define to the sub-directory in which libtool stores uninstalled libraries.
   */
#define LT_OBJDIR ".libs/"

/* Define to 1 if your C compiler doesn't accept -c and -o together. */
/* #undef NO_MINUS_C_MINUS_O */

/* Name of package */
#define PACKAGE "libunwind"

/* Define to the address where bug reports for this package should be sent. */
#define PACKAGE_BUGREPORT "libunwind-devel@nongnu.org"

/* Define to the full name of this package. */
#define PACKAGE_NAME "libunwind"

/* Define to the full name and version of this package. */
#define PACKAGE_STRING "libunwind 1.1"

/* Define to the one symbol short name of this package. */
#define PACKAGE_TARNAME "libunwind"

/* Define to the home page for this package. */
#define PACKAGE_URL ""

/* Define to the version of this package. */
#define PACKAGE_VERSION "1.1"

/* The size of `off_t', as computed by sizeof. */
#define SIZEOF_OFF_T 4

/* Define to 1 if you have the ANSI C header files. */
#define STDC_HEADERS 1

/* Version number of package */
#define VERSION "1.1"

/* Define to empty if `const' does not conform to ANSI C. */
/* #undef const */

/* Define to `__inline__' or `__inline' if that's what the C compiler
   calls it, or to nothing if 'inline' is not supported under any name.  */
#ifndef __cplusplus
/* #undef inline */
#endif

/* Define to `unsigned int' if <sys/types.h> does not define. */
/* #undef size_t */
