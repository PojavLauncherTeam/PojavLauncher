#include "envvars.h"
#include "glext.h"
#include "../includes.h"
#include <cstdio>
#include <cstdlib>
#include <cstdarg>

const char* GetEnvVar(const char *name)
{
	return getenv(name);
}

int HasEnvVar(const char *name)
{
	return GetEnvVar(name) != nullptr;
}

int ReturnEnvVarInt(const char *name)
{
	const char *s=GetEnvVar(name);
	return s ? atoi(s) : 0;
}

int ReturnEnvVarIntDef(const char *name,int def)
{
	const char *s=GetEnvVar(name);
	return s ? atoi(s) : def;
}

int IsEnvVarTrue(const char *name)
{
	const char *s=GetEnvVar(name);
	return s && atoi(s)!=0;
}

int IsEnvVarFalse(const char *name)
{
	const char *s=GetEnvVar(name);
	return s && *s=='0';
}

int IsEnvVarInt(const char *name,int i)
{
	const char *s=GetEnvVar(name);
	return s && atoi(s)==i;
}

int GetEnvVarInt(const char *name,int *i,int def)
{
	const char *s=GetEnvVar(name);
	*i=s ? atoi(s) : def;
	return s!=NULL;
}

int GetEnvVarBool(const char *name,int *b,int def)
{
	const char *s=GetEnvVar(name);
	*b=s ? atoi(s) : def;
	if(*b) *b=1;
	return s!=NULL;
}

int GetEnvVarFloat(const char *name,float *f,float def)
{
	const char *s=GetEnvVar(name);
	*f=s ? atof(s) : def;
	return s!=NULL;
}

int GetEnvVarFmt(const char *name,const char *fmt,...)
{
	int cnt=0;
	const char *s=GetEnvVar(name);
	if(s) {
		va_list args;
  	va_start(args,fmt);
  	cnt=vsscanf(s,fmt,args);
  	va_end(args);
	}
	return cnt;
}