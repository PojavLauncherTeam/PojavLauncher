#include "glsl_for_es.h"

#include <glslang/Public/ShaderLang.h>
#include <glslang/Include/Types.h>
#include <glslang/Public/ShaderLang.h>
#include <spirv_cross/spirv_cross_c.h>
#include <iostream>
#include <fstream>
#include "../log.h"
#include "glslang/SPIRV/GlslangToSpv.h"
#include "preConvertedGlsl.h"
#include <string>
#include <regex>
#include <strstream>
#include <algorithm>
#include "cache.h"
#include "../../version.h"

//#define FEATURE_PRE_CONVERTED_GLSL

#define DEBUG 0

char* (*MesaConvertShader)(const char *src, unsigned int type, unsigned int glsl, unsigned int essl);

//void trim(char* str) {
//    char* end;
//    while (isspace((unsigned char)*str)) str++;
//    if (*str == 0) return;
//    end = str + strlen(str) - 1;
//    while (end > str && isspace((unsigned char)*end)) end--;
//    *(end + 1) = 0;
//}
//
//int startsWith(const char *str, const char *prefix) {
//    if (!str || !prefix) return 0;
//    while (*prefix) {
//        if (*str++ != *prefix++) return 0;
//    }
//    return 1;
//}

static TBuiltInResource InitResources()
{
    TBuiltInResource Resources{};

    Resources.maxLights                                 = 32;
    Resources.maxClipPlanes                             = 6;
    Resources.maxTextureUnits                           = 32;
    Resources.maxTextureCoords                          = 32;
    Resources.maxVertexAttribs                          = 64;
    Resources.maxVertexUniformComponents                = 4096;
    Resources.maxVaryingFloats                          = 64;
    Resources.maxVertexTextureImageUnits                = 32;
    Resources.maxCombinedTextureImageUnits              = 80;
    Resources.maxTextureImageUnits                      = 32;
    Resources.maxFragmentUniformComponents              = 4096;
    Resources.maxDrawBuffers                            = 32;
    Resources.maxVertexUniformVectors                   = 128;
    Resources.maxVaryingVectors                         = 8;
    Resources.maxFragmentUniformVectors                 = 16;
    Resources.maxVertexOutputVectors                    = 16;
    Resources.maxFragmentInputVectors                   = 15;
    Resources.minProgramTexelOffset                     = -8;
    Resources.maxProgramTexelOffset                     = 7;
    Resources.maxClipDistances                          = 8;
    Resources.maxComputeWorkGroupCountX                 = 65535;
    Resources.maxComputeWorkGroupCountY                 = 65535;
    Resources.maxComputeWorkGroupCountZ                 = 65535;
    Resources.maxComputeWorkGroupSizeX                  = 1024;
    Resources.maxComputeWorkGroupSizeY                  = 1024;
    Resources.maxComputeWorkGroupSizeZ                  = 64;
    Resources.maxComputeUniformComponents               = 1024;
    Resources.maxComputeTextureImageUnits               = 16;
    Resources.maxComputeImageUniforms                   = 8;
    Resources.maxComputeAtomicCounters                  = 8;
    Resources.maxComputeAtomicCounterBuffers            = 1;
    Resources.maxVaryingComponents                      = 60;
    Resources.maxVertexOutputComponents                 = 64;
    Resources.maxGeometryInputComponents                = 64;
    Resources.maxGeometryOutputComponents               = 128;
    Resources.maxFragmentInputComponents                = 128;
    Resources.maxImageUnits                             = 8;
    Resources.maxCombinedImageUnitsAndFragmentOutputs   = 8;
    Resources.maxCombinedShaderOutputResources          = 8;
    Resources.maxImageSamples                           = 0;
    Resources.maxVertexImageUniforms                    = 0;
    Resources.maxTessControlImageUniforms               = 0;
    Resources.maxTessEvaluationImageUniforms            = 0;
    Resources.maxGeometryImageUniforms                  = 0;
    Resources.maxFragmentImageUniforms                  = 8;
    Resources.maxCombinedImageUniforms                  = 8;
    Resources.maxGeometryTextureImageUnits              = 16;
    Resources.maxGeometryOutputVertices                 = 256;
    Resources.maxGeometryTotalOutputComponents          = 1024;
    Resources.maxGeometryUniformComponents              = 1024;
    Resources.maxGeometryVaryingComponents              = 64;
    Resources.maxTessControlInputComponents             = 128;
    Resources.maxTessControlOutputComponents            = 128;
    Resources.maxTessControlTextureImageUnits           = 16;
    Resources.maxTessControlUniformComponents           = 1024;
    Resources.maxTessControlTotalOutputComponents       = 4096;
    Resources.maxTessEvaluationInputComponents          = 128;
    Resources.maxTessEvaluationOutputComponents         = 128;
    Resources.maxTessEvaluationTextureImageUnits        = 16;
    Resources.maxTessEvaluationUniformComponents        = 1024;
    Resources.maxTessPatchComponents                    = 120;
    Resources.maxPatchVertices                          = 32;
    Resources.maxTessGenLevel                           = 64;
    Resources.maxViewports                              = 16;
    Resources.maxVertexAtomicCounters                   = 0;
    Resources.maxTessControlAtomicCounters              = 0;
    Resources.maxTessEvaluationAtomicCounters           = 0;
    Resources.maxGeometryAtomicCounters                 = 0;
    Resources.maxFragmentAtomicCounters                 = 8;
    Resources.maxCombinedAtomicCounters                 = 8;
    Resources.maxAtomicCounterBindings                  = 1;
    Resources.maxVertexAtomicCounterBuffers             = 0;
    Resources.maxTessControlAtomicCounterBuffers        = 0;
    Resources.maxTessEvaluationAtomicCounterBuffers     = 0;
    Resources.maxGeometryAtomicCounterBuffers           = 0;
    Resources.maxFragmentAtomicCounterBuffers           = 1;
    Resources.maxCombinedAtomicCounterBuffers           = 1;
    Resources.maxAtomicCounterBufferSize                = 16384;
    Resources.maxTransformFeedbackBuffers               = 4;
    Resources.maxTransformFeedbackInterleavedComponents = 64;
    Resources.maxCullDistances                          = 8;
    Resources.maxCombinedClipAndCullDistances           = 8;
    Resources.maxSamples                                = 4;
    Resources.maxMeshOutputVerticesNV                   = 256;
    Resources.maxMeshOutputPrimitivesNV                 = 512;
    Resources.maxMeshWorkGroupSizeX_NV                  = 32;
    Resources.maxMeshWorkGroupSizeY_NV                  = 1;
    Resources.maxMeshWorkGroupSizeZ_NV                  = 1;
    Resources.maxTaskWorkGroupSizeX_NV                  = 32;
    Resources.maxTaskWorkGroupSizeY_NV                  = 1;
    Resources.maxTaskWorkGroupSizeZ_NV                  = 1;
    Resources.maxMeshViewCountNV                        = 4;

    Resources.limits.nonInductiveForLoops                 = true;
    Resources.limits.whileLoops                           = true;
    Resources.limits.doWhileLoops                         = true;
    Resources.limits.generalUniformIndexing               = true;
    Resources.limits.generalAttributeMatrixVectorIndexing = true;
    Resources.limits.generalVaryingIndexing               = true;
    Resources.limits.generalSamplerIndexing               = true;
    Resources.limits.generalVariableIndexing              = true;
    Resources.limits.generalConstantMatrixVectorIndexing  = true;

    return Resources;
}

