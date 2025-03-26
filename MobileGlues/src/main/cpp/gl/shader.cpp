//
// Created by BZLZHH on 2025/1/26.
//

#include <cctype>
#include "shader.h"

#include "gl.h"
#include "log.h"
#include "../gles/loader.h"
#include "../includes.h"
#include "glsl/glsl_for_es.h"
#include "../config/settings.h"

#define DEBUG 0

struct shader_t shaderInfo;

bool can_run_essl3(unsigned int esversion, const char *glsl) {
    if (strncmp(glsl, "#version 100", 12) == 0) {
        return true; 
    }

    unsigned int glsl_version = 0;
    if (strncmp(glsl, "#version 300 es", 15) == 0) {
        glsl_version = 300;
    } else if (strncmp(glsl, "#version 310 es", 15) == 0) {
        glsl_version = 310;
    } else if (strncmp(glsl, "#version 320 es", 15) == 0) {
        glsl_version = 320;
    } else {
        return false;
    }
    return esversion >= glsl_version;
}

bool is_direct_shader(const char *glsl)
{
    bool es3_ability = can_run_essl3(hardware->es_version, glsl);
    return es3_ability;
}

void glShaderSource(GLuint shader, GLsizei count, const GLchar *const* string, const GLint *length) {    
    LOG()
    shaderInfo.id = 0;
    shaderInfo.converted = "";
    shaderInfo.frag_data_changed = 0;
    size_t l = 0;
    for (int i=0; i<count; i++) l+=(length && length[i] >= 0)?length[i]:strlen(string[i]);
    std::string glsl_src, essl_src;
    glsl_src.reserve(l + 1);
    if (length) {
        for (int i = 0; i < count; i++) {
            if(length[i] >= 0)
                glsl_src += std::string_view(string[i], length[i]);
            else
                glsl_src += string[i];
        }
    } else {
        for (int i=0; i < count; i++)
            glsl_src += string[i];
    }

//    char* source = nullptr;
//    char* converted = nullptr;
//    source = (char*)malloc(l+1);
//    memset(source, 0, l+1);
//    if(length) {
//        for (int i=0; i<count; i++) {
//            if(length[i] >= 0)
//                strncat(source, string[i], length[i]);
//            else
//                strcat(source, string[i]);
//        }
//    } else {
//        for (int i=0; i<count; i++)
//            strcat(source, string[i]);
//    }
    if (GLES.glShaderSource) {
        if(is_direct_shader(glsl_src.c_str())){
            LOG_D("[INFO] [Shader] Direct shader source: ")
            LOG_D("%s", glsl_src.c_str())
            essl_src = glsl_src;
        } else {
            int glsl_version = getGLSLVersion(glsl_src.c_str());
            LOG_D("[INFO] [Shader] Shader source: ")
            LOG_D("%s", glsl_src.c_str())
            GLint shaderType;
            GLES.glGetShaderiv(shader, GL_SHADER_TYPE, &shaderType);
            essl_src = getCachedESSL(glsl_src.c_str(), hardware->es_version);
            if (essl_src.empty())
                essl_src = GLSLtoGLSLES(glsl_src.c_str(), shaderType, hardware->es_version, glsl_version);
            if (essl_src.empty()) {
                LOG_E("Failed to convert shader %d.", shader)
                return;
            }
            LOG_D("\n[INFO] [Shader] Converted Shader source: \n%s", essl_src.c_str())
        }
        if (!essl_src.empty()) {
            shaderInfo.id = shader;
            shaderInfo.converted = essl_src;
            const char* s[] = { essl_src.c_str() };
            GLES.glShaderSource(shader, count, s, nullptr);
        }
        else
            LOG_E("Failed to convert glsl.")
        CHECK_GL_ERROR
    } else {
        LOG_E("No GLES.glShaderSource")
    }
}

void glGetShaderiv(GLuint shader, GLenum pname, GLint *params) {
    LOG()
    GLES.glGetShaderiv(shader, pname, params);
    if(global_settings.ignore_error >= 1 && pname == GL_COMPILE_STATUS && !*params) {
        GLchar infoLog[512];
        GLES.glGetShaderInfoLog(shader, 512, nullptr, infoLog);
        LOG_W_FORCE("Shader %d compilation failed: \n%s", shader, infoLog)
        LOG_W_FORCE("Now try to cheat.")
        *params = GL_TRUE;
    }
    CHECK_GL_ERROR
}