/* This is a hack to get around a problem with a case insensitve ar that
 * accidentally includes backtrace.o and Backtrace.o causing duplicate
 * symbols. See b/15198981 for more information.
 */
#include "Backtrace.c"
