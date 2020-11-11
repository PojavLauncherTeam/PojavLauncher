/*
 * This is a temporary work around for using libunwind.a with
 * WHOLE_STATIC_LIBRARIES. Since every build will have one Lstep.o for the
 * target architecture and another for dwarf, libunwind.a was given two copies
 * of Lstep.o. Our build system is unable to handle this right now.
 *
 * Bug: 15110069
 */
#include "Lstep.c"