int getGLSLVersion(const char* glsl_code) {
    std::string code(glsl_code);
    static std::regex version_pattern(R"(#version\s+(\d{3}))");
    std::smatch match;
    if (std::regex_search(code, match, version_pattern)) {
        return std::stoi(match[1].str());
    }

    return -1;
}

//std::string removeSecondLine(std::string code) {
//    size_t firstLineEnd = code.find('\n');
//    if (firstLineEnd == std::string::npos) {
//        return code;
//    }
//    size_t secondLineEnd = code.find('\n', firstLineEnd + 1);
//    if (secondLineEnd == std::string::npos) {
//        return code;
//    }
//    code.erase(firstLineEnd + 1, secondLineEnd - firstLineEnd);
//    return code;
//}

//char* disable_GL_ARB_derivative_control(const char* glslCode) {
//    std::string code(glslCode);
//    std::string target = "GL_ARB_derivative_control";
//    size_t pos = code.find(target);
//
//    if (pos != std::string::npos) {
//        size_t ifdefPos = 0;
//        while ((ifdefPos = code.find("#ifdef GL_ARB_derivative_control", ifdefPos)) != std::string::npos) {
//            code.replace(ifdefPos, 32, "#if 0");
//            ifdefPos += 4;
//        }
//
//        size_t ifndefPos = 0;
//        while ((ifndefPos = code.find("#ifndef GL_ARB_derivative_control", ifndefPos)) != std::string::npos) {
//            code.replace(ifndefPos, 33, "#if 1");
//            ifndefPos += 4;
//        }
//
//        code = removeSecondLine(code);
//
//        char* result = new char[code.length() + 1];
//        std::strcpy(result, code.c_str());
//        return result;
//    }
//
//    char* result = new char[code.length() + 1];
//    std::strcpy(result, code.c_str());
//    return result;
//}
//
//char* forceSupporterInput(char* glslCode) {
//    // first
//    const char* target = "const mat3 rotInverse = transpose(rot);";
//    const char* replacement = "const mat3 rotInverse = mat3(rot[0][0], rot[1][0], rot[2][0], rot[0][1], rot[1][1], rot[2][1], rot[0][2], rot[1][2], rot[2][2]);";
//
//    char* pos = strstr(glslCode, target);
//    if (pos != nullptr) {
//        size_t targetLen = strlen(target);
//        size_t replacementLen = strlen(replacement);
//
//        size_t newSize = strlen(glslCode) - targetLen + replacementLen + 1;
//        char* modifiedCode = new char[newSize];
//
//        strncpy(modifiedCode, glslCode, pos - glslCode);
//        modifiedCode[pos - glslCode] = '\0';
//
//        strcat(modifiedCode, replacement);
//
//        strcat(modifiedCode, pos + targetLen);
//        glslCode = new char[strlen(modifiedCode) + 1];
//        std::strcpy(glslCode, modifiedCode);
//        std::free(modifiedCode);
//    }
//
//    // second
//    if (!std::strstr(glslCode, "deferredOutput2 = GI_TemporalFilter()")) {
//        return glslCode;
//    }
//
//    if (std::strstr(glslCode, "vec4 GI_TemporalFilter()")) {
//        return glslCode;
//    }
//
//
//    LOG_D("find GI_TemporalFilter()")
//
//    const char* GI_TemporalFilter = R"(
//vec4 GI_TemporalFilter() {
//    vec2 uv = gl_FragCoord.xy / screenSize;
//    uv += taaJitter * pixelSize;
//    vec4 currentGI = texture(colortex0, uv);
//    float depth = texture(depthtex0, uv).r;
//    vec4 clipPos = vec4(uv * 2.0 - 1.0, depth, 1.0);
//    vec4 viewPos = gbufferProjectionInverse * clipPos;
//    viewPos /= viewPos.w;
//    vec4 worldPos = gbufferModelViewInverse * viewPos;
//    vec4 prevClipPos = gbufferPreviousProjection * (gbufferPreviousModelView * worldPos);
//    prevClipPos /= prevClipPos.w;
//    vec2 prevUV = prevClipPos.xy * 0.5 + 0.5;
//    vec4 historyGI = texture(colortex1, prevUV);
//    float difference = length(currentGI.rgb - historyGI.rgb);
//    float thresholdValue = 0.1;
//    float adaptiveBlend = mix(0.9, 0.0, smoothstep(thresholdValue, thresholdValue * 2.0, difference));
//    vec4 filteredGI = mix(currentGI, historyGI, adaptiveBlend);
//    if (difference > thresholdValue * 2.0) {
//        filteredGI = currentGI;
//    }
//    return filteredGI;
//}
//)";
//
//    char *mainPos = strstr(glslCode, "\nvoid main()");
//    if (mainPos == nullptr) {
//        LOG_E("Error: 'void main()' not found in GLSL code.")
//        return glslCode;
//    }
//
//    size_t prefixLength = mainPos - glslCode;
//    size_t originalLength = strlen(glslCode);
//    size_t insertLength = strlen(GI_TemporalFilter);
//
//    char *modifiedCode = (char *)malloc(originalLength + insertLength + 2);
//    if (modifiedCode == nullptr) {
//        LOG_E("Memory allocation failed.")
//        return glslCode;
//    }
//
//    strncpy(modifiedCode, glslCode, prefixLength);
//    modifiedCode[prefixLength] = '\0';
//
//    strcat(modifiedCode, "\n");
//    strcat(modifiedCode, GI_TemporalFilter);
//    strcat(modifiedCode, "\n");
//
//    strcat(modifiedCode, mainPos);
//
//    free(glslCode);
//    glslCode = modifiedCode;
//
//    return glslCode;
//}


