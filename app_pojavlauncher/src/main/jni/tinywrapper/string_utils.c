#include <string.h>
#include <stdio.h>
#include <stdlib.h>

#include "string_utils.h"

const char* AllSeparators = " \t\n\r.,;()[]{}-<>+*/%&\\\"'^$=!:?";

char* ResizeIfNeeded(char* pBuffer, int *size, int addsize);

char* InplaceReplace(char* pBuffer, int* size, const char* S, const char* D)
{
    int lS = strlen(S), lD = strlen(D);
    pBuffer = ResizeIfNeeded(pBuffer, size, (lD-lS)*CountString(pBuffer, S));
    char* p = pBuffer;
    while((p = strstr(p, S)))
    {
        // found an occurence of S
        // check if good to replace, strchr also found '\0' :)
        if(strchr(AllSeparators, p[lS])!=NULL && (p==pBuffer || strchr(AllSeparators, p[-1])!=NULL)) {
            // move out rest of string
            memmove(p+lD, p+lS, strlen(p)-lS+1);
            // replace
            memcpy(p, D, strlen(D));
            // next
            p+=lD;
        } else p+=lS;
    }
    
    return pBuffer;
}

char* InplaceInsert(char* pBuffer, const char* S, char* master, int* size)
{
    char* m = ResizeIfNeeded(master, size, strlen(S));
    if(m!=master) {
        pBuffer += (m-master);
        master = m;
    }
    char* p = pBuffer;
    int lS = strlen(S), ll = strlen(pBuffer);
    memmove(p+lS, p, ll+1);
    memcpy(p, S, lS);

    return master;
}

char* GetLine(char* pBuffer, int num)
{
    char *p = pBuffer;
    while(num-- && (p=strstr(p, "\n"))) p+=strlen("\n");
    return (p)?p:pBuffer;
}

int CountLine(const char* pBuffer)
{
    int n=0;
    const char* p = pBuffer;
    while((p=strstr(p, "\n"))) {
        p+=strlen("\n");
        n++;
    }
    return n;
}

int GetLineFor(const char* pBuffer, const char* S)
{
    int n=0;
    const char* p = pBuffer;
    const char* end = FindString(pBuffer, S);
    if(!end)
        return 0;
    while((p=strstr(p, "\n"))) {
        p+=strlen("\n");
        n++;
        if(p>=end)
            return n;
    }
    return n;
}

int CountString(const char* pBuffer, const char* S)
{
    const char* p = pBuffer;
    int lS = strlen(S);
    int n = 0;
    while((p = strstr(p, S)))
    {
        // found an occurence of S
        // check if good to count, strchr also found '\0' :)
        if(strchr(AllSeparators, p[lS])!=NULL && (p==pBuffer || strchr(AllSeparators, p[-1])!=NULL))
            n++;
        p+=lS;
    }
    return n;
}

const char* FindString(const char* pBuffer, const char* S)
{
    const char* p = pBuffer;
    int lS = strlen(S);
    while((p = strstr(p, S)))
    {
        // found an occurence of S
        // check if good to count, strchr also found '\0' :)
        if(strchr(AllSeparators, p[lS])!=NULL && (p==pBuffer || strchr(AllSeparators, p[-1])!=NULL))
            return p;
        p+=lS;
    }
    return NULL;
}

char* FindStringNC(char* pBuffer, const char* S)
{
    char* p = pBuffer;
    int lS = strlen(S);
    while((p = strstr(p, S)))
    {
        // found an occurence of S
        // check if good to count, strchr also found '\0' :)
        if(strchr(AllSeparators, p[lS])!=NULL && (p==pBuffer || strchr(AllSeparators, p[-1])!=NULL))
            return p;
        p+=lS;
    }
    return NULL;
}

char* ResizeIfNeeded(char* pBuffer, int *size, int addsize) {
    char* p = pBuffer;
    int newsize = strlen(pBuffer)+addsize+1;
    if (newsize>*size) {
        newsize += 100;
        p = (char*)realloc(pBuffer, newsize);
        *size=newsize;
    }
    return p;
}

char* Append(char* pBuffer, int* size, const char* S) {
    char* p =pBuffer;
    p = ResizeIfNeeded(pBuffer, size, strlen(S));
    strcat(p, S);
    return p;
}

int isBlank(char c)  {
    switch(c) {
        case ' ':
        case '\t':
        case '\n':
        case '\r':
        case ':':
        case ',':
        case ';':
        case '/':
            return 1;
        default:
            return 0;
    }
}
char* StrNext(char *pBuffer, const char* S) {
    if(!pBuffer) return NULL;
    char *p = strstr(pBuffer, S);
    return (p)?p:(p+strlen(S));
}

char* NextStr(char* pBuffer) {
    if(!pBuffer) return NULL;
    while(isBlank(*pBuffer))
        ++pBuffer;
    return pBuffer;
}

char* NextBlank(char* pBuffer) {
    if(!pBuffer) return NULL;
    while(!isBlank(*pBuffer))
        ++pBuffer;
    return pBuffer;
}

char* NextLine(char* pBuffer) {
    if(!pBuffer) return NULL;
    while(*pBuffer && *pBuffer!='\n')
        ++pBuffer;
    return pBuffer;
}

const char* GetNextStr(char* pBuffer) {
    static char buff[100] = {0};
    buff[0] = '\0';
    if(!pBuffer) return NULL;
    char* p1 = NextStr(pBuffer);
    if(!p1) return buff;
    char* p2 = NextBlank(p1);
    if(!p2) return buff;
    int i=0;
    while(p1!=p2 && i<99)
        buff[i++] = *(p1++);
    buff[i] = '\0';
    return buff;
}

int CountStringSimple(char* pBuffer, const char* S)
{
    char* p = pBuffer;
    int lS = strlen(S);
    int n = 0;
    while((p = strstr(p, S)))
    {
        // found an occurence of S
        n++;
        p+=lS;
    }
    return n;
}

char* InplaceReplaceSimple(char* pBuffer, int* size, const char* S, const char* D)
{
    int lS = strlen(S), lD = strlen(D);
    pBuffer = ResizeIfNeeded(pBuffer, size, (lD-lS)*CountStringSimple(pBuffer, S));
    char* p = pBuffer;
    while((p = strstr(p, S)))
    {
        // found an occurence of S
        // move out rest of string
        memmove(p+lD, p+lS, strlen(p)-lS+1);
        // replace
        memcpy(p, D, strlen(D));
        // next
        p+=lD;
    }
    
    return pBuffer;
}