/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 * MACHINE GENERATED FILE, DO NOT EDIT
 */
package org.lwjgl.opengl;

import javax.annotation.*;

import java.nio.*;

import org.lwjgl.*;

import org.lwjgl.system.*;

import static org.lwjgl.system.Checks.*;
import static org.lwjgl.system.JNI.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

/**
 * Native bindings to the <a target="_blank" href="https://www.khronos.org/registry/OpenGL/extensions/ARB/ARB_vertex_shader.txt">ARB_vertex_shader</a> extension.
 * 
 * <p>This extension adds programmable vertex level processing to OpenGL. The application can write vertex shaders in a high level language as defined in the
 * OpenGL Shading Language specification. A vertex shader replaces the transformation, texture coordinate generation and lighting parts of OpenGL, and it
 * also adds texture access at the vertex level. Furthermore, management of vertex shader objects and loading generic attributes are discussed. A vertex
 * shader object, attached to a program object, can be compiled and linked to produce an executable that runs on the vertex processor in OpenGL.
 * This extension also defines how such an executable interacts with the fixed functionality vertex processing of OpenGL 1.4.</p>
 * 
 * <p>Promoted to core in {@link GL20 OpenGL 2.0}.</p>
 */
public class ARBVertexShader {

    /** Accepted by the {@code shaderType} argument of CreateShaderObjectARB and returned by the {@code params} parameter of GetObjectParameter{if}vARB. */
    public static final int GL_VERTEX_SHADER_ARB = 0x8B31;

    /** Accepted by the {@code pname} parameter of GetBooleanv, GetIntegerv, GetFloatv, and GetDoublev. */
    public static final int
        GL_MAX_VERTEX_UNIFORM_COMPONENTS_ARB    = 0x8B4A,
        GL_MAX_VARYING_FLOATS_ARB               = 0x8B4B,
        GL_MAX_VERTEX_ATTRIBS_ARB               = 0x8869,
        GL_MAX_TEXTURE_IMAGE_UNITS_ARB          = 0x8872,
        GL_MAX_VERTEX_TEXTURE_IMAGE_UNITS_ARB   = 0x8B4C,
        GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS_ARB = 0x8B4D,
        GL_MAX_TEXTURE_COORDS_ARB               = 0x8871;

    /**
     * Accepted by the {@code cap} parameter of Disable, Enable, and IsEnabled, and by the {@code pname} parameter of GetBooleanv, GetIntegerv, GetFloatv, and
     * GetDoublev.
     */
    public static final int
        GL_VERTEX_PROGRAM_POINT_SIZE_ARB = 0x8642,
        GL_VERTEX_PROGRAM_TWO_SIDE_ARB   = 0x8643;

    /** Accepted by the {@code pname} parameter GetObjectParameter{if}vARB. */
    public static final int
        GL_OBJECT_ACTIVE_ATTRIBUTES_ARB           = 0x8B89,
        GL_OBJECT_ACTIVE_ATTRIBUTE_MAX_LENGTH_ARB = 0x8B8A;

    /** Accepted by the {@code pname} parameter of GetVertexAttrib{dfi}vARB. */
    public static final int
        GL_VERTEX_ATTRIB_ARRAY_ENABLED_ARB    = 0x8622,
        GL_VERTEX_ATTRIB_ARRAY_SIZE_ARB       = 0x8623,
        GL_VERTEX_ATTRIB_ARRAY_STRIDE_ARB     = 0x8624,
        GL_VERTEX_ATTRIB_ARRAY_TYPE_ARB       = 0x8625,
        GL_VERTEX_ATTRIB_ARRAY_NORMALIZED_ARB = 0x886A,
        GL_CURRENT_VERTEX_ATTRIB_ARB          = 0x8626;

    /** Accepted by the {@code pname} parameter of GetVertexAttribPointervARB. */
    public static final int GL_VERTEX_ATTRIB_ARRAY_POINTER_ARB = 0x8645;

    /** Returned by the {@code type} parameter of GetActiveAttribARB. */
    public static final int
        GL_FLOAT_VEC2_ARB = 0x8B50,
        GL_FLOAT_VEC3_ARB = 0x8B51,
        GL_FLOAT_VEC4_ARB = 0x8B52,
        GL_FLOAT_MAT2_ARB = 0x8B5A,
        GL_FLOAT_MAT3_ARB = 0x8B5B,
        GL_FLOAT_MAT4_ARB = 0x8B5C;

    static { GL.initialize(); }

    protected ARBVertexShader() {
        throw new UnsupportedOperationException();
    }
    
// -- Begin LWJGL2 part --
    public static void glVertexAttribPointerARB(int index, int size, boolean unsigned, boolean normalized, int stride, ByteBuffer buffer) {
        glVertexAttribPointerARB(index, size, unsigned ? GL11.GL_UNSIGNED_BYTE : GL11.GL_BYTE, normalized, stride, buffer);
    }

    public static void glVertexAttribPointerARB(int index, int size, boolean unsigned, boolean normalized, int stride, IntBuffer buffer) {
        glVertexAttribPointerARB(index, size, unsigned ? GL11.GL_UNSIGNED_INT : GL11.GL_INT, normalized, stride, buffer);
    }
    
    public static void glVertexAttribPointerARB(int index, int size, boolean unsigned, boolean normalized, int stride, ShortBuffer buffer) {
        glVertexAttribPointerARB(index, size, unsigned ? GL11.GL_UNSIGNED_SHORT : GL11.GL_SHORT, normalized, stride, buffer);
    }
// -- End LWJGL2 part --

    // --- [ glVertexAttrib1fARB ] ---

    /**
     * Specifies the value of a generic vertex attribute. The y and z components are implicitly set to 0.0f and w to 1.0f.
     *
     * @param index the index of the generic vertex attribute to be modified
     * @param v0    the vertex attribute x component
     */
    public static native void glVertexAttrib1fARB(@NativeType("GLuint") int index, @NativeType("GLfloat") float v0);

    // --- [ glVertexAttrib1sARB ] ---

    /**
     * Short version of {@link #glVertexAttrib1fARB VertexAttrib1fARB}.
     *
     * @param index the index of the generic vertex attribute to be modified
     * @param v0    the vertex attribute x component
     */
    public static native void glVertexAttrib1sARB(@NativeType("GLuint") int index, @NativeType("GLshort") short v0);

    // --- [ glVertexAttrib1dARB ] ---

    /**
     * Double version of {@link #glVertexAttrib1fARB VertexAttrib1fARB}.
     *
     * @param index the index of the generic vertex attribute to be modified
     * @param v0    the vertex attribute x component
     */
    public static native void glVertexAttrib1dARB(@NativeType("GLuint") int index, @NativeType("GLdouble") double v0);

    // --- [ glVertexAttrib2fARB ] ---

    /**
     * Specifies the value of a generic vertex attribute. The y component is implicitly set to 0.0f and w to 1.0f.
     *
     * @param index the index of the generic vertex attribute to be modified
     * @param v0    the vertex attribute x component
     * @param v1    the vertex attribute y component
     */
    public static native void glVertexAttrib2fARB(@NativeType("GLuint") int index, @NativeType("GLfloat") float v0, @NativeType("GLfloat") float v1);

    // --- [ glVertexAttrib2sARB ] ---

    /**
     * Short version of {@link #glVertexAttrib2fARB VertexAttrib2fARB}.
     *
     * @param index the index of the generic vertex attribute to be modified
     * @param v0    the vertex attribute x component
     * @param v1    the vertex attribute y component
     */
    public static native void glVertexAttrib2sARB(@NativeType("GLuint") int index, @NativeType("GLshort") short v0, @NativeType("GLshort") short v1);

    // --- [ glVertexAttrib2dARB ] ---

    /**
     * Double version of {@link #glVertexAttrib2fARB VertexAttrib2fARB}.
     *
     * @param index the index of the generic vertex attribute to be modified
     * @param v0    the vertex attribute x component
     * @param v1    the vertex attribute y component
     */
    public static native void glVertexAttrib2dARB(@NativeType("GLuint") int index, @NativeType("GLdouble") double v0, @NativeType("GLdouble") double v1);

    // --- [ glVertexAttrib3fARB ] ---

    /**
     * Specifies the value of a generic vertex attribute. The w is implicitly set to 1.0f.
     *
     * @param index the index of the generic vertex attribute to be modified
     * @param v0    the vertex attribute x component
     * @param v1    the vertex attribute y component
     * @param v2    the vertex attribute z component
     */
    public static native void glVertexAttrib3fARB(@NativeType("GLuint") int index, @NativeType("GLfloat") float v0, @NativeType("GLfloat") float v1, @NativeType("GLfloat") float v2);