std::string forceSupporterOutput(const std::string& glslCode) {
    bool hasPrecisionFloat = glslCode.find("precision ") != std::string::npos &&
                             glslCode.find("float;") != std::string::npos;
    bool hasPrecisionInt = glslCode.find("precision ") != std::string::npos &&
                           glslCode.find("int;") != std::string::npos;

    std::string result = glslCode;
    std::string precisionFloat;
    std::string precisionInt;

    if (hasPrecisionFloat && hasPrecisionInt) {
        std::istringstream iss(result);
        std::vector<std::string> lines;
        std::string line;
        while (std::getline(iss, line)) {
            bool isPrecisionLine = (line.find("precision ") != std::string::npos) &&
                                   (line.find("float;") != std::string::npos || line.find("int;") != std::string::npos);
            if (!isPrecisionLine) {
                lines.push_back(line);
            }
        }
        result.clear();
        for (size_t i = 0; i < lines.size(); ++i) {
            if (i != 0) result += '\n';
            result += lines[i];
        }
        precisionFloat = "precision highp float;\n";
        precisionInt = "precision highp int;\n";
    } else {
        precisionFloat = hasPrecisionFloat ? "" : "precision highp float;\n";
        precisionInt = hasPrecisionInt ? "" : "precision highp int;\n";
    }

    size_t lastExtensionPos = result.rfind("#extension");
    size_t insertionPos = 0;

    if (lastExtensionPos != std::string::npos) {
        size_t nextNewline = result.find('\n', lastExtensionPos);
        if (nextNewline != std::string::npos) {
            insertionPos = nextNewline + 1;
        } else {
            insertionPos = result.length();
        }
    } else {
        size_t firstNewline = result.find('\n');
        if (firstNewline != std::string::npos) {
            insertionPos = firstNewline + 1;
        } else {
            result = precisionFloat + precisionInt + result;
            return result;
        }
    }

    result.insert(insertionPos, precisionFloat + precisionInt);
    return result;
}

