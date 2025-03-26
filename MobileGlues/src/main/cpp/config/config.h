#ifndef _MOBILEGLUES_CONFIG_H_
#define _MOBILEGLUES_CONFIG_H_

#ifdef __cplusplus
extern "C" {
#endif

extern char* mg_directory_path;
extern char* config_file_path;
extern char* log_file_path;
extern char* glsl_cache_file_path;

extern int initialized;

char* concatenate(char* str1, char* str2);

int check_path();

int config_refresh();
int config_get_int(char* name);
char* config_get_string(char* name);
void config_cleanup();

#ifdef __cplusplus
}
#endif

#endif // _MOBILEGLUES_CONFIG_H_
