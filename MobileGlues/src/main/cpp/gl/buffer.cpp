//
// Created by BZLZHH on 2025/1/28.
//

#include "buffer.h"
#include <unordered_map>

#define DEBUG 0

GLint maxBufferId = 0;
GLint maxArrayId = 0;

std::unordered_map<GLuint, GLuint> g_gen_buffers;
std::unordered_map<GLuint, GLuint> g_gen_arrays;

std::unordered_map<GLenum, GLuint> g_bound_buffers;
GLuint bound_array = 0;

std::unordered_map<GLuint, BufferMapping> g_active_mappings;

GLuint gen_buffer() {
    maxBufferId++;
    g_gen_buffers[maxBufferId] = 0;
    return maxBufferId;
}

GLboolean has_buffer(GLuint key) {
    auto it = g_gen_buffers.find(key);
    return it != g_gen_buffers.end();
}

void modify_buffer(GLuint key, GLuint value) {
    g_gen_buffers[key] = value;
}

void remove_buffer(GLuint key) {
    if (g_gen_buffers.find(key) != g_gen_buffers.end())
        g_gen_buffers.erase(key);
}

GLuint find_real_buffer(GLuint key) {
    auto it = g_gen_buffers.find(key);
    if (it != g_gen_buffers.end())
        return it->second;
    else
        return 0;
}

GLuint find_bound_buffer(GLenum key) {
    GLenum target = 0;
    switch (key) {
        case GL_ARRAY_BUFFER_BINDING:
            target = GL_ARRAY_BUFFER;
            break;
        case GL_ATOMIC_COUNTER_BUFFER_BINDING:
            target = GL_ATOMIC_COUNTER_BUFFER;
            break;
        case GL_COPY_READ_BUFFER_BINDING:
            target = GL_COPY_READ_BUFFER;
            break;
        case GL_COPY_WRITE_BUFFER_BINDING:
            target = GL_COPY_WRITE_BUFFER;
            break;
        case GL_DRAW_INDIRECT_BUFFER_BINDING:
            target = GL_DRAW_INDIRECT_BUFFER;
            break;
        case GL_DISPATCH_INDIRECT_BUFFER_BINDING:
            target = GL_DISPATCH_INDIRECT_BUFFER;
            break;
        case GL_ELEMENT_ARRAY_BUFFER_BINDING:
            target = GL_ELEMENT_ARRAY_BUFFER;
            break;
        case GL_PIXEL_PACK_BUFFER_BINDING:
            target = GL_PIXEL_PACK_BUFFER;
            break;
        case GL_PIXEL_UNPACK_BUFFER_BINDING:
            target = GL_PIXEL_UNPACK_BUFFER;
            break;
        case GL_SHADER_STORAGE_BUFFER_BINDING:
            target = GL_SHADER_STORAGE_BUFFER;
            break;
        case GL_TRANSFORM_FEEDBACK_BUFFER_BINDING:
            target = GL_TRANSFORM_FEEDBACK_BUFFER;
            break;
        case GL_UNIFORM_BUFFER_BINDING:
            target = GL_UNIFORM_BUFFER;
            break;
        default:
            target = 0;
            break;
    }
    auto it = g_bound_buffers.find(target);
    if (it != g_bound_buffers.end())
        return it->second;
    else
        return 0;
}

GLuint gen_array() {
    maxArrayId++;
    g_gen_arrays[maxArrayId] = 0;
    return maxArrayId;
}

GLboolean has_array(GLuint key) {
    auto it = g_gen_arrays.find(key);
    return it != g_gen_arrays.end();
}

void modify_array(GLuint key, GLuint value) {
    g_gen_arrays[key] = value;
}

void remove_array(GLuint key) {
    if (g_gen_arrays.find(key) != g_gen_arrays.end())
        g_gen_arrays.erase(key);
}

GLuint find_real_array(GLuint key) {
    auto it = g_gen_arrays.find(key);
    if (it != g_gen_arrays.end())
        return it->second;
    else
        return 0;
}