std::string removeLayoutBinding(const std::string& glslCode) {
    static std::regex bindingRegex(R"(layout\s*\(\s*binding\s*=\s*\d+\s*\)\s*)");
    std::string result = std::regex_replace(glslCode, bindingRegex, "");
    static std::regex bindingRegex2(R"(layout\s*\(\s*binding\s*=\s*\d+\s*,)");
    result = std::regex_replace(result, bindingRegex2, "layout(");
    return result;
}

// TODO
[[maybe_unused]] std::string makeRGBWriteonly(const std::string& input) {
    static std::regex pattern(R"(.*layout\([^)]*rgba[^)]*\).*?)");
    std::string result;
    std::string::size_type start = 0;
    std::string::size_type end;
    while ((end = input.find('\n', start)) != std::string::npos) {
        std::string line = input.substr(start, end - start);
        if (std::regex_search(line, pattern)) {
            result += "writeonly " + line + "\n";
        } else {
            result += line + "\n";
        }
        start = end + 1;
    }
    std::string lastLine = input.substr(start);
    if (std::regex_search(lastLine, pattern)) {
        result += "writeonly " + lastLine;
    } else {
        result += lastLine;
    }

    return result;
}

//char* removeLineDirective(char* glslCode) {
//    char* cursor = glslCode;
//    int modifiedCodeIndex = 0;
//    size_t maxLength = 1024 * 10;
//    char* modifiedGlslCode = (char*)malloc(maxLength * sizeof(char));
//    if (!modifiedGlslCode) return nullptr;
//
//    while (*cursor) {
//        if (strncmp(cursor, "\n#", 2) == 0) {
//            modifiedGlslCode[modifiedCodeIndex++] = *cursor++;
//            modifiedGlslCode[modifiedCodeIndex++] = *cursor++;
//            char* last_cursor = cursor;
//            while (cursor[0] != '\n') cursor++;
//            char* line_feed_cursor = cursor;
//            while (isspace(cursor[0])) cursor--;
//            if (cursor[0] == '\\')
//            {
//                // find line directive, now remove it
//                char* slash_cursor = cursor;
//                cursor = last_cursor;
//                while (cursor < slash_cursor - 1)
//                    modifiedGlslCode[modifiedCodeIndex++] = *cursor++;
//                modifiedGlslCode[modifiedCodeIndex++] = ' ';
//                cursor = line_feed_cursor + 1;
//                while (isspace(cursor[0])) cursor++;
//
//                while (true) {
//                    char* last_cursor2 = cursor;
//                    while (cursor[0] != '\n') cursor++;
//                    cursor -= 1;
//                    while (isspace(cursor[0])) cursor--;
//                    if (cursor[0] == '\\') {
//                        char* slash_cursor2 = cursor;
//                        cursor = last_cursor2;
//                        while (cursor < slash_cursor2)
//                            modifiedGlslCode[modifiedCodeIndex++] = *cursor++;
//                        while (cursor[0] != '\n') cursor++;
//                        cursor++;
//                        while (isspace(cursor[0])) cursor++;
//                    } else {
//                        cursor = last_cursor2;
//                        while (cursor[0] != '\n')
//                            modifiedGlslCode[modifiedCodeIndex++] = *cursor++;
//                        break;
//                    }
//                }
//                cursor++;
//            }
//            else {
//                cursor = last_cursor;
//            }
//        }
//        else {
//            modifiedGlslCode[modifiedCodeIndex++] = *cursor++;
//        }
//
//        if (modifiedCodeIndex >= maxLength - 1) {
//            maxLength *= 2;
//            modifiedGlslCode = (char*)realloc(modifiedGlslCode, maxLength);
//            if (!modifiedGlslCode) return nullptr;
//        }
//    }
//
//    modifiedGlslCode[modifiedCodeIndex] = '\0';
//    return modifiedGlslCode;
//}

