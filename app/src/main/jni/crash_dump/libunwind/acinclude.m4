AC_DEFUN([LIBUNWIND___THREAD],
[dnl Check whether the compiler supports the __thread keyword.
if test "x$enable___thread" != xno; then
  AC_CACHE_CHECK([for __thread], libc_cv_gcc___thread,
		 [cat > conftest.c <<\EOF
    __thread int a = 42;
EOF
  if AC_TRY_COMMAND([${CC-cc} $CFLAGS -c conftest.c >&AS_MESSAGE_LOG_FD]); then
    libc_cv_gcc___thread=yes
  else
    libc_cv_gcc___thread=no
  fi
  rm -f conftest*])
  if test "$libc_cv_gcc___thread" = yes; then
    AC_DEFINE(HAVE___THREAD, 1,
	[Define to 1 if __thread keyword is supported by the C compiler.])
  fi
else
  libc_cv_gcc___thread=no
fi])

AC_DEFUN([CHECK_ATOMIC_OPS],
[dnl Check whether the system has the atomic_ops package installed.
  AC_CHECK_HEADERS(atomic_ops.h)
#
# Don't link against libatomic_ops for now.  We don't want libunwind
# to depend on libatomic_ops.so.  Fortunately, none of the platforms
# we care about so far need libatomic_ops.a (everything is done via
# inline macros).
#
#  AC_CHECK_LIB(atomic_ops, main)
])

# ANDROID support update.
AC_DEFUN([CHECK_ANDROID],
[dnl Check whether the system has __ANDROID__ defined.
if test "x$enable_conserve_stack" != xno; then
  AC_CACHE_CHECK([for __ANDROID__], ac_cv_android,
         [cat > conftest.c <<\EOF
  #if !defined(__ANDROID__)
    value = fail
  #endif
EOF
  if AC_TRY_COMMAND([${CC-cc} $CFLAGS -c conftest.c >&AS_MESSAGE_LOG_FD]); then
    ac_cv_android=yes
  else
    ac_cv_android=no
  fi
  rm -f conftest*])
  if test "$ac_cv_android" = yes; then
    AC_DEFINE([CONSERVE_STACK], [],
              [Allocate large structures rather than place them on the stack.])
  fi
else
  ac_cv_android=no
fi])
# End of ANDROID update.