GLuint find_bound_array() {
    return bound_array;
}

static GLenum get_binding_query(GLenum target) {
    switch(target) {
        case GL_ARRAY_BUFFER:          return GL_ARRAY_BUFFER_BINDING;
        case GL_ELEMENT_ARRAY_BUFFER:  return GL_ELEMENT_ARRAY_BUFFER_BINDING;
        case GL_PIXEL_PACK_BUFFER:     return GL_PIXEL_PACK_BUFFER_BINDING;
        case GL_PIXEL_UNPACK_BUFFER:   return GL_PIXEL_UNPACK_BUFFER_BINDING;
        default:                       return 0;
    }
}

void glGenBuffers(GLsizei n, GLuint *buffers) {
    LOG()
    LOG_D("glGenBuffers(%i, %p)", n, buffers)
    for (int i = 0; i < n; ++i) {
        buffers[i] = gen_buffer();
    }
}

void glDeleteBuffers(GLsizei n, const GLuint *buffers) {
    LOG()
    LOG_D("glDeleteBuffers(%i, %p)", n, buffers)
    for (int i = 0; i < n; ++i) {
        if (find_real_buffer(buffers[i])) {
            GLuint real_buff = find_real_buffer(buffers[i]);
            GLES.glDeleteBuffers(1, &real_buff);
            CHECK_GL_ERROR
        }
        remove_buffer(buffers[i]);
    }
}

GLboolean glIsBuffer(GLuint buffer) {
    LOG()
    LOG_D("glIsBuffer, buffer = %d", buffer)
    return has_buffer(buffer);
}

void glBindBuffer(GLenum target, GLuint buffer) {
    LOG()
    LOG_D("glBindBuffer, target = %s, buffer = %d", glEnumToString(target), buffer)
    g_bound_buffers[target] = buffer;
    if (!has_buffer(buffer) || buffer == 0) {
        GLES.glBindBuffer(target, buffer);
        CHECK_GL_ERROR
        return;
    }
    GLuint real_buffer = find_real_buffer(buffer);
    if (!real_buffer) {
        GLES.glGenBuffers(1, &real_buffer);
        modify_buffer(buffer, real_buffer);
        CHECK_GL_ERROR
    }
    GLES.glBindBuffer(target, real_buffer);
    CHECK_GL_ERROR
}

void glBindBufferRange(GLenum target, GLuint index, GLuint buffer, GLintptr offset, GLsizeiptr size) {
    LOG()
    LOG_D("glBindBufferRange, target = %s, index = %d, buffer = %d, offset = %p, size = %zi", glEnumToString(target), index, buffer, (void*) offset, size)
    g_bound_buffers[target] = buffer;
    if (!has_buffer(buffer) || buffer == 0) {
        GLES.glBindBufferRange(target, index, buffer, offset, size);
        CHECK_GL_ERROR
        return;
    }
    GLuint real_buffer = find_real_buffer(buffer);
    if (!real_buffer) {
        GLES.glGenBuffers(1, &real_buffer);
        modify_buffer(buffer, real_buffer);
        CHECK_GL_ERROR
    }
    GLES.glBindBufferRange(target, index, real_buffer, offset, size);
    CHECK_GL_ERROR
}

void glBindBufferBase(GLenum target, GLuint index, GLuint buffer) {
    LOG()
    LOG_D("glBindBufferBase, target = %s, index = %d, buffer = %d", glEnumToString(target), index, buffer)
    g_bound_buffers[target] = buffer;
    if (!has_buffer(buffer) || buffer == 0) {
        GLES.glBindBufferBase(target, index, buffer);
        CHECK_GL_ERROR
        return;
    }
    GLuint real_buffer = find_real_buffer(buffer);
    if (!real_buffer) {
        GLES.glGenBuffers(1, &real_buffer);
        modify_buffer(buffer, real_buffer);
        CHECK_GL_ERROR
    }
    GLES.glBindBufferBase(target, index, real_buffer);
    CHECK_GL_ERROR
}

void glBindVertexBuffer(GLuint bindingindex, GLuint buffer, GLintptr offset, GLsizei stride) {
    LOG()
    LOG_D("glBindVertexBuffer, bindingindex = %d, buffer = %d, offset = %p, stride = %i", bindingindex, buffer, offset, stride)
    // Todo: should record fake buffer binding here, when glGetVertexArrayIntegeri_v is called, should return fake buffer id
    if (!has_buffer(buffer) || buffer == 0) {
        GLES.glBindVertexBuffer(bindingindex, buffer, offset, stride);
        CHECK_GL_ERROR
        return;
    }
    GLuint real_buffer = find_real_buffer(buffer);
    if (!real_buffer) {
        GLES.glGenBuffers(1, &real_buffer);
        modify_buffer(buffer, real_buffer);
        CHECK_GL_ERROR
    }
    GLES.glBindVertexBuffer(bindingindex, real_buffer, offset, stride);
    CHECK_GL_ERROR
}

void glBufferData(GLenum target, GLsizeiptr size, const void *data, GLenum usage) {
    LOG()
    LOG_D("glBufferData, target = %s, size = %d, data = 0x%x, usage = %s",
          glEnumToString(target), size, data, glEnumToString(usage))
    GLES.glBufferData(target, size, data, usage);
    CHECK_GL_ERROR
}

void* glMapBuffer(GLenum target, GLenum access) {
    LOG()
    LOG_D("glMapBuffer, target = %s, access = %s", glEnumToString(target), glEnumToString(access))
    if(g_gles_caps.GL_OES_mapbuffer) {
        return GLES.glMapBufferOES(target, access);
    }
    if (get_binding_query(target) == 0) {
        return nullptr;
    }
    GLint current_buffer;
    GLES.glGetIntegerv(get_binding_query(target), &current_buffer);
    if (current_buffer == 0) {
        return nullptr;
    }
    if (g_active_mappings[current_buffer].mapped_ptr != nullptr) {
        return nullptr;
    }
    GLint buffer_size;
    GLES.glGetBufferParameteriv(target, GL_BUFFER_SIZE, &buffer_size);
    if (buffer_size <= 0 || glGetError() != GL_NO_ERROR) {
        return nullptr;
    }
    GLbitfield flags = 0;
    switch (access) {
        case GL_READ_ONLY:  flags = GL_MAP_READ_BIT; break;
        case GL_WRITE_ONLY: flags = GL_MAP_WRITE_BIT /*| GL_MAP_INVALIDATE_BUFFER_BIT*/; break;
        case GL_READ_WRITE: flags = GL_MAP_READ_BIT | GL_MAP_WRITE_BIT; break;
        default:
            return nullptr;
    }
    void* ptr = GLES.glMapBufferRange(target, 0, buffer_size, flags);
    if (!ptr) return nullptr;
    BufferMapping mapping;
    mapping.target = target;
    mapping.buffer_id = (GLuint)current_buffer;
    mapping.mapped_ptr = ptr;
#if GLOBAL_DEBUG || DEBUG
    if (target == GL_PIXEL_UNPACK_BUFFER) {
        mapping.client_ptr = malloc(buffer_size);
        memset(mapping.client_ptr, 0xFF, buffer_size);
    }
#endif
    mapping.size = buffer_size;
    mapping.flags = flags;
    mapping.is_dirty = (flags & GL_MAP_WRITE_BIT) ? GL_TRUE : GL_FALSE;
    g_active_mappings[current_buffer] = mapping;
    CHECK_GL_ERROR
#if GLOBAL_DEBUG || DEBUG
    if (target == GL_PIXEL_UNPACK_BUFFER)
        return mapping.client_ptr;
    else
        return ptr;
#else
    return ptr;
#endif
}

