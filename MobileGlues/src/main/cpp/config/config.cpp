#include "config.h"

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/stat.h>
#include "cJSON.h"
#include "../gl/log.h"

#define DEBUG 0

char* DEFAULT_MG_DIRECTORY_PATH = "/sdcard/MG";

char* mg_directory_path;
char* config_file_path;
char* log_file_path;
char* glsl_cache_file_path;

static cJSON *config_json = NULL;

int initialized = 0;

char* concatenate(char* str1, char* str2) {
    std::string str = std::string(str1) + str2;
    char* result = new char[str.size() + 1];
    strcpy(result, str.c_str());
    return result;
}

int check_path() {
    char* var = getenv("MG_DIR_PATH");
    mg_directory_path = var ? var : DEFAULT_MG_DIRECTORY_PATH;
    config_file_path = concatenate(mg_directory_path, "/config.json");
    log_file_path = concatenate(mg_directory_path, "/latest.log");
    glsl_cache_file_path = concatenate(mg_directory_path, "/glsl_cache.tmp");

    if(mkdir(mg_directory_path, 0755) != 0 && errno != EEXIST) {
        LOG_E("Error creating MG directory.\n")
        return 0;
    }
    return 1;
}

int config_refresh() {
    LOG_D("MG_DIRECTORY_PATH=%s", mg_directory_path)
    LOG_D("CONFIG_FILE_PATH=%s", config_file_path)
    LOG_D("LOG_FILE_PATH=%s", log_file_path)
    LOG_D("GLSL_CACHE_FILE_PATH=%s", glsl_cache_file_path)

    FILE *file = fopen(config_file_path, "r");
    if (file == NULL) {
        LOG_E("Unable to open config file %s", config_file_path);
        return 0;
    }

    fseek(file, 0, SEEK_END);
    long file_size = ftell(file);
    fseek(file, 0, SEEK_SET);

    char *file_content = (char *)malloc(file_size + 1);
    if (file_content == NULL) {
        LOG_E("Unable to allocate memory for file content");
        fclose(file);
        return 0;
    }

    fread(file_content, 1, file_size, file);
    fclose(file);
    file_content[file_size] = '\0';

    config_json = cJSON_Parse(file_content);
    free(file_content);

    if (config_json == NULL) {
        LOG_E("Error parsing config JSON: %s\n", cJSON_GetErrorPtr());
        return 0;
    }

    initialized = 1;
    return 1;
}

int config_get_int(char* name) {
    if (config_json == NULL) {
        return -1;
    }

    cJSON *item = cJSON_GetObjectItem(config_json, name);
    if (item == NULL || !cJSON_IsNumber(item)) {
        LOG_D("Config item '%s' not found or not an integer.\n", name);
        return -1;
    }

    return item->valueint;
}

char* config_get_string(char* name) {
    if (config_json == NULL) {
        return NULL;
    }

    cJSON *item = cJSON_GetObjectItem(config_json, name);
    if (item == NULL || !cJSON_IsString(item)) {
        LOG_D("Config item '%s' not found or not a string.\n", name);
        return ""; 
    }

    return item->valuestring;
}

void config_cleanup() {
    if (config_json != NULL) {
        cJSON_Delete(config_json);
        config_json = NULL;
    }
}