    // --- [ glVertexAttrib3sARB ] ---

    /**
     * Short version of {@link #glVertexAttrib3fARB VertexAttrib3fARB}.
     *
     * @param index the index of the generic vertex attribute to be modified
     * @param v0    the vertex attribute x component
     * @param v1    the vertex attribute y component
     * @param v2    the vertex attribute z component
     */
    public static native void glVertexAttrib3sARB(@NativeType("GLuint") int index, @NativeType("GLshort") short v0, @NativeType("GLshort") short v1, @NativeType("GLshort") short v2);

    // --- [ glVertexAttrib3dARB ] ---

    /**
     * Double version of {@link #glVertexAttrib3fARB VertexAttrib3fARB}.
     *
     * @param index the index of the generic vertex attribute to be modified
     * @param v0    the vertex attribute x component
     * @param v1    the vertex attribute y component
     * @param v2    the vertex attribute z component
     */
    public static native void glVertexAttrib3dARB(@NativeType("GLuint") int index, @NativeType("GLdouble") double v0, @NativeType("GLdouble") double v1, @NativeType("GLdouble") double v2);

    // --- [ glVertexAttrib4fARB ] ---

    /**
     * Specifies the value of a generic vertex attribute.
     *
     * @param index the index of the generic vertex attribute to be modified
     * @param v0    the vertex attribute x component
     * @param v1    the vertex attribute y component
     * @param v2    the vertex attribute z component
     * @param v3    the vertex attribute w component
     */
    public static native void glVertexAttrib4fARB(@NativeType("GLuint") int index, @NativeType("GLfloat") float v0, @NativeType("GLfloat") float v1, @NativeType("GLfloat") float v2, @NativeType("GLfloat") float v3);

    // --- [ glVertexAttrib4sARB ] ---

    /**
     * Short version of {@link #glVertexAttrib4fARB VertexAttrib4fARB}.
     *
     * @param index the index of the generic vertex attribute to be modified
     * @param v0    the vertex attribute x component
     * @param v1    the vertex attribute y component
     * @param v2    the vertex attribute z component
     * @param v3    the vertex attribute w component
     */
    public static native void glVertexAttrib4sARB(@NativeType("GLuint") int index, @NativeType("GLshort") short v0, @NativeType("GLshort") short v1, @NativeType("GLshort") short v2, @NativeType("GLshort") short v3);

    // --- [ glVertexAttrib4dARB ] ---

    /**
     * Double version of {@link #glVertexAttrib4fARB VertexAttrib4fARB}.
     *
     * @param index the index of the generic vertex attribute to be modified
     * @param v0    the vertex attribute x component
     * @param v1    the vertex attribute y component
     * @param v2    the vertex attribute z component
     * @param v3    the vertex attribute w component
     */
    public static native void glVertexAttrib4dARB(@NativeType("GLuint") int index, @NativeType("GLdouble") double v0, @NativeType("GLdouble") double v1, @NativeType("GLdouble") double v2, @NativeType("GLdouble") double v3);

    // --- [ glVertexAttrib4NubARB ] ---

    /**
     * Normalized unsigned byte version of {@link #glVertexAttrib4fARB VertexAttrib4fARB}.
     *
     * @param index the index of the generic vertex attribute to be modified
     * @param x     the vertex attribute x component
     * @param y     the vertex attribute y component
     * @param z     the vertex attribute z component
     * @param w     the vertex attribute w component
     */
    public static native void glVertexAttrib4NubARB(@NativeType("GLuint") int index, @NativeType("GLubyte") byte x, @NativeType("GLubyte") byte y, @NativeType("GLubyte") byte z, @NativeType("GLubyte") byte w);

    // --- [ glVertexAttrib1fvARB ] ---

    /** Unsafe version of: {@link #glVertexAttrib1fvARB VertexAttrib1fvARB} */
    public static native void nglVertexAttrib1fvARB(int index, long v);

    /**
     * Pointer version of {@link #glVertexAttrib1fARB VertexAttrib1fARB}.
     *
     * @param index the index of the generic vertex attribute to be modified
     * @param v     the vertex attribute buffer
     */
    public static void glVertexAttrib1fvARB(@NativeType("GLuint") int index, @NativeType("GLfloat const *") FloatBuffer v) {
        if (CHECKS) {
            check(v, 1);
        }
        nglVertexAttrib1fvARB(index, memAddress(v));
    }

    // --- [ glVertexAttrib1svARB ] ---

    /** Unsafe version of: {@link #glVertexAttrib1svARB VertexAttrib1svARB} */
    public static native void nglVertexAttrib1svARB(int index, long v);

    /**
     * Pointer version of {@link #glVertexAttrib1sARB VertexAttrib1sARB}.
     *
     * @param index the index of the generic vertex attribute to be modified
     * @param v     the vertex attribute buffer
     */
    public static void glVertexAttrib1svARB(@NativeType("GLuint") int index, @NativeType("GLshort const *") ShortBuffer v) {
        if (CHECKS) {
            check(v, 1);
        }
        nglVertexAttrib1svARB(index, memAddress(v));
    }

    // --- [ glVertexAttrib1dvARB ] ---

    /** Unsafe version of: {@link #glVertexAttrib1dvARB VertexAttrib1dvARB} */
    public static native void nglVertexAttrib1dvARB(int index, long v);

    /**
     * Pointer version of {@link #glVertexAttrib1dARB VertexAttrib1dARB}.
     *
     * @param index the index of the generic vertex attribute to be modified
     * @param v     the vertex attribute buffer
     */
    public static void glVertexAttrib1dvARB(@NativeType("GLuint") int index, @NativeType("GLdouble const *") DoubleBuffer v) {
        if (CHECKS) {
            check(v, 1);
        }
        nglVertexAttrib1dvARB(index, memAddress(v));
    }

    // --- [ glVertexAttrib2fvARB ] ---

    /** Unsafe version of: {@link #glVertexAttrib2fvARB VertexAttrib2fvARB} */
    public static native void nglVertexAttrib2fvARB(int index, long v);

    /**
     * Pointer version of {@link #glVertexAttrib2fARB VertexAttrib2fARB}.
     *
     * @param index the index of the generic vertex attribute to be modified
     * @param v     the vertex attribute buffer
     */
    public static void glVertexAttrib2fvARB(@NativeType("GLuint") int index, @NativeType("GLfloat const *") FloatBuffer v) {
        if (CHECKS) {
            check(v, 2);
        }
        nglVertexAttrib2fvARB(index, memAddress(v));
    }

    // --- [ glVertexAttrib2svARB ] ---

    /** Unsafe version of: {@link #glVertexAttrib2svARB VertexAttrib2svARB} */
    public static native void nglVertexAttrib2svARB(int index, long v);

    /**
     * Pointer version of {@link #glVertexAttrib2sARB VertexAttrib2sARB}.
     *
     * @param index the index of the generic vertex attribute to be modified
     * @param v     the vertex attribute buffer
     */
    public static void glVertexAttrib2svARB(@NativeType("GLuint") int index, @NativeType("GLshort const *") ShortBuffer v) {
        if (CHECKS) {
            check(v, 2);
        }
        nglVertexAttrib2svARB(index, memAddress(v));
    }

    // --- [ glVertexAttrib2dvARB ] ---

    /** Unsafe version of: {@link #glVertexAttrib2dvARB VertexAttrib2dvARB} */
    public static native void nglVertexAttrib2dvARB(int index, long v);

    /**
     * Pointer version of {@link #glVertexAttrib2dARB VertexAttrib2dARB}.
     *
     * @param index the index of the generic vertex attribute to be modified
     * @param v     the vertex attribute buffer
     */
    public static void glVertexAttrib2dvARB(@NativeType("GLuint") int index, @NativeType("GLdouble const *") DoubleBuffer v) {
        if (CHECKS) {
            check(v, 2);
        }
        nglVertexAttrib2dvARB(index, memAddress(v));
    }

    // --- [ glVertexAttrib3fvARB ] ---

    /** Unsafe version of: {@link #glVertexAttrib3fvARB VertexAttrib3fvARB} */
    public static native void nglVertexAttrib3fvARB(int index, long v);

    /**
     * Pointer version of {@link #glVertexAttrib3fARB VertexAttrib3fARB}.
     *
     * @param index the index of the generic vertex attribute to be modified
     * @param v     the vertex attribute buffer
     */
    public static void glVertexAttrib3fvARB(@NativeType("GLuint") int index, @NativeType("GLfloat const *") FloatBuffer v) {
        if (CHECKS) {
            check(v, 3);
        }
        nglVertexAttrib3fvARB(index, memAddress(v));
    }