//char* process_uniform_declarations(char* glslCode) {
//    char* cursor = glslCode;
//    char name[256], type[256], initial_value[1024];
//    int modifiedCodeIndex = 0;
//    size_t maxLength = 1024 * 10;
//    char* modifiedGlslCode = (char*)malloc(maxLength * sizeof(char));
//    if (!modifiedGlslCode) return nullptr;
//
//    while (*cursor) {
//        if (strncmp(cursor, "uniform", 7) == 0) {
//            char* cursor_start = cursor;
//
//            cursor += 7;
//
//            while (isspace((unsigned char)*cursor)) cursor++;
//
//            // may be precision qualifier
//            char* precision = nullptr;
//            if (startsWith(cursor, "highp")) {
//                precision = " highp";
//                cursor += 5;
//                while (isspace((unsigned char)*cursor)) cursor++;
//            } else if (startsWith(cursor, "lowp")) {
//                precision = " lowp";
//                cursor += 4;
//                while (isspace((unsigned char)*cursor)) cursor++;
//            } else if (startsWith(cursor, "mediump")) {
//                precision = " mediump";
//                cursor += 7;
//                while (isspace((unsigned char)*cursor)) cursor++;
//            }
//
//            int i = 0;
//            while (isalnum((unsigned char)*cursor) || *cursor == '_') {
//                type[i++] = *cursor++;
//            }
//            type[i] = '\0';
//
//            while (isspace((unsigned char)*cursor)) cursor++;
//
//            // may be precision qualifier
//            if(!precision)
//            {
//                if (startsWith(cursor, "highp")) {
//                    precision = " highp";
//                    cursor += 5;
//                    while (isspace((unsigned char)*cursor)) cursor++;
//                } else if (startsWith(cursor, "lowp")) {
//                    precision = " lowp";
//                    cursor += 4;
//                    while (isspace((unsigned char)*cursor)) cursor++;
//                } else if (startsWith(cursor, "mediump")) {
//                    precision = " mediump";
//                    cursor += 7;
//                    while (isspace((unsigned char)*cursor)) cursor++;
//                } else {
//                    precision = "";
//                }
//            }
//
//            while (isspace((unsigned char)*cursor)) cursor++;
//
//            i = 0;
//            while (isalnum((unsigned char)*cursor) || *cursor == '_') {
//                name[i++] = *cursor++;
//            }
//            name[i] = '\0';
//            while (isspace((unsigned char)*cursor)) cursor++;
//
//            initial_value[0] = '\0';
//            if (*cursor == '=') {
//                cursor++;
//                i = 0;
//                while (*cursor && *cursor != ';') {
//                    initial_value[i++] = *cursor++;
//                }
//                initial_value[i] = '\0';
//                trim(initial_value);
//            }
//
//            while (*cursor != ';' && *cursor) {
//                cursor++;
//            }
//
//            char* cursor_end = cursor;
//
//            size_t spaceLeft = maxLength - modifiedCodeIndex;
//            int len = 0;
//
//            if (*initial_value) {
//                len = snprintf(modifiedGlslCode + modifiedCodeIndex, spaceLeft, "uniform%s %s %s;", precision, type, name);
//            } else {
//                // use original declaration
//                size_t length = cursor_end - cursor_start + 1;
//                if (length < spaceLeft) {
//                    memcpy(modifiedGlslCode + modifiedCodeIndex, cursor_start, length);
//                    len = (int)length;
//                } else {
//                    fprintf(stderr, "Error: Not enough space in buffer\n");
//                }
//                // len = snprintf(modifiedGlslCode + modifiedCodeIndex, spaceLeft, "uniform%s %s %s;", precision, type, name);
//            }
//
//            if (len < 0 || len >= spaceLeft) {
//                free(modifiedGlslCode);
//                return nullptr;
//            }
//            modifiedCodeIndex += len;
//
//            while (*cursor == ';') cursor++;
//
//        } else {
//            modifiedGlslCode[modifiedCodeIndex++] = *cursor++;
//        }
//
//        while (modifiedCodeIndex >= maxLength - 1) {
//            maxLength *= 2;
//            char* temp = (char*)realloc(modifiedGlslCode, maxLength);
//            if (!temp) {
//                free(modifiedGlslCode);
//                return nullptr;
//            }
//            modifiedGlslCode = temp;
//        }
//    }
//
//    modifiedGlslCode[modifiedCodeIndex] = '\0';
//    return modifiedGlslCode;
//}

void trim(std::string& str) {
    str.erase(str.begin(), std::find_if(str.begin(), str.end(), [](int ch) {
        return !std::isspace(ch);
    }));
    str.erase(std::find_if(str.rbegin(), str.rend(), [](int ch) {
        return !std::isspace(ch);
    }).base(), str.end());
}

// Process all uniform declarations into `uniform <precision> <type> <name>;` form
std::string process_uniform_declarations(const std::string& glslCode) {
    std::string result;
    size_t scan_pos = 0;
    size_t chunk_start = 0;
    const size_t length = glslCode.length();
    const std::vector<std::string> precision_kws = {"highp", "lowp", "mediump"};

    result.reserve(glslCode.length());

    while (scan_pos < length) {
        if (glslCode.compare(scan_pos, 7, "uniform") == 0) {
            if (scan_pos > chunk_start) {
                result.append(glslCode, chunk_start, scan_pos - chunk_start);
            }

            const size_t decl_start = scan_pos;
            scan_pos += 7;  // Skip "uniform"

            // 解析精度限定符和类型
            std::string precision, type;
            bool found_precision = false;

            // 第一轮解析：类型前的精度限定符
            while (scan_pos < length) {
                while (scan_pos < length && std::isspace(glslCode[scan_pos])) ++scan_pos;

                // 检查精度限定符
                for (const auto& kw : precision_kws) {
                    if (glslCode.compare(scan_pos, kw.length(), kw) == 0) {
                        precision = " " + kw;
                        scan_pos += kw.length();
                        found_precision = true;
                        break;
                    }
                }
                if (found_precision) break;

                // 开始提取类型
                const size_t type_start = scan_pos;
                while (scan_pos < length && (std::isalnum(glslCode[scan_pos]) || glslCode[scan_pos] == '_')) {
                    ++scan_pos;
                }
                type = glslCode.substr(type_start, scan_pos - type_start);
                break;
            }

            // 第二轮解析：类型后的精度限定符
            while (scan_pos < length) {
                while (scan_pos < length && std::isspace(glslCode[scan_pos])) ++scan_pos;

                bool found = false;
                for (const auto& kw : precision_kws) {
                    if (glslCode.compare(scan_pos, kw.length(), kw) == 0) {
                        if (precision.empty()) precision = " " + kw;
                        scan_pos += kw.length();
                        found = true;
                        break;
                    }
                }
                if (!found) break;
            }

            // 确保类型被正确提取
            if (type.empty()) {
                const size_t type_start = scan_pos;
                while (scan_pos < length && (std::isalnum(glslCode[scan_pos]) || glslCode[scan_pos] == '_')) {
                    ++scan_pos;
                }
                type = glslCode.substr(type_start, scan_pos - type_start);
            }

            // 提取变量名
            while (scan_pos < length && std::isspace(glslCode[scan_pos])) ++scan_pos;
            const size_t name_start = scan_pos;
            while (scan_pos < length && (std::isalnum(glslCode[scan_pos]) || glslCode[scan_pos] == '_')) {
                ++scan_pos;
            }
            const std::string name = glslCode.substr(name_start, scan_pos - name_start);

            // 定位声明结束
            size_t decl_end = glslCode.find(';', scan_pos);
            if (decl_end == std::string::npos) decl_end = length;
            else ++decl_end;

            // 处理初始化值
            const bool has_initializer = (glslCode.find('=', scan_pos) < decl_end);
            if (has_initializer) {
                result.append("uniform")
                        .append(precision)
                        .append(" ")
                        .append(type)
                        .append(" ")
                        .append(name)
                        .append(";");
            } else {
                result.append(glslCode, decl_start, decl_end - decl_start);
            }

            scan_pos = chunk_start = decl_end;
        } else {
            ++scan_pos;
        }
    }

    if (chunk_start < length) {
        result.append(glslCode, chunk_start, length - chunk_start);
    }

    return result;
}

std::string processOutColorLocations(const std::string& glslCode) {
    const static std::regex pattern(R"(\n(out highp vec4 outColor)(\d+);)");
    const std::string replacement = "\nlayout(location=$2) $1$2;";
    return std::regex_replace(glslCode, pattern, replacement);
}

std::string getCachedESSL(const char* glsl_code, uint essl_version) {
    std::string sha256_string(glsl_code);
    sha256_string += "\n//" + std::to_string(MAJOR) + "." + std::to_string(MINOR) + "." + std::to_string(REVISION) + "|" + std::to_string(essl_version);
    const char* cachedESSL = Cache::get_instance().get(sha256_string.c_str());
    if (cachedESSL) {
        LOG_D("GLSL Hit Cache:\n%s\n-->\n%s", glsl_code, cachedESSL)
        return cachedESSL;
    } else return "";
}


std::string GLSLtoGLSLES(const char* glsl_code, GLenum glsl_type, uint essl_version, uint glsl_version) {
    std::string sha256_string(glsl_code);
    sha256_string += "\n//" + std::to_string(MAJOR) + "." + std::to_string(MINOR) + "." + std::to_string(REVISION) + "|" + std::to_string(essl_version);
    const char* cachedESSL = Cache::get_instance().get(sha256_string.c_str());
    if (cachedESSL) {
        LOG_D("GLSL Hit Cache:\n%s\n-->\n%s", glsl_code, cachedESSL)
        return (char*)cachedESSL;
    }
    
    int return_code = -1;
    std::string converted = glsl_version<140? GLSLtoGLSLES_1(glsl_code, glsl_type, essl_version, return_code):GLSLtoGLSLES_2(glsl_code, glsl_type, essl_version, return_code);
    if (return_code == 0 && !converted.empty()) {
        converted = process_uniform_declarations(converted);
        Cache::get_instance().put(sha256_string.c_str(), converted.c_str());
    }

    return (return_code == 0) ? converted : glsl_code;
}

std::string replace_line_starting_with(const std::string& glslCode, const std::string& starting, const std::string& substitution = "") {
    std::string result;
    size_t length = glslCode.size();
    size_t start = 0;  // 当前保留块的起始位置
    size_t current = 0;

    auto append_chunk = [&](size_t end) {
        if (end > start) {
            result.append(glslCode, start, end - start);
        }
    };

    while (current < length) {
        // Skip whitespace at line begin
        size_t lineStart = current;
        while (current < length && (glslCode[current] == ' ' || glslCode[current] == '\t')) {
            current++;
        }

        // Check whether #line directive
        bool isLineDirective = false;
        if (current + 5 <= length && glslCode.compare(current, 5, "#line") == 0) {
            isLineDirective = true;
        }

        // Move to line end
        while (current < length && glslCode[current] != '\r' && glslCode[current] != '\n') {
            current++;
        }

        // Handle carriage return
        size_t newlineLength = 0;
        if (current < length) {
            if (glslCode[current] == '\r') {
                newlineLength = (current + 1 < length && glslCode[current + 1] == '\n') ? 2 : 1;
            }
            else {
                newlineLength = 1;
            }
        }

        if (isLineDirective) {
            // Find #line directive ->
            //  1. Append chunk
            append_chunk(lineStart); // from chunk_begin to before `#line`
            // 2. Skip this line (incl. \n)
            current += newlineLength;
            start = current; // 3. Starting from next line

            result += substitution;
        }
        else {
            // move to a new line
            current += newlineLength;
        }
    }

    // append last block
    append_chunk(current);
    return result;
}

