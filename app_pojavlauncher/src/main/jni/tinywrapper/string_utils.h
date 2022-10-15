#ifndef _GL4ES_STRING_UTILS_H_
#define _GL4ES_STRING_UTILS_H_

extern const char* AllSeparators;

const char* FindString(const char* pBuffer, const char* S);
char* FindStringNC(char* pBuffer, const char* S);
int CountString(const char* pBuffer, const char* S);
char* ResizeIfNeeded(char* pBuffer, int *size, int addsize);
char* InplaceReplace(char* pBuffer, int* size, const char* S, const char* D);
char* Append(char* pBuffer, int* size, const char* S);
char* InplaceInsert(char* pBuffer, const char* S, char* master, int* size);
char* GetLine(char* pBuffer, int num);
int CountLine(const char* pBuffer);
int GetLineFor(const char* pBuffer, const char* S); // get the line number for 1st occurent of S in pBuffer
char* StrNext(char *pBuffer, const char* S); // mostly as strstr, but go after the substring if found
//"blank" (space, tab, cr, lf,":", ",", ";", ".", "/")
char* NextStr(char* pBuffer);   // go to next non "blank"
char* NextBlank(char* pBuffer);   // go to next "blank"
char* NextLine(char* pBuffer);   // go to next new line (crlf not included)

const char* GetNextStr(char* pBuffer); // get a (static) copy of next str (until next separator), can be a simple number or separator also

// those function don't try to be smart with separators...
int CountStringSimple(char* pBuffer, const char* S);
char* InplaceReplaceSimple(char* pBuffer, int* size, const char* S, const char* D);


#endif // _GL4ES_STRING_UTILS_H_