    // --- [ glVertexAttrib3svARB ] ---

    /** Unsafe version of: {@link #glVertexAttrib3svARB VertexAttrib3svARB} */
    public static native void nglVertexAttrib3svARB(int index, long v);

    /**
     * Pointer version of {@link #glVertexAttrib3sARB VertexAttrib3sARB}.
     *
     * @param index the index of the generic vertex attribute to be modified
     * @param v     the vertex attribute buffer
     */
    public static void glVertexAttrib3svARB(@NativeType("GLuint") int index, @NativeType("GLshort const *") ShortBuffer v) {
        if (CHECKS) {
            check(v, 3);
        }
        nglVertexAttrib3svARB(index, memAddress(v));
    }

    // --- [ glVertexAttrib3dvARB ] ---

    /** Unsafe version of: {@link #glVertexAttrib3dvARB VertexAttrib3dvARB} */
    public static native void nglVertexAttrib3dvARB(int index, long v);

    /**
     * Pointer version of {@link #glVertexAttrib3dARB VertexAttrib3dARB}.
     *
     * @param index the index of the generic vertex attribute to be modified
     * @param v     the vertex attribute buffer
     */
    public static void glVertexAttrib3dvARB(@NativeType("GLuint") int index, @NativeType("GLdouble const *") DoubleBuffer v) {
        if (CHECKS) {
            check(v, 3);
        }
        nglVertexAttrib3dvARB(index, memAddress(v));
    }

    // --- [ glVertexAttrib4fvARB ] ---

    /** Unsafe version of: {@link #glVertexAttrib4fvARB VertexAttrib4fvARB} */
    public static native void nglVertexAttrib4fvARB(int index, long v);

    /**
     * Pointer version of {@link #glVertexAttrib4fARB VertexAttrib4fARB}.
     *
     * @param index the index of the generic vertex attribute to be modified
     * @param v     the vertex attribute buffer
     */
    public static void glVertexAttrib4fvARB(@NativeType("GLuint") int index, @NativeType("GLfloat const *") FloatBuffer v) {
        if (CHECKS) {
            check(v, 4);
        }
        nglVertexAttrib4fvARB(index, memAddress(v));
    }

    // --- [ glVertexAttrib4svARB ] ---

    /** Unsafe version of: {@link #glVertexAttrib4svARB VertexAttrib4svARB} */
    public static native void nglVertexAttrib4svARB(int index, long v);

    /**
     * Pointer version of {@link #glVertexAttrib4sARB VertexAttrib4sARB}.
     *
     * @param index the index of the generic vertex attribute to be modified
     * @param v     the vertex attribute buffer
     */
    public static void glVertexAttrib4svARB(@NativeType("GLuint") int index, @NativeType("GLshort const *") ShortBuffer v) {
        if (CHECKS) {
            check(v, 4);
        }
        nglVertexAttrib4svARB(index, memAddress(v));
    }

    // --- [ glVertexAttrib4dvARB ] ---

    /** Unsafe version of: {@link #glVertexAttrib4dvARB VertexAttrib4dvARB} */
    public static native void nglVertexAttrib4dvARB(int index, long v);

    /**
     * Pointer version of {@link #glVertexAttrib4dARB VertexAttrib4dARB}.
     *
     * @param index the index of the generic vertex attribute to be modified
     * @param v     the vertex attribute buffer
     */
    public static void glVertexAttrib4dvARB(@NativeType("GLuint") int index, @NativeType("GLdouble const *") DoubleBuffer v) {
        if (CHECKS) {
            check(v, 4);
        }
        nglVertexAttrib4dvARB(index, memAddress(v));
    }

    // --- [ glVertexAttrib4ivARB ] ---

    /** Unsafe version of: {@link #glVertexAttrib4ivARB VertexAttrib4ivARB} */
    public static native void nglVertexAttrib4ivARB(int index, long v);

    /**
     * Integer pointer version of {@link #glVertexAttrib4fARB VertexAttrib4fARB}.
     *
     * @param index the index of the generic vertex attribute to be modified
     * @param v     the vertex attribute buffer
     */
    public static void glVertexAttrib4ivARB(@NativeType("GLuint") int index, @NativeType("GLint const *") IntBuffer v) {
        if (CHECKS) {
            check(v, 4);
        }
        nglVertexAttrib4ivARB(index, memAddress(v));
    }

    // --- [ glVertexAttrib4bvARB ] ---

    /** Unsafe version of: {@link #glVertexAttrib4bvARB VertexAttrib4bvARB} */
    public static native void nglVertexAttrib4bvARB(int index, long v);

    /**
     * Byte pointer version of {@link #glVertexAttrib4fARB VertexAttrib4fARB}.
     *
     * @param index the index of the generic vertex attribute to be modified
     * @param v     the vertex attribute buffer
     */
    public static void glVertexAttrib4bvARB(@NativeType("GLuint") int index, @NativeType("GLbyte const *") ByteBuffer v) {
        if (CHECKS) {
            check(v, 4);
        }
        nglVertexAttrib4bvARB(index, memAddress(v));
    }

    // --- [ glVertexAttrib4ubvARB ] ---

    /** Unsafe version of: {@link #glVertexAttrib4ubvARB VertexAttrib4ubvARB} */
    public static native void nglVertexAttrib4ubvARB(int index, long v);

    /**
     * Pointer version of {@link #glVertexAttrib4NubARB VertexAttrib4NubARB}.
     *
     * @param index the index of the generic vertex attribute to be modified
     * @param v     the vertex attribute buffer
     */
    public static void glVertexAttrib4ubvARB(@NativeType("GLuint") int index, @NativeType("GLubyte const *") ByteBuffer v) {
        if (CHECKS) {
            check(v, 4);
        }
        nglVertexAttrib4ubvARB(index, memAddress(v));
    }

    // --- [ glVertexAttrib4usvARB ] ---

    /** Unsafe version of: {@link #glVertexAttrib4usvARB VertexAttrib4usvARB} */
    public static native void nglVertexAttrib4usvARB(int index, long v);

    /**
     * Unsigned short pointer version of {@link #glVertexAttrib4fARB VertexAttrib4fARB}.
     *
     * @param index the index of the generic vertex attribute to be modified
     * @param v     the vertex attribute buffer
     */
    public static void glVertexAttrib4usvARB(@NativeType("GLuint") int index, @NativeType("GLushort const *") ShortBuffer v) {
        if (CHECKS) {
            check(v, 4);
        }
        nglVertexAttrib4usvARB(index, memAddress(v));
    }

    // --- [ glVertexAttrib4uivARB ] ---

    /** Unsafe version of: {@link #glVertexAttrib4uivARB VertexAttrib4uivARB} */
    public static native void nglVertexAttrib4uivARB(int index, long v);

    /**
     * Unsigned int pointer version of {@link #glVertexAttrib4fARB VertexAttrib4fARB}.
     *
     * @param index the index of the generic vertex attribute to be modified
     * @param v     the vertex attribute buffer
     */
    public static void glVertexAttrib4uivARB(@NativeType("GLuint") int index, @NativeType("GLuint const *") IntBuffer v) {
        if (CHECKS) {
            check(v, 4);
        }
        nglVertexAttrib4uivARB(index, memAddress(v));
    }

    // --- [ glVertexAttrib4NbvARB ] ---

    /** Unsafe version of: {@link #glVertexAttrib4NbvARB VertexAttrib4NbvARB} */
    public static native void nglVertexAttrib4NbvARB(int index, long v);

    /**
     * Normalized byte pointer version of {@link #glVertexAttrib4fARB VertexAttrib4fARB}.
     *
     * @param index the index of the generic vertex attribute to be modified
     * @param v     the vertex attribute buffer
     */
    public static void glVertexAttrib4NbvARB(@NativeType("GLuint") int index, @NativeType("GLbyte const *") ByteBuffer v) {
        if (CHECKS) {
            check(v, 4);
        }
        nglVertexAttrib4NbvARB(index, memAddress(v));
    }

    // --- [ glVertexAttrib4NsvARB ] ---

    /** Unsafe version of: {@link #glVertexAttrib4NsvARB VertexAttrib4NsvARB} */
    public static native void nglVertexAttrib4NsvARB(int index, long v);

    /**
     * Normalized short pointer version of {@link #glVertexAttrib4fARB VertexAttrib4fARB}.
     *
     * @param index the index of the generic vertex attribute to be modified
     * @param v     the vertex attribute buffer
     */
    public static void glVertexAttrib4NsvARB(@NativeType("GLuint") int index, @NativeType("GLshort const *") ShortBuffer v) {
        if (CHECKS) {
            check(v, 4);
        }
        nglVertexAttrib4NsvARB(index, memAddress(v));
    }

    // --- [ glVertexAttrib4NivARB ] ---

    /** Unsafe version of: {@link #glVertexAttrib4NivARB VertexAttrib4NivARB} */
    public static native void nglVertexAttrib4NivARB(int index, long v);

    /**
     * Normalized int pointer version of {@link #glVertexAttrib4fARB VertexAttrib4fARB}.
     *
     * @param index the index of the generic vertex attribute to be modified
     * @param v     the vertex attribute buffer
     */
    public static void glVertexAttrib4NivARB(@NativeType("GLuint") int index, @NativeType("GLint const *") IntBuffer v) {
        if (CHECKS) {
            check(v, 4);
        }
        nglVertexAttrib4NivARB(index, memAddress(v));
    }

    // --- [ glVertexAttrib4NubvARB ] ---

    /** Unsafe version of: {@link #glVertexAttrib4NubvARB VertexAttrib4NubvARB} */
    public static native void nglVertexAttrib4NubvARB(int index, long v);

    /**
     * Normalized unsigned byte pointer version of {@link #glVertexAttrib4fARB VertexAttrib4fARB}.
     *
     * @param index the index of the generic vertex attribute to be modified
     * @param v     the vertex attribute buffer
     */
    public static void glVertexAttrib4NubvARB(@NativeType("GLuint") int index, @NativeType("GLubyte const *") ByteBuffer v) {
        if (CHECKS) {
            check(v, 4);
        }
        nglVertexAttrib4NubvARB(index, memAddress(v));
    }

    // --- [ glVertexAttrib4NusvARB ] ---

    /** Unsafe version of: {@link #glVertexAttrib4NusvARB VertexAttrib4NusvARB} */
    public static native void nglVertexAttrib4NusvARB(int index, long v);

    /**
     * Normalized unsigned short pointer version of {@link #glVertexAttrib4fARB VertexAttrib4fARB}.
     *
     * @param index the index of the generic vertex attribute to be modified
     * @param v     the vertex attribute buffer
     */
    public static void glVertexAttrib4NusvARB(@NativeType("GLuint") int index, @NativeType("GLushort const *") ShortBuffer v) {
        if (CHECKS) {
            check(v, 4);
        }
        nglVertexAttrib4NusvARB(index, memAddress(v));
    }

    // --- [ glVertexAttrib4NuivARB ] ---

    /** Unsafe version of: {@link #glVertexAttrib4NuivARB VertexAttrib4NuivARB} */
    public static native void nglVertexAttrib4NuivARB(int index, long v);

    /**
     * Normalized unsigned int pointer version of {@link #glVertexAttrib4fARB VertexAttrib4fARB}.
     *
     * @param index the index of the generic vertex attribute to be modified
     * @param v     the vertex attribute buffer
     */
    public static void glVertexAttrib4NuivARB(@NativeType("GLuint") int index, @NativeType("GLuint const *") IntBuffer v) {
        if (CHECKS) {
            check(v, 4);
        }
        nglVertexAttrib4NuivARB(index, memAddress(v));
    }

    // --- [ glVertexAttribPointerARB ] ---

    /** Unsafe version of: {@link #glVertexAttribPointerARB VertexAttribPointerARB} */
    public static native void nglVertexAttribPointerARB(int index, int size, int type, boolean normalized, int stride, long pointer);

    /**
     * Specifies the location and organization of a vertex attribute array.
     *
     * @param index      the index of the generic vertex attribute to be modified
     * @param size       the number of values per vertex that are stored in the array. The initial value is 4. One of:<br><table><tr><td>1</td><td>2</td><td>3</td><td>4</td><td>{@link GL12#GL_BGRA BGRA}</td></tr></table>
     * @param type       the data type of each component in the array. The initial value is GL_FLOAT. One of:<br><table><tr><td>{@link GL11#GL_BYTE BYTE}</td><td>{@link GL11#GL_UNSIGNED_BYTE UNSIGNED_BYTE}</td><td>{@link GL11#GL_SHORT SHORT}</td><td>{@link GL11#GL_UNSIGNED_SHORT UNSIGNED_SHORT}</td><td>{@link GL11#GL_INT INT}</td><td>{@link GL11#GL_UNSIGNED_INT UNSIGNED_INT}</td><td>{@link GL30#GL_HALF_FLOAT HALF_FLOAT}</td><td>{@link GL11#GL_FLOAT FLOAT}</td></tr><tr><td>{@link GL11#GL_DOUBLE DOUBLE}</td><td>{@link GL12#GL_UNSIGNED_INT_2_10_10_10_REV UNSIGNED_INT_2_10_10_10_REV}</td><td>{@link GL33#GL_INT_2_10_10_10_REV INT_2_10_10_10_REV}</td><td>{@link GL41#GL_FIXED FIXED}</td></tr></table>
     * @param normalized whether fixed-point data values should be normalized or converted directly as fixed-point values when they are accessed
     * @param stride     the byte offset between consecutive generic vertex attributes. If stride is 0, the generic vertex attributes are understood to be tightly packed in
     *                   the array. The initial value is 0.
     * @param pointer    the vertex attribute data or the offset of the first component of the first generic vertex attribute in the array in the data store of the buffer
     *                   currently bound to the {@link GL15#GL_ARRAY_BUFFER ARRAY_BUFFER} target. The initial value is 0.
     */
    public static void glVertexAttribPointerARB(@NativeType("GLuint") int index, @NativeType("GLint") int size, @NativeType("GLenum") int type, @NativeType("GLboolean") boolean normalized, @NativeType("GLsizei") int stride, @NativeType("void const *") ByteBuffer pointer) {
        nglVertexAttribPointerARB(index, size, type, normalized, stride, memAddress(pointer));
    }

    /**
     * Specifies the location and organization of a vertex attribute array.
     *
     * @param index      the index of the generic vertex attribute to be modified
     * @param size       the number of values per vertex that are stored in the array. The initial value is 4. One of:<br><table><tr><td>1</td><td>2</td><td>3</td><td>4</td><td>{@link GL12#GL_BGRA BGRA}</td></tr></table>
     * @param type       the data type of each component in the array. The initial value is GL_FLOAT. One of:<br><table><tr><td>{@link GL11#GL_BYTE BYTE}</td><td>{@link GL11#GL_UNSIGNED_BYTE UNSIGNED_BYTE}</td><td>{@link GL11#GL_SHORT SHORT}</td><td>{@link GL11#GL_UNSIGNED_SHORT UNSIGNED_SHORT}</td><td>{@link GL11#GL_INT INT}</td><td>{@link GL11#GL_UNSIGNED_INT UNSIGNED_INT}</td><td>{@link GL30#GL_HALF_FLOAT HALF_FLOAT}</td><td>{@link GL11#GL_FLOAT FLOAT}</td></tr><tr><td>{@link GL11#GL_DOUBLE DOUBLE}</td><td>{@link GL12#GL_UNSIGNED_INT_2_10_10_10_REV UNSIGNED_INT_2_10_10_10_REV}</td><td>{@link GL33#GL_INT_2_10_10_10_REV INT_2_10_10_10_REV}</td><td>{@link GL41#GL_FIXED FIXED}</td></tr></table>
     * @param normalized whether fixed-point data values should be normalized or converted directly as fixed-point values when they are accessed
     * @param stride     the byte offset between consecutive generic vertex attributes. If stride is 0, the generic vertex attributes are understood to be tightly packed in
     *                   the array. The initial value is 0.
     * @param pointer    the vertex attribute data or the offset of the first component of the first generic vertex attribute in the array in the data store of the buffer
     *                   currently bound to the {@link GL15#GL_ARRAY_BUFFER ARRAY_BUFFER} target. The initial value is 0.
     */
    public static void glVertexAttribPointerARB(@NativeType("GLuint") int index, @NativeType("GLint") int size, @NativeType("GLenum") int type, @NativeType("GLboolean") boolean normalized, @NativeType("GLsizei") int stride, @NativeType("void const *") long pointer) {
        nglVertexAttribPointerARB(index, size, type, normalized, stride, pointer);
    }