#if GLOBAL_DEBUG || DEBUG
#include <fstream>
#define BIN_FILE_PREFIX "/sdcard/MG/buf/"
#endif

extern "C" {
    GLAPI GLAPIENTRY void *glMapBufferARB(GLenum target, GLenum access) __attribute__((alias("glMapBuffer")));
    GLAPI GLAPIENTRY void *glBufferDataARB(GLenum target, GLenum access) __attribute__((alias("glBufferData")));
    GLAPI GLAPIENTRY GLboolean glUnmapBufferARB(GLenum target) __attribute__((alias("glUnmapBuffer")));
    GLAPI GLAPIENTRY void glBufferStorageARB(GLenum target, GLsizeiptr size, const void* data, GLbitfield flags) __attribute__((alias("glBufferStorage")));
    GLAPI GLAPIENTRY void glBindBufferARB(GLenum target, GLuint buffer) __attribute__((alias("glBindBuffer")));
}

GLboolean glUnmapBuffer(GLenum target) {
    LOG()
    if(g_gles_caps.GL_OES_mapbuffer)
        return GLES.glUnmapBuffer(target);

    GLint buffer;
    GLenum binding_query = get_binding_query(target);
    GLES.glGetIntegerv(binding_query, &buffer);

    if (buffer == 0)
        return GL_FALSE;

#if GLOBAL_DEBUG || DEBUG
//     Blit data from client side to OpenGL here
    if (target == GL_PIXEL_UNPACK_BUFFER) {
        auto &mapping = g_active_mappings[buffer];

        std::fstream fs(std::string(BIN_FILE_PREFIX) + "buf" + std::to_string(buffer) + ".bin", std::ios::out | std::ios::binary | std::ios::trunc);
        fs.write((const char*)mapping.client_ptr, mapping.size);
        fs.close();

//        memset(mapping.mapped_ptr, 0xFF, mapping.size);
        memcpy(mapping.mapped_ptr, mapping.client_ptr, mapping.size);
        free(mapping.client_ptr);
        mapping.client_ptr = nullptr;
    }
#endif

    GLboolean result = GLES.glUnmapBuffer(target);
    g_active_mappings.erase(buffer);
    CHECK_GL_ERROR
    return result;
}

void glBufferStorage(GLenum target, GLsizeiptr size, const void* data, GLbitfield flags) {
    LOG()
    if(GLES.glBufferStorageEXT)
        GLES.glBufferStorageEXT(target,size,data,flags);
    CHECK_GL_ERROR
}

void glGenVertexArrays(GLsizei n, GLuint *arrays) {
    LOG()
    LOG_D("glGenVertexArrays(%i, %p)", n, arrays)
    for (int i = 0; i < n; ++i) {
        arrays[i] = gen_array();
    }
}

void glDeleteVertexArrays(GLsizei n, const GLuint *arrays) {
    LOG()
    LOG_D("glDeleteVertexArrays(%i, %p)", n, arrays)
    for (int i = 0; i < n; ++i) {
        if (find_real_array(arrays[i])) {
            GLuint real_array = find_real_array(arrays[i]);
            GLES.glDeleteVertexArrays(1, &real_array);
            CHECK_GL_ERROR
        }
        remove_array(arrays[i]);
    }
}

GLboolean glIsVertexArray(GLuint array) {
    LOG()
    LOG_D("glIsVertexArray(%d)", array)
    return has_array(array);
}

void glBindVertexArray(GLuint array) {
    LOG()
    LOG_D("glBindVertexArray(%d)", array)
    bound_array = array;
    if (!has_array(array) || array == 0) {
        GLES.glBindVertexArray(array);
        CHECK_GL_ERROR
        return;
    }
    GLuint real_array = find_real_array(array);
    if (!real_array) {
        GLES.glGenVertexArrays(1, &real_array);
        modify_array(array, real_array);
        CHECK_GL_ERROR
    }
    GLES.glBindVertexArray(real_array);
    CHECK_GL_ERROR
}
