
#ifndef _GL4ESINCLUDE_INIT_H_
#define _GL4ESINCLUDE_INIT_H_

// set driver GetProcAddress implementation. required for hardext detection with NOEGL or when loader is disabled
void set_getprocaddress(void *(*new_proc_address)(const char *));
// reguired with NOEGL
void set_getmainfbsize(void (*new_getMainFBSize)(int* width, int* height));
// do this before any GL calls if init constructors disabled
void initialize_gl4es(void);
// wrapped GetProcAddress
void *gl4es_GetProcAddress(const char *name);
#endif