    /**
     * Specifies the location and organization of a vertex attribute array.
     *
     * @param index      the index of the generic vertex attribute to be modified
     * @param size       the number of values per vertex that are stored in the array. The initial value is 4. One of:<br><table><tr><td>1</td><td>2</td><td>3</td><td>4</td><td>{@link GL12#GL_BGRA BGRA}</td></tr></table>
     * @param type       the data type of each component in the array. The initial value is GL_FLOAT. One of:<br><table><tr><td>{@link GL11#GL_BYTE BYTE}</td><td>{@link GL11#GL_UNSIGNED_BYTE UNSIGNED_BYTE}</td><td>{@link GL11#GL_SHORT SHORT}</td><td>{@link GL11#GL_UNSIGNED_SHORT UNSIGNED_SHORT}</td><td>{@link GL11#GL_INT INT}</td><td>{@link GL11#GL_UNSIGNED_INT UNSIGNED_INT}</td><td>{@link GL30#GL_HALF_FLOAT HALF_FLOAT}</td><td>{@link GL11#GL_FLOAT FLOAT}</td></tr><tr><td>{@link GL11#GL_DOUBLE DOUBLE}</td><td>{@link GL12#GL_UNSIGNED_INT_2_10_10_10_REV UNSIGNED_INT_2_10_10_10_REV}</td><td>{@link GL33#GL_INT_2_10_10_10_REV INT_2_10_10_10_REV}</td><td>{@link GL41#GL_FIXED FIXED}</td></tr></table>
     * @param normalized whether fixed-point data values should be normalized or converted directly as fixed-point values when they are accessed
     * @param stride     the byte offset between consecutive generic vertex attributes. If stride is 0, the generic vertex attributes are understood to be tightly packed in
     *                   the array. The initial value is 0.
     * @param pointer    the vertex attribute data or the offset of the first component of the first generic vertex attribute in the array in the data store of the buffer
     *                   currently bound to the {@link GL15#GL_ARRAY_BUFFER ARRAY_BUFFER} target. The initial value is 0.
     */
    public static void glVertexAttribPointerARB(@NativeType("GLuint") int index, @NativeType("GLint") int size, @NativeType("GLenum") int type, @NativeType("GLboolean") boolean normalized, @NativeType("GLsizei") int stride, @NativeType("void const *") ShortBuffer pointer) {
        nglVertexAttribPointerARB(index, size, type, normalized, stride, memAddress(pointer));
    }

    /**
     * Specifies the location and organization of a vertex attribute array.
     *
     * @param index      the index of the generic vertex attribute to be modified
     * @param size       the number of values per vertex that are stored in the array. The initial value is 4. One of:<br><table><tr><td>1</td><td>2</td><td>3</td><td>4</td><td>{@link GL12#GL_BGRA BGRA}</td></tr></table>
     * @param type       the data type of each component in the array. The initial value is GL_FLOAT. One of:<br><table><tr><td>{@link GL11#GL_BYTE BYTE}</td><td>{@link GL11#GL_UNSIGNED_BYTE UNSIGNED_BYTE}</td><td>{@link GL11#GL_SHORT SHORT}</td><td>{@link GL11#GL_UNSIGNED_SHORT UNSIGNED_SHORT}</td><td>{@link GL11#GL_INT INT}</td><td>{@link GL11#GL_UNSIGNED_INT UNSIGNED_INT}</td><td>{@link GL30#GL_HALF_FLOAT HALF_FLOAT}</td><td>{@link GL11#GL_FLOAT FLOAT}</td></tr><tr><td>{@link GL11#GL_DOUBLE DOUBLE}</td><td>{@link GL12#GL_UNSIGNED_INT_2_10_10_10_REV UNSIGNED_INT_2_10_10_10_REV}</td><td>{@link GL33#GL_INT_2_10_10_10_REV INT_2_10_10_10_REV}</td><td>{@link GL41#GL_FIXED FIXED}</td></tr></table>
     * @param normalized whether fixed-point data values should be normalized or converted directly as fixed-point values when they are accessed
     * @param stride     the byte offset between consecutive generic vertex attributes. If stride is 0, the generic vertex attributes are understood to be tightly packed in
     *                   the array. The initial value is 0.
     * @param pointer    the vertex attribute data or the offset of the first component of the first generic vertex attribute in the array in the data store of the buffer
     *                   currently bound to the {@link GL15#GL_ARRAY_BUFFER ARRAY_BUFFER} target. The initial value is 0.
     */
    public static void glVertexAttribPointerARB(@NativeType("GLuint") int index, @NativeType("GLint") int size, @NativeType("GLenum") int type, @NativeType("GLboolean") boolean normalized, @NativeType("GLsizei") int stride, @NativeType("void const *") IntBuffer pointer) {
        nglVertexAttribPointerARB(index, size, type, normalized, stride, memAddress(pointer));
    }

    /**
     * Specifies the location and organization of a vertex attribute array.
     *
     * @param index      the index of the generic vertex attribute to be modified
     * @param size       the number of values per vertex that are stored in the array. The initial value is 4. One of:<br><table><tr><td>1</td><td>2</td><td>3</td><td>4</td><td>{@link GL12#GL_BGRA BGRA}</td></tr></table>
     * @param type       the data type of each component in the array. The initial value is GL_FLOAT. One of:<br><table><tr><td>{@link GL11#GL_BYTE BYTE}</td><td>{@link GL11#GL_UNSIGNED_BYTE UNSIGNED_BYTE}</td><td>{@link GL11#GL_SHORT SHORT}</td><td>{@link GL11#GL_UNSIGNED_SHORT UNSIGNED_SHORT}</td><td>{@link GL11#GL_INT INT}</td><td>{@link GL11#GL_UNSIGNED_INT UNSIGNED_INT}</td><td>{@link GL30#GL_HALF_FLOAT HALF_FLOAT}</td><td>{@link GL11#GL_FLOAT FLOAT}</td></tr><tr><td>{@link GL11#GL_DOUBLE DOUBLE}</td><td>{@link GL12#GL_UNSIGNED_INT_2_10_10_10_REV UNSIGNED_INT_2_10_10_10_REV}</td><td>{@link GL33#GL_INT_2_10_10_10_REV INT_2_10_10_10_REV}</td><td>{@link GL41#GL_FIXED FIXED}</td></tr></table>
     * @param normalized whether fixed-point data values should be normalized or converted directly as fixed-point values when they are accessed
     * @param stride     the byte offset between consecutive generic vertex attributes. If stride is 0, the generic vertex attributes are understood to be tightly packed in
     *                   the array. The initial value is 0.
     * @param pointer    the vertex attribute data or the offset of the first component of the first generic vertex attribute in the array in the data store of the buffer
     *                   currently bound to the {@link GL15#GL_ARRAY_BUFFER ARRAY_BUFFER} target. The initial value is 0.
     */
    public static void glVertexAttribPointerARB(@NativeType("GLuint") int index, @NativeType("GLint") int size, @NativeType("GLenum") int type, @NativeType("GLboolean") boolean normalized, @NativeType("GLsizei") int stride, @NativeType("void const *") FloatBuffer pointer) {
        nglVertexAttribPointerARB(index, size, type, normalized, stride, memAddress(pointer));
    }

    // --- [ glEnableVertexAttribArrayARB ] ---

    /**
     * Enables a generic vertex attribute array.
     *
     * @param index the index of the generic vertex attribute to be enabled
     */
    public static native void glEnableVertexAttribArrayARB(@NativeType("GLuint") int index);

    // --- [ glDisableVertexAttribArrayARB ] ---

    /**
     * Disables a generic vertex attribute array.
     *
     * @param index the index of the generic vertex attribute to be disabled
     */
    public static native void glDisableVertexAttribArrayARB(@NativeType("GLuint") int index);

    // --- [ glBindAttribLocationARB ] ---

    /** Unsafe version of: {@link #glBindAttribLocationARB BindAttribLocationARB} */
    public static native void nglBindAttribLocationARB(int programObj, int index, long name);

    /**
     * Associates a generic vertex attribute index with a named attribute variable.
     *
     * @param programObj the handle of the program object in which the association is to be made
     * @param index      the index of the generic vertex attribute to be bound
     * @param name       a null terminated string containing the name of the vertex shader attribute variable to which {@code index} is to be bound
     */
    public static void glBindAttribLocationARB(@NativeType("GLhandleARB") int programObj, @NativeType("GLuint") int index, @NativeType("GLchar const *") ByteBuffer name) {
        if (CHECKS) {
            checkNT1(name);
        }
        nglBindAttribLocationARB(programObj, index, memAddress(name));
    }