static inline void replace_all(std::string& str, const std::string& from, const std::string& to) {
    size_t start_pos = 0;
    while((start_pos = str.find(from, start_pos)) != std::string::npos) {
        str.replace(start_pos, from.length(), to);
        start_pos += to.length(); // Handles case where 'to' is a substring of 'from'
    }
}

static inline void inject_temporal_filter(std::string& glsl) {
    const std::string temporalFilterCall = "deferredOutput2 = GI_TemporalFilter()";
    const std::string temporalFilterDef = "vec4 GI_TemporalFilter()";
    const std::string mainStart = "void main()";

    // Already defined function
    const auto def_loc = glsl.find(temporalFilterDef);
    if (def_loc != std::string::npos)
        return;

    // Never called function
    const auto call_loc = glsl.find(temporalFilterCall);
    if (call_loc == std::string::npos)
        return;


    const auto main_loc = glsl.find(mainStart);
    // No main(), no inject
    if (main_loc == std::string::npos)
        return;

    const std::string GI_TemporalFilter = R"(
vec4 GI_TemporalFilter() {
    vec2 uv = gl_FragCoord.xy / screenSize;
    uv += taaJitter * pixelSize;
    vec4 currentGI = texture(colortex0, uv);
    float depth = texture(depthtex0, uv).r;
    vec4 clipPos = vec4(uv * 2.0 - 1.0, depth, 1.0);
    vec4 viewPos = gbufferProjectionInverse * clipPos;
    viewPos /= viewPos.w;
    vec4 worldPos = gbufferModelViewInverse * viewPos;
    vec4 prevClipPos = gbufferPreviousProjection * (gbufferPreviousModelView * worldPos);
    prevClipPos /= prevClipPos.w;
    vec2 prevUV = prevClipPos.xy * 0.5 + 0.5;
    vec4 historyGI = texture(colortex1, prevUV);
    float difference = length(currentGI.rgb - historyGI.rgb);
    float thresholdValue = 0.1;
    float adaptiveBlend = mix(0.9, 0.0, smoothstep(thresholdValue, thresholdValue * 2.0, difference));
    vec4 filteredGI = mix(currentGI, historyGI, adaptiveBlend);
    if (difference > thresholdValue * 2.0) {
        filteredGI = currentGI;
    }
    return filteredGI;
}
)";
    // Do injection here
    glsl.insert(main_loc, "\n" + GI_TemporalFilter + "\n");
}

std::string preprocess_glsl(const std::string& glsl) {
    std::string ret = glsl;
    // Remove lines beginning with `#line`
    ret = replace_line_starting_with(ret, "#line");
    // Act as if disable_GL_ARB_derivative_control is false
    replace_all(ret, "#ifdef GL_ARB_derivative_control", "#if 0");
    replace_all(ret, "#ifndef GL_ARB_derivative_control", "#if 1");

    // Polyfill transpose()
    replace_all(ret,
                "const mat3 rotInverse = transpose(rot);",
                "const mat3 rotInverse = mat3(rot[0][0], rot[1][0], rot[2][0], rot[0][1], rot[1][1], rot[2][1], rot[0][2], rot[1][2], rot[2][2]);");

    // GI_TemporalFilter injection
    inject_temporal_filter(ret);
    return ret;
}

int get_or_add_glsl_version(std::string& glsl) {
    int glsl_version = getGLSLVersion(glsl.c_str());
    if (glsl_version == -1) {
        glsl_version = 140;
        glsl.insert(0, "#version 140\n");
    }
    LOG_D("GLSL version: %d",glsl_version)
    return glsl_version;
}

std::vector<unsigned int> glsl_to_spirv(GLenum shader_type, int glsl_version, const char * const *shader_src, int& errc) {
    EShLanguage shader_language;
    switch (shader_type) {
        case GL_VERTEX_SHADER:
            shader_language = EShLanguage::EShLangVertex;
            break;
        case GL_FRAGMENT_SHADER:
            shader_language = EShLanguage::EShLangFragment;
            break;
        case GL_COMPUTE_SHADER:
            shader_language = EShLanguage::EShLangCompute;
            break;
        case GL_TESS_CONTROL_SHADER:
            shader_language = EShLanguage::EShLangTessControl;
            break;
        case GL_TESS_EVALUATION_SHADER:
            shader_language = EShLanguage::EShLangTessEvaluation;
            break;
        case GL_GEOMETRY_SHADER:
            shader_language = EShLanguage::EShLangGeometry;
            break;
        default:
            LOG_D("GLSL type not supported!")
            errc = -1;
            return {};
    }

    glslang::TShader shader(shader_language);
    shader.setStrings(shader_src, 1);

    using namespace glslang;
    shader.setEnvInput(EShSourceGlsl, shader_language, EShClientVulkan, glsl_version);
    shader.setEnvClient(EShClientOpenGL, EShTargetOpenGL_450);
    shader.setEnvTarget(EShTargetSpv, EShTargetSpv_1_6);
    shader.setAutoMapLocations(true);
    shader.setAutoMapBindings(true);

    TBuiltInResource TBuiltInResource_resources = InitResources();

    if (!shader.parse(&TBuiltInResource_resources, glsl_version, true, EShMsgDefault)) {
        LOG_D("GLSL Compiling ERROR: \n%s",shader.getInfoLog())
        errc = -1;
        return {};
    }
    LOG_D("GLSL Compiled.")

    glslang::TProgram program;
    program.addShader(&shader);

    if (!program.link(EShMsgDefault)) {
        LOG_D("Shader Linking ERROR: %s", program.getInfoLog())
        errc = -1;
        return {};
    }
    LOG_D("Shader Linked." )
    std::vector<unsigned int> spirv_code;
    glslang::SpvOptions spvOptions;
    spvOptions.disableOptimizer = false;
    glslang::GlslangToSpv(*program.getIntermediate(shader_language), spirv_code, &spvOptions);
    errc = 0;
    return spirv_code;
}