    /**
     * Associates a generic vertex attribute index with a named attribute variable.
     *
     * @param programObj the handle of the program object in which the association is to be made
     * @param index      the index of the generic vertex attribute to be bound
     * @param name       a null terminated string containing the name of the vertex shader attribute variable to which {@code index} is to be bound
     */
    public static void glBindAttribLocationARB(@NativeType("GLhandleARB") int programObj, @NativeType("GLuint") int index, @NativeType("GLchar const *") CharSequence name) {
        MemoryStack stack = stackGet(); int stackPointer = stack.getPointer();
        try {
            stack.nASCII(name, true);
            long nameEncoded = stack.getPointerAddress();
            nglBindAttribLocationARB(programObj, index, nameEncoded);
        } finally {
            stack.setPointer(stackPointer);
        }
    }

    // --- [ glGetActiveAttribARB ] ---

    /**
     * Unsafe version of: {@link #glGetActiveAttribARB GetActiveAttribARB}
     *
     * @param maxLength the maximum number of characters OpenGL is allowed to write in the character buffer indicated by {@code name}
     */
    public static native void nglGetActiveAttribARB(int programObj, int index, int maxLength, long length, long size, long type, long name);

    /**
     * Returns information about an active attribute variable for the specified program object.
     *
     * @param programObj the program object to be queried
     * @param index      the index of the attribute variable to be queried
     * @param length     the number of characters actually written by OpenGL in the string indicated by {@code name} (excluding the null terminator) if a value other than
     *                   {@code NULL} is passed
     * @param size       the size of the attribute variable
     * @param type       the data type of the attribute variable
     * @param name       a null terminated string containing the name of the attribute variable
     */
    public static void glGetActiveAttribARB(@NativeType("GLhandleARB") int programObj, @NativeType("GLuint") int index, @Nullable @NativeType("GLsizei *") IntBuffer length, @NativeType("GLint *") IntBuffer size, @NativeType("GLenum *") IntBuffer type, @NativeType("GLchar *") ByteBuffer name) {
        if (CHECKS) {
            checkSafe(length, 1);
            check(size, 1);
            check(type, 1);
        }
        nglGetActiveAttribARB(programObj, index, name.remaining(), memAddressSafe(length), memAddress(size), memAddress(type), memAddress(name));
    }

    /**
     * Returns information about an active attribute variable for the specified program object.
     *
     * @param programObj the program object to be queried
     * @param index      the index of the attribute variable to be queried
     * @param maxLength  the maximum number of characters OpenGL is allowed to write in the character buffer indicated by {@code name}
     * @param size       the size of the attribute variable
     * @param type       the data type of the attribute variable
     */
    @NativeType("void")
    public static String glGetActiveAttribARB(@NativeType("GLhandleARB") int programObj, @NativeType("GLuint") int index, @NativeType("GLsizei") int maxLength, @NativeType("GLint *") IntBuffer size, @NativeType("GLenum *") IntBuffer type) {
        if (CHECKS) {
            check(size, 1);
            check(type, 1);
        }
        MemoryStack stack = stackGet(); int stackPointer = stack.getPointer();
        try {
            IntBuffer length = stack.ints(0);
            ByteBuffer name = stack.malloc(maxLength);
            nglGetActiveAttribARB(programObj, index, maxLength, memAddress(length), memAddress(size), memAddress(type), memAddress(name));
            return memASCII(name, length.get(0));
        } finally {
            stack.setPointer(stackPointer);
        }
    }

    /**
     * Returns information about an active attribute variable for the specified program object.
     *
     * @param programObj the program object to be queried
     * @param index      the index of the attribute variable to be queried
     * @param size       the size of the attribute variable
     * @param type       the data type of the attribute variable
     */
    @NativeType("void")
    public static String glGetActiveAttribARB(@NativeType("GLhandleARB") int programObj, @NativeType("GLuint") int index, @NativeType("GLint *") IntBuffer size, @NativeType("GLenum *") IntBuffer type) {
        return glGetActiveAttribARB(programObj, index, ARBShaderObjects.glGetObjectParameteriARB(programObj, GL_OBJECT_ACTIVE_ATTRIBUTE_MAX_LENGTH_ARB), size, type);
    }

    // --- [ glGetAttribLocationARB ] ---

    /** Unsafe version of: {@link #glGetAttribLocationARB GetAttribLocationARB} */
    public static native int nglGetAttribLocationARB(int programObj, long name);

    /**
     * Returns the location of an attribute variable.
     *
     * @param programObj the program object to be queried
     * @param name       a null terminated string containing the name of the attribute variable whose location is to be queried
     */
    @NativeType("GLint")
    public static int glGetAttribLocationARB(@NativeType("GLhandleARB") int programObj, @NativeType("GLchar const *") ByteBuffer name) {
        if (CHECKS) {
            checkNT1(name);
        }
        return nglGetAttribLocationARB(programObj, memAddress(name));
    }

    /**
     * Returns the location of an attribute variable.
     *
     * @param programObj the program object to be queried
     * @param name       a null terminated string containing the name of the attribute variable whose location is to be queried
     */
    @NativeType("GLint")
    public static int glGetAttribLocationARB(@NativeType("GLhandleARB") int programObj, @NativeType("GLchar const *") CharSequence name) {
        MemoryStack stack = stackGet(); int stackPointer = stack.getPointer();
        try {
            stack.nASCII(name, true);
            long nameEncoded = stack.getPointerAddress();
            return nglGetAttribLocationARB(programObj, nameEncoded);
        } finally {
            stack.setPointer(stackPointer);
        }
    }

    // --- [ glGetVertexAttribivARB ] ---

    /** Unsafe version of: {@link #glGetVertexAttribivARB GetVertexAttribivARB} */
    public static native void nglGetVertexAttribivARB(int index, int pname, long params);

    /**
     * Returns the integer value of a generic vertex attribute parameter.
     *
     * @param index  the generic vertex attribute parameter to be queried
     * @param pname  the symbolic name of the vertex attribute parameter to be queried. One of:<br><table><tr><td>{@link GL15#GL_VERTEX_ATTRIB_ARRAY_BUFFER_BINDING VERTEX_ATTRIB_ARRAY_BUFFER_BINDING}</td><td>{@link #GL_VERTEX_ATTRIB_ARRAY_ENABLED_ARB VERTEX_ATTRIB_ARRAY_ENABLED_ARB}</td></tr><tr><td>{@link #GL_VERTEX_ATTRIB_ARRAY_SIZE_ARB VERTEX_ATTRIB_ARRAY_SIZE_ARB}</td><td>{@link #GL_VERTEX_ATTRIB_ARRAY_STRIDE_ARB VERTEX_ATTRIB_ARRAY_STRIDE_ARB}</td></tr><tr><td>{@link #GL_VERTEX_ATTRIB_ARRAY_TYPE_ARB VERTEX_ATTRIB_ARRAY_TYPE_ARB}</td><td>{@link #GL_VERTEX_ATTRIB_ARRAY_NORMALIZED_ARB VERTEX_ATTRIB_ARRAY_NORMALIZED_ARB}</td></tr><tr><td>{@link #GL_CURRENT_VERTEX_ATTRIB_ARB CURRENT_VERTEX_ATTRIB_ARB}</td><td>{@link GL30#GL_VERTEX_ATTRIB_ARRAY_INTEGER VERTEX_ATTRIB_ARRAY_INTEGER}</td></tr><tr><td>{@link GL33#GL_VERTEX_ATTRIB_ARRAY_DIVISOR VERTEX_ATTRIB_ARRAY_DIVISOR}</td></tr></table>
     * @param params returns the requested data
     */
    public static void glGetVertexAttribivARB(@NativeType("GLuint") int index, @NativeType("GLenum") int pname, @NativeType("GLint *") IntBuffer params) {
        if (CHECKS) {
            check(params, 1);
        }
        nglGetVertexAttribivARB(index, pname, memAddress(params));
    }

    /**
     * Returns the integer value of a generic vertex attribute parameter.
     *
     * @param index the generic vertex attribute parameter to be queried
     * @param pname the symbolic name of the vertex attribute parameter to be queried. One of:<br><table><tr><td>{@link GL15#GL_VERTEX_ATTRIB_ARRAY_BUFFER_BINDING VERTEX_ATTRIB_ARRAY_BUFFER_BINDING}</td><td>{@link #GL_VERTEX_ATTRIB_ARRAY_ENABLED_ARB VERTEX_ATTRIB_ARRAY_ENABLED_ARB}</td></tr><tr><td>{@link #GL_VERTEX_ATTRIB_ARRAY_SIZE_ARB VERTEX_ATTRIB_ARRAY_SIZE_ARB}</td><td>{@link #GL_VERTEX_ATTRIB_ARRAY_STRIDE_ARB VERTEX_ATTRIB_ARRAY_STRIDE_ARB}</td></tr><tr><td>{@link #GL_VERTEX_ATTRIB_ARRAY_TYPE_ARB VERTEX_ATTRIB_ARRAY_TYPE_ARB}</td><td>{@link #GL_VERTEX_ATTRIB_ARRAY_NORMALIZED_ARB VERTEX_ATTRIB_ARRAY_NORMALIZED_ARB}</td></tr><tr><td>{@link #GL_CURRENT_VERTEX_ATTRIB_ARB CURRENT_VERTEX_ATTRIB_ARB}</td><td>{@link GL30#GL_VERTEX_ATTRIB_ARRAY_INTEGER VERTEX_ATTRIB_ARRAY_INTEGER}</td></tr><tr><td>{@link GL33#GL_VERTEX_ATTRIB_ARRAY_DIVISOR VERTEX_ATTRIB_ARRAY_DIVISOR}</td></tr></table>
     */
    @NativeType("void")
    public static int glGetVertexAttribiARB(@NativeType("GLuint") int index, @NativeType("GLenum") int pname) {
        MemoryStack stack = stackGet(); int stackPointer = stack.getPointer();
        try {
            IntBuffer params = stack.callocInt(1);
            nglGetVertexAttribivARB(index, pname, memAddress(params));
            return params.get(0);
        } finally {
            stack.setPointer(stackPointer);
        }
    }

    // --- [ glGetVertexAttribfvARB ] ---

    /** Unsafe version of: {@link #glGetVertexAttribfvARB GetVertexAttribfvARB} */
    public static native void nglGetVertexAttribfvARB(int index, int pname, long params);

    /**
     * Float version of {@link #glGetVertexAttribivARB GetVertexAttribivARB}.
     *
     * @param index  the generic vertex attribute parameter to be queried
     * @param pname  the symbolic name of the vertex attribute parameter to be queried
     * @param params returns the requested data
     */
    public static void glGetVertexAttribfvARB(@NativeType("GLuint") int index, @NativeType("GLenum") int pname, @NativeType("GLfloat *") FloatBuffer params) {
        if (CHECKS) {
            check(params, 4);
        }
        nglGetVertexAttribfvARB(index, pname, memAddress(params));
    }

    // --- [ glGetVertexAttribdvARB ] ---

    /** Unsafe version of: {@link #glGetVertexAttribdvARB GetVertexAttribdvARB} */
    public static native void nglGetVertexAttribdvARB(int index, int pname, long params);

    /**
     * Double version of {@link #glGetVertexAttribivARB GetVertexAttribivARB}.
     *
     * @param index  the generic vertex attribute parameter to be queried
     * @param pname  the symbolic name of the vertex attribute parameter to be queried
     * @param params returns the requested data
     */
    public static void glGetVertexAttribdvARB(@NativeType("GLuint") int index, @NativeType("GLenum") int pname, @NativeType("GLdouble *") DoubleBuffer params) {
        if (CHECKS) {
            check(params, 4);
        }
        nglGetVertexAttribdvARB(index, pname, memAddress(params));
    }

    // --- [ glGetVertexAttribPointervARB ] ---

    /** Unsafe version of: {@link #glGetVertexAttribPointervARB GetVertexAttribPointervARB} */
    public static native void nglGetVertexAttribPointervARB(int index, int pname, long pointer);

    /**
     * Returns the address of the specified generic vertex attribute pointer.
     *
     * @param index   the generic vertex attribute parameter to be queried
     * @param pname   the symbolic name of the generic vertex attribute parameter to be returned. Must be:<br><table><tr><td>{@link #GL_VERTEX_ATTRIB_ARRAY_POINTER_ARB VERTEX_ATTRIB_ARRAY_POINTER_ARB}</td></tr></table>
     * @param pointer the pointer value
     */
    public static void glGetVertexAttribPointervARB(@NativeType("GLuint") int index, @NativeType("GLenum") int pname, @NativeType("void **") PointerBuffer pointer) {
        if (CHECKS) {
            check(pointer, 1);
        }
        nglGetVertexAttribPointervARB(index, pname, memAddress(pointer));
    }

    /**
     * Returns the address of the specified generic vertex attribute pointer.
     *
     * @param index the generic vertex attribute parameter to be queried
     * @param pname the symbolic name of the generic vertex attribute parameter to be returned. Must be:<br><table><tr><td>{@link #GL_VERTEX_ATTRIB_ARRAY_POINTER_ARB VERTEX_ATTRIB_ARRAY_POINTER_ARB}</td></tr></table>
     */
    @NativeType("void")
    public static long glGetVertexAttribPointerARB(@NativeType("GLuint") int index, @NativeType("GLenum") int pname) {
        MemoryStack stack = stackGet(); int stackPointer = stack.getPointer();
        try {
            PointerBuffer pointer = stack.callocPointer(1);
            nglGetVertexAttribPointervARB(index, pname, memAddress(pointer));
            return pointer.get(0);
        } finally {
            stack.setPointer(stackPointer);
        }
    }

    /** Array version of: {@link #glVertexAttrib1fvARB VertexAttrib1fvARB} */
    public static void glVertexAttrib1fvARB(@NativeType("GLuint") int index, @NativeType("GLfloat const *") float[] v) {
        long __functionAddress = GL.getICD().glVertexAttrib1fvARB;
        if (CHECKS) {
            check(__functionAddress);
            check(v, 1);
        }
        callPV(index, v, __functionAddress);
    }

    /** Array version of: {@link #glVertexAttrib1svARB VertexAttrib1svARB} */
    public static void glVertexAttrib1svARB(@NativeType("GLuint") int index, @NativeType("GLshort const *") short[] v) {
        long __functionAddress = GL.getICD().glVertexAttrib1svARB;
        if (CHECKS) {
            check(__functionAddress);
            check(v, 1);
        }
        callPV(index, v, __functionAddress);
    }

    /** Array version of: {@link #glVertexAttrib1dvARB VertexAttrib1dvARB} */
    public static void glVertexAttrib1dvARB(@NativeType("GLuint") int index, @NativeType("GLdouble const *") double[] v) {
        long __functionAddress = GL.getICD().glVertexAttrib1dvARB;
        if (CHECKS) {
            check(__functionAddress);
            check(v, 1);
        }
        callPV(index, v, __functionAddress);
    }

    /** Array version of: {@link #glVertexAttrib2fvARB VertexAttrib2fvARB} */
    public static void glVertexAttrib2fvARB(@NativeType("GLuint") int index, @NativeType("GLfloat const *") float[] v) {
        long __functionAddress = GL.getICD().glVertexAttrib2fvARB;
        if (CHECKS) {
            check(__functionAddress);
            check(v, 2);
        }
        callPV(index, v, __functionAddress);
    }

    /** Array version of: {@link #glVertexAttrib2svARB VertexAttrib2svARB} */
    public static void glVertexAttrib2svARB(@NativeType("GLuint") int index, @NativeType("GLshort const *") short[] v) {
        long __functionAddress = GL.getICD().glVertexAttrib2svARB;
        if (CHECKS) {
            check(__functionAddress);
            check(v, 2);
        }
        callPV(index, v, __functionAddress);
    }

    /** Array version of: {@link #glVertexAttrib2dvARB VertexAttrib2dvARB} */
    public static void glVertexAttrib2dvARB(@NativeType("GLuint") int index, @NativeType("GLdouble const *") double[] v) {
        long __functionAddress = GL.getICD().glVertexAttrib2dvARB;
        if (CHECKS) {
            check(__functionAddress);
            check(v, 2);
        }
        callPV(index, v, __functionAddress);
    }

    /** Array version of: {@link #glVertexAttrib3fvARB VertexAttrib3fvARB} */
    public static void glVertexAttrib3fvARB(@NativeType("GLuint") int index, @NativeType("GLfloat const *") float[] v) {
        long __functionAddress = GL.getICD().glVertexAttrib3fvARB;
        if (CHECKS) {
            check(__functionAddress);
            check(v, 3);
        }
        callPV(index, v, __functionAddress);
    }

    /** Array version of: {@link #glVertexAttrib3svARB VertexAttrib3svARB} */
    public static void glVertexAttrib3svARB(@NativeType("GLuint") int index, @NativeType("GLshort const *") short[] v) {
        long __functionAddress = GL.getICD().glVertexAttrib3svARB;
        if (CHECKS) {
            check(__functionAddress);
            check(v, 3);
        }
        callPV(index, v, __functionAddress);
    }