std::string spirv_to_essl(std::vector<unsigned int> spirv, uint essl_version, int& errc) {
    spvc_context context = nullptr;
    spvc_parsed_ir ir = nullptr;
    spvc_compiler compiler_glsl = nullptr;
    spvc_compiler_options options = nullptr;
    spvc_resources resources = nullptr;
    const spvc_reflected_resource *list = nullptr;
    const char *result = nullptr;
    size_t count;

    const SpvId *p_spirv = spirv.data();
    size_t word_count = spirv.size();

    LOG_D("spirv_code.size(): %d", spirv.size())
    spvc_context_create(&context);
    spvc_context_parse_spirv(context, p_spirv, word_count, &ir);
    spvc_context_create_compiler(context, SPVC_BACKEND_GLSL, ir, SPVC_CAPTURE_MODE_TAKE_OWNERSHIP, &compiler_glsl);
    spvc_compiler_create_shader_resources(compiler_glsl, &resources);
    spvc_resources_get_resource_list_for_type(resources, SPVC_RESOURCE_TYPE_UNIFORM_BUFFER, &list, &count);
    spvc_compiler_create_compiler_options(compiler_glsl, &options);
    spvc_compiler_options_set_uint(options, SPVC_COMPILER_OPTION_GLSL_VERSION, essl_version >= 300 ? essl_version : 300);
    spvc_compiler_options_set_bool(options, SPVC_COMPILER_OPTION_GLSL_ES, SPVC_TRUE);
    spvc_compiler_install_compiler_options(compiler_glsl, options);
    spvc_compiler_compile(compiler_glsl, &result);

    if (!result) {
        LOG_E("Error: unexpected error in spirv-cross.")
        errc = -1;
        return "";
    }

    std::string essl = result;

    spvc_context_destroy(context);

    errc = 0;
    return essl;
}

static bool glslang_inited = false;
std::string GLSLtoGLSLES_2(const char *glsl_code, GLenum glsl_type, uint essl_version, int& return_code) {
#ifdef FEATURE_PRE_CONVERTED_GLSL
    if (getGLSLVersion(glsl_code) == 430) {
        char* converted = preConvertedGlsl(glsl_code);
        if (converted) {
            LOG_D("Find pre-converted glsl, now use it.")
            return converted;
        }
    }
#endif
//    char* correct_glsl = glsl_code;
    std::string correct_glsl_str = preprocess_glsl(glsl_code);
    LOG_D("Firstly converted GLSL:\n%s", correct_glsl_str.c_str())
    int glsl_version = get_or_add_glsl_version(correct_glsl_str);

    if (!glslang_inited) {
        glslang::InitializeProcess();
        glslang_inited = true;
    }
    const char* s[] = { correct_glsl_str.c_str() };
    int errc = 0;
    std::vector<unsigned int> spirv_code = glsl_to_spirv(glsl_type, glsl_version, s, errc);
    if (errc != 0) {
        return_code = -1;
        return "";
    }
    errc = 0;
    std::string essl = spirv_to_essl(spirv_code, essl_version, errc);
    if (errc != 0) {
        return_code = -2;
        return "";
    }

    // Post-processing ESSL
    essl = removeLayoutBinding(essl);
    essl = processOutColorLocations(essl);
    essl = forceSupporterOutput(essl);
    //essl = makeRGBWriteonly(essl);

//    char* result_essl = new char[essl.length() + 1];
//    std::strcpy(result_essl, essl.c_str());

    LOG_D("Originally GLSL to GLSL ES Complete: \n%s", essl.c_str())

//    free(shader_source);
//    glslang::FinalizeProcess();
    return_code = errc;
    return essl;
}

std::string GLSLtoGLSLES_1(const char *glsl_code, GLenum glsl_type, uint esversion, int& return_code) {
    LOG_W("Warning: use glsl optimizer to convert shader.")
    if (esversion < 300) esversion = 300;
    std::string result = MesaConvertShader(glsl_code, glsl_type == GL_VERTEX_SHADER ? GL_VERTEX_SHADER : GL_FRAGMENT_SHADER, 460LL, esversion);
//    char * ret = (char*)malloc(sizeof(char) * strlen(result) + 1);
//    strcpy(ret, result);
    return_code = 0;
    return result;
}