    /** Array version of: {@link #glVertexAttrib3dvARB VertexAttrib3dvARB} */
    public static void glVertexAttrib3dvARB(@NativeType("GLuint") int index, @NativeType("GLdouble const *") double[] v) {
        long __functionAddress = GL.getICD().glVertexAttrib3dvARB;
        if (CHECKS) {
            check(__functionAddress);
            check(v, 3);
        }
        callPV(index, v, __functionAddress);
    }

    /** Array version of: {@link #glVertexAttrib4fvARB VertexAttrib4fvARB} */
    public static void glVertexAttrib4fvARB(@NativeType("GLuint") int index, @NativeType("GLfloat const *") float[] v) {
        long __functionAddress = GL.getICD().glVertexAttrib4fvARB;
        if (CHECKS) {
            check(__functionAddress);
            check(v, 4);
        }
        callPV(index, v, __functionAddress);
    }

    /** Array version of: {@link #glVertexAttrib4svARB VertexAttrib4svARB} */
    public static void glVertexAttrib4svARB(@NativeType("GLuint") int index, @NativeType("GLshort const *") short[] v) {
        long __functionAddress = GL.getICD().glVertexAttrib4svARB;
        if (CHECKS) {
            check(__functionAddress);
            check(v, 4);
        }
        callPV(index, v, __functionAddress);
    }

    /** Array version of: {@link #glVertexAttrib4dvARB VertexAttrib4dvARB} */
    public static void glVertexAttrib4dvARB(@NativeType("GLuint") int index, @NativeType("GLdouble const *") double[] v) {
        long __functionAddress = GL.getICD().glVertexAttrib4dvARB;
        if (CHECKS) {
            check(__functionAddress);
            check(v, 4);
        }
        callPV(index, v, __functionAddress);
    }

    /** Array version of: {@link #glVertexAttrib4ivARB VertexAttrib4ivARB} */
    public static void glVertexAttrib4ivARB(@NativeType("GLuint") int index, @NativeType("GLint const *") int[] v) {
        long __functionAddress = GL.getICD().glVertexAttrib4ivARB;
        if (CHECKS) {
            check(__functionAddress);
            check(v, 4);
        }
        callPV(index, v, __functionAddress);
    }

    /** Array version of: {@link #glVertexAttrib4usvARB VertexAttrib4usvARB} */
    public static void glVertexAttrib4usvARB(@NativeType("GLuint") int index, @NativeType("GLushort const *") short[] v) {
        long __functionAddress = GL.getICD().glVertexAttrib4usvARB;
        if (CHECKS) {
            check(__functionAddress);
            check(v, 4);
        }
        callPV(index, v, __functionAddress);
    }

    /** Array version of: {@link #glVertexAttrib4uivARB VertexAttrib4uivARB} */
    public static void glVertexAttrib4uivARB(@NativeType("GLuint") int index, @NativeType("GLuint const *") int[] v) {
        long __functionAddress = GL.getICD().glVertexAttrib4uivARB;
        if (CHECKS) {
            check(__functionAddress);
            check(v, 4);
        }
        callPV(index, v, __functionAddress);
    }

    /** Array version of: {@link #glVertexAttrib4NsvARB VertexAttrib4NsvARB} */
    public static void glVertexAttrib4NsvARB(@NativeType("GLuint") int index, @NativeType("GLshort const *") short[] v) {
        long __functionAddress = GL.getICD().glVertexAttrib4NsvARB;
        if (CHECKS) {
            check(__functionAddress);
            check(v, 4);
        }
        callPV(index, v, __functionAddress);
    }

    /** Array version of: {@link #glVertexAttrib4NivARB VertexAttrib4NivARB} */
    public static void glVertexAttrib4NivARB(@NativeType("GLuint") int index, @NativeType("GLint const *") int[] v) {
        long __functionAddress = GL.getICD().glVertexAttrib4NivARB;
        if (CHECKS) {
            check(__functionAddress);
            check(v, 4);
        }
        callPV(index, v, __functionAddress);
    }

    /** Array version of: {@link #glVertexAttrib4NusvARB VertexAttrib4NusvARB} */
    public static void glVertexAttrib4NusvARB(@NativeType("GLuint") int index, @NativeType("GLushort const *") short[] v) {
        long __functionAddress = GL.getICD().glVertexAttrib4NusvARB;
        if (CHECKS) {
            check(__functionAddress);
            check(v, 4);
        }
        callPV(index, v, __functionAddress);
    }

    /** Array version of: {@link #glVertexAttrib4NuivARB VertexAttrib4NuivARB} */
    public static void glVertexAttrib4NuivARB(@NativeType("GLuint") int index, @NativeType("GLuint const *") int[] v) {
        long __functionAddress = GL.getICD().glVertexAttrib4NuivARB;
        if (CHECKS) {
            check(__functionAddress);
            check(v, 4);
        }
        callPV(index, v, __functionAddress);
    }

    /** Array version of: {@link #glVertexAttribPointerARB VertexAttribPointerARB} */
    public static void glVertexAttribPointerARB(@NativeType("GLuint") int index, @NativeType("GLint") int size, @NativeType("GLenum") int type, @NativeType("GLboolean") boolean normalized, @NativeType("GLsizei") int stride, @NativeType("void const *") short[] pointer) {
        long __functionAddress = GL.getICD().glVertexAttribPointerARB;
        if (CHECKS) {
            check(__functionAddress);
        }
        callPV(index, size, type, normalized, stride, pointer, __functionAddress);
    }

    /** Array version of: {@link #glVertexAttribPointerARB VertexAttribPointerARB} */
    public static void glVertexAttribPointerARB(@NativeType("GLuint") int index, @NativeType("GLint") int size, @NativeType("GLenum") int type, @NativeType("GLboolean") boolean normalized, @NativeType("GLsizei") int stride, @NativeType("void const *") int[] pointer) {
        long __functionAddress = GL.getICD().glVertexAttribPointerARB;
        if (CHECKS) {
            check(__functionAddress);
        }
        callPV(index, size, type, normalized, stride, pointer, __functionAddress);
    }

    /** Array version of: {@link #glVertexAttribPointerARB VertexAttribPointerARB} */
    public static void glVertexAttribPointerARB(@NativeType("GLuint") int index, @NativeType("GLint") int size, @NativeType("GLenum") int type, @NativeType("GLboolean") boolean normalized, @NativeType("GLsizei") int stride, @NativeType("void const *") float[] pointer) {
        long __functionAddress = GL.getICD().glVertexAttribPointerARB;
        if (CHECKS) {
            check(__functionAddress);
        }
        callPV(index, size, type, normalized, stride, pointer, __functionAddress);
    }

    /** Array version of: {@link #glGetActiveAttribARB GetActiveAttribARB} */
    public static void glGetActiveAttribARB(@NativeType("GLhandleARB") int programObj, @NativeType("GLuint") int index, @Nullable @NativeType("GLsizei *") int[] length, @NativeType("GLint *") int[] size, @NativeType("GLenum *") int[] type, @NativeType("GLchar *") ByteBuffer name) {
        long __functionAddress = GL.getICD().glGetActiveAttribARB;
        if (CHECKS) {
            check(__functionAddress);
            checkSafe(length, 1);
            check(size, 1);
            check(type, 1);
        }
        callPPPPV(programObj, index, name.remaining(), length, size, type, memAddress(name), __functionAddress);
    }

    /** Array version of: {@link #glGetVertexAttribivARB GetVertexAttribivARB} */
    public static void glGetVertexAttribivARB(@NativeType("GLuint") int index, @NativeType("GLenum") int pname, @NativeType("GLint *") int[] params) {
        long __functionAddress = GL.getICD().glGetVertexAttribivARB;
        if (CHECKS) {
            check(__functionAddress);
            check(params, 1);
        }
        callPV(index, pname, params, __functionAddress);
    }

    /** Array version of: {@link #glGetVertexAttribfvARB GetVertexAttribfvARB} */
    public static void glGetVertexAttribfvARB(@NativeType("GLuint") int index, @NativeType("GLenum") int pname, @NativeType("GLfloat *") float[] params) {
        long __functionAddress = GL.getICD().glGetVertexAttribfvARB;
        if (CHECKS) {
            check(__functionAddress);
            check(params, 4);
        }
        callPV(index, pname, params, __functionAddress);
    }

    /** Array version of: {@link #glGetVertexAttribdvARB GetVertexAttribdvARB} */
    public static void glGetVertexAttribdvARB(@NativeType("GLuint") int index, @NativeType("GLenum") int pname, @NativeType("GLdouble *") double[] params) {
        long __functionAddress = GL.getICD().glGetVertexAttribdvARB;
        if (CHECKS) {
            check(__functionAddress);
            check(params, 4);
        }
        callPV(index, pname, params, __functionAddress);
    }

}
