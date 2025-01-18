/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 * MACHINE GENERATED FILE, DO NOT EDIT
 */
package org.lwjgl.opengl;

import org.lwjgl.system.*;
import java.util.Set;
import org.lwjgl.*;
import java.util.function.IntFunction;

import static org.lwjgl.system.APIUtil.*;
import static org.lwjgl.system.Checks.*;
import static org.lwjgl.system.MemoryUtil.*;

/** Defines the capabilities of an OpenGL context. */
public final class GLCapabilities {

    static final int ADDRESS_BUFFER_SIZE = 2226;

    // GL11
    public final long
            glEnable,
            glDisable,
            glAccum,
            glAlphaFunc,
            glAreTexturesResident,
            glArrayElement,
            glBegin,
            glBindTexture,
            glBitmap,
            glBlendFunc,
            glCallList,
            glCallLists,
            glClear,
            glClearAccum,
            glClearColor,
            glClearDepth,
            glClearIndex,
            glClearStencil,
            glClipPlane,
            glColor3b,
            glColor3s,
            glColor3i,
            glColor3f,
            glColor3d,
            glColor3ub,
            glColor3us,
            glColor3ui,
            glColor3bv,
            glColor3sv,
            glColor3iv,
            glColor3fv,
            glColor3dv,
            glColor3ubv,
            glColor3usv,
            glColor3uiv,
            glColor4b,
            glColor4s,
            glColor4i,
            glColor4f,
            glColor4d,
            glColor4ub,
            glColor4us,
            glColor4ui,
            glColor4bv,
            glColor4sv,
            glColor4iv,
            glColor4fv,
            glColor4dv,
            glColor4ubv,
            glColor4usv,
            glColor4uiv,
            glColorMask,
            glColorMaterial,
            glColorPointer,
            glCopyPixels,
            glCullFace,
            glDeleteLists,
            glDepthFunc,
            glDepthMask,
            glDepthRange,
            glDisableClientState,
            glDrawArrays,
            glDrawBuffer,
            glDrawElements,
            glDrawPixels,
            glEdgeFlag,
            glEdgeFlagv,
            glEdgeFlagPointer,
            glEnableClientState,
            glEnd,
            glEvalCoord1f,
            glEvalCoord1fv,
            glEvalCoord1d,
            glEvalCoord1dv,
            glEvalCoord2f,
            glEvalCoord2fv,
            glEvalCoord2d,
            glEvalCoord2dv,
            glEvalMesh1,
            glEvalMesh2,
            glEvalPoint1,
            glEvalPoint2,
            glFeedbackBuffer,
            glFinish,
            glFlush,
            glFogi,
            glFogiv,
            glFogf,
            glFogfv,
            glFrontFace,
            glGenLists,
            glGenTextures,
            glDeleteTextures,
            glGetClipPlane,
            glGetBooleanv,
            glGetFloatv,
            glGetIntegerv,
            glGetDoublev,
            glGetError,
            glGetLightiv,
            glGetLightfv,
            glGetMapiv,
            glGetMapfv,
            glGetMapdv,
            glGetMaterialiv,
            glGetMaterialfv,
            glGetPixelMapfv,
            glGetPixelMapusv,
            glGetPixelMapuiv,
            glGetPointerv,
            glGetPolygonStipple,
            glGetString,
            glGetTexEnviv,
            glGetTexEnvfv,
            glGetTexGeniv,
            glGetTexGenfv,
            glGetTexGendv,
            glGetTexImage,
            glGetTexLevelParameteriv,
            glGetTexLevelParameterfv,
            glGetTexParameteriv,
            glGetTexParameterfv,
            glHint,
            glIndexi,
            glIndexub,
            glIndexs,
            glIndexf,
            glIndexd,
            glIndexiv,
            glIndexubv,
            glIndexsv,
            glIndexfv,
            glIndexdv,
            glIndexMask,
            glIndexPointer,
            glInitNames,
            glInterleavedArrays,
            glIsEnabled,
            glIsList,
            glIsTexture,
            glLightModeli,
            glLightModelf,
            glLightModeliv,
            glLightModelfv,
            glLighti,
            glLightf,
            glLightiv,
            glLightfv,
            glLineStipple,
            glLineWidth,
            glListBase,
            glLoadMatrixf,
            glLoadMatrixd,
            glLoadIdentity,
            glLoadName,
            glLogicOp,
            glMap1f,
            glMap1d,
            glMap2f,
            glMap2d,
            glMapGrid1f,
            glMapGrid1d,
            glMapGrid2f,
            glMapGrid2d,
            glMateriali,
            glMaterialf,
            glMaterialiv,
            glMaterialfv,
            glMatrixMode,
            glMultMatrixf,
            glMultMatrixd,
            glFrustum,
            glNewList,
            glEndList,
            glNormal3f,
            glNormal3b,
            glNormal3s,
            glNormal3i,
            glNormal3d,
            glNormal3fv,
            glNormal3bv,
            glNormal3sv,
            glNormal3iv,
            glNormal3dv,
            glNormalPointer,
            glOrtho,
            glPassThrough,
            glPixelMapfv,
            glPixelMapusv,
            glPixelMapuiv,
            glPixelStorei,
            glPixelStoref,
            glPixelTransferi,
            glPixelTransferf,
            glPixelZoom,
            glPointSize,
            glPolygonMode,
            glPolygonOffset,
            glPolygonStipple,
            glPushAttrib,
            glPushClientAttrib,
            glPopAttrib,
            glPopClientAttrib,
            glPopMatrix,
            glPopName,
            glPrioritizeTextures,
            glPushMatrix,
            glPushName,
            glRasterPos2i,
            glRasterPos2s,
            glRasterPos2f,
            glRasterPos2d,
            glRasterPos2iv,
            glRasterPos2sv,
            glRasterPos2fv,
            glRasterPos2dv,
            glRasterPos3i,
            glRasterPos3s,
            glRasterPos3f,
            glRasterPos3d,
            glRasterPos3iv,
            glRasterPos3sv,
            glRasterPos3fv,
            glRasterPos3dv,
            glRasterPos4i,
            glRasterPos4s,
            glRasterPos4f,
            glRasterPos4d,
            glRasterPos4iv,
            glRasterPos4sv,
            glRasterPos4fv,
            glRasterPos4dv,
            glReadBuffer,
            glReadPixels,
            glRecti,
            glRects,
            glRectf,
            glRectd,
            glRectiv,
            glRectsv,
            glRectfv,
            glRectdv,
            glRenderMode,
            glRotatef,
            glRotated,
            glScalef,
            glScaled,
            glScissor,
            glSelectBuffer,
            glShadeModel,
            glStencilFunc,
            glStencilMask,
            glStencilOp,
            glTexCoord1f,
            glTexCoord1s,
            glTexCoord1i,
            glTexCoord1d,
            glTexCoord1fv,
            glTexCoord1sv,
            glTexCoord1iv,
            glTexCoord1dv,
            glTexCoord2f,
            glTexCoord2s,
            glTexCoord2i,
            glTexCoord2d,
            glTexCoord2fv,
            glTexCoord2sv,
            glTexCoord2iv,
            glTexCoord2dv,
            glTexCoord3f,
            glTexCoord3s,
            glTexCoord3i,
            glTexCoord3d,
            glTexCoord3fv,
            glTexCoord3sv,
            glTexCoord3iv,
            glTexCoord3dv,
            glTexCoord4f,
            glTexCoord4s,
            glTexCoord4i,
            glTexCoord4d,
            glTexCoord4fv,
            glTexCoord4sv,
            glTexCoord4iv,
            glTexCoord4dv,
            glTexCoordPointer,
            glTexEnvi,
            glTexEnviv,
            glTexEnvf,
            glTexEnvfv,
            glTexGeni,
            glTexGeniv,
            glTexGenf,
            glTexGenfv,
            glTexGend,
            glTexGendv,
            glTexImage1D,
            glTexImage2D,
            glCopyTexImage1D,
            glCopyTexImage2D,
            glCopyTexSubImage1D,
            glCopyTexSubImage2D,
            glTexParameteri,
            glTexParameteriv,
            glTexParameterf,
            glTexParameterfv,
            glTexSubImage1D,
            glTexSubImage2D,
            glTranslatef,
            glTranslated,
            glVertex2f,
            glVertex2s,
            glVertex2i,
            glVertex2d,
            glVertex2fv,
            glVertex2sv,
            glVertex2iv,
            glVertex2dv,
            glVertex3f,
            glVertex3s,
            glVertex3i,
            glVertex3d,
            glVertex3fv,
            glVertex3sv,
            glVertex3iv,
            glVertex3dv,
            glVertex4f,
            glVertex4s,
            glVertex4i,
            glVertex4d,
            glVertex4fv,
            glVertex4sv,
            glVertex4iv,
            glVertex4dv,
            glVertexPointer,
            glViewport;

    // GL12
    public final long
            glTexImage3D,
            glTexSubImage3D,
            glCopyTexSubImage3D,
            glDrawRangeElements;

    // GL13
    public final long
            glCompressedTexImage3D,
            glCompressedTexImage2D,
            glCompressedTexImage1D,
            glCompressedTexSubImage3D,
            glCompressedTexSubImage2D,
            glCompressedTexSubImage1D,
            glGetCompressedTexImage,
            glSampleCoverage,
            glActiveTexture,
            glClientActiveTexture,
            glMultiTexCoord1f,
            glMultiTexCoord1s,
            glMultiTexCoord1i,
            glMultiTexCoord1d,
            glMultiTexCoord1fv,
            glMultiTexCoord1sv,
            glMultiTexCoord1iv,
            glMultiTexCoord1dv,
            glMultiTexCoord2f,
            glMultiTexCoord2s,
            glMultiTexCoord2i,
            glMultiTexCoord2d,
            glMultiTexCoord2fv,
            glMultiTexCoord2sv,
            glMultiTexCoord2iv,
            glMultiTexCoord2dv,
            glMultiTexCoord3f,
            glMultiTexCoord3s,
            glMultiTexCoord3i,
            glMultiTexCoord3d,
            glMultiTexCoord3fv,
            glMultiTexCoord3sv,
            glMultiTexCoord3iv,
            glMultiTexCoord3dv,
            glMultiTexCoord4f,
            glMultiTexCoord4s,
            glMultiTexCoord4i,
            glMultiTexCoord4d,
            glMultiTexCoord4fv,
            glMultiTexCoord4sv,
            glMultiTexCoord4iv,
            glMultiTexCoord4dv,
            glLoadTransposeMatrixf,
            glLoadTransposeMatrixd,
            glMultTransposeMatrixf,
            glMultTransposeMatrixd;

    // GL14
    public final long
            glBlendColor,
            glBlendEquation,
            glFogCoordf,
            glFogCoordd,
            glFogCoordfv,
            glFogCoorddv,
            glFogCoordPointer,
            glMultiDrawArrays,
            glMultiDrawElements,
            glPointParameterf,
            glPointParameteri,
            glPointParameterfv,
            glPointParameteriv,
            glSecondaryColor3b,
            glSecondaryColor3s,
            glSecondaryColor3i,
            glSecondaryColor3f,
            glSecondaryColor3d,
            glSecondaryColor3ub,
            glSecondaryColor3us,
            glSecondaryColor3ui,
            glSecondaryColor3bv,
            glSecondaryColor3sv,
            glSecondaryColor3iv,
            glSecondaryColor3fv,
            glSecondaryColor3dv,
            glSecondaryColor3ubv,
            glSecondaryColor3usv,
            glSecondaryColor3uiv,
            glSecondaryColorPointer,
            glBlendFuncSeparate,
            glWindowPos2i,
            glWindowPos2s,
            glWindowPos2f,
            glWindowPos2d,
            glWindowPos2iv,
            glWindowPos2sv,
            glWindowPos2fv,
            glWindowPos2dv,
            glWindowPos3i,
            glWindowPos3s,
            glWindowPos3f,
            glWindowPos3d,
            glWindowPos3iv,
            glWindowPos3sv,
            glWindowPos3fv,
            glWindowPos3dv;

    // GL15
    public final long
            glBindBuffer,
            glDeleteBuffers,
            glGenBuffers,
            glIsBuffer,
            glBufferData,
            glBufferSubData,
            glGetBufferSubData,
            glMapBuffer,
            glUnmapBuffer,
            glGetBufferParameteriv,
            glGetBufferPointerv,
            glGenQueries,
            glDeleteQueries,
            glIsQuery,
            glBeginQuery,
            glEndQuery,
            glGetQueryiv,
            glGetQueryObjectiv,
            glGetQueryObjectuiv;

    // GL20
    public final long
            glCreateProgram,
            glDeleteProgram,
            glIsProgram,
            glCreateShader,
            glDeleteShader,
            glIsShader,
            glAttachShader,
            glDetachShader,
            glShaderSource,
            glCompileShader,
            glLinkProgram,
            glUseProgram,
            glValidateProgram,
            glUniform1f,
            glUniform2f,
            glUniform3f,
            glUniform4f,
            glUniform1i,
            glUniform2i,
            glUniform3i,
            glUniform4i,
            glUniform1fv,
            glUniform2fv,
            glUniform3fv,
            glUniform4fv,
            glUniform1iv,
            glUniform2iv,
            glUniform3iv,
            glUniform4iv,
            glUniformMatrix2fv,
            glUniformMatrix3fv,
            glUniformMatrix4fv,
            glGetShaderiv,
            glGetProgramiv,
            glGetShaderInfoLog,
            glGetProgramInfoLog,
            glGetAttachedShaders,
            glGetUniformLocation,
            glGetActiveUniform,
            glGetUniformfv,
            glGetUniformiv,
            glGetShaderSource,
            glVertexAttrib1f,
            glVertexAttrib1s,
            glVertexAttrib1d,
            glVertexAttrib2f,
            glVertexAttrib2s,
            glVertexAttrib2d,
            glVertexAttrib3f,
            glVertexAttrib3s,
            glVertexAttrib3d,
            glVertexAttrib4f,
            glVertexAttrib4s,
            glVertexAttrib4d,
            glVertexAttrib4Nub,
            glVertexAttrib1fv,
            glVertexAttrib1sv,
            glVertexAttrib1dv,
            glVertexAttrib2fv,
            glVertexAttrib2sv,
            glVertexAttrib2dv,
            glVertexAttrib3fv,
            glVertexAttrib3sv,
            glVertexAttrib3dv,
            glVertexAttrib4fv,
            glVertexAttrib4sv,
            glVertexAttrib4dv,
            glVertexAttrib4iv,
            glVertexAttrib4bv,
            glVertexAttrib4ubv,
            glVertexAttrib4usv,
            glVertexAttrib4uiv,
            glVertexAttrib4Nbv,
            glVertexAttrib4Nsv,
            glVertexAttrib4Niv,
            glVertexAttrib4Nubv,
            glVertexAttrib4Nusv,
            glVertexAttrib4Nuiv,
            glVertexAttribPointer,
            glEnableVertexAttribArray,
            glDisableVertexAttribArray,
            glBindAttribLocation,
            glGetActiveAttrib,
            glGetAttribLocation,
            glGetVertexAttribiv,
            glGetVertexAttribfv,
            glGetVertexAttribdv,
            glGetVertexAttribPointerv,
            glDrawBuffers,
            glBlendEquationSeparate,
            glStencilOpSeparate,
            glStencilFuncSeparate,
            glStencilMaskSeparate;

    // GL21
    public final long
            glUniformMatrix2x3fv,
            glUniformMatrix3x2fv,
            glUniformMatrix2x4fv,
            glUniformMatrix4x2fv,
            glUniformMatrix3x4fv,
            glUniformMatrix4x3fv;

    // GL30
    public final long
            glGetStringi,
            glClearBufferiv,
            glClearBufferuiv,
            glClearBufferfv,
            glClearBufferfi,
            glVertexAttribI1i,
            glVertexAttribI2i,
            glVertexAttribI3i,
            glVertexAttribI4i,
            glVertexAttribI1ui,
            glVertexAttribI2ui,
            glVertexAttribI3ui,
            glVertexAttribI4ui,
            glVertexAttribI1iv,
            glVertexAttribI2iv,
            glVertexAttribI3iv,
            glVertexAttribI4iv,
            glVertexAttribI1uiv,
            glVertexAttribI2uiv,
            glVertexAttribI3uiv,
            glVertexAttribI4uiv,
            glVertexAttribI4bv,
            glVertexAttribI4sv,
            glVertexAttribI4ubv,
            glVertexAttribI4usv,
            glVertexAttribIPointer,
            glGetVertexAttribIiv,
            glGetVertexAttribIuiv,
            glUniform1ui,
            glUniform2ui,
            glUniform3ui,
            glUniform4ui,
            glUniform1uiv,
            glUniform2uiv,
            glUniform3uiv,
            glUniform4uiv,
            glGetUniformuiv,
            glBindFragDataLocation,
            glGetFragDataLocation,
            glBeginConditionalRender,
            glEndConditionalRender,
            glMapBufferRange,
            glFlushMappedBufferRange,
            glClampColor,
            glIsRenderbuffer,
            glBindRenderbuffer,
            glDeleteRenderbuffers,
            glGenRenderbuffers,
            glRenderbufferStorage,
            glRenderbufferStorageMultisample,
            glGetRenderbufferParameteriv,
            glIsFramebuffer,
            glBindFramebuffer,
            glDeleteFramebuffers,
            glGenFramebuffers,
            glCheckFramebufferStatus,
            glFramebufferTexture1D,
            glFramebufferTexture2D,
            glFramebufferTexture3D,
            glFramebufferTextureLayer,
            glFramebufferRenderbuffer,
            glGetFramebufferAttachmentParameteriv,
            glBlitFramebuffer,
            glGenerateMipmap,
            glTexParameterIiv,
            glTexParameterIuiv,
            glGetTexParameterIiv,
            glGetTexParameterIuiv,
            glColorMaski,
            glGetBooleani_v,
            glGetIntegeri_v,
            glEnablei,
            glDisablei,
            glIsEnabledi,
            glBindBufferRange,
            glBindBufferBase,
            glBeginTransformFeedback,
            glEndTransformFeedback,
            glTransformFeedbackVaryings,
            glGetTransformFeedbackVarying,
            glBindVertexArray,
            glDeleteVertexArrays,
            glGenVertexArrays,
            glIsVertexArray;

    // GL31
    public final long
            glDrawArraysInstanced,
            glDrawElementsInstanced,
            glCopyBufferSubData,
            glPrimitiveRestartIndex,
            glTexBuffer,
            glGetUniformIndices,
            glGetActiveUniformsiv,
            glGetActiveUniformName,
            glGetUniformBlockIndex,
            glGetActiveUniformBlockiv,
            glGetActiveUniformBlockName,
            glUniformBlockBinding;

    // GL32
    public final long
            glGetBufferParameteri64v,
            glDrawElementsBaseVertex,
            glDrawRangeElementsBaseVertex,
            glDrawElementsInstancedBaseVertex,
            glMultiDrawElementsBaseVertex,
            glProvokingVertex,
            glTexImage2DMultisample,
            glTexImage3DMultisample,
            glGetMultisamplefv,
            glSampleMaski,
            glFramebufferTexture,
            glFenceSync,
            glIsSync,
            glDeleteSync,
            glClientWaitSync,
            glWaitSync,
            glGetInteger64v,
            glGetInteger64i_v,
            glGetSynciv;

    // GL33
    public final long
            glBindFragDataLocationIndexed,
            glGetFragDataIndex,
            glGenSamplers,
            glDeleteSamplers,
            glIsSampler,
            glBindSampler,
            glSamplerParameteri,
            glSamplerParameterf,
            glSamplerParameteriv,
            glSamplerParameterfv,
            glSamplerParameterIiv,
            glSamplerParameterIuiv,
            glGetSamplerParameteriv,
            glGetSamplerParameterfv,
            glGetSamplerParameterIiv,
            glGetSamplerParameterIuiv,
            glQueryCounter,
            glGetQueryObjecti64v,
            glGetQueryObjectui64v,
            glVertexAttribDivisor,
            glVertexP2ui,
            glVertexP3ui,
            glVertexP4ui,
            glVertexP2uiv,
            glVertexP3uiv,
            glVertexP4uiv,
            glTexCoordP1ui,
            glTexCoordP2ui,
            glTexCoordP3ui,
            glTexCoordP4ui,
            glTexCoordP1uiv,
            glTexCoordP2uiv,
            glTexCoordP3uiv,
            glTexCoordP4uiv,
            glMultiTexCoordP1ui,
            glMultiTexCoordP2ui,
            glMultiTexCoordP3ui,
            glMultiTexCoordP4ui,
            glMultiTexCoordP1uiv,
            glMultiTexCoordP2uiv,
            glMultiTexCoordP3uiv,
            glMultiTexCoordP4uiv,
            glNormalP3ui,
            glNormalP3uiv,
            glColorP3ui,
            glColorP4ui,
            glColorP3uiv,
            glColorP4uiv,
            glSecondaryColorP3ui,
            glSecondaryColorP3uiv,
            glVertexAttribP1ui,
            glVertexAttribP2ui,
            glVertexAttribP3ui,
            glVertexAttribP4ui,
            glVertexAttribP1uiv,
            glVertexAttribP2uiv,
            glVertexAttribP3uiv,
            glVertexAttribP4uiv;

    // GL40
    public final long
            glBlendEquationi,
            glBlendEquationSeparatei,
            glBlendFunci,
            glBlendFuncSeparatei,
            glDrawArraysIndirect,
            glDrawElementsIndirect,
            glUniform1d,
            glUniform2d,
            glUniform3d,
            glUniform4d,
            glUniform1dv,
            glUniform2dv,
            glUniform3dv,
            glUniform4dv,
            glUniformMatrix2dv,
            glUniformMatrix3dv,
            glUniformMatrix4dv,
            glUniformMatrix2x3dv,
            glUniformMatrix2x4dv,
            glUniformMatrix3x2dv,
            glUniformMatrix3x4dv,
            glUniformMatrix4x2dv,
            glUniformMatrix4x3dv,
            glGetUniformdv,
            glMinSampleShading,
            glGetSubroutineUniformLocation,
            glGetSubroutineIndex,
            glGetActiveSubroutineUniformiv,
            glGetActiveSubroutineUniformName,
            glGetActiveSubroutineName,
            glUniformSubroutinesuiv,
            glGetUniformSubroutineuiv,
            glGetProgramStageiv,
            glPatchParameteri,
            glPatchParameterfv,
            glBindTransformFeedback,
            glDeleteTransformFeedbacks,
            glGenTransformFeedbacks,
            glIsTransformFeedback,
            glPauseTransformFeedback,
            glResumeTransformFeedback,
            glDrawTransformFeedback,
            glDrawTransformFeedbackStream,
            glBeginQueryIndexed,
            glEndQueryIndexed,
            glGetQueryIndexediv;

    // GL41
    public final long
            glReleaseShaderCompiler,
            glShaderBinary,
            glGetShaderPrecisionFormat,
            glDepthRangef,
            glClearDepthf,
            glGetProgramBinary,
            glProgramBinary,
            glProgramParameteri,
            glUseProgramStages,
            glActiveShaderProgram,
            glCreateShaderProgramv,
            glBindProgramPipeline,
            glDeleteProgramPipelines,
            glGenProgramPipelines,
            glIsProgramPipeline,
            glGetProgramPipelineiv,
            glProgramUniform1i,
            glProgramUniform2i,
            glProgramUniform3i,
            glProgramUniform4i,
            glProgramUniform1ui,
            glProgramUniform2ui,
            glProgramUniform3ui,
            glProgramUniform4ui,
            glProgramUniform1f,
            glProgramUniform2f,
            glProgramUniform3f,
            glProgramUniform4f,
            glProgramUniform1d,
            glProgramUniform2d,
            glProgramUniform3d,
            glProgramUniform4d,
            glProgramUniform1iv,
            glProgramUniform2iv,
            glProgramUniform3iv,
            glProgramUniform4iv,
            glProgramUniform1uiv,
            glProgramUniform2uiv,
            glProgramUniform3uiv,
            glProgramUniform4uiv,
            glProgramUniform1fv,
            glProgramUniform2fv,
            glProgramUniform3fv,
            glProgramUniform4fv,
            glProgramUniform1dv,
            glProgramUniform2dv,
            glProgramUniform3dv,
            glProgramUniform4dv,
            glProgramUniformMatrix2fv,
            glProgramUniformMatrix3fv,
            glProgramUniformMatrix4fv,
            glProgramUniformMatrix2dv,
            glProgramUniformMatrix3dv,
            glProgramUniformMatrix4dv,
            glProgramUniformMatrix2x3fv,
            glProgramUniformMatrix3x2fv,
            glProgramUniformMatrix2x4fv,
            glProgramUniformMatrix4x2fv,
            glProgramUniformMatrix3x4fv,
            glProgramUniformMatrix4x3fv,
            glProgramUniformMatrix2x3dv,
            glProgramUniformMatrix3x2dv,
            glProgramUniformMatrix2x4dv,
            glProgramUniformMatrix4x2dv,
            glProgramUniformMatrix3x4dv,
            glProgramUniformMatrix4x3dv,
            glValidateProgramPipeline,
            glGetProgramPipelineInfoLog,
            glVertexAttribL1d,
            glVertexAttribL2d,
            glVertexAttribL3d,
            glVertexAttribL4d,
            glVertexAttribL1dv,
            glVertexAttribL2dv,
            glVertexAttribL3dv,
            glVertexAttribL4dv,
            glVertexAttribLPointer,
            glGetVertexAttribLdv,
            glViewportArrayv,
            glViewportIndexedf,
            glViewportIndexedfv,
            glScissorArrayv,
            glScissorIndexed,
            glScissorIndexedv,
            glDepthRangeArrayv,
            glDepthRangeIndexed,
            glGetFloati_v,
            glGetDoublei_v;

    // GL42
    public final long
            glGetActiveAtomicCounterBufferiv,
            glTexStorage1D,
            glTexStorage2D,
            glTexStorage3D,
            glDrawTransformFeedbackInstanced,
            glDrawTransformFeedbackStreamInstanced,
            glDrawArraysInstancedBaseInstance,
            glDrawElementsInstancedBaseInstance,
            glDrawElementsInstancedBaseVertexBaseInstance,
            glBindImageTexture,
            glMemoryBarrier,
            glGetInternalformativ;

    // GL43
    public final long
            glClearBufferData,
            glClearBufferSubData,
            glDispatchCompute,
            glDispatchComputeIndirect,
            glCopyImageSubData,
            glDebugMessageControl,
            glDebugMessageInsert,
            glDebugMessageCallback,
            glGetDebugMessageLog,
            glPushDebugGroup,
            glPopDebugGroup,
            glObjectLabel,
            glGetObjectLabel,
            glObjectPtrLabel,
            glGetObjectPtrLabel,
            glFramebufferParameteri,
            glGetFramebufferParameteriv,
            glGetInternalformati64v,
            glInvalidateTexSubImage,
            glInvalidateTexImage,
            glInvalidateBufferSubData,
            glInvalidateBufferData,
            glInvalidateFramebuffer,
            glInvalidateSubFramebuffer,
            glMultiDrawArraysIndirect,
            glMultiDrawElementsIndirect,
            glGetProgramInterfaceiv,
            glGetProgramResourceIndex,
            glGetProgramResourceName,
            glGetProgramResourceiv,
            glGetProgramResourceLocation,
            glGetProgramResourceLocationIndex,
            glShaderStorageBlockBinding,
            glTexBufferRange,
            glTexStorage2DMultisample,
            glTexStorage3DMultisample,
            glTextureView,
            glBindVertexBuffer,
            glVertexAttribFormat,
            glVertexAttribIFormat,
            glVertexAttribLFormat,
            glVertexAttribBinding,
            glVertexBindingDivisor;

    // GL44
    public final long
            glBufferStorage,
            glClearTexSubImage,
            glClearTexImage,
            glBindBuffersBase,
            glBindBuffersRange,
            glBindTextures,
            glBindSamplers,
            glBindImageTextures,
            glBindVertexBuffers;

    // GL45
    public final long
            glClipControl,
            glCreateTransformFeedbacks,
            glTransformFeedbackBufferBase,
            glTransformFeedbackBufferRange,
            glGetTransformFeedbackiv,
            glGetTransformFeedbacki_v,
            glGetTransformFeedbacki64_v,
            glCreateBuffers,
            glNamedBufferStorage,
            glNamedBufferData,
            glNamedBufferSubData,
            glCopyNamedBufferSubData,
            glClearNamedBufferData,
            glClearNamedBufferSubData,
            glMapNamedBuffer,
            glMapNamedBufferRange,
            glUnmapNamedBuffer,
            glFlushMappedNamedBufferRange,
            glGetNamedBufferParameteriv,
            glGetNamedBufferParameteri64v,
            glGetNamedBufferPointerv,
            glGetNamedBufferSubData,
            glCreateFramebuffers,
            glNamedFramebufferRenderbuffer,
            glNamedFramebufferParameteri,
            glNamedFramebufferTexture,
            glNamedFramebufferTextureLayer,
            glNamedFramebufferDrawBuffer,
            glNamedFramebufferDrawBuffers,
            glNamedFramebufferReadBuffer,
            glInvalidateNamedFramebufferData,
            glInvalidateNamedFramebufferSubData,
            glClearNamedFramebufferiv,
            glClearNamedFramebufferuiv,
            glClearNamedFramebufferfv,
            glClearNamedFramebufferfi,
            glBlitNamedFramebuffer,
            glCheckNamedFramebufferStatus,
            glGetNamedFramebufferParameteriv,
            glGetNamedFramebufferAttachmentParameteriv,
            glCreateRenderbuffers,
            glNamedRenderbufferStorage,
            glNamedRenderbufferStorageMultisample,
            glGetNamedRenderbufferParameteriv,
            glCreateTextures,
            glTextureBuffer,
            glTextureBufferRange,
            glTextureStorage1D,
            glTextureStorage2D,
            glTextureStorage3D,
            glTextureStorage2DMultisample,
            glTextureStorage3DMultisample,
            glTextureSubImage1D,
            glTextureSubImage2D,
            glTextureSubImage3D,
            glCompressedTextureSubImage1D,
            glCompressedTextureSubImage2D,
            glCompressedTextureSubImage3D,
            glCopyTextureSubImage1D,
            glCopyTextureSubImage2D,
            glCopyTextureSubImage3D,
            glTextureParameterf,
            glTextureParameterfv,
            glTextureParameteri,
            glTextureParameterIiv,
            glTextureParameterIuiv,
            glTextureParameteriv,
            glGenerateTextureMipmap,
            glBindTextureUnit,
            glGetTextureImage,
            glGetCompressedTextureImage,
            glGetTextureLevelParameterfv,
            glGetTextureLevelParameteriv,
            glGetTextureParameterfv,
            glGetTextureParameterIiv,
            glGetTextureParameterIuiv,
            glGetTextureParameteriv,
            glCreateVertexArrays,
            glDisableVertexArrayAttrib,
            glEnableVertexArrayAttrib,
            glVertexArrayElementBuffer,
            glVertexArrayVertexBuffer,
            glVertexArrayVertexBuffers,
            glVertexArrayAttribFormat,
            glVertexArrayAttribIFormat,
            glVertexArrayAttribLFormat,
            glVertexArrayAttribBinding,
            glVertexArrayBindingDivisor,
            glGetVertexArrayiv,
            glGetVertexArrayIndexediv,
            glGetVertexArrayIndexed64iv,
            glCreateSamplers,
            glCreateProgramPipelines,
            glCreateQueries,
            glGetQueryBufferObjectiv,
            glGetQueryBufferObjectuiv,
            glGetQueryBufferObjecti64v,
            glGetQueryBufferObjectui64v,
            glMemoryBarrierByRegion,
            glGetTextureSubImage,
            glGetCompressedTextureSubImage,
            glTextureBarrier,
            glGetGraphicsResetStatus,
            glGetnMapdv,
            glGetnMapfv,
            glGetnMapiv,
            glGetnPixelMapfv,
            glGetnPixelMapuiv,
            glGetnPixelMapusv,
            glGetnPolygonStipple,
            glGetnTexImage,
            glReadnPixels,
            glGetnColorTable,
            glGetnConvolutionFilter,
            glGetnSeparableFilter,
            glGetnHistogram,
            glGetnMinmax,
            glGetnCompressedTexImage,
            glGetnUniformfv,
            glGetnUniformdv,
            glGetnUniformiv,
            glGetnUniformuiv;

    // GL46
    public final long
            glMultiDrawArraysIndirectCount,
            glMultiDrawElementsIndirectCount,
            glPolygonOffsetClamp,
            glSpecializeShader;

    // AMD_debug_output
    public final long
            glDebugMessageEnableAMD,
            glDebugMessageInsertAMD,
            glDebugMessageCallbackAMD,
            glGetDebugMessageLogAMD;

    // AMD_draw_buffers_blend
    public final long
            glBlendFuncIndexedAMD,
            glBlendFuncSeparateIndexedAMD,
            glBlendEquationIndexedAMD,
            glBlendEquationSeparateIndexedAMD;

    // AMD_framebuffer_multisample_advanced
    public final long
            glRenderbufferStorageMultisampleAdvancedAMD,
            glNamedRenderbufferStorageMultisampleAdvancedAMD;

    // AMD_gpu_shader_int64
    public final long
            glUniform1i64NV,
            glUniform2i64NV,
            glUniform3i64NV,
            glUniform4i64NV,
            glUniform1i64vNV,
            glUniform2i64vNV,
            glUniform3i64vNV,
            glUniform4i64vNV,
            glUniform1ui64NV,
            glUniform2ui64NV,
            glUniform3ui64NV,
            glUniform4ui64NV,
            glUniform1ui64vNV,
            glUniform2ui64vNV,
            glUniform3ui64vNV,
            glUniform4ui64vNV,
            glGetUniformi64vNV,
            glGetUniformui64vNV,
            glProgramUniform1i64NV,
            glProgramUniform2i64NV,
            glProgramUniform3i64NV,
            glProgramUniform4i64NV,
            glProgramUniform1i64vNV,
            glProgramUniform2i64vNV,
            glProgramUniform3i64vNV,
            glProgramUniform4i64vNV,
            glProgramUniform1ui64NV,
            glProgramUniform2ui64NV,
            glProgramUniform3ui64NV,
            glProgramUniform4ui64NV,
            glProgramUniform1ui64vNV,
            glProgramUniform2ui64vNV,
            glProgramUniform3ui64vNV,
            glProgramUniform4ui64vNV;

    // AMD_interleaved_elements
    public final long
            glVertexAttribParameteriAMD;

    // AMD_occlusion_query_event
    public final long
            glQueryObjectParameteruiAMD;

    // AMD_performance_monitor
    public final long
            glGetPerfMonitorGroupsAMD,
            glGetPerfMonitorCountersAMD,
            glGetPerfMonitorGroupStringAMD,
            glGetPerfMonitorCounterStringAMD,
            glGetPerfMonitorCounterInfoAMD,
            glGenPerfMonitorsAMD,
            glDeletePerfMonitorsAMD,
            glSelectPerfMonitorCountersAMD,
            glBeginPerfMonitorAMD,
            glEndPerfMonitorAMD,
            glGetPerfMonitorCounterDataAMD;

    // AMD_sample_positions
    public final long
            glSetMultisamplefvAMD;

    // AMD_sparse_texture
    public final long
            glTexStorageSparseAMD,
            glTextureStorageSparseAMD;

    // AMD_stencil_operation_extended
    public final long
            glStencilOpValueAMD;

    // AMD_vertex_shader_tessellator
    public final long
            glTessellationFactorAMD,
            glTessellationModeAMD;

    // ARB_bindless_texture
    public final long
            glGetTextureHandleARB,
            glGetTextureSamplerHandleARB,
            glMakeTextureHandleResidentARB,
            glMakeTextureHandleNonResidentARB,
            glGetImageHandleARB,
            glMakeImageHandleResidentARB,
            glMakeImageHandleNonResidentARB,
            glUniformHandleui64ARB,
            glUniformHandleui64vARB,
            glProgramUniformHandleui64ARB,
            glProgramUniformHandleui64vARB,
            glIsTextureHandleResidentARB,
            glIsImageHandleResidentARB,
            glVertexAttribL1ui64ARB,
            glVertexAttribL1ui64vARB,
            glGetVertexAttribLui64vARB;

    // ARB_buffer_storage
    public final long
            glNamedBufferStorageEXT;

    // ARB_cl_event
    public final long
            glCreateSyncFromCLeventARB;

    // ARB_clear_buffer_object
    public final long
            glClearNamedBufferDataEXT,
            glClearNamedBufferSubDataEXT;

    // ARB_color_buffer_float
    public final long
            glClampColorARB;

    // ARB_compute_variable_group_size
    public final long
            glDispatchComputeGroupSizeARB;

    // ARB_debug_output
    public final long
            glDebugMessageControlARB,
            glDebugMessageInsertARB,
            glDebugMessageCallbackARB,
            glGetDebugMessageLogARB;

    // ARB_draw_buffers
    public final long
            glDrawBuffersARB;

    // ARB_draw_buffers_blend
    public final long
            glBlendEquationiARB,
            glBlendEquationSeparateiARB,
            glBlendFunciARB,
            glBlendFuncSeparateiARB;

    // ARB_draw_instanced
    public final long
            glDrawArraysInstancedARB,
            glDrawElementsInstancedARB;

    // ARB_ES3_2_compatibility
    public final long
            glPrimitiveBoundingBoxARB;

    // ARB_framebuffer_no_attachments
    public final long
            glNamedFramebufferParameteriEXT,
            glGetNamedFramebufferParameterivEXT;

    // ARB_geometry_shader4
    public final long
            glProgramParameteriARB,
            glFramebufferTextureARB,
            glFramebufferTextureLayerARB,
            glFramebufferTextureFaceARB;

    // ARB_gl_spirv
    public final long
            glSpecializeShaderARB;

    // ARB_gpu_shader_fp64
    public final long
            glProgramUniform1dEXT,
            glProgramUniform2dEXT,
            glProgramUniform3dEXT,
            glProgramUniform4dEXT,
            glProgramUniform1dvEXT,
            glProgramUniform2dvEXT,
            glProgramUniform3dvEXT,
            glProgramUniform4dvEXT,
            glProgramUniformMatrix2dvEXT,
            glProgramUniformMatrix3dvEXT,
            glProgramUniformMatrix4dvEXT,
            glProgramUniformMatrix2x3dvEXT,
            glProgramUniformMatrix2x4dvEXT,
            glProgramUniformMatrix3x2dvEXT,
            glProgramUniformMatrix3x4dvEXT,
            glProgramUniformMatrix4x2dvEXT,
            glProgramUniformMatrix4x3dvEXT;

    // ARB_gpu_shader_int64
    public final long
            glUniform1i64ARB,
            glUniform1i64vARB,
            glProgramUniform1i64ARB,
            glProgramUniform1i64vARB,
            glUniform2i64ARB,
            glUniform2i64vARB,
            glProgramUniform2i64ARB,
            glProgramUniform2i64vARB,
            glUniform3i64ARB,
            glUniform3i64vARB,
            glProgramUniform3i64ARB,
            glProgramUniform3i64vARB,
            glUniform4i64ARB,
            glUniform4i64vARB,
            glProgramUniform4i64ARB,
            glProgramUniform4i64vARB,
            glUniform1ui64ARB,
            glUniform1ui64vARB,
            glProgramUniform1ui64ARB,
            glProgramUniform1ui64vARB,
            glUniform2ui64ARB,
            glUniform2ui64vARB,
            glProgramUniform2ui64ARB,
            glProgramUniform2ui64vARB,
            glUniform3ui64ARB,
            glUniform3ui64vARB,
            glProgramUniform3ui64ARB,
            glProgramUniform3ui64vARB,
            glUniform4ui64ARB,
            glUniform4ui64vARB,
            glProgramUniform4ui64ARB,
            glProgramUniform4ui64vARB,
            glGetUniformi64vARB,
            glGetUniformui64vARB,
            glGetnUniformi64vARB,
            glGetnUniformui64vARB;

    // ARB_imaging
    public final long
            glColorTable,
            glCopyColorTable,
            glColorTableParameteriv,
            glColorTableParameterfv,
            glGetColorTable,
            glGetColorTableParameteriv,
            glGetColorTableParameterfv,
            glColorSubTable,
            glCopyColorSubTable,
            glConvolutionFilter1D,
            glConvolutionFilter2D,
            glCopyConvolutionFilter1D,
            glCopyConvolutionFilter2D,
            glGetConvolutionFilter,
            glSeparableFilter2D,
            glGetSeparableFilter,
            glConvolutionParameteri,
            glConvolutionParameteriv,
            glConvolutionParameterf,
            glConvolutionParameterfv,
            glGetConvolutionParameteriv,
            glGetConvolutionParameterfv,
            glHistogram,
            glResetHistogram,
            glGetHistogram,
            glGetHistogramParameteriv,
            glGetHistogramParameterfv,
            glMinmax,
            glResetMinmax,
            glGetMinmax,
            glGetMinmaxParameteriv,
            glGetMinmaxParameterfv;

    // ARB_indirect_parameters
    public final long
            glMultiDrawArraysIndirectCountARB,
            glMultiDrawElementsIndirectCountARB;

    // ARB_instanced_arrays
    public final long
            glVertexAttribDivisorARB,
            glVertexArrayVertexAttribDivisorEXT;

    // ARB_matrix_palette
    public final long
            glCurrentPaletteMatrixARB,
            glMatrixIndexuivARB,
            glMatrixIndexubvARB,
            glMatrixIndexusvARB,
            glMatrixIndexPointerARB;

    // ARB_multisample
    public final long
            glSampleCoverageARB;

    // ARB_multitexture
    public final long
            glActiveTextureARB,
            glClientActiveTextureARB,
            glMultiTexCoord1fARB,
            glMultiTexCoord1sARB,
            glMultiTexCoord1iARB,
            glMultiTexCoord1dARB,
            glMultiTexCoord1fvARB,
            glMultiTexCoord1svARB,
            glMultiTexCoord1ivARB,
            glMultiTexCoord1dvARB,
            glMultiTexCoord2fARB,
            glMultiTexCoord2sARB,
            glMultiTexCoord2iARB,
            glMultiTexCoord2dARB,
            glMultiTexCoord2fvARB,
            glMultiTexCoord2svARB,
            glMultiTexCoord2ivARB,
            glMultiTexCoord2dvARB,
            glMultiTexCoord3fARB,
            glMultiTexCoord3sARB,
            glMultiTexCoord3iARB,
            glMultiTexCoord3dARB,
            glMultiTexCoord3fvARB,
            glMultiTexCoord3svARB,
            glMultiTexCoord3ivARB,
            glMultiTexCoord3dvARB,
            glMultiTexCoord4fARB,
            glMultiTexCoord4sARB,
            glMultiTexCoord4iARB,
            glMultiTexCoord4dARB,
            glMultiTexCoord4fvARB,
            glMultiTexCoord4svARB,
            glMultiTexCoord4ivARB,
            glMultiTexCoord4dvARB;

    // ARB_occlusion_query
    public final long
            glGenQueriesARB,
            glDeleteQueriesARB,
            glIsQueryARB,
            glBeginQueryARB,
            glEndQueryARB,
            glGetQueryivARB,
            glGetQueryObjectivARB,
            glGetQueryObjectuivARB;

    // ARB_parallel_shader_compile
    public final long
            glMaxShaderCompilerThreadsARB;

    // ARB_point_parameters
    public final long
            glPointParameterfARB,
            glPointParameterfvARB;

    // ARB_robustness
    public final long
            glGetGraphicsResetStatusARB,
            glGetnMapdvARB,
            glGetnMapfvARB,
            glGetnMapivARB,
            glGetnPixelMapfvARB,
            glGetnPixelMapuivARB,
            glGetnPixelMapusvARB,
            glGetnPolygonStippleARB,
            glGetnTexImageARB,
            glReadnPixelsARB,
            glGetnColorTableARB,
            glGetnConvolutionFilterARB,
            glGetnSeparableFilterARB,
            glGetnHistogramARB,
            glGetnMinmaxARB,
            glGetnCompressedTexImageARB,
            glGetnUniformfvARB,
            glGetnUniformivARB,
            glGetnUniformuivARB,
            glGetnUniformdvARB;

    // ARB_sample_locations
    public final long
            glFramebufferSampleLocationsfvARB,
            glNamedFramebufferSampleLocationsfvARB,
            glEvaluateDepthValuesARB;

    // ARB_sample_shading
    public final long
            glMinSampleShadingARB;

    // ARB_shader_objects
    public final long
            glDeleteObjectARB,
            glGetHandleARB,
            glDetachObjectARB,
            glCreateShaderObjectARB,
            glShaderSourceARB,
            glCompileShaderARB,
            glCreateProgramObjectARB,
            glAttachObjectARB,
            glLinkProgramARB,
            glUseProgramObjectARB,
            glValidateProgramARB,
            glUniform1fARB,
            glUniform2fARB,
            glUniform3fARB,
            glUniform4fARB,
            glUniform1iARB,
            glUniform2iARB,
            glUniform3iARB,
            glUniform4iARB,
            glUniform1fvARB,
            glUniform2fvARB,
            glUniform3fvARB,
            glUniform4fvARB,
            glUniform1ivARB,
            glUniform2ivARB,
            glUniform3ivARB,
            glUniform4ivARB,
            glUniformMatrix2fvARB,
            glUniformMatrix3fvARB,
            glUniformMatrix4fvARB,
            glGetObjectParameterfvARB,
            glGetObjectParameterivARB,
            glGetInfoLogARB,
            glGetAttachedObjectsARB,
            glGetUniformLocationARB,
            glGetActiveUniformARB,
            glGetUniformfvARB,
            glGetUniformivARB,
            glGetShaderSourceARB;

    // ARB_shading_language_include
    public final long
            glNamedStringARB,
            glDeleteNamedStringARB,
            glCompileShaderIncludeARB,
            glIsNamedStringARB,
            glGetNamedStringARB,
            glGetNamedStringivARB;

    // ARB_sparse_buffer
    public final long
            glBufferPageCommitmentARB,
            glNamedBufferPageCommitmentEXT,
            glNamedBufferPageCommitmentARB;

    // ARB_sparse_texture
    public final long
            glTexPageCommitmentARB,
            glTexturePageCommitmentEXT;

    // ARB_texture_buffer_object
    public final long
            glTexBufferARB;

    // ARB_texture_buffer_range
    public final long
            glTextureBufferRangeEXT;

    // ARB_texture_compression
    public final long
            glCompressedTexImage3DARB,
            glCompressedTexImage2DARB,
            glCompressedTexImage1DARB,
            glCompressedTexSubImage3DARB,
            glCompressedTexSubImage2DARB,
            glCompressedTexSubImage1DARB,
            glGetCompressedTexImageARB;

    // ARB_texture_storage
    public final long
            glTextureStorage1DEXT,
            glTextureStorage2DEXT,
            glTextureStorage3DEXT;

    // ARB_texture_storage_multisample
    public final long
            glTextureStorage2DMultisampleEXT,
            glTextureStorage3DMultisampleEXT;

    // ARB_transpose_matrix
    public final long
            glLoadTransposeMatrixfARB,
            glLoadTransposeMatrixdARB,
            glMultTransposeMatrixfARB,
            glMultTransposeMatrixdARB;

    // ARB_vertex_attrib_64bit
    public final long
            glVertexArrayVertexAttribLOffsetEXT;

    // ARB_vertex_attrib_binding
    public final long
            glVertexArrayBindVertexBufferEXT,
            glVertexArrayVertexAttribFormatEXT,
            glVertexArrayVertexAttribIFormatEXT,
            glVertexArrayVertexAttribLFormatEXT,
            glVertexArrayVertexAttribBindingEXT,
            glVertexArrayVertexBindingDivisorEXT;

    // ARB_vertex_blend
    public final long
            glWeightfvARB,
            glWeightbvARB,
            glWeightubvARB,
            glWeightsvARB,
            glWeightusvARB,
            glWeightivARB,
            glWeightuivARB,
            glWeightdvARB,
            glWeightPointerARB,
            glVertexBlendARB;

    // ARB_vertex_buffer_object
    public final long
            glBindBufferARB,
            glDeleteBuffersARB,
            glGenBuffersARB,
            glIsBufferARB,
            glBufferDataARB,
            glBufferSubDataARB,
            glGetBufferSubDataARB,
            glMapBufferARB,
            glUnmapBufferARB,
            glGetBufferParameterivARB,
            glGetBufferPointervARB;

    // ARB_vertex_program
    public final long
            glVertexAttrib1sARB,
            glVertexAttrib1fARB,
            glVertexAttrib1dARB,
            glVertexAttrib2sARB,
            glVertexAttrib2fARB,
            glVertexAttrib2dARB,
            glVertexAttrib3sARB,
            glVertexAttrib3fARB,
            glVertexAttrib3dARB,
            glVertexAttrib4sARB,
            glVertexAttrib4fARB,
            glVertexAttrib4dARB,
            glVertexAttrib4NubARB,
            glVertexAttrib1svARB,
            glVertexAttrib1fvARB,
            glVertexAttrib1dvARB,
            glVertexAttrib2svARB,
            glVertexAttrib2fvARB,
            glVertexAttrib2dvARB,
            glVertexAttrib3svARB,
            glVertexAttrib3fvARB,
            glVertexAttrib3dvARB,
            glVertexAttrib4fvARB,
            glVertexAttrib4bvARB,
            glVertexAttrib4svARB,
            glVertexAttrib4ivARB,
            glVertexAttrib4ubvARB,
            glVertexAttrib4usvARB,
            glVertexAttrib4uivARB,
            glVertexAttrib4dvARB,
            glVertexAttrib4NbvARB,
            glVertexAttrib4NsvARB,
            glVertexAttrib4NivARB,
            glVertexAttrib4NubvARB,
            glVertexAttrib4NusvARB,
            glVertexAttrib4NuivARB,
            glVertexAttribPointerARB,
            glEnableVertexAttribArrayARB,
            glDisableVertexAttribArrayARB,
            glProgramStringARB,
            glBindProgramARB,
            glDeleteProgramsARB,
            glGenProgramsARB,
            glProgramEnvParameter4dARB,
            glProgramEnvParameter4dvARB,
            glProgramEnvParameter4fARB,
            glProgramEnvParameter4fvARB,
            glProgramLocalParameter4dARB,
            glProgramLocalParameter4dvARB,
            glProgramLocalParameter4fARB,
            glProgramLocalParameter4fvARB,
            glGetProgramEnvParameterfvARB,
            glGetProgramEnvParameterdvARB,
            glGetProgramLocalParameterfvARB,
            glGetProgramLocalParameterdvARB,
            glGetProgramivARB,
            glGetProgramStringARB,
            glGetVertexAttribfvARB,
            glGetVertexAttribdvARB,
            glGetVertexAttribivARB,
            glGetVertexAttribPointervARB,
            glIsProgramARB;

    // ARB_vertex_shader
    public final long
            glBindAttribLocationARB,
            glGetActiveAttribARB,
            glGetAttribLocationARB;

    // ARB_window_pos
    public final long
            glWindowPos2iARB,
            glWindowPos2sARB,
            glWindowPos2fARB,
            glWindowPos2dARB,
            glWindowPos2ivARB,
            glWindowPos2svARB,
            glWindowPos2fvARB,
            glWindowPos2dvARB,
            glWindowPos3iARB,
            glWindowPos3sARB,
            glWindowPos3fARB,
            glWindowPos3dARB,
            glWindowPos3ivARB,
            glWindowPos3svARB,
            glWindowPos3fvARB,
            glWindowPos3dvARB;

    // EXT_bindable_uniform
    public final long
            glUniformBufferEXT,
            glGetUniformBufferSizeEXT,
            glGetUniformOffsetEXT;

    // EXT_blend_color
    public final long
            glBlendColorEXT;

    // EXT_blend_equation_separate
    public final long
            glBlendEquationSeparateEXT;

    // EXT_blend_func_separate
    public final long
            glBlendFuncSeparateEXT;

    // EXT_blend_minmax
    public final long
            glBlendEquationEXT;

    // EXT_compiled_vertex_array
    public final long
            glLockArraysEXT,
            glUnlockArraysEXT;

    // EXT_debug_label
    public final long
            glLabelObjectEXT,
            glGetObjectLabelEXT;

    // EXT_debug_marker
    public final long
            glInsertEventMarkerEXT,
            glPushGroupMarkerEXT,
            glPopGroupMarkerEXT;

    // EXT_depth_bounds_test
    public final long
            glDepthBoundsEXT;

    // EXT_direct_state_access
    public final long
            glClientAttribDefaultEXT,
            glPushClientAttribDefaultEXT,
            glMatrixLoadfEXT,
            glMatrixLoaddEXT,
            glMatrixMultfEXT,
            glMatrixMultdEXT,
            glMatrixLoadIdentityEXT,
            glMatrixRotatefEXT,
            glMatrixRotatedEXT,
            glMatrixScalefEXT,
            glMatrixScaledEXT,
            glMatrixTranslatefEXT,
            glMatrixTranslatedEXT,
            glMatrixOrthoEXT,
            glMatrixFrustumEXT,
            glMatrixPushEXT,
            glMatrixPopEXT,
            glTextureParameteriEXT,
            glTextureParameterivEXT,
            glTextureParameterfEXT,
            glTextureParameterfvEXT,
            glTextureImage1DEXT,
            glTextureImage2DEXT,
            glTextureSubImage1DEXT,
            glTextureSubImage2DEXT,
            glCopyTextureImage1DEXT,
            glCopyTextureImage2DEXT,
            glCopyTextureSubImage1DEXT,
            glCopyTextureSubImage2DEXT,
            glGetTextureImageEXT,
            glGetTextureParameterfvEXT,
            glGetTextureParameterivEXT,
            glGetTextureLevelParameterfvEXT,
            glGetTextureLevelParameterivEXT,
            glTextureImage3DEXT,
            glTextureSubImage3DEXT,
            glCopyTextureSubImage3DEXT,
            glBindMultiTextureEXT,
            glMultiTexCoordPointerEXT,
            glMultiTexEnvfEXT,
            glMultiTexEnvfvEXT,
            glMultiTexEnviEXT,
            glMultiTexEnvivEXT,
            glMultiTexGendEXT,
            glMultiTexGendvEXT,
            glMultiTexGenfEXT,
            glMultiTexGenfvEXT,
            glMultiTexGeniEXT,
            glMultiTexGenivEXT,
            glGetMultiTexEnvfvEXT,
            glGetMultiTexEnvivEXT,
            glGetMultiTexGendvEXT,
            glGetMultiTexGenfvEXT,
            glGetMultiTexGenivEXT,
            glMultiTexParameteriEXT,
            glMultiTexParameterivEXT,
            glMultiTexParameterfEXT,
            glMultiTexParameterfvEXT,
            glMultiTexImage1DEXT,
            glMultiTexImage2DEXT,
            glMultiTexSubImage1DEXT,
            glMultiTexSubImage2DEXT,
            glCopyMultiTexImage1DEXT,
            glCopyMultiTexImage2DEXT,
            glCopyMultiTexSubImage1DEXT,
            glCopyMultiTexSubImage2DEXT,
            glGetMultiTexImageEXT,
            glGetMultiTexParameterfvEXT,
            glGetMultiTexParameterivEXT,
            glGetMultiTexLevelParameterfvEXT,
            glGetMultiTexLevelParameterivEXT,
            glMultiTexImage3DEXT,
            glMultiTexSubImage3DEXT,
            glCopyMultiTexSubImage3DEXT,
            glEnableClientStateIndexedEXT,
            glDisableClientStateIndexedEXT,
            glEnableClientStateiEXT,
            glDisableClientStateiEXT,
            glGetFloatIndexedvEXT,
            glGetDoubleIndexedvEXT,
            glGetPointerIndexedvEXT,
            glGetFloati_vEXT,
            glGetDoublei_vEXT,
            glGetPointeri_vEXT,
            glEnableIndexedEXT,
            glDisableIndexedEXT,
            glIsEnabledIndexedEXT,
            glGetIntegerIndexedvEXT,
            glGetBooleanIndexedvEXT,
            glNamedProgramStringEXT,
            glNamedProgramLocalParameter4dEXT,
            glNamedProgramLocalParameter4dvEXT,
            glNamedProgramLocalParameter4fEXT,
            glNamedProgramLocalParameter4fvEXT,
            glGetNamedProgramLocalParameterdvEXT,
            glGetNamedProgramLocalParameterfvEXT,
            glGetNamedProgramivEXT,
            glGetNamedProgramStringEXT,
            glCompressedTextureImage3DEXT,
            glCompressedTextureImage2DEXT,
            glCompressedTextureImage1DEXT,
            glCompressedTextureSubImage3DEXT,
            glCompressedTextureSubImage2DEXT,
            glCompressedTextureSubImage1DEXT,
            glGetCompressedTextureImageEXT,
            glCompressedMultiTexImage3DEXT,
            glCompressedMultiTexImage2DEXT,
            glCompressedMultiTexImage1DEXT,
            glCompressedMultiTexSubImage3DEXT,
            glCompressedMultiTexSubImage2DEXT,
            glCompressedMultiTexSubImage1DEXT,
            glGetCompressedMultiTexImageEXT,
            glMatrixLoadTransposefEXT,
            glMatrixLoadTransposedEXT,
            glMatrixMultTransposefEXT,
            glMatrixMultTransposedEXT,
            glNamedBufferDataEXT,
            glNamedBufferSubDataEXT,
            glMapNamedBufferEXT,
            glUnmapNamedBufferEXT,
            glGetNamedBufferParameterivEXT,
            glGetNamedBufferSubDataEXT,
            glProgramUniform1fEXT,
            glProgramUniform2fEXT,
            glProgramUniform3fEXT,
            glProgramUniform4fEXT,
            glProgramUniform1iEXT,
            glProgramUniform2iEXT,
            glProgramUniform3iEXT,
            glProgramUniform4iEXT,
            glProgramUniform1fvEXT,
            glProgramUniform2fvEXT,
            glProgramUniform3fvEXT,
            glProgramUniform4fvEXT,
            glProgramUniform1ivEXT,
            glProgramUniform2ivEXT,
            glProgramUniform3ivEXT,
            glProgramUniform4ivEXT,
            glProgramUniformMatrix2fvEXT,
            glProgramUniformMatrix3fvEXT,
            glProgramUniformMatrix4fvEXT,
            glProgramUniformMatrix2x3fvEXT,
            glProgramUniformMatrix3x2fvEXT,
            glProgramUniformMatrix2x4fvEXT,
            glProgramUniformMatrix4x2fvEXT,
            glProgramUniformMatrix3x4fvEXT,
            glProgramUniformMatrix4x3fvEXT,
            glTextureBufferEXT,
            glMultiTexBufferEXT,
            glTextureParameterIivEXT,
            glTextureParameterIuivEXT,
            glGetTextureParameterIivEXT,
            glGetTextureParameterIuivEXT,
            glMultiTexParameterIivEXT,
            glMultiTexParameterIuivEXT,
            glGetMultiTexParameterIivEXT,
            glGetMultiTexParameterIuivEXT,
            glProgramUniform1uiEXT,
            glProgramUniform2uiEXT,
            glProgramUniform3uiEXT,
            glProgramUniform4uiEXT,
            glProgramUniform1uivEXT,
            glProgramUniform2uivEXT,
            glProgramUniform3uivEXT,
            glProgramUniform4uivEXT,
            glNamedProgramLocalParameters4fvEXT,
            glNamedProgramLocalParameterI4iEXT,
            glNamedProgramLocalParameterI4ivEXT,
            glNamedProgramLocalParametersI4ivEXT,
            glNamedProgramLocalParameterI4uiEXT,
            glNamedProgramLocalParameterI4uivEXT,
            glNamedProgramLocalParametersI4uivEXT,
            glGetNamedProgramLocalParameterIivEXT,
            glGetNamedProgramLocalParameterIuivEXT,
            glNamedRenderbufferStorageEXT,
            glGetNamedRenderbufferParameterivEXT,
            glNamedRenderbufferStorageMultisampleEXT,
            glNamedRenderbufferStorageMultisampleCoverageEXT,
            glCheckNamedFramebufferStatusEXT,
            glNamedFramebufferTexture1DEXT,
            glNamedFramebufferTexture2DEXT,
            glNamedFramebufferTexture3DEXT,
            glNamedFramebufferRenderbufferEXT,
            glGetNamedFramebufferAttachmentParameterivEXT,
            glGenerateTextureMipmapEXT,
            glGenerateMultiTexMipmapEXT,
            glFramebufferDrawBufferEXT,
            glFramebufferDrawBuffersEXT,
            glFramebufferReadBufferEXT,
            glGetFramebufferParameterivEXT,
            glNamedCopyBufferSubDataEXT,
            glNamedFramebufferTextureEXT,
            glNamedFramebufferTextureLayerEXT,
            glNamedFramebufferTextureFaceEXT,
            glTextureRenderbufferEXT,
            glMultiTexRenderbufferEXT,
            glVertexArrayVertexOffsetEXT,
            glVertexArrayColorOffsetEXT,
            glVertexArrayEdgeFlagOffsetEXT,
            glVertexArrayIndexOffsetEXT,
            glVertexArrayNormalOffsetEXT,
            glVertexArrayTexCoordOffsetEXT,
            glVertexArrayMultiTexCoordOffsetEXT,
            glVertexArrayFogCoordOffsetEXT,
            glVertexArraySecondaryColorOffsetEXT,
            glVertexArrayVertexAttribOffsetEXT,
            glVertexArrayVertexAttribIOffsetEXT,
            glEnableVertexArrayEXT,
            glDisableVertexArrayEXT,
            glEnableVertexArrayAttribEXT,
            glDisableVertexArrayAttribEXT,
            glGetVertexArrayIntegervEXT,
            glGetVertexArrayPointervEXT,
            glGetVertexArrayIntegeri_vEXT,
            glGetVertexArrayPointeri_vEXT,
            glMapNamedBufferRangeEXT,
            glFlushMappedNamedBufferRangeEXT;

    // EXT_draw_buffers2
    public final long
            glColorMaskIndexedEXT;

    // EXT_draw_instanced
    public final long
            glDrawArraysInstancedEXT,
            glDrawElementsInstancedEXT;

    // EXT_EGL_image_storage
    public final long
            glEGLImageTargetTexStorageEXT,
            glEGLImageTargetTextureStorageEXT;

    // EXT_external_buffer
    public final long
            glBufferStorageExternalEXT,
            glNamedBufferStorageExternalEXT;

    // EXT_framebuffer_blit
    public final long
            glBlitFramebufferEXT;

    // EXT_framebuffer_multisample
    public final long
            glRenderbufferStorageMultisampleEXT;

    // EXT_framebuffer_object
    public final long
            glIsRenderbufferEXT,
            glBindRenderbufferEXT,
            glDeleteRenderbuffersEXT,
            glGenRenderbuffersEXT,
            glRenderbufferStorageEXT,
            glGetRenderbufferParameterivEXT,
            glIsFramebufferEXT,
            glBindFramebufferEXT,
            glDeleteFramebuffersEXT,
            glGenFramebuffersEXT,
            glCheckFramebufferStatusEXT,
            glFramebufferTexture1DEXT,
            glFramebufferTexture2DEXT,
            glFramebufferTexture3DEXT,
            glFramebufferRenderbufferEXT,
            glGetFramebufferAttachmentParameterivEXT,
            glGenerateMipmapEXT;

    // EXT_geometry_shader4
    public final long
            glProgramParameteriEXT,
            glFramebufferTextureEXT,
            glFramebufferTextureLayerEXT,
            glFramebufferTextureFaceEXT;

    // EXT_gpu_program_parameters
    public final long
            glProgramEnvParameters4fvEXT,
            glProgramLocalParameters4fvEXT;

    // EXT_gpu_shader4
    public final long
            glVertexAttribI1iEXT,
            glVertexAttribI2iEXT,
            glVertexAttribI3iEXT,
            glVertexAttribI4iEXT,
            glVertexAttribI1uiEXT,
            glVertexAttribI2uiEXT,
            glVertexAttribI3uiEXT,
            glVertexAttribI4uiEXT,
            glVertexAttribI1ivEXT,
            glVertexAttribI2ivEXT,
            glVertexAttribI3ivEXT,
            glVertexAttribI4ivEXT,
            glVertexAttribI1uivEXT,
            glVertexAttribI2uivEXT,
            glVertexAttribI3uivEXT,
            glVertexAttribI4uivEXT,
            glVertexAttribI4bvEXT,
            glVertexAttribI4svEXT,
            glVertexAttribI4ubvEXT,
            glVertexAttribI4usvEXT,
            glVertexAttribIPointerEXT,
            glGetVertexAttribIivEXT,
            glGetVertexAttribIuivEXT,
            glGetUniformuivEXT,
            glBindFragDataLocationEXT,
            glGetFragDataLocationEXT,
            glUniform1uiEXT,
            glUniform2uiEXT,
            glUniform3uiEXT,
            glUniform4uiEXT,
            glUniform1uivEXT,
            glUniform2uivEXT,
            glUniform3uivEXT,
            glUniform4uivEXT;

    // EXT_memory_object
    public final long
            glGetUnsignedBytevEXT,
            glGetUnsignedBytei_vEXT,
            glDeleteMemoryObjectsEXT,
            glIsMemoryObjectEXT,
            glCreateMemoryObjectsEXT,
            glMemoryObjectParameterivEXT,
            glGetMemoryObjectParameterivEXT,
            glTexStorageMem2DEXT,
            glTexStorageMem2DMultisampleEXT,
            glTexStorageMem3DEXT,
            glTexStorageMem3DMultisampleEXT,
            glBufferStorageMemEXT,
            glTextureStorageMem2DEXT,
            glTextureStorageMem2DMultisampleEXT,
            glTextureStorageMem3DEXT,
            glTextureStorageMem3DMultisampleEXT,
            glNamedBufferStorageMemEXT,
            glTexStorageMem1DEXT,
            glTextureStorageMem1DEXT;

    // EXT_memory_object_fd
    public final long
            glImportMemoryFdEXT;

    // EXT_memory_object_win32
    public final long
            glImportMemoryWin32HandleEXT,
            glImportMemoryWin32NameEXT;

    // EXT_point_parameters
    public final long
            glPointParameterfEXT,
            glPointParameterfvEXT;

    // EXT_polygon_offset_clamp
    public final long
            glPolygonOffsetClampEXT;

    // EXT_provoking_vertex
    public final long
            glProvokingVertexEXT;

    // EXT_raster_multisample
    public final long
            glRasterSamplesEXT;

    // EXT_secondary_color
    public final long
            glSecondaryColor3bEXT,
            glSecondaryColor3sEXT,
            glSecondaryColor3iEXT,
            glSecondaryColor3fEXT,
            glSecondaryColor3dEXT,
            glSecondaryColor3ubEXT,
            glSecondaryColor3usEXT,
            glSecondaryColor3uiEXT,
            glSecondaryColor3bvEXT,
            glSecondaryColor3svEXT,
            glSecondaryColor3ivEXT,
            glSecondaryColor3fvEXT,
            glSecondaryColor3dvEXT,
            glSecondaryColor3ubvEXT,
            glSecondaryColor3usvEXT,
            glSecondaryColor3uivEXT,
            glSecondaryColorPointerEXT;

    // EXT_semaphore
    public final long
            glGenSemaphoresEXT,
            glDeleteSemaphoresEXT,
            glIsSemaphoreEXT,
            glSemaphoreParameterui64vEXT,
            glGetSemaphoreParameterui64vEXT,
            glWaitSemaphoreEXT,
            glSignalSemaphoreEXT;

    // EXT_semaphore_fd
    public final long
            glImportSemaphoreFdEXT;

    // EXT_semaphore_win32
    public final long
            glImportSemaphoreWin32HandleEXT,
            glImportSemaphoreWin32NameEXT;

    // EXT_separate_shader_objects
    public final long
            glUseShaderProgramEXT,
            glActiveProgramEXT,
            glCreateShaderProgramEXT;

    // EXT_shader_framebuffer_fetch_non_coherent
    public final long
            glFramebufferFetchBarrierEXT;

    // EXT_shader_image_load_store
    public final long
            glBindImageTextureEXT,
            glMemoryBarrierEXT;

    // EXT_stencil_clear_tag
    public final long
            glStencilClearTagEXT;

    // EXT_stencil_two_side
    public final long
            glActiveStencilFaceEXT;

    // EXT_texture_buffer_object
    public final long
            glTexBufferEXT;

    // EXT_texture_integer
    public final long
            glClearColorIiEXT,
            glClearColorIuiEXT,
            glTexParameterIivEXT,
            glTexParameterIuivEXT,
            glGetTexParameterIivEXT,
            glGetTexParameterIuivEXT;

    // EXT_texture_storage
    public final long
            glTexStorage1DEXT,
            glTexStorage2DEXT,
            glTexStorage3DEXT;

    // EXT_timer_query
    public final long
            glGetQueryObjecti64vEXT,
            glGetQueryObjectui64vEXT;

    // EXT_transform_feedback
    public final long
            glBindBufferRangeEXT,
            glBindBufferOffsetEXT,
            glBindBufferBaseEXT,
            glBeginTransformFeedbackEXT,
            glEndTransformFeedbackEXT,
            glTransformFeedbackVaryingsEXT,
            glGetTransformFeedbackVaryingEXT;

    // EXT_vertex_attrib_64bit
    public final long
            glVertexAttribL1dEXT,
            glVertexAttribL2dEXT,
            glVertexAttribL3dEXT,
            glVertexAttribL4dEXT,
            glVertexAttribL1dvEXT,
            glVertexAttribL2dvEXT,
            glVertexAttribL3dvEXT,
            glVertexAttribL4dvEXT,
            glVertexAttribLPointerEXT,
            glGetVertexAttribLdvEXT;

    // EXT_win32_keyed_mutex
    public final long
            glAcquireKeyedMutexWin32EXT,
            glReleaseKeyedMutexWin32EXT;

    // EXT_window_rectangles
    public final long
            glWindowRectanglesEXT;

    // EXT_x11_sync_object
    public final long
            glImportSyncEXT;

    // GREMEDY_frame_terminator
    public final long
            glFrameTerminatorGREMEDY;

    // GREMEDY_string_marker
    public final long
            glStringMarkerGREMEDY;

    // INTEL_framebuffer_CMAA
    public final long
            glApplyFramebufferAttachmentCMAAINTEL;

    // INTEL_map_texture
    public final long
            glSyncTextureINTEL,
            glUnmapTexture2DINTEL,
            glMapTexture2DINTEL;

    // INTEL_performance_query
    public final long
            glBeginPerfQueryINTEL,
            glCreatePerfQueryINTEL,
            glDeletePerfQueryINTEL,
            glEndPerfQueryINTEL,
            glGetFirstPerfQueryIdINTEL,
            glGetNextPerfQueryIdINTEL,
            glGetPerfCounterInfoINTEL,
            glGetPerfQueryDataINTEL,
            glGetPerfQueryIdByNameINTEL,
            glGetPerfQueryInfoINTEL;

    // KHR_blend_equation_advanced
    public final long
            glBlendBarrierKHR;

    // KHR_parallel_shader_compile
    public final long
            glMaxShaderCompilerThreadsKHR;

    // MESA_framebuffer_flip_y
    public final long
            glFramebufferParameteriMESA,
            glGetFramebufferParameterivMESA;

    // NV_alpha_to_coverage_dither_control
    public final long
            glAlphaToCoverageDitherControlNV;

    // NV_bindless_multi_draw_indirect
    public final long
            glMultiDrawArraysIndirectBindlessNV,
            glMultiDrawElementsIndirectBindlessNV;

    // NV_bindless_multi_draw_indirect_count
    public final long
            glMultiDrawArraysIndirectBindlessCountNV,
            glMultiDrawElementsIndirectBindlessCountNV;

    // NV_bindless_texture
    public final long
            glGetTextureHandleNV,
            glGetTextureSamplerHandleNV,
            glMakeTextureHandleResidentNV,
            glMakeTextureHandleNonResidentNV,
            glGetImageHandleNV,
            glMakeImageHandleResidentNV,
            glMakeImageHandleNonResidentNV,
            glUniformHandleui64NV,
            glUniformHandleui64vNV,
            glProgramUniformHandleui64NV,
            glProgramUniformHandleui64vNV,
            glIsTextureHandleResidentNV,
            glIsImageHandleResidentNV;

    // NV_blend_equation_advanced
    public final long
            glBlendParameteriNV,
            glBlendBarrierNV;

    // NV_clip_space_w_scaling
    public final long
            glViewportPositionWScaleNV;

    // NV_command_list
    public final long
            glCreateStatesNV,
            glDeleteStatesNV,
            glIsStateNV,
            glStateCaptureNV,
            glGetCommandHeaderNV,
            glGetStageIndexNV,
            glDrawCommandsNV,
            glDrawCommandsAddressNV,
            glDrawCommandsStatesNV,
            glDrawCommandsStatesAddressNV,
            glCreateCommandListsNV,
            glDeleteCommandListsNV,
            glIsCommandListNV,
            glListDrawCommandsStatesClientNV,
            glCommandListSegmentsNV,
            glCompileCommandListNV,
            glCallCommandListNV;

    // NV_conditional_render
    public final long
            glBeginConditionalRenderNV,
            glEndConditionalRenderNV;

    // NV_conservative_raster
    public final long
            glSubpixelPrecisionBiasNV;

    // NV_conservative_raster_dilate
    public final long
            glConservativeRasterParameterfNV;

    // NV_conservative_raster_pre_snap_triangles
    public final long
            glConservativeRasterParameteriNV;

    // NV_copy_image
    public final long
            glCopyImageSubDataNV;

    // NV_depth_buffer_float
    public final long
            glDepthRangedNV,
            glClearDepthdNV,
            glDepthBoundsdNV;

    // NV_draw_texture
    public final long
            glDrawTextureNV;

    // NV_draw_vulkan_image
    public final long
            glDrawVkImageNV,
            glGetVkProcAddrNV,
            glWaitVkSemaphoreNV,
            glSignalVkSemaphoreNV,
            glSignalVkFenceNV;

    // NV_explicit_multisample
    public final long
            glGetMultisamplefvNV,
            glSampleMaskIndexedNV,
            glTexRenderbufferNV;

    // NV_fence
    public final long
            glDeleteFencesNV,
            glGenFencesNV,
            glIsFenceNV,
            glTestFenceNV,
            glGetFenceivNV,
            glFinishFenceNV,
            glSetFenceNV;

    // NV_fragment_coverage_to_color
    public final long
            glFragmentCoverageColorNV;

    // NV_framebuffer_mixed_samples
    public final long
            glCoverageModulationTableNV,
            glGetCoverageModulationTableNV,
            glCoverageModulationNV;

    // NV_framebuffer_multisample_coverage
    public final long
            glRenderbufferStorageMultisampleCoverageNV;

    // NV_gpu_multicast
    public final long
            glRenderGpuMaskNV,
            glMulticastBufferSubDataNV,
            glMulticastCopyBufferSubDataNV,
            glMulticastCopyImageSubDataNV,
            glMulticastBlitFramebufferNV,
            glMulticastFramebufferSampleLocationsfvNV,
            glMulticastBarrierNV,
            glMulticastWaitSyncNV,
            glMulticastGetQueryObjectivNV,
            glMulticastGetQueryObjectuivNV,
            glMulticastGetQueryObjecti64vNV,
            glMulticastGetQueryObjectui64vNV;

    // NV_half_float
    public final long
            glVertex2hNV,
            glVertex2hvNV,
            glVertex3hNV,
            glVertex3hvNV,
            glVertex4hNV,
            glVertex4hvNV,
            glNormal3hNV,
            glNormal3hvNV,
            glColor3hNV,
            glColor3hvNV,
            glColor4hNV,
            glColor4hvNV,
            glTexCoord1hNV,
            glTexCoord1hvNV,
            glTexCoord2hNV,
            glTexCoord2hvNV,
            glTexCoord3hNV,
            glTexCoord3hvNV,
            glTexCoord4hNV,
            glTexCoord4hvNV,
            glMultiTexCoord1hNV,
            glMultiTexCoord1hvNV,
            glMultiTexCoord2hNV,
            glMultiTexCoord2hvNV,
            glMultiTexCoord3hNV,
            glMultiTexCoord3hvNV,
            glMultiTexCoord4hNV,
            glMultiTexCoord4hvNV,
            glFogCoordhNV,
            glFogCoordhvNV,
            glSecondaryColor3hNV,
            glSecondaryColor3hvNV,
            glVertexWeighthNV,
            glVertexWeighthvNV,
            glVertexAttrib1hNV,
            glVertexAttrib1hvNV,
            glVertexAttrib2hNV,
            glVertexAttrib2hvNV,
            glVertexAttrib3hNV,
            glVertexAttrib3hvNV,
            glVertexAttrib4hNV,
            glVertexAttrib4hvNV,
            glVertexAttribs1hvNV,
            glVertexAttribs2hvNV,
            glVertexAttribs3hvNV,
            glVertexAttribs4hvNV;

    // NV_internalformat_sample_query
    public final long
            glGetInternalformatSampleivNV;

    // NV_memory_attachment
    public final long
            glGetMemoryObjectDetachedResourcesuivNV,
            glResetMemoryObjectParameterNV,
            glTexAttachMemoryNV,
            glBufferAttachMemoryNV,
            glTextureAttachMemoryNV,
            glNamedBufferAttachMemoryNV;

    // NV_memory_object_sparse
    public final long
            glBufferPageCommitmentMemNV,
            glNamedBufferPageCommitmentMemNV,
            glTexPageCommitmentMemNV,
            glTexturePageCommitmentMemNV;

    // NV_mesh_shader
    public final long
            glDrawMeshTasksNV,
            glDrawMeshTasksIndirectNV,
            glMultiDrawMeshTasksIndirectNV,
            glMultiDrawMeshTasksIndirectCountNV;

    // NV_path_rendering
    public final long
            glPathCommandsNV,
            glPathCoordsNV,
            glPathSubCommandsNV,
            glPathSubCoordsNV,
            glPathStringNV,
            glPathGlyphsNV,
            glPathGlyphRangeNV,
            glPathGlyphIndexArrayNV,
            glPathMemoryGlyphIndexArrayNV,
            glCopyPathNV,
            glWeightPathsNV,
            glInterpolatePathsNV,
            glTransformPathNV,
            glPathParameterivNV,
            glPathParameteriNV,
            glPathParameterfvNV,
            glPathParameterfNV,
            glPathDashArrayNV,
            glGenPathsNV,
            glDeletePathsNV,
            glIsPathNV,
            glPathStencilFuncNV,
            glPathStencilDepthOffsetNV,
            glStencilFillPathNV,
            glStencilStrokePathNV,
            glStencilFillPathInstancedNV,
            glStencilStrokePathInstancedNV,
            glPathCoverDepthFuncNV,
            glPathColorGenNV,
            glPathTexGenNV,
            glPathFogGenNV,
            glCoverFillPathNV,
            glCoverStrokePathNV,
            glCoverFillPathInstancedNV,
            glCoverStrokePathInstancedNV,
            glStencilThenCoverFillPathNV,
            glStencilThenCoverStrokePathNV,
            glStencilThenCoverFillPathInstancedNV,
            glStencilThenCoverStrokePathInstancedNV,
            glPathGlyphIndexRangeNV,
            glProgramPathFragmentInputGenNV,
            glGetPathParameterivNV,
            glGetPathParameterfvNV,
            glGetPathCommandsNV,
            glGetPathCoordsNV,
            glGetPathDashArrayNV,
            glGetPathMetricsNV,
            glGetPathMetricRangeNV,
            glGetPathSpacingNV,
            glGetPathColorGenivNV,
            glGetPathColorGenfvNV,
            glGetPathTexGenivNV,
            glGetPathTexGenfvNV,
            glIsPointInFillPathNV,
            glIsPointInStrokePathNV,
            glGetPathLengthNV,
            glPointAlongPathNV,
            glMatrixLoad3x2fNV,
            glMatrixLoad3x3fNV,
            glMatrixLoadTranspose3x3fNV,
            glMatrixMult3x2fNV,
            glMatrixMult3x3fNV,
            glMatrixMultTranspose3x3fNV,
            glGetProgramResourcefvNV;

    // NV_pixel_data_range
    public final long
            glPixelDataRangeNV,
            glFlushPixelDataRangeNV;

    // NV_point_sprite
    public final long
            glPointParameteriNV,
            glPointParameterivNV;

    // NV_primitive_restart
    public final long
            glPrimitiveRestartNV,
            glPrimitiveRestartIndexNV;

    // NV_query_resource
    public final long
            glQueryResourceNV;

    // NV_query_resource_tag
    public final long
            glGenQueryResourceTagNV,
            glDeleteQueryResourceTagNV,
            glQueryResourceTagNV;

    // NV_sample_locations
    public final long
            glFramebufferSampleLocationsfvNV,
            glNamedFramebufferSampleLocationsfvNV,
            glResolveDepthValuesNV;

    // NV_scissor_exclusive
    public final long
            glScissorExclusiveArrayvNV,
            glScissorExclusiveNV;

    // NV_shader_buffer_load
    public final long
            glMakeBufferResidentNV,
            glMakeBufferNonResidentNV,
            glIsBufferResidentNV,
            glMakeNamedBufferResidentNV,
            glMakeNamedBufferNonResidentNV,
            glIsNamedBufferResidentNV,
            glGetBufferParameterui64vNV,
            glGetNamedBufferParameterui64vNV,
            glGetIntegerui64vNV,
            glUniformui64NV,
            glUniformui64vNV,
            glProgramUniformui64NV,
            glProgramUniformui64vNV;

    // NV_shading_rate_image
    public final long
            glBindShadingRateImageNV,
            glShadingRateImagePaletteNV,
            glGetShadingRateImagePaletteNV,
            glShadingRateImageBarrierNV,
            glShadingRateSampleOrderNV,
            glShadingRateSampleOrderCustomNV,
            glGetShadingRateSampleLocationivNV;

    // NV_texture_barrier
    public final long
            glTextureBarrierNV;

    // NV_texture_multisample
    public final long
            glTexImage2DMultisampleCoverageNV,
            glTexImage3DMultisampleCoverageNV,
            glTextureImage2DMultisampleNV,
            glTextureImage3DMultisampleNV,
            glTextureImage2DMultisampleCoverageNV,
            glTextureImage3DMultisampleCoverageNV;

    // NV_timeline_semaphore
    public final long
            glCreateSemaphoresNV,
            glSemaphoreParameterivNV,
            glGetSemaphoreParameterivNV;

    // NV_transform_feedback
    public final long
            glBeginTransformFeedbackNV,
            glEndTransformFeedbackNV,
            glTransformFeedbackAttribsNV,
            glBindBufferRangeNV,
            glBindBufferOffsetNV,
            glBindBufferBaseNV,
            glTransformFeedbackVaryingsNV,
            glActiveVaryingNV,
            glGetVaryingLocationNV,
            glGetActiveVaryingNV,
            glGetTransformFeedbackVaryingNV,
            glTransformFeedbackStreamAttribsNV;

    // NV_transform_feedback2
    public final long
            glBindTransformFeedbackNV,
            glDeleteTransformFeedbacksNV,
            glGenTransformFeedbacksNV,
            glIsTransformFeedbackNV,
            glPauseTransformFeedbackNV,
            glResumeTransformFeedbackNV,
            glDrawTransformFeedbackNV;

    // NV_vertex_array_range
    public final long
            glVertexArrayRangeNV,
            glFlushVertexArrayRangeNV;

    // NV_vertex_attrib_integer_64bit
    public final long
            glVertexAttribL1i64NV,
            glVertexAttribL2i64NV,
            glVertexAttribL3i64NV,
            glVertexAttribL4i64NV,
            glVertexAttribL1i64vNV,
            glVertexAttribL2i64vNV,
            glVertexAttribL3i64vNV,
            glVertexAttribL4i64vNV,
            glVertexAttribL1ui64NV,
            glVertexAttribL2ui64NV,
            glVertexAttribL3ui64NV,
            glVertexAttribL4ui64NV,
            glVertexAttribL1ui64vNV,
            glVertexAttribL2ui64vNV,
            glVertexAttribL3ui64vNV,
            glVertexAttribL4ui64vNV,
            glGetVertexAttribLi64vNV,
            glGetVertexAttribLui64vNV,
            glVertexAttribLFormatNV;

    // NV_vertex_buffer_unified_memory
    public final long
            glBufferAddressRangeNV,
            glVertexFormatNV,
            glNormalFormatNV,
            glColorFormatNV,
            glIndexFormatNV,
            glTexCoordFormatNV,
            glEdgeFlagFormatNV,
            glSecondaryColorFormatNV,
            glFogCoordFormatNV,
            glVertexAttribFormatNV,
            glVertexAttribIFormatNV,
            glGetIntegerui64i_vNV;

    // NV_viewport_swizzle
    public final long
            glViewportSwizzleNV;

    // NVX_conditional_render
    public final long
            glBeginConditionalRenderNVX,
            glEndConditionalRenderNVX;

    // NVX_gpu_multicast2
    public final long
            glAsyncCopyImageSubDataNVX,
            glAsyncCopyBufferSubDataNVX,
            glUploadGpuMaskNVX,
            glMulticastViewportArrayvNVX,
            glMulticastScissorArrayvNVX,
            glMulticastViewportPositionWScaleNVX;

    // NVX_progress_fence
    public final long
            glCreateProgressFenceNVX,
            glSignalSemaphoreui64NVX,
            glWaitSemaphoreui64NVX,
            glClientWaitSemaphoreui64NVX;

    // OVR_multiview
    public final long
            glFramebufferTextureMultiviewOVR,
            glNamedFramebufferTextureMultiviewOVR;

    /** When true, {@link GL11} is supported. */
    public final boolean OpenGL11;
    /** When true, {@link GL12} is supported. */
    public final boolean OpenGL12;
    /** When true, {@link GL13} is supported. */
    public final boolean OpenGL13;
    /** When true, {@link GL14} is supported. */
    public final boolean OpenGL14;
    /** When true, {@link GL15} is supported. */
    public final boolean OpenGL15;
    /** When true, {@link GL20} is supported. */
    public final boolean OpenGL20;
    /** When true, {@link GL21} is supported. */
    public final boolean OpenGL21;
    /** When true, {@link GL30} is supported. */
    public final boolean OpenGL30;
    /** When true, {@link GL31} is supported. */
    public final boolean OpenGL31;
    /** When true, {@link GL32} is supported. */
    public final boolean OpenGL32;
    /** When true, {@link GL33} is supported. */
    public final boolean OpenGL33;
    /** When true, {@link GL40} is supported. */
    public final boolean OpenGL40;
    /** When true, {@link GL41} is supported. */
    public final boolean OpenGL41;
    /** When true, {@link GL42} is supported. */
    public final boolean OpenGL42;
    /** When true, {@link GL43} is supported. */
    public final boolean OpenGL43;
    /** When true, {@link GL44} is supported. */
    public final boolean OpenGL44;
    /** When true, {@link GL45} is supported. */
    public final boolean OpenGL45;
    /** When true, {@link GL46} is supported. */
    public final boolean OpenGL46;
    /** When true, {@link _3DFXTextureCompressionFXT1} is supported. */
    public final boolean GL_3DFX_texture_compression_FXT1;
    /** When true, {@link AMDBlendMinmaxFactor} is supported. */
    public final boolean GL_AMD_blend_minmax_factor;
    /**
     * When true, the <a target="_blank" href="https://www.khronos.org/registry/OpenGL/extensions/AMD/AMD_conservative_depth.txt">AMD_conservative_depth</a> extension is supported.
     *
     * <p>There is a common optimization for hardware accelerated implementation of OpenGL which relies on an early depth test to be run before the fragment
     * shader so that the shader evaluation can be skipped if the fragment ends up being discarded because it is occluded.</p>
     *
     * <p>This optimization does not affect the final rendering, and is typically possible when the fragment does not change the depth programmatically. (i.e.: it
     * does not write to the built-in {@code gl_FragDepth} output). There are, however a class of operations on the depth in the shader which could still be
     * performed while allowing the early depth test to operate.</p>
     *
     * <p>This extension allows the application to pass enough information to the GL implementation to activate such optimizations safely.</p>
     *
     * <p>Requires {@link GL30 OpenGL 3.0}. Promoted to core in {@link GL42 OpenGL 4.2}.</p>
     */
    public final boolean GL_AMD_conservative_depth;
    /** When true, {@link AMDDebugOutput} is supported. */
    public final boolean GL_AMD_debug_output;
    /** When true, {@link AMDDepthClampSeparate} is supported. */
    public final boolean GL_AMD_depth_clamp_separate;
    /** When true, {@link AMDDrawBuffersBlend} is supported. */
    public final boolean GL_AMD_draw_buffers_blend;
    /** When true, {@link AMDFramebufferMultisampleAdvanced} is supported. */
    public final boolean GL_AMD_framebuffer_multisample_advanced;
    /**
     * When true, the <a target="_blank" href="https://www.khronos.org/registry/OpenGL/extensions/AMD/AMD_gcn_shader.txt">AMD_gcn_shader</a> extension is supported.
     *
     * <p>This extension exposes miscellaneous features of the AMD "Graphics Core Next" shader architecture that do not cleanly fit into other extensions
     * and are not significant enough alone to warrant their own extensions. This includes cross-SIMD lane ballots, cube map query functions and a
     * functionality to query the elapsed shader core time.</p>
     *
     * <p>Requires {@link #GL_AMD_gpu_shader_int64 AMD_gpu_shader_int64} or {@link #GL_NV_gpu_shader5 NV_gpu_shader5}.</p>
     */
    public final boolean GL_AMD_gcn_shader;
    /** When true, {@link AMDGPUShaderHalfFloat} is supported. */
    public final boolean GL_AMD_gpu_shader_half_float;
    /** When true, {@link AMDGPUShaderHalfFloatFetch} is supported. */
    public final boolean GL_AMD_gpu_shader_half_float_fetch;
    /**
     * When true, the <a target="_blank" href="https://www.khronos.org/registry/OpenGL/extensions/AMD/AMD_gpu_shader_int16.txt">AMD_gpu_shader_int16</a> extension is supported.
     *
     * <p>This extension was developed to allow implementations supporting 16-bit integers to expose the feature in GLSL.</p>
     *
     * <p>The extension introduces the following features for all shader types:</p>
     *
     * <ul>
     * <li>new built-in functions to pack and unpack 32-bit integer types into a two-component 16-bit integer vector;</li>
     * <li>new built-in functions to convert half-precision floating-point values to or from their 16-bit integer bit encodings;</li>
     * <li>vector relational functions supporting comparisons of vectors of 16-bit integer types; and</li>
     * <li>common functions abs, frexp, ldexp, sign, min, max, clamp, and mix supporting arguments of 16-bit integer types.</li>
     * </ul>
     *
     * <p>Requires GLSL 4.00.</p>
     */
    public final boolean GL_AMD_gpu_shader_int16;
    /** When true, {@link AMDGPUShaderInt64} is supported. */
    public final boolean GL_AMD_gpu_shader_int64;
    /** When true, {@link AMDInterleavedElements} is supported. */
    public final boolean GL_AMD_interleaved_elements;
    /** When true, {@link AMDOcclusionQueryEvent} is supported. */
    public final boolean GL_AMD_occlusion_query_event;
    /** When true, {@link AMDPerformanceMonitor} is supported. */
    public final boolean GL_AMD_performance_monitor;
    /** When true, {@link AMDPinnedMemory} is supported. */
    public final boolean GL_AMD_pinned_memory;
    /** When true, {@link AMDQueryBufferObject} is supported. */
    public final boolean GL_AMD_query_buffer_object;
    /** When true, {@link AMDSamplePositions} is supported. */
    public final boolean GL_AMD_sample_positions;
    /** When true, {@link AMDSeamlessCubemapPerTexture} is supported. */
    public final boolean GL_AMD_seamless_cubemap_per_texture;
    /**
     * When true, the <a target="_blank" href="https://www.khronos.org/registry/OpenGL/extensions/AMD/AMD_shader_atomic_counter_ops.txt">AMD_shader_atomic_counter_ops</a> extension is supported.
     *
     * <p>This extension is written against the OpenGL 4.3 (core) specification and the GLSL 4.30.7 specification.</p>
     *
     * <p>Requires {@link GL42 OpenGL 4.2} or {@link #GL_ARB_shader_atomic_counters ARB_shader_atomic_counters}.</p>
     */
    public final boolean GL_AMD_shader_atomic_counter_ops;
    /**
     * When true, the <a target="_blank" href="https://www.khronos.org/registry/OpenGL/extensions/AMD/AMD_shader_ballot.txt">AMD_shader_ballot</a> extension is supported.
     *
     * <p>The extensions {@code ARB_shader_group_vote} and {@code ARB_shader_ballot} introduced the concept of sub-groups and a set of operations that allow data
     * exchange across shader invocations within a sub-group.</p>
     *
     * <p>This extension further extends the capabilities of these extensions with additional sub-group operations.</p>
     *
     * <p>Requires {@link #GL_ARB_shader_group_vote ARB_shader_group_vote}, {@link #GL_ARB_shader_ballot ARB_shader_ballot} and {@link ARBGPUShaderInt64 ARB_gpu_shader_int64} or {@link AMDGPUShaderInt64 AMD_gpu_shader_int64}.</p>
     */
    public final boolean GL_AMD_shader_ballot;
    /**
     * When true, the <a target="_blank" href="https://www.khronos.org/registry/OpenGL/extensions/AMD/AMD_shader_explicit_vertex_parameter.txt">AMD_shader_explicit_vertex_parameter</a> extension is supported.
     *
     * <p>Unextended GLSL provides a set of fixed function interpolation modes and even those are limited to certain types of interpolants (for example,
     * interpolation of integer and double isn't supported).</p>
     *
     * <p>This extension introduces new built-in functions allowing access to vertex parameters explicitly in the fragment shader. It also exposes barycentric
     * coordinates as new built-in variables, which can be used to implement custom interpolation algorithms using shader code.</p>
     *
     * <p>Requires {@link GL20 OpenGL 2.0} or {@link ARBShaderObjects ARB_shader_objects}.</p>
     */
    public final boolean GL_AMD_shader_explicit_vertex_parameter;
    /**
     * When true, the <a target="_blank" href="https://www.khronos.org/registry/OpenGL/extensions/AMD/AMD_shader_image_load_store_lod.txt">AMD_shader_image_load_store_lod</a> extension is supported.
     *
     * <p>This extension was developed based on the {@link ARBShaderImageLoadStore ARB_shader_image_load_store} extension to allow implementations supporting loads and stores on mipmap
     * texture images.</p>
     *
     * <p>Requires {@link GL40 OpenGL 4.0} and GLSL 4.00</p>
     */
    public final boolean GL_AMD_shader_image_load_store_lod;
    /**
     * When true, the <a target="_blank" href="https://www.khronos.org/registry/OpenGL/extensions/AMD/AMD_shader_stencil_export.txt">AMD_shader_stencil_export</a> extension is supported.
     *
     * <p>In OpenGL, the stencil test is a powerful mechanism to selectively discard fragments based on the content of the stencil buffer. However, facilites to
     * update the content of the stencil buffer are limited to operations such as incrementing the existing value, or overwriting with a fixed reference value.</p>
     *
     * <p>This extension provides a mechanism whereby a shader may generate the stencil reference value per invocation. When stencil testing is enabled, this
     * allows the test to be performed against the value generated in the shader. When the stencil operation is set to {@link GL11#GL_REPLACE REPLACE}, this allows a value generated
     * in the shader to be written to the stencil buffer directly.</p>
     *
     * <p>Requires {@link #GL_ARB_fragment_shader ARB_fragment_shader}.</p>
     */
    public final boolean GL_AMD_shader_stencil_export;
    /**
     * When true, the <a target="_blank" href="https://www.khronos.org/registry/OpenGL/extensions/AMD/AMD_shader_trinary_minmax.txt">AMD_shader_trinary_minmax</a> extension is supported.
     *
     * <p>This extension introduces three new trinary built-in functions to the OpenGL Shading Languages. These functions allow the minimum, maximum or median of
     * three inputs to be found with a single function call. These operations may be useful for sorting and filtering operations, for example. By explicitly
     * performing a trinary operation with a single built-in function, shader compilers and optimizers may be able to generate better instruction sequences for
     * perform sorting and other multi-input functions.</p>
     *
     * <p>Requires {@link GL20 OpenGL 2.0} or {@link #GL_ARB_shader_objects ARB_shader_objects}.</p>
     */
    public final boolean GL_AMD_shader_trinary_minmax;
    /** When true, {@link AMDSparseTexture} is supported. */
    public final boolean GL_AMD_sparse_texture;
    /** When true, {@link AMDStencilOperationExtended} is supported. */
    public final boolean GL_AMD_stencil_operation_extended;
    /**
     * When true, the <a target="_blank" href="https://www.khronos.org/registry/OpenGL/extensions/AMD/AMD_texture_gather_bias_lod.txt">AMD_texture_gather_bias_lod</a> extension is supported.
     *
     * <p>This extension was developed based on existing built-in texture gather functions to allow implementations supporting bias of implicit level of detail
     * and explicit control of level of detail in texture gather operations.</p>
     */
    public final boolean GL_AMD_texture_gather_bias_lod;
    /**
     * When true, the <a target="_blank" href="https://www.khronos.org/registry/OpenGL/extensions/AMD/AMD_texture_texture4.txt">AMD_texture_texture4</a> extension is supported.
     *
     * <p>This extension adds new shading language built-in texture functions to the shading language.</p>
     *
     * <p>These texture functions may be used to access one component textures.</p>
     *
     * <p>The {@code texture4} built-in function returns a texture value derived from a 2x2 set of texels in the image array of level levelbase is selected. These
     * texels are selected in the same way as when the value of {@link GL11#GL_TEXTURE_MIN_FILTER TEXTURE_MIN_FILTER} is {@link GL11#GL_LINEAR LINEAR}, but instead of these texels being filtered to generate the
     * texture value, the R, G, B and A texture values are derived directly from these four texels.</p>
     */
    public final boolean GL_AMD_texture_texture4;
    /**
     * When true, the <a target="_blank" href="https://www.khronos.org/registry/OpenGL/extensions/AMD/AMD_transform_feedback3_lines_triangles.txt">AMD_transform_feedback3_lines_triangles</a> extension is supported.
     *
     * <p>OpenGL 4.0 introduced the ability to record primitives into multiple output streams using transform feedback. However, the restriction that all streams
     * must output {@link GL11#GL_POINT POINT} primitives when more than one output stream is active was also introduced. This extension simply removes that restriction, allowing
     * the same set of primitives to be used with multiple transform feedback streams as with a single stream.</p>
     *
     * <p>Requires {@link GL40 OpenGL 4.0} or {@link ARBTransformFeedback3 ARB_transform_feedback3}.</p>
     */
    public final boolean GL_AMD_transform_feedback3_lines_triangles;
    /** When true, {@link AMDTransformFeedback4} is supported. */
    public final boolean GL_AMD_transform_feedback4;
    /**
     * When true, the <a target="_blank" href="https://www.khronos.org/registry/OpenGL/extensions/AMD/AMD_vertex_shader_layer.txt">AMD_vertex_shader_layer</a> extension is supported.
     *
     * <p>The {@code gl_Layer} built-in shading language variable was introduced with the {@link #GL_ARB_geometry_shader4 ARB_geometry_shader4} extension and subsequently promoted to core
     * OpenGL in version 3.2. This variable is an output from the geometry shader stage that allows rendering to be directed to a specific layer of an array
     * texture, slice of a 3D texture or face of a cube map or cube map array attachment of the framebuffer. Thus, this extremely useful functionality is only
     * available if a geometry shader is present - even if the geometry shader is not otherwise required by the application. This adds overhead to the graphics
     * processing pipeline, and complexity to applications. It also precludes implementations that cannot support geometry shaders from supporting rendering to
     * layered framebuffer attachments.</p>
     *
     * <p>This extension exposes the {@code gl_Layer} built-in variable in the vertex shader, allowing rendering to be directed to layered framebuffer attachments
     * with only a vertex and fragment shader present. Combined with features such as instancing, or static vertex attributes and so on, this allows a wide
     * variety of techniques to be implemented without the requirement for a geometry shader to be present.</p>
     *
     * <p>Requires {@link GL30 OpenGL 3.0} or {@link #GL_EXT_texture_array EXT_texture_array}.</p>
     */
    public final boolean GL_AMD_vertex_shader_layer;
    /** When true, {@link AMDVertexShaderTessellator} is supported. */
    public final boolean GL_AMD_vertex_shader_tessellator;
    /**
     * When true, the <a target="_blank" href="https://www.khronos.org/registry/OpenGL/extensions/AMD/AMD_vertex_shader_viewport_index.txt">AMD_vertex_shader_viewport_index</a> extension is supported.
     *
     * <p>The {@code gl_ViewportIndex} built-in variable was introduced by the {@link #GL_ARB_viewport_array ARB_viewport_array} extension and {@link GL41 OpenGL 4.1}. This variable is available
     * in un-extended OpenGL only to the geometry shader. When written in the geometry shader, it causes geometry to be directed to one of an array of several
     * independent viewport rectangles.</p>
     *
     * <p>In order to use any viewport other than zero, a geometry shader must be present. Geometry shaders introduce processing overhead and potential
     * performance issues. This extension exposes the {@code gl_ViewportIndex} built-in variable to the vertex shader, allowing the functionality introduced by
     * ARB_viewport_array to be accessed without requiring a geometry shader to be present.</p>
     *
     * <p>Requires {@link GL41 OpenGL 4.1} or {@link #GL_ARB_viewport_array ARB_viewport_array}.</p>
     */
    public final boolean GL_AMD_vertex_shader_viewport_index;
    /**
     * When true, the <a target="_blank" href="https://www.khronos.org/registry/OpenGL/extensions/ARB/ARB_arrays_of_arrays.txt">ARB_arrays_of_arrays</a> extension is supported.
     *
     * <p>This extension removes the restriction that arrays cannot be formed into arrays, allowing arrays of arrays to be declared.</p>
     *
     * <p>Requires GLSL 1.2. Promoted to core in {@link GL43 OpenGL 4.3}.</p>
     */
    public final boolean GL_ARB_arrays_of_arrays;
    /** When true, {@link ARBBaseInstance} is supported. */
    public final boolean GL_ARB_base_instance;
    /** When true, {@link ARBBindlessTexture} is supported. */
    public final boolean GL_ARB_bindless_texture;
    /** When true, {@link ARBBlendFuncExtended} is supported. */
    public final boolean GL_ARB_blend_func_extended;
    /** When true, {@link ARBBufferStorage} is supported. */
    public final boolean GL_ARB_buffer_storage;
    /** When true, {@link ARBCLEvent} is supported. */
    public final boolean GL_ARB_cl_event;
    /** When true, {@link ARBClearBufferObject} is supported. */
    public final boolean GL_ARB_clear_buffer_object;
    /** When true, {@link ARBClearTexture} is supported. */
    public final boolean GL_ARB_clear_texture;
    /** When true, {@link ARBClipControl} is supported. */
    public final boolean GL_ARB_clip_control;
    /** When true, {@link ARBColorBufferFloat} is supported. */
    public final boolean GL_ARB_color_buffer_float;
    /**
     * When true, the <a target="_blank" href="https://www.khronos.org/registry/OpenGL/extensions/ARB/ARB_compatibility.txt">ARB_compatibility</a> extension is supported.
     *
     * <p>This extension restores features deprecated by {@link GL30 OpenGL 3.0}.</p>
     */
    public final boolean GL_ARB_compatibility;
    /** When true, {@link ARBCompressedTexturePixelStorage} is supported. */
    public final boolean GL_ARB_compressed_texture_pixel_storage;
    /** When true, {@link ARBComputeShader} is supported. */
    public final boolean GL_ARB_compute_shader;
    /** When true, {@link ARBComputeVariableGroupSize} is supported. */
    public final boolean GL_ARB_compute_variable_group_size;
    /** When true, {@link ARBConditionalRenderInverted} is supported. */
    public final boolean GL_ARB_conditional_render_inverted;
    /**
     * When true, the <a target="_blank" href="https://www.khronos.org/registry/OpenGL/extensions/ARB/ARB_conservative_depth.txt">ARB_conservative_depth</a> extension is supported.
     *
     * <p>There is a common optimization for hardware accelerated implementation of OpenGL which relies on an early depth test to be run before the fragment
     * shader so that the shader evaluation can be skipped if the fragment ends up being discarded because it is occluded.</p>
     *
     * <p>This optimization does not affect the final rendering, and is typically possible when the fragment does not change the depth programmatically. (i.e.: it
     * does not write to the built-in gl_FragDepth output). There are, however a class of operations on the depth in the shader which could still be performed
     * while allowing the early depth test to operate.</p>
     *
     * <p>This extension allows the application to pass enough information to the GL implementation to activate such optimizations safely.</p>
     *
     * <p>Requires {@link GL30 OpenGL 3.0}. Promoted to core in {@link GL42 OpenGL 4.2}.</p>
     */
    public final boolean GL_ARB_conservative_depth;
    /** When true, {@link ARBCopyBuffer} is supported. */
    public final boolean GL_ARB_copy_buffer;
    /** When true, {@link ARBCopyImage} is supported. */
    public final boolean GL_ARB_copy_image;
    /** When true, {@link ARBCullDistance} is supported. */
    public final boolean GL_ARB_cull_distance;
    /** When true, {@link ARBDebugOutput} is supported. */
    public final boolean GL_ARB_debug_output;
    /** When true, {@link ARBDepthBufferFloat} is supported. */
    public final boolean GL_ARB_depth_buffer_float;
    /** When true, {@link ARBDepthClamp} is supported. */
    public final boolean GL_ARB_depth_clamp;
    /** When true, {@link ARBDepthTexture} is supported. */
    public final boolean GL_ARB_depth_texture;
    /**
     * When true, the <a target="_blank" href="https://www.khronos.org/registry/OpenGL/extensions/ARB/ARB_derivative_control.txt">ARB_derivative_control</a> extension is supported.
     *
     * <p>This extension provides control over the spacial granularity at which the underlying implementation computes derivatives.</p>
     *
     * <p>For example, for the coarse-granularity derivative, a single x derivative could be computed for each 2x2 group of pixels, using that same derivative
     * value for all 4 pixels. For the fine-granularity derivative, two derivatives could be computed for each 2x2 group of pixels; one for the top row and one
     * for the bottom row. Implementations vary somewhat on how this is done.</p>
     *
     * <p>To select the coarse derivative, use:</p>
     *
     * <pre><code>
     * dFdxCoarse(p)
     * dFdyCoarse(p)
     * fwidthCoarse(p)</code></pre>
     *
     * <p>To select the fine derivative, use:</p>
     *
     * <pre><code>
     * dFdxFine(p)
     * dFdyFine(p)
     * fwidthFine(p)</code></pre>
     *
     * <p>To select which ever is "better" (based on performance, API hints, or other factors), use:</p>
     *
     * <pre><code>
     * dFdx(p)
     * dFdy(p)
     * fwidth(p)</code></pre>
     *
     * <p>This last set is the set of previously existing built-ins for derivatives, and continues to work in a backward compatible way.</p>
     *
     * <p>Requires {@link GL40 OpenGL 4.0} and GLSL 4.00. Promoted to core in {@link GL45 OpenGL 4.5}.</p>
     */
    public final boolean GL_ARB_derivative_control;
    /** When true, {@link ARBDirectStateAccess} is supported. */
    public final boolean GL_ARB_direct_state_access;
    /** When true, {@link ARBDrawBuffers} is supported. */
    public final boolean GL_ARB_draw_buffers;
    /** When true, {@link ARBDrawBuffersBlend} is supported. */
    public final boolean GL_ARB_draw_buffers_blend;
    /** When true, {@link ARBDrawElementsBaseVertex} is supported. */
    public final boolean GL_ARB_draw_elements_base_vertex;
    /** When true, {@link ARBDrawIndirect} is supported. */
    public final boolean GL_ARB_draw_indirect;
    /** When true, {@link ARBDrawInstanced} is supported. */
    public final boolean GL_ARB_draw_instanced;
    /** When true, {@link ARBEnhancedLayouts} is supported. */
    public final boolean GL_ARB_enhanced_layouts;
    /** When true, {@link ARBES2Compatibility} is supported. */
    public final boolean GL_ARB_ES2_compatibility;
    /** When true, {@link ARBES31Compatibility} is supported. */
    public final boolean GL_ARB_ES3_1_compatibility;
    /** When true, {@link ARBES32Compatibility} is supported. */
    public final boolean GL_ARB_ES3_2_compatibility;
    /** When true, {@link ARBES3Compatibility} is supported. */
    public final boolean GL_ARB_ES3_compatibility;
    /**
     * When true, the <a target="_blank" href="https://www.khronos.org/registry/OpenGL/extensions/ARB/ARB_explicit_attrib_location.txt">ARB_explicit_attrib_location</a> extension is supported.
     *
     * <p>This extension provides a method to pre-assign attribute locations to named vertex shader inputs and color numbers to named fragment shader outputs.
     * This allows applications to globally assign a particular semantic meaning, such as diffuse color or vertex normal, to a particular attribute location
     * without knowing how that attribute will be named in any particular shader.</p>
     *
     * <p>Requires {@link GL20 OpenGL 2.0} or {@link #GL_ARB_vertex_shader ARB_vertex_shader}. Promoted to core in {@link GL33 OpenGL 3.3}.</p>
     */
    public final boolean GL_ARB_explicit_attrib_location;
    /** When true, {@link ARBExplicitUniformLocation} is supported. */
    public final boolean GL_ARB_explicit_uniform_location;
    /**
     * When true, the <a target="_blank" href="https://www.khronos.org/registry/OpenGL/extensions/ARB/ARB_fragment_coord_conventions.txt">ARB_fragment_coord_conventions</a> extension is supported.
     *
     * <p>This extension provides alternative conventions for the fragment coordinate XY location available for programmable fragment processing.</p>
     *
     * <p>The scope of this extension deals *only* with how the fragment coordinate XY location appears during programming fragment processing. Beyond the scope
     * of this extension are coordinate conventions used for rasterization or transformation.</p>
     *
     * <p>In the case of the coordinate conventions for rasterization and transformation, some combination of the viewport, depth range, culling state, and
     * projection matrix state can be reconfigured to adopt other arbitrary clip-space and window-space coordinate space conventions. Adopting other clip-space
     * and window-space conventions involves adjusting existing OpenGL state. However it is non-trivial to massage an arbitrary fragment shader or program to
     * adopt a different window-space coordinate system because such shaders are encoded in various textual representations.</p>
     *
     * <p>The dominant 2D and 3D rendering APIs make two basic choices of convention when locating fragments in window space. The two choices are:</p>
     *
     * <ol>
     * <li>Is the origin nearest the lower-left- or upper-left-most pixel of the window?</li>
     * <li>Is the (x,y) location of the pixel nearest the origin at (0,0) or (0.5,0.5)?</li>
     * </ol>
     *
     * <p>OpenGL assumes a lower-left origin for window coordinates and assumes pixel centers are located at half-pixel coordinates. This means the XY location
     * (0.5,0.5) corresponds to the lower-left-most pixel in a window.</p>
     *
     * <p>Other window coordinate conventions exist for other rendering APIs. X11, GDI, and Direct3D version through DirectX 9 assume an upper-left window origin
     * and locate pixel centers at integer XY values. By this alternative convention, the XY location (0,0) corresponds to the upper-left-most pixel in a window.</p>
     *
     * <p>Direct3D for DirectX 10 assumes an upper-left origin (as do prior DirectX versions) yet assumes half-pixel coordinates (unlike prior DirectX versions).
     * By the DirectX 10 convention, the XY location (0.5,0.5) corresponds to the upper-left-most pixel in a window.</p>
     *
     * <p>Fragment shaders can directly access the location of a given processed fragment in window space. We call this location the "fragment coordinate".</p>
     *
     * <p>This extension provides a means for fragment shaders written in GLSL or OpenGL assembly extensions to specify alternative conventions for determining
     * the fragment coordinate value accessed during programmable fragment processing.</p>
     *
     * <p>The motivation for this extension is to provide an easy, efficient means for fragment shaders accessing a fragment's window-space location to adopt the
     * fragment coordinate convention for which the shader was originally written.</p>
     *
     * <p>Promoted to core in {@link GL32 OpenGL 3.2}.</p>
     */
    public final boolean GL_ARB_fragment_coord_conventions;
    /**
     * When true, the <a target="_blank" href="https://www.khronos.org/registry/OpenGL/extensions/ARB/ARB_fragment_layer_viewport.txt">ARB_fragment_layer_viewport</a> extension is supported.
     *
     * <p>The geometry shader has the special built-in variables gl_Layer and gl_ViewportIndex that specify which layer and viewport primitives are rendered to.
     * Currently the fragment shader does not know which layer or viewport the fragments are being written to without the application implementing their own
     * interface variables between the geometry and fragment shaders.</p>
     *
     * <p>This extension specifies that the gl_Layer and gl_ViewportIndex built-in variables are also available to the fragment shader so the application doesn't
     * need to implement these manually.</p>
     *
     * <p>Requires {@link GL30 OpenGL 3.0} and {@link #GL_ARB_geometry_shader4 ARB_geometry_shader4}, or {@link GL32 OpenGL 3.2}. Promoted to core in {@link GL43 OpenGL 4.3}.</p>
     */
    public final boolean GL_ARB_fragment_layer_viewport;
    /** When true, {@link ARBFragmentProgram} is supported. */
    public final boolean GL_ARB_fragment_program;
    /**
     * When true, the <a target="_blank" href="https://www.khronos.org/registry/OpenGL/extensions/ARB/ARB_fragment_program_shadow.txt">ARB_fragment_program_shadow</a> extension is supported.
     *
     * <p>This extension extends ARB_fragment_program to remove the interaction with ARB_shadow and defines the program option "ARB_fragment_program_shadow".</p>
     *
     * <p>Requires {@link #GL_ARB_fragment_program ARB_fragment_program} and {@link #GL_ARB_shadow ARB_shadow}.</p>
     */
    public final boolean GL_ARB_fragment_program_shadow;
    /** When true, {@link ARBFragmentShader} is supported. */
    public final boolean GL_ARB_fragment_shader;
    /**
     * When true, the <a target="_blank" href="https://www.khronos.org/registry/OpenGL/extensions/ARB/ARB_fragment_shader_interlock.txt">ARB_fragment_shader_interlock</a> extension is supported.
     *
     * <p>In unextended OpenGL 4.5, applications may produce a large number of fragment shader invocations that perform loads and stores to memory using image
     * uniforms, atomic counter uniforms, buffer variables, or pointers. The order in which loads and stores to common addresses are performed by different
     * fragment shader invocations is largely undefined. For algorithms that use shader writes and touch the same pixels more than once, one or more of the
     * following techniques may be required to ensure proper execution ordering:</p>
     *
     * <ul>
     * <li>inserting Finish or WaitSync commands to drain the pipeline between different "passes" or "layers";</li>
     * <li>using only atomic memory operations to write to shader memory (which may be relatively slow and limits how memory may be updated); or</li>
     * <li>injecting spin loops into shaders to prevent multiple shader invocations from touching the same memory concurrently.</li>
     * </ul>
     *
     * <p>This extension provides new GLSL built-in functions beginInvocationInterlockARB() and endInvocationInterlockARB() that delimit a critical section of
     * fragment shader code. For pairs of shader invocations with "overlapping" coverage in a given pixel, the OpenGL implementation will guarantee that the
     * critical section of the fragment shader will be executed for only one fragment at a time.</p>
     *
     * <p>There are four different interlock modes supported by this extension, which are identified by layout qualifiers. The qualifiers
     * "pixel_interlock_ordered" and "pixel_interlock_unordered" provides mutual exclusion in the critical section for any pair of fragments corresponding to
     * the same pixel. When using multisampling, the qualifiers "sample_interlock_ordered" and "sample_interlock_unordered" only provide mutual exclusion for
     * pairs of fragments that both cover at least one common sample in the same pixel; these are recommended for performance if shaders use per-sample data
     * structures.</p>
     *
     * <p>Additionally, when the "pixel_interlock_ordered" or "sample_interlock_ordered" layout qualifier is used, the interlock also guarantees that the
     * critical section for multiple shader invocations with "overlapping" coverage will be executed in the order in which the primitives were processed by
     * the GL. Such a guarantee is useful for applications like blending in the fragment shader, where an application requires that fragment values to be
     * composited in the framebuffer in primitive order.</p>
     *
     * <p>This extension can be useful for algorithms that need to access per-pixel data structures via shader loads and stores. Such algorithms using this
     * extension can access such data structures in the critical section without worrying about other invocations for the same pixel accessing the data
     * structures concurrently. Additionally, the ordering guarantees are useful for cases where the API ordering of fragments is meaningful. For example,
     * applications may be able to execute programmable blending operations in the fragment shader, where the destination buffer is read via image loads and
     * the final value is written via image stores.</p>
     *
     * <p>Requires {@link GL42 OpenGL 4.2} or {@link ARBShaderImageLoadStore ARB_shader_image_load_store}.</p>
     */
    public final boolean GL_ARB_fragment_shader_interlock;
    /** When true, {@link ARBFramebufferNoAttachments} is supported. */
    public final boolean GL_ARB_framebuffer_no_attachments;
    /** When true, {@link ARBFramebufferObject} is supported. */
    public final boolean GL_ARB_framebuffer_object;
    /** When true, {@link ARBFramebufferSRGB} is supported. */
    public final boolean GL_ARB_framebuffer_sRGB;
    /** When true, {@link ARBGeometryShader4} is supported. */
    public final boolean GL_ARB_geometry_shader4;
    /** When true, {@link ARBGetProgramBinary} is supported. */
    public final boolean GL_ARB_get_program_binary;
    /** When true, {@link ARBGetTextureSubImage} is supported. */
    public final boolean GL_ARB_get_texture_sub_image;
    /** When true, {@link ARBGLSPIRV} is supported. */
    public final boolean GL_ARB_gl_spirv;
    /** When true, {@link ARBGPUShader5} is supported. */
    public final boolean GL_ARB_gpu_shader5;
    /** When true, {@link ARBGPUShaderFP64} is supported. */
    public final boolean GL_ARB_gpu_shader_fp64;
    /** When true, {@link ARBGPUShaderInt64} is supported. */
    public final boolean GL_ARB_gpu_shader_int64;
    /** When true, {@link ARBHalfFloatPixel} is supported. */
    public final boolean GL_ARB_half_float_pixel;
    /** When true, {@link ARBHalfFloatVertex} is supported. */
    public final boolean GL_ARB_half_float_vertex;
    /** When true, {@link ARBImaging} is supported. */
    public final boolean GL_ARB_imaging;
    /** When true, {@link ARBIndirectParameters} is supported. */
    public final boolean GL_ARB_indirect_parameters;
    /** When true, {@link ARBInstancedArrays} is supported. */
    public final boolean GL_ARB_instanced_arrays;
    /** When true, {@link ARBInternalformatQuery} is supported. */
    public final boolean GL_ARB_internalformat_query;
    /** When true, {@link ARBInternalformatQuery2} is supported. */
    public final boolean GL_ARB_internalformat_query2;
    /** When true, {@link ARBInvalidateSubdata} is supported. */
    public final boolean GL_ARB_invalidate_subdata;
    /** When true, {@link ARBMapBufferAlignment} is supported. */
    public final boolean GL_ARB_map_buffer_alignment;
    /** When true, {@link ARBMapBufferRange} is supported. */
    public final boolean GL_ARB_map_buffer_range;
    /** When true, {@link ARBMatrixPalette} is supported. */
    public final boolean GL_ARB_matrix_palette;
    /** When true, {@link ARBMultiBind} is supported. */
    public final boolean GL_ARB_multi_bind;
    /** When true, {@link ARBMultiDrawIndirect} is supported. */
    public final boolean GL_ARB_multi_draw_indirect;
    /** When true, {@link ARBMultisample} is supported. */
    public final boolean GL_ARB_multisample;
    /** When true, {@link ARBMultitexture} is supported. */
    public final boolean GL_ARB_multitexture;
    /** When true, {@link ARBOcclusionQuery} is supported. */
    public final boolean GL_ARB_occlusion_query;
    /** When true, {@link ARBOcclusionQuery2} is supported. */
    public final boolean GL_ARB_occlusion_query2;
    /** When true, {@link ARBParallelShaderCompile} is supported. */
    public final boolean GL_ARB_parallel_shader_compile;
    /** When true, {@link ARBPipelineStatisticsQuery} is supported. */
    public final boolean GL_ARB_pipeline_statistics_query;
    /** When true, {@link ARBPixelBufferObject} is supported. */
    public final boolean GL_ARB_pixel_buffer_object;
    /** When true, {@link ARBPointParameters} is supported. */
    public final boolean GL_ARB_point_parameters;
    /** When true, {@link ARBPointSprite} is supported. */
    public final boolean GL_ARB_point_sprite;
    /** When true, {@link ARBPolygonOffsetClamp} is supported. */
    public final boolean GL_ARB_polygon_offset_clamp;
    /**
     * When true, the <a target="_blank" href="https://www.khronos.org/registry/OpenGL/extensions/ARB/ARB_post_depth_coverage.txt">ARB_post_depth_coverage</a> extension is supported.
     *
     * <p>This extension allows the fragment shader to control whether values in {@code gl_SampleMaskIn[]} reflect the coverage after application of the early
     * depth and stencil tests. This feature can be enabled with the following layout qualifier in the fragment shader:</p>
     *
     * <pre><code>
     *         layout(post_depth_coverage) in;</code></pre>
     *
     * <p>Use of this feature implicitly enables early fragment tests.</p>
     */
    public final boolean GL_ARB_post_depth_coverage;
    /** When true, {@link ARBProgramInterfaceQuery} is supported. */
    public final boolean GL_ARB_program_interface_query;
    /** When true, {@link ARBProvokingVertex} is supported. */
    public final boolean GL_ARB_provoking_vertex;
    /** When true, {@link ARBQueryBufferObject} is supported. */
    public final boolean GL_ARB_query_buffer_object;
    /**
     * When true, the <a target="_blank" href="https://www.khronos.org/registry/OpenGL/extensions/ARB/ARB_robust_buffer_access_behavior.txt">ARB_robust_buffer_access_behavior</a> extension is supported.
     *
     * <p>This extension specifies the behavior of out-of-bounds buffer and array accesses. This is an improvement over the existing ARB_robustness extension
     * which stated that the application should not crash, but the behavior is otherwise undefined. This extension specifies the access protection provided by
     * the GL to ensure that out-of-bounds accesses cannot read from or write to data not owned by the application. All accesses are contained within the
     * buffer object and program area they reference. These additional robustness guarantees apply to contexts created with the
     * {@code CONTEXT_FLAG_ROBUST_ACCESS_BIT_ARB} feature enabled.</p>
     *
     * <p>Requires {@link ARBRobustness ARB_robustness}. Promoted to core in {@link GL43 OpenGL 4.3}.</p>
     */
    public final boolean GL_ARB_robust_buffer_access_behavior;
    /** When true, {@link ARBRobustness} is supported. */
    public final boolean GL_ARB_robustness;
    /**
     * When true, the <a target="_blank" href="https://www.khronos.org/registry/OpenGL/extensions/ARB/ARB_robustness_application_isolation.txt">ARB_robustness_application_isolation</a> extension is supported.
     *
     * <p>{@link ARBRobustness ARB_robustness} and supporting window system extensions allow creating an OpenGL context supporting graphics reset notification behavior. This
     * extension provides stronger guarantees about the possible side-effects of a graphics reset.</p>
     *
     * <p>It is expected that there may be a performance cost associated with isolating an application or share group from other contexts on the GPU. For this
     * reason, ARB_robustness_isolation is phrased as an opt-in mechanism, with a new context creation bit defined in the window system bindings. It is
     * expected that implementations might only advertise the strings in this extension if both the implementation supports the desired isolation properties,
     * and the context was created with the appropriate reset isolation bit.</p>
     *
     * <p>If the graphics driver advertises the {@code GL_ARB_robustness_application_isolation} extension string, then the driver guarantees that if a particular
     * application causes a graphics reset to occur:</p>
     *
     * <ol>
     * <li>No other application on the system is affected by the graphics reset.</li>
     * <li>No other application on the system receives any notification that the graphics reset occurred.</li>
     * </ol>
     *
     * <p>Requires {@link ARBRobustness ARB_robustness}. Promoted to core in {@link GL43 OpenGL 4.3}.</p>
     */
    public final boolean GL_ARB_robustness_application_isolation;
    /**
     * When true, the <a target="_blank" href="https://www.khronos.org/registry/OpenGL/extensions/ARB/ARB_robustness_application_isolation.txt">ARB_robustness_share_group_isolation</a> extension is supported.
     *
     * <p>See {@link #GL_ARB_robustness_application_isolation ARB_robustness_application_isolation}.</p>
     *
     * <p>If the graphics driver advertises the {@code GL_ARB_robustness_share_group_isolation} extension string, then the driver guarantees that if a context in
     * a particular share group causes a graphics reset to occur:</p>
     *
     * <ol>
     * <li>No other share group within the application is affected by the graphics reset. Additionally, no other application on the system is affected by the
     * graphics reset.</li>
     * <li>No other share group within the application receives any notification that the graphics reset occurred. Additionally, no other application on the
     * system receives any notification that the graphics reset occurred.</li>
     * </ol>
     */
    public final boolean GL_ARB_robustness_share_group_isolation;
    /** When true, {@link ARBSampleLocations} is supported. */
    public final boolean GL_ARB_sample_locations;
    /** When true, {@link ARBSampleShading} is supported. */
    public final boolean GL_ARB_sample_shading;
    /** When true, {@link ARBSamplerObjects} is supported. */
    public final boolean GL_ARB_sampler_objects;
    /** When true, {@link ARBSeamlessCubeMap} is supported. */
    public final boolean GL_ARB_seamless_cube_map;
    /** When true, {@link ARBSeamlessCubemapPerTexture} is supported. */
    public final boolean GL_ARB_seamless_cubemap_per_texture;
    /** When true, {@link ARBSeparateShaderObjects} is supported. */
    public final boolean GL_ARB_separate_shader_objects;
    /**
     * When true, the <a target="_blank" href="https://www.khronos.org/registry/OpenGL/extensions/ARB/ARB_shader_atomic_counter_ops.txt">ARB_shader_atomic_counter_ops</a> extension is supported.
     *
     * <p>The {@link ARBShaderAtomicCounters ARB_shader_atomic_counters} extension introduced atomic counters, but it limits list of potential operations that can be performed on them
     * to increment, decrement, and query. This extension extends the list of GLSL built-in functions that can operate on atomic counters. The list of new
     * operations include:</p>
     *
     * <ul>
     * <li>Addition and subtraction</li>
     * <li>Minimum and maximum</li>
     * <li>Bitwise operators (AND, OR, XOR, etc.)</li>
     * <li>Exchange, and compare and exchange operators</li>
     * </ul>
     *
     * <p>Requires {@link GL42 OpenGL 4.2} or {@link ARBShaderAtomicCounters ARB_shader_atomic_counters}.</p>
     */
    public final boolean GL_ARB_shader_atomic_counter_ops;
    /** When true, {@link ARBShaderAtomicCounters} is supported. */
    public final boolean GL_ARB_shader_atomic_counters;
    /**
     * When true, the <a target="_blank" href="https://www.khronos.org/registry/OpenGL/extensions/ARB/ARB_shader_ballot.txt">ARB_shader_ballot</a> extension is supported.
     *
     * <p>This extension provides the ability for a group of invocations which execute in lockstep to do limited forms of cross-invocation communication via a
     * group broadcast of a invocation value, or broadcast of a bitarray representing a predicate value from each invocation in the group.</p>
     *
     * <p>Requires {@link ARBGPUShaderInt64 ARB_gpu_shader_int64}.</p>
     */
    public final boolean GL_ARB_shader_ballot;
    /**
     * When true, the <a target="_blank" href="https://www.khronos.org/registry/OpenGL/extensions/ARB/ARB_shader_bit_encoding.txt">ARB_shader_bit_encoding</a> extension is supported.
     *
     * <p>This extension trivially adds built-in functions for getting/setting the bit encoding for floating-point values in the OpenGL Shading Language.</p>
     *
     * <p>Promoted to core in {@link GL33 OpenGL 3.3}.</p>
     */
    public final boolean GL_ARB_shader_bit_encoding;
    /**
     * When true, the <a target="_blank" href="https://www.khronos.org/registry/OpenGL/extensions/ARB/ARB_shader_clock.txt">ARB_shader_clock</a> extension is supported.
     *
     * <p>This extension exposes a 64-bit monotonically incrementing shader counter which may be used to derive local timing information within a single shader
     * invocation.</p>
     */
    public final boolean GL_ARB_shader_clock;
    /**
     * When true, the <a target="_blank" href="https://www.khronos.org/registry/OpenGL/extensions/ARB/ARB_shader_draw_parameters.txt">ARB_shader_draw_parameters</a> extension is supported.
     *
     * <p>In unextended GL, vertex shaders have inputs named {@code gl_VertexID} and {@code gl_InstanceID}, which contain, respectively the index of the vertex
     * and instance. The value of {@code gl_VertexID} is the implicitly passed index of the vertex being processed, which includes the value of baseVertex, for
     * those commands that accept it. Meanwhile, {@code gl_InstanceID} is the integer index of the current instance being processed, but, even for commands
     * that accept a baseInstance parameter, it does not include the value of this argument. Furthermore, the equivalents to these variables in other graphics
     * APIs do not necessarily follow these conventions. The reason for this inconsistency is that there are legitimate use cases for both inclusion and
     * exclusion of the baseVertex or baseInstance parameters in {@code gl_VertexID} and {@code gl_InstanceID}, respectively.</p>
     *
     * <p>Rather than change the semantics of either built-in variable, this extension adds two new built-in variables to the GL shading language,
     * {@code gl_BaseVertexARB} and {@code gl_BaseInstanceARB}, which contain the values passed in the baseVertex and baseInstance parameters, respectively.
     * Shaders provided by the application may use these variables to offset {@code gl_VertexID} or {@code gl_InstanceID} if desired, or use them for any other
     * purpose.</p>
     *
     * <p>Additionally, this extension adds a further built-in variable, {@code gl_DrawID} to the shading language. This variable contains the index of the draw
     * currently being processed by a Multi* variant of a drawing command (such as {@link GL14C#glMultiDrawElements MultiDrawElements} or {@link GL43C#glMultiDrawArraysIndirect MultiDrawArraysIndirect}).</p>
     *
     * <p>Requires {@link GL31 OpenGL 3.1}. Promoted to core in {@link GL33 OpenGL 3.3}.</p>
     */
    public final boolean GL_ARB_shader_draw_parameters;
    /**
     * When true, the <a target="_blank" href="https://www.khronos.org/registry/OpenGL/extensions/ARB/ARB_shader_group_vote.txt">ARB_shader_group_vote</a> extension is supported.
     *
     * <p>This extension provides new built-in functions to compute the composite of a set of boolean conditions across a group of shader invocations. These
     * composite results may be used to execute shaders more efficiently on a single-instruction multiple-data (SIMD) processor. The set of shader invocations
     * across which boolean conditions are evaluated is implementation-dependent, and this extension provides no guarantee over how individual shader
     * invocations are assigned to such sets. In particular, the set of shader invocations has no necessary relationship with the compute shader local work
     * group -- a pair of shader invocations in a single compute shader work group may end up in different sets used by these built-ins.</p>
     *
     * <p>Compute shaders operate on an explicitly specified group of threads (a local work group), but many implementations of OpenGL 4.3 will even group
     * non-compute shader invocations and execute them in a SIMD fashion. When executing code like</p>
     *
     * <pre><code>
     * if (condition) {
     *     result = do_fast_path();
     * } else {
     *     result = do_general_path();
     * }</code></pre>
     *
     * <p>where {@code condition} diverges between invocations, a SIMD implementation might first call do_fast_path() for the invocations where {@code condition}
     * is true and leave the other invocations dormant. Once do_fast_path() returns, it might call do_general_path() for invocations where {@code condition} is
     * false and leave the other invocations dormant. In this case, the shader executes *both* the fast and the general path and might be better off just using
     * the general path for all invocations.</p>
     *
     * <p>This extension provides the ability to avoid divergent execution by evaluting a condition across an entire SIMD invocation group using code like:</p>
     *
     * <pre><code>
     * if (allInvocationsARB(condition)) {
     *     result = do_fast_path();
     * } else {
     *     result = do_general_path();
     * }</code></pre>
     *
     * <p>The built-in function allInvocationsARB() will return the same value for all invocations in the group, so the group will either execute do_fast_path()
     * or do_general_path(), but never both. For example, shader code might want to evaluate a complex function iteratively by starting with an approximation
     * of the result and then refining the approximation. Some input values may require a small number of iterations to generate an accurate result
     * (do_fast_path) while others require a larger number (do_general_path). In another example, shader code might want to evaluate a complex function
     * (do_general_path) that can be greatly simplified when assuming a specific value for one of its inputs (do_fast_path).</p>
     *
     * <p>Requires {@link GL43 OpenGL 4.3} or {@link ARBComputeShader ARB_compute_shader}.</p>
     */
    public final boolean GL_ARB_shader_group_vote;
    /** When true, {@link ARBShaderImageLoadStore} is supported. */
    public final boolean GL_ARB_shader_image_load_store;
    /**
     * When true, the <a target="_blank" href="https://www.khronos.org/registry/OpenGL/extensions/ARB/ARB_shader_image_size.txt">ARB_shader_image_size</a> extension is supported.
     *
     * <p>This extension provides GLSL built-in functions allowing shaders to query the size of an image.</p>
     *
     * <p>Requires {@link GL42 OpenGL 4.2} and GLSL 4.20. Promoted to core in {@link GL43 OpenGL 4.3}.</p>
     */
    public final boolean GL_ARB_shader_image_size;
    /** When true, {@link ARBShaderObjects} is supported. */
    public final boolean GL_ARB_shader_objects;
    /**
     * When true, the <a target="_blank" href="https://www.khronos.org/registry/OpenGL/extensions/ARB/ARB_shader_precision.txt">ARB_shader_precision</a> extension is supported.
     *
     * <p>This extension more clearly restricts the precision requirements of implementations of the GLSL specification. These include precision of arithmetic
     * operations (operators '+', '/', ...), transcendentals (log, exp, pow, reciprocal sqrt, ...), when NaNs (not a number) and INFs (infinities) will be
     * supported and generated, and denorm flushing behavior.  Trigonometric built-ins and some other categories of built-ins are not addressed.</p>
     *
     * <p>Requires {@link GL40 OpenGL 4.0}. Promoted to core in {@link GL41 OpenGL 4.1}.</p>
     */
    public final boolean GL_ARB_shader_precision;
    /**
     * When true, the <a target="_blank" href="https://www.khronos.org/registry/OpenGL/extensions/ARB/ARB_shader_stencil_export.txt">ARB_shader_stencil_export</a> extension is supported.
     *
     * <p>In OpenGL, the stencil test is a powerful mechanism to selectively discard fragments based on the content of the stencil buffer. However, facilites to
     * update the content of the stencil buffer are limited to operations such as incrementing the existing value, or overwriting with a fixed reference value.</p>
     *
     * <p>This extension provides a mechanism whereby a shader may generate the stencil reference value per invocation. When stencil testing is enabled, this
     * allows the test to be performed against the value generated in the shader. When the stencil operation is set to {@link GL11#GL_REPLACE REPLACE}, this allows a value generated
     * in the shader to be written to the stencil buffer directly.</p>
     *
     * <p>Requires {@link #GL_ARB_fragment_shader ARB_fragment_shader}.</p>
     */
    public final boolean GL_ARB_shader_stencil_export;
    /** When true, {@link ARBShaderStorageBufferObject} is supported. */
    public final boolean GL_ARB_shader_storage_buffer_object;
    /** When true, {@link ARBShaderSubroutine} is supported. */
    public final boolean GL_ARB_shader_subroutine;
    /**
     * When true, the <a target="_blank" href="https://www.khronos.org/registry/OpenGL/extensions/ARB/ARB_shader_texture_image_samples.txt">ARB_shader_texture_image_samples</a> extension is supported.
     *
     * <p>This extension provides GLSL built-in functions allowing shaders to query the number of samples of a texture.</p>
     *
     * <p>Requires GLSL 1.50 or {@link ARBTextureMultisample ARB_texture_multisample}.</p>
     */
    public final boolean GL_ARB_shader_texture_image_samples;
    /**
     * When true, the <a target="_blank" href="https://www.khronos.org/registry/OpenGL/extensions/ARB/ARB_shader_texture_lod.txt">ARB_shader_texture_lod</a> extension is supported.
     *
     * <p>This extension adds additional texture functions to the OpenGL Shading Language which provide the shader writer with explicit control of LOD.</p>
     *
     * <p>Mipmap texture fetches and anisotropic texture fetches require an implicit derivatives to calculate rho, lambda and/or the line of anisotropy. These
     * implicit derivatives will be undefined for texture fetches occurring inside non-uniform control flow or for vertex shader texture fetches, resulting in
     * undefined texels.</p>
     *
     * <p>The additional texture functions introduced with this extension provide explict control of LOD (isotropic texture functions) or provide explicit
     * derivatives (anisotropic texture functions).</p>
     *
     * <p>Anisotropic texture functions return defined texels for mipmap texture fetches or anisotropic texture fetches, even inside non-uniform control flow.
     * Isotropic texture functions return defined texels for mipmap texture fetches, even inside non-uniform control flow. However, isotropic texture functions
     * return undefined texels for anisotropic texture fetches.</p>
     *
     * <p>The existing isotropic vertex texture functions:</p>
     *
     * <pre><code>
     * texture1DLod,   texture1DProjLod,
     * texture2DLod,   texture2DProjLod,
     * texture3DLod,   texture3DProjLod,
     * textureCubeLod,
     * shadow1DLod,    shadow1DProjLod,
     * shadow2DLod,    shadow2DProjLod</code></pre>
     *
     * <p>are added to the built-in functions for fragment shaders.</p>
     *
     * <p>New anisotropic texture functions, providing explicit derivatives:</p>
     *
     * <pre><code>
     * texture1DGradARB(
     *     sampler1D sampler,
     *     float P, float dPdx, float dPdy);
     * texture1DProjGradARB(
     *     sampler1D sampler,
     *     vec2 P, float dPdx, float dPdy);
     * texture1DProjGradARB(
     *     sampler1D sampler,
     *     vec4 P, float dPdx, float dPdy);
     * texture2DGradARB(
     *     sampler2D sampler,
     *     vec2 P, vec2 dPdx, vec2 dPdy);
     * texture2DProjGradARB(
     *     sampler2D sampler,
     *     vec3 P, vec2 dPdx, vec2 dPdy);
     * texture2DProjGradARB(
     *     sampler2D sampler,
     *     vec4 P, vec2 dPdx, vec2 dPdy);
     * texture3DGradARB(
     *     sampler3D sampler,
     *     vec3 P, vec3 dPdx, vec3 dPdy);
     * texture3DProjGradARB(
     *     sampler3D sampler,
     *     vec4 P, vec3 dPdx, vec3 dPdy);
     * textureCubeGradARB(
     *     samplerCube sampler,
     *     vec3 P, vec3 dPdx, vec3 dPdy);
     *
     * shadow1DGradARB(
     *     sampler1DShadow sampler,
     *     vec3 P, float dPdx, float dPdy);
     * shadow1DProjGradARB(
     *     sampler1DShadow sampler,
     *     vec4 P, float dPdx, float dPdy);
     * shadow2DGradARB(
     *     sampler2DShadow sampler,
     *     vec3 P, vec2 dPdx, vec2 dPdy);
     * shadow2DProjGradARB(
     *     sampler2DShadow sampler,
     *     vec4 P, vec2 dPdx, vec2 dPdy);
     *
     * texture2DRectGradARB(
     *     sampler2DRect sampler,
     *     vec2 P, vec2 dPdx, vec2 dPdy);
     * texture2DRectProjGradARB(
     *     sampler2DRect sampler,
     *     vec3 P, vec2 dPdx, vec2 dPdy);
     * texture2DRectProjGradARB(
     *     sampler2DRect sampler,
     *     vec4 P, vec2 dPdx, vec2 dPdy);
     *
     * shadow2DRectGradARB(
     *     sampler2DRectShadow sampler,
     *     vec3 P, vec2 dPdx, vec2 dPdy);
     * shadow2DRectProjGradARB(
     *     sampler2DRectShadow sampler,
     *     vec4 P, vec2 dPdx, vec2 dPdy);</code></pre>
     *
     * <p>are added to the built-in functions for vertex shaders and fragment shaders.</p>
     *
     * <p>Requires {@link #GL_ARB_shader_objects ARB_shader_objects}. Promoted to core in {@link GL30 OpenGL 3.0}.</p>
     */
    public final boolean GL_ARB_shader_texture_lod;
    /**
     * When true, the <a target="_blank" href="https://www.khronos.org/registry/OpenGL/extensions/ARB/ARB_shader_viewport_layer_array.txt">ARB_shader_viewport_layer_array</a> extension is supported.
     *
     * <p>The gl_ViewportIndex and gl_Layer built-in variables were introduced by the in OpenGL 4.1. These variables are available in un-extended OpenGL only to
     * the geometry shader. When written in the geometry shader, they cause geometry to be directed to one of an array of several independent viewport
     * rectangles or framebuffer attachment layers, respectively.</p>
     *
     * <p>In order to use any viewport or attachment layer other than zero, a geometry shader must be present. Geometry shaders introduce processing overhead and
     * potential performance issues. The AMD_vertex_shader_layer and AMD_vertex_shader_viewport_index extensions allowed the gl_Layer and gl_ViewportIndex
     * outputs to be written directly from the vertex shader with no geometry shader present.</p>
     *
     * <p>This extension effectively merges the AMD_vertex_shader_layer and AMD_vertex_shader_viewport_index extensions together and extends them further to
     * allow both outputs to be written from tessellation evaluation shaders.</p>
     *
     * <p>Requires {@link GL41 OpenGL 4.1}.</p>
     */
    public final boolean GL_ARB_shader_viewport_layer_array;
    /** When true, {@link ARBShadingLanguage100} is supported. */
    public final boolean GL_ARB_shading_language_100;
    /**
     * When true, the <a target="_blank" href="https://www.khronos.org/registry/OpenGL/extensions/ARB/ARB_shading_language_420pack.txt">ARB_shading_language_420pack</a> extension is supported.
     *
     * <p>This is a language feature only extension formed from changes made to version 4.20 of GLSL. It includes:</p>
     *
     * <ul>
     * <li>Add line-continuation using '', as in C++.</li>
     * <li>Change from ASCII to UTF-8 for the language character set and also allow any characters inside comments.</li>
     * <li>Allow implicit conversions of return values to the declared type of the function.</li>
     * <li>The *const* keyword can be used to declare variables within a function body with initializer expressions that are not constant expressions.</li>
     * <li>Qualifiers on variable declarations no longer have to follow a strict order. The layout qualifier can be used multiple times, and multiple parameter
     * qualifiers can be used. However, this is not as straightforward as saying declarations have arbitrary lists of initializers. Typically, one
     * qualifier from each class of qualifiers is allowed, so care is now taken to classify them and say so. Then, of these, order restrictions are removed.</li>
     * <li>Add layout qualifier identifier "binding" to bind the location of a uniform block. This requires version 1.4 of GLSL. If this extension is used with
     * an earlier version than 1.4, this feature is not present.</li>
     * <li>Add layout qualifier identifier "binding" to bind units to sampler and image variable declarations.</li>
     * <li>Add C-style curly brace initializer lists syntax for initializers. Full initialization of aggregates is required when these are used.</li>
     * <li>Allow ".length()" to be applied to vectors and matrices, returning the number of components or columns.</li>
     * <li>Allow swizzle operations on scalars.</li>
     * <li>Built-in constants for {@code gl_MinProgramTexelOffset} and {@code gl_MaxProgramTexelOffset}.</li>
     * </ul>
     *
     * <p>Requires GLSL 1.30. Requires GLSL 1.40 for uniform block bindings. Promoted to core in {@link GL42 OpenGL 4.2}.</p>
     */
    public final boolean GL_ARB_shading_language_420pack;
    /** When true, {@link ARBShadingLanguageInclude} is supported. */
    public final boolean GL_ARB_shading_language_include;
    /**
     * When true, the <a target="_blank" href="https://www.khronos.org/registry/OpenGL/extensions/ARB/ARB_shading_language_packing.txt">ARB_shading_language_packing</a> extension is supported.
     *
     * <p>This extension provides the GLSL built-in functions to convert a 32-bit unsigned integer holding a pair of 16-bit floating-point values to or from a
     * two-component floating-point vector (vec2).</p>
     *
     * <p>This mechanism allows GLSL shaders to read and write 16-bit floating-point encodings (via 32-bit unsigned integers) without introducing a full set of
     * 16-bit floating-point data types.</p>
     *
     * <p>This extension also adds the GLSL built-in packing functions included in GLSL version 4.00 and the ARB_gpu_shader5 extension which pack and unpack
     * vectors of small fixed-point data types into a larger scalar. By putting these packing functions in this separate extension it allows implementations to
     * provide these functions in hardware that supports them independent of the other {@link #GL_ARB_gpu_shader5 ARB_gpu_shader5} features.</p>
     *
     * <p>In addition to the packing functions from ARB_gpu_shader5 this extension also adds the missing {@code [un]packSnorm2x16} for completeness.</p>
     *
     * <p>Promoted to core in {@link GL42 OpenGL 4.2}.</p>
     */
    public final boolean GL_ARB_shading_language_packing;
    /** When true, {@link ARBShadow} is supported. */
    public final boolean GL_ARB_shadow;
    /** When true, {@link ARBShadowAmbient} is supported. */
    public final boolean GL_ARB_shadow_ambient;
    /** When true, {@link ARBSparseBuffer} is supported. */
    public final boolean GL_ARB_sparse_buffer;
    /** When true, {@link ARBSparseTexture} is supported. */
    public final boolean GL_ARB_sparse_texture;
    /**
     * When true, the <a target="_blank" href="https://www.khronos.org/registry/OpenGL/extensions/ARB/ARB_sparse_texture2.txt">ARB_sparse_texture2</a> extension is supported.
     *
     * <p>This extension builds on the {@link ARBSparseTexture ARB_sparse_texture} extension, providing the following new functionality:</p>
     *
     * <ul>
     * <li>New built-in GLSL texture lookup and image load functions are provided that return information on whether the texels accessed for the texture
     * lookup accessed uncommitted texture memory.</li>
     * <li>New built-in GLSL texture lookup functions are provided that specify a minimum level of detail to use for lookups where the level of detail is
     * computed automatically. This allows shaders to avoid accessing unpopulated portions of high-resolution levels of detail when it knows that the
     * memory accessed is unpopulated, either from a priori knowledge or from feedback provided by the return value of previously executed "sparse"
     * texture lookup functions.</li>
     * <li>Reads of uncommitted texture memory will act as though such memory were filled with zeroes; previously, the values returned by reads were
     * undefined.</li>
     * <li>Standard implementation-independent virtual page sizes for internal formats required to be supported with sparse textures. These standard sizes can
     * be requested by leaving {@link ARBSparseTexture#GL_VIRTUAL_PAGE_SIZE_INDEX_ARB VIRTUAL_PAGE_SIZE_INDEX_ARB} at its initial value (0).</li>
     * <li>Support for creating sparse multisample and multisample array textures is added. However, the virtual page sizes for such textures remain fully
     * implementation-dependent.</li>
     * </ul>
     *
     * <p>Requires {@link ARBSparseTexture ARB_sparse_texture}</p>
     */
    public final boolean GL_ARB_sparse_texture2;
    /**
     * When true, the <a target="_blank" href="https://www.khronos.org/registry/OpenGL/extensions/ARB/ARB_sparse_texture_clamp.txt">ARB_sparse_texture_clamp</a> extension is supported.
     *
     * <p>This extension builds on the {@link #GL_ARB_sparse_texture2 ARB_sparse_texture2} extension, providing the following new functionality:</p>
     *
     * <p>New built-in GLSL texture lookup functions are provided that specify a minimum level of detail to use for lookups where the level of detail is
     * computed automatically. This allows shaders to avoid accessing unpopulated portions of high-resolution levels of detail when it knows that the memory
     * accessed is unpopulated, either from a priori knowledge or from feedback provided by the return value of previously executed "sparse" texture lookup
     * functions.</p>
     *
     * <p>Requires {@link #GL_ARB_sparse_texture2 ARB_sparse_texture2}</p>
     */
    public final boolean GL_ARB_sparse_texture_clamp;
    /** When true, {@link ARBSPIRVExtensions} is supported. */
    public final boolean GL_ARB_spirv_extensions;
    /** When true, {@link ARBStencilTexturing} is supported. */
    public final boolean GL_ARB_stencil_texturing;
    /** When true, {@link ARBSync} is supported. */
    public final boolean GL_ARB_sync;
    /** When true, {@link ARBTessellationShader} is supported. */
    public final boolean GL_ARB_tessellation_shader;
    /** When true, {@link ARBTextureBarrier} is supported. */
    public final boolean GL_ARB_texture_barrier;
    /** When true, {@link ARBTextureBorderClamp} is supported. */
    public final boolean GL_ARB_texture_border_clamp;
    /** When true, {@link ARBTextureBufferObject} is supported. */
    public final boolean GL_ARB_texture_buffer_object;
    /**
     * When true, the <a target="_blank" href="https://www.khronos.org/registry/OpenGL/extensions/ARB/ARB_texture_buffer_object_rgb32.txt">ARB_texture_buffer_object_rgb32</a> extension is supported.
     *
     * <p>This extension adds three new buffer texture formats - RGB32F, RGB32I, and RGB32UI. This partially addresses one of the limitations of buffer textures
     * in the original {@link #GL_EXT_texture_buffer_object EXT_texture_buffer_object} extension and in {@link GL31 OpenGL 3.1}, which provide no support for three-component formats.</p>
     *
     * <p>Promoted to core in {@link GL40 OpenGL 4.0}.</p>
     */
    public final boolean GL_ARB_texture_buffer_object_rgb32;
    /** When true, {@link ARBTextureBufferRange} is supported. */
    public final boolean GL_ARB_texture_buffer_range;
    /** When true, {@link ARBTextureCompression} is supported. */
    public final boolean GL_ARB_texture_compression;
    /** When true, {@link ARBTextureCompressionBPTC} is supported. */
    public final boolean GL_ARB_texture_compression_bptc;
    /** When true, {@link ARBTextureCompressionRGTC} is supported. */
    public final boolean GL_ARB_texture_compression_rgtc;
    /** When true, {@link ARBTextureCubeMap} is supported. */
    public final boolean GL_ARB_texture_cube_map;
    /** When true, {@link ARBTextureCubeMapArray} is supported. */
    public final boolean GL_ARB_texture_cube_map_array;
    /**
     * When true, the <a target="_blank" href="https://www.khronos.org/registry/OpenGL/extensions/ARB/ARB_texture_env_add.txt">ARB_texture_env_add</a> extension is supported.
     *
     * <p>This extension adds a new texture environment function: ADD.</p>
     *
     * <p>Promoted to core in {@link GL13 OpenGL 1.3}.</p>
     */
    public final boolean GL_ARB_texture_env_add;
    /** When true, {@link ARBTextureEnvCombine} is supported. */
    public final boolean GL_ARB_texture_env_combine;
    /**
     * When true, the <a target="_blank" href="https://www.khronos.org/registry/OpenGL/extensions/ARB/ARB_texture_env_crossbar.txt">ARB_texture_env_crossbar</a> extension is supported.
     *
     * <p>This extension adds the capability to use the texture color from other texture units as sources to the {@link ARBTextureEnvCombine#GL_COMBINE_ARB COMBINE_ARB} environment
     * function. The {@link ARBTextureEnvCombine ARB_texture_env_combine} extension defined texture environment functions which could use the color from the current texture unit
     * as a source. This extension adds the ability to use the color from any texture unit as a source.</p>
     *
     * <p>Requires {@link #GL_ARB_multitexture ARB_multitexture} and {@link ARBTextureEnvCombine ARB_texture_env_combine}. Promoted to core in {@link GL14 OpenGL 1.4}.</p>
     */
    public final boolean GL_ARB_texture_env_crossbar;
    /** When true, {@link ARBTextureEnvDot3} is supported. */
    public final boolean GL_ARB_texture_env_dot3;
    /** When true, {@link ARBTextureFilterAnisotropic} is supported. */
    public final boolean GL_ARB_texture_filter_anisotropic;
    /** When true, {@link ARBTextureFilterMinmax} is supported. */
    public final boolean GL_ARB_texture_filter_minmax;
    /** When true, {@link ARBTextureFloat} is supported. */
    public final boolean GL_ARB_texture_float;
    /** When true, {@link ARBTextureGather} is supported. */
    public final boolean GL_ARB_texture_gather;
    /** When true, {@link ARBTextureMirrorClampToEdge} is supported. */
    public final boolean GL_ARB_texture_mirror_clamp_to_edge;
    /** When true, {@link ARBTextureMirroredRepeat} is supported. */
    public final boolean GL_ARB_texture_mirrored_repeat;
    /** When true, {@link ARBTextureMultisample} is supported. */
    public final boolean GL_ARB_texture_multisample;
    /**
     * When true, the <a target="_blank" href="https://www.khronos.org/registry/OpenGL/extensions/ARB/ARB_texture_non_power_of_two.txt">ARB_texture_non_power_of_two</a> extension is supported.
     *
     * <p>Conventional OpenGL texturing is limited to images with power-of-two dimensions and an optional 1-texel border. This extension relaxes the size
     * restrictions for the 1D, 2D, cube map, and 3D texture targets.</p>
     *
     * <p>Promoted to core in {@link GL20 OpenGL 2.0}.</p>
     */
    public final boolean GL_ARB_texture_non_power_of_two;
    /**
     * When true, the <a target="_blank" href="https://www.khronos.org/registry/OpenGL/extensions/ARB/ARB_texture_query_levels.txt">ARB_texture_query_levels</a> extension is supported.
     *
     * <p>This extension provides a new set of texture functions ({@code textureQueryLevels}) in the OpenGL Shading Language that exposes the number of accessible
     * mipmap levels in the texture associated with a GLSL sampler variable. The set of accessible levels includes all the levels of the texture defined either
     * through TexImage*, TexStorage*, or TextureView* ({@link ARBTextureView ARB_texture_view}) APIs that are not below the {@link GL12#GL_TEXTURE_BASE_LEVEL TEXTURE_BASE_LEVEL} or above the
     * {@link GL12#GL_TEXTURE_MAX_LEVEL TEXTURE_MAX_LEVEL} parameters. For textures defined with TexImage*, the set of resident levels is somewhat implementation-dependent. For fully
     * defined results, applications should use TexStorage*&#47;TextureView unless the texture has a full mipmap chain and is used with a mipmapped minification
     * filter.</p>
     *
     * <p>These functions means that shaders are not required to manually recompute, approximate, or maintain a uniform holding a pre-computed level count, since
     * the true level count is already available to the implementation. This value can be used to avoid black or leaking pixel artifacts for rendering methods
     * which are using texture images as memory pages (eg: virtual textures); methods that can't only rely on the fixed pipeline texture functions which take
     * advantage of {@link GL12#GL_TEXTURE_MAX_LEVEL TEXTURE_MAX_LEVEL} for their sampling.</p>
     *
     * <p>Requires {@link GL30 OpenGL 3.0} and GLSL 1.30. Promoted to core in {@link GL43 OpenGL 4.3}.</p>
     */
    public final boolean GL_ARB_texture_query_levels;
    /**
     * When true, the <a target="_blank" href="https://www.khronos.org/registry/OpenGL/extensions/ARB/ARB_texture_query_lod.txt">ARB_texture_query_lod</a> extension is supported.
     *
     * <p>This extension provides a new set of fragment shader texture functions ({@code textureLOD}) that return the results of automatic level-of-detail
     * computations that would be performed if a texture lookup were performed.</p>
     *
     * <p>Requires {@link GL20 OpenGL 2.0}, {@link #GL_EXT_gpu_shader4 EXT_gpu_shader4}, {@link #GL_EXT_texture_array EXT_texture_array} and GLSL 1.30. Promoted to core in {@link GL40 OpenGL 4.0}.</p>
     */
    public final boolean GL_ARB_texture_query_lod;
    /** When true, {@link ARBTextureRectangle} is supported. */
    public final boolean GL_ARB_texture_rectangle;
    /** When true, {@link ARBTextureRG} is supported. */
    public final boolean GL_ARB_texture_rg;
    /** When true, {@link ARBTextureRGB10_A2UI} is supported. */
    public final boolean GL_ARB_texture_rgb10_a2ui;
    /**
     * When true, the <a target="_blank" href="https://www.khronos.org/registry/OpenGL/extensions/ARB/ARB_texture_stencil8.txt">ARB_texture_stencil8</a> extension is supported.
     *
     * <p>This extension accepts {@link GL30#GL_STENCIL_INDEX8 STENCIL_INDEX8} as a texture internal format, and adds STENCIL_INDEX8 to the required internal format list. This removes the
     * need to use renderbuffers if a stencil-only format is desired.</p>
     *
     * <p>Promoted to core in {@link GL44 OpenGL 4.4}.</p>
     */
    public final boolean GL_ARB_texture_stencil8;
    /** When true, {@link ARBTextureStorage} is supported. */
    public final boolean GL_ARB_texture_storage;
    /** When true, {@link ARBTextureStorageMultisample} is supported. */
    public final boolean GL_ARB_texture_storage_multisample;
    /** When true, {@link ARBTextureSwizzle} is supported. */
    public final boolean GL_ARB_texture_swizzle;
    /** When true, {@link ARBTextureView} is supported. */
    public final boolean GL_ARB_texture_view;
    /** When true, {@link ARBTimerQuery} is supported. */
    public final boolean GL_ARB_timer_query;
    /** When true, {@link ARBTransformFeedback2} is supported. */
    public final boolean GL_ARB_transform_feedback2;
    /** When true, {@link ARBTransformFeedback3} is supported. */
    public final boolean GL_ARB_transform_feedback3;
    /** When true, {@link ARBTransformFeedbackInstanced} is supported. */
    public final boolean GL_ARB_transform_feedback_instanced;
    /** When true, {@link ARBTransformFeedbackOverflowQuery} is supported. */
    public final boolean GL_ARB_transform_feedback_overflow_query;
    /** When true, {@link ARBTransposeMatrix} is supported. */
    public final boolean GL_ARB_transpose_matrix;
    /** When true, {@link ARBUniformBufferObject} is supported. */
    public final boolean GL_ARB_uniform_buffer_object;
    /** When true, {@link ARBVertexArrayBGRA} is supported. */
    public final boolean GL_ARB_vertex_array_bgra;
    /** When true, {@link ARBVertexArrayObject} is supported. */
    public final boolean GL_ARB_vertex_array_object;
    /** When true, {@link ARBVertexAttrib64Bit} is supported. */
    public final boolean GL_ARB_vertex_attrib_64bit;
    /** When true, {@link ARBVertexAttribBinding} is supported. */
    public final boolean GL_ARB_vertex_attrib_binding;
    /** When true, {@link ARBVertexBlend} is supported. */
    public final boolean GL_ARB_vertex_blend;
    /** When true, {@link ARBVertexBufferObject} is supported. */
    public final boolean GL_ARB_vertex_buffer_object;
    /** When true, {@link ARBVertexProgram} is supported. */
    public final boolean GL_ARB_vertex_program;
    /** When true, {@link ARBVertexShader} is supported. */
    public final boolean GL_ARB_vertex_shader;
    /**
     * When true, the <a target="_blank" href="https://www.khronos.org/registry/OpenGL/extensions/ARB/ARB_vertex_type_10f_11f_11f_rev.txt">ARB_vertex_type_10f_11f_11f_rev</a> extension is supported.
     *
     * <p>This extension a new vertex attribute data format: a packed 11.11.10 unsigned float vertex data format. This vertex data format can be used to describe
     * a compressed 3 component stream of values that can be represented by 10- or 11-bit unsigned floating point values.</p>
     *
     * <p>The {@link GL30#GL_UNSIGNED_INT_10F_11F_11F_REV UNSIGNED_INT_10F_11F_11F_REV} vertex attribute type is equivalent to the {@link GL30#GL_R11F_G11F_B10F R11F_G11F_B10F} texture internal format.</p>
     *
     * <p>Requires {@link GL30 OpenGL 3.0} and {@link ARBVertexType2_10_10_10_REV ARB_vertex_type_2_10_10_10_rev}. Promoted to core in {@link GL44 OpenGL 4.4}.</p>
     */
    public final boolean GL_ARB_vertex_type_10f_11f_11f_rev;
    /** When true, {@link ARBVertexType2_10_10_10_REV} is supported. */
    public final boolean GL_ARB_vertex_type_2_10_10_10_rev;
    /** When true, {@link ARBViewportArray} is supported. */
    public final boolean GL_ARB_viewport_array;
    /** When true, {@link ARBWindowPos} is supported. */
    public final boolean GL_ARB_window_pos;
    /** When true, {@link ATIMeminfo} is supported. */
    public final boolean GL_ATI_meminfo;
    /** When true, the <a target="_blank" href="https://www.khronos.org/registry/OpenGL/extensions/ATI/ATI_shader_texture_lod.txt">ATI_shader_texture_lod</a> extension is supported. */
    public final boolean GL_ATI_shader_texture_lod;
    /** When true, {@link ATITextureCompression3DC} is supported. */
    public final boolean GL_ATI_texture_compression_3dc;
    /** When true, {@link EXT422Pixels} is supported. */
    public final boolean GL_EXT_422_pixels;
    /** When true, {@link EXTABGR} is supported. */
    public final boolean GL_EXT_abgr;
    /** When true, {@link EXTBGRA} is supported. */
    public final boolean GL_EXT_bgra;
    /** When true, {@link EXTBindableUniform} is supported. */
    public final boolean GL_EXT_bindable_uniform;
    /** When true, {@link EXTBlendColor} is supported. */
    public final boolean GL_EXT_blend_color;
    /** When true, {@link EXTBlendEquationSeparate} is supported. */
    public final boolean GL_EXT_blend_equation_separate;
    /** When true, {@link EXTBlendFuncSeparate} is supported. */
    public final boolean GL_EXT_blend_func_separate;
    /** When true, {@link EXTBlendMinmax} is supported. */
    public final boolean GL_EXT_blend_minmax;
    /** When true, {@link EXTBlendSubtract} is supported. */
    public final boolean GL_EXT_blend_subtract;
    /** When true, {@link EXTClipVolumeHint} is supported. */
    public final boolean GL_EXT_clip_volume_hint;
    /** When true, {@link EXTCompiledVertexArray} is supported. */
    public final boolean GL_EXT_compiled_vertex_array;
    /** When true, {@link EXTDebugLabel} is supported. */
    public final boolean GL_EXT_debug_label;
    /** When true, {@link EXTDebugMarker} is supported. */
    public final boolean GL_EXT_debug_marker;
    /** When true, {@link EXTDepthBoundsTest} is supported. */
    public final boolean GL_EXT_depth_bounds_test;
    /** When true, {@link EXTDirectStateAccess} is supported. */
    public final boolean GL_EXT_direct_state_access;
    /** When true, {@link EXTDrawBuffers2} is supported. */
    public final boolean GL_EXT_draw_buffers2;
    /** When true, {@link EXTDrawInstanced} is supported. */
    public final boolean GL_EXT_draw_instanced;
    /** When true, {@link EXTEGLImageStorage} is supported. */
    public final boolean GL_EXT_EGL_image_storage;
    /**
     * When true, the <a target="_blank" href="https://www.khronos.org/registry/OpenGL/extensions/EXT/EXT_EGL_sync.txt">EXT_EGL_sync</a> extension is supported.
     *
     * <p>This extension extends {@code EGL_KHR_fence_sync} with client API support for OpenGL (compatibility or core profiles) as an EXT extension.</p>
     *
     * <p>The {@code "GL_EXT_EGL_sync"} string indicates that a fence sync object can be created in association with a fence command placed in the command stream
     * of a bound OpenGL context.</p>
     */
    public final boolean GL_EXT_EGL_sync;
    /** When true, {@link EXTExternalBuffer} is supported. */
    public final boolean GL_EXT_external_buffer;
    /** When true, {@link EXTFramebufferBlit} is supported. */
    public final boolean GL_EXT_framebuffer_blit;
    /** When true, {@link EXTFramebufferMultisample} is supported. */
    public final boolean GL_EXT_framebuffer_multisample;
    /** When true, {@link EXTFramebufferMultisampleBlitScaled} is supported. */
    public final boolean GL_EXT_framebuffer_multisample_blit_scaled;
    /** When true, {@link EXTFramebufferObject} is supported. */
    public final boolean GL_EXT_framebuffer_object;
    /** When true, {@link EXTFramebufferSRGB} is supported. */
    public final boolean GL_EXT_framebuffer_sRGB;
    /** When true, {@link EXTGeometryShader4} is supported. */
    public final boolean GL_EXT_geometry_shader4;
    /** When true, {@link EXTGPUProgramParameters} is supported. */
    public final boolean GL_EXT_gpu_program_parameters;
    /** When true, {@link EXTGPUShader4} is supported. */
    public final boolean GL_EXT_gpu_shader4;
    /** When true, {@link EXTMemoryObject} is supported. */
    public final boolean GL_EXT_memory_object;
    /** When true, {@link EXTMemoryObjectFD} is supported. */
    public final boolean GL_EXT_memory_object_fd;
    /** When true, {@link EXTMemoryObjectWin32} is supported. */
    public final boolean GL_EXT_memory_object_win32;
    /**
     * When true, the <a target="_blank" href="https://www.khronos.org/registry/OpenGL/extensions/EXT/EXT_multiview_tessellation_geometry_shader.txt">EXT_multiview_tessellation_geometry_shader</a> extension is supported.
     *
     * <p>This extension removes one of the limitations of the {@code OVR_multiview} extension by allowing the use of tessellation control, tessellation
     * evaluation, and geometry shaders during multiview rendering. {@code OVR_multiview} by itself forbids the use of any of these shader types.</p>
     *
     * <p>When using tessellation control, tessellation evaluation, and geometry shaders during multiview rendering, any such shader must use the
     * "{@code num_views}" layout qualifier provided by the matching shading language extension to specify a view count. The view count specified in these
     * shaders must match the count specified in the vertex shader. Additionally, the shading language extension allows these shaders to use the
     * {@code gl_ViewID_OVR} built-in to handle tessellation or geometry shader processing differently for each view.</p>
     *
     * <p>{@code OVR_multiview2} extends {@code OVR_multiview} by allowing view-dependent values for any vertex attributes instead of just the position. This new
     * extension does not imply the availability of {@code OVR_multiview2}, but if both are available, view-dependent values for any vertex attributes are
     * also allowed in tessellation control, tessellation evaluation, and geometry shaders.</p>
     *
     * <p>Requires {@link GL40 OpenGL 4.0} and {@link OVRMultiview OVR_multiview}.</p>
     */
    public final boolean GL_EXT_multiview_tessellation_geometry_shader;
    /**
     * When true, the <a target="_blank" href="https://www.khronos.org/registry/OpenGL/extensions/EXT/EXT_multiview_texture_multisample.txt">EXT_multiview_texture_multisample</a> extension is supported.
     *
     * <p>This extension removes one of the limitations of the {@code OVR_multiview} extension by allowing the use of multisample textures during multiview
     * rendering.</p>
     *
     * <p>This is one of two extensions that allow multisampling when using {@code OVR_multiview}. Each supports one of the two different approaches to
     * multisampling in OpenGL:</p>
     *
     * <p>Core OpenGL has explicit support for multisample texture types, such as {@link GL32#GL_TEXTURE_2D_MULTISAMPLE TEXTURE_2D_MULTISAMPLE}. Applications can access the values of individual
     * samples and can explicitly "resolve" the samples of each pixel down to a single color.</p>
     *
     * <p>The extension {@code EXT_multisampled_render_to_texture} provides support for multisampled rendering to non-multisample texture types, such as
     * {@link GL11#GL_TEXTURE_2D TEXTURE_2D}. The individual samples for each pixel are maintained internally by the implementation and can not be accessed directly by applications.
     * These samples are eventually resolved implicitly to a single color for each pixel.</p>
     *
     * <p>This extension supports the first multisampling style with multiview rendering; the {@code OVR_multiview_multisampled_render_to_texture} extension
     * supports the second style. Note that support for one of these multiview extensions does not imply support for the other.</p>
     *
     * <p>Requires {@link GL40 OpenGL 4.0} and {@link OVRMultiview OVR_multiview}.</p>
     */
    public final boolean GL_EXT_multiview_texture_multisample;
    /**
     * When true, the <a target="_blank" href="https://www.khronos.org/registry/OpenGL/extensions/EXT/EXT_multiview_timer_query.txt">EXT_multiview_timer_query</a> extension is supported.
     *
     * <p>This extension removes one of the limitations of the {@code OVR_multiview} extension by allowing the use of timer queries during multiview rendering.
     * {@code OVR_multiview} does not specify defined behavior for such usage.</p>
     *
     * <p>Requires {@link GL40 OpenGL 4.0} and {@link OVRMultiview OVR_multiview}.</p>
     */
    public final boolean GL_EXT_multiview_timer_query;
    /** When true, {@link EXTPackedDepthStencil} is supported. */
    public final boolean GL_EXT_packed_depth_stencil;
    /** When true, {@link EXTPackedFloat} is supported. */
    public final boolean GL_EXT_packed_float;
    /** When true, {@link EXTPixelBufferObject} is supported. */
    public final boolean GL_EXT_pixel_buffer_object;
    /** When true, {@link EXTPointParameters} is supported. */
    public final boolean GL_EXT_point_parameters;
    /** When true, {@link EXTPolygonOffsetClamp} is supported. */
    public final boolean GL_EXT_polygon_offset_clamp;
    /**
     * When true, the <a target="_blank" href="https://www.khronos.org/registry/OpenGL/extensions/EXT/EXT_post_depth_coverage.txt">EXT_post_depth_coverage</a> extension is supported.
     *
     * <p>This extension allows the fragment shader to control whether values in {@code gl_SampleMaskIn[]} reflect the coverage after application of the early
     * depth and stencil tests.  This feature can be enabled with the following layout qualifier in the fragment shader:</p>
     *
     * <pre><code>
     * layout(post_depth_coverage) in;</code></pre>
     *
     * <p>To use this feature, early fragment tests must also be enabled in the fragment shader via:</p>
     *
     * <pre><code>
     * layout(early_fragment_tests) in;</code></pre>
     */
    public final boolean GL_EXT_post_depth_coverage;
    /** When true, {@link EXTProvokingVertex} is supported. */
    public final boolean GL_EXT_provoking_vertex;
    /** When true, {@link EXTRasterMultisample} is supported. */
    public final boolean GL_EXT_raster_multisample;
    /** When true, {@link EXTSecondaryColor} is supported. */
    public final boolean GL_EXT_secondary_color;
    /** When true, {@link EXTSemaphore} is supported. */
    public final boolean GL_EXT_semaphore;
    /** When true, {@link EXTSemaphoreFD} is supported. */
    public final boolean GL_EXT_semaphore_fd;
    /** When true, {@link EXTSemaphoreWin32} is supported. */
    public final boolean GL_EXT_semaphore_win32;
    /** When true, {@link EXTSeparateShaderObjects} is supported. */
    public final boolean GL_EXT_separate_shader_objects;
    /** When true, {@link EXTShaderFramebufferFetch} is supported. */
    public final boolean GL_EXT_shader_framebuffer_fetch;
    /** When true, {@link EXTShaderFramebufferFetchNonCoherent} is supported. */
    public final boolean GL_EXT_shader_framebuffer_fetch_non_coherent;
    /**
     * When true, the <a target="_blank" href="https://www.khronos.org/registry/OpenGL/extensions/EXT/EXT_shader_image_load_formatted.txt">EXT_shader_image_load_formatted</a> extension is supported.
     *
     * <p>{@link ARBShaderImageLoadStore ARB_shader_image_load_store} (and OpenGL 4.2) added support for random access load and store from/to texture images, but due to hardware
     * limitations, loads were required to declare the image format in the shader source. This extension relaxes that requirement, and the return values from
     * {@code imageLoad} can be format-converted based on the format of the image binding.</p>
     */
    public final boolean GL_EXT_shader_image_load_formatted;
    /** When true, {@link EXTShaderImageLoadStore} is supported. */
    public final boolean GL_EXT_shader_image_load_store;
    /**
     * When true, the <a target="_blank" href="https://www.khronos.org/registry/OpenGL/extensions/EXT/EXT_shader_integer_mix.txt">EXT_shader_integer_mix</a> extension is supported.
     *
     * <p>GLSL 1.30 (and GLSL ES 3.00) expanded the mix() built-in function to operate on a boolean third argument that does not interpolate but selects. This
     * extension extends mix() to select between int, uint, and bool components.</p>
     *
     * <p>Requires {@link GL30 OpenGL 3.0}.</p>
     */
    public final boolean GL_EXT_shader_integer_mix;
    /**
     * When true, the <a target="_blank" href="https://www.khronos.org/registry/OpenGL/extensions/EXT/EXT_shadow_funcs.txt">EXT_shadow_funcs</a> extension is supported.
     *
     * <p>This extension generalizes the {@link #GL_ARB_shadow ARB_shadow} extension to support all eight binary texture comparison functions rather than just {@link GL11#GL_LEQUAL LEQUAL} and
     * {@link GL11#GL_GEQUAL GEQUAL}.</p>
     *
     * <p>Requires {@link #GL_ARB_depth_texture ARB_depth_texture} and {@link #GL_ARB_shadow ARB_shadow}.</p>
     */
    public final boolean GL_EXT_shadow_funcs;
    /** When true, {@link EXTSharedTexturePalette} is supported. */
    public final boolean GL_EXT_shared_texture_palette;
    /**
     * When true, the <a target="_blank" href="https://www.khronos.org/registry/OpenGL/extensions/EXT/EXT_sparse_texture2.txt">EXT_sparse_texture2</a> extension is supported.
     *
     * <p>This extension builds on the {@link ARBSparseTexture ARB_sparse_texture} extension, providing the following new functionality:</p>
     *
     * <ul>
     * <li>New built-in GLSL texture lookup and image load functions are provided that return information on whether the texels accessed for the texture
     * lookup accessed uncommitted texture memory.
     *
     * <p>New built-in GLSL texture lookup functions are provided that specify a minimum level of detail to use for lookups where the level of detail is
     * computed automatically. This allows shaders to avoid accessing unpopulated portions of high-resolution levels of detail when it knows that the
     * memory accessed is unpopulated, either from a priori knowledge or from feedback provided by the return value of previously executed "sparse"
     * texture lookup functions.</p>
     *
     * <p>Reads of uncommitted texture memory will act as though such memory were filled with zeroes; previously, the values returned by reads were undefined.</p>
     *
     * <p>Standard implementation-independent virtual page sizes for internal formats required to be supported with sparse textures. These standard sizes can
     * be requested by leaving {@link ARBSparseTexture#GL_VIRTUAL_PAGE_SIZE_INDEX_ARB VIRTUAL_PAGE_SIZE_INDEX_ARB} at its initial value (0).</p>
     *
     * <p>Support for creating sparse multisample and multisample array textures is added. However, the virtual page sizes for such textures remain fully
     * implementation-dependent.</p></li>
     * </ul>
     *
     * <p>Requires {@link ARBSparseTexture ARB_sparse_texture}.</p>
     */
    public final boolean GL_EXT_sparse_texture2;
    /** When true, {@link EXTStencilClearTag} is supported. */
    public final boolean GL_EXT_stencil_clear_tag;
    /** When true, {@link EXTStencilTwoSide} is supported. */
    public final boolean GL_EXT_stencil_two_side;
    /** When true, {@link EXTStencilWrap} is supported. */
    public final boolean GL_EXT_stencil_wrap;
    /** When true, {@link EXTTextureArray} is supported. */
    public final boolean GL_EXT_texture_array;
    /** When true, {@link EXTTextureBufferObject} is supported. */
    public final boolean GL_EXT_texture_buffer_object;
    /** When true, {@link EXTTextureCompressionLATC} is supported. */
    public final boolean GL_EXT_texture_compression_latc;
    /** When true, {@link EXTTextureCompressionRGTC} is supported. */
    public final boolean GL_EXT_texture_compression_rgtc;
    /** When true, {@link EXTTextureCompressionS3TC} is supported. */
    public final boolean GL_EXT_texture_compression_s3tc;
    /** When true, {@link EXTTextureFilterAnisotropic} is supported. */
    public final boolean GL_EXT_texture_filter_anisotropic;
    /** When true, {@link EXTTextureFilterMinmax} is supported. */
    public final boolean GL_EXT_texture_filter_minmax;
    /** When true, {@link EXTTextureInteger} is supported. */
    public final boolean GL_EXT_texture_integer;
    /** When true, {@link EXTTextureMirrorClamp} is supported. */
    public final boolean GL_EXT_texture_mirror_clamp;
    /**
     * This extension adds support for various shadow sampler types with texture functions having interactions with the LOD of texture lookups.
     *
     * <p>Modern shading languages support LOD queries for shadow sampler types, but until now the OpenGL Shading Language Specification has excluded multiple
     * texture function overloads involving LOD calculations with various shadow samplers. Shading languages for other APIs do support the equivalent
     * LOD-based texture sampling functions for these types which has made porting between those shading languages to GLSL cumbersome and has required the
     * usage of sub-optimal workarounds.</p>
     *
     * <p>Requires {@link GL20 OpenGL 2.0} and {@link EXTGPUShader4 EXT_gpu_shader4} or equivalent functionality.</p>
     */
    public final boolean GL_EXT_texture_shadow_lod;
    /** When true, {@link EXTTextureSharedExponent} is supported. */
    public final boolean GL_EXT_texture_shared_exponent;
    /** When true, {@link EXTTextureSnorm} is supported. */
    public final boolean GL_EXT_texture_snorm;
    /** When true, {@link EXTTextureSRGB} is supported. */
    public final boolean GL_EXT_texture_sRGB;
    /** When true, {@link EXTTextureSRGBDecode} is supported. */
    public final boolean GL_EXT_texture_sRGB_decode;
    /** When true, {@link EXTTextureSRGBR8} is supported. */
    public final boolean GL_EXT_texture_sRGB_R8;
    /** When true, {@link EXTTextureSRGBRG8} is supported. */
    public final boolean GL_EXT_texture_sRGB_RG8;
    /** When true, {@link EXTTextureStorage} is supported. */
    public final boolean GL_EXT_texture_storage;
    /** When true, {@link EXTTextureSwizzle} is supported. */
    public final boolean GL_EXT_texture_swizzle;
    /** When true, {@link EXTTimerQuery} is supported. */
    public final boolean GL_EXT_timer_query;
    /** When true, {@link EXTTransformFeedback} is supported. */
    public final boolean GL_EXT_transform_feedback;
    /**
     * When true, the <a target="_blank" href="https://www.khronos.org/registry/OpenGL/extensions/EXT/EXT_vertex_array_bgra.txt">EXT_vertex_array_bgra</a> extension is supported.
     *
     * <p>This extension provides a single new component format for vertex arrays to read 4-component unsigned byte vertex attributes with a BGRA component
     * ordering.</p>
     *
     * <p>OpenGL expects vertex arrays containing 4 unsigned bytes per element to be in the RGBA, STRQ, or XYZW order (reading components left-to-right in their
     * lower address to higher address order). Essentially the order the components appear in memory is the order the components appear in the resulting
     * vertex attribute vector.</p>
     *
     * <p>However Direct3D has color (diffuse and specular) vertex arrays containing 4 unsigned bytes per element that are in a BGRA order (again reading
     * components left-to-right in their lower address to higher address order). Direct3D calls this "ARGB" reading the components in the opposite order
     * (reading components left-to-right in their higher address to lower address order). This ordering is generalized in the DirectX 10 by the
     * DXGI_FORMAT_B8G8R8A8_UNORM format.</p>
     *
     * <p>For an OpenGL application to source color data from a vertex buffer formatted for Direct3D's color array format conventions, the application is forced
     * to either:</p>
     *
     * <ol>
     * <li>Rely on a vertex program or shader to swizzle the color components from the BGRA to conventional RGBA order.</li>
     * <li>Re-order the color data components in the vertex buffer from Direct3D's native BGRA order to OpenGL's native RGBA order.</li>
     * </ol>
     *
     * <p>Neither option is entirely satisfactory.</p>
     *
     * <p>Option 1 means vertex shaders have to be re-written to source colors differently. If the same vertex shader is used with vertex arrays configured to
     * source the color as 4 floating-point color components, the swizzle for BGRA colors stored as 4 unsigned bytes is no longer appropriate. The shader's
     * swizzling of colors becomes dependent on the type and number of color components. Ideally the vertex shader should be independent from the format and
     * component ordering of the data it sources.</p>
     *
     * <p>Option 2 is expensive because vertex buffers may have to be reformatted prior to use. OpenGL treats the memory for vertex arrays (whether client-side
     * memory or buffer objects) as essentially untyped memory and vertex arrays can be stored separately, interleaved, or even interwoven (where multiple
     * arrays overlap with differing strides and formats).</p>
     *
     * <p>Rather than force a re-ordering of either vertex array components in memory or a vertex array format-dependent re-ordering of vertex shader inputs,
     * OpenGL can simply provide a vertex array format that matches the Direct3D color component ordering.</p>
     *
     * <p>This approach mimics that of the EXT_bgra extension for pixel and texel formats except for vertex instead of image data.</p>
     */
    public final boolean GL_EXT_vertex_array_bgra;
    /** When true, {@link EXTVertexAttrib64bit} is supported. */
    public final boolean GL_EXT_vertex_attrib_64bit;
    /** When true, {@link EXTWin32KeyedMutex} is supported. */
    public final boolean GL_EXT_win32_keyed_mutex;
    /** When true, {@link EXTWindowRectangles} is supported. */
    public final boolean GL_EXT_window_rectangles;
    /** When true, {@link EXTX11SyncObject} is supported. */
    public final boolean GL_EXT_x11_sync_object;
    /** When true, {@link GREMEDYFrameTerminator} is supported. */
    public final boolean GL_GREMEDY_frame_terminator;
    /** When true, {@link GREMEDYStringMarker} is supported. */
    public final boolean GL_GREMEDY_string_marker;
    /** When true, {@link INTELBlackholeRender} is supported. */
    public final boolean GL_INTEL_blackhole_render;
    /** When true, {@link INTELConservativeRasterization} is supported. */
    public final boolean GL_INTEL_conservative_rasterization;
    /**
     * When true, the <a target="_blank" href="https://www.khronos.org/registry/OpenGL/extensions/INTEL/INTEL_fragment_shader_ordering.txt">INTEL_fragment_shader_ordering</a> extension is supported.
     *
     * <p>Graphics devices may execute in parallel fragment shaders referring to the same window xy coordinates. Framebuffer writes are guaranteed to be
     * processed in primitive rasterization order, but there is no order guarantee for other instructions and image or buffer object accesses in particular.</p>
     *
     * <p>The extension introduces a new GLSL built-in function, beginFragmentShaderOrderingINTEL(), which blocks execution of a fragment shader invocation until
     * invocations from previous primitives that map to the same xy window coordinates (and same sample when per-sample shading is active) complete their
     * execution. All memory transactions from previous fragment shader invocations are made visible to the fragment shader invocation that called
     * beginFragmentShaderOrderingINTEL() when the function returns.</p>
     */
    public final boolean GL_INTEL_fragment_shader_ordering;
    /** When true, {@link INTELFramebufferCMAA} is supported. */
    public final boolean GL_INTEL_framebuffer_CMAA;
    /** When true, {@link INTELMapTexture} is supported. */
    public final boolean GL_INTEL_map_texture;
    /** When true, {@link INTELPerformanceQuery} is supported. */
    public final boolean GL_INTEL_performance_query;
    /**
     * When true, the <a target="_blank" href="https://www.khronos.org/registry/OpenGL/extensions/INTEL/INTEL_shader_integer_functions2.txt">INTEL_shader_integer_functions2</a> extension is supported.
     *
     * <p>OpenCL and other GPU programming environments provides a number of useful functions operating on integer data. Many of these functions are supported by
     * specialized instructions various GPUs. Correct GLSL implementations for some of these functions are non-trivial. Recognizing open-coded versions of
     * these functions is often impractical. As a result, potential performance improvements go unrealized.</p>
     *
     * <p>This extension makes available a number of functions that have specialized instruction support on Intel GPUs.</p>
     *
     * <p>Requires GLSL 1.30 or EXT_gpu_shader4.</p>
     */
    public final boolean GL_INTEL_shader_integer_functions2;
    /** When true, {@link KHRBlendEquationAdvanced} is supported. */
    public final boolean GL_KHR_blend_equation_advanced;
    /** When true, {@link KHRBlendEquationAdvancedCoherent} is supported. */
    public final boolean GL_KHR_blend_equation_advanced_coherent;
    /** When true, {@link KHRContextFlushControl} is supported. */
    public final boolean GL_KHR_context_flush_control;
    /** When true, {@link KHRDebug} is supported. */
    public final boolean GL_KHR_debug;
    /** When true, {@link KHRNoError} is supported. */
    public final boolean GL_KHR_no_error;
    /** When true, {@link KHRParallelShaderCompile} is supported. */
    public final boolean GL_KHR_parallel_shader_compile;
    /**
     * When true, the <a target="_blank" href="https://www.khronos.org/registry/OpenGL/extensions/KHR/KHR_robust_buffer_access_behavior.txt">KHR_robust_buffer_access_behavior</a> extension is supported.
     *
     * <p>This extension specifies the behavior of out-of-bounds buffer and array accesses. This is an improvement over the existing {@link #GL_KHR_robustness KHR_robustness}
     * extension which states that the application should not crash, but that behavior is otherwise undefined. This extension specifies the access protection
     * provided by the GL to ensure that out-of-bounds accesses cannot read from or write to data not owned by the application. All accesses are contained
     * within the buffer object and program area they reference. These additional robustness guarantees apply to contexts created with the robust access flag
     * set.</p>
     *
     * <p>Requires {@link GL32 OpenGL 3.2} and {@link #GL_KHR_robustness KHR_robustness}.</p>
     */
    public final boolean GL_KHR_robust_buffer_access_behavior;
    /** When true, {@link KHRRobustness} is supported. */
    public final boolean GL_KHR_robustness;
    /** When true, {@link KHRShaderSubgroup} is supported. */
    public final boolean GL_KHR_shader_subgroup;
    /**
     * When true, the <a target="_blank" href="https://www.khronos.org/registry/OpenGL/extensions/KHR/KHR_texture_compression_astc_hdr.txt">KHR_texture_compression_astc_hdr</a> extension is supported.
     *
     * <p>This extension corresponds to the ASTC HDR Profile, see {@link KHRTextureCompressionASTCLDR KHR_texture_compression_astc_ldr} for details.</p>
     */
    public final boolean GL_KHR_texture_compression_astc_hdr;
    /** When true, {@link KHRTextureCompressionASTCLDR} is supported. */
    public final boolean GL_KHR_texture_compression_astc_ldr;
    /**
     * When true, the <a target="_blank" href="https://www.khronos.org/registry/OpenGL/extensions/KHR/KHR_texture_compression_astc_sliced_3d.txt">KHR_texture_compression_astc_sliced_3d</a> extension is supported.
     *
     * <p>Adaptive Scalable Texture Compression (ASTC) is a new texture compression technology that offers unprecendented flexibility, while producing better or
     * comparable results than existing texture compressions at all bit rates. It includes support for 2D and slice-based 3D textures, with low and high
     * dynamic range, at bitrates from below 1 bit/pixel up to 8 bits/pixel in fine steps.</p>
     *
     * <p>This extension extends the functionality of {@link KHRTextureCompressionASTCLDR KHR_texture_compression_astc_ldr} to include slice-based 3D textures for textures using the LDR
     * profile in the same way as the HDR profile allows slice-based 3D textures.</p>
     *
     * <p>Requires {@link KHRTextureCompressionASTCLDR KHR_texture_compression_astc_ldr}.</p>
     */
    public final boolean GL_KHR_texture_compression_astc_sliced_3d;
    /** When true, {@link MESAFramebufferFlipX} is supported. */
    public final boolean GL_MESA_framebuffer_flip_x;
    /** When true, {@link MESAFramebufferFlipY} is supported. */
    public final boolean GL_MESA_framebuffer_flip_y;
    /** When true, {@link MESAFramebufferSwapXY} is supported. */
    public final boolean GL_MESA_framebuffer_swap_xy;
    /**
     * When true, the <a target="_blank" href="https://www.khronos.org/registry/OpenGL/extensions/MESA/MESA_tile_raster_order.txt">MESA_tile_raster_order</a> extension is supported.
     *
     * <p>This extension extends the sampling-from-the-framebuffer behavior provided by {@code GL_ARB_texture_barrier} to allow setting the rasterization order
     * of the scene, so that overlapping blits can be implemented. This can be used for scrolling or window movement within in 2D scenes, without first
     * copying to a temporary.</p>
     *
     * <p>Requires {@link ARBTextureBarrier ARB_texture_barrier} or {@link NVTextureBarrier NV_texture_barrier}.</p>
     */
    public final boolean GL_MESA_tile_raster_order;
    /** When true, {@link NVAlphaToCoverageDitherControl} is supported. */
    public final boolean GL_NV_alpha_to_coverage_dither_control;
    /** When true, {@link NVBindlessMultiDrawIndirect} is supported. */
    public final boolean GL_NV_bindless_multi_draw_indirect;
    /** When true, {@link NVBindlessMultiDrawIndirectCount} is supported. */
    public final boolean GL_NV_bindless_multi_draw_indirect_count;
    /** When true, {@link NVBindlessTexture} is supported. */
    public final boolean GL_NV_bindless_texture;
    /** When true, {@link NVBlendEquationAdvanced} is supported. */
    public final boolean GL_NV_blend_equation_advanced;
    /** When true, {@link NVBlendEquationAdvancedCoherent} is supported. */
    public final boolean GL_NV_blend_equation_advanced_coherent;
    /** When true, {@link NVBlendMinmaxFactor} is supported. */
    public final boolean GL_NV_blend_minmax_factor;
    /** When true, the <a target="_blank" href="https://www.khronos.org/registry/OpenGL/extensions/NV/NV_blend_square.txt">NV_blend_square</a> extension is supported. */
    public final boolean GL_NV_blend_square;
    /** When true, {@link NVClipSpaceWScaling} is supported. */
    public final boolean GL_NV_clip_space_w_scaling;
    /** When true, {@link NVCommandList} is supported. */
    public final boolean GL_NV_command_list;
    /**
     * When true, the <a target="_blank" href="https://www.khronos.org/registry/OpenGL/extensions/NV/NV_compute_shader_derivatives.txt">NV_compute_shader_derivatives</a> extension is supported.
     *
     * <p>This extension adds OpenGL API support for the OpenGL Shading Language (GLSL) extension {@code "NV_compute_shader_derivatives"}.</p>
     *
     * <p>That extension, when enabled, allows applications to use derivatives in compute shaders. It adds compute shader support for explicit derivative
     * built-in functions like {@code dFdx()}, automatic derivative computation in texture lookup functions like {@code texture()}, use of the optional LOD
     * bias parameter to adjust the computed level of detail values in texture lookup functions, and the texture level of detail query function
     * {@code textureQueryLod()}.</p>
     *
     * <p>Requires {@link GL45 OpenGL 4.5}.</p>
     */
    public final boolean GL_NV_compute_shader_derivatives;
    /** When true, {@link NVConditionalRender} is supported. */
    public final boolean GL_NV_conditional_render;
    /** When true, {@link NVConservativeRaster} is supported. */
    public final boolean GL_NV_conservative_raster;
    /** When true, {@link NVConservativeRasterDilate} is supported. */
    public final boolean GL_NV_conservative_raster_dilate;
    /** When true, {@link NVConservativeRasterPreSnap} is supported. */
    public final boolean GL_NV_conservative_raster_pre_snap;
    /** When true, {@link NVConservativeRasterPreSnapTriangles} is supported. */
    public final boolean GL_NV_conservative_raster_pre_snap_triangles;
    /**
     * When true, the <a target="_blank" href="https://www.khronos.org/registry/OpenGL/extensions/NV/NV_conservative_raster_underestimation.txt">NV_conservative_raster_underestimation</a> extension is supported.
     *
     * <p>The extension {@link NVConservativeRaster NV_conservative_raster} provides a new rasterization mode known as "Overestimated Conservative Rasterization", where any pixel
     * that is partially covered, even if no sample location is covered, is treated as fully covered and a corresponding fragment will be shaded. There is
     * also an "Underestimated Conservative Rasterization" variant, where only the pixels that are completely covered by the primitive are rasterized.</p>
     *
     * <p>This extension provides the underestimated conservative rasterization information for each fragment in the fragment shader through a new built-in
     * {@code gl_FragFullyCoveredNV}.</p>
     */
    public final boolean GL_NV_conservative_raster_underestimation;
    /** When true, {@link NVCopyDepthToColor} is supported. */
    public final boolean GL_NV_copy_depth_to_color;
    /** When true, {@link NVCopyImage} is supported. */
    public final boolean GL_NV_copy_image;
    /** When true, {@link NVDeepTexture3D} is supported. */
    public final boolean GL_NV_deep_texture3D;
    /** When true, {@link NVDepthBufferFloat} is supported. */
    public final boolean GL_NV_depth_buffer_float;
    /** When true, {@link NVDepthClamp} is supported. */
    public final boolean GL_NV_depth_clamp;
    /** When true, {@link NVDrawTexture} is supported. */
    public final boolean GL_NV_draw_texture;
    /** When true, {@link NVDrawVulkanImage} is supported. */
    public final boolean GL_NV_draw_vulkan_image;
    /** When true, the <a target="_blank" href="https://www.khronos.org/registry/OpenGL/extensions/NV/NV_ES3_1_compatibility.txt">NV_ES3_1_compatibility</a> extension is supported. */
    public final boolean GL_NV_ES3_1_compatibility;
    /** When true, {@link NVExplicitMultisample} is supported. */
    public final boolean GL_NV_explicit_multisample;
    /** When true, {@link NVFence} is supported. */
    public final boolean GL_NV_fence;
    /** When true, {@link NVFillRectangle} is supported. */
    public final boolean GL_NV_fill_rectangle;
    /** When true, {@link NVFloatBuffer} is supported. */
    public final boolean GL_NV_float_buffer;
    /** When true, {@link NVFogDistance} is supported. */
    public final boolean GL_NV_fog_distance;
    /** When true, {@link NVFragmentCoverageToColor} is supported. */
    public final boolean GL_NV_fragment_coverage_to_color;
    /** When true, the <a target="_blank" href="https://www.khronos.org/registry/OpenGL/extensions/NV/NV_fragment_program4.txt">NV_fragment_program4</a> extension is supported. */
    public final boolean GL_NV_fragment_program4;
    /** When true, the <a target="_blank" href="https://www.khronos.org/registry/OpenGL/extensions/NV/NV_fragment_program_option.txt">NV_fragment_program_option</a> extension is supported. */
    public final boolean GL_NV_fragment_program_option;
    /**
     * When true, the <a target="_blank" href="https://www.khronos.org/registry/OpenGL/extensions/NV/NV_fragment_shader_barycentric.txt">NV_fragment_shader_barycentric</a> extension is supported.
     *
     * <p>This extension advertises OpenGL support for the OpenGL Shading Language (GLSL) extension {@code "NV_fragment_shader_barycentric"}, which provides
     * fragment shader built-in variables holding barycentric weight vectors that identify the location of the fragment within its primitive. Additionally,
     * the GLSL extension allows fragment the ability to read raw attribute values for each of the vertices of the primitive that produced the fragment.</p>
     *
     * <p>Requires {@link GL45 OpenGL 4.5}.</p>
     */
    public final boolean GL_NV_fragment_shader_barycentric;
    /**
     * When true, the <a target="_blank" href="https://www.khronos.org/registry/OpenGL/extensions/NV/NV_fragment_shader_interlock.txt">NV_fragment_shader_interlock</a> extension is supported.
     *
     * <p>In unextended OpenGL 4.3, applications may produce a large number of fragment shader invocations that perform loads and stores to memory using image
     * uniforms, atomic counter uniforms, buffer variables, or pointers. The order in which loads and stores to common addresses are performed by different
     * fragment shader invocations is largely undefined. For algorithms that use shader writes and touch the same pixels more than once, one or more of the
     * following techniques may be required to ensure proper execution ordering:</p>
     *
     * <ul>
     * <li>inserting Finish or WaitSync commands to drain the pipeline between different "passes" or "layers";</li>
     * <li>using only atomic memory operations to write to shader memory (which may be relatively slow and limits how memory may be updated); or</li>
     * <li>injecting spin loops into shaders to prevent multiple shader invocations from touching the same memory concurrently.</li>
     * </ul>
     *
     * <p>This extension provides new GLSL built-in functions beginInvocationInterlockNV() and endInvocationInterlockNV() that delimit a critical section of
     * fragment shader code. For pairs of shader invocations with "overlapping" coverage in a given pixel, the OpenGL implementation will guarantee that the
     * critical section of the fragment shader will be executed for only one fragment at a time.</p>
     *
     * <p>There are four different interlock modes supported by this extension, which are identified by layout qualifiers. The qualifiers
     * "pixel_interlock_ordered" and "pixel_interlock_unordered" provides mutual exclusion in the critical section for any pair of fragments corresponding to
     * the same pixel. When using multisampling, the qualifiers "sample_interlock_ordered" and "sample_interlock_unordered" only provide mutual exclusion for
     * pairs of fragments that both cover at least one common sample in the same pixel; these are recommended for performance if shaders use per-sample data
     * structures.</p>
     *
     * <p>Additionally, when the "pixel_interlock_ordered" or "sample_interlock_ordered" layout qualifier is used, the interlock also guarantees that the
     * critical section for multiple shader invocations with "overlapping" coverage will be executed in the order in which the primitives were processed by
     * the GL. Such a guarantee is useful for applications like blending in the fragment shader, where an application requires that fragment values to be
     * composited in the framebuffer in primitive order.</p>
     *
     * <p>This extension can be useful for algorithms that need to access per-pixel data structures via shader loads and stores. Such algorithms using this
     * extension can access such data structures in the critical section without worrying about other invocations for the same pixel accessing the data
     * structures concurrently. Additionally, the ordering guarantees are useful for cases where the API ordering of fragments is meaningful. For example,
     * applications may be able to execute programmable blending operations in the fragment shader, where the destination buffer is read via image loads and
     * the final value is written via image stores.</p>
     *
     * <p>Requires {@link GL43 OpenGL 4.3} and GLSL 4.30.</p>
     */
    public final boolean GL_NV_fragment_shader_interlock;
    /** When true, {@link NVFramebufferMixedSamples} is supported. */
    public final boolean GL_NV_framebuffer_mixed_samples;
    /** When true, {@link NVFramebufferMultisampleCoverage} is supported. */
    public final boolean GL_NV_framebuffer_multisample_coverage;
    /**
     * When true, the <a target="_blank" href="https://www.khronos.org/registry/OpenGL/extensions/NV/NV_geometry_shader4.txt">NV_geometry_shader4</a> extension is supported.
     *
     * <p>This extension builds upon the {@link #GL_EXT_geometry_shader4 EXT_geometry_shader4} specification to provide two additional capabilities:</p>
     *
     * <ul>
     * <li>Support for QUADS, QUAD_STRIP, and POLYGON primitive types when geometry shaders are enabled.  Such primitives will be tessellated into individual
     * triangles.</li>
     * <li>Setting the value of GEOMETRY_VERTICES_OUT_EXT will take effect immediately. It is not necessary to link the program object in order for this change
     * to take effect, as is the case in the EXT version of this extension.</li>
     * </ul>
     *
     * <p>Requires {@link #GL_EXT_geometry_shader4 EXT_geometry_shader4}.</p>
     */
    public final boolean GL_NV_geometry_shader4;
    /**
     * When true, the <a target="_blank" href="https://www.khronos.org/registry/OpenGL/extensions/NV/NV_geometry_shader_passthrough.txt">NV_geometry_shader_passthrough</a> extension is supported.
     *
     * <p>This extension provides a shading language abstraction to express such shaders without requiring explicit logic to manually copy attributes from input
     * vertices to output vertices.</p>
     */
    public final boolean GL_NV_geometry_shader_passthrough;
    /** When true, {@link NVGPUMulticast} is supported. */
    public final boolean GL_NV_gpu_multicast;
    /** When true, {@link NVGPUShader5} is supported. */
    public final boolean GL_NV_gpu_shader5;
    /** When true, {@link NVHalfFloat} is supported. */
    public final boolean GL_NV_half_float;
    /** When true, {@link NVInternalformatSampleQuery} is supported. */
    public final boolean GL_NV_internalformat_sample_query;
    /** When true, {@link NVLightMaxExponent} is supported. */
    public final boolean GL_NV_light_max_exponent;
    /** When true, {@link NVMemoryAttachment} is supported. */
    public final boolean GL_NV_memory_attachment;
    /** When true, {@link NVMemoryObjectSparse} is supported. */
    public final boolean GL_NV_memory_object_sparse;
    /** When true, {@link NVMeshShader} is supported. */
    public final boolean GL_NV_mesh_shader;
    /** When true, {@link NVMultisampleCoverage} is supported. */
    public final boolean GL_NV_multisample_coverage;
    /** When true, {@link NVMultisampleFilterHint} is supported. */
    public final boolean GL_NV_multisample_filter_hint;
    /** When true, {@link NVPackedDepthStencil} is supported. */
    public final boolean GL_NV_packed_depth_stencil;
    /** When true, {@link NVPathRendering} is supported. */
    public final boolean GL_NV_path_rendering;
    /** When true, {@link NVPathRenderingSharedEdge} is supported. */
    public final boolean GL_NV_path_rendering_shared_edge;
    /** When true, {@link NVPixelDataRange} is supported. */
    public final boolean GL_NV_pixel_data_range;
    /** When true, {@link NVPointSprite} is supported. */
    public final boolean GL_NV_point_sprite;
    /** When true, {@link NVPrimitiveRestart} is supported. */
    public final boolean GL_NV_primitive_restart;
    /** When true, {@link NVPrimitiveShadingRate} is supported. */
    public final boolean GL_NV_primitive_shading_rate;
    /** When true, {@link NVQueryResource} is supported. */
    public final boolean GL_NV_query_resource;
    /** When true, {@link NVQueryResourceTag} is supported. */
    public final boolean GL_NV_query_resource_tag;
    /** When true, {@link NVRepresentativeFragmentTest} is supported. */
    public final boolean GL_NV_representative_fragment_test;
    /** When true, {@link NVRobustnessVideoMemoryPurge} is supported. */
    public final boolean GL_NV_robustness_video_memory_purge;
    /** When true, {@link NVSampleLocations} is supported. */
    public final boolean GL_NV_sample_locations;
    /**
     * When true, the <a target="_blank" href="https://www.khronos.org/registry/OpenGL/extensions/NV/NV_sample_mask_override_coverage.txt">NV_sample_mask_override_coverage</a> extension is supported.
     *
     * <p>This extension allows the fragment shader to control whether the gl_SampleMask output can enable samples that were not covered by the original
     * primitive, or that failed the early depth/stencil tests.</p>
     */
    public final boolean GL_NV_sample_mask_override_coverage;
    /** When true, {@link NVScissorExclusive} is supported. */
    public final boolean GL_NV_scissor_exclusive;
    /**
     * When true, the <a target="_blank" href="https://www.khronos.org/registry/OpenGL/extensions/NV/NV_shader_atomic_float.txt">NV_shader_atomic_float</a> extension is supported.
     *
     * <p>This extension provides GLSL built-in functions and assembly opcodes allowing shaders to perform atomic read-modify-write operations to buffer or
     * texture memory with floating-point components.  The set of atomic operations provided by this extension is limited to adds and exchanges. Providing
     * atomic add support allows shaders to atomically accumulate the sum of floating-point values into buffer or texture memory across multiple (possibly
     * concurrent) shader invocations.</p>
     *
     * <p>This extension provides GLSL support for atomics targeting image uniforms (if GLSL 4.20, {@link #GL_ARB_shader_image_load_store ARB_shader_image_load_store}, or
     * {@link #GL_EXT_shader_image_load_store EXT_shader_image_load_store} is supported) or floating-point pointers (if {@link #GL_NV_gpu_shader5 NV_gpu_shader5} is supported). Additionally, assembly opcodes
     * for these operations is also provided if <a target="_blank" href="https://www.khronos.org/registry/OpenGL/extensions/NV/NV_gpu_program5.txt">NV_gpu_program5</a> is supported.</p>
     */
    public final boolean GL_NV_shader_atomic_float;
    /**
     * When true, the <a target="_blank" href="https://www.khronos.org/registry/OpenGL/extensions/NV/NV_shader_atomic_float64.txt">NV_shader_atomic_float64</a> extension is supported.
     *
     * <p>This extension provides GLSL built-in functions and assembly opcodes allowing shaders to perform atomic read-modify-write operations to buffer or
     * shared memory with double-precision floating-point components.  The set of atomic operations provided by this extension is limited to adds and
     * exchanges. Providing atomic add support allows shaders to atomically accumulate the sum of double-precision floating-point values into buffer memory
     * across multiple (possibly concurrent) shader invocations.</p>
     *
     * <p>This extension provides GLSL support for atomics targeting double-precision floating-point pointers (if {@link NVGPUShader5 NV_gpu_shader5} is supported).
     * Additionally, assembly opcodes for these operations are also provided if {@code NV_gpu_program5} is supported.</p>
     *
     * <p>Requires {@link ARBGPUShaderFP64 ARB_gpu_shader_fp64} or {@code NV_gpu_program_fp64}.</p>
     */
    public final boolean GL_NV_shader_atomic_float64;
    /**
     * When true, the <a target="_blank" href="https://www.khronos.org/registry/OpenGL/extensions/NV/NV_shader_atomic_fp16_vector.txt">NV_shader_atomic_fp16_vector</a> extension is supported.
     *
     * <p>This extension provides GLSL built-in functions and assembly opcodes allowing shaders to perform a limited set of atomic read-modify-write operations
     * to buffer or texture memory with 16-bit floating point vector surface formats.</p>
     *
     * <p>Requires {@link #GL_NV_gpu_shader5 NV_gpu_shader5}.</p>
     */
    public final boolean GL_NV_shader_atomic_fp16_vector;
    /**
     * When true, the <a target="_blank" href="https://www.khronos.org/registry/OpenGL/extensions/NV/NV_shader_atomic_int64.txt">NV_shader_atomic_int64</a> extension is supported.
     *
     * <p>This extension provides additional GLSL built-in functions and assembly opcodes allowing shaders to perform additional atomic read-modify-write
     * operations on 64-bit signed and unsigned integers stored in buffer object memory.</p>
     */
    public final boolean GL_NV_shader_atomic_int64;
    /** When true, {@link NVShaderBufferLoad} is supported. */
    public final boolean GL_NV_shader_buffer_load;
    /** When true, {@link NVShaderBufferStore} is supported. */
    public final boolean GL_NV_shader_buffer_store;
    /** When true, {@link NVShaderSubgroupPartitioned} is supported. */
    public final boolean GL_NV_shader_subgroup_partitioned;
    /**
     * When true, the <a target="_blank" href="https://www.khronos.org/registry/OpenGL/extensions/NV/NV_shader_texture_footprint.txt">NV_shader_texture_footprint</a> extension is supported.
     *
     * <p>This extension adds OpenGL API support for the OpenGL Shading Language (GLSL) extension {@code "NV_shader_texture_footprint"}. That extension adds a
     * new set of texture query functions ({@code "textureFootprint*NV"}) to GLSL. These built-in functions prepare to perform a filtered texture lookup based
     * on coordinates and other parameters passed in by the calling code. However, instead of returning data from the provided texture image, these query
     * functions instead return data identifying the <em>texture footprint</em> for an equivalent texture access. The texture footprint identifies a set of
     * texels that may be accessed in order to return a filtered result for the texture access.</p>
     *
     * <p>The footprint itself is a structure that includes integer values that identify a small neighborhood of texels in the texture being accessed and a
     * bitfield that indicates which texels in that neighborhood would be used. Each bit in the returned bitfield identifies whether any texel in a small
     * aligned block of texels would be fetched by the texture lookup. The size of each block is specified by an access <em>granularity</em> provided by the
     * shader. The minimum granularity supported by this extension is 2x2 (for 2D textures) and 2x2x2 (for 3D textures); the maximum granularity is 256x256
     * (for 2D textures) or 64x32x32 (for 3D textures). Each footprint query returns the footprint from a single texture level. When using minification
     * filters that combine accesses from multiple mipmap levels, shaders must perform separate queries for the two levels accessed ("fine" and "coarse"). The
     * footprint query also returns a flag indicating if the texture lookup would access texels from only one mipmap level or from two neighboring levels.</p>
     *
     * <p>This extension should be useful for multi-pass rendering operations that do an initial expensive rendering pass to produce a first image that is then
     * used as a texture for a second pass. If the second pass ends up accessing only portions of the first image (e.g., due to visibility), the work spent
     * rendering the non-accessed portion of the first image was wasted. With this feature, an application can limit this waste using an initial pass over the
     * geometry in the second image that performs a footprint query for each visible pixel to determine the set of pixels that it needs from the first image.
     * This pass would accumulate an aggregate footprint of all visible pixels into a separate "footprint texture" using shader atomics. Then, when rendering
     * the first image, the application can kill all shading work for pixels not in this aggregate footprint.</p>
     *
     * <p>The implementation of this extension has a number of limitations. The texture footprint query functions are only supported for two- and
     * three-dimensional textures ({@link GL11#GL_TEXTURE_2D TEXTURE_2D}, {@link GL12#GL_TEXTURE_3D TEXTURE_3D}). Texture footprint evaluation only supports the {@link GL12#GL_CLAMP_TO_EDGE CLAMP_TO_EDGE} wrap mode; results are undefined
     * for all other wrap modes. The implementation supports only a limited set of granularity values and does not support separate coverage information for
     * each texel in the original texture.</p>
     *
     * <p>Requires {@link GL45 OpenGL 4.5}.</p>
     */
    public final boolean GL_NV_shader_texture_footprint;
    /** When true, {@link NVShaderThreadGroup} is supported. */
    public final boolean GL_NV_shader_thread_group;
    /**
     * When true, the <a target="_blank" href="https://www.khronos.org/registry/OpenGL/extensions/NV/NV_shader_thread_shuffle.txt">NV_shader_thread_shuffle</a> extension is supported.
     *
     * <p>Implementations of the OpenGL Shading Language may, but are not required, to run multiple shader threads for a single stage as a SIMD thread group,
     * where individual execution threads are assigned to thread groups in an undefined, implementation-dependent order.  This extension provides a set of
     * new features to the OpenGL Shading Language to share data between multiple threads within a thread group.</p>
     *
     * <p>Requires {@link GL43 OpenGL 4.3} and GLSL 4.3.</p>
     */
    public final boolean GL_NV_shader_thread_shuffle;
    /** When true, {@link NVShadingRateImage} is supported. */
    public final boolean GL_NV_shading_rate_image;
    /**
     * When true, the <a target="_blank" href="https://www.khronos.org/registry/OpenGL/extensions/NV/NV_stereo_view_rendering.txt">NV_stereo_view_rendering</a> extension is supported.
     *
     * <p>Virtual reality (VR) applications often render a single logical scene from multiple views corresponding to a pair of eyes. The views (eyes) are
     * separated by a fixed offset in the X direction.</p>
     *
     * <p>Traditionally, multiple views are rendered via multiple rendering passes. This is expensive for the GPU because the objects in the scene must be
     * transformed, rasterized, shaded, and fragment processed redundantly. This is expensive for the CPU because the scene graph needs to be visited multiple
     * times and driver validation happens for each view. Rendering N passes tends to take N times longer than a single pass.</p>
     *
     * <p>This extension provides a mechanism to render binocular (stereo) views from a single stream of OpenGL rendering commands. Vertex, tessellation, and
     * geometry (VTG) shaders can output two positions for each vertex corresponding to the two eye views. A built-in "gl_SecondaryPositionNV" is added to
     * specify the second position. The positions from each view may be sent to different viewports and/or layers. A built-in "gl_SecondaryViewportMaskNV[]"
     * is also added to specify the viewport mask for the second view. A new layout-qualifier "secondary_view_offset" is added for built-in output "gl_Layer"
     * which allows for the geometry from each view to be sent to different layers for rendering.</p>
     *
     * <p>Requires {@link #GL_NV_viewport_array2 NV_viewport_array2}.</p>
     */
    public final boolean GL_NV_stereo_view_rendering;
    /** When true, {@link NVTexgenReflection} is supported. */
    public final boolean GL_NV_texgen_reflection;
    /** When true, {@link NVTextureBarrier} is supported. */
    public final boolean GL_NV_texture_barrier;
    /**
     * When true, the <a target="_blank" href="https://www.khronos.org/registry/OpenGL/extensions/NV/NV_texture_compression_vtc.txt">NV_texture_compression_vtc</a> extension is supported.
     *
     * <p>This extension adds support for the VTC 3D texture compression formats, which are analogous to the S3TC texture compression formats, with the addition
     * of some retiling in the Z direction. VTC has the same compression ratio as S3TC and uses 4x4x1, 4x4x2, (4x4x3 when non-power-of-two textures are
     * supported), or 4x4x4 blocks.</p>
     */
    public final boolean GL_NV_texture_compression_vtc;
    /** When true, {@link NVTextureMultisample} is supported. */
    public final boolean GL_NV_texture_multisample;
    /**
     * When true, the <a target="_blank" href="https://www.khronos.org/registry/OpenGL/extensions/NV/NV_texture_rectangle_compressed.txt">NV_texture_rectangle_compressed</a> extension is supported.
     *
     * <p>This extension allows applications to use compressed texture formats with the {@link GL31#GL_TEXTURE_RECTANGLE TEXTURE_RECTANGLE} texture target, removing an old limitation that
     * prohibited such usage globally for rectangle textures.</p>
     */
    public final boolean GL_NV_texture_rectangle_compressed;
    /** When true, {@link NVTextureShader} is supported. */
    public final boolean GL_NV_texture_shader;
    /** When true, {@link NVTextureShader2} is supported. */
    public final boolean GL_NV_texture_shader2;
    /** When true, {@link NVTextureShader3} is supported. */
    public final boolean GL_NV_texture_shader3;
    /** When true, {@link NVTimelineSemaphore} is supported. */
    public final boolean GL_NV_timeline_semaphore;
    /** When true, {@link NVTransformFeedback} is supported. */
    public final boolean GL_NV_transform_feedback;
    /** When true, {@link NVTransformFeedback2} is supported. */
    public final boolean GL_NV_transform_feedback2;
    /** When true, {@link NVUniformBufferUnifiedMemory} is supported. */
    public final boolean GL_NV_uniform_buffer_unified_memory;
    /** When true, {@link NVVertexArrayRange} is supported. */
    public final boolean GL_NV_vertex_array_range;
    /** When true, {@link NVVertexArrayRange2} is supported. */
    public final boolean GL_NV_vertex_array_range2;
    /** When true, {@link NVVertexAttribInteger64bit} is supported. */
    public final boolean GL_NV_vertex_attrib_integer_64bit;
    /** When true, {@link NVVertexBufferUnifiedMemory} is supported. */
    public final boolean GL_NV_vertex_buffer_unified_memory;
    /**
     * When true, the <a target="_blank" href="https://www.khronos.org/registry/OpenGL/extensions/NV/NV_viewport_array2.txt">NV_viewport_array2</a> extension is supported.
     *
     * <p>This extension provides new support allowing a single primitive to be broadcast to multiple viewports and/or multiple layers. A shader output
     * gl_ViewportMask[] is provided, allowing a single primitive to be output to multiple viewports simultaneously. Also, a new shader option is provided to
     * control whether the effective viewport index is added into gl_Layer. These capabilities allow a single primitive to be output to multiple layers
     * simultaneously.</p>
     *
     * <p>The gl_ViewportMask[] output is available in vertex, tessellation control, tessellation evaluation, and geometry shaders. gl_ViewportIndex and gl_Layer
     * are also made available in all these shader stages. The actual viewport index or mask and render target layer values are taken from the last active
     * shader stage from this set of stages.</p>
     *
     * <p>This extension is a superset of the GL_AMD_vertex_shader_layer and GL_AMD_vertex_shader_viewport_index extensions, and thus those extension strings are
     * expected to be exported if GL_NV_viewport_array2 is supported.</p>
     */
    public final boolean GL_NV_viewport_array2;
    /** When true, {@link NVViewportSwizzle} is supported. */
    public final boolean GL_NV_viewport_swizzle;
    /**
     * When true, the <a target="_blank" href="https://www.khronos.org/registry/OpenGL/extensions/NVX/NVX_blend_equation_advanced_multi_draw_buffers.txt">NVX_blend_equation_advanced_multi_draw_buffers</a> extension is supported.
     *
     * <p>This extension adds support for using advanced blend equations introduced with {@link NVBlendEquationAdvanced NV_blend_equation_advanced} (and standardized by
     * {@link KHRBlendEquationAdvanced KHR_blend_equation_advanced}) in conjunction with multiple draw buffers. The NV_blend_equation_advanced extension supports advanced blending
     * equations only when rending to a single color buffer using fragment color zero and throws and {@link GL11#GL_INVALID_OPERATION INVALID_OPERATION} error when multiple draw buffers are
     * used. This extension removes this restriction.</p>
     *
     * <p>Requires either {@link NVBlendEquationAdvanced NV_blend_equation_advanced} or {@link KHRBlendEquationAdvanced KHR_blend_equation_advanced}.</p>
     */
    public final boolean GL_NVX_blend_equation_advanced_multi_draw_buffers;
    /** When true, {@link NVXConditionalRender} is supported. */
    public final boolean GL_NVX_conditional_render;
    /** When true, {@link NVXGPUMemoryInfo} is supported. */
    public final boolean GL_NVX_gpu_memory_info;
    /** When true, {@link NVXGpuMulticast2} is supported. */
    public final boolean GL_NVX_gpu_multicast2;
    /** When true, {@link NVXProgressFence} is supported. */
    public final boolean GL_NVX_progress_fence;
    /** When true, {@link OVRMultiview} is supported. */
    public final boolean GL_OVR_multiview;
    /**
     * When true, the <a target="_blank" href="https://www.khronos.org/registry/OpenGL/extensions/OVR/OVR_multiview2.txt">OVR_multiview2</a> extension is supported.
     *
     * <p>This extension relaxes the restriction in OVR_multiview that only {@code gl_Position} can depend on {@code ViewID} in the vertex shader.  With this
     * change, view-dependent outputs like reflection vectors and similar are allowed.</p>
     *
     * <p>Requires {@link GL30 OpenGL 3.0} and {@link OVRMultiview OVR_multiview}.</p>
     */
    public final boolean GL_OVR_multiview2;
    /** When true, {@link S3S3TC} is supported. */
    public final boolean GL_S3_s3tc;

    /** When true, deprecated functions are not available. */
    public final boolean forwardCompatible;

    /** Off-heap array of the above function addresses. */
    final PointerBuffer addresses;

    GLCapabilities(FunctionProvider provider, Set<String> ext, boolean fc, IntFunction<PointerBuffer> bufferFactory) {
        forwardCompatible = fc;

        PojavRendererInit.onCreateCapabilities(provider);

        PointerBuffer caps = bufferFactory.apply(ADDRESS_BUFFER_SIZE);

        OpenGL11 = check_GL11(provider, caps, ext, fc);
        OpenGL12 = check_GL12(provider, caps, ext);
        OpenGL13 = check_GL13(provider, caps, ext, fc);
        OpenGL14 = check_GL14(provider, caps, ext, fc);
        OpenGL15 = check_GL15(provider, caps, ext);
        OpenGL20 = check_GL20(provider, caps, ext);
        OpenGL21 = check_GL21(provider, caps, ext);
        OpenGL30 = check_GL30(provider, caps, ext);
        OpenGL31 = check_GL31(provider, caps, ext);
        OpenGL32 = check_GL32(provider, caps, ext);
        OpenGL33 = check_GL33(provider, caps, ext, fc);
        OpenGL40 = check_GL40(provider, caps, ext);
        OpenGL41 = check_GL41(provider, caps, ext);
        OpenGL42 = check_GL42(provider, caps, ext);
        OpenGL43 = check_GL43(provider, caps, ext);
        OpenGL44 = check_GL44(provider, caps, ext);
        OpenGL45 = check_GL45(provider, caps, ext);
        OpenGL46 = check_GL46(provider, caps, ext);
        GL_3DFX_texture_compression_FXT1 = ext.contains("GL_3DFX_texture_compression_FXT1");
        GL_AMD_blend_minmax_factor = ext.contains("GL_AMD_blend_minmax_factor");
        GL_AMD_conservative_depth = ext.contains("GL_AMD_conservative_depth");
        GL_AMD_debug_output = check_AMD_debug_output(provider, caps, ext);
        GL_AMD_depth_clamp_separate = ext.contains("GL_AMD_depth_clamp_separate");
        GL_AMD_draw_buffers_blend = check_AMD_draw_buffers_blend(provider, caps, ext);
        GL_AMD_framebuffer_multisample_advanced = check_AMD_framebuffer_multisample_advanced(provider, caps, ext);
        GL_AMD_gcn_shader = ext.contains("GL_AMD_gcn_shader");
        GL_AMD_gpu_shader_half_float = ext.contains("GL_AMD_gpu_shader_half_float");
        GL_AMD_gpu_shader_half_float_fetch = ext.contains("GL_AMD_gpu_shader_half_float_fetch");
        GL_AMD_gpu_shader_int16 = ext.contains("GL_AMD_gpu_shader_int16");
        GL_AMD_gpu_shader_int64 = check_AMD_gpu_shader_int64(provider, caps, ext);
        GL_AMD_interleaved_elements = check_AMD_interleaved_elements(provider, caps, ext);
        GL_AMD_occlusion_query_event = check_AMD_occlusion_query_event(provider, caps, ext);
        GL_AMD_performance_monitor = check_AMD_performance_monitor(provider, caps, ext);
        GL_AMD_pinned_memory = ext.contains("GL_AMD_pinned_memory");
        GL_AMD_query_buffer_object = ext.contains("GL_AMD_query_buffer_object");
        GL_AMD_sample_positions = check_AMD_sample_positions(provider, caps, ext);
        GL_AMD_seamless_cubemap_per_texture = ext.contains("GL_AMD_seamless_cubemap_per_texture");
        GL_AMD_shader_atomic_counter_ops = ext.contains("GL_AMD_shader_atomic_counter_ops");
        GL_AMD_shader_ballot = ext.contains("GL_AMD_shader_ballot");
        GL_AMD_shader_explicit_vertex_parameter = ext.contains("GL_AMD_shader_explicit_vertex_parameter");
        GL_AMD_shader_image_load_store_lod = ext.contains("GL_AMD_shader_image_load_store_lod");
        GL_AMD_shader_stencil_export = ext.contains("GL_AMD_shader_stencil_export");
        GL_AMD_shader_trinary_minmax = ext.contains("GL_AMD_shader_trinary_minmax");
        GL_AMD_sparse_texture = check_AMD_sparse_texture(provider, caps, ext);
        GL_AMD_stencil_operation_extended = check_AMD_stencil_operation_extended(provider, caps, ext);
        GL_AMD_texture_gather_bias_lod = ext.contains("GL_AMD_texture_gather_bias_lod");
        GL_AMD_texture_texture4 = ext.contains("GL_AMD_texture_texture4");
        GL_AMD_transform_feedback3_lines_triangles = ext.contains("GL_AMD_transform_feedback3_lines_triangles");
        GL_AMD_transform_feedback4 = ext.contains("GL_AMD_transform_feedback4");
        GL_AMD_vertex_shader_layer = ext.contains("GL_AMD_vertex_shader_layer");
        GL_AMD_vertex_shader_tessellator = check_AMD_vertex_shader_tessellator(provider, caps, ext);
        GL_AMD_vertex_shader_viewport_index = ext.contains("GL_AMD_vertex_shader_viewport_index");
        GL_ARB_arrays_of_arrays = ext.contains("GL_ARB_arrays_of_arrays");
        GL_ARB_base_instance = check_ARB_base_instance(provider, caps, ext);
        GL_ARB_bindless_texture = check_ARB_bindless_texture(provider, caps, ext);
        GL_ARB_blend_func_extended = check_ARB_blend_func_extended(provider, caps, ext);
        GL_ARB_buffer_storage = check_ARB_buffer_storage(provider, caps, ext);
        GL_ARB_cl_event = check_ARB_cl_event(provider, caps, ext);
        GL_ARB_clear_buffer_object = check_ARB_clear_buffer_object(provider, caps, ext);
        GL_ARB_clear_texture = check_ARB_clear_texture(provider, caps, ext);
        GL_ARB_clip_control = check_ARB_clip_control(provider, caps, ext);
        GL_ARB_color_buffer_float = check_ARB_color_buffer_float(provider, caps, ext);
        GL_ARB_compatibility = ext.contains("GL_ARB_compatibility");
        GL_ARB_compressed_texture_pixel_storage = ext.contains("GL_ARB_compressed_texture_pixel_storage");
        GL_ARB_compute_shader = check_ARB_compute_shader(provider, caps, ext);
        GL_ARB_compute_variable_group_size = check_ARB_compute_variable_group_size(provider, caps, ext);
        GL_ARB_conditional_render_inverted = ext.contains("GL_ARB_conditional_render_inverted");
        GL_ARB_conservative_depth = ext.contains("GL_ARB_conservative_depth");
        GL_ARB_copy_buffer = check_ARB_copy_buffer(provider, caps, ext);
        GL_ARB_copy_image = check_ARB_copy_image(provider, caps, ext);
        GL_ARB_cull_distance = ext.contains("GL_ARB_cull_distance");
        GL_ARB_debug_output = check_ARB_debug_output(provider, caps, ext);
        GL_ARB_depth_buffer_float = ext.contains("GL_ARB_depth_buffer_float");
        GL_ARB_depth_clamp = ext.contains("GL_ARB_depth_clamp");
        GL_ARB_depth_texture = ext.contains("GL_ARB_depth_texture");
        GL_ARB_derivative_control = ext.contains("GL_ARB_derivative_control");
        GL_ARB_direct_state_access = check_ARB_direct_state_access(provider, caps, ext);
        GL_ARB_draw_buffers = check_ARB_draw_buffers(provider, caps, ext);
        GL_ARB_draw_buffers_blend = check_ARB_draw_buffers_blend(provider, caps, ext);
        GL_ARB_draw_elements_base_vertex = check_ARB_draw_elements_base_vertex(provider, caps, ext);
        GL_ARB_draw_indirect = check_ARB_draw_indirect(provider, caps, ext);
        GL_ARB_draw_instanced = check_ARB_draw_instanced(provider, caps, ext);
        GL_ARB_enhanced_layouts = ext.contains("GL_ARB_enhanced_layouts");
        GL_ARB_ES2_compatibility = check_ARB_ES2_compatibility(provider, caps, ext);
        GL_ARB_ES3_1_compatibility = check_ARB_ES3_1_compatibility(provider, caps, ext);
        GL_ARB_ES3_2_compatibility = check_ARB_ES3_2_compatibility(provider, caps, ext);
        GL_ARB_ES3_compatibility = ext.contains("GL_ARB_ES3_compatibility");
        GL_ARB_explicit_attrib_location = ext.contains("GL_ARB_explicit_attrib_location");
        GL_ARB_explicit_uniform_location = ext.contains("GL_ARB_explicit_uniform_location");
        GL_ARB_fragment_coord_conventions = ext.contains("GL_ARB_fragment_coord_conventions");
        GL_ARB_fragment_layer_viewport = ext.contains("GL_ARB_fragment_layer_viewport");
        GL_ARB_fragment_program = ext.contains("GL_ARB_fragment_program");
        GL_ARB_fragment_program_shadow = ext.contains("GL_ARB_fragment_program_shadow");
        GL_ARB_fragment_shader = ext.contains("GL_ARB_fragment_shader");
        GL_ARB_fragment_shader_interlock = ext.contains("GL_ARB_fragment_shader_interlock");
        GL_ARB_framebuffer_no_attachments = check_ARB_framebuffer_no_attachments(provider, caps, ext);
        GL_ARB_framebuffer_object = check_ARB_framebuffer_object(provider, caps, ext);
        GL_ARB_framebuffer_sRGB = ext.contains("GL_ARB_framebuffer_sRGB");
        GL_ARB_geometry_shader4 = check_ARB_geometry_shader4(provider, caps, ext);
        GL_ARB_get_program_binary = check_ARB_get_program_binary(provider, caps, ext);
        GL_ARB_get_texture_sub_image = check_ARB_get_texture_sub_image(provider, caps, ext);
        GL_ARB_gl_spirv = check_ARB_gl_spirv(provider, caps, ext);
        GL_ARB_gpu_shader5 = ext.contains("GL_ARB_gpu_shader5");
        GL_ARB_gpu_shader_fp64 = check_ARB_gpu_shader_fp64(provider, caps, ext);
        GL_ARB_gpu_shader_int64 = check_ARB_gpu_shader_int64(provider, caps, ext);
        GL_ARB_half_float_pixel = ext.contains("GL_ARB_half_float_pixel");
        GL_ARB_half_float_vertex = ext.contains("GL_ARB_half_float_vertex");
        GL_ARB_imaging = check_ARB_imaging(provider, caps, ext, fc);
        GL_ARB_indirect_parameters = check_ARB_indirect_parameters(provider, caps, ext);
        GL_ARB_instanced_arrays = check_ARB_instanced_arrays(provider, caps, ext);
        GL_ARB_internalformat_query = check_ARB_internalformat_query(provider, caps, ext);
        GL_ARB_internalformat_query2 = check_ARB_internalformat_query2(provider, caps, ext);
        GL_ARB_invalidate_subdata = check_ARB_invalidate_subdata(provider, caps, ext);
        GL_ARB_map_buffer_alignment = ext.contains("GL_ARB_map_buffer_alignment");
        GL_ARB_map_buffer_range = check_ARB_map_buffer_range(provider, caps, ext);
        GL_ARB_matrix_palette = check_ARB_matrix_palette(provider, caps, ext);
        GL_ARB_multi_bind = check_ARB_multi_bind(provider, caps, ext);
        GL_ARB_multi_draw_indirect = check_ARB_multi_draw_indirect(provider, caps, ext);
        GL_ARB_multisample = check_ARB_multisample(provider, caps, ext);
        GL_ARB_multitexture = check_ARB_multitexture(provider, caps, ext);
        GL_ARB_occlusion_query = check_ARB_occlusion_query(provider, caps, ext);
        GL_ARB_occlusion_query2 = ext.contains("GL_ARB_occlusion_query2");
        GL_ARB_parallel_shader_compile = check_ARB_parallel_shader_compile(provider, caps, ext);
        GL_ARB_pipeline_statistics_query = ext.contains("GL_ARB_pipeline_statistics_query");
        GL_ARB_pixel_buffer_object = ext.contains("GL_ARB_pixel_buffer_object");
        GL_ARB_point_parameters = check_ARB_point_parameters(provider, caps, ext);
        GL_ARB_point_sprite = ext.contains("GL_ARB_point_sprite");
        GL_ARB_polygon_offset_clamp = check_ARB_polygon_offset_clamp(provider, caps, ext);
        GL_ARB_post_depth_coverage = ext.contains("GL_ARB_post_depth_coverage");
        GL_ARB_program_interface_query = check_ARB_program_interface_query(provider, caps, ext);
        GL_ARB_provoking_vertex = check_ARB_provoking_vertex(provider, caps, ext);
        GL_ARB_query_buffer_object = ext.contains("GL_ARB_query_buffer_object");
        GL_ARB_robust_buffer_access_behavior = ext.contains("GL_ARB_robust_buffer_access_behavior");
        GL_ARB_robustness = check_ARB_robustness(provider, caps, ext);
        GL_ARB_robustness_application_isolation = ext.contains("GL_ARB_robustness_application_isolation");
        GL_ARB_robustness_share_group_isolation = ext.contains("GL_ARB_robustness_share_group_isolation");
        GL_ARB_sample_locations = check_ARB_sample_locations(provider, caps, ext);
        GL_ARB_sample_shading = check_ARB_sample_shading(provider, caps, ext);
        GL_ARB_sampler_objects = check_ARB_sampler_objects(provider, caps, ext);
        GL_ARB_seamless_cube_map = ext.contains("GL_ARB_seamless_cube_map");
        GL_ARB_seamless_cubemap_per_texture = ext.contains("GL_ARB_seamless_cubemap_per_texture");
        GL_ARB_separate_shader_objects = check_ARB_separate_shader_objects(provider, caps, ext);
        GL_ARB_shader_atomic_counter_ops = ext.contains("GL_ARB_shader_atomic_counter_ops");
        GL_ARB_shader_atomic_counters = check_ARB_shader_atomic_counters(provider, caps, ext);
        GL_ARB_shader_ballot = ext.contains("GL_ARB_shader_ballot");
        GL_ARB_shader_bit_encoding = ext.contains("GL_ARB_shader_bit_encoding");
        GL_ARB_shader_clock = ext.contains("GL_ARB_shader_clock");
        GL_ARB_shader_draw_parameters = ext.contains("GL_ARB_shader_draw_parameters");
        GL_ARB_shader_group_vote = ext.contains("GL_ARB_shader_group_vote");
        GL_ARB_shader_image_load_store = check_ARB_shader_image_load_store(provider, caps, ext);
        GL_ARB_shader_image_size = ext.contains("GL_ARB_shader_image_size");
        GL_ARB_shader_objects = check_ARB_shader_objects(provider, caps, ext);
        GL_ARB_shader_precision = ext.contains("GL_ARB_shader_precision");
        GL_ARB_shader_stencil_export = ext.contains("GL_ARB_shader_stencil_export");
        GL_ARB_shader_storage_buffer_object = check_ARB_shader_storage_buffer_object(provider, caps, ext);
        GL_ARB_shader_subroutine = check_ARB_shader_subroutine(provider, caps, ext);
        GL_ARB_shader_texture_image_samples = ext.contains("GL_ARB_shader_texture_image_samples");
        GL_ARB_shader_texture_lod = ext.contains("GL_ARB_shader_texture_lod");
        GL_ARB_shader_viewport_layer_array = ext.contains("GL_ARB_shader_viewport_layer_array");
        GL_ARB_shading_language_100 = ext.contains("GL_ARB_shading_language_100");
        GL_ARB_shading_language_420pack = ext.contains("GL_ARB_shading_language_420pack");
        GL_ARB_shading_language_include = check_ARB_shading_language_include(provider, caps, ext);
        GL_ARB_shading_language_packing = ext.contains("GL_ARB_shading_language_packing");
        GL_ARB_shadow = ext.contains("GL_ARB_shadow");
        GL_ARB_shadow_ambient = ext.contains("GL_ARB_shadow_ambient");
        GL_ARB_sparse_buffer = check_ARB_sparse_buffer(provider, caps, ext);
        GL_ARB_sparse_texture = check_ARB_sparse_texture(provider, caps, ext);
        GL_ARB_sparse_texture2 = ext.contains("GL_ARB_sparse_texture2");
        GL_ARB_sparse_texture_clamp = ext.contains("GL_ARB_sparse_texture_clamp");
        GL_ARB_spirv_extensions = ext.contains("GL_ARB_spirv_extensions");
        GL_ARB_stencil_texturing = ext.contains("GL_ARB_stencil_texturing");
        GL_ARB_sync = check_ARB_sync(provider, caps, ext);
        GL_ARB_tessellation_shader = check_ARB_tessellation_shader(provider, caps, ext);
        GL_ARB_texture_barrier = check_ARB_texture_barrier(provider, caps, ext);
        GL_ARB_texture_border_clamp = ext.contains("GL_ARB_texture_border_clamp");
        GL_ARB_texture_buffer_object = check_ARB_texture_buffer_object(provider, caps, ext);
        GL_ARB_texture_buffer_object_rgb32 = ext.contains("GL_ARB_texture_buffer_object_rgb32");
        GL_ARB_texture_buffer_range = check_ARB_texture_buffer_range(provider, caps, ext);
        GL_ARB_texture_compression = check_ARB_texture_compression(provider, caps, ext);
        GL_ARB_texture_compression_bptc = ext.contains("GL_ARB_texture_compression_bptc");
        GL_ARB_texture_compression_rgtc = ext.contains("GL_ARB_texture_compression_rgtc");
        GL_ARB_texture_cube_map = ext.contains("GL_ARB_texture_cube_map");
        GL_ARB_texture_cube_map_array = ext.contains("GL_ARB_texture_cube_map_array");
        GL_ARB_texture_env_add = ext.contains("GL_ARB_texture_env_add");
        GL_ARB_texture_env_combine = ext.contains("GL_ARB_texture_env_combine");
        GL_ARB_texture_env_crossbar = ext.contains("GL_ARB_texture_env_crossbar");
        GL_ARB_texture_env_dot3 = ext.contains("GL_ARB_texture_env_dot3");
        GL_ARB_texture_filter_anisotropic = ext.contains("GL_ARB_texture_filter_anisotropic");
        GL_ARB_texture_filter_minmax = ext.contains("GL_ARB_texture_filter_minmax");
        GL_ARB_texture_float = ext.contains("GL_ARB_texture_float");
        GL_ARB_texture_gather = ext.contains("GL_ARB_texture_gather");
        GL_ARB_texture_mirror_clamp_to_edge = ext.contains("GL_ARB_texture_mirror_clamp_to_edge");
        GL_ARB_texture_mirrored_repeat = ext.contains("GL_ARB_texture_mirrored_repeat");
        GL_ARB_texture_multisample = check_ARB_texture_multisample(provider, caps, ext);
        GL_ARB_texture_non_power_of_two = ext.contains("GL_ARB_texture_non_power_of_two");
        GL_ARB_texture_query_levels = ext.contains("GL_ARB_texture_query_levels");
        GL_ARB_texture_query_lod = ext.contains("GL_ARB_texture_query_lod");
        GL_ARB_texture_rectangle = ext.contains("GL_ARB_texture_rectangle");
        GL_ARB_texture_rg = ext.contains("GL_ARB_texture_rg");
        GL_ARB_texture_rgb10_a2ui = ext.contains("GL_ARB_texture_rgb10_a2ui");
        GL_ARB_texture_stencil8 = ext.contains("GL_ARB_texture_stencil8");
        GL_ARB_texture_storage = check_ARB_texture_storage(provider, caps, ext);
        GL_ARB_texture_storage_multisample = check_ARB_texture_storage_multisample(provider, caps, ext);
        GL_ARB_texture_swizzle = ext.contains("GL_ARB_texture_swizzle");
        GL_ARB_texture_view = check_ARB_texture_view(provider, caps, ext);
        GL_ARB_timer_query = check_ARB_timer_query(provider, caps, ext);
        GL_ARB_transform_feedback2 = check_ARB_transform_feedback2(provider, caps, ext);
        GL_ARB_transform_feedback3 = check_ARB_transform_feedback3(provider, caps, ext);
        GL_ARB_transform_feedback_instanced = check_ARB_transform_feedback_instanced(provider, caps, ext);
        GL_ARB_transform_feedback_overflow_query = ext.contains("GL_ARB_transform_feedback_overflow_query");
        GL_ARB_transpose_matrix = check_ARB_transpose_matrix(provider, caps, ext);
        GL_ARB_uniform_buffer_object = check_ARB_uniform_buffer_object(provider, caps, ext);
        GL_ARB_vertex_array_bgra = ext.contains("GL_ARB_vertex_array_bgra");
        GL_ARB_vertex_array_object = check_ARB_vertex_array_object(provider, caps, ext);
        GL_ARB_vertex_attrib_64bit = check_ARB_vertex_attrib_64bit(provider, caps, ext);
        GL_ARB_vertex_attrib_binding = check_ARB_vertex_attrib_binding(provider, caps, ext);
        GL_ARB_vertex_blend = check_ARB_vertex_blend(provider, caps, ext);
        GL_ARB_vertex_buffer_object = check_ARB_vertex_buffer_object(provider, caps, ext);
        GL_ARB_vertex_program = check_ARB_vertex_program(provider, caps, ext);
        GL_ARB_vertex_shader = check_ARB_vertex_shader(provider, caps, ext);
        GL_ARB_vertex_type_10f_11f_11f_rev = ext.contains("GL_ARB_vertex_type_10f_11f_11f_rev");
        GL_ARB_vertex_type_2_10_10_10_rev = check_ARB_vertex_type_2_10_10_10_rev(provider, caps, ext, fc);
        GL_ARB_viewport_array = check_ARB_viewport_array(provider, caps, ext);
        GL_ARB_window_pos = check_ARB_window_pos(provider, caps, ext);
        GL_ATI_meminfo = ext.contains("GL_ATI_meminfo");
        GL_ATI_shader_texture_lod = ext.contains("GL_ATI_shader_texture_lod");
        GL_ATI_texture_compression_3dc = ext.contains("GL_ATI_texture_compression_3dc");
        GL_EXT_422_pixels = ext.contains("GL_EXT_422_pixels");
        GL_EXT_abgr = ext.contains("GL_EXT_abgr");
        GL_EXT_bgra = ext.contains("GL_EXT_bgra");
        GL_EXT_bindable_uniform = check_EXT_bindable_uniform(provider, caps, ext);
        GL_EXT_blend_color = check_EXT_blend_color(provider, caps, ext);
        GL_EXT_blend_equation_separate = check_EXT_blend_equation_separate(provider, caps, ext);
        GL_EXT_blend_func_separate = check_EXT_blend_func_separate(provider, caps, ext);
        GL_EXT_blend_minmax = check_EXT_blend_minmax(provider, caps, ext);
        GL_EXT_blend_subtract = ext.contains("GL_EXT_blend_subtract");
        GL_EXT_clip_volume_hint = ext.contains("GL_EXT_clip_volume_hint");
        GL_EXT_compiled_vertex_array = check_EXT_compiled_vertex_array(provider, caps, ext);
        GL_EXT_debug_label = check_EXT_debug_label(provider, caps, ext);
        GL_EXT_debug_marker = check_EXT_debug_marker(provider, caps, ext);
        GL_EXT_depth_bounds_test = check_EXT_depth_bounds_test(provider, caps, ext);
        GL_EXT_direct_state_access = check_EXT_direct_state_access(provider, caps, ext);
        GL_EXT_draw_buffers2 = check_EXT_draw_buffers2(provider, caps, ext);
        GL_EXT_draw_instanced = check_EXT_draw_instanced(provider, caps, ext);
        GL_EXT_EGL_image_storage = check_EXT_EGL_image_storage(provider, caps, ext);
        GL_EXT_EGL_sync = ext.contains("GL_EXT_EGL_sync");
        GL_EXT_external_buffer = check_EXT_external_buffer(provider, caps, ext);
        GL_EXT_framebuffer_blit = check_EXT_framebuffer_blit(provider, caps, ext);
        GL_EXT_framebuffer_multisample = check_EXT_framebuffer_multisample(provider, caps, ext);
        GL_EXT_framebuffer_multisample_blit_scaled = ext.contains("GL_EXT_framebuffer_multisample_blit_scaled");
        GL_EXT_framebuffer_object = check_EXT_framebuffer_object(provider, caps, ext);
        GL_EXT_framebuffer_sRGB = ext.contains("GL_EXT_framebuffer_sRGB");
        GL_EXT_geometry_shader4 = check_EXT_geometry_shader4(provider, caps, ext);
        GL_EXT_gpu_program_parameters = check_EXT_gpu_program_parameters(provider, caps, ext);
        GL_EXT_gpu_shader4 = check_EXT_gpu_shader4(provider, caps, ext);
        GL_EXT_memory_object = check_EXT_memory_object(provider, caps, ext);
        GL_EXT_memory_object_fd = check_EXT_memory_object_fd(provider, caps, ext);
        GL_EXT_memory_object_win32 = check_EXT_memory_object_win32(provider, caps, ext);
        GL_EXT_multiview_tessellation_geometry_shader = ext.contains("GL_EXT_multiview_tessellation_geometry_shader");
        GL_EXT_multiview_texture_multisample = ext.contains("GL_EXT_multiview_texture_multisample");
        GL_EXT_multiview_timer_query = ext.contains("GL_EXT_multiview_timer_query");
        GL_EXT_packed_depth_stencil = ext.contains("GL_EXT_packed_depth_stencil");
        GL_EXT_packed_float = ext.contains("GL_EXT_packed_float");
        GL_EXT_pixel_buffer_object = ext.contains("GL_EXT_pixel_buffer_object");
        GL_EXT_point_parameters = check_EXT_point_parameters(provider, caps, ext);
        GL_EXT_polygon_offset_clamp = check_EXT_polygon_offset_clamp(provider, caps, ext);
        GL_EXT_post_depth_coverage = ext.contains("GL_EXT_post_depth_coverage");
        GL_EXT_provoking_vertex = check_EXT_provoking_vertex(provider, caps, ext);
        GL_EXT_raster_multisample = check_EXT_raster_multisample(provider, caps, ext);
        GL_EXT_secondary_color = check_EXT_secondary_color(provider, caps, ext);
        GL_EXT_semaphore = check_EXT_semaphore(provider, caps, ext);
        GL_EXT_semaphore_fd = check_EXT_semaphore_fd(provider, caps, ext);
        GL_EXT_semaphore_win32 = check_EXT_semaphore_win32(provider, caps, ext);
        GL_EXT_separate_shader_objects = check_EXT_separate_shader_objects(provider, caps, ext);
        GL_EXT_shader_framebuffer_fetch = ext.contains("GL_EXT_shader_framebuffer_fetch");
        GL_EXT_shader_framebuffer_fetch_non_coherent = check_EXT_shader_framebuffer_fetch_non_coherent(provider, caps, ext);
        GL_EXT_shader_image_load_formatted = ext.contains("GL_EXT_shader_image_load_formatted");
        GL_EXT_shader_image_load_store = check_EXT_shader_image_load_store(provider, caps, ext);
        GL_EXT_shader_integer_mix = ext.contains("GL_EXT_shader_integer_mix");
        GL_EXT_shadow_funcs = ext.contains("GL_EXT_shadow_funcs");
        GL_EXT_shared_texture_palette = ext.contains("GL_EXT_shared_texture_palette");
        GL_EXT_sparse_texture2 = ext.contains("GL_EXT_sparse_texture2");
        GL_EXT_stencil_clear_tag = check_EXT_stencil_clear_tag(provider, caps, ext);
        GL_EXT_stencil_two_side = check_EXT_stencil_two_side(provider, caps, ext);
        GL_EXT_stencil_wrap = ext.contains("GL_EXT_stencil_wrap");
        GL_EXT_texture_array = check_EXT_texture_array(provider, caps, ext);
        GL_EXT_texture_buffer_object = check_EXT_texture_buffer_object(provider, caps, ext);
        GL_EXT_texture_compression_latc = ext.contains("GL_EXT_texture_compression_latc");
        GL_EXT_texture_compression_rgtc = ext.contains("GL_EXT_texture_compression_rgtc");
        GL_EXT_texture_compression_s3tc = ext.contains("GL_EXT_texture_compression_s3tc");
        GL_EXT_texture_filter_anisotropic = ext.contains("GL_EXT_texture_filter_anisotropic");
        GL_EXT_texture_filter_minmax = ext.contains("GL_EXT_texture_filter_minmax");
        GL_EXT_texture_integer = check_EXT_texture_integer(provider, caps, ext);
        GL_EXT_texture_mirror_clamp = ext.contains("GL_EXT_texture_mirror_clamp");
        GL_EXT_texture_shadow_lod = ext.contains("GL_EXT_texture_shadow_lod");
        GL_EXT_texture_shared_exponent = ext.contains("GL_EXT_texture_shared_exponent");
        GL_EXT_texture_snorm = ext.contains("GL_EXT_texture_snorm");
        GL_EXT_texture_sRGB = ext.contains("GL_EXT_texture_sRGB");
        GL_EXT_texture_sRGB_decode = ext.contains("GL_EXT_texture_sRGB_decode");
        GL_EXT_texture_sRGB_R8 = ext.contains("GL_EXT_texture_sRGB_R8");
        GL_EXT_texture_sRGB_RG8 = ext.contains("GL_EXT_texture_sRGB_RG8");
        GL_EXT_texture_storage = check_EXT_texture_storage(provider, caps, ext);
        GL_EXT_texture_swizzle = ext.contains("GL_EXT_texture_swizzle");
        GL_EXT_timer_query = check_EXT_timer_query(provider, caps, ext);
        GL_EXT_transform_feedback = check_EXT_transform_feedback(provider, caps, ext);
        GL_EXT_vertex_array_bgra = ext.contains("GL_EXT_vertex_array_bgra");
        GL_EXT_vertex_attrib_64bit = check_EXT_vertex_attrib_64bit(provider, caps, ext);
        GL_EXT_win32_keyed_mutex = check_EXT_win32_keyed_mutex(provider, caps, ext);
        GL_EXT_window_rectangles = check_EXT_window_rectangles(provider, caps, ext);
        GL_EXT_x11_sync_object = check_EXT_x11_sync_object(provider, caps, ext);
        GL_GREMEDY_frame_terminator = check_GREMEDY_frame_terminator(provider, caps, ext);
        GL_GREMEDY_string_marker = check_GREMEDY_string_marker(provider, caps, ext);
        GL_INTEL_blackhole_render = ext.contains("GL_INTEL_blackhole_render");
        GL_INTEL_conservative_rasterization = ext.contains("GL_INTEL_conservative_rasterization");
        GL_INTEL_fragment_shader_ordering = ext.contains("GL_INTEL_fragment_shader_ordering");
        GL_INTEL_framebuffer_CMAA = check_INTEL_framebuffer_CMAA(provider, caps, ext);
        GL_INTEL_map_texture = check_INTEL_map_texture(provider, caps, ext);
        GL_INTEL_performance_query = check_INTEL_performance_query(provider, caps, ext);
        GL_INTEL_shader_integer_functions2 = ext.contains("GL_INTEL_shader_integer_functions2");
        GL_KHR_blend_equation_advanced = check_KHR_blend_equation_advanced(provider, caps, ext);
        GL_KHR_blend_equation_advanced_coherent = ext.contains("GL_KHR_blend_equation_advanced_coherent");
        GL_KHR_context_flush_control = ext.contains("GL_KHR_context_flush_control");
        GL_KHR_debug = check_KHR_debug(provider, caps, ext);
        GL_KHR_no_error = ext.contains("GL_KHR_no_error");
        GL_KHR_parallel_shader_compile = check_KHR_parallel_shader_compile(provider, caps, ext);
        GL_KHR_robust_buffer_access_behavior = ext.contains("GL_KHR_robust_buffer_access_behavior");
        GL_KHR_robustness = check_KHR_robustness(provider, caps, ext);
        GL_KHR_shader_subgroup = ext.contains("GL_KHR_shader_subgroup");
        GL_KHR_texture_compression_astc_hdr = ext.contains("GL_KHR_texture_compression_astc_hdr");
        GL_KHR_texture_compression_astc_ldr = ext.contains("GL_KHR_texture_compression_astc_ldr");
        GL_KHR_texture_compression_astc_sliced_3d = ext.contains("GL_KHR_texture_compression_astc_sliced_3d");
        GL_MESA_framebuffer_flip_x = ext.contains("GL_MESA_framebuffer_flip_x");
        GL_MESA_framebuffer_flip_y = check_MESA_framebuffer_flip_y(provider, caps, ext);
        GL_MESA_framebuffer_swap_xy = ext.contains("GL_MESA_framebuffer_swap_xy");
        GL_MESA_tile_raster_order = ext.contains("GL_MESA_tile_raster_order");
        GL_NV_alpha_to_coverage_dither_control = check_NV_alpha_to_coverage_dither_control(provider, caps, ext);
        GL_NV_bindless_multi_draw_indirect = check_NV_bindless_multi_draw_indirect(provider, caps, ext);
        GL_NV_bindless_multi_draw_indirect_count = check_NV_bindless_multi_draw_indirect_count(provider, caps, ext);
        GL_NV_bindless_texture = check_NV_bindless_texture(provider, caps, ext);
        GL_NV_blend_equation_advanced = check_NV_blend_equation_advanced(provider, caps, ext);
        GL_NV_blend_equation_advanced_coherent = ext.contains("GL_NV_blend_equation_advanced_coherent");
        GL_NV_blend_minmax_factor = ext.contains("GL_NV_blend_minmax_factor");
        GL_NV_blend_square = ext.contains("GL_NV_blend_square");
        GL_NV_clip_space_w_scaling = check_NV_clip_space_w_scaling(provider, caps, ext);
        GL_NV_command_list = check_NV_command_list(provider, caps, ext);
        GL_NV_compute_shader_derivatives = ext.contains("GL_NV_compute_shader_derivatives");
        GL_NV_conditional_render = check_NV_conditional_render(provider, caps, ext);
        GL_NV_conservative_raster = check_NV_conservative_raster(provider, caps, ext);
        GL_NV_conservative_raster_dilate = check_NV_conservative_raster_dilate(provider, caps, ext);
        GL_NV_conservative_raster_pre_snap = ext.contains("GL_NV_conservative_raster_pre_snap");
        GL_NV_conservative_raster_pre_snap_triangles = check_NV_conservative_raster_pre_snap_triangles(provider, caps, ext);
        GL_NV_conservative_raster_underestimation = ext.contains("GL_NV_conservative_raster_underestimation");
        GL_NV_copy_depth_to_color = ext.contains("GL_NV_copy_depth_to_color");
        GL_NV_copy_image = check_NV_copy_image(provider, caps, ext);
        GL_NV_deep_texture3D = ext.contains("GL_NV_deep_texture3D");
        GL_NV_depth_buffer_float = check_NV_depth_buffer_float(provider, caps, ext);
        GL_NV_depth_clamp = ext.contains("GL_NV_depth_clamp");
        GL_NV_draw_texture = check_NV_draw_texture(provider, caps, ext);
        GL_NV_draw_vulkan_image = check_NV_draw_vulkan_image(provider, caps, ext);
        GL_NV_ES3_1_compatibility = ext.contains("GL_NV_ES3_1_compatibility");
        GL_NV_explicit_multisample = check_NV_explicit_multisample(provider, caps, ext);
        GL_NV_fence = check_NV_fence(provider, caps, ext);
        GL_NV_fill_rectangle = ext.contains("GL_NV_fill_rectangle");
        GL_NV_float_buffer = ext.contains("GL_NV_float_buffer");
        GL_NV_fog_distance = ext.contains("GL_NV_fog_distance");
        GL_NV_fragment_coverage_to_color = check_NV_fragment_coverage_to_color(provider, caps, ext);
        GL_NV_fragment_program4 = ext.contains("GL_NV_fragment_program4");
        GL_NV_fragment_program_option = ext.contains("GL_NV_fragment_program_option");
        GL_NV_fragment_shader_barycentric = ext.contains("GL_NV_fragment_shader_barycentric");
        GL_NV_fragment_shader_interlock = ext.contains("GL_NV_fragment_shader_interlock");
        GL_NV_framebuffer_mixed_samples = check_NV_framebuffer_mixed_samples(provider, caps, ext);
        GL_NV_framebuffer_multisample_coverage = check_NV_framebuffer_multisample_coverage(provider, caps, ext);
        GL_NV_geometry_shader4 = ext.contains("GL_NV_geometry_shader4");
        GL_NV_geometry_shader_passthrough = ext.contains("GL_NV_geometry_shader_passthrough");
        GL_NV_gpu_multicast = check_NV_gpu_multicast(provider, caps, ext);
        GL_NV_gpu_shader5 = check_NV_gpu_shader5(provider, caps, ext);
        GL_NV_half_float = check_NV_half_float(provider, caps, ext);
        GL_NV_internalformat_sample_query = check_NV_internalformat_sample_query(provider, caps, ext);
        GL_NV_light_max_exponent = ext.contains("GL_NV_light_max_exponent");
        GL_NV_memory_attachment = check_NV_memory_attachment(provider, caps, ext);
        GL_NV_memory_object_sparse = check_NV_memory_object_sparse(provider, caps, ext);
        GL_NV_mesh_shader = check_NV_mesh_shader(provider, caps, ext);
        GL_NV_multisample_coverage = ext.contains("GL_NV_multisample_coverage");
        GL_NV_multisample_filter_hint = ext.contains("GL_NV_multisample_filter_hint");
        GL_NV_packed_depth_stencil = ext.contains("GL_NV_packed_depth_stencil");
        GL_NV_path_rendering = check_NV_path_rendering(provider, caps, ext);
        GL_NV_path_rendering_shared_edge = ext.contains("GL_NV_path_rendering_shared_edge");
        GL_NV_pixel_data_range = check_NV_pixel_data_range(provider, caps, ext);
        GL_NV_point_sprite = check_NV_point_sprite(provider, caps, ext);
        GL_NV_primitive_restart = check_NV_primitive_restart(provider, caps, ext);
        GL_NV_primitive_shading_rate = ext.contains("GL_NV_primitive_shading_rate");
        GL_NV_query_resource = check_NV_query_resource(provider, caps, ext);
        GL_NV_query_resource_tag = check_NV_query_resource_tag(provider, caps, ext);
        GL_NV_representative_fragment_test = ext.contains("GL_NV_representative_fragment_test");
        GL_NV_robustness_video_memory_purge = ext.contains("GL_NV_robustness_video_memory_purge");
        GL_NV_sample_locations = check_NV_sample_locations(provider, caps, ext);
        GL_NV_sample_mask_override_coverage = ext.contains("GL_NV_sample_mask_override_coverage");
        GL_NV_scissor_exclusive = check_NV_scissor_exclusive(provider, caps, ext);
        GL_NV_shader_atomic_float = ext.contains("GL_NV_shader_atomic_float");
        GL_NV_shader_atomic_float64 = ext.contains("GL_NV_shader_atomic_float64");
        GL_NV_shader_atomic_fp16_vector = ext.contains("GL_NV_shader_atomic_fp16_vector");
        GL_NV_shader_atomic_int64 = ext.contains("GL_NV_shader_atomic_int64");
        GL_NV_shader_buffer_load = check_NV_shader_buffer_load(provider, caps, ext);
        GL_NV_shader_buffer_store = ext.contains("GL_NV_shader_buffer_store");
        GL_NV_shader_subgroup_partitioned = ext.contains("GL_NV_shader_subgroup_partitioned");
        GL_NV_shader_texture_footprint = ext.contains("GL_NV_shader_texture_footprint");
        GL_NV_shader_thread_group = ext.contains("GL_NV_shader_thread_group");
        GL_NV_shader_thread_shuffle = ext.contains("GL_NV_shader_thread_shuffle");
        GL_NV_shading_rate_image = check_NV_shading_rate_image(provider, caps, ext);
        GL_NV_stereo_view_rendering = ext.contains("GL_NV_stereo_view_rendering");
        GL_NV_texgen_reflection = ext.contains("GL_NV_texgen_reflection");
        GL_NV_texture_barrier = check_NV_texture_barrier(provider, caps, ext);
        GL_NV_texture_compression_vtc = ext.contains("GL_NV_texture_compression_vtc");
        GL_NV_texture_multisample = check_NV_texture_multisample(provider, caps, ext);
        GL_NV_texture_rectangle_compressed = ext.contains("GL_NV_texture_rectangle_compressed");
        GL_NV_texture_shader = ext.contains("GL_NV_texture_shader");
        GL_NV_texture_shader2 = ext.contains("GL_NV_texture_shader2");
        GL_NV_texture_shader3 = ext.contains("GL_NV_texture_shader3");
        GL_NV_timeline_semaphore = check_NV_timeline_semaphore(provider, caps, ext);
        GL_NV_transform_feedback = check_NV_transform_feedback(provider, caps, ext);
        GL_NV_transform_feedback2 = check_NV_transform_feedback2(provider, caps, ext);
        GL_NV_uniform_buffer_unified_memory = ext.contains("GL_NV_uniform_buffer_unified_memory");
        GL_NV_vertex_array_range = check_NV_vertex_array_range(provider, caps, ext);
        GL_NV_vertex_array_range2 = ext.contains("GL_NV_vertex_array_range2");
        GL_NV_vertex_attrib_integer_64bit = check_NV_vertex_attrib_integer_64bit(provider, caps, ext);
        GL_NV_vertex_buffer_unified_memory = check_NV_vertex_buffer_unified_memory(provider, caps, ext);
        GL_NV_viewport_array2 = ext.contains("GL_NV_viewport_array2");
        GL_NV_viewport_swizzle = check_NV_viewport_swizzle(provider, caps, ext);
        GL_NVX_blend_equation_advanced_multi_draw_buffers = ext.contains("GL_NVX_blend_equation_advanced_multi_draw_buffers");
        GL_NVX_conditional_render = check_NVX_conditional_render(provider, caps, ext);
        GL_NVX_gpu_memory_info = ext.contains("GL_NVX_gpu_memory_info");
        GL_NVX_gpu_multicast2 = check_NVX_gpu_multicast2(provider, caps, ext);
        GL_NVX_progress_fence = check_NVX_progress_fence(provider, caps, ext);
        GL_OVR_multiview = check_OVR_multiview(provider, caps, ext);
        GL_OVR_multiview2 = ext.contains("GL_OVR_multiview2");
        GL_S3_s3tc = ext.contains("GL_S3_s3tc");

        glEnable = caps.get(0);
        glDisable = caps.get(1);
        glAccum = caps.get(2);
        glAlphaFunc = caps.get(3);
        glAreTexturesResident = caps.get(4);
        glArrayElement = caps.get(5);
        glBegin = caps.get(6);
        glBindTexture = caps.get(7);
        glBitmap = caps.get(8);
        glBlendFunc = caps.get(9);
        glCallList = caps.get(10);
        glCallLists = caps.get(11);
        glClear = caps.get(12);
        glClearAccum = caps.get(13);
        glClearColor = caps.get(14);
        glClearDepth = caps.get(15);
        glClearIndex = caps.get(16);
        glClearStencil = caps.get(17);
        glClipPlane = caps.get(18);
        glColor3b = caps.get(19);
        glColor3s = caps.get(20);
        glColor3i = caps.get(21);
        glColor3f = caps.get(22);
        glColor3d = caps.get(23);
        glColor3ub = caps.get(24);
        glColor3us = caps.get(25);
        glColor3ui = caps.get(26);
        glColor3bv = caps.get(27);
        glColor3sv = caps.get(28);
        glColor3iv = caps.get(29);
        glColor3fv = caps.get(30);
        glColor3dv = caps.get(31);
        glColor3ubv = caps.get(32);
        glColor3usv = caps.get(33);
        glColor3uiv = caps.get(34);
        glColor4b = caps.get(35);
        glColor4s = caps.get(36);
        glColor4i = caps.get(37);
        glColor4f = caps.get(38);
        glColor4d = caps.get(39);
        glColor4ub = caps.get(40);
        glColor4us = caps.get(41);
        glColor4ui = caps.get(42);
        glColor4bv = caps.get(43);
        glColor4sv = caps.get(44);
        glColor4iv = caps.get(45);
        glColor4fv = caps.get(46);
        glColor4dv = caps.get(47);
        glColor4ubv = caps.get(48);
        glColor4usv = caps.get(49);
        glColor4uiv = caps.get(50);
        glColorMask = caps.get(51);
        glColorMaterial = caps.get(52);
        glColorPointer = caps.get(53);
        glCopyPixels = caps.get(54);
        glCullFace = caps.get(55);
        glDeleteLists = caps.get(56);
        glDepthFunc = caps.get(57);
        glDepthMask = caps.get(58);
        glDepthRange = caps.get(59);
        glDisableClientState = caps.get(60);
        glDrawArrays = caps.get(61);
        glDrawBuffer = caps.get(62);
        glDrawElements = caps.get(63);
        glDrawPixels = caps.get(64);
        glEdgeFlag = caps.get(65);
        glEdgeFlagv = caps.get(66);
        glEdgeFlagPointer = caps.get(67);
        glEnableClientState = caps.get(68);
        glEnd = caps.get(69);
        glEvalCoord1f = caps.get(70);
        glEvalCoord1fv = caps.get(71);
        glEvalCoord1d = caps.get(72);
        glEvalCoord1dv = caps.get(73);
        glEvalCoord2f = caps.get(74);
        glEvalCoord2fv = caps.get(75);
        glEvalCoord2d = caps.get(76);
        glEvalCoord2dv = caps.get(77);
        glEvalMesh1 = caps.get(78);
        glEvalMesh2 = caps.get(79);
        glEvalPoint1 = caps.get(80);
        glEvalPoint2 = caps.get(81);
        glFeedbackBuffer = caps.get(82);
        glFinish = caps.get(83);
        glFlush = caps.get(84);
        glFogi = caps.get(85);
        glFogiv = caps.get(86);
        glFogf = caps.get(87);
        glFogfv = caps.get(88);
        glFrontFace = caps.get(89);
        glGenLists = caps.get(90);
        glGenTextures = caps.get(91);
        glDeleteTextures = caps.get(92);
        glGetClipPlane = caps.get(93);
        glGetBooleanv = caps.get(94);
        glGetFloatv = caps.get(95);
        glGetIntegerv = caps.get(96);
        glGetDoublev = caps.get(97);
        glGetError = caps.get(98);
        glGetLightiv = caps.get(99);
        glGetLightfv = caps.get(100);
        glGetMapiv = caps.get(101);
        glGetMapfv = caps.get(102);
        glGetMapdv = caps.get(103);
        glGetMaterialiv = caps.get(104);
        glGetMaterialfv = caps.get(105);
        glGetPixelMapfv = caps.get(106);
        glGetPixelMapusv = caps.get(107);
        glGetPixelMapuiv = caps.get(108);
        glGetPointerv = caps.get(109);
        glGetPolygonStipple = caps.get(110);
        glGetString = caps.get(111);
        glGetTexEnviv = caps.get(112);
        glGetTexEnvfv = caps.get(113);
        glGetTexGeniv = caps.get(114);
        glGetTexGenfv = caps.get(115);
        glGetTexGendv = caps.get(116);
        glGetTexImage = caps.get(117);
        glGetTexLevelParameteriv = caps.get(118);
        glGetTexLevelParameterfv = caps.get(119);
        glGetTexParameteriv = caps.get(120);
        glGetTexParameterfv = caps.get(121);
        glHint = caps.get(122);
        glIndexi = caps.get(123);
        glIndexub = caps.get(124);
        glIndexs = caps.get(125);
        glIndexf = caps.get(126);
        glIndexd = caps.get(127);
        glIndexiv = caps.get(128);
        glIndexubv = caps.get(129);
        glIndexsv = caps.get(130);
        glIndexfv = caps.get(131);
        glIndexdv = caps.get(132);
        glIndexMask = caps.get(133);
        glIndexPointer = caps.get(134);
        glInitNames = caps.get(135);
        glInterleavedArrays = caps.get(136);
        glIsEnabled = caps.get(137);
        glIsList = caps.get(138);
        glIsTexture = caps.get(139);
        glLightModeli = caps.get(140);
        glLightModelf = caps.get(141);
        glLightModeliv = caps.get(142);
        glLightModelfv = caps.get(143);
        glLighti = caps.get(144);
        glLightf = caps.get(145);
        glLightiv = caps.get(146);
        glLightfv = caps.get(147);
        glLineStipple = caps.get(148);
        glLineWidth = caps.get(149);
        glListBase = caps.get(150);
        glLoadMatrixf = caps.get(151);
        glLoadMatrixd = caps.get(152);
        glLoadIdentity = caps.get(153);
        glLoadName = caps.get(154);
        glLogicOp = caps.get(155);
        glMap1f = caps.get(156);
        glMap1d = caps.get(157);
        glMap2f = caps.get(158);
        glMap2d = caps.get(159);
        glMapGrid1f = caps.get(160);
        glMapGrid1d = caps.get(161);
        glMapGrid2f = caps.get(162);
        glMapGrid2d = caps.get(163);
        glMateriali = caps.get(164);
        glMaterialf = caps.get(165);
        glMaterialiv = caps.get(166);
        glMaterialfv = caps.get(167);
        glMatrixMode = caps.get(168);
        glMultMatrixf = caps.get(169);
        glMultMatrixd = caps.get(170);
        glFrustum = caps.get(171);
        glNewList = caps.get(172);
        glEndList = caps.get(173);
        glNormal3f = caps.get(174);
        glNormal3b = caps.get(175);
        glNormal3s = caps.get(176);
        glNormal3i = caps.get(177);
        glNormal3d = caps.get(178);
        glNormal3fv = caps.get(179);
        glNormal3bv = caps.get(180);
        glNormal3sv = caps.get(181);
        glNormal3iv = caps.get(182);
        glNormal3dv = caps.get(183);
        glNormalPointer = caps.get(184);
        glOrtho = caps.get(185);
        glPassThrough = caps.get(186);
        glPixelMapfv = caps.get(187);
        glPixelMapusv = caps.get(188);
        glPixelMapuiv = caps.get(189);
        glPixelStorei = caps.get(190);
        glPixelStoref = caps.get(191);
        glPixelTransferi = caps.get(192);
        glPixelTransferf = caps.get(193);
        glPixelZoom = caps.get(194);
        glPointSize = caps.get(195);
        glPolygonMode = caps.get(196);
        glPolygonOffset = caps.get(197);
        glPolygonStipple = caps.get(198);
        glPushAttrib = caps.get(199);
        glPushClientAttrib = caps.get(200);
        glPopAttrib = caps.get(201);
        glPopClientAttrib = caps.get(202);
        glPopMatrix = caps.get(203);
        glPopName = caps.get(204);
        glPrioritizeTextures = caps.get(205);
        glPushMatrix = caps.get(206);
        glPushName = caps.get(207);
        glRasterPos2i = caps.get(208);
        glRasterPos2s = caps.get(209);
        glRasterPos2f = caps.get(210);
        glRasterPos2d = caps.get(211);
        glRasterPos2iv = caps.get(212);
        glRasterPos2sv = caps.get(213);
        glRasterPos2fv = caps.get(214);
        glRasterPos2dv = caps.get(215);
        glRasterPos3i = caps.get(216);
        glRasterPos3s = caps.get(217);
        glRasterPos3f = caps.get(218);
        glRasterPos3d = caps.get(219);
        glRasterPos3iv = caps.get(220);
        glRasterPos3sv = caps.get(221);
        glRasterPos3fv = caps.get(222);
        glRasterPos3dv = caps.get(223);
        glRasterPos4i = caps.get(224);
        glRasterPos4s = caps.get(225);
        glRasterPos4f = caps.get(226);
        glRasterPos4d = caps.get(227);
        glRasterPos4iv = caps.get(228);
        glRasterPos4sv = caps.get(229);
        glRasterPos4fv = caps.get(230);
        glRasterPos4dv = caps.get(231);
        glReadBuffer = caps.get(232);
        glReadPixels = caps.get(233);
        glRecti = caps.get(234);
        glRects = caps.get(235);
        glRectf = caps.get(236);
        glRectd = caps.get(237);
        glRectiv = caps.get(238);
        glRectsv = caps.get(239);
        glRectfv = caps.get(240);
        glRectdv = caps.get(241);
        glRenderMode = caps.get(242);
        glRotatef = caps.get(243);
        glRotated = caps.get(244);
        glScalef = caps.get(245);
        glScaled = caps.get(246);
        glScissor = caps.get(247);
        glSelectBuffer = caps.get(248);
        glShadeModel = caps.get(249);
        glStencilFunc = caps.get(250);
        glStencilMask = caps.get(251);
        glStencilOp = caps.get(252);
        glTexCoord1f = caps.get(253);
        glTexCoord1s = caps.get(254);
        glTexCoord1i = caps.get(255);
        glTexCoord1d = caps.get(256);
        glTexCoord1fv = caps.get(257);
        glTexCoord1sv = caps.get(258);
        glTexCoord1iv = caps.get(259);
        glTexCoord1dv = caps.get(260);
        glTexCoord2f = caps.get(261);
        glTexCoord2s = caps.get(262);
        glTexCoord2i = caps.get(263);
        glTexCoord2d = caps.get(264);
        glTexCoord2fv = caps.get(265);
        glTexCoord2sv = caps.get(266);
        glTexCoord2iv = caps.get(267);
        glTexCoord2dv = caps.get(268);
        glTexCoord3f = caps.get(269);
        glTexCoord3s = caps.get(270);
        glTexCoord3i = caps.get(271);
        glTexCoord3d = caps.get(272);
        glTexCoord3fv = caps.get(273);
        glTexCoord3sv = caps.get(274);
        glTexCoord3iv = caps.get(275);
        glTexCoord3dv = caps.get(276);
        glTexCoord4f = caps.get(277);
        glTexCoord4s = caps.get(278);
        glTexCoord4i = caps.get(279);
        glTexCoord4d = caps.get(280);
        glTexCoord4fv = caps.get(281);
        glTexCoord4sv = caps.get(282);
        glTexCoord4iv = caps.get(283);
        glTexCoord4dv = caps.get(284);
        glTexCoordPointer = caps.get(285);
        glTexEnvi = caps.get(286);
        glTexEnviv = caps.get(287);
        glTexEnvf = caps.get(288);
        glTexEnvfv = caps.get(289);
        glTexGeni = caps.get(290);
        glTexGeniv = caps.get(291);
        glTexGenf = caps.get(292);
        glTexGenfv = caps.get(293);
        glTexGend = caps.get(294);
        glTexGendv = caps.get(295);
        glTexImage1D = caps.get(296);
        glTexImage2D = caps.get(297);
        glCopyTexImage1D = caps.get(298);
        glCopyTexImage2D = caps.get(299);
        glCopyTexSubImage1D = caps.get(300);
        glCopyTexSubImage2D = caps.get(301);
        glTexParameteri = caps.get(302);
        glTexParameteriv = caps.get(303);
        glTexParameterf = caps.get(304);
        glTexParameterfv = caps.get(305);
        glTexSubImage1D = caps.get(306);
        glTexSubImage2D = caps.get(307);
        glTranslatef = caps.get(308);
        glTranslated = caps.get(309);
        glVertex2f = caps.get(310);
        glVertex2s = caps.get(311);
        glVertex2i = caps.get(312);
        glVertex2d = caps.get(313);
        glVertex2fv = caps.get(314);
        glVertex2sv = caps.get(315);
        glVertex2iv = caps.get(316);
        glVertex2dv = caps.get(317);
        glVertex3f = caps.get(318);
        glVertex3s = caps.get(319);
        glVertex3i = caps.get(320);
        glVertex3d = caps.get(321);
        glVertex3fv = caps.get(322);
        glVertex3sv = caps.get(323);
        glVertex3iv = caps.get(324);
        glVertex3dv = caps.get(325);
        glVertex4f = caps.get(326);
        glVertex4s = caps.get(327);
        glVertex4i = caps.get(328);
        glVertex4d = caps.get(329);
        glVertex4fv = caps.get(330);
        glVertex4sv = caps.get(331);
        glVertex4iv = caps.get(332);
        glVertex4dv = caps.get(333);
        glVertexPointer = caps.get(334);
        glViewport = caps.get(335);
        glTexImage3D = caps.get(336);
        glTexSubImage3D = caps.get(337);
        glCopyTexSubImage3D = caps.get(338);
        glDrawRangeElements = caps.get(339);
        glCompressedTexImage3D = caps.get(340);
        glCompressedTexImage2D = caps.get(341);
        glCompressedTexImage1D = caps.get(342);
        glCompressedTexSubImage3D = caps.get(343);
        glCompressedTexSubImage2D = caps.get(344);
        glCompressedTexSubImage1D = caps.get(345);
        glGetCompressedTexImage = caps.get(346);
        glSampleCoverage = caps.get(347);
        glActiveTexture = caps.get(348);
        glClientActiveTexture = caps.get(349);
        glMultiTexCoord1f = caps.get(350);
        glMultiTexCoord1s = caps.get(351);
        glMultiTexCoord1i = caps.get(352);
        glMultiTexCoord1d = caps.get(353);
        glMultiTexCoord1fv = caps.get(354);
        glMultiTexCoord1sv = caps.get(355);
        glMultiTexCoord1iv = caps.get(356);
        glMultiTexCoord1dv = caps.get(357);
        glMultiTexCoord2f = caps.get(358);
        glMultiTexCoord2s = caps.get(359);
        glMultiTexCoord2i = caps.get(360);
        glMultiTexCoord2d = caps.get(361);
        glMultiTexCoord2fv = caps.get(362);
        glMultiTexCoord2sv = caps.get(363);
        glMultiTexCoord2iv = caps.get(364);
        glMultiTexCoord2dv = caps.get(365);
        glMultiTexCoord3f = caps.get(366);
        glMultiTexCoord3s = caps.get(367);
        glMultiTexCoord3i = caps.get(368);
        glMultiTexCoord3d = caps.get(369);
        glMultiTexCoord3fv = caps.get(370);
        glMultiTexCoord3sv = caps.get(371);
        glMultiTexCoord3iv = caps.get(372);
        glMultiTexCoord3dv = caps.get(373);
        glMultiTexCoord4f = caps.get(374);
        glMultiTexCoord4s = caps.get(375);
        glMultiTexCoord4i = caps.get(376);
        glMultiTexCoord4d = caps.get(377);
        glMultiTexCoord4fv = caps.get(378);
        glMultiTexCoord4sv = caps.get(379);
        glMultiTexCoord4iv = caps.get(380);
        glMultiTexCoord4dv = caps.get(381);
        glLoadTransposeMatrixf = caps.get(382);
        glLoadTransposeMatrixd = caps.get(383);
        glMultTransposeMatrixf = caps.get(384);
        glMultTransposeMatrixd = caps.get(385);
        glBlendColor = caps.get(386);
        glBlendEquation = caps.get(387);
        glFogCoordf = caps.get(388);
        glFogCoordd = caps.get(389);
        glFogCoordfv = caps.get(390);
        glFogCoorddv = caps.get(391);
        glFogCoordPointer = caps.get(392);
        glMultiDrawArrays = caps.get(393);
        glMultiDrawElements = caps.get(394);
        glPointParameterf = caps.get(395);
        glPointParameteri = caps.get(396);
        glPointParameterfv = caps.get(397);
        glPointParameteriv = caps.get(398);
        glSecondaryColor3b = caps.get(399);
        glSecondaryColor3s = caps.get(400);
        glSecondaryColor3i = caps.get(401);
        glSecondaryColor3f = caps.get(402);
        glSecondaryColor3d = caps.get(403);
        glSecondaryColor3ub = caps.get(404);
        glSecondaryColor3us = caps.get(405);
        glSecondaryColor3ui = caps.get(406);
        glSecondaryColor3bv = caps.get(407);
        glSecondaryColor3sv = caps.get(408);
        glSecondaryColor3iv = caps.get(409);
        glSecondaryColor3fv = caps.get(410);
        glSecondaryColor3dv = caps.get(411);
        glSecondaryColor3ubv = caps.get(412);
        glSecondaryColor3usv = caps.get(413);
        glSecondaryColor3uiv = caps.get(414);
        glSecondaryColorPointer = caps.get(415);
        glBlendFuncSeparate = caps.get(416);
        glWindowPos2i = caps.get(417);
        glWindowPos2s = caps.get(418);
        glWindowPos2f = caps.get(419);
        glWindowPos2d = caps.get(420);
        glWindowPos2iv = caps.get(421);
        glWindowPos2sv = caps.get(422);
        glWindowPos2fv = caps.get(423);
        glWindowPos2dv = caps.get(424);
        glWindowPos3i = caps.get(425);
        glWindowPos3s = caps.get(426);
        glWindowPos3f = caps.get(427);
        glWindowPos3d = caps.get(428);
        glWindowPos3iv = caps.get(429);
        glWindowPos3sv = caps.get(430);
        glWindowPos3fv = caps.get(431);
        glWindowPos3dv = caps.get(432);
        glBindBuffer = caps.get(433);
        glDeleteBuffers = caps.get(434);
        glGenBuffers = caps.get(435);
        glIsBuffer = caps.get(436);
        glBufferData = caps.get(437);
        glBufferSubData = caps.get(438);
        glGetBufferSubData = caps.get(439);
        glMapBuffer = caps.get(440);
        glUnmapBuffer = caps.get(441);
        glGetBufferParameteriv = caps.get(442);
        glGetBufferPointerv = caps.get(443);
        glGenQueries = caps.get(444);
        glDeleteQueries = caps.get(445);
        glIsQuery = caps.get(446);
        glBeginQuery = caps.get(447);
        glEndQuery = caps.get(448);
        glGetQueryiv = caps.get(449);
        glGetQueryObjectiv = caps.get(450);
        glGetQueryObjectuiv = caps.get(451);
        glCreateProgram = caps.get(452);
        glDeleteProgram = caps.get(453);
        glIsProgram = caps.get(454);
        glCreateShader = caps.get(455);
        glDeleteShader = caps.get(456);
        glIsShader = caps.get(457);
        glAttachShader = caps.get(458);
        glDetachShader = caps.get(459);
        glShaderSource = caps.get(460);
        glCompileShader = caps.get(461);
        glLinkProgram = caps.get(462);
        glUseProgram = caps.get(463);
        glValidateProgram = caps.get(464);
        glUniform1f = caps.get(465);
        glUniform2f = caps.get(466);
        glUniform3f = caps.get(467);
        glUniform4f = caps.get(468);
        glUniform1i = caps.get(469);
        glUniform2i = caps.get(470);
        glUniform3i = caps.get(471);
        glUniform4i = caps.get(472);
        glUniform1fv = caps.get(473);
        glUniform2fv = caps.get(474);
        glUniform3fv = caps.get(475);
        glUniform4fv = caps.get(476);
        glUniform1iv = caps.get(477);
        glUniform2iv = caps.get(478);
        glUniform3iv = caps.get(479);
        glUniform4iv = caps.get(480);
        glUniformMatrix2fv = caps.get(481);
        glUniformMatrix3fv = caps.get(482);
        glUniformMatrix4fv = caps.get(483);
        glGetShaderiv = caps.get(484);
        glGetProgramiv = caps.get(485);
        glGetShaderInfoLog = caps.get(486);
        glGetProgramInfoLog = caps.get(487);
        glGetAttachedShaders = caps.get(488);
        glGetUniformLocation = caps.get(489);
        glGetActiveUniform = caps.get(490);
        glGetUniformfv = caps.get(491);
        glGetUniformiv = caps.get(492);
        glGetShaderSource = caps.get(493);
        glVertexAttrib1f = caps.get(494);
        glVertexAttrib1s = caps.get(495);
        glVertexAttrib1d = caps.get(496);
        glVertexAttrib2f = caps.get(497);
        glVertexAttrib2s = caps.get(498);
        glVertexAttrib2d = caps.get(499);
        glVertexAttrib3f = caps.get(500);
        glVertexAttrib3s = caps.get(501);
        glVertexAttrib3d = caps.get(502);
        glVertexAttrib4f = caps.get(503);
        glVertexAttrib4s = caps.get(504);
        glVertexAttrib4d = caps.get(505);
        glVertexAttrib4Nub = caps.get(506);
        glVertexAttrib1fv = caps.get(507);
        glVertexAttrib1sv = caps.get(508);
        glVertexAttrib1dv = caps.get(509);
        glVertexAttrib2fv = caps.get(510);
        glVertexAttrib2sv = caps.get(511);
        glVertexAttrib2dv = caps.get(512);
        glVertexAttrib3fv = caps.get(513);
        glVertexAttrib3sv = caps.get(514);
        glVertexAttrib3dv = caps.get(515);
        glVertexAttrib4fv = caps.get(516);
        glVertexAttrib4sv = caps.get(517);
        glVertexAttrib4dv = caps.get(518);
        glVertexAttrib4iv = caps.get(519);
        glVertexAttrib4bv = caps.get(520);
        glVertexAttrib4ubv = caps.get(521);
        glVertexAttrib4usv = caps.get(522);
        glVertexAttrib4uiv = caps.get(523);
        glVertexAttrib4Nbv = caps.get(524);
        glVertexAttrib4Nsv = caps.get(525);
        glVertexAttrib4Niv = caps.get(526);
        glVertexAttrib4Nubv = caps.get(527);
        glVertexAttrib4Nusv = caps.get(528);
        glVertexAttrib4Nuiv = caps.get(529);
        glVertexAttribPointer = caps.get(530);
        glEnableVertexAttribArray = caps.get(531);
        glDisableVertexAttribArray = caps.get(532);
        glBindAttribLocation = caps.get(533);
        glGetActiveAttrib = caps.get(534);
        glGetAttribLocation = caps.get(535);
        glGetVertexAttribiv = caps.get(536);
        glGetVertexAttribfv = caps.get(537);
        glGetVertexAttribdv = caps.get(538);
        glGetVertexAttribPointerv = caps.get(539);
        glDrawBuffers = caps.get(540);
        glBlendEquationSeparate = caps.get(541);
        glStencilOpSeparate = caps.get(542);
        glStencilFuncSeparate = caps.get(543);
        glStencilMaskSeparate = caps.get(544);
        glUniformMatrix2x3fv = caps.get(545);
        glUniformMatrix3x2fv = caps.get(546);
        glUniformMatrix2x4fv = caps.get(547);
        glUniformMatrix4x2fv = caps.get(548);
        glUniformMatrix3x4fv = caps.get(549);
        glUniformMatrix4x3fv = caps.get(550);
        glGetStringi = caps.get(551);
        glClearBufferiv = caps.get(552);
        glClearBufferuiv = caps.get(553);
        glClearBufferfv = caps.get(554);
        glClearBufferfi = caps.get(555);
        glVertexAttribI1i = caps.get(556);
        glVertexAttribI2i = caps.get(557);
        glVertexAttribI3i = caps.get(558);
        glVertexAttribI4i = caps.get(559);
        glVertexAttribI1ui = caps.get(560);
        glVertexAttribI2ui = caps.get(561);
        glVertexAttribI3ui = caps.get(562);
        glVertexAttribI4ui = caps.get(563);
        glVertexAttribI1iv = caps.get(564);
        glVertexAttribI2iv = caps.get(565);
        glVertexAttribI3iv = caps.get(566);
        glVertexAttribI4iv = caps.get(567);
        glVertexAttribI1uiv = caps.get(568);
        glVertexAttribI2uiv = caps.get(569);
        glVertexAttribI3uiv = caps.get(570);
        glVertexAttribI4uiv = caps.get(571);
        glVertexAttribI4bv = caps.get(572);
        glVertexAttribI4sv = caps.get(573);
        glVertexAttribI4ubv = caps.get(574);
        glVertexAttribI4usv = caps.get(575);
        glVertexAttribIPointer = caps.get(576);
        glGetVertexAttribIiv = caps.get(577);
        glGetVertexAttribIuiv = caps.get(578);
        glUniform1ui = caps.get(579);
        glUniform2ui = caps.get(580);
        glUniform3ui = caps.get(581);
        glUniform4ui = caps.get(582);
        glUniform1uiv = caps.get(583);
        glUniform2uiv = caps.get(584);
        glUniform3uiv = caps.get(585);
        glUniform4uiv = caps.get(586);
        glGetUniformuiv = caps.get(587);
        glBindFragDataLocation = caps.get(588);
        glGetFragDataLocation = caps.get(589);
        glBeginConditionalRender = caps.get(590);
        glEndConditionalRender = caps.get(591);
        glMapBufferRange = caps.get(592);
        glFlushMappedBufferRange = caps.get(593);
        glClampColor = caps.get(594);
        glIsRenderbuffer = caps.get(595);
        glBindRenderbuffer = caps.get(596);
        glDeleteRenderbuffers = caps.get(597);
        glGenRenderbuffers = caps.get(598);
        glRenderbufferStorage = caps.get(599);
        glRenderbufferStorageMultisample = caps.get(600);
        glGetRenderbufferParameteriv = caps.get(601);
        glIsFramebuffer = caps.get(602);
        glBindFramebuffer = caps.get(603);
        glDeleteFramebuffers = caps.get(604);
        glGenFramebuffers = caps.get(605);
        glCheckFramebufferStatus = caps.get(606);
        glFramebufferTexture1D = caps.get(607);
        glFramebufferTexture2D = caps.get(608);
        glFramebufferTexture3D = caps.get(609);
        glFramebufferTextureLayer = caps.get(610);
        glFramebufferRenderbuffer = caps.get(611);
        glGetFramebufferAttachmentParameteriv = caps.get(612);
        glBlitFramebuffer = caps.get(613);
        glGenerateMipmap = caps.get(614);
        glTexParameterIiv = caps.get(615);
        glTexParameterIuiv = caps.get(616);
        glGetTexParameterIiv = caps.get(617);
        glGetTexParameterIuiv = caps.get(618);
        glColorMaski = caps.get(619);
        glGetBooleani_v = caps.get(620);
        glGetIntegeri_v = caps.get(621);
        glEnablei = caps.get(622);
        glDisablei = caps.get(623);
        glIsEnabledi = caps.get(624);
        glBindBufferRange = caps.get(625);
        glBindBufferBase = caps.get(626);
        glBeginTransformFeedback = caps.get(627);
        glEndTransformFeedback = caps.get(628);
        glTransformFeedbackVaryings = caps.get(629);
        glGetTransformFeedbackVarying = caps.get(630);
        glBindVertexArray = caps.get(631);
        glDeleteVertexArrays = caps.get(632);
        glGenVertexArrays = caps.get(633);
        glIsVertexArray = caps.get(634);
        glDrawArraysInstanced = caps.get(635);
        glDrawElementsInstanced = caps.get(636);
        glCopyBufferSubData = caps.get(637);
        glPrimitiveRestartIndex = caps.get(638);
        glTexBuffer = caps.get(639);
        glGetUniformIndices = caps.get(640);
        glGetActiveUniformsiv = caps.get(641);
        glGetActiveUniformName = caps.get(642);
        glGetUniformBlockIndex = caps.get(643);
        glGetActiveUniformBlockiv = caps.get(644);
        glGetActiveUniformBlockName = caps.get(645);
        glUniformBlockBinding = caps.get(646);
        glGetBufferParameteri64v = caps.get(647);
        glDrawElementsBaseVertex = caps.get(648);
        glDrawRangeElementsBaseVertex = caps.get(649);
        glDrawElementsInstancedBaseVertex = caps.get(650);
        glMultiDrawElementsBaseVertex = caps.get(651);
        glProvokingVertex = caps.get(652);
        glTexImage2DMultisample = caps.get(653);
        glTexImage3DMultisample = caps.get(654);
        glGetMultisamplefv = caps.get(655);
        glSampleMaski = caps.get(656);
        glFramebufferTexture = caps.get(657);
        glFenceSync = caps.get(658);
        glIsSync = caps.get(659);
        glDeleteSync = caps.get(660);
        glClientWaitSync = caps.get(661);
        glWaitSync = caps.get(662);
        glGetInteger64v = caps.get(663);
        glGetInteger64i_v = caps.get(664);
        glGetSynciv = caps.get(665);
        glBindFragDataLocationIndexed = caps.get(666);
        glGetFragDataIndex = caps.get(667);
        glGenSamplers = caps.get(668);
        glDeleteSamplers = caps.get(669);
        glIsSampler = caps.get(670);
        glBindSampler = caps.get(671);
        glSamplerParameteri = caps.get(672);
        glSamplerParameterf = caps.get(673);
        glSamplerParameteriv = caps.get(674);
        glSamplerParameterfv = caps.get(675);
        glSamplerParameterIiv = caps.get(676);
        glSamplerParameterIuiv = caps.get(677);
        glGetSamplerParameteriv = caps.get(678);
        glGetSamplerParameterfv = caps.get(679);
        glGetSamplerParameterIiv = caps.get(680);
        glGetSamplerParameterIuiv = caps.get(681);
        glQueryCounter = caps.get(682);
        glGetQueryObjecti64v = caps.get(683);
        glGetQueryObjectui64v = caps.get(684);
        glVertexAttribDivisor = caps.get(685);
        glVertexP2ui = caps.get(686);
        glVertexP3ui = caps.get(687);
        glVertexP4ui = caps.get(688);
        glVertexP2uiv = caps.get(689);
        glVertexP3uiv = caps.get(690);
        glVertexP4uiv = caps.get(691);
        glTexCoordP1ui = caps.get(692);
        glTexCoordP2ui = caps.get(693);
        glTexCoordP3ui = caps.get(694);
        glTexCoordP4ui = caps.get(695);
        glTexCoordP1uiv = caps.get(696);
        glTexCoordP2uiv = caps.get(697);
        glTexCoordP3uiv = caps.get(698);
        glTexCoordP4uiv = caps.get(699);
        glMultiTexCoordP1ui = caps.get(700);
        glMultiTexCoordP2ui = caps.get(701);
        glMultiTexCoordP3ui = caps.get(702);
        glMultiTexCoordP4ui = caps.get(703);
        glMultiTexCoordP1uiv = caps.get(704);
        glMultiTexCoordP2uiv = caps.get(705);
        glMultiTexCoordP3uiv = caps.get(706);
        glMultiTexCoordP4uiv = caps.get(707);
        glNormalP3ui = caps.get(708);
        glNormalP3uiv = caps.get(709);
        glColorP3ui = caps.get(710);
        glColorP4ui = caps.get(711);
        glColorP3uiv = caps.get(712);
        glColorP4uiv = caps.get(713);
        glSecondaryColorP3ui = caps.get(714);
        glSecondaryColorP3uiv = caps.get(715);
        glVertexAttribP1ui = caps.get(716);
        glVertexAttribP2ui = caps.get(717);
        glVertexAttribP3ui = caps.get(718);
        glVertexAttribP4ui = caps.get(719);
        glVertexAttribP1uiv = caps.get(720);
        glVertexAttribP2uiv = caps.get(721);
        glVertexAttribP3uiv = caps.get(722);
        glVertexAttribP4uiv = caps.get(723);
        glBlendEquationi = caps.get(724);
        glBlendEquationSeparatei = caps.get(725);
        glBlendFunci = caps.get(726);
        glBlendFuncSeparatei = caps.get(727);
        glDrawArraysIndirect = caps.get(728);
        glDrawElementsIndirect = caps.get(729);
        glUniform1d = caps.get(730);
        glUniform2d = caps.get(731);
        glUniform3d = caps.get(732);
        glUniform4d = caps.get(733);
        glUniform1dv = caps.get(734);
        glUniform2dv = caps.get(735);
        glUniform3dv = caps.get(736);
        glUniform4dv = caps.get(737);
        glUniformMatrix2dv = caps.get(738);
        glUniformMatrix3dv = caps.get(739);
        glUniformMatrix4dv = caps.get(740);
        glUniformMatrix2x3dv = caps.get(741);
        glUniformMatrix2x4dv = caps.get(742);
        glUniformMatrix3x2dv = caps.get(743);
        glUniformMatrix3x4dv = caps.get(744);
        glUniformMatrix4x2dv = caps.get(745);
        glUniformMatrix4x3dv = caps.get(746);
        glGetUniformdv = caps.get(747);
        glMinSampleShading = caps.get(748);
        glGetSubroutineUniformLocation = caps.get(749);
        glGetSubroutineIndex = caps.get(750);
        glGetActiveSubroutineUniformiv = caps.get(751);
        glGetActiveSubroutineUniformName = caps.get(752);
        glGetActiveSubroutineName = caps.get(753);
        glUniformSubroutinesuiv = caps.get(754);
        glGetUniformSubroutineuiv = caps.get(755);
        glGetProgramStageiv = caps.get(756);
        glPatchParameteri = caps.get(757);
        glPatchParameterfv = caps.get(758);
        glBindTransformFeedback = caps.get(759);
        glDeleteTransformFeedbacks = caps.get(760);
        glGenTransformFeedbacks = caps.get(761);
        glIsTransformFeedback = caps.get(762);
        glPauseTransformFeedback = caps.get(763);
        glResumeTransformFeedback = caps.get(764);
        glDrawTransformFeedback = caps.get(765);
        glDrawTransformFeedbackStream = caps.get(766);
        glBeginQueryIndexed = caps.get(767);
        glEndQueryIndexed = caps.get(768);
        glGetQueryIndexediv = caps.get(769);
        glReleaseShaderCompiler = caps.get(770);
        glShaderBinary = caps.get(771);
        glGetShaderPrecisionFormat = caps.get(772);
        glDepthRangef = caps.get(773);
        glClearDepthf = caps.get(774);
        glGetProgramBinary = caps.get(775);
        glProgramBinary = caps.get(776);
        glProgramParameteri = caps.get(777);
        glUseProgramStages = caps.get(778);
        glActiveShaderProgram = caps.get(779);
        glCreateShaderProgramv = caps.get(780);
        glBindProgramPipeline = caps.get(781);
        glDeleteProgramPipelines = caps.get(782);
        glGenProgramPipelines = caps.get(783);
        glIsProgramPipeline = caps.get(784);
        glGetProgramPipelineiv = caps.get(785);
        glProgramUniform1i = caps.get(786);
        glProgramUniform2i = caps.get(787);
        glProgramUniform3i = caps.get(788);
        glProgramUniform4i = caps.get(789);
        glProgramUniform1ui = caps.get(790);
        glProgramUniform2ui = caps.get(791);
        glProgramUniform3ui = caps.get(792);
        glProgramUniform4ui = caps.get(793);
        glProgramUniform1f = caps.get(794);
        glProgramUniform2f = caps.get(795);
        glProgramUniform3f = caps.get(796);
        glProgramUniform4f = caps.get(797);
        glProgramUniform1d = caps.get(798);
        glProgramUniform2d = caps.get(799);
        glProgramUniform3d = caps.get(800);
        glProgramUniform4d = caps.get(801);
        glProgramUniform1iv = caps.get(802);
        glProgramUniform2iv = caps.get(803);
        glProgramUniform3iv = caps.get(804);
        glProgramUniform4iv = caps.get(805);
        glProgramUniform1uiv = caps.get(806);
        glProgramUniform2uiv = caps.get(807);
        glProgramUniform3uiv = caps.get(808);
        glProgramUniform4uiv = caps.get(809);
        glProgramUniform1fv = caps.get(810);
        glProgramUniform2fv = caps.get(811);
        glProgramUniform3fv = caps.get(812);
        glProgramUniform4fv = caps.get(813);
        glProgramUniform1dv = caps.get(814);
        glProgramUniform2dv = caps.get(815);
        glProgramUniform3dv = caps.get(816);
        glProgramUniform4dv = caps.get(817);
        glProgramUniformMatrix2fv = caps.get(818);
        glProgramUniformMatrix3fv = caps.get(819);
        glProgramUniformMatrix4fv = caps.get(820);
        glProgramUniformMatrix2dv = caps.get(821);
        glProgramUniformMatrix3dv = caps.get(822);
        glProgramUniformMatrix4dv = caps.get(823);
        glProgramUniformMatrix2x3fv = caps.get(824);
        glProgramUniformMatrix3x2fv = caps.get(825);
        glProgramUniformMatrix2x4fv = caps.get(826);
        glProgramUniformMatrix4x2fv = caps.get(827);
        glProgramUniformMatrix3x4fv = caps.get(828);
        glProgramUniformMatrix4x3fv = caps.get(829);
        glProgramUniformMatrix2x3dv = caps.get(830);
        glProgramUniformMatrix3x2dv = caps.get(831);
        glProgramUniformMatrix2x4dv = caps.get(832);
        glProgramUniformMatrix4x2dv = caps.get(833);
        glProgramUniformMatrix3x4dv = caps.get(834);
        glProgramUniformMatrix4x3dv = caps.get(835);
        glValidateProgramPipeline = caps.get(836);
        glGetProgramPipelineInfoLog = caps.get(837);
        glVertexAttribL1d = caps.get(838);
        glVertexAttribL2d = caps.get(839);
        glVertexAttribL3d = caps.get(840);
        glVertexAttribL4d = caps.get(841);
        glVertexAttribL1dv = caps.get(842);
        glVertexAttribL2dv = caps.get(843);
        glVertexAttribL3dv = caps.get(844);
        glVertexAttribL4dv = caps.get(845);
        glVertexAttribLPointer = caps.get(846);
        glGetVertexAttribLdv = caps.get(847);
        glViewportArrayv = caps.get(848);
        glViewportIndexedf = caps.get(849);
        glViewportIndexedfv = caps.get(850);
        glScissorArrayv = caps.get(851);
        glScissorIndexed = caps.get(852);
        glScissorIndexedv = caps.get(853);
        glDepthRangeArrayv = caps.get(854);
        glDepthRangeIndexed = caps.get(855);
        glGetFloati_v = caps.get(856);
        glGetDoublei_v = caps.get(857);
        glGetActiveAtomicCounterBufferiv = caps.get(858);
        glTexStorage1D = caps.get(859);
        glTexStorage2D = caps.get(860);
        glTexStorage3D = caps.get(861);
        glDrawTransformFeedbackInstanced = caps.get(862);
        glDrawTransformFeedbackStreamInstanced = caps.get(863);
        glDrawArraysInstancedBaseInstance = caps.get(864);
        glDrawElementsInstancedBaseInstance = caps.get(865);
        glDrawElementsInstancedBaseVertexBaseInstance = caps.get(866);
        glBindImageTexture = caps.get(867);
        glMemoryBarrier = caps.get(868);
        glGetInternalformativ = caps.get(869);
        glClearBufferData = caps.get(870);
        glClearBufferSubData = caps.get(871);
        glDispatchCompute = caps.get(872);
        glDispatchComputeIndirect = caps.get(873);
        glCopyImageSubData = caps.get(874);
        glDebugMessageControl = caps.get(875);
        glDebugMessageInsert = caps.get(876);
        glDebugMessageCallback = caps.get(877);
        glGetDebugMessageLog = caps.get(878);
        glPushDebugGroup = caps.get(879);
        glPopDebugGroup = caps.get(880);
        glObjectLabel = caps.get(881);
        glGetObjectLabel = caps.get(882);
        glObjectPtrLabel = caps.get(883);
        glGetObjectPtrLabel = caps.get(884);
        glFramebufferParameteri = caps.get(885);
        glGetFramebufferParameteriv = caps.get(886);
        glGetInternalformati64v = caps.get(887);
        glInvalidateTexSubImage = caps.get(888);
        glInvalidateTexImage = caps.get(889);
        glInvalidateBufferSubData = caps.get(890);
        glInvalidateBufferData = caps.get(891);
        glInvalidateFramebuffer = caps.get(892);
        glInvalidateSubFramebuffer = caps.get(893);
        glMultiDrawArraysIndirect = caps.get(894);
        glMultiDrawElementsIndirect = caps.get(895);
        glGetProgramInterfaceiv = caps.get(896);
        glGetProgramResourceIndex = caps.get(897);
        glGetProgramResourceName = caps.get(898);
        glGetProgramResourceiv = caps.get(899);
        glGetProgramResourceLocation = caps.get(900);
        glGetProgramResourceLocationIndex = caps.get(901);
        glShaderStorageBlockBinding = caps.get(902);
        glTexBufferRange = caps.get(903);
        glTexStorage2DMultisample = caps.get(904);
        glTexStorage3DMultisample = caps.get(905);
        glTextureView = caps.get(906);
        glBindVertexBuffer = caps.get(907);
        glVertexAttribFormat = caps.get(908);
        glVertexAttribIFormat = caps.get(909);
        glVertexAttribLFormat = caps.get(910);
        glVertexAttribBinding = caps.get(911);
        glVertexBindingDivisor = caps.get(912);
        glBufferStorage = caps.get(913);
        glClearTexSubImage = caps.get(914);
        glClearTexImage = caps.get(915);
        glBindBuffersBase = caps.get(916);
        glBindBuffersRange = caps.get(917);
        glBindTextures = caps.get(918);
        glBindSamplers = caps.get(919);
        glBindImageTextures = caps.get(920);
        glBindVertexBuffers = caps.get(921);
        glClipControl = caps.get(922);
        glCreateTransformFeedbacks = caps.get(923);
        glTransformFeedbackBufferBase = caps.get(924);
        glTransformFeedbackBufferRange = caps.get(925);
        glGetTransformFeedbackiv = caps.get(926);
        glGetTransformFeedbacki_v = caps.get(927);
        glGetTransformFeedbacki64_v = caps.get(928);
        glCreateBuffers = caps.get(929);
        glNamedBufferStorage = caps.get(930);
        glNamedBufferData = caps.get(931);
        glNamedBufferSubData = caps.get(932);
        glCopyNamedBufferSubData = caps.get(933);
        glClearNamedBufferData = caps.get(934);
        glClearNamedBufferSubData = caps.get(935);
        glMapNamedBuffer = caps.get(936);
        glMapNamedBufferRange = caps.get(937);
        glUnmapNamedBuffer = caps.get(938);
        glFlushMappedNamedBufferRange = caps.get(939);
        glGetNamedBufferParameteriv = caps.get(940);
        glGetNamedBufferParameteri64v = caps.get(941);
        glGetNamedBufferPointerv = caps.get(942);
        glGetNamedBufferSubData = caps.get(943);
        glCreateFramebuffers = caps.get(944);
        glNamedFramebufferRenderbuffer = caps.get(945);
        glNamedFramebufferParameteri = caps.get(946);
        glNamedFramebufferTexture = caps.get(947);
        glNamedFramebufferTextureLayer = caps.get(948);
        glNamedFramebufferDrawBuffer = caps.get(949);
        glNamedFramebufferDrawBuffers = caps.get(950);
        glNamedFramebufferReadBuffer = caps.get(951);
        glInvalidateNamedFramebufferData = caps.get(952);
        glInvalidateNamedFramebufferSubData = caps.get(953);
        glClearNamedFramebufferiv = caps.get(954);
        glClearNamedFramebufferuiv = caps.get(955);
        glClearNamedFramebufferfv = caps.get(956);
        glClearNamedFramebufferfi = caps.get(957);
        glBlitNamedFramebuffer = caps.get(958);
        glCheckNamedFramebufferStatus = caps.get(959);
        glGetNamedFramebufferParameteriv = caps.get(960);
        glGetNamedFramebufferAttachmentParameteriv = caps.get(961);
        glCreateRenderbuffers = caps.get(962);
        glNamedRenderbufferStorage = caps.get(963);
        glNamedRenderbufferStorageMultisample = caps.get(964);
        glGetNamedRenderbufferParameteriv = caps.get(965);
        glCreateTextures = caps.get(966);
        glTextureBuffer = caps.get(967);
        glTextureBufferRange = caps.get(968);
        glTextureStorage1D = caps.get(969);
        glTextureStorage2D = caps.get(970);
        glTextureStorage3D = caps.get(971);
        glTextureStorage2DMultisample = caps.get(972);
        glTextureStorage3DMultisample = caps.get(973);
        glTextureSubImage1D = caps.get(974);
        glTextureSubImage2D = caps.get(975);
        glTextureSubImage3D = caps.get(976);
        glCompressedTextureSubImage1D = caps.get(977);
        glCompressedTextureSubImage2D = caps.get(978);
        glCompressedTextureSubImage3D = caps.get(979);
        glCopyTextureSubImage1D = caps.get(980);
        glCopyTextureSubImage2D = caps.get(981);
        glCopyTextureSubImage3D = caps.get(982);
        glTextureParameterf = caps.get(983);
        glTextureParameterfv = caps.get(984);
        glTextureParameteri = caps.get(985);
        glTextureParameterIiv = caps.get(986);
        glTextureParameterIuiv = caps.get(987);
        glTextureParameteriv = caps.get(988);
        glGenerateTextureMipmap = caps.get(989);
        glBindTextureUnit = caps.get(990);
        glGetTextureImage = caps.get(991);
        glGetCompressedTextureImage = caps.get(992);
        glGetTextureLevelParameterfv = caps.get(993);
        glGetTextureLevelParameteriv = caps.get(994);
        glGetTextureParameterfv = caps.get(995);
        glGetTextureParameterIiv = caps.get(996);
        glGetTextureParameterIuiv = caps.get(997);
        glGetTextureParameteriv = caps.get(998);
        glCreateVertexArrays = caps.get(999);
        glDisableVertexArrayAttrib = caps.get(1000);
        glEnableVertexArrayAttrib = caps.get(1001);
        glVertexArrayElementBuffer = caps.get(1002);
        glVertexArrayVertexBuffer = caps.get(1003);
        glVertexArrayVertexBuffers = caps.get(1004);
        glVertexArrayAttribFormat = caps.get(1005);
        glVertexArrayAttribIFormat = caps.get(1006);
        glVertexArrayAttribLFormat = caps.get(1007);
        glVertexArrayAttribBinding = caps.get(1008);
        glVertexArrayBindingDivisor = caps.get(1009);
        glGetVertexArrayiv = caps.get(1010);
        glGetVertexArrayIndexediv = caps.get(1011);
        glGetVertexArrayIndexed64iv = caps.get(1012);
        glCreateSamplers = caps.get(1013);
        glCreateProgramPipelines = caps.get(1014);
        glCreateQueries = caps.get(1015);
        glGetQueryBufferObjectiv = caps.get(1016);
        glGetQueryBufferObjectuiv = caps.get(1017);
        glGetQueryBufferObjecti64v = caps.get(1018);
        glGetQueryBufferObjectui64v = caps.get(1019);
        glMemoryBarrierByRegion = caps.get(1020);
        glGetTextureSubImage = caps.get(1021);
        glGetCompressedTextureSubImage = caps.get(1022);
        glTextureBarrier = caps.get(1023);
        glGetGraphicsResetStatus = caps.get(1024);
        glGetnMapdv = caps.get(1025);
        glGetnMapfv = caps.get(1026);
        glGetnMapiv = caps.get(1027);
        glGetnPixelMapfv = caps.get(1028);
        glGetnPixelMapuiv = caps.get(1029);
        glGetnPixelMapusv = caps.get(1030);
        glGetnPolygonStipple = caps.get(1031);
        glGetnTexImage = caps.get(1032);
        glReadnPixels = caps.get(1033);
        glGetnColorTable = caps.get(1034);
        glGetnConvolutionFilter = caps.get(1035);
        glGetnSeparableFilter = caps.get(1036);
        glGetnHistogram = caps.get(1037);
        glGetnMinmax = caps.get(1038);
        glGetnCompressedTexImage = caps.get(1039);
        glGetnUniformfv = caps.get(1040);
        glGetnUniformdv = caps.get(1041);
        glGetnUniformiv = caps.get(1042);
        glGetnUniformuiv = caps.get(1043);
        glMultiDrawArraysIndirectCount = caps.get(1044);
        glMultiDrawElementsIndirectCount = caps.get(1045);
        glPolygonOffsetClamp = caps.get(1046);
        glSpecializeShader = caps.get(1047);
        glDebugMessageEnableAMD = caps.get(1048);
        glDebugMessageInsertAMD = caps.get(1049);
        glDebugMessageCallbackAMD = caps.get(1050);
        glGetDebugMessageLogAMD = caps.get(1051);
        glBlendFuncIndexedAMD = caps.get(1052);
        glBlendFuncSeparateIndexedAMD = caps.get(1053);
        glBlendEquationIndexedAMD = caps.get(1054);
        glBlendEquationSeparateIndexedAMD = caps.get(1055);
        glRenderbufferStorageMultisampleAdvancedAMD = caps.get(1056);
        glNamedRenderbufferStorageMultisampleAdvancedAMD = caps.get(1057);
        glUniform1i64NV = caps.get(1058);
        glUniform2i64NV = caps.get(1059);
        glUniform3i64NV = caps.get(1060);
        glUniform4i64NV = caps.get(1061);
        glUniform1i64vNV = caps.get(1062);
        glUniform2i64vNV = caps.get(1063);
        glUniform3i64vNV = caps.get(1064);
        glUniform4i64vNV = caps.get(1065);
        glUniform1ui64NV = caps.get(1066);
        glUniform2ui64NV = caps.get(1067);
        glUniform3ui64NV = caps.get(1068);
        glUniform4ui64NV = caps.get(1069);
        glUniform1ui64vNV = caps.get(1070);
        glUniform2ui64vNV = caps.get(1071);
        glUniform3ui64vNV = caps.get(1072);
        glUniform4ui64vNV = caps.get(1073);
        glGetUniformi64vNV = caps.get(1074);
        glGetUniformui64vNV = caps.get(1075);
        glProgramUniform1i64NV = caps.get(1076);
        glProgramUniform2i64NV = caps.get(1077);
        glProgramUniform3i64NV = caps.get(1078);
        glProgramUniform4i64NV = caps.get(1079);
        glProgramUniform1i64vNV = caps.get(1080);
        glProgramUniform2i64vNV = caps.get(1081);
        glProgramUniform3i64vNV = caps.get(1082);
        glProgramUniform4i64vNV = caps.get(1083);
        glProgramUniform1ui64NV = caps.get(1084);
        glProgramUniform2ui64NV = caps.get(1085);
        glProgramUniform3ui64NV = caps.get(1086);
        glProgramUniform4ui64NV = caps.get(1087);
        glProgramUniform1ui64vNV = caps.get(1088);
        glProgramUniform2ui64vNV = caps.get(1089);
        glProgramUniform3ui64vNV = caps.get(1090);
        glProgramUniform4ui64vNV = caps.get(1091);
        glVertexAttribParameteriAMD = caps.get(1092);
        glQueryObjectParameteruiAMD = caps.get(1093);
        glGetPerfMonitorGroupsAMD = caps.get(1094);
        glGetPerfMonitorCountersAMD = caps.get(1095);
        glGetPerfMonitorGroupStringAMD = caps.get(1096);
        glGetPerfMonitorCounterStringAMD = caps.get(1097);
        glGetPerfMonitorCounterInfoAMD = caps.get(1098);
        glGenPerfMonitorsAMD = caps.get(1099);
        glDeletePerfMonitorsAMD = caps.get(1100);
        glSelectPerfMonitorCountersAMD = caps.get(1101);
        glBeginPerfMonitorAMD = caps.get(1102);
        glEndPerfMonitorAMD = caps.get(1103);
        glGetPerfMonitorCounterDataAMD = caps.get(1104);
        glSetMultisamplefvAMD = caps.get(1105);
        glTexStorageSparseAMD = caps.get(1106);
        glTextureStorageSparseAMD = caps.get(1107);
        glStencilOpValueAMD = caps.get(1108);
        glTessellationFactorAMD = caps.get(1109);
        glTessellationModeAMD = caps.get(1110);
        glGetTextureHandleARB = caps.get(1111);
        glGetTextureSamplerHandleARB = caps.get(1112);
        glMakeTextureHandleResidentARB = caps.get(1113);
        glMakeTextureHandleNonResidentARB = caps.get(1114);
        glGetImageHandleARB = caps.get(1115);
        glMakeImageHandleResidentARB = caps.get(1116);
        glMakeImageHandleNonResidentARB = caps.get(1117);
        glUniformHandleui64ARB = caps.get(1118);
        glUniformHandleui64vARB = caps.get(1119);
        glProgramUniformHandleui64ARB = caps.get(1120);
        glProgramUniformHandleui64vARB = caps.get(1121);
        glIsTextureHandleResidentARB = caps.get(1122);
        glIsImageHandleResidentARB = caps.get(1123);
        glVertexAttribL1ui64ARB = caps.get(1124);
        glVertexAttribL1ui64vARB = caps.get(1125);
        glGetVertexAttribLui64vARB = caps.get(1126);
        glNamedBufferStorageEXT = caps.get(1127);
        glCreateSyncFromCLeventARB = caps.get(1128);
        glClearNamedBufferDataEXT = caps.get(1129);
        glClearNamedBufferSubDataEXT = caps.get(1130);
        glClampColorARB = caps.get(1131);
        glDispatchComputeGroupSizeARB = caps.get(1132);
        glDebugMessageControlARB = caps.get(1133);
        glDebugMessageInsertARB = caps.get(1134);
        glDebugMessageCallbackARB = caps.get(1135);
        glGetDebugMessageLogARB = caps.get(1136);
        glDrawBuffersARB = caps.get(1137);
        glBlendEquationiARB = caps.get(1138);
        glBlendEquationSeparateiARB = caps.get(1139);
        glBlendFunciARB = caps.get(1140);
        glBlendFuncSeparateiARB = caps.get(1141);
        glDrawArraysInstancedARB = caps.get(1142);
        glDrawElementsInstancedARB = caps.get(1143);
        glPrimitiveBoundingBoxARB = caps.get(1144);
        glNamedFramebufferParameteriEXT = caps.get(1145);
        glGetNamedFramebufferParameterivEXT = caps.get(1146);
        glProgramParameteriARB = caps.get(1147);
        glFramebufferTextureARB = caps.get(1148);
        glFramebufferTextureLayerARB = caps.get(1149);
        glFramebufferTextureFaceARB = caps.get(1150);
        glSpecializeShaderARB = caps.get(1151);
        glProgramUniform1dEXT = caps.get(1152);
        glProgramUniform2dEXT = caps.get(1153);
        glProgramUniform3dEXT = caps.get(1154);
        glProgramUniform4dEXT = caps.get(1155);
        glProgramUniform1dvEXT = caps.get(1156);
        glProgramUniform2dvEXT = caps.get(1157);
        glProgramUniform3dvEXT = caps.get(1158);
        glProgramUniform4dvEXT = caps.get(1159);
        glProgramUniformMatrix2dvEXT = caps.get(1160);
        glProgramUniformMatrix3dvEXT = caps.get(1161);
        glProgramUniformMatrix4dvEXT = caps.get(1162);
        glProgramUniformMatrix2x3dvEXT = caps.get(1163);
        glProgramUniformMatrix2x4dvEXT = caps.get(1164);
        glProgramUniformMatrix3x2dvEXT = caps.get(1165);
        glProgramUniformMatrix3x4dvEXT = caps.get(1166);
        glProgramUniformMatrix4x2dvEXT = caps.get(1167);
        glProgramUniformMatrix4x3dvEXT = caps.get(1168);
        glUniform1i64ARB = caps.get(1169);
        glUniform1i64vARB = caps.get(1170);
        glProgramUniform1i64ARB = caps.get(1171);
        glProgramUniform1i64vARB = caps.get(1172);
        glUniform2i64ARB = caps.get(1173);
        glUniform2i64vARB = caps.get(1174);
        glProgramUniform2i64ARB = caps.get(1175);
        glProgramUniform2i64vARB = caps.get(1176);
        glUniform3i64ARB = caps.get(1177);
        glUniform3i64vARB = caps.get(1178);
        glProgramUniform3i64ARB = caps.get(1179);
        glProgramUniform3i64vARB = caps.get(1180);
        glUniform4i64ARB = caps.get(1181);
        glUniform4i64vARB = caps.get(1182);
        glProgramUniform4i64ARB = caps.get(1183);
        glProgramUniform4i64vARB = caps.get(1184);
        glUniform1ui64ARB = caps.get(1185);
        glUniform1ui64vARB = caps.get(1186);
        glProgramUniform1ui64ARB = caps.get(1187);
        glProgramUniform1ui64vARB = caps.get(1188);
        glUniform2ui64ARB = caps.get(1189);
        glUniform2ui64vARB = caps.get(1190);
        glProgramUniform2ui64ARB = caps.get(1191);
        glProgramUniform2ui64vARB = caps.get(1192);
        glUniform3ui64ARB = caps.get(1193);
        glUniform3ui64vARB = caps.get(1194);
        glProgramUniform3ui64ARB = caps.get(1195);
        glProgramUniform3ui64vARB = caps.get(1196);
        glUniform4ui64ARB = caps.get(1197);
        glUniform4ui64vARB = caps.get(1198);
        glProgramUniform4ui64ARB = caps.get(1199);
        glProgramUniform4ui64vARB = caps.get(1200);
        glGetUniformi64vARB = caps.get(1201);
        glGetUniformui64vARB = caps.get(1202);
        glGetnUniformi64vARB = caps.get(1203);
        glGetnUniformui64vARB = caps.get(1204);
        glColorTable = caps.get(1205);
        glCopyColorTable = caps.get(1206);
        glColorTableParameteriv = caps.get(1207);
        glColorTableParameterfv = caps.get(1208);
        glGetColorTable = caps.get(1209);
        glGetColorTableParameteriv = caps.get(1210);
        glGetColorTableParameterfv = caps.get(1211);
        glColorSubTable = caps.get(1212);
        glCopyColorSubTable = caps.get(1213);
        glConvolutionFilter1D = caps.get(1214);
        glConvolutionFilter2D = caps.get(1215);
        glCopyConvolutionFilter1D = caps.get(1216);
        glCopyConvolutionFilter2D = caps.get(1217);
        glGetConvolutionFilter = caps.get(1218);
        glSeparableFilter2D = caps.get(1219);
        glGetSeparableFilter = caps.get(1220);
        glConvolutionParameteri = caps.get(1221);
        glConvolutionParameteriv = caps.get(1222);
        glConvolutionParameterf = caps.get(1223);
        glConvolutionParameterfv = caps.get(1224);
        glGetConvolutionParameteriv = caps.get(1225);
        glGetConvolutionParameterfv = caps.get(1226);
        glHistogram = caps.get(1227);
        glResetHistogram = caps.get(1228);
        glGetHistogram = caps.get(1229);
        glGetHistogramParameteriv = caps.get(1230);
        glGetHistogramParameterfv = caps.get(1231);
        glMinmax = caps.get(1232);
        glResetMinmax = caps.get(1233);
        glGetMinmax = caps.get(1234);
        glGetMinmaxParameteriv = caps.get(1235);
        glGetMinmaxParameterfv = caps.get(1236);
        glMultiDrawArraysIndirectCountARB = caps.get(1237);
        glMultiDrawElementsIndirectCountARB = caps.get(1238);
        glVertexAttribDivisorARB = caps.get(1239);
        glVertexArrayVertexAttribDivisorEXT = caps.get(1240);
        glCurrentPaletteMatrixARB = caps.get(1241);
        glMatrixIndexuivARB = caps.get(1242);
        glMatrixIndexubvARB = caps.get(1243);
        glMatrixIndexusvARB = caps.get(1244);
        glMatrixIndexPointerARB = caps.get(1245);
        glSampleCoverageARB = caps.get(1246);
        glActiveTextureARB = caps.get(1247);
        glClientActiveTextureARB = caps.get(1248);
        glMultiTexCoord1fARB = caps.get(1249);
        glMultiTexCoord1sARB = caps.get(1250);
        glMultiTexCoord1iARB = caps.get(1251);
        glMultiTexCoord1dARB = caps.get(1252);
        glMultiTexCoord1fvARB = caps.get(1253);
        glMultiTexCoord1svARB = caps.get(1254);
        glMultiTexCoord1ivARB = caps.get(1255);
        glMultiTexCoord1dvARB = caps.get(1256);
        glMultiTexCoord2fARB = caps.get(1257);
        glMultiTexCoord2sARB = caps.get(1258);
        glMultiTexCoord2iARB = caps.get(1259);
        glMultiTexCoord2dARB = caps.get(1260);
        glMultiTexCoord2fvARB = caps.get(1261);
        glMultiTexCoord2svARB = caps.get(1262);
        glMultiTexCoord2ivARB = caps.get(1263);
        glMultiTexCoord2dvARB = caps.get(1264);
        glMultiTexCoord3fARB = caps.get(1265);
        glMultiTexCoord3sARB = caps.get(1266);
        glMultiTexCoord3iARB = caps.get(1267);
        glMultiTexCoord3dARB = caps.get(1268);
        glMultiTexCoord3fvARB = caps.get(1269);
        glMultiTexCoord3svARB = caps.get(1270);
        glMultiTexCoord3ivARB = caps.get(1271);
        glMultiTexCoord3dvARB = caps.get(1272);
        glMultiTexCoord4fARB = caps.get(1273);
        glMultiTexCoord4sARB = caps.get(1274);
        glMultiTexCoord4iARB = caps.get(1275);
        glMultiTexCoord4dARB = caps.get(1276);
        glMultiTexCoord4fvARB = caps.get(1277);
        glMultiTexCoord4svARB = caps.get(1278);
        glMultiTexCoord4ivARB = caps.get(1279);
        glMultiTexCoord4dvARB = caps.get(1280);
        glGenQueriesARB = caps.get(1281);
        glDeleteQueriesARB = caps.get(1282);
        glIsQueryARB = caps.get(1283);
        glBeginQueryARB = caps.get(1284);
        glEndQueryARB = caps.get(1285);
        glGetQueryivARB = caps.get(1286);
        glGetQueryObjectivARB = caps.get(1287);
        glGetQueryObjectuivARB = caps.get(1288);
        glMaxShaderCompilerThreadsARB = caps.get(1289);
        glPointParameterfARB = caps.get(1290);
        glPointParameterfvARB = caps.get(1291);
        glGetGraphicsResetStatusARB = caps.get(1292);
        glGetnMapdvARB = caps.get(1293);
        glGetnMapfvARB = caps.get(1294);
        glGetnMapivARB = caps.get(1295);
        glGetnPixelMapfvARB = caps.get(1296);
        glGetnPixelMapuivARB = caps.get(1297);
        glGetnPixelMapusvARB = caps.get(1298);
        glGetnPolygonStippleARB = caps.get(1299);
        glGetnTexImageARB = caps.get(1300);
        glReadnPixelsARB = caps.get(1301);
        glGetnColorTableARB = caps.get(1302);
        glGetnConvolutionFilterARB = caps.get(1303);
        glGetnSeparableFilterARB = caps.get(1304);
        glGetnHistogramARB = caps.get(1305);
        glGetnMinmaxARB = caps.get(1306);
        glGetnCompressedTexImageARB = caps.get(1307);
        glGetnUniformfvARB = caps.get(1308);
        glGetnUniformivARB = caps.get(1309);
        glGetnUniformuivARB = caps.get(1310);
        glGetnUniformdvARB = caps.get(1311);
        glFramebufferSampleLocationsfvARB = caps.get(1312);
        glNamedFramebufferSampleLocationsfvARB = caps.get(1313);
        glEvaluateDepthValuesARB = caps.get(1314);
        glMinSampleShadingARB = caps.get(1315);
        glDeleteObjectARB = caps.get(1316);
        glGetHandleARB = caps.get(1317);
        glDetachObjectARB = caps.get(1318);
        glCreateShaderObjectARB = caps.get(1319);
        glShaderSourceARB = caps.get(1320);
        glCompileShaderARB = caps.get(1321);
        glCreateProgramObjectARB = caps.get(1322);
        glAttachObjectARB = caps.get(1323);
        glLinkProgramARB = caps.get(1324);
        glUseProgramObjectARB = caps.get(1325);
        glValidateProgramARB = caps.get(1326);
        glUniform1fARB = caps.get(1327);
        glUniform2fARB = caps.get(1328);
        glUniform3fARB = caps.get(1329);
        glUniform4fARB = caps.get(1330);
        glUniform1iARB = caps.get(1331);
        glUniform2iARB = caps.get(1332);
        glUniform3iARB = caps.get(1333);
        glUniform4iARB = caps.get(1334);
        glUniform1fvARB = caps.get(1335);
        glUniform2fvARB = caps.get(1336);
        glUniform3fvARB = caps.get(1337);
        glUniform4fvARB = caps.get(1338);
        glUniform1ivARB = caps.get(1339);
        glUniform2ivARB = caps.get(1340);
        glUniform3ivARB = caps.get(1341);
        glUniform4ivARB = caps.get(1342);
        glUniformMatrix2fvARB = caps.get(1343);
        glUniformMatrix3fvARB = caps.get(1344);
        glUniformMatrix4fvARB = caps.get(1345);
        glGetObjectParameterfvARB = caps.get(1346);
        glGetObjectParameterivARB = caps.get(1347);
        glGetInfoLogARB = caps.get(1348);
        glGetAttachedObjectsARB = caps.get(1349);
        glGetUniformLocationARB = caps.get(1350);
        glGetActiveUniformARB = caps.get(1351);
        glGetUniformfvARB = caps.get(1352);
        glGetUniformivARB = caps.get(1353);
        glGetShaderSourceARB = caps.get(1354);
        glNamedStringARB = caps.get(1355);
        glDeleteNamedStringARB = caps.get(1356);
        glCompileShaderIncludeARB = caps.get(1357);
        glIsNamedStringARB = caps.get(1358);
        glGetNamedStringARB = caps.get(1359);
        glGetNamedStringivARB = caps.get(1360);
        glBufferPageCommitmentARB = caps.get(1361);
        glNamedBufferPageCommitmentEXT = caps.get(1362);
        glNamedBufferPageCommitmentARB = caps.get(1363);
        glTexPageCommitmentARB = caps.get(1364);
        glTexturePageCommitmentEXT = caps.get(1365);
        glTexBufferARB = caps.get(1366);
        glTextureBufferRangeEXT = caps.get(1367);
        glCompressedTexImage3DARB = caps.get(1368);
        glCompressedTexImage2DARB = caps.get(1369);
        glCompressedTexImage1DARB = caps.get(1370);
        glCompressedTexSubImage3DARB = caps.get(1371);
        glCompressedTexSubImage2DARB = caps.get(1372);
        glCompressedTexSubImage1DARB = caps.get(1373);
        glGetCompressedTexImageARB = caps.get(1374);
        glTextureStorage1DEXT = caps.get(1375);
        glTextureStorage2DEXT = caps.get(1376);
        glTextureStorage3DEXT = caps.get(1377);
        glTextureStorage2DMultisampleEXT = caps.get(1378);
        glTextureStorage3DMultisampleEXT = caps.get(1379);
        glLoadTransposeMatrixfARB = caps.get(1380);
        glLoadTransposeMatrixdARB = caps.get(1381);
        glMultTransposeMatrixfARB = caps.get(1382);
        glMultTransposeMatrixdARB = caps.get(1383);
        glVertexArrayVertexAttribLOffsetEXT = caps.get(1384);
        glVertexArrayBindVertexBufferEXT = caps.get(1385);
        glVertexArrayVertexAttribFormatEXT = caps.get(1386);
        glVertexArrayVertexAttribIFormatEXT = caps.get(1387);
        glVertexArrayVertexAttribLFormatEXT = caps.get(1388);
        glVertexArrayVertexAttribBindingEXT = caps.get(1389);
        glVertexArrayVertexBindingDivisorEXT = caps.get(1390);
        glWeightfvARB = caps.get(1391);
        glWeightbvARB = caps.get(1392);
        glWeightubvARB = caps.get(1393);
        glWeightsvARB = caps.get(1394);
        glWeightusvARB = caps.get(1395);
        glWeightivARB = caps.get(1396);
        glWeightuivARB = caps.get(1397);
        glWeightdvARB = caps.get(1398);
        glWeightPointerARB = caps.get(1399);
        glVertexBlendARB = caps.get(1400);
        glBindBufferARB = caps.get(1401);
        glDeleteBuffersARB = caps.get(1402);
        glGenBuffersARB = caps.get(1403);
        glIsBufferARB = caps.get(1404);
        glBufferDataARB = caps.get(1405);
        glBufferSubDataARB = caps.get(1406);
        glGetBufferSubDataARB = caps.get(1407);
        glMapBufferARB = caps.get(1408);
        glUnmapBufferARB = caps.get(1409);
        glGetBufferParameterivARB = caps.get(1410);
        glGetBufferPointervARB = caps.get(1411);
        glVertexAttrib1sARB = caps.get(1412);
        glVertexAttrib1fARB = caps.get(1413);
        glVertexAttrib1dARB = caps.get(1414);
        glVertexAttrib2sARB = caps.get(1415);
        glVertexAttrib2fARB = caps.get(1416);
        glVertexAttrib2dARB = caps.get(1417);
        glVertexAttrib3sARB = caps.get(1418);
        glVertexAttrib3fARB = caps.get(1419);
        glVertexAttrib3dARB = caps.get(1420);
        glVertexAttrib4sARB = caps.get(1421);
        glVertexAttrib4fARB = caps.get(1422);
        glVertexAttrib4dARB = caps.get(1423);
        glVertexAttrib4NubARB = caps.get(1424);
        glVertexAttrib1svARB = caps.get(1425);
        glVertexAttrib1fvARB = caps.get(1426);
        glVertexAttrib1dvARB = caps.get(1427);
        glVertexAttrib2svARB = caps.get(1428);
        glVertexAttrib2fvARB = caps.get(1429);
        glVertexAttrib2dvARB = caps.get(1430);
        glVertexAttrib3svARB = caps.get(1431);
        glVertexAttrib3fvARB = caps.get(1432);
        glVertexAttrib3dvARB = caps.get(1433);
        glVertexAttrib4fvARB = caps.get(1434);
        glVertexAttrib4bvARB = caps.get(1435);
        glVertexAttrib4svARB = caps.get(1436);
        glVertexAttrib4ivARB = caps.get(1437);
        glVertexAttrib4ubvARB = caps.get(1438);
        glVertexAttrib4usvARB = caps.get(1439);
        glVertexAttrib4uivARB = caps.get(1440);
        glVertexAttrib4dvARB = caps.get(1441);
        glVertexAttrib4NbvARB = caps.get(1442);
        glVertexAttrib4NsvARB = caps.get(1443);
        glVertexAttrib4NivARB = caps.get(1444);
        glVertexAttrib4NubvARB = caps.get(1445);
        glVertexAttrib4NusvARB = caps.get(1446);
        glVertexAttrib4NuivARB = caps.get(1447);
        glVertexAttribPointerARB = caps.get(1448);
        glEnableVertexAttribArrayARB = caps.get(1449);
        glDisableVertexAttribArrayARB = caps.get(1450);
        glProgramStringARB = caps.get(1451);
        glBindProgramARB = caps.get(1452);
        glDeleteProgramsARB = caps.get(1453);
        glGenProgramsARB = caps.get(1454);
        glProgramEnvParameter4dARB = caps.get(1455);
        glProgramEnvParameter4dvARB = caps.get(1456);
        glProgramEnvParameter4fARB = caps.get(1457);
        glProgramEnvParameter4fvARB = caps.get(1458);
        glProgramLocalParameter4dARB = caps.get(1459);
        glProgramLocalParameter4dvARB = caps.get(1460);
        glProgramLocalParameter4fARB = caps.get(1461);
        glProgramLocalParameter4fvARB = caps.get(1462);
        glGetProgramEnvParameterfvARB = caps.get(1463);
        glGetProgramEnvParameterdvARB = caps.get(1464);
        glGetProgramLocalParameterfvARB = caps.get(1465);
        glGetProgramLocalParameterdvARB = caps.get(1466);
        glGetProgramivARB = caps.get(1467);
        glGetProgramStringARB = caps.get(1468);
        glGetVertexAttribfvARB = caps.get(1469);
        glGetVertexAttribdvARB = caps.get(1470);
        glGetVertexAttribivARB = caps.get(1471);
        glGetVertexAttribPointervARB = caps.get(1472);
        glIsProgramARB = caps.get(1473);
        glBindAttribLocationARB = caps.get(1474);
        glGetActiveAttribARB = caps.get(1475);
        glGetAttribLocationARB = caps.get(1476);
        glWindowPos2iARB = caps.get(1477);
        glWindowPos2sARB = caps.get(1478);
        glWindowPos2fARB = caps.get(1479);
        glWindowPos2dARB = caps.get(1480);
        glWindowPos2ivARB = caps.get(1481);
        glWindowPos2svARB = caps.get(1482);
        glWindowPos2fvARB = caps.get(1483);
        glWindowPos2dvARB = caps.get(1484);
        glWindowPos3iARB = caps.get(1485);
        glWindowPos3sARB = caps.get(1486);
        glWindowPos3fARB = caps.get(1487);
        glWindowPos3dARB = caps.get(1488);
        glWindowPos3ivARB = caps.get(1489);
        glWindowPos3svARB = caps.get(1490);
        glWindowPos3fvARB = caps.get(1491);
        glWindowPos3dvARB = caps.get(1492);
        glUniformBufferEXT = caps.get(1493);
        glGetUniformBufferSizeEXT = caps.get(1494);
        glGetUniformOffsetEXT = caps.get(1495);
        glBlendColorEXT = caps.get(1496);
        glBlendEquationSeparateEXT = caps.get(1497);
        glBlendFuncSeparateEXT = caps.get(1498);
        glBlendEquationEXT = caps.get(1499);
        glLockArraysEXT = caps.get(1500);
        glUnlockArraysEXT = caps.get(1501);
        glLabelObjectEXT = caps.get(1502);
        glGetObjectLabelEXT = caps.get(1503);
        glInsertEventMarkerEXT = caps.get(1504);
        glPushGroupMarkerEXT = caps.get(1505);
        glPopGroupMarkerEXT = caps.get(1506);
        glDepthBoundsEXT = caps.get(1507);
        glClientAttribDefaultEXT = caps.get(1508);
        glPushClientAttribDefaultEXT = caps.get(1509);
        glMatrixLoadfEXT = caps.get(1510);
        glMatrixLoaddEXT = caps.get(1511);
        glMatrixMultfEXT = caps.get(1512);
        glMatrixMultdEXT = caps.get(1513);
        glMatrixLoadIdentityEXT = caps.get(1514);
        glMatrixRotatefEXT = caps.get(1515);
        glMatrixRotatedEXT = caps.get(1516);
        glMatrixScalefEXT = caps.get(1517);
        glMatrixScaledEXT = caps.get(1518);
        glMatrixTranslatefEXT = caps.get(1519);
        glMatrixTranslatedEXT = caps.get(1520);
        glMatrixOrthoEXT = caps.get(1521);
        glMatrixFrustumEXT = caps.get(1522);
        glMatrixPushEXT = caps.get(1523);
        glMatrixPopEXT = caps.get(1524);
        glTextureParameteriEXT = caps.get(1525);
        glTextureParameterivEXT = caps.get(1526);
        glTextureParameterfEXT = caps.get(1527);
        glTextureParameterfvEXT = caps.get(1528);
        glTextureImage1DEXT = caps.get(1529);
        glTextureImage2DEXT = caps.get(1530);
        glTextureSubImage1DEXT = caps.get(1531);
        glTextureSubImage2DEXT = caps.get(1532);
        glCopyTextureImage1DEXT = caps.get(1533);
        glCopyTextureImage2DEXT = caps.get(1534);
        glCopyTextureSubImage1DEXT = caps.get(1535);
        glCopyTextureSubImage2DEXT = caps.get(1536);
        glGetTextureImageEXT = caps.get(1537);
        glGetTextureParameterfvEXT = caps.get(1538);
        glGetTextureParameterivEXT = caps.get(1539);
        glGetTextureLevelParameterfvEXT = caps.get(1540);
        glGetTextureLevelParameterivEXT = caps.get(1541);
        glTextureImage3DEXT = caps.get(1542);
        glTextureSubImage3DEXT = caps.get(1543);
        glCopyTextureSubImage3DEXT = caps.get(1544);
        glBindMultiTextureEXT = caps.get(1545);
        glMultiTexCoordPointerEXT = caps.get(1546);
        glMultiTexEnvfEXT = caps.get(1547);
        glMultiTexEnvfvEXT = caps.get(1548);
        glMultiTexEnviEXT = caps.get(1549);
        glMultiTexEnvivEXT = caps.get(1550);
        glMultiTexGendEXT = caps.get(1551);
        glMultiTexGendvEXT = caps.get(1552);
        glMultiTexGenfEXT = caps.get(1553);
        glMultiTexGenfvEXT = caps.get(1554);
        glMultiTexGeniEXT = caps.get(1555);
        glMultiTexGenivEXT = caps.get(1556);
        glGetMultiTexEnvfvEXT = caps.get(1557);
        glGetMultiTexEnvivEXT = caps.get(1558);
        glGetMultiTexGendvEXT = caps.get(1559);
        glGetMultiTexGenfvEXT = caps.get(1560);
        glGetMultiTexGenivEXT = caps.get(1561);
        glMultiTexParameteriEXT = caps.get(1562);
        glMultiTexParameterivEXT = caps.get(1563);
        glMultiTexParameterfEXT = caps.get(1564);
        glMultiTexParameterfvEXT = caps.get(1565);
        glMultiTexImage1DEXT = caps.get(1566);
        glMultiTexImage2DEXT = caps.get(1567);
        glMultiTexSubImage1DEXT = caps.get(1568);
        glMultiTexSubImage2DEXT = caps.get(1569);
        glCopyMultiTexImage1DEXT = caps.get(1570);
        glCopyMultiTexImage2DEXT = caps.get(1571);
        glCopyMultiTexSubImage1DEXT = caps.get(1572);
        glCopyMultiTexSubImage2DEXT = caps.get(1573);
        glGetMultiTexImageEXT = caps.get(1574);
        glGetMultiTexParameterfvEXT = caps.get(1575);
        glGetMultiTexParameterivEXT = caps.get(1576);
        glGetMultiTexLevelParameterfvEXT = caps.get(1577);
        glGetMultiTexLevelParameterivEXT = caps.get(1578);
        glMultiTexImage3DEXT = caps.get(1579);
        glMultiTexSubImage3DEXT = caps.get(1580);
        glCopyMultiTexSubImage3DEXT = caps.get(1581);
        glEnableClientStateIndexedEXT = caps.get(1582);
        glDisableClientStateIndexedEXT = caps.get(1583);
        glEnableClientStateiEXT = caps.get(1584);
        glDisableClientStateiEXT = caps.get(1585);
        glGetFloatIndexedvEXT = caps.get(1586);
        glGetDoubleIndexedvEXT = caps.get(1587);
        glGetPointerIndexedvEXT = caps.get(1588);
        glGetFloati_vEXT = caps.get(1589);
        glGetDoublei_vEXT = caps.get(1590);
        glGetPointeri_vEXT = caps.get(1591);
        glEnableIndexedEXT = caps.get(1592);
        glDisableIndexedEXT = caps.get(1593);
        glIsEnabledIndexedEXT = caps.get(1594);
        glGetIntegerIndexedvEXT = caps.get(1595);
        glGetBooleanIndexedvEXT = caps.get(1596);
        glNamedProgramStringEXT = caps.get(1597);
        glNamedProgramLocalParameter4dEXT = caps.get(1598);
        glNamedProgramLocalParameter4dvEXT = caps.get(1599);
        glNamedProgramLocalParameter4fEXT = caps.get(1600);
        glNamedProgramLocalParameter4fvEXT = caps.get(1601);
        glGetNamedProgramLocalParameterdvEXT = caps.get(1602);
        glGetNamedProgramLocalParameterfvEXT = caps.get(1603);
        glGetNamedProgramivEXT = caps.get(1604);
        glGetNamedProgramStringEXT = caps.get(1605);
        glCompressedTextureImage3DEXT = caps.get(1606);
        glCompressedTextureImage2DEXT = caps.get(1607);
        glCompressedTextureImage1DEXT = caps.get(1608);
        glCompressedTextureSubImage3DEXT = caps.get(1609);
        glCompressedTextureSubImage2DEXT = caps.get(1610);
        glCompressedTextureSubImage1DEXT = caps.get(1611);
        glGetCompressedTextureImageEXT = caps.get(1612);
        glCompressedMultiTexImage3DEXT = caps.get(1613);
        glCompressedMultiTexImage2DEXT = caps.get(1614);
        glCompressedMultiTexImage1DEXT = caps.get(1615);
        glCompressedMultiTexSubImage3DEXT = caps.get(1616);
        glCompressedMultiTexSubImage2DEXT = caps.get(1617);
        glCompressedMultiTexSubImage1DEXT = caps.get(1618);
        glGetCompressedMultiTexImageEXT = caps.get(1619);
        glMatrixLoadTransposefEXT = caps.get(1620);
        glMatrixLoadTransposedEXT = caps.get(1621);
        glMatrixMultTransposefEXT = caps.get(1622);
        glMatrixMultTransposedEXT = caps.get(1623);
        glNamedBufferDataEXT = caps.get(1624);
        glNamedBufferSubDataEXT = caps.get(1625);
        glMapNamedBufferEXT = caps.get(1626);
        glUnmapNamedBufferEXT = caps.get(1627);
        glGetNamedBufferParameterivEXT = caps.get(1628);
        glGetNamedBufferSubDataEXT = caps.get(1629);
        glProgramUniform1fEXT = caps.get(1630);
        glProgramUniform2fEXT = caps.get(1631);
        glProgramUniform3fEXT = caps.get(1632);
        glProgramUniform4fEXT = caps.get(1633);
        glProgramUniform1iEXT = caps.get(1634);
        glProgramUniform2iEXT = caps.get(1635);
        glProgramUniform3iEXT = caps.get(1636);
        glProgramUniform4iEXT = caps.get(1637);
        glProgramUniform1fvEXT = caps.get(1638);
        glProgramUniform2fvEXT = caps.get(1639);
        glProgramUniform3fvEXT = caps.get(1640);
        glProgramUniform4fvEXT = caps.get(1641);
        glProgramUniform1ivEXT = caps.get(1642);
        glProgramUniform2ivEXT = caps.get(1643);
        glProgramUniform3ivEXT = caps.get(1644);
        glProgramUniform4ivEXT = caps.get(1645);
        glProgramUniformMatrix2fvEXT = caps.get(1646);
        glProgramUniformMatrix3fvEXT = caps.get(1647);
        glProgramUniformMatrix4fvEXT = caps.get(1648);
        glProgramUniformMatrix2x3fvEXT = caps.get(1649);
        glProgramUniformMatrix3x2fvEXT = caps.get(1650);
        glProgramUniformMatrix2x4fvEXT = caps.get(1651);
        glProgramUniformMatrix4x2fvEXT = caps.get(1652);
        glProgramUniformMatrix3x4fvEXT = caps.get(1653);
        glProgramUniformMatrix4x3fvEXT = caps.get(1654);
        glTextureBufferEXT = caps.get(1655);
        glMultiTexBufferEXT = caps.get(1656);
        glTextureParameterIivEXT = caps.get(1657);
        glTextureParameterIuivEXT = caps.get(1658);
        glGetTextureParameterIivEXT = caps.get(1659);
        glGetTextureParameterIuivEXT = caps.get(1660);
        glMultiTexParameterIivEXT = caps.get(1661);
        glMultiTexParameterIuivEXT = caps.get(1662);
        glGetMultiTexParameterIivEXT = caps.get(1663);
        glGetMultiTexParameterIuivEXT = caps.get(1664);
        glProgramUniform1uiEXT = caps.get(1665);
        glProgramUniform2uiEXT = caps.get(1666);
        glProgramUniform3uiEXT = caps.get(1667);
        glProgramUniform4uiEXT = caps.get(1668);
        glProgramUniform1uivEXT = caps.get(1669);
        glProgramUniform2uivEXT = caps.get(1670);
        glProgramUniform3uivEXT = caps.get(1671);
        glProgramUniform4uivEXT = caps.get(1672);
        glNamedProgramLocalParameters4fvEXT = caps.get(1673);
        glNamedProgramLocalParameterI4iEXT = caps.get(1674);
        glNamedProgramLocalParameterI4ivEXT = caps.get(1675);
        glNamedProgramLocalParametersI4ivEXT = caps.get(1676);
        glNamedProgramLocalParameterI4uiEXT = caps.get(1677);
        glNamedProgramLocalParameterI4uivEXT = caps.get(1678);
        glNamedProgramLocalParametersI4uivEXT = caps.get(1679);
        glGetNamedProgramLocalParameterIivEXT = caps.get(1680);
        glGetNamedProgramLocalParameterIuivEXT = caps.get(1681);
        glNamedRenderbufferStorageEXT = caps.get(1682);
        glGetNamedRenderbufferParameterivEXT = caps.get(1683);
        glNamedRenderbufferStorageMultisampleEXT = caps.get(1684);
        glNamedRenderbufferStorageMultisampleCoverageEXT = caps.get(1685);
        glCheckNamedFramebufferStatusEXT = caps.get(1686);
        glNamedFramebufferTexture1DEXT = caps.get(1687);
        glNamedFramebufferTexture2DEXT = caps.get(1688);
        glNamedFramebufferTexture3DEXT = caps.get(1689);
        glNamedFramebufferRenderbufferEXT = caps.get(1690);
        glGetNamedFramebufferAttachmentParameterivEXT = caps.get(1691);
        glGenerateTextureMipmapEXT = caps.get(1692);
        glGenerateMultiTexMipmapEXT = caps.get(1693);
        glFramebufferDrawBufferEXT = caps.get(1694);
        glFramebufferDrawBuffersEXT = caps.get(1695);
        glFramebufferReadBufferEXT = caps.get(1696);
        glGetFramebufferParameterivEXT = caps.get(1697);
        glNamedCopyBufferSubDataEXT = caps.get(1698);
        glNamedFramebufferTextureEXT = caps.get(1699);
        glNamedFramebufferTextureLayerEXT = caps.get(1700);
        glNamedFramebufferTextureFaceEXT = caps.get(1701);
        glTextureRenderbufferEXT = caps.get(1702);
        glMultiTexRenderbufferEXT = caps.get(1703);
        glVertexArrayVertexOffsetEXT = caps.get(1704);
        glVertexArrayColorOffsetEXT = caps.get(1705);
        glVertexArrayEdgeFlagOffsetEXT = caps.get(1706);
        glVertexArrayIndexOffsetEXT = caps.get(1707);
        glVertexArrayNormalOffsetEXT = caps.get(1708);
        glVertexArrayTexCoordOffsetEXT = caps.get(1709);
        glVertexArrayMultiTexCoordOffsetEXT = caps.get(1710);
        glVertexArrayFogCoordOffsetEXT = caps.get(1711);
        glVertexArraySecondaryColorOffsetEXT = caps.get(1712);
        glVertexArrayVertexAttribOffsetEXT = caps.get(1713);
        glVertexArrayVertexAttribIOffsetEXT = caps.get(1714);
        glEnableVertexArrayEXT = caps.get(1715);
        glDisableVertexArrayEXT = caps.get(1716);
        glEnableVertexArrayAttribEXT = caps.get(1717);
        glDisableVertexArrayAttribEXT = caps.get(1718);
        glGetVertexArrayIntegervEXT = caps.get(1719);
        glGetVertexArrayPointervEXT = caps.get(1720);
        glGetVertexArrayIntegeri_vEXT = caps.get(1721);
        glGetVertexArrayPointeri_vEXT = caps.get(1722);
        glMapNamedBufferRangeEXT = caps.get(1723);
        glFlushMappedNamedBufferRangeEXT = caps.get(1724);
        glColorMaskIndexedEXT = caps.get(1725);
        glDrawArraysInstancedEXT = caps.get(1726);
        glDrawElementsInstancedEXT = caps.get(1727);
        glEGLImageTargetTexStorageEXT = caps.get(1728);
        glEGLImageTargetTextureStorageEXT = caps.get(1729);
        glBufferStorageExternalEXT = caps.get(1730);
        glNamedBufferStorageExternalEXT = caps.get(1731);
        glBlitFramebufferEXT = caps.get(1732);
        glRenderbufferStorageMultisampleEXT = caps.get(1733);
        glIsRenderbufferEXT = caps.get(1734);
        glBindRenderbufferEXT = caps.get(1735);
        glDeleteRenderbuffersEXT = caps.get(1736);
        glGenRenderbuffersEXT = caps.get(1737);
        glRenderbufferStorageEXT = caps.get(1738);
        glGetRenderbufferParameterivEXT = caps.get(1739);
        glIsFramebufferEXT = caps.get(1740);
        glBindFramebufferEXT = caps.get(1741);
        glDeleteFramebuffersEXT = caps.get(1742);
        glGenFramebuffersEXT = caps.get(1743);
        glCheckFramebufferStatusEXT = caps.get(1744);
        glFramebufferTexture1DEXT = caps.get(1745);
        glFramebufferTexture2DEXT = caps.get(1746);
        glFramebufferTexture3DEXT = caps.get(1747);
        glFramebufferRenderbufferEXT = caps.get(1748);
        glGetFramebufferAttachmentParameterivEXT = caps.get(1749);
        glGenerateMipmapEXT = caps.get(1750);
        glProgramParameteriEXT = caps.get(1751);
        glFramebufferTextureEXT = caps.get(1752);
        glFramebufferTextureLayerEXT = caps.get(1753);
        glFramebufferTextureFaceEXT = caps.get(1754);
        glProgramEnvParameters4fvEXT = caps.get(1755);
        glProgramLocalParameters4fvEXT = caps.get(1756);
        glVertexAttribI1iEXT = caps.get(1757);
        glVertexAttribI2iEXT = caps.get(1758);
        glVertexAttribI3iEXT = caps.get(1759);
        glVertexAttribI4iEXT = caps.get(1760);
        glVertexAttribI1uiEXT = caps.get(1761);
        glVertexAttribI2uiEXT = caps.get(1762);
        glVertexAttribI3uiEXT = caps.get(1763);
        glVertexAttribI4uiEXT = caps.get(1764);
        glVertexAttribI1ivEXT = caps.get(1765);
        glVertexAttribI2ivEXT = caps.get(1766);
        glVertexAttribI3ivEXT = caps.get(1767);
        glVertexAttribI4ivEXT = caps.get(1768);
        glVertexAttribI1uivEXT = caps.get(1769);
        glVertexAttribI2uivEXT = caps.get(1770);
        glVertexAttribI3uivEXT = caps.get(1771);
        glVertexAttribI4uivEXT = caps.get(1772);
        glVertexAttribI4bvEXT = caps.get(1773);
        glVertexAttribI4svEXT = caps.get(1774);
        glVertexAttribI4ubvEXT = caps.get(1775);
        glVertexAttribI4usvEXT = caps.get(1776);
        glVertexAttribIPointerEXT = caps.get(1777);
        glGetVertexAttribIivEXT = caps.get(1778);
        glGetVertexAttribIuivEXT = caps.get(1779);
        glGetUniformuivEXT = caps.get(1780);
        glBindFragDataLocationEXT = caps.get(1781);
        glGetFragDataLocationEXT = caps.get(1782);
        glUniform1uiEXT = caps.get(1783);
        glUniform2uiEXT = caps.get(1784);
        glUniform3uiEXT = caps.get(1785);
        glUniform4uiEXT = caps.get(1786);
        glUniform1uivEXT = caps.get(1787);
        glUniform2uivEXT = caps.get(1788);
        glUniform3uivEXT = caps.get(1789);
        glUniform4uivEXT = caps.get(1790);
        glGetUnsignedBytevEXT = caps.get(1791);
        glGetUnsignedBytei_vEXT = caps.get(1792);
        glDeleteMemoryObjectsEXT = caps.get(1793);
        glIsMemoryObjectEXT = caps.get(1794);
        glCreateMemoryObjectsEXT = caps.get(1795);
        glMemoryObjectParameterivEXT = caps.get(1796);
        glGetMemoryObjectParameterivEXT = caps.get(1797);
        glTexStorageMem2DEXT = caps.get(1798);
        glTexStorageMem2DMultisampleEXT = caps.get(1799);
        glTexStorageMem3DEXT = caps.get(1800);
        glTexStorageMem3DMultisampleEXT = caps.get(1801);
        glBufferStorageMemEXT = caps.get(1802);
        glTextureStorageMem2DEXT = caps.get(1803);
        glTextureStorageMem2DMultisampleEXT = caps.get(1804);
        glTextureStorageMem3DEXT = caps.get(1805);
        glTextureStorageMem3DMultisampleEXT = caps.get(1806);
        glNamedBufferStorageMemEXT = caps.get(1807);
        glTexStorageMem1DEXT = caps.get(1808);
        glTextureStorageMem1DEXT = caps.get(1809);
        glImportMemoryFdEXT = caps.get(1810);
        glImportMemoryWin32HandleEXT = caps.get(1811);
        glImportMemoryWin32NameEXT = caps.get(1812);
        glPointParameterfEXT = caps.get(1813);
        glPointParameterfvEXT = caps.get(1814);
        glPolygonOffsetClampEXT = caps.get(1815);
        glProvokingVertexEXT = caps.get(1816);
        glRasterSamplesEXT = caps.get(1817);
        glSecondaryColor3bEXT = caps.get(1818);
        glSecondaryColor3sEXT = caps.get(1819);
        glSecondaryColor3iEXT = caps.get(1820);
        glSecondaryColor3fEXT = caps.get(1821);
        glSecondaryColor3dEXT = caps.get(1822);
        glSecondaryColor3ubEXT = caps.get(1823);
        glSecondaryColor3usEXT = caps.get(1824);
        glSecondaryColor3uiEXT = caps.get(1825);
        glSecondaryColor3bvEXT = caps.get(1826);
        glSecondaryColor3svEXT = caps.get(1827);
        glSecondaryColor3ivEXT = caps.get(1828);
        glSecondaryColor3fvEXT = caps.get(1829);
        glSecondaryColor3dvEXT = caps.get(1830);
        glSecondaryColor3ubvEXT = caps.get(1831);
        glSecondaryColor3usvEXT = caps.get(1832);
        glSecondaryColor3uivEXT = caps.get(1833);
        glSecondaryColorPointerEXT = caps.get(1834);
        glGenSemaphoresEXT = caps.get(1835);
        glDeleteSemaphoresEXT = caps.get(1836);
        glIsSemaphoreEXT = caps.get(1837);
        glSemaphoreParameterui64vEXT = caps.get(1838);
        glGetSemaphoreParameterui64vEXT = caps.get(1839);
        glWaitSemaphoreEXT = caps.get(1840);
        glSignalSemaphoreEXT = caps.get(1841);
        glImportSemaphoreFdEXT = caps.get(1842);
        glImportSemaphoreWin32HandleEXT = caps.get(1843);
        glImportSemaphoreWin32NameEXT = caps.get(1844);
        glUseShaderProgramEXT = caps.get(1845);
        glActiveProgramEXT = caps.get(1846);
        glCreateShaderProgramEXT = caps.get(1847);
        glFramebufferFetchBarrierEXT = caps.get(1848);
        glBindImageTextureEXT = caps.get(1849);
        glMemoryBarrierEXT = caps.get(1850);
        glStencilClearTagEXT = caps.get(1851);
        glActiveStencilFaceEXT = caps.get(1852);
        glTexBufferEXT = caps.get(1853);
        glClearColorIiEXT = caps.get(1854);
        glClearColorIuiEXT = caps.get(1855);
        glTexParameterIivEXT = caps.get(1856);
        glTexParameterIuivEXT = caps.get(1857);
        glGetTexParameterIivEXT = caps.get(1858);
        glGetTexParameterIuivEXT = caps.get(1859);
        glTexStorage1DEXT = caps.get(1860);
        glTexStorage2DEXT = caps.get(1861);
        glTexStorage3DEXT = caps.get(1862);
        glGetQueryObjecti64vEXT = caps.get(1863);
        glGetQueryObjectui64vEXT = caps.get(1864);
        glBindBufferRangeEXT = caps.get(1865);
        glBindBufferOffsetEXT = caps.get(1866);
        glBindBufferBaseEXT = caps.get(1867);
        glBeginTransformFeedbackEXT = caps.get(1868);
        glEndTransformFeedbackEXT = caps.get(1869);
        glTransformFeedbackVaryingsEXT = caps.get(1870);
        glGetTransformFeedbackVaryingEXT = caps.get(1871);
        glVertexAttribL1dEXT = caps.get(1872);
        glVertexAttribL2dEXT = caps.get(1873);
        glVertexAttribL3dEXT = caps.get(1874);
        glVertexAttribL4dEXT = caps.get(1875);
        glVertexAttribL1dvEXT = caps.get(1876);
        glVertexAttribL2dvEXT = caps.get(1877);
        glVertexAttribL3dvEXT = caps.get(1878);
        glVertexAttribL4dvEXT = caps.get(1879);
        glVertexAttribLPointerEXT = caps.get(1880);
        glGetVertexAttribLdvEXT = caps.get(1881);
        glAcquireKeyedMutexWin32EXT = caps.get(1882);
        glReleaseKeyedMutexWin32EXT = caps.get(1883);
        glWindowRectanglesEXT = caps.get(1884);
        glImportSyncEXT = caps.get(1885);
        glFrameTerminatorGREMEDY = caps.get(1886);
        glStringMarkerGREMEDY = caps.get(1887);
        glApplyFramebufferAttachmentCMAAINTEL = caps.get(1888);
        glSyncTextureINTEL = caps.get(1889);
        glUnmapTexture2DINTEL = caps.get(1890);
        glMapTexture2DINTEL = caps.get(1891);
        glBeginPerfQueryINTEL = caps.get(1892);
        glCreatePerfQueryINTEL = caps.get(1893);
        glDeletePerfQueryINTEL = caps.get(1894);
        glEndPerfQueryINTEL = caps.get(1895);
        glGetFirstPerfQueryIdINTEL = caps.get(1896);
        glGetNextPerfQueryIdINTEL = caps.get(1897);
        glGetPerfCounterInfoINTEL = caps.get(1898);
        glGetPerfQueryDataINTEL = caps.get(1899);
        glGetPerfQueryIdByNameINTEL = caps.get(1900);
        glGetPerfQueryInfoINTEL = caps.get(1901);
        glBlendBarrierKHR = caps.get(1902);
        glMaxShaderCompilerThreadsKHR = caps.get(1903);
        glFramebufferParameteriMESA = caps.get(1904);
        glGetFramebufferParameterivMESA = caps.get(1905);
        glAlphaToCoverageDitherControlNV = caps.get(1906);
        glMultiDrawArraysIndirectBindlessNV = caps.get(1907);
        glMultiDrawElementsIndirectBindlessNV = caps.get(1908);
        glMultiDrawArraysIndirectBindlessCountNV = caps.get(1909);
        glMultiDrawElementsIndirectBindlessCountNV = caps.get(1910);
        glGetTextureHandleNV = caps.get(1911);
        glGetTextureSamplerHandleNV = caps.get(1912);
        glMakeTextureHandleResidentNV = caps.get(1913);
        glMakeTextureHandleNonResidentNV = caps.get(1914);
        glGetImageHandleNV = caps.get(1915);
        glMakeImageHandleResidentNV = caps.get(1916);
        glMakeImageHandleNonResidentNV = caps.get(1917);
        glUniformHandleui64NV = caps.get(1918);
        glUniformHandleui64vNV = caps.get(1919);
        glProgramUniformHandleui64NV = caps.get(1920);
        glProgramUniformHandleui64vNV = caps.get(1921);
        glIsTextureHandleResidentNV = caps.get(1922);
        glIsImageHandleResidentNV = caps.get(1923);
        glBlendParameteriNV = caps.get(1924);
        glBlendBarrierNV = caps.get(1925);
        glViewportPositionWScaleNV = caps.get(1926);
        glCreateStatesNV = caps.get(1927);
        glDeleteStatesNV = caps.get(1928);
        glIsStateNV = caps.get(1929);
        glStateCaptureNV = caps.get(1930);
        glGetCommandHeaderNV = caps.get(1931);
        glGetStageIndexNV = caps.get(1932);
        glDrawCommandsNV = caps.get(1933);
        glDrawCommandsAddressNV = caps.get(1934);
        glDrawCommandsStatesNV = caps.get(1935);
        glDrawCommandsStatesAddressNV = caps.get(1936);
        glCreateCommandListsNV = caps.get(1937);
        glDeleteCommandListsNV = caps.get(1938);
        glIsCommandListNV = caps.get(1939);
        glListDrawCommandsStatesClientNV = caps.get(1940);
        glCommandListSegmentsNV = caps.get(1941);
        glCompileCommandListNV = caps.get(1942);
        glCallCommandListNV = caps.get(1943);
        glBeginConditionalRenderNV = caps.get(1944);
        glEndConditionalRenderNV = caps.get(1945);
        glSubpixelPrecisionBiasNV = caps.get(1946);
        glConservativeRasterParameterfNV = caps.get(1947);
        glConservativeRasterParameteriNV = caps.get(1948);
        glCopyImageSubDataNV = caps.get(1949);
        glDepthRangedNV = caps.get(1950);
        glClearDepthdNV = caps.get(1951);
        glDepthBoundsdNV = caps.get(1952);
        glDrawTextureNV = caps.get(1953);
        glDrawVkImageNV = caps.get(1954);
        glGetVkProcAddrNV = caps.get(1955);
        glWaitVkSemaphoreNV = caps.get(1956);
        glSignalVkSemaphoreNV = caps.get(1957);
        glSignalVkFenceNV = caps.get(1958);
        glGetMultisamplefvNV = caps.get(1959);
        glSampleMaskIndexedNV = caps.get(1960);
        glTexRenderbufferNV = caps.get(1961);
        glDeleteFencesNV = caps.get(1962);
        glGenFencesNV = caps.get(1963);
        glIsFenceNV = caps.get(1964);
        glTestFenceNV = caps.get(1965);
        glGetFenceivNV = caps.get(1966);
        glFinishFenceNV = caps.get(1967);
        glSetFenceNV = caps.get(1968);
        glFragmentCoverageColorNV = caps.get(1969);
        glCoverageModulationTableNV = caps.get(1970);
        glGetCoverageModulationTableNV = caps.get(1971);
        glCoverageModulationNV = caps.get(1972);
        glRenderbufferStorageMultisampleCoverageNV = caps.get(1973);
        glRenderGpuMaskNV = caps.get(1974);
        glMulticastBufferSubDataNV = caps.get(1975);
        glMulticastCopyBufferSubDataNV = caps.get(1976);
        glMulticastCopyImageSubDataNV = caps.get(1977);
        glMulticastBlitFramebufferNV = caps.get(1978);
        glMulticastFramebufferSampleLocationsfvNV = caps.get(1979);
        glMulticastBarrierNV = caps.get(1980);
        glMulticastWaitSyncNV = caps.get(1981);
        glMulticastGetQueryObjectivNV = caps.get(1982);
        glMulticastGetQueryObjectuivNV = caps.get(1983);
        glMulticastGetQueryObjecti64vNV = caps.get(1984);
        glMulticastGetQueryObjectui64vNV = caps.get(1985);
        glVertex2hNV = caps.get(1986);
        glVertex2hvNV = caps.get(1987);
        glVertex3hNV = caps.get(1988);
        glVertex3hvNV = caps.get(1989);
        glVertex4hNV = caps.get(1990);
        glVertex4hvNV = caps.get(1991);
        glNormal3hNV = caps.get(1992);
        glNormal3hvNV = caps.get(1993);
        glColor3hNV = caps.get(1994);
        glColor3hvNV = caps.get(1995);
        glColor4hNV = caps.get(1996);
        glColor4hvNV = caps.get(1997);
        glTexCoord1hNV = caps.get(1998);
        glTexCoord1hvNV = caps.get(1999);
        glTexCoord2hNV = caps.get(2000);
        glTexCoord2hvNV = caps.get(2001);
        glTexCoord3hNV = caps.get(2002);
        glTexCoord3hvNV = caps.get(2003);
        glTexCoord4hNV = caps.get(2004);
        glTexCoord4hvNV = caps.get(2005);
        glMultiTexCoord1hNV = caps.get(2006);
        glMultiTexCoord1hvNV = caps.get(2007);
        glMultiTexCoord2hNV = caps.get(2008);
        glMultiTexCoord2hvNV = caps.get(2009);
        glMultiTexCoord3hNV = caps.get(2010);
        glMultiTexCoord3hvNV = caps.get(2011);
        glMultiTexCoord4hNV = caps.get(2012);
        glMultiTexCoord4hvNV = caps.get(2013);
        glFogCoordhNV = caps.get(2014);
        glFogCoordhvNV = caps.get(2015);
        glSecondaryColor3hNV = caps.get(2016);
        glSecondaryColor3hvNV = caps.get(2017);
        glVertexWeighthNV = caps.get(2018);
        glVertexWeighthvNV = caps.get(2019);
        glVertexAttrib1hNV = caps.get(2020);
        glVertexAttrib1hvNV = caps.get(2021);
        glVertexAttrib2hNV = caps.get(2022);
        glVertexAttrib2hvNV = caps.get(2023);
        glVertexAttrib3hNV = caps.get(2024);
        glVertexAttrib3hvNV = caps.get(2025);
        glVertexAttrib4hNV = caps.get(2026);
        glVertexAttrib4hvNV = caps.get(2027);
        glVertexAttribs1hvNV = caps.get(2028);
        glVertexAttribs2hvNV = caps.get(2029);
        glVertexAttribs3hvNV = caps.get(2030);
        glVertexAttribs4hvNV = caps.get(2031);
        glGetInternalformatSampleivNV = caps.get(2032);
        glGetMemoryObjectDetachedResourcesuivNV = caps.get(2033);
        glResetMemoryObjectParameterNV = caps.get(2034);
        glTexAttachMemoryNV = caps.get(2035);
        glBufferAttachMemoryNV = caps.get(2036);
        glTextureAttachMemoryNV = caps.get(2037);
        glNamedBufferAttachMemoryNV = caps.get(2038);
        glBufferPageCommitmentMemNV = caps.get(2039);
        glNamedBufferPageCommitmentMemNV = caps.get(2040);
        glTexPageCommitmentMemNV = caps.get(2041);
        glTexturePageCommitmentMemNV = caps.get(2042);
        glDrawMeshTasksNV = caps.get(2043);
        glDrawMeshTasksIndirectNV = caps.get(2044);
        glMultiDrawMeshTasksIndirectNV = caps.get(2045);
        glMultiDrawMeshTasksIndirectCountNV = caps.get(2046);
        glPathCommandsNV = caps.get(2047);
        glPathCoordsNV = caps.get(2048);
        glPathSubCommandsNV = caps.get(2049);
        glPathSubCoordsNV = caps.get(2050);
        glPathStringNV = caps.get(2051);
        glPathGlyphsNV = caps.get(2052);
        glPathGlyphRangeNV = caps.get(2053);
        glPathGlyphIndexArrayNV = caps.get(2054);
        glPathMemoryGlyphIndexArrayNV = caps.get(2055);
        glCopyPathNV = caps.get(2056);
        glWeightPathsNV = caps.get(2057);
        glInterpolatePathsNV = caps.get(2058);
        glTransformPathNV = caps.get(2059);
        glPathParameterivNV = caps.get(2060);
        glPathParameteriNV = caps.get(2061);
        glPathParameterfvNV = caps.get(2062);
        glPathParameterfNV = caps.get(2063);
        glPathDashArrayNV = caps.get(2064);
        glGenPathsNV = caps.get(2065);
        glDeletePathsNV = caps.get(2066);
        glIsPathNV = caps.get(2067);
        glPathStencilFuncNV = caps.get(2068);
        glPathStencilDepthOffsetNV = caps.get(2069);
        glStencilFillPathNV = caps.get(2070);
        glStencilStrokePathNV = caps.get(2071);
        glStencilFillPathInstancedNV = caps.get(2072);
        glStencilStrokePathInstancedNV = caps.get(2073);
        glPathCoverDepthFuncNV = caps.get(2074);
        glPathColorGenNV = caps.get(2075);
        glPathTexGenNV = caps.get(2076);
        glPathFogGenNV = caps.get(2077);
        glCoverFillPathNV = caps.get(2078);
        glCoverStrokePathNV = caps.get(2079);
        glCoverFillPathInstancedNV = caps.get(2080);
        glCoverStrokePathInstancedNV = caps.get(2081);
        glStencilThenCoverFillPathNV = caps.get(2082);
        glStencilThenCoverStrokePathNV = caps.get(2083);
        glStencilThenCoverFillPathInstancedNV = caps.get(2084);
        glStencilThenCoverStrokePathInstancedNV = caps.get(2085);
        glPathGlyphIndexRangeNV = caps.get(2086);
        glProgramPathFragmentInputGenNV = caps.get(2087);
        glGetPathParameterivNV = caps.get(2088);
        glGetPathParameterfvNV = caps.get(2089);
        glGetPathCommandsNV = caps.get(2090);
        glGetPathCoordsNV = caps.get(2091);
        glGetPathDashArrayNV = caps.get(2092);
        glGetPathMetricsNV = caps.get(2093);
        glGetPathMetricRangeNV = caps.get(2094);
        glGetPathSpacingNV = caps.get(2095);
        glGetPathColorGenivNV = caps.get(2096);
        glGetPathColorGenfvNV = caps.get(2097);
        glGetPathTexGenivNV = caps.get(2098);
        glGetPathTexGenfvNV = caps.get(2099);
        glIsPointInFillPathNV = caps.get(2100);
        glIsPointInStrokePathNV = caps.get(2101);
        glGetPathLengthNV = caps.get(2102);
        glPointAlongPathNV = caps.get(2103);
        glMatrixLoad3x2fNV = caps.get(2104);
        glMatrixLoad3x3fNV = caps.get(2105);
        glMatrixLoadTranspose3x3fNV = caps.get(2106);
        glMatrixMult3x2fNV = caps.get(2107);
        glMatrixMult3x3fNV = caps.get(2108);
        glMatrixMultTranspose3x3fNV = caps.get(2109);
        glGetProgramResourcefvNV = caps.get(2110);
        glPixelDataRangeNV = caps.get(2111);
        glFlushPixelDataRangeNV = caps.get(2112);
        glPointParameteriNV = caps.get(2113);
        glPointParameterivNV = caps.get(2114);
        glPrimitiveRestartNV = caps.get(2115);
        glPrimitiveRestartIndexNV = caps.get(2116);
        glQueryResourceNV = caps.get(2117);
        glGenQueryResourceTagNV = caps.get(2118);
        glDeleteQueryResourceTagNV = caps.get(2119);
        glQueryResourceTagNV = caps.get(2120);
        glFramebufferSampleLocationsfvNV = caps.get(2121);
        glNamedFramebufferSampleLocationsfvNV = caps.get(2122);
        glResolveDepthValuesNV = caps.get(2123);
        glScissorExclusiveArrayvNV = caps.get(2124);
        glScissorExclusiveNV = caps.get(2125);
        glMakeBufferResidentNV = caps.get(2126);
        glMakeBufferNonResidentNV = caps.get(2127);
        glIsBufferResidentNV = caps.get(2128);
        glMakeNamedBufferResidentNV = caps.get(2129);
        glMakeNamedBufferNonResidentNV = caps.get(2130);
        glIsNamedBufferResidentNV = caps.get(2131);
        glGetBufferParameterui64vNV = caps.get(2132);
        glGetNamedBufferParameterui64vNV = caps.get(2133);
        glGetIntegerui64vNV = caps.get(2134);
        glUniformui64NV = caps.get(2135);
        glUniformui64vNV = caps.get(2136);
        glProgramUniformui64NV = caps.get(2137);
        glProgramUniformui64vNV = caps.get(2138);
        glBindShadingRateImageNV = caps.get(2139);
        glShadingRateImagePaletteNV = caps.get(2140);
        glGetShadingRateImagePaletteNV = caps.get(2141);
        glShadingRateImageBarrierNV = caps.get(2142);
        glShadingRateSampleOrderNV = caps.get(2143);
        glShadingRateSampleOrderCustomNV = caps.get(2144);
        glGetShadingRateSampleLocationivNV = caps.get(2145);
        glTextureBarrierNV = caps.get(2146);
        glTexImage2DMultisampleCoverageNV = caps.get(2147);
        glTexImage3DMultisampleCoverageNV = caps.get(2148);
        glTextureImage2DMultisampleNV = caps.get(2149);
        glTextureImage3DMultisampleNV = caps.get(2150);
        glTextureImage2DMultisampleCoverageNV = caps.get(2151);
        glTextureImage3DMultisampleCoverageNV = caps.get(2152);
        glCreateSemaphoresNV = caps.get(2153);
        glSemaphoreParameterivNV = caps.get(2154);
        glGetSemaphoreParameterivNV = caps.get(2155);
        glBeginTransformFeedbackNV = caps.get(2156);
        glEndTransformFeedbackNV = caps.get(2157);
        glTransformFeedbackAttribsNV = caps.get(2158);
        glBindBufferRangeNV = caps.get(2159);
        glBindBufferOffsetNV = caps.get(2160);
        glBindBufferBaseNV = caps.get(2161);
        glTransformFeedbackVaryingsNV = caps.get(2162);
        glActiveVaryingNV = caps.get(2163);
        glGetVaryingLocationNV = caps.get(2164);
        glGetActiveVaryingNV = caps.get(2165);
        glGetTransformFeedbackVaryingNV = caps.get(2166);
        glTransformFeedbackStreamAttribsNV = caps.get(2167);
        glBindTransformFeedbackNV = caps.get(2168);
        glDeleteTransformFeedbacksNV = caps.get(2169);
        glGenTransformFeedbacksNV = caps.get(2170);
        glIsTransformFeedbackNV = caps.get(2171);
        glPauseTransformFeedbackNV = caps.get(2172);
        glResumeTransformFeedbackNV = caps.get(2173);
        glDrawTransformFeedbackNV = caps.get(2174);
        glVertexArrayRangeNV = caps.get(2175);
        glFlushVertexArrayRangeNV = caps.get(2176);
        glVertexAttribL1i64NV = caps.get(2177);
        glVertexAttribL2i64NV = caps.get(2178);
        glVertexAttribL3i64NV = caps.get(2179);
        glVertexAttribL4i64NV = caps.get(2180);
        glVertexAttribL1i64vNV = caps.get(2181);
        glVertexAttribL2i64vNV = caps.get(2182);
        glVertexAttribL3i64vNV = caps.get(2183);
        glVertexAttribL4i64vNV = caps.get(2184);
        glVertexAttribL1ui64NV = caps.get(2185);
        glVertexAttribL2ui64NV = caps.get(2186);
        glVertexAttribL3ui64NV = caps.get(2187);
        glVertexAttribL4ui64NV = caps.get(2188);
        glVertexAttribL1ui64vNV = caps.get(2189);
        glVertexAttribL2ui64vNV = caps.get(2190);
        glVertexAttribL3ui64vNV = caps.get(2191);
        glVertexAttribL4ui64vNV = caps.get(2192);
        glGetVertexAttribLi64vNV = caps.get(2193);
        glGetVertexAttribLui64vNV = caps.get(2194);
        glVertexAttribLFormatNV = caps.get(2195);
        glBufferAddressRangeNV = caps.get(2196);
        glVertexFormatNV = caps.get(2197);
        glNormalFormatNV = caps.get(2198);
        glColorFormatNV = caps.get(2199);
        glIndexFormatNV = caps.get(2200);
        glTexCoordFormatNV = caps.get(2201);
        glEdgeFlagFormatNV = caps.get(2202);
        glSecondaryColorFormatNV = caps.get(2203);
        glFogCoordFormatNV = caps.get(2204);
        glVertexAttribFormatNV = caps.get(2205);
        glVertexAttribIFormatNV = caps.get(2206);
        glGetIntegerui64i_vNV = caps.get(2207);
        glViewportSwizzleNV = caps.get(2208);
        glBeginConditionalRenderNVX = caps.get(2209);
        glEndConditionalRenderNVX = caps.get(2210);
        glAsyncCopyImageSubDataNVX = caps.get(2211);
        glAsyncCopyBufferSubDataNVX = caps.get(2212);
        glUploadGpuMaskNVX = caps.get(2213);
        glMulticastViewportArrayvNVX = caps.get(2214);
        glMulticastScissorArrayvNVX = caps.get(2215);
        glMulticastViewportPositionWScaleNVX = caps.get(2216);
        glCreateProgressFenceNVX = caps.get(2217);
        glSignalSemaphoreui64NVX = caps.get(2218);
        glWaitSemaphoreui64NVX = caps.get(2219);
        glClientWaitSemaphoreui64NVX = caps.get(2220);
        glFramebufferTextureMultiviewOVR = caps.get(2221);
        glNamedFramebufferTextureMultiviewOVR = caps.get(2222);

        addresses = ThreadLocalUtil.setupAddressBuffer(caps);
    }

    /** Returns the buffer of OpenGL function pointers. */
    public PointerBuffer getAddressBuffer() {
        return addresses;
    }

    /** Ensures that the lwjgl_opengl shared library has been loaded. */
    public static void initialize() {
        // intentionally empty to trigger static initializer
    }

    private static boolean check_GL11(FunctionProvider provider, PointerBuffer caps, Set<String> ext, boolean fc) {
        int flag0 = !fc || ext.contains("GL_NV_vertex_buffer_unified_memory") ? 0 : Integer.MIN_VALUE;

        return ((fc || checkFunctions(provider, caps, new int[] {
                        2, 3, 4, 5, 6, 8, 10, 11, 13, 16, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45,
                        46, 47, 48, 49, 50, 52, 53, 54, 56, 64, 65, 66, 67, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 85, 86, 87, 88, 90, 93, 99, 100, 101,
                        102, 103, 104, 105, 106, 107, 108, 110, 112, 113, 114, 115, 116, 123, 124, 125, 126, 127, 128, 129, 130, 131, 132, 133, 134, 135, 136, 138, 140,
                        141, 142, 143, 144, 145, 146, 147, 148, 150, 151, 152, 153, 154, 156, 157, 158, 159, 160, 161, 162, 163, 164, 165, 166, 167, 168, 169, 170, 171,
                        172, 173, 174, 175, 176, 177, 178, 179, 180, 181, 182, 183, 184, 185, 186, 187, 188, 189, 192, 193, 194, 198, 199, 200, 201, 202, 203, 204, 205,
                        206, 207, 208, 209, 210, 211, 212, 213, 214, 215, 216, 217, 218, 219, 220, 221, 222, 223, 224, 225, 226, 227, 228, 229, 230, 231, 234, 235, 236,
                        237, 238, 239, 240, 241, 242, 243, 244, 245, 246, 248, 249, 253, 254, 255, 256, 257, 258, 259, 260, 261, 262, 263, 264, 265, 266, 267, 268, 269,
                        270, 271, 272, 273, 274, 275, 276, 277, 278, 279, 280, 281, 282, 283, 284, 285, 286, 287, 288, 289, 290, 291, 292, 293, 294, 295, 308, 309, 310,
                        311, 312, 313, 314, 315, 316, 317, 318, 319, 320, 321, 322, 323, 324, 325, 326, 327, 328, 329, 330, 331, 332, 333, 334
                },
                "glAccum", "glAlphaFunc", "glAreTexturesResident", "glArrayElement", "glBegin", "glBitmap", "glCallList", "glCallLists", "glClearAccum",
                "glClearIndex", "glClipPlane", "glColor3b", "glColor3s", "glColor3i", "glColor3f", "glColor3d", "glColor3ub", "glColor3us", "glColor3ui",
                "glColor3bv", "glColor3sv", "glColor3iv", "glColor3fv", "glColor3dv", "glColor3ubv", "glColor3usv", "glColor3uiv", "glColor4b", "glColor4s",
                "glColor4i", "glColor4f", "glColor4d", "glColor4ub", "glColor4us", "glColor4ui", "glColor4bv", "glColor4sv", "glColor4iv", "glColor4fv",
                "glColor4dv", "glColor4ubv", "glColor4usv", "glColor4uiv", "glColorMaterial", "glColorPointer", "glCopyPixels", "glDeleteLists", "glDrawPixels",
                "glEdgeFlag", "glEdgeFlagv", "glEdgeFlagPointer", "glEnd", "glEvalCoord1f", "glEvalCoord1fv", "glEvalCoord1d", "glEvalCoord1dv", "glEvalCoord2f",
                "glEvalCoord2fv", "glEvalCoord2d", "glEvalCoord2dv", "glEvalMesh1", "glEvalMesh2", "glEvalPoint1", "glEvalPoint2", "glFeedbackBuffer", "glFogi",
                "glFogiv", "glFogf", "glFogfv", "glGenLists", "glGetClipPlane", "glGetLightiv", "glGetLightfv", "glGetMapiv", "glGetMapfv", "glGetMapdv",
                "glGetMaterialiv", "glGetMaterialfv", "glGetPixelMapfv", "glGetPixelMapusv", "glGetPixelMapuiv", "glGetPolygonStipple", "glGetTexEnviv",
                "glGetTexEnvfv", "glGetTexGeniv", "glGetTexGenfv", "glGetTexGendv", "glIndexi", "glIndexub", "glIndexs", "glIndexf", "glIndexd", "glIndexiv",
                "glIndexubv", "glIndexsv", "glIndexfv", "glIndexdv", "glIndexMask", "glIndexPointer", "glInitNames", "glInterleavedArrays", "glIsList",
                "glLightModeli", "glLightModelf", "glLightModeliv", "glLightModelfv", "glLighti", "glLightf", "glLightiv", "glLightfv", "glLineStipple",
                "glListBase", "glLoadMatrixf", "glLoadMatrixd", "glLoadIdentity", "glLoadName", "glMap1f", "glMap1d", "glMap2f", "glMap2d", "glMapGrid1f",
                "glMapGrid1d", "glMapGrid2f", "glMapGrid2d", "glMateriali", "glMaterialf", "glMaterialiv", "glMaterialfv", "glMatrixMode", "glMultMatrixf",
                "glMultMatrixd", "glFrustum", "glNewList", "glEndList", "glNormal3f", "glNormal3b", "glNormal3s", "glNormal3i", "glNormal3d", "glNormal3fv",
                "glNormal3bv", "glNormal3sv", "glNormal3iv", "glNormal3dv", "glNormalPointer", "glOrtho", "glPassThrough", "glPixelMapfv", "glPixelMapusv",
                "glPixelMapuiv", "glPixelTransferi", "glPixelTransferf", "glPixelZoom", "glPolygonStipple", "glPushAttrib", "glPushClientAttrib", "glPopAttrib",
                "glPopClientAttrib", "glPopMatrix", "glPopName", "glPrioritizeTextures", "glPushMatrix", "glPushName", "glRasterPos2i", "glRasterPos2s",
                "glRasterPos2f", "glRasterPos2d", "glRasterPos2iv", "glRasterPos2sv", "glRasterPos2fv", "glRasterPos2dv", "glRasterPos3i", "glRasterPos3s",
                "glRasterPos3f", "glRasterPos3d", "glRasterPos3iv", "glRasterPos3sv", "glRasterPos3fv", "glRasterPos3dv", "glRasterPos4i", "glRasterPos4s",
                "glRasterPos4f", "glRasterPos4d", "glRasterPos4iv", "glRasterPos4sv", "glRasterPos4fv", "glRasterPos4dv", "glRecti", "glRects", "glRectf",
                "glRectd", "glRectiv", "glRectsv", "glRectfv", "glRectdv", "glRenderMode", "glRotatef", "glRotated", "glScalef", "glScaled", "glSelectBuffer",
                "glShadeModel", "glTexCoord1f", "glTexCoord1s", "glTexCoord1i", "glTexCoord1d", "glTexCoord1fv", "glTexCoord1sv", "glTexCoord1iv", "glTexCoord1dv",
                "glTexCoord2f", "glTexCoord2s", "glTexCoord2i", "glTexCoord2d", "glTexCoord2fv", "glTexCoord2sv", "glTexCoord2iv", "glTexCoord2dv", "glTexCoord3f",
                "glTexCoord3s", "glTexCoord3i", "glTexCoord3d", "glTexCoord3fv", "glTexCoord3sv", "glTexCoord3iv", "glTexCoord3dv", "glTexCoord4f", "glTexCoord4s",
                "glTexCoord4i", "glTexCoord4d", "glTexCoord4fv", "glTexCoord4sv", "glTexCoord4iv", "glTexCoord4dv", "glTexCoordPointer", "glTexEnvi", "glTexEnviv",
                "glTexEnvf", "glTexEnvfv", "glTexGeni", "glTexGeniv", "glTexGenf", "glTexGenfv", "glTexGend", "glTexGendv", "glTranslatef", "glTranslated",
                "glVertex2f", "glVertex2s", "glVertex2i", "glVertex2d", "glVertex2fv", "glVertex2sv", "glVertex2iv", "glVertex2dv", "glVertex3f", "glVertex3s",
                "glVertex3i", "glVertex3d", "glVertex3fv", "glVertex3sv", "glVertex3iv", "glVertex3dv", "glVertex4f", "glVertex4s", "glVertex4i", "glVertex4d",
                "glVertex4fv", "glVertex4sv", "glVertex4iv", "glVertex4dv", "glVertexPointer"
        )) & checkFunctions(provider, caps, new int[] {
                        0, 1, 7, 9, 12, 14, 15, 17, 51, 55, 57, 58, 59, flag0 + 60, 61, 62, 63, flag0 + 68, 83, 84, 89, 91, 92, 94, 95, 96, 97, 98, 109, 111, 117, 118, 119,
                        120, 121, 122, 137, 139, 149, 155, 190, 191, 195, 196, 197, 232, 233, 247, 250, 251, 252, 296, 297, 298, 299, 300, 301, 302, 303, 304, 305, 306,
                        307, 335
                },
                "glEnable", "glDisable", "glBindTexture", "glBlendFunc", "glClear", "glClearColor", "glClearDepth", "glClearStencil", "glColorMask", "glCullFace",
                "glDepthFunc", "glDepthMask", "glDepthRange", "glDisableClientState", "glDrawArrays", "glDrawBuffer", "glDrawElements", "glEnableClientState",
                "glFinish", "glFlush", "glFrontFace", "glGenTextures", "glDeleteTextures", "glGetBooleanv", "glGetFloatv", "glGetIntegerv", "glGetDoublev",
                "glGetError", "glGetPointerv", "glGetString", "glGetTexImage", "glGetTexLevelParameteriv", "glGetTexLevelParameterfv", "glGetTexParameteriv",
                "glGetTexParameterfv", "glHint", "glIsEnabled", "glIsTexture", "glLineWidth", "glLogicOp", "glPixelStorei", "glPixelStoref", "glPointSize",
                "glPolygonMode", "glPolygonOffset", "glReadBuffer", "glReadPixels", "glScissor", "glStencilFunc", "glStencilMask", "glStencilOp", "glTexImage1D",
                "glTexImage2D", "glCopyTexImage1D", "glCopyTexImage2D", "glCopyTexSubImage1D", "glCopyTexSubImage2D", "glTexParameteri", "glTexParameteriv",
                "glTexParameterf", "glTexParameterfv", "glTexSubImage1D", "glTexSubImage2D", "glViewport"
        ) & ext.contains("OpenGL11")) || reportMissing("GL", "OpenGL11");
    }

    private static boolean check_GL12(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {

        return (checkFunctions(provider, caps, new int[] {
                        336, 337, 338, 339
                },
                "glTexImage3D", "glTexSubImage3D", "glCopyTexSubImage3D", "glDrawRangeElements"
        ) & ext.contains("OpenGL12")) || reportMissing("GL", "OpenGL12");
    }

    private static boolean check_GL13(FunctionProvider provider, PointerBuffer caps, Set<String> ext, boolean fc) {

        return ((fc || checkFunctions(provider, caps, new int[] {
                        349, 350, 351, 352, 353, 354, 355, 356, 357, 358, 359, 360, 361, 362, 363, 364, 365, 366, 367, 368, 369, 370, 371, 372, 373, 374, 375, 376, 377,
                        378, 379, 380, 381, 382, 383, 384, 385
                },
                "glClientActiveTexture", "glMultiTexCoord1f", "glMultiTexCoord1s", "glMultiTexCoord1i", "glMultiTexCoord1d", "glMultiTexCoord1fv",
                "glMultiTexCoord1sv", "glMultiTexCoord1iv", "glMultiTexCoord1dv", "glMultiTexCoord2f", "glMultiTexCoord2s", "glMultiTexCoord2i",
                "glMultiTexCoord2d", "glMultiTexCoord2fv", "glMultiTexCoord2sv", "glMultiTexCoord2iv", "glMultiTexCoord2dv", "glMultiTexCoord3f",
                "glMultiTexCoord3s", "glMultiTexCoord3i", "glMultiTexCoord3d", "glMultiTexCoord3fv", "glMultiTexCoord3sv", "glMultiTexCoord3iv",
                "glMultiTexCoord3dv", "glMultiTexCoord4f", "glMultiTexCoord4s", "glMultiTexCoord4i", "glMultiTexCoord4d", "glMultiTexCoord4fv",
                "glMultiTexCoord4sv", "glMultiTexCoord4iv", "glMultiTexCoord4dv", "glLoadTransposeMatrixf", "glLoadTransposeMatrixd", "glMultTransposeMatrixf",
                "glMultTransposeMatrixd"
        )) & checkFunctions(provider, caps, new int[] {
                        340, 341, 342, 343, 344, 345, 346, 347, 348
                },
                "glCompressedTexImage3D", "glCompressedTexImage2D", "glCompressedTexImage1D", "glCompressedTexSubImage3D", "glCompressedTexSubImage2D",
                "glCompressedTexSubImage1D", "glGetCompressedTexImage", "glSampleCoverage", "glActiveTexture"
        ) & ext.contains("OpenGL13")) || reportMissing("GL", "OpenGL13");
    }

    private static boolean check_GL14(FunctionProvider provider, PointerBuffer caps, Set<String> ext, boolean fc) {

        return ((fc || checkFunctions(provider, caps, new int[] {
                        388, 389, 390, 391, 392, 399, 400, 401, 402, 403, 404, 405, 406, 407, 408, 409, 410, 411, 412, 413, 414, 415, 417, 418, 419, 420, 421, 422, 423,
                        424, 425, 426, 427, 428, 429, 430, 431, 432
                },
                "glFogCoordf", "glFogCoordd", "glFogCoordfv", "glFogCoorddv", "glFogCoordPointer", "glSecondaryColor3b", "glSecondaryColor3s", "glSecondaryColor3i",
                "glSecondaryColor3f", "glSecondaryColor3d", "glSecondaryColor3ub", "glSecondaryColor3us", "glSecondaryColor3ui", "glSecondaryColor3bv",
                "glSecondaryColor3sv", "glSecondaryColor3iv", "glSecondaryColor3fv", "glSecondaryColor3dv", "glSecondaryColor3ubv", "glSecondaryColor3usv",
                "glSecondaryColor3uiv", "glSecondaryColorPointer", "glWindowPos2i", "glWindowPos2s", "glWindowPos2f", "glWindowPos2d", "glWindowPos2iv",
                "glWindowPos2sv", "glWindowPos2fv", "glWindowPos2dv", "glWindowPos3i", "glWindowPos3s", "glWindowPos3f", "glWindowPos3d", "glWindowPos3iv",
                "glWindowPos3sv", "glWindowPos3fv", "glWindowPos3dv"
        )) & checkFunctions(provider, caps, new int[] {
                        386, 387, 393, 394, 395, 396, 397, 398, 416
                },
                "glBlendColor", "glBlendEquation", "glMultiDrawArrays", "glMultiDrawElements", "glPointParameterf", "glPointParameteri", "glPointParameterfv",
                "glPointParameteriv", "glBlendFuncSeparate"
        ) & ext.contains("OpenGL14")) || reportMissing("GL", "OpenGL14");
    }

    private static boolean check_GL15(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {

        return (checkFunctions(provider, caps, new int[] {
                        433, 434, 435, 436, 437, 438, 439, 440, 441, 442, 443, 444, 445, 446, 447, 448, 449, 450, 451
                },
                "glBindBuffer", "glDeleteBuffers", "glGenBuffers", "glIsBuffer", "glBufferData", "glBufferSubData", "glGetBufferSubData", "glMapBuffer",
                "glUnmapBuffer", "glGetBufferParameteriv", "glGetBufferPointerv", "glGenQueries", "glDeleteQueries", "glIsQuery", "glBeginQuery", "glEndQuery",
                "glGetQueryiv", "glGetQueryObjectiv", "glGetQueryObjectuiv"
        ) & ext.contains("OpenGL15")) || reportMissing("GL", "OpenGL15");
    }

    private static boolean check_GL20(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {

        return (checkFunctions(provider, caps, new int[] {
                        452, 453, 454, 455, 456, 457, 458, 459, 460, 461, 462, 463, 464, 465, 466, 467, 468, 469, 470, 471, 472, 473, 474, 475, 476, 477, 478, 479, 480,
                        481, 482, 483, 484, 485, 486, 487, 488, 489, 490, 491, 492, 493, 494, 495, 496, 497, 498, 499, 500, 501, 502, 503, 504, 505, 506, 507, 508, 509,
                        510, 511, 512, 513, 514, 515, 516, 517, 518, 519, 520, 521, 522, 523, 524, 525, 526, 527, 528, 529, 530, 531, 532, 533, 534, 535, 536, 537, 538,
                        539, 540, 541, 542, 543, 544
                },
                "glCreateProgram", "glDeleteProgram", "glIsProgram", "glCreateShader", "glDeleteShader", "glIsShader", "glAttachShader", "glDetachShader",
                "glShaderSource", "glCompileShader", "glLinkProgram", "glUseProgram", "glValidateProgram", "glUniform1f", "glUniform2f", "glUniform3f",
                "glUniform4f", "glUniform1i", "glUniform2i", "glUniform3i", "glUniform4i", "glUniform1fv", "glUniform2fv", "glUniform3fv", "glUniform4fv",
                "glUniform1iv", "glUniform2iv", "glUniform3iv", "glUniform4iv", "glUniformMatrix2fv", "glUniformMatrix3fv", "glUniformMatrix4fv", "glGetShaderiv",
                "glGetProgramiv", "glGetShaderInfoLog", "glGetProgramInfoLog", "glGetAttachedShaders", "glGetUniformLocation", "glGetActiveUniform",
                "glGetUniformfv", "glGetUniformiv", "glGetShaderSource", "glVertexAttrib1f", "glVertexAttrib1s", "glVertexAttrib1d", "glVertexAttrib2f",
                "glVertexAttrib2s", "glVertexAttrib2d", "glVertexAttrib3f", "glVertexAttrib3s", "glVertexAttrib3d", "glVertexAttrib4f", "glVertexAttrib4s",
                "glVertexAttrib4d", "glVertexAttrib4Nub", "glVertexAttrib1fv", "glVertexAttrib1sv", "glVertexAttrib1dv", "glVertexAttrib2fv", "glVertexAttrib2sv",
                "glVertexAttrib2dv", "glVertexAttrib3fv", "glVertexAttrib3sv", "glVertexAttrib3dv", "glVertexAttrib4fv", "glVertexAttrib4sv", "glVertexAttrib4dv",
                "glVertexAttrib4iv", "glVertexAttrib4bv", "glVertexAttrib4ubv", "glVertexAttrib4usv", "glVertexAttrib4uiv", "glVertexAttrib4Nbv",
                "glVertexAttrib4Nsv", "glVertexAttrib4Niv", "glVertexAttrib4Nubv", "glVertexAttrib4Nusv", "glVertexAttrib4Nuiv", "glVertexAttribPointer",
                "glEnableVertexAttribArray", "glDisableVertexAttribArray", "glBindAttribLocation", "glGetActiveAttrib", "glGetAttribLocation",
                "glGetVertexAttribiv", "glGetVertexAttribfv", "glGetVertexAttribdv", "glGetVertexAttribPointerv", "glDrawBuffers", "glBlendEquationSeparate",
                "glStencilOpSeparate", "glStencilFuncSeparate", "glStencilMaskSeparate"
        ) & ext.contains("OpenGL20")) || reportMissing("GL", "OpenGL20");
    }

    private static boolean check_GL21(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {

        return (checkFunctions(provider, caps, new int[] {
                        545, 546, 547, 548, 549, 550
                },
                "glUniformMatrix2x3fv", "glUniformMatrix3x2fv", "glUniformMatrix2x4fv", "glUniformMatrix4x2fv", "glUniformMatrix3x4fv", "glUniformMatrix4x3fv"
        ) & ext.contains("OpenGL21")) || reportMissing("GL", "OpenGL21");
    }

    private static boolean check_GL30(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {

        return (checkFunctions(provider, caps, new int[] {
                        551, 552, 553, 554, 555, 556, 557, 558, 559, 560, 561, 562, 563, 564, 565, 566, 567, 568, 569, 570, 571, 572, 573, 574, 575, 576, 577, 578, 579,
                        580, 581, 582, 583, 584, 585, 586, 587, 588, 589, 590, 591, 592, 593, 594, 595, 596, 597, 598, 599, 600, 601, 602, 603, 604, 605, 606, 607, 608,
                        609, 610, 611, 612, 613, 614, 615, 616, 617, 618, 619, 620, 621, 622, 623, 624, 625, 626, 627, 628, 629, 630, 631, 632, 633, 634
                },
                "glGetStringi", "glClearBufferiv", "glClearBufferuiv", "glClearBufferfv", "glClearBufferfi", "glVertexAttribI1i", "glVertexAttribI2i",
                "glVertexAttribI3i", "glVertexAttribI4i", "glVertexAttribI1ui", "glVertexAttribI2ui", "glVertexAttribI3ui", "glVertexAttribI4ui",
                "glVertexAttribI1iv", "glVertexAttribI2iv", "glVertexAttribI3iv", "glVertexAttribI4iv", "glVertexAttribI1uiv", "glVertexAttribI2uiv",
                "glVertexAttribI3uiv", "glVertexAttribI4uiv", "glVertexAttribI4bv", "glVertexAttribI4sv", "glVertexAttribI4ubv", "glVertexAttribI4usv",
                "glVertexAttribIPointer", "glGetVertexAttribIiv", "glGetVertexAttribIuiv", "glUniform1ui", "glUniform2ui", "glUniform3ui", "glUniform4ui",
                "glUniform1uiv", "glUniform2uiv", "glUniform3uiv", "glUniform4uiv", "glGetUniformuiv", "glBindFragDataLocation", "glGetFragDataLocation",
                "glBeginConditionalRender", "glEndConditionalRender", "glMapBufferRange", "glFlushMappedBufferRange", "glClampColor", "glIsRenderbuffer",
                "glBindRenderbuffer", "glDeleteRenderbuffers", "glGenRenderbuffers", "glRenderbufferStorage", "glRenderbufferStorageMultisample",
                "glGetRenderbufferParameteriv", "glIsFramebuffer", "glBindFramebuffer", "glDeleteFramebuffers", "glGenFramebuffers", "glCheckFramebufferStatus",
                "glFramebufferTexture1D", "glFramebufferTexture2D", "glFramebufferTexture3D", "glFramebufferTextureLayer", "glFramebufferRenderbuffer",
                "glGetFramebufferAttachmentParameteriv", "glBlitFramebuffer", "glGenerateMipmap", "glTexParameterIiv", "glTexParameterIuiv", "glGetTexParameterIiv",
                "glGetTexParameterIuiv", "glColorMaski", "glGetBooleani_v", "glGetIntegeri_v", "glEnablei", "glDisablei", "glIsEnabledi", "glBindBufferRange",
                "glBindBufferBase", "glBeginTransformFeedback", "glEndTransformFeedback", "glTransformFeedbackVaryings", "glGetTransformFeedbackVarying",
                "glBindVertexArray", "glDeleteVertexArrays", "glGenVertexArrays", "glIsVertexArray"
        ) & ext.contains("OpenGL30")) || reportMissing("GL", "OpenGL30");
    }

    private static boolean check_GL31(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {

        return (checkFunctions(provider, caps, new int[] {
                        635, 636, 637, 638, 639, 640, 641, 642, 643, 644, 645, 646
                },
                "glDrawArraysInstanced", "glDrawElementsInstanced", "glCopyBufferSubData", "glPrimitiveRestartIndex", "glTexBuffer", "glGetUniformIndices",
                "glGetActiveUniformsiv", "glGetActiveUniformName", "glGetUniformBlockIndex", "glGetActiveUniformBlockiv", "glGetActiveUniformBlockName",
                "glUniformBlockBinding"
        ) & ext.contains("OpenGL31")) || reportMissing("GL", "OpenGL31");
    }

    private static boolean check_GL32(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {

        return (checkFunctions(provider, caps, new int[] {
                        647, 648, 649, 650, 651, 652, 653, 654, 655, 656, 657, 658, 659, 660, 661, 662, 663, 664, 665
                },
                "glGetBufferParameteri64v", "glDrawElementsBaseVertex", "glDrawRangeElementsBaseVertex", "glDrawElementsInstancedBaseVertex",
                "glMultiDrawElementsBaseVertex", "glProvokingVertex", "glTexImage2DMultisample", "glTexImage3DMultisample", "glGetMultisamplefv", "glSampleMaski",
                "glFramebufferTexture", "glFenceSync", "glIsSync", "glDeleteSync", "glClientWaitSync", "glWaitSync", "glGetInteger64v", "glGetInteger64i_v",
                "glGetSynciv"
        ) & ext.contains("OpenGL32")) || reportMissing("GL", "OpenGL32");
    }

    private static boolean check_GL33(FunctionProvider provider, PointerBuffer caps, Set<String> ext, boolean fc) {

        return ((fc || checkFunctions(provider, caps, new int[] {
                        686, 687, 688, 689, 690, 691, 692, 693, 694, 695, 696, 697, 698, 699, 700, 701, 702, 703, 704, 705, 706, 707, 708, 709, 710, 711, 712, 713, 714,
                        715
                },
                "glVertexP2ui", "glVertexP3ui", "glVertexP4ui", "glVertexP2uiv", "glVertexP3uiv", "glVertexP4uiv", "glTexCoordP1ui", "glTexCoordP2ui",
                "glTexCoordP3ui", "glTexCoordP4ui", "glTexCoordP1uiv", "glTexCoordP2uiv", "glTexCoordP3uiv", "glTexCoordP4uiv", "glMultiTexCoordP1ui",
                "glMultiTexCoordP2ui", "glMultiTexCoordP3ui", "glMultiTexCoordP4ui", "glMultiTexCoordP1uiv", "glMultiTexCoordP2uiv", "glMultiTexCoordP3uiv",
                "glMultiTexCoordP4uiv", "glNormalP3ui", "glNormalP3uiv", "glColorP3ui", "glColorP4ui", "glColorP3uiv", "glColorP4uiv", "glSecondaryColorP3ui",
                "glSecondaryColorP3uiv"
        )) & checkFunctions(provider, caps, new int[] {
                        666, 667, 668, 669, 670, 671, 672, 673, 674, 675, 676, 677, 678, 679, 680, 681, 682, 683, 684, 685, 716, 717, 718, 719, 720, 721, 722, 723
                },
                "glBindFragDataLocationIndexed", "glGetFragDataIndex", "glGenSamplers", "glDeleteSamplers", "glIsSampler", "glBindSampler", "glSamplerParameteri",
                "glSamplerParameterf", "glSamplerParameteriv", "glSamplerParameterfv", "glSamplerParameterIiv", "glSamplerParameterIuiv", "glGetSamplerParameteriv",
                "glGetSamplerParameterfv", "glGetSamplerParameterIiv", "glGetSamplerParameterIuiv", "glQueryCounter", "glGetQueryObjecti64v",
                "glGetQueryObjectui64v", "glVertexAttribDivisor", "glVertexAttribP1ui", "glVertexAttribP2ui", "glVertexAttribP3ui", "glVertexAttribP4ui",
                "glVertexAttribP1uiv", "glVertexAttribP2uiv", "glVertexAttribP3uiv", "glVertexAttribP4uiv"
        ) & ext.contains("OpenGL33")) || reportMissing("GL", "OpenGL33");
    }

    private static boolean check_GL40(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("OpenGL40")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        724, 725, 726, 727, 728, 729, 730, 731, 732, 733, 734, 735, 736, 737, 738, 739, 740, 741, 742, 743, 744, 745, 746, 747, 748, 749, 750, 751, 752,
                        753, 754, 755, 756, 757, 758, 759, 760, 761, 762, 763, 764, 765, 766, 767, 768, 769
                },
                "glBlendEquationi", "glBlendEquationSeparatei", "glBlendFunci", "glBlendFuncSeparatei", "glDrawArraysIndirect", "glDrawElementsIndirect",
                "glUniform1d", "glUniform2d", "glUniform3d", "glUniform4d", "glUniform1dv", "glUniform2dv", "glUniform3dv", "glUniform4dv", "glUniformMatrix2dv",
                "glUniformMatrix3dv", "glUniformMatrix4dv", "glUniformMatrix2x3dv", "glUniformMatrix2x4dv", "glUniformMatrix3x2dv", "glUniformMatrix3x4dv",
                "glUniformMatrix4x2dv", "glUniformMatrix4x3dv", "glGetUniformdv", "glMinSampleShading", "glGetSubroutineUniformLocation", "glGetSubroutineIndex",
                "glGetActiveSubroutineUniformiv", "glGetActiveSubroutineUniformName", "glGetActiveSubroutineName", "glUniformSubroutinesuiv",
                "glGetUniformSubroutineuiv", "glGetProgramStageiv", "glPatchParameteri", "glPatchParameterfv", "glBindTransformFeedback",
                "glDeleteTransformFeedbacks", "glGenTransformFeedbacks", "glIsTransformFeedback", "glPauseTransformFeedback", "glResumeTransformFeedback",
                "glDrawTransformFeedback", "glDrawTransformFeedbackStream", "glBeginQueryIndexed", "glEndQueryIndexed", "glGetQueryIndexediv"
        )) || reportMissing("GL", "OpenGL40");
    }

    private static boolean check_GL41(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("OpenGL41")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        770, 771, 772, 773, 774, 775, 776, 777, 778, 779, 780, 781, 782, 783, 784, 785, 786, 787, 788, 789, 790, 791, 792, 793, 794, 795, 796, 797, 798,
                        799, 800, 801, 802, 803, 804, 805, 806, 807, 808, 809, 810, 811, 812, 813, 814, 815, 816, 817, 818, 819, 820, 821, 822, 823, 824, 825, 826, 827,
                        828, 829, 830, 831, 832, 833, 834, 835, 836, 837, 838, 839, 840, 841, 842, 843, 844, 845, 846, 847, 848, 849, 850, 851, 852, 853, 854, 855, 856,
                        857
                },
                "glReleaseShaderCompiler", "glShaderBinary", "glGetShaderPrecisionFormat", "glDepthRangef", "glClearDepthf", "glGetProgramBinary",
                "glProgramBinary", "glProgramParameteri", "glUseProgramStages", "glActiveShaderProgram", "glCreateShaderProgramv", "glBindProgramPipeline",
                "glDeleteProgramPipelines", "glGenProgramPipelines", "glIsProgramPipeline", "glGetProgramPipelineiv", "glProgramUniform1i", "glProgramUniform2i",
                "glProgramUniform3i", "glProgramUniform4i", "glProgramUniform1ui", "glProgramUniform2ui", "glProgramUniform3ui", "glProgramUniform4ui",
                "glProgramUniform1f", "glProgramUniform2f", "glProgramUniform3f", "glProgramUniform4f", "glProgramUniform1d", "glProgramUniform2d",
                "glProgramUniform3d", "glProgramUniform4d", "glProgramUniform1iv", "glProgramUniform2iv", "glProgramUniform3iv", "glProgramUniform4iv",
                "glProgramUniform1uiv", "glProgramUniform2uiv", "glProgramUniform3uiv", "glProgramUniform4uiv", "glProgramUniform1fv", "glProgramUniform2fv",
                "glProgramUniform3fv", "glProgramUniform4fv", "glProgramUniform1dv", "glProgramUniform2dv", "glProgramUniform3dv", "glProgramUniform4dv",
                "glProgramUniformMatrix2fv", "glProgramUniformMatrix3fv", "glProgramUniformMatrix4fv", "glProgramUniformMatrix2dv", "glProgramUniformMatrix3dv",
                "glProgramUniformMatrix4dv", "glProgramUniformMatrix2x3fv", "glProgramUniformMatrix3x2fv", "glProgramUniformMatrix2x4fv",
                "glProgramUniformMatrix4x2fv", "glProgramUniformMatrix3x4fv", "glProgramUniformMatrix4x3fv", "glProgramUniformMatrix2x3dv",
                "glProgramUniformMatrix3x2dv", "glProgramUniformMatrix2x4dv", "glProgramUniformMatrix4x2dv", "glProgramUniformMatrix3x4dv",
                "glProgramUniformMatrix4x3dv", "glValidateProgramPipeline", "glGetProgramPipelineInfoLog", "glVertexAttribL1d", "glVertexAttribL2d",
                "glVertexAttribL3d", "glVertexAttribL4d", "glVertexAttribL1dv", "glVertexAttribL2dv", "glVertexAttribL3dv", "glVertexAttribL4dv",
                "glVertexAttribLPointer", "glGetVertexAttribLdv", "glViewportArrayv", "glViewportIndexedf", "glViewportIndexedfv", "glScissorArrayv",
                "glScissorIndexed", "glScissorIndexedv", "glDepthRangeArrayv", "glDepthRangeIndexed", "glGetFloati_v", "glGetDoublei_v"
        )) || reportMissing("GL", "OpenGL41");
    }

    private static boolean check_GL42(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("OpenGL42")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        858, 859, 860, 861, 862, 863, 864, 865, 866, 867, 868, 869
                },
                "glGetActiveAtomicCounterBufferiv", "glTexStorage1D", "glTexStorage2D", "glTexStorage3D", "glDrawTransformFeedbackInstanced",
                "glDrawTransformFeedbackStreamInstanced", "glDrawArraysInstancedBaseInstance", "glDrawElementsInstancedBaseInstance",
                "glDrawElementsInstancedBaseVertexBaseInstance", "glBindImageTexture", "glMemoryBarrier", "glGetInternalformativ"
        )) || reportMissing("GL", "OpenGL42");
    }

    private static boolean check_GL43(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("OpenGL43")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        870, 871, 872, 873, 874, 875, 876, 877, 878, 879, 880, 881, 882, 883, 884, 885, 886, 887, 888, 889, 890, 891, 892, 893, 894, 895, 896, 897, 898,
                        899, 900, 901, 902, 903, 904, 905, 906, 907, 908, 909, 910, 911, 912
                },
                "glClearBufferData", "glClearBufferSubData", "glDispatchCompute", "glDispatchComputeIndirect", "glCopyImageSubData", "glDebugMessageControl",
                "glDebugMessageInsert", "glDebugMessageCallback", "glGetDebugMessageLog", "glPushDebugGroup", "glPopDebugGroup", "glObjectLabel",
                "glGetObjectLabel", "glObjectPtrLabel", "glGetObjectPtrLabel", "glFramebufferParameteri", "glGetFramebufferParameteriv", "glGetInternalformati64v",
                "glInvalidateTexSubImage", "glInvalidateTexImage", "glInvalidateBufferSubData", "glInvalidateBufferData", "glInvalidateFramebuffer",
                "glInvalidateSubFramebuffer", "glMultiDrawArraysIndirect", "glMultiDrawElementsIndirect", "glGetProgramInterfaceiv", "glGetProgramResourceIndex",
                "glGetProgramResourceName", "glGetProgramResourceiv", "glGetProgramResourceLocation", "glGetProgramResourceLocationIndex",
                "glShaderStorageBlockBinding", "glTexBufferRange", "glTexStorage2DMultisample", "glTexStorage3DMultisample", "glTextureView", "glBindVertexBuffer",
                "glVertexAttribFormat", "glVertexAttribIFormat", "glVertexAttribLFormat", "glVertexAttribBinding", "glVertexBindingDivisor"
        )) || reportMissing("GL", "OpenGL43");
    }

    private static boolean check_GL44(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("OpenGL44")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        913, 914, 915, 916, 917, 918, 919, 920, 921
                },
                "glBufferStorage", "glClearTexSubImage", "glClearTexImage", "glBindBuffersBase", "glBindBuffersRange", "glBindTextures", "glBindSamplers",
                "glBindImageTextures", "glBindVertexBuffers"
        )) || reportMissing("GL", "OpenGL44");
    }

    private static boolean check_GL45(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("OpenGL45")) {
            return false;
        }

        int flag0 = provider.getFunctionAddress("glGetMapdv") != NULL ? 0 : Integer.MIN_VALUE;
        int flag1 = provider.getFunctionAddress("glGetMapfv") != NULL ? 0 : Integer.MIN_VALUE;
        int flag2 = provider.getFunctionAddress("glGetMapiv") != NULL ? 0 : Integer.MIN_VALUE;
        int flag3 = provider.getFunctionAddress("glGetPixelMapfv") != NULL ? 0 : Integer.MIN_VALUE;
        int flag4 = provider.getFunctionAddress("glGetPixelMapuiv") != NULL ? 0 : Integer.MIN_VALUE;
        int flag5 = provider.getFunctionAddress("glGetPixelMapusv") != NULL ? 0 : Integer.MIN_VALUE;
        int flag6 = provider.getFunctionAddress("glGetPolygonStipple") != NULL ? 0 : Integer.MIN_VALUE;
        int flag7 = ext.contains("GL_ARB_imaging") & provider.getFunctionAddress("glGetColorTable") != NULL ? 0 : Integer.MIN_VALUE;
        int flag8 = ext.contains("GL_ARB_imaging") & provider.getFunctionAddress("glGetConvolutionFilter") != NULL ? 0 : Integer.MIN_VALUE;
        int flag9 = ext.contains("GL_ARB_imaging") & provider.getFunctionAddress("glGetSeparableFilter") != NULL ? 0 : Integer.MIN_VALUE;
        int flag10 = ext.contains("GL_ARB_imaging") & provider.getFunctionAddress("glGetHistogram") != NULL ? 0 : Integer.MIN_VALUE;
        int flag11 = ext.contains("GL_ARB_imaging") & provider.getFunctionAddress("glGetMinmax") != NULL ? 0 : Integer.MIN_VALUE;

        return (checkFunctions(provider, caps, new int[] {
                        922, 923, 924, 925, 926, 927, 928, 929, 930, 931, 932, 933, 934, 935, 936, 937, 938, 939, 940, 941, 942, 943, 944, 945, 946, 947, 948, 949, 950,
                        951, 952, 953, 954, 955, 956, 957, 958, 959, 960, 961, 962, 963, 964, 965, 966, 967, 968, 969, 970, 971, 972, 973, 974, 975, 976, 977, 978, 979,
                        980, 981, 982, 983, 984, 985, 986, 987, 988, 989, 990, 991, 992, 993, 994, 995, 996, 997, 998, 999, 1000, 1001, 1002, 1003, 1004, 1005, 1006, 1007,
                        1008, 1009, 1010, 1011, 1012, 1013, 1014, 1015, 1016, 1017, 1018, 1019, 1020, 1021, 1022, 1023, 1024, 1033, 1040, 1042, 1043
                },
                "glClipControl", "glCreateTransformFeedbacks", "glTransformFeedbackBufferBase", "glTransformFeedbackBufferRange", "glGetTransformFeedbackiv",
                "glGetTransformFeedbacki_v", "glGetTransformFeedbacki64_v", "glCreateBuffers", "glNamedBufferStorage", "glNamedBufferData", "glNamedBufferSubData",
                "glCopyNamedBufferSubData", "glClearNamedBufferData", "glClearNamedBufferSubData", "glMapNamedBuffer", "glMapNamedBufferRange",
                "glUnmapNamedBuffer", "glFlushMappedNamedBufferRange", "glGetNamedBufferParameteriv", "glGetNamedBufferParameteri64v", "glGetNamedBufferPointerv",
                "glGetNamedBufferSubData", "glCreateFramebuffers", "glNamedFramebufferRenderbuffer", "glNamedFramebufferParameteri", "glNamedFramebufferTexture",
                "glNamedFramebufferTextureLayer", "glNamedFramebufferDrawBuffer", "glNamedFramebufferDrawBuffers", "glNamedFramebufferReadBuffer",
                "glInvalidateNamedFramebufferData", "glInvalidateNamedFramebufferSubData", "glClearNamedFramebufferiv", "glClearNamedFramebufferuiv",
                "glClearNamedFramebufferfv", "glClearNamedFramebufferfi", "glBlitNamedFramebuffer", "glCheckNamedFramebufferStatus",
                "glGetNamedFramebufferParameteriv", "glGetNamedFramebufferAttachmentParameteriv", "glCreateRenderbuffers", "glNamedRenderbufferStorage",
                "glNamedRenderbufferStorageMultisample", "glGetNamedRenderbufferParameteriv", "glCreateTextures", "glTextureBuffer", "glTextureBufferRange",
                "glTextureStorage1D", "glTextureStorage2D", "glTextureStorage3D", "glTextureStorage2DMultisample", "glTextureStorage3DMultisample",
                "glTextureSubImage1D", "glTextureSubImage2D", "glTextureSubImage3D", "glCompressedTextureSubImage1D", "glCompressedTextureSubImage2D",
                "glCompressedTextureSubImage3D", "glCopyTextureSubImage1D", "glCopyTextureSubImage2D", "glCopyTextureSubImage3D", "glTextureParameterf",
                "glTextureParameterfv", "glTextureParameteri", "glTextureParameterIiv", "glTextureParameterIuiv", "glTextureParameteriv", "glGenerateTextureMipmap",
                "glBindTextureUnit", "glGetTextureImage", "glGetCompressedTextureImage", "glGetTextureLevelParameterfv", "glGetTextureLevelParameteriv",
                "glGetTextureParameterfv", "glGetTextureParameterIiv", "glGetTextureParameterIuiv", "glGetTextureParameteriv", "glCreateVertexArrays",
                "glDisableVertexArrayAttrib", "glEnableVertexArrayAttrib", "glVertexArrayElementBuffer", "glVertexArrayVertexBuffer", "glVertexArrayVertexBuffers",
                "glVertexArrayAttribFormat", "glVertexArrayAttribIFormat", "glVertexArrayAttribLFormat", "glVertexArrayAttribBinding",
                "glVertexArrayBindingDivisor", "glGetVertexArrayiv", "glGetVertexArrayIndexediv", "glGetVertexArrayIndexed64iv", "glCreateSamplers",
                "glCreateProgramPipelines", "glCreateQueries", "glGetQueryBufferObjectiv", "glGetQueryBufferObjectuiv", "glGetQueryBufferObjecti64v",
                "glGetQueryBufferObjectui64v", "glMemoryBarrierByRegion", "glGetTextureSubImage", "glGetCompressedTextureSubImage", "glTextureBarrier",
                "glGetGraphicsResetStatus", "glReadnPixels", "glGetnUniformfv", "glGetnUniformiv", "glGetnUniformuiv"
        )) || reportMissing("GL", "OpenGL45");
    }

    private static boolean check_GL46(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("OpenGL46")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1044, 1045, 1046, 1047
                },
                "glMultiDrawArraysIndirectCount", "glMultiDrawElementsIndirectCount", "glPolygonOffsetClamp", "glSpecializeShader"
        )) || reportMissing("GL", "OpenGL46");
    }

    private static boolean check_AMD_debug_output(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_AMD_debug_output")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1048, 1049, 1050, 1051
                },
                "glDebugMessageEnableAMD", "glDebugMessageInsertAMD", "glDebugMessageCallbackAMD", "glGetDebugMessageLogAMD"
        )) || reportMissing("GL", "GL_AMD_debug_output");
    }

    private static boolean check_AMD_draw_buffers_blend(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_AMD_draw_buffers_blend")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1052, 1053, 1054, 1055
                },
                "glBlendFuncIndexedAMD", "glBlendFuncSeparateIndexedAMD", "glBlendEquationIndexedAMD", "glBlendEquationSeparateIndexedAMD"
        )) || reportMissing("GL", "GL_AMD_draw_buffers_blend");
    }

    private static boolean check_AMD_framebuffer_multisample_advanced(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_AMD_framebuffer_multisample_advanced")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1056, 1057
                },
                "glRenderbufferStorageMultisampleAdvancedAMD", "glNamedRenderbufferStorageMultisampleAdvancedAMD"
        )) || reportMissing("GL", "GL_AMD_framebuffer_multisample_advanced");
    }

    private static boolean check_AMD_gpu_shader_int64(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_AMD_gpu_shader_int64")) {
            return false;
        }

        int flag0 = ext.contains("GL_EXT_direct_state_access") ? 0 : Integer.MIN_VALUE;

        return (checkFunctions(provider, caps, new int[] {
                        1058, 1059, 1060, 1061, 1062, 1063, 1064, 1065, 1066, 1067, 1068, 1069, 1070, 1071, 1072, 1073, 1074, 1075, flag0 + 1076, flag0 + 1077,
                        flag0 + 1078, flag0 + 1079, flag0 + 1080, flag0 + 1081, flag0 + 1082, flag0 + 1083, flag0 + 1084, flag0 + 1085, flag0 + 1086, flag0 + 1087,
                        flag0 + 1088, flag0 + 1089, flag0 + 1090, flag0 + 1091
                },
                "glUniform1i64NV", "glUniform2i64NV", "glUniform3i64NV", "glUniform4i64NV", "glUniform1i64vNV", "glUniform2i64vNV", "glUniform3i64vNV",
                "glUniform4i64vNV", "glUniform1ui64NV", "glUniform2ui64NV", "glUniform3ui64NV", "glUniform4ui64NV", "glUniform1ui64vNV", "glUniform2ui64vNV",
                "glUniform3ui64vNV", "glUniform4ui64vNV", "glGetUniformi64vNV", "glGetUniformui64vNV", "glProgramUniform1i64NV", "glProgramUniform2i64NV",
                "glProgramUniform3i64NV", "glProgramUniform4i64NV", "glProgramUniform1i64vNV", "glProgramUniform2i64vNV", "glProgramUniform3i64vNV",
                "glProgramUniform4i64vNV", "glProgramUniform1ui64NV", "glProgramUniform2ui64NV", "glProgramUniform3ui64NV", "glProgramUniform4ui64NV",
                "glProgramUniform1ui64vNV", "glProgramUniform2ui64vNV", "glProgramUniform3ui64vNV", "glProgramUniform4ui64vNV"
        )) || reportMissing("GL", "GL_AMD_gpu_shader_int64");
    }

    private static boolean check_AMD_interleaved_elements(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_AMD_interleaved_elements")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1092
                },
                "glVertexAttribParameteriAMD"
        )) || reportMissing("GL", "GL_AMD_interleaved_elements");
    }

    private static boolean check_AMD_occlusion_query_event(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_AMD_occlusion_query_event")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1093
                },
                "glQueryObjectParameteruiAMD"
        )) || reportMissing("GL", "GL_AMD_occlusion_query_event");
    }

    private static boolean check_AMD_performance_monitor(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_AMD_performance_monitor")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1094, 1095, 1096, 1097, 1098, 1099, 1100, 1101, 1102, 1103, 1104
                },
                "glGetPerfMonitorGroupsAMD", "glGetPerfMonitorCountersAMD", "glGetPerfMonitorGroupStringAMD", "glGetPerfMonitorCounterStringAMD",
                "glGetPerfMonitorCounterInfoAMD", "glGenPerfMonitorsAMD", "glDeletePerfMonitorsAMD", "glSelectPerfMonitorCountersAMD", "glBeginPerfMonitorAMD",
                "glEndPerfMonitorAMD", "glGetPerfMonitorCounterDataAMD"
        )) || reportMissing("GL", "GL_AMD_performance_monitor");
    }

    private static boolean check_AMD_sample_positions(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_AMD_sample_positions")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1105
                },
                "glSetMultisamplefvAMD"
        )) || reportMissing("GL", "GL_AMD_sample_positions");
    }

    private static boolean check_AMD_sparse_texture(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_AMD_sparse_texture")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1106, 1107
                },
                "glTexStorageSparseAMD", "glTextureStorageSparseAMD"
        )) || reportMissing("GL", "GL_AMD_sparse_texture");
    }

    private static boolean check_AMD_stencil_operation_extended(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_AMD_stencil_operation_extended")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1108
                },
                "glStencilOpValueAMD"
        )) || reportMissing("GL", "GL_AMD_stencil_operation_extended");
    }

    private static boolean check_AMD_vertex_shader_tessellator(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_AMD_vertex_shader_tessellator")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1109, 1110
                },
                "glTessellationFactorAMD", "glTessellationModeAMD"
        )) || reportMissing("GL", "GL_AMD_vertex_shader_tessellator");
    }

    private static boolean check_ARB_base_instance(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_ARB_base_instance")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        864, 865, 866
                },
                "glDrawArraysInstancedBaseInstance", "glDrawElementsInstancedBaseInstance", "glDrawElementsInstancedBaseVertexBaseInstance"
        )) || reportMissing("GL", "GL_ARB_base_instance");
    }

    private static boolean check_ARB_bindless_texture(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_ARB_bindless_texture")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1111, 1112, 1113, 1114, 1115, 1116, 1117, 1118, 1119, 1120, 1121, 1122, 1123, 1124, 1125, 1126
                },
                "glGetTextureHandleARB", "glGetTextureSamplerHandleARB", "glMakeTextureHandleResidentARB", "glMakeTextureHandleNonResidentARB",
                "glGetImageHandleARB", "glMakeImageHandleResidentARB", "glMakeImageHandleNonResidentARB", "glUniformHandleui64ARB", "glUniformHandleui64vARB",
                "glProgramUniformHandleui64ARB", "glProgramUniformHandleui64vARB", "glIsTextureHandleResidentARB", "glIsImageHandleResidentARB",
                "glVertexAttribL1ui64ARB", "glVertexAttribL1ui64vARB", "glGetVertexAttribLui64vARB"
        )) || reportMissing("GL", "GL_ARB_bindless_texture");
    }

    private static boolean check_ARB_blend_func_extended(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_ARB_blend_func_extended")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        666, 667
                },
                "glBindFragDataLocationIndexed", "glGetFragDataIndex"
        )) || reportMissing("GL", "GL_ARB_blend_func_extended");
    }

    private static boolean check_ARB_buffer_storage(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_ARB_buffer_storage")) {
            return false;
        }

        int flag0 = ext.contains("GL_EXT_direct_state_access") ? 0 : Integer.MIN_VALUE;

        return (checkFunctions(provider, caps, new int[] {
                        913, flag0 + 1127
                },
                "glBufferStorage", "glNamedBufferStorageEXT"
        )) || reportMissing("GL", "GL_ARB_buffer_storage");
    }

    private static boolean check_ARB_cl_event(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_ARB_cl_event")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1128
                },
                "glCreateSyncFromCLeventARB"
        )) || reportMissing("GL", "GL_ARB_cl_event");
    }

    private static boolean check_ARB_clear_buffer_object(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_ARB_clear_buffer_object")) {
            return false;
        }

        int flag0 = ext.contains("GL_EXT_direct_state_access") ? 0 : Integer.MIN_VALUE;

        return (checkFunctions(provider, caps, new int[] {
                        870, 871, flag0 + 1129, flag0 + 1130
                },
                "glClearBufferData", "glClearBufferSubData", "glClearNamedBufferDataEXT", "glClearNamedBufferSubDataEXT"
        )) || reportMissing("GL", "GL_ARB_clear_buffer_object");
    }

    private static boolean check_ARB_clear_texture(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_ARB_clear_texture")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        914, 915
                },
                "glClearTexSubImage", "glClearTexImage"
        )) || reportMissing("GL", "GL_ARB_clear_texture");
    }

    private static boolean check_ARB_clip_control(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_ARB_clip_control")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        922
                },
                "glClipControl"
        )) || reportMissing("GL", "GL_ARB_clip_control");
    }

    private static boolean check_ARB_color_buffer_float(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_ARB_color_buffer_float")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1131
                },
                "glClampColorARB"
        )) || reportMissing("GL", "GL_ARB_color_buffer_float");
    }

    private static boolean check_ARB_compute_shader(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_ARB_compute_shader")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        872, 873
                },
                "glDispatchCompute", "glDispatchComputeIndirect"
        )) || reportMissing("GL", "GL_ARB_compute_shader");
    }

    private static boolean check_ARB_compute_variable_group_size(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_ARB_compute_variable_group_size")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1132
                },
                "glDispatchComputeGroupSizeARB"
        )) || reportMissing("GL", "GL_ARB_compute_variable_group_size");
    }

    private static boolean check_ARB_copy_buffer(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_ARB_copy_buffer")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        637
                },
                "glCopyBufferSubData"
        )) || reportMissing("GL", "GL_ARB_copy_buffer");
    }

    private static boolean check_ARB_copy_image(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_ARB_copy_image")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        874
                },
                "glCopyImageSubData"
        )) || reportMissing("GL", "GL_ARB_copy_image");
    }

    private static boolean check_ARB_debug_output(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_ARB_debug_output")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1133, 1134, 1135, 1136
                },
                "glDebugMessageControlARB", "glDebugMessageInsertARB", "glDebugMessageCallbackARB", "glGetDebugMessageLogARB"
        )) || reportMissing("GL", "GL_ARB_debug_output");
    }

    private static boolean check_ARB_direct_state_access(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_ARB_direct_state_access")) {
            return false;
        }

        int flag0 = ARB_transform_feedback2(ext) ? 0 : Integer.MIN_VALUE;
        int flag1 = ARB_uniform_buffer_object(ext) ? 0 : Integer.MIN_VALUE;
        int flag6 = ARB_buffer_storage(ext) ? 0 : Integer.MIN_VALUE;
        int flag7 = ARB_copy_buffer(ext) ? 0 : Integer.MIN_VALUE;
        int flag8 = ARB_clear_texture(ext) ? 0 : Integer.MIN_VALUE;
        int flag10 = ARB_map_buffer_range(ext) ? 0 : Integer.MIN_VALUE;
        int flag12 = ARB_framebuffer_object(ext) ? 0 : Integer.MIN_VALUE;
        int flag14 = ARB_framebuffer_no_attachments(ext) ? 0 : Integer.MIN_VALUE;
        int flag20 = ARB_invalidate_subdata(ext) ? 0 : Integer.MIN_VALUE;
        int flag34 = ARB_texture_buffer_object(ext) ? 0 : Integer.MIN_VALUE;
        int flag35 = ARB_texture_buffer_range(ext) ? 0 : Integer.MIN_VALUE;
        int flag36 = ARB_texture_storage(ext) ? 0 : Integer.MIN_VALUE;
        int flag39 = ARB_texture_storage_multisample(ext) ? 0 : Integer.MIN_VALUE;
        int flag42 = ARB_vertex_array_object(ext) ? 0 : Integer.MIN_VALUE;
        int flag46 = ARB_vertex_attrib_binding(ext) ? 0 : Integer.MIN_VALUE;
        int flag47 = ARB_multi_bind(ext) ? 0 : Integer.MIN_VALUE;
        int flag56 = ARB_sampler_objects(ext) ? 0 : Integer.MIN_VALUE;
        int flag57 = ARB_separate_shader_objects(ext) ? 0 : Integer.MIN_VALUE;
        int flag58 = ARB_query_buffer_object(ext) ? 0 : Integer.MIN_VALUE;

        return (checkFunctions(provider, caps, new int[] {
                        flag0 + 923, flag1 + 924, flag1 + 925, flag0 + 926, flag0 + 927, flag0 + 928, 929, flag6 + 930, 931, 932, flag7 + 933, flag8 + 934, flag8 + 935,
                        936, flag10 + 937, 938, flag10 + 939, 940, 941, 942, 943, flag12 + 944, flag12 + 945, flag14 + 946, flag12 + 947, flag12 + 948, flag12 + 949,
                        flag12 + 950, flag12 + 951, flag20 + 952, flag20 + 953, flag12 + 954, flag12 + 955, flag12 + 956, flag12 + 957, flag12 + 958, flag12 + 959,
                        flag14 + 960, flag12 + 961, flag12 + 962, flag12 + 963, flag12 + 964, flag12 + 965, 966, flag34 + 967, flag35 + 968, flag36 + 969, flag36 + 970,
                        flag36 + 971, flag39 + 972, flag39 + 973, 974, 975, 976, 977, 978, 979, 980, 981, 982, 983, 984, 985, 986, 987, 988, flag12 + 989, 990, 991, 992,
                        993, 994, 995, 996, 997, 998, flag42 + 999, flag42 + 1000, flag42 + 1001, flag42 + 1002, flag46 + 1003, flag47 + 1004, flag46 + 1005, flag46 + 1006,
                        flag46 + 1007, flag46 + 1008, flag46 + 1009, flag42 + 1010, flag42 + 1011, flag42 + 1012, flag56 + 1013, flag57 + 1014, 1015, flag58 + 1018,
                        flag58 + 1016, flag58 + 1019, flag58 + 1017
                },
                "glCreateTransformFeedbacks", "glTransformFeedbackBufferBase", "glTransformFeedbackBufferRange", "glGetTransformFeedbackiv",
                "glGetTransformFeedbacki_v", "glGetTransformFeedbacki64_v", "glCreateBuffers", "glNamedBufferStorage", "glNamedBufferData", "glNamedBufferSubData",
                "glCopyNamedBufferSubData", "glClearNamedBufferData", "glClearNamedBufferSubData", "glMapNamedBuffer", "glMapNamedBufferRange",
                "glUnmapNamedBuffer", "glFlushMappedNamedBufferRange", "glGetNamedBufferParameteriv", "glGetNamedBufferParameteri64v", "glGetNamedBufferPointerv",
                "glGetNamedBufferSubData", "glCreateFramebuffers", "glNamedFramebufferRenderbuffer", "glNamedFramebufferParameteri", "glNamedFramebufferTexture",
                "glNamedFramebufferTextureLayer", "glNamedFramebufferDrawBuffer", "glNamedFramebufferDrawBuffers", "glNamedFramebufferReadBuffer",
                "glInvalidateNamedFramebufferData", "glInvalidateNamedFramebufferSubData", "glClearNamedFramebufferiv", "glClearNamedFramebufferuiv",
                "glClearNamedFramebufferfv", "glClearNamedFramebufferfi", "glBlitNamedFramebuffer", "glCheckNamedFramebufferStatus",
                "glGetNamedFramebufferParameteriv", "glGetNamedFramebufferAttachmentParameteriv", "glCreateRenderbuffers", "glNamedRenderbufferStorage",
                "glNamedRenderbufferStorageMultisample", "glGetNamedRenderbufferParameteriv", "glCreateTextures", "glTextureBuffer", "glTextureBufferRange",
                "glTextureStorage1D", "glTextureStorage2D", "glTextureStorage3D", "glTextureStorage2DMultisample", "glTextureStorage3DMultisample",
                "glTextureSubImage1D", "glTextureSubImage2D", "glTextureSubImage3D", "glCompressedTextureSubImage1D", "glCompressedTextureSubImage2D",
                "glCompressedTextureSubImage3D", "glCopyTextureSubImage1D", "glCopyTextureSubImage2D", "glCopyTextureSubImage3D", "glTextureParameterf",
                "glTextureParameterfv", "glTextureParameteri", "glTextureParameterIiv", "glTextureParameterIuiv", "glTextureParameteriv", "glGenerateTextureMipmap",
                "glBindTextureUnit", "glGetTextureImage", "glGetCompressedTextureImage", "glGetTextureLevelParameterfv", "glGetTextureLevelParameteriv",
                "glGetTextureParameterfv", "glGetTextureParameterIiv", "glGetTextureParameterIuiv", "glGetTextureParameteriv", "glCreateVertexArrays",
                "glDisableVertexArrayAttrib", "glEnableVertexArrayAttrib", "glVertexArrayElementBuffer", "glVertexArrayVertexBuffer", "glVertexArrayVertexBuffers",
                "glVertexArrayAttribFormat", "glVertexArrayAttribIFormat", "glVertexArrayAttribLFormat", "glVertexArrayAttribBinding",
                "glVertexArrayBindingDivisor", "glGetVertexArrayiv", "glGetVertexArrayIndexediv", "glGetVertexArrayIndexed64iv", "glCreateSamplers",
                "glCreateProgramPipelines", "glCreateQueries", "glGetQueryBufferObjecti64v", "glGetQueryBufferObjectiv", "glGetQueryBufferObjectui64v",
                "glGetQueryBufferObjectuiv"
        )) || reportMissing("GL", "GL_ARB_direct_state_access");
    }

    private static boolean check_ARB_draw_buffers(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_ARB_draw_buffers")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1137
                },
                "glDrawBuffersARB"
        )) || reportMissing("GL", "GL_ARB_draw_buffers");
    }

    private static boolean check_ARB_draw_buffers_blend(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_ARB_draw_buffers_blend")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1138, 1139, 1140, 1141
                },
                "glBlendEquationiARB", "glBlendEquationSeparateiARB", "glBlendFunciARB", "glBlendFuncSeparateiARB"
        )) || reportMissing("GL", "GL_ARB_draw_buffers_blend");
    }

    private static boolean check_ARB_draw_elements_base_vertex(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_ARB_draw_elements_base_vertex")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        648, 649, 650, 651
                },
                "glDrawElementsBaseVertex", "glDrawRangeElementsBaseVertex", "glDrawElementsInstancedBaseVertex", "glMultiDrawElementsBaseVertex"
        )) || reportMissing("GL", "GL_ARB_draw_elements_base_vertex");
    }

    private static boolean check_ARB_draw_indirect(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_ARB_draw_indirect")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        728, 729
                },
                "glDrawArraysIndirect", "glDrawElementsIndirect"
        )) || reportMissing("GL", "GL_ARB_draw_indirect");
    }

    private static boolean check_ARB_draw_instanced(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_ARB_draw_instanced")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1142, 1143
                },
                "glDrawArraysInstancedARB", "glDrawElementsInstancedARB"
        )) || reportMissing("GL", "GL_ARB_draw_instanced");
    }

    private static boolean check_ARB_ES2_compatibility(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_ARB_ES2_compatibility")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        770, 771, 772, 773, 774
                },
                "glReleaseShaderCompiler", "glShaderBinary", "glGetShaderPrecisionFormat", "glDepthRangef", "glClearDepthf"
        )) || reportMissing("GL", "GL_ARB_ES2_compatibility");
    }

    private static boolean check_ARB_ES3_1_compatibility(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_ARB_ES3_1_compatibility")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1020
                },
                "glMemoryBarrierByRegion"
        )) || reportMissing("GL", "GL_ARB_ES3_1_compatibility");
    }

    private static boolean check_ARB_ES3_2_compatibility(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_ARB_ES3_2_compatibility")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1144
                },
                "glPrimitiveBoundingBoxARB"
        )) || reportMissing("GL", "GL_ARB_ES3_2_compatibility");
    }

    private static boolean check_ARB_framebuffer_no_attachments(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_ARB_framebuffer_no_attachments")) {
            return false;
        }

        int flag0 = ext.contains("GL_EXT_direct_state_access") ? 0 : Integer.MIN_VALUE;

        return (checkFunctions(provider, caps, new int[] {
                        885, 886, flag0 + 1145, flag0 + 1146
                },
                "glFramebufferParameteri", "glGetFramebufferParameteriv", "glNamedFramebufferParameteriEXT", "glGetNamedFramebufferParameterivEXT"
        )) || reportMissing("GL", "GL_ARB_framebuffer_no_attachments");
    }

    private static boolean check_ARB_framebuffer_object(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_ARB_framebuffer_object")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        595, 596, 597, 598, 599, 600, 601, 602, 603, 604, 605, 606, 607, 608, 609, 610, 611, 612, 613, 614
                },
                "glIsRenderbuffer", "glBindRenderbuffer", "glDeleteRenderbuffers", "glGenRenderbuffers", "glRenderbufferStorage",
                "glRenderbufferStorageMultisample", "glGetRenderbufferParameteriv", "glIsFramebuffer", "glBindFramebuffer", "glDeleteFramebuffers",
                "glGenFramebuffers", "glCheckFramebufferStatus", "glFramebufferTexture1D", "glFramebufferTexture2D", "glFramebufferTexture3D",
                "glFramebufferTextureLayer", "glFramebufferRenderbuffer", "glGetFramebufferAttachmentParameteriv", "glBlitFramebuffer", "glGenerateMipmap"
        )) || reportMissing("GL", "GL_ARB_framebuffer_object");
    }

    private static boolean check_ARB_geometry_shader4(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_ARB_geometry_shader4")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1147, 1148, 1149, 1150
                },
                "glProgramParameteriARB", "glFramebufferTextureARB", "glFramebufferTextureLayerARB", "glFramebufferTextureFaceARB"
        )) || reportMissing("GL", "GL_ARB_geometry_shader4");
    }

    private static boolean check_ARB_get_program_binary(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_ARB_get_program_binary")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        775, 776, 777
                },
                "glGetProgramBinary", "glProgramBinary", "glProgramParameteri"
        )) || reportMissing("GL", "GL_ARB_get_program_binary");
    }

    private static boolean check_ARB_get_texture_sub_image(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_ARB_get_texture_sub_image")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1021, 1022
                },
                "glGetTextureSubImage", "glGetCompressedTextureSubImage"
        )) || reportMissing("GL", "GL_ARB_get_texture_sub_image");
    }

    private static boolean check_ARB_gl_spirv(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_ARB_gl_spirv")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1151
                },
                "glSpecializeShaderARB"
        )) || reportMissing("GL", "GL_ARB_gl_spirv");
    }

    private static boolean check_ARB_gpu_shader_fp64(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_ARB_gpu_shader_fp64")) {
            return false;
        }

        int flag0 = ext.contains("GL_EXT_direct_state_access") ? 0 : Integer.MIN_VALUE;

        return (checkFunctions(provider, caps, new int[] {
                        730, 731, 732, 733, 734, 735, 736, 737, 738, 739, 740, 741, 742, 743, 744, 745, 746, 747
                },
                "glUniform1d", "glUniform2d", "glUniform3d", "glUniform4d", "glUniform1dv", "glUniform2dv", "glUniform3dv", "glUniform4dv", "glUniformMatrix2dv",
                "glUniformMatrix3dv", "glUniformMatrix4dv", "glUniformMatrix2x3dv", "glUniformMatrix2x4dv", "glUniformMatrix3x2dv", "glUniformMatrix3x4dv",
                "glUniformMatrix4x2dv", "glUniformMatrix4x3dv", "glGetUniformdv"
        )) || reportMissing("GL", "GL_ARB_gpu_shader_fp64");
    }

    private static boolean check_ARB_gpu_shader_int64(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_ARB_gpu_shader_int64")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1169, 1170, 1171, 1172, 1173, 1174, 1175, 1176, 1177, 1178, 1179, 1180, 1181, 1182, 1183, 1184, 1185, 1186, 1187, 1188, 1189, 1190, 1191, 1192,
                        1193, 1194, 1195, 1196, 1197, 1198, 1199, 1200, 1201, 1202, 1203, 1204
                },
                "glUniform1i64ARB", "glUniform1i64vARB", "glProgramUniform1i64ARB", "glProgramUniform1i64vARB", "glUniform2i64ARB", "glUniform2i64vARB",
                "glProgramUniform2i64ARB", "glProgramUniform2i64vARB", "glUniform3i64ARB", "glUniform3i64vARB", "glProgramUniform3i64ARB",
                "glProgramUniform3i64vARB", "glUniform4i64ARB", "glUniform4i64vARB", "glProgramUniform4i64ARB", "glProgramUniform4i64vARB", "glUniform1ui64ARB",
                "glUniform1ui64vARB", "glProgramUniform1ui64ARB", "glProgramUniform1ui64vARB", "glUniform2ui64ARB", "glUniform2ui64vARB",
                "glProgramUniform2ui64ARB", "glProgramUniform2ui64vARB", "glUniform3ui64ARB", "glUniform3ui64vARB", "glProgramUniform3ui64ARB",
                "glProgramUniform3ui64vARB", "glUniform4ui64ARB", "glUniform4ui64vARB", "glProgramUniform4ui64ARB", "glProgramUniform4ui64vARB",
                "glGetUniformi64vARB", "glGetUniformui64vARB", "glGetnUniformi64vARB", "glGetnUniformui64vARB"
        )) || reportMissing("GL", "GL_ARB_gpu_shader_int64");
    }

    private static boolean check_ARB_imaging(FunctionProvider provider, PointerBuffer caps, Set<String> ext, boolean fc) {
        if (!ext.contains("GL_ARB_imaging")) {
            return false;
        }

        return ((fc || checkFunctions(provider, caps, new int[] {
                        1205, 1206, 1207, 1208, 1209, 1210, 1211, 1212, 1213, 1214, 1215, 1216, 1217, 1218, 1219, 1220, 1221, 1222, 1223, 1224, 1225, 1226, 1227, 1228,
                        1229, 1230, 1231, 1232, 1233, 1234, 1235, 1236
                },
                "glColorTable", "glCopyColorTable", "glColorTableParameteriv", "glColorTableParameterfv", "glGetColorTable", "glGetColorTableParameteriv",
                "glGetColorTableParameterfv", "glColorSubTable", "glCopyColorSubTable", "glConvolutionFilter1D", "glConvolutionFilter2D",
                "glCopyConvolutionFilter1D", "glCopyConvolutionFilter2D", "glGetConvolutionFilter", "glSeparableFilter2D", "glGetSeparableFilter",
                "glConvolutionParameteri", "glConvolutionParameteriv", "glConvolutionParameterf", "glConvolutionParameterfv", "glGetConvolutionParameteriv",
                "glGetConvolutionParameterfv", "glHistogram", "glResetHistogram", "glGetHistogram", "glGetHistogramParameteriv", "glGetHistogramParameterfv",
                "glMinmax", "glResetMinmax", "glGetMinmax", "glGetMinmaxParameteriv", "glGetMinmaxParameterfv"
        )) & checkFunctions(provider, caps, new int[] {
                        386, 387
                },
                "glBlendColor", "glBlendEquation"
        )) || reportMissing("GL", "GL_ARB_imaging");
    }

    private static boolean check_ARB_indirect_parameters(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_ARB_indirect_parameters")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1237, 1238
                },
                "glMultiDrawArraysIndirectCountARB", "glMultiDrawElementsIndirectCountARB"
        )) || reportMissing("GL", "GL_ARB_indirect_parameters");
    }

    private static boolean check_ARB_instanced_arrays(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_ARB_instanced_arrays")) {
            return false;
        }

        int flag0 = ext.contains("GL_EXT_direct_state_access") ? 0 : Integer.MIN_VALUE;

        return (checkFunctions(provider, caps, new int[] {
                        1239
                },
                "glVertexAttribDivisorARB"
        )) || reportMissing("GL", "GL_ARB_instanced_arrays");
    }

    private static boolean check_ARB_internalformat_query(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_ARB_internalformat_query")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        869
                },
                "glGetInternalformativ"
        )) || reportMissing("GL", "GL_ARB_internalformat_query");
    }

    private static boolean check_ARB_internalformat_query2(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_ARB_internalformat_query2")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        887
                },
                "glGetInternalformati64v"
        )) || reportMissing("GL", "GL_ARB_internalformat_query2");
    }

    private static boolean check_ARB_invalidate_subdata(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_ARB_invalidate_subdata")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        888, 889, 890, 891, 892, 893
                },
                "glInvalidateTexSubImage", "glInvalidateTexImage", "glInvalidateBufferSubData", "glInvalidateBufferData", "glInvalidateFramebuffer",
                "glInvalidateSubFramebuffer"
        )) || reportMissing("GL", "GL_ARB_invalidate_subdata");
    }

    private static boolean check_ARB_map_buffer_range(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_ARB_map_buffer_range")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        592, 593
                },
                "glMapBufferRange", "glFlushMappedBufferRange"
        )) || reportMissing("GL", "GL_ARB_map_buffer_range");
    }

    private static boolean check_ARB_matrix_palette(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_ARB_matrix_palette")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1241, 1242, 1243, 1244, 1245
                },
                "glCurrentPaletteMatrixARB", "glMatrixIndexuivARB", "glMatrixIndexubvARB", "glMatrixIndexusvARB", "glMatrixIndexPointerARB"
        )) || reportMissing("GL", "GL_ARB_matrix_palette");
    }

    private static boolean check_ARB_multi_bind(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_ARB_multi_bind")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        916, 917, 918, 919, 920, 921
                },
                "glBindBuffersBase", "glBindBuffersRange", "glBindTextures", "glBindSamplers", "glBindImageTextures", "glBindVertexBuffers"
        )) || reportMissing("GL", "GL_ARB_multi_bind");
    }

    private static boolean check_ARB_multi_draw_indirect(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_ARB_multi_draw_indirect")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        894, 895
                },
                "glMultiDrawArraysIndirect", "glMultiDrawElementsIndirect"
        )) || reportMissing("GL", "GL_ARB_multi_draw_indirect");
    }

    private static boolean check_ARB_multisample(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_ARB_multisample")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1246
                },
                "glSampleCoverageARB"
        )) || reportMissing("GL", "GL_ARB_multisample");
    }

    private static boolean check_ARB_multitexture(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_ARB_multitexture")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1247, 1248, 1249, 1250, 1251, 1252, 1253, 1254, 1255, 1256, 1257, 1258, 1259, 1260, 1261, 1262, 1263, 1264, 1265, 1266, 1267, 1268, 1269, 1270,
                        1271, 1272, 1273, 1274, 1275, 1276, 1277, 1278, 1279, 1280
                },
                "glActiveTextureARB", "glClientActiveTextureARB", "glMultiTexCoord1fARB", "glMultiTexCoord1sARB", "glMultiTexCoord1iARB", "glMultiTexCoord1dARB",
                "glMultiTexCoord1fvARB", "glMultiTexCoord1svARB", "glMultiTexCoord1ivARB", "glMultiTexCoord1dvARB", "glMultiTexCoord2fARB", "glMultiTexCoord2sARB",
                "glMultiTexCoord2iARB", "glMultiTexCoord2dARB", "glMultiTexCoord2fvARB", "glMultiTexCoord2svARB", "glMultiTexCoord2ivARB", "glMultiTexCoord2dvARB",
                "glMultiTexCoord3fARB", "glMultiTexCoord3sARB", "glMultiTexCoord3iARB", "glMultiTexCoord3dARB", "glMultiTexCoord3fvARB", "glMultiTexCoord3svARB",
                "glMultiTexCoord3ivARB", "glMultiTexCoord3dvARB", "glMultiTexCoord4fARB", "glMultiTexCoord4sARB", "glMultiTexCoord4iARB", "glMultiTexCoord4dARB",
                "glMultiTexCoord4fvARB", "glMultiTexCoord4svARB", "glMultiTexCoord4ivARB", "glMultiTexCoord4dvARB"
        )) || reportMissing("GL", "GL_ARB_multitexture");
    }

    private static boolean check_ARB_occlusion_query(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_ARB_occlusion_query")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1281, 1282, 1283, 1284, 1285, 1286, 1287, 1288
                },
                "glGenQueriesARB", "glDeleteQueriesARB", "glIsQueryARB", "glBeginQueryARB", "glEndQueryARB", "glGetQueryivARB", "glGetQueryObjectivARB",
                "glGetQueryObjectuivARB"
        )) || reportMissing("GL", "GL_ARB_occlusion_query");
    }

    private static boolean check_ARB_parallel_shader_compile(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_ARB_parallel_shader_compile")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1289
                },
                "glMaxShaderCompilerThreadsARB"
        )) || reportMissing("GL", "GL_ARB_parallel_shader_compile");
    }

    private static boolean check_ARB_point_parameters(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_ARB_point_parameters")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1290, 1291
                },
                "glPointParameterfARB", "glPointParameterfvARB"
        )) || reportMissing("GL", "GL_ARB_point_parameters");
    }

    private static boolean check_ARB_polygon_offset_clamp(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_ARB_polygon_offset_clamp")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1046
                },
                "glPolygonOffsetClamp"
        )) || reportMissing("GL", "GL_ARB_polygon_offset_clamp");
    }

    private static boolean check_ARB_program_interface_query(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_ARB_program_interface_query")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        896, 897, 898, 899, 900, 901
                },
                "glGetProgramInterfaceiv", "glGetProgramResourceIndex", "glGetProgramResourceName", "glGetProgramResourceiv", "glGetProgramResourceLocation",
                "glGetProgramResourceLocationIndex"
        )) || reportMissing("GL", "GL_ARB_program_interface_query");
    }

    private static boolean check_ARB_provoking_vertex(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_ARB_provoking_vertex")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        652
                },
                "glProvokingVertex"
        )) || reportMissing("GL", "GL_ARB_provoking_vertex");
    }

    private static boolean check_ARB_robustness(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_ARB_robustness")) {
            return false;
        }

        int flag0 = provider.getFunctionAddress("glGetMapdv") != NULL ? 0 : Integer.MIN_VALUE;
        int flag1 = provider.getFunctionAddress("glGetMapfv") != NULL ? 0 : Integer.MIN_VALUE;
        int flag2 = provider.getFunctionAddress("glGetMapiv") != NULL ? 0 : Integer.MIN_VALUE;
        int flag3 = provider.getFunctionAddress("glGetPixelMapfv") != NULL ? 0 : Integer.MIN_VALUE;
        int flag4 = provider.getFunctionAddress("glGetPixelMapuiv") != NULL ? 0 : Integer.MIN_VALUE;
        int flag5 = provider.getFunctionAddress("glGetPixelMapusv") != NULL ? 0 : Integer.MIN_VALUE;
        int flag6 = provider.getFunctionAddress("glGetPolygonStipple") != NULL ? 0 : Integer.MIN_VALUE;
        int flag7 = ext.contains("GL_ARB_imaging") & provider.getFunctionAddress("glGetColorTable") != NULL ? 0 : Integer.MIN_VALUE;
        int flag8 = ext.contains("GL_ARB_imaging") & provider.getFunctionAddress("glGetConvolutionFilter") != NULL ? 0 : Integer.MIN_VALUE;
        int flag9 = ext.contains("GL_ARB_imaging") & provider.getFunctionAddress("glGetSeparableFilter") != NULL ? 0 : Integer.MIN_VALUE;
        int flag10 = ext.contains("GL_ARB_imaging") & provider.getFunctionAddress("glGetHistogram") != NULL ? 0 : Integer.MIN_VALUE;
        int flag11 = ext.contains("GL_ARB_imaging") & provider.getFunctionAddress("glGetMinmax") != NULL ? 0 : Integer.MIN_VALUE;
        int flag12 = ext.contains("OpenGL13") ? 0 : Integer.MIN_VALUE;
        int flag13 = ext.contains("OpenGL20") ? 0 : Integer.MIN_VALUE;
        int flag15 = ext.contains("OpenGL30") ? 0 : Integer.MIN_VALUE;
        int flag16 = ext.contains("OpenGL40") ? 0 : Integer.MIN_VALUE;

        return (checkFunctions(provider, caps, new int[] {
                        1292, flag0 + 1293, flag1 + 1294, flag2 + 1295, flag3 + 1296, flag4 + 1297, flag5 + 1298, flag6 + 1299, 1300, 1301, flag7 + 1302, flag8 + 1303,
                        flag9 + 1304, flag10 + 1305, flag11 + 1306, flag12 + 1307, flag13 + 1308, flag13 + 1309, flag15 + 1310, flag16 + 1311
                },
                "glGetGraphicsResetStatusARB", "glGetnMapdvARB", "glGetnMapfvARB", "glGetnMapivARB", "glGetnPixelMapfvARB", "glGetnPixelMapuivARB",
                "glGetnPixelMapusvARB", "glGetnPolygonStippleARB", "glGetnTexImageARB", "glReadnPixelsARB", "glGetnColorTableARB", "glGetnConvolutionFilterARB",
                "glGetnSeparableFilterARB", "glGetnHistogramARB", "glGetnMinmaxARB", "glGetnCompressedTexImageARB", "glGetnUniformfvARB", "glGetnUniformivARB",
                "glGetnUniformuivARB", "glGetnUniformdvARB"
        )) || reportMissing("GL", "GL_ARB_robustness");
    }

    private static boolean check_ARB_sample_locations(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_ARB_sample_locations")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1312, 1313, 1314
                },
                "glFramebufferSampleLocationsfvARB", "glNamedFramebufferSampleLocationsfvARB", "glEvaluateDepthValuesARB"
        )) || reportMissing("GL", "GL_ARB_sample_locations");
    }

    private static boolean check_ARB_sample_shading(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_ARB_sample_shading")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1315
                },
                "glMinSampleShadingARB"
        )) || reportMissing("GL", "GL_ARB_sample_shading");
    }

    private static boolean check_ARB_sampler_objects(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_ARB_sampler_objects")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        668, 669, 670, 671, 672, 673, 674, 675, 676, 677, 678, 679, 680, 681
                },
                "glGenSamplers", "glDeleteSamplers", "glIsSampler", "glBindSampler", "glSamplerParameteri", "glSamplerParameterf", "glSamplerParameteriv",
                "glSamplerParameterfv", "glSamplerParameterIiv", "glSamplerParameterIuiv", "glGetSamplerParameteriv", "glGetSamplerParameterfv",
                "glGetSamplerParameterIiv", "glGetSamplerParameterIuiv"
        )) || reportMissing("GL", "GL_ARB_sampler_objects");
    }

    private static boolean check_ARB_separate_shader_objects(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_ARB_separate_shader_objects")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        778, 779, 780, 781, 782, 783, 784, 777, 785, 786, 787, 788, 789, 790, 791, 792, 793, 794, 795, 796, 797, 798, 799, 800, 801, 802, 803, 804, 805,
                        806, 807, 808, 809, 810, 811, 812, 813, 814, 815, 816, 817, 818, 819, 820, 821, 822, 823, 824, 825, 826, 827, 828, 829, 830, 831, 832, 833, 834,
                        835, 836, 837
                },
                "glUseProgramStages", "glActiveShaderProgram", "glCreateShaderProgramv", "glBindProgramPipeline", "glDeleteProgramPipelines",
                "glGenProgramPipelines", "glIsProgramPipeline", "glProgramParameteri", "glGetProgramPipelineiv", "glProgramUniform1i", "glProgramUniform2i",
                "glProgramUniform3i", "glProgramUniform4i", "glProgramUniform1ui", "glProgramUniform2ui", "glProgramUniform3ui", "glProgramUniform4ui",
                "glProgramUniform1f", "glProgramUniform2f", "glProgramUniform3f", "glProgramUniform4f", "glProgramUniform1d", "glProgramUniform2d",
                "glProgramUniform3d", "glProgramUniform4d", "glProgramUniform1iv", "glProgramUniform2iv", "glProgramUniform3iv", "glProgramUniform4iv",
                "glProgramUniform1uiv", "glProgramUniform2uiv", "glProgramUniform3uiv", "glProgramUniform4uiv", "glProgramUniform1fv", "glProgramUniform2fv",
                "glProgramUniform3fv", "glProgramUniform4fv", "glProgramUniform1dv", "glProgramUniform2dv", "glProgramUniform3dv", "glProgramUniform4dv",
                "glProgramUniformMatrix2fv", "glProgramUniformMatrix3fv", "glProgramUniformMatrix4fv", "glProgramUniformMatrix2dv", "glProgramUniformMatrix3dv",
                "glProgramUniformMatrix4dv", "glProgramUniformMatrix2x3fv", "glProgramUniformMatrix3x2fv", "glProgramUniformMatrix2x4fv",
                "glProgramUniformMatrix4x2fv", "glProgramUniformMatrix3x4fv", "glProgramUniformMatrix4x3fv", "glProgramUniformMatrix2x3dv",
                "glProgramUniformMatrix3x2dv", "glProgramUniformMatrix2x4dv", "glProgramUniformMatrix4x2dv", "glProgramUniformMatrix3x4dv",
                "glProgramUniformMatrix4x3dv", "glValidateProgramPipeline", "glGetProgramPipelineInfoLog"
        )) || reportMissing("GL", "GL_ARB_separate_shader_objects");
    }

    private static boolean check_ARB_shader_atomic_counters(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_ARB_shader_atomic_counters")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        858
                },
                "glGetActiveAtomicCounterBufferiv"
        )) || reportMissing("GL", "GL_ARB_shader_atomic_counters");
    }

    private static boolean check_ARB_shader_image_load_store(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_ARB_shader_image_load_store")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        867, 868
                },
                "glBindImageTexture", "glMemoryBarrier"
        )) || reportMissing("GL", "GL_ARB_shader_image_load_store");
    }

    private static boolean check_ARB_shader_objects(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_ARB_shader_objects")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1316, 1317, 1318, 1319, 1320, 1321, 1322, 1323, 1324, 1325, 1326, 1327, 1328, 1329, 1330, 1331, 1332, 1333, 1334, 1335, 1336, 1337, 1338, 1339,
                        1340, 1341, 1342, 1343, 1344, 1345, 1346, 1347, 1348, 1349, 1350, 1351, 1352, 1353, 1354
                },
                "glDeleteObjectARB", "glGetHandleARB", "glDetachObjectARB", "glCreateShaderObjectARB", "glShaderSourceARB", "glCompileShaderARB",
                "glCreateProgramObjectARB", "glAttachObjectARB", "glLinkProgramARB", "glUseProgramObjectARB", "glValidateProgramARB", "glUniform1fARB",
                "glUniform2fARB", "glUniform3fARB", "glUniform4fARB", "glUniform1iARB", "glUniform2iARB", "glUniform3iARB", "glUniform4iARB", "glUniform1fvARB",
                "glUniform2fvARB", "glUniform3fvARB", "glUniform4fvARB", "glUniform1ivARB", "glUniform2ivARB", "glUniform3ivARB", "glUniform4ivARB",
                "glUniformMatrix2fvARB", "glUniformMatrix3fvARB", "glUniformMatrix4fvARB", "glGetObjectParameterfvARB", "glGetObjectParameterivARB",
                "glGetInfoLogARB", "glGetAttachedObjectsARB", "glGetUniformLocationARB", "glGetActiveUniformARB", "glGetUniformfvARB", "glGetUniformivARB",
                "glGetShaderSourceARB"
        )) || reportMissing("GL", "GL_ARB_shader_objects");
    }

    private static boolean check_ARB_shader_storage_buffer_object(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_ARB_shader_storage_buffer_object")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        902
                },
                "glShaderStorageBlockBinding"
        )) || reportMissing("GL", "GL_ARB_shader_storage_buffer_object");
    }

    private static boolean check_ARB_shader_subroutine(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_ARB_shader_subroutine")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        749, 750, 751, 752, 753, 754, 755, 756
                },
                "glGetSubroutineUniformLocation", "glGetSubroutineIndex", "glGetActiveSubroutineUniformiv", "glGetActiveSubroutineUniformName",
                "glGetActiveSubroutineName", "glUniformSubroutinesuiv", "glGetUniformSubroutineuiv", "glGetProgramStageiv"
        )) || reportMissing("GL", "GL_ARB_shader_subroutine");
    }

    private static boolean check_ARB_shading_language_include(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_ARB_shading_language_include")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1355, 1356, 1357, 1358, 1359, 1360
                },
                "glNamedStringARB", "glDeleteNamedStringARB", "glCompileShaderIncludeARB", "glIsNamedStringARB", "glGetNamedStringARB", "glGetNamedStringivARB"
        )) || reportMissing("GL", "GL_ARB_shading_language_include");
    }

    private static boolean check_ARB_sparse_buffer(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_ARB_sparse_buffer")) {
            return false;
        }

        int flag0 = ext.contains("GL_EXT_direct_state_access") ? 0 : Integer.MIN_VALUE;
        int flag1 = ext.contains("GL_ARB_direct_state_access") ? 0 : Integer.MIN_VALUE;

        return (checkFunctions(provider, caps, new int[] {
                        1361
                },
                "glBufferPageCommitmentARB"
        )) || reportMissing("GL", "GL_ARB_sparse_buffer");
    }

    private static boolean check_ARB_sparse_texture(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_ARB_sparse_texture")) {
            return false;
        }

        int flag0 = ext.contains("GL_EXT_direct_state_access") ? 0 : Integer.MIN_VALUE;

        return (checkFunctions(provider, caps, new int[] {
                        1364, flag0 + 1365
                },
                "glTexPageCommitmentARB", "glTexturePageCommitmentEXT"
        )) || reportMissing("GL", "GL_ARB_sparse_texture");
    }

    private static boolean check_ARB_sync(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_ARB_sync")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        658, 659, 660, 661, 662, 663, 665
                },
                "glFenceSync", "glIsSync", "glDeleteSync", "glClientWaitSync", "glWaitSync", "glGetInteger64v", "glGetSynciv"
        )) || reportMissing("GL", "GL_ARB_sync");
    }

    private static boolean check_ARB_tessellation_shader(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_ARB_tessellation_shader")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        757, 758
                },
                "glPatchParameteri", "glPatchParameterfv"
        )) || reportMissing("GL", "GL_ARB_tessellation_shader");
    }

    private static boolean check_ARB_texture_barrier(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_ARB_texture_barrier")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1023
                },
                "glTextureBarrier"
        )) || reportMissing("GL", "GL_ARB_texture_barrier");
    }

    private static boolean check_ARB_texture_buffer_object(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_ARB_texture_buffer_object")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1366
                },
                "glTexBufferARB"
        )) || reportMissing("GL", "GL_ARB_texture_buffer_object");
    }

    private static boolean check_ARB_texture_buffer_range(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_ARB_texture_buffer_range")) {
            return false;
        }

        int flag0 = ext.contains("GL_EXT_direct_state_access") ? 0 : Integer.MIN_VALUE;

        return (checkFunctions(provider, caps, new int[] {
                        903, flag0 + 1367
                },
                "glTexBufferRange", "glTextureBufferRangeEXT"
        )) || reportMissing("GL", "GL_ARB_texture_buffer_range");
    }

    private static boolean check_ARB_texture_compression(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_ARB_texture_compression")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1368, 1369, 1370, 1371, 1372, 1373, 1374
                },
                "glCompressedTexImage3DARB", "glCompressedTexImage2DARB", "glCompressedTexImage1DARB", "glCompressedTexSubImage3DARB",
                "glCompressedTexSubImage2DARB", "glCompressedTexSubImage1DARB", "glGetCompressedTexImageARB"
        )) || reportMissing("GL", "GL_ARB_texture_compression");
    }

    private static boolean check_ARB_texture_multisample(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_ARB_texture_multisample")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        653, 654, 655, 656
                },
                "glTexImage2DMultisample", "glTexImage3DMultisample", "glGetMultisamplefv", "glSampleMaski"
        )) || reportMissing("GL", "GL_ARB_texture_multisample");
    }

    private static boolean check_ARB_texture_storage(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_ARB_texture_storage")) {
            return false;
        }

        int flag0 = ext.contains("GL_EXT_direct_state_access") ? 0 : Integer.MIN_VALUE;

        return (checkFunctions(provider, caps, new int[] {
                        859, 860, 861, flag0 + 1375, flag0 + 1376, flag0 + 1377
                },
                "glTexStorage1D", "glTexStorage2D", "glTexStorage3D", "glTextureStorage1DEXT", "glTextureStorage2DEXT", "glTextureStorage3DEXT"
        )) || reportMissing("GL", "GL_ARB_texture_storage");
    }

    private static boolean check_ARB_texture_storage_multisample(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_ARB_texture_storage_multisample")) {
            return false;
        }

        int flag0 = ext.contains("GL_EXT_direct_state_access") ? 0 : Integer.MIN_VALUE;

        return (checkFunctions(provider, caps, new int[] {
                        904, 905, flag0 + 1378, flag0 + 1379
                },
                "glTexStorage2DMultisample", "glTexStorage3DMultisample", "glTextureStorage2DMultisampleEXT", "glTextureStorage3DMultisampleEXT"
        )) || reportMissing("GL", "GL_ARB_texture_storage_multisample");
    }

    private static boolean check_ARB_texture_view(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_ARB_texture_view")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        906
                },
                "glTextureView"
        )) || reportMissing("GL", "GL_ARB_texture_view");
    }

    private static boolean check_ARB_timer_query(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_ARB_timer_query")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        682, 683, 684
                },
                "glQueryCounter", "glGetQueryObjecti64v", "glGetQueryObjectui64v"
        )) || reportMissing("GL", "GL_ARB_timer_query");
    }

    private static boolean check_ARB_transform_feedback2(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_ARB_transform_feedback2")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        759, 760, 761, 762, 763, 764, 765
                },
                "glBindTransformFeedback", "glDeleteTransformFeedbacks", "glGenTransformFeedbacks", "glIsTransformFeedback", "glPauseTransformFeedback",
                "glResumeTransformFeedback", "glDrawTransformFeedback"
        )) || reportMissing("GL", "GL_ARB_transform_feedback2");
    }

    private static boolean check_ARB_transform_feedback3(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_ARB_transform_feedback3")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        766, 767, 768, 769
                },
                "glDrawTransformFeedbackStream", "glBeginQueryIndexed", "glEndQueryIndexed", "glGetQueryIndexediv"
        )) || reportMissing("GL", "GL_ARB_transform_feedback3");
    }

    private static boolean check_ARB_transform_feedback_instanced(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_ARB_transform_feedback_instanced")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        862, 863
                },
                "glDrawTransformFeedbackInstanced", "glDrawTransformFeedbackStreamInstanced"
        )) || reportMissing("GL", "GL_ARB_transform_feedback_instanced");
    }

    private static boolean check_ARB_transpose_matrix(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_ARB_transpose_matrix")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1380, 1381, 1382, 1383
                },
                "glLoadTransposeMatrixfARB", "glLoadTransposeMatrixdARB", "glMultTransposeMatrixfARB", "glMultTransposeMatrixdARB"
        )) || reportMissing("GL", "GL_ARB_transpose_matrix");
    }

    private static boolean check_ARB_uniform_buffer_object(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_ARB_uniform_buffer_object")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        640, 641, 642, 643, 644, 645, 625, 626, 621, 646
                },
                "glGetUniformIndices", "glGetActiveUniformsiv", "glGetActiveUniformName", "glGetUniformBlockIndex", "glGetActiveUniformBlockiv",
                "glGetActiveUniformBlockName", "glBindBufferRange", "glBindBufferBase", "glGetIntegeri_v", "glUniformBlockBinding"
        )) || reportMissing("GL", "GL_ARB_uniform_buffer_object");
    }

    private static boolean check_ARB_vertex_array_object(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_ARB_vertex_array_object")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        631, 632, 633, 634
                },
                "glBindVertexArray", "glDeleteVertexArrays", "glGenVertexArrays", "glIsVertexArray"
        )) || reportMissing("GL", "GL_ARB_vertex_array_object");
    }

    private static boolean check_ARB_vertex_attrib_64bit(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_ARB_vertex_attrib_64bit")) {
            return false;
        }

        int flag0 = ext.contains("GL_EXT_direct_state_access") ? 0 : Integer.MIN_VALUE;

        return (checkFunctions(provider, caps, new int[] {
                        838, 839, 840, 841, 842, 843, 844, 845, 846, 847, flag0 + 1384
                },
                "glVertexAttribL1d", "glVertexAttribL2d", "glVertexAttribL3d", "glVertexAttribL4d", "glVertexAttribL1dv", "glVertexAttribL2dv",
                "glVertexAttribL3dv", "glVertexAttribL4dv", "glVertexAttribLPointer", "glGetVertexAttribLdv", "glVertexArrayVertexAttribLOffsetEXT"
        )) || reportMissing("GL", "GL_ARB_vertex_attrib_64bit");
    }

    private static boolean check_ARB_vertex_attrib_binding(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_ARB_vertex_attrib_binding")) {
            return false;
        }

        int flag0 = ext.contains("GL_EXT_direct_state_access") ? 0 : Integer.MIN_VALUE;

        return (checkFunctions(provider, caps, new int[] {
                        907, 908, 909, 910, 911, 912, flag0 + 1385, flag0 + 1386, flag0 + 1387, flag0 + 1388, flag0 + 1389, flag0 + 1390
                },
                "glBindVertexBuffer", "glVertexAttribFormat", "glVertexAttribIFormat", "glVertexAttribLFormat", "glVertexAttribBinding", "glVertexBindingDivisor",
                "glVertexArrayBindVertexBufferEXT", "glVertexArrayVertexAttribFormatEXT", "glVertexArrayVertexAttribIFormatEXT",
                "glVertexArrayVertexAttribLFormatEXT", "glVertexArrayVertexAttribBindingEXT", "glVertexArrayVertexBindingDivisorEXT"
        )) || reportMissing("GL", "GL_ARB_vertex_attrib_binding");
    }

    private static boolean check_ARB_vertex_blend(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_ARB_vertex_blend")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1391, 1392, 1393, 1394, 1395, 1396, 1397, 1398, 1399, 1400
                },
                "glWeightfvARB", "glWeightbvARB", "glWeightubvARB", "glWeightsvARB", "glWeightusvARB", "glWeightivARB", "glWeightuivARB", "glWeightdvARB",
                "glWeightPointerARB", "glVertexBlendARB"
        )) || reportMissing("GL", "GL_ARB_vertex_blend");
    }

    private static boolean check_ARB_vertex_buffer_object(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_ARB_vertex_buffer_object")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1401, 1402, 1403, 1404, 1405, 1406, 1407, 1408, 1409, 1410, 1411
                },
                "glBindBufferARB", "glDeleteBuffersARB", "glGenBuffersARB", "glIsBufferARB", "glBufferDataARB", "glBufferSubDataARB", "glGetBufferSubDataARB",
                "glMapBufferARB", "glUnmapBufferARB", "glGetBufferParameterivARB", "glGetBufferPointervARB"
        )) || reportMissing("GL", "GL_ARB_vertex_buffer_object");
    }

    private static boolean check_ARB_vertex_program(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_ARB_vertex_program")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1412, 1413, 1414, 1415, 1416, 1417, 1418, 1419, 1420, 1421, 1422, 1423, 1424, 1425, 1426, 1427, 1428, 1429, 1430, 1431, 1432, 1433, 1434, 1435,
                        1436, 1437, 1438, 1439, 1440, 1441, 1442, 1443, 1444, 1445, 1446, 1447, 1448, 1449, 1450, 1451, 1452, 1453, 1454, 1455, 1456, 1457, 1458, 1459,
                        1460, 1461, 1462, 1463, 1464, 1465, 1466, 1467, 1468, 1469, 1470, 1471, 1472, 1473
                },
                "glVertexAttrib1sARB", "glVertexAttrib1fARB", "glVertexAttrib1dARB", "glVertexAttrib2sARB", "glVertexAttrib2fARB", "glVertexAttrib2dARB",
                "glVertexAttrib3sARB", "glVertexAttrib3fARB", "glVertexAttrib3dARB", "glVertexAttrib4sARB", "glVertexAttrib4fARB", "glVertexAttrib4dARB",
                "glVertexAttrib4NubARB", "glVertexAttrib1svARB", "glVertexAttrib1fvARB", "glVertexAttrib1dvARB", "glVertexAttrib2svARB", "glVertexAttrib2fvARB",
                "glVertexAttrib2dvARB", "glVertexAttrib3svARB", "glVertexAttrib3fvARB", "glVertexAttrib3dvARB", "glVertexAttrib4fvARB", "glVertexAttrib4bvARB",
                "glVertexAttrib4svARB", "glVertexAttrib4ivARB", "glVertexAttrib4ubvARB", "glVertexAttrib4usvARB", "glVertexAttrib4uivARB", "glVertexAttrib4dvARB",
                "glVertexAttrib4NbvARB", "glVertexAttrib4NsvARB", "glVertexAttrib4NivARB", "glVertexAttrib4NubvARB", "glVertexAttrib4NusvARB",
                "glVertexAttrib4NuivARB", "glVertexAttribPointerARB", "glEnableVertexAttribArrayARB", "glDisableVertexAttribArrayARB", "glProgramStringARB",
                "glBindProgramARB", "glDeleteProgramsARB", "glGenProgramsARB", "glProgramEnvParameter4dARB", "glProgramEnvParameter4dvARB",
                "glProgramEnvParameter4fARB", "glProgramEnvParameter4fvARB", "glProgramLocalParameter4dARB", "glProgramLocalParameter4dvARB",
                "glProgramLocalParameter4fARB", "glProgramLocalParameter4fvARB", "glGetProgramEnvParameterfvARB", "glGetProgramEnvParameterdvARB",
                "glGetProgramLocalParameterfvARB", "glGetProgramLocalParameterdvARB", "glGetProgramivARB", "glGetProgramStringARB", "glGetVertexAttribfvARB",
                "glGetVertexAttribdvARB", "glGetVertexAttribivARB", "glGetVertexAttribPointervARB", "glIsProgramARB"
        )) || reportMissing("GL", "GL_ARB_vertex_program");
    }

    private static boolean check_ARB_vertex_shader(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_ARB_vertex_shader")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1413, 1412, 1414, 1416, 1415, 1417, 1419, 1418, 1420, 1422, 1421, 1423, 1424, 1426, 1425, 1427, 1429, 1428, 1430, 1432, 1431, 1433, 1434, 1436,
                        1441, 1437, 1435, 1438, 1439, 1440, 1442, 1443, 1444, 1445, 1446, 1447, 1448, 1449, 1450, 1474, 1475, 1476, 1471, 1469, 1470, 1472
                },
                "glVertexAttrib1fARB", "glVertexAttrib1sARB", "glVertexAttrib1dARB", "glVertexAttrib2fARB", "glVertexAttrib2sARB", "glVertexAttrib2dARB",
                "glVertexAttrib3fARB", "glVertexAttrib3sARB", "glVertexAttrib3dARB", "glVertexAttrib4fARB", "glVertexAttrib4sARB", "glVertexAttrib4dARB",
                "glVertexAttrib4NubARB", "glVertexAttrib1fvARB", "glVertexAttrib1svARB", "glVertexAttrib1dvARB", "glVertexAttrib2fvARB", "glVertexAttrib2svARB",
                "glVertexAttrib2dvARB", "glVertexAttrib3fvARB", "glVertexAttrib3svARB", "glVertexAttrib3dvARB", "glVertexAttrib4fvARB", "glVertexAttrib4svARB",
                "glVertexAttrib4dvARB", "glVertexAttrib4ivARB", "glVertexAttrib4bvARB", "glVertexAttrib4ubvARB", "glVertexAttrib4usvARB", "glVertexAttrib4uivARB",
                "glVertexAttrib4NbvARB", "glVertexAttrib4NsvARB", "glVertexAttrib4NivARB", "glVertexAttrib4NubvARB", "glVertexAttrib4NusvARB",
                "glVertexAttrib4NuivARB", "glVertexAttribPointerARB", "glEnableVertexAttribArrayARB", "glDisableVertexAttribArrayARB", "glBindAttribLocationARB",
                "glGetActiveAttribARB", "glGetAttribLocationARB", "glGetVertexAttribivARB", "glGetVertexAttribfvARB", "glGetVertexAttribdvARB",
                "glGetVertexAttribPointervARB"
        )) || reportMissing("GL", "GL_ARB_vertex_shader");
    }

    private static boolean check_ARB_vertex_type_2_10_10_10_rev(FunctionProvider provider, PointerBuffer caps, Set<String> ext, boolean fc) {
        if (!ext.contains("GL_ARB_vertex_type_2_10_10_10_rev")) {
            return false;
        }

        return ((fc || checkFunctions(provider, caps, new int[] {
                        686, 687, 688, 689, 690, 691, 692, 693, 694, 695, 696, 697, 698, 699, 700, 701, 702, 703, 704, 705, 706, 707, 708, 709, 710, 711, 712, 713, 714,
                        715
                },
                "glVertexP2ui", "glVertexP3ui", "glVertexP4ui", "glVertexP2uiv", "glVertexP3uiv", "glVertexP4uiv", "glTexCoordP1ui", "glTexCoordP2ui",
                "glTexCoordP3ui", "glTexCoordP4ui", "glTexCoordP1uiv", "glTexCoordP2uiv", "glTexCoordP3uiv", "glTexCoordP4uiv", "glMultiTexCoordP1ui",
                "glMultiTexCoordP2ui", "glMultiTexCoordP3ui", "glMultiTexCoordP4ui", "glMultiTexCoordP1uiv", "glMultiTexCoordP2uiv", "glMultiTexCoordP3uiv",
                "glMultiTexCoordP4uiv", "glNormalP3ui", "glNormalP3uiv", "glColorP3ui", "glColorP4ui", "glColorP3uiv", "glColorP4uiv", "glSecondaryColorP3ui",
                "glSecondaryColorP3uiv"
        )) & checkFunctions(provider, caps, new int[] {
                        716, 717, 718, 719, 720, 721, 722, 723
                },
                "glVertexAttribP1ui", "glVertexAttribP2ui", "glVertexAttribP3ui", "glVertexAttribP4ui", "glVertexAttribP1uiv", "glVertexAttribP2uiv",
                "glVertexAttribP3uiv", "glVertexAttribP4uiv"
        )) || reportMissing("GL", "GL_ARB_vertex_type_2_10_10_10_rev");
    }

    private static boolean check_ARB_viewport_array(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_ARB_viewport_array")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        848, 849, 850, 851, 852, 853, 854, 855, 856, 857
                },
                "glViewportArrayv", "glViewportIndexedf", "glViewportIndexedfv", "glScissorArrayv", "glScissorIndexed", "glScissorIndexedv", "glDepthRangeArrayv",
                "glDepthRangeIndexed", "glGetFloati_v", "glGetDoublei_v"
        )) || reportMissing("GL", "GL_ARB_viewport_array");
    }

    private static boolean check_ARB_window_pos(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_ARB_window_pos")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1477, 1478, 1479, 1480, 1481, 1482, 1483, 1484, 1485, 1486, 1487, 1488, 1489, 1490, 1491, 1492
                },
                "glWindowPos2iARB", "glWindowPos2sARB", "glWindowPos2fARB", "glWindowPos2dARB", "glWindowPos2ivARB", "glWindowPos2svARB", "glWindowPos2fvARB",
                "glWindowPos2dvARB", "glWindowPos3iARB", "glWindowPos3sARB", "glWindowPos3fARB", "glWindowPos3dARB", "glWindowPos3ivARB", "glWindowPos3svARB",
                "glWindowPos3fvARB", "glWindowPos3dvARB"
        )) || reportMissing("GL", "GL_ARB_window_pos");
    }

    private static boolean check_EXT_bindable_uniform(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_EXT_bindable_uniform")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1493, 1494, 1495
                },
                "glUniformBufferEXT", "glGetUniformBufferSizeEXT", "glGetUniformOffsetEXT"
        )) || reportMissing("GL", "GL_EXT_bindable_uniform");
    }

    private static boolean check_EXT_blend_color(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_EXT_blend_color")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1496
                },
                "glBlendColorEXT"
        )) || reportMissing("GL", "GL_EXT_blend_color");
    }

    private static boolean check_EXT_blend_equation_separate(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_EXT_blend_equation_separate")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1497
                },
                "glBlendEquationSeparateEXT"
        )) || reportMissing("GL", "GL_EXT_blend_equation_separate");
    }

    private static boolean check_EXT_blend_func_separate(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_EXT_blend_func_separate")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1498
                },
                "glBlendFuncSeparateEXT"
        )) || reportMissing("GL", "GL_EXT_blend_func_separate");
    }

    private static boolean check_EXT_blend_minmax(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_EXT_blend_minmax")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1499
                },
                "glBlendEquationEXT"
        )) || reportMissing("GL", "GL_EXT_blend_minmax");
    }

    private static boolean check_EXT_compiled_vertex_array(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_EXT_compiled_vertex_array")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1500, 1501
                },
                "glLockArraysEXT", "glUnlockArraysEXT"
        )) || reportMissing("GL", "GL_EXT_compiled_vertex_array");
    }

    private static boolean check_EXT_debug_label(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_EXT_debug_label")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1502, 1503
                },
                "glLabelObjectEXT", "glGetObjectLabelEXT"
        )) || reportMissing("GL", "GL_EXT_debug_label");
    }

    private static boolean check_EXT_debug_marker(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_EXT_debug_marker")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1504, 1505, 1506
                },
                "glInsertEventMarkerEXT", "glPushGroupMarkerEXT", "glPopGroupMarkerEXT"
        )) || reportMissing("GL", "GL_EXT_debug_marker");
    }

    private static boolean check_EXT_depth_bounds_test(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_EXT_depth_bounds_test")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1507
                },
                "glDepthBoundsEXT"
        )) || reportMissing("GL", "GL_EXT_depth_bounds_test");
    }

    private static boolean check_EXT_direct_state_access(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_EXT_direct_state_access")) {
            return false;
        }

        int flag0 = ext.contains("OpenGL12") ? 0 : Integer.MIN_VALUE;
        int flag3 = ext.contains("OpenGL13") ? 0 : Integer.MIN_VALUE;
        int flag42 = ext.contains("OpenGL30") ? 0 : Integer.MIN_VALUE;
        int flag55 = ext.contains("GL_ARB_vertex_program") ? 0 : Integer.MIN_VALUE;
        int flag82 = ext.contains("OpenGL15") ? 0 : Integer.MIN_VALUE;
        int flag88 = ext.contains("OpenGL20") ? 0 : Integer.MIN_VALUE;
        int flag107 = ext.contains("OpenGL21") ? 0 : Integer.MIN_VALUE;
        int flag113 = ext.contains("GL_EXT_texture_buffer_object") ? 0 : Integer.MIN_VALUE;
        int flag115 = ext.contains("GL_EXT_texture_integer") ? 0 : Integer.MIN_VALUE;
        int flag123 = ext.contains("GL_EXT_gpu_shader4") ? 0 : Integer.MIN_VALUE;
        int flag131 = ext.contains("GL_EXT_gpu_program_parameters") ? 0 : Integer.MIN_VALUE;
        int flag132 = ext.contains("GL_NV_gpu_program4") ? 0 : Integer.MIN_VALUE;
        int flag143 = ext.contains("GL_NV_framebuffer_multisample_coverage") ? 0 : Integer.MIN_VALUE;
        int flag157 = ext.contains("GL_EXT_geometry_shader4") || ext.contains("GL_NV_gpu_program4") ? 0 : Integer.MIN_VALUE;
        int flag160 = ext.contains("GL_NV_explicit_multisample") ? 0 : Integer.MIN_VALUE;

        return (checkFunctions(provider, caps, new int[] {
                        1508, 1509, 1510, 1511, 1512, 1513, 1514, 1515, 1516, 1517, 1518, 1519, 1520, 1521, 1522, 1523, 1524, 1525, 1526, 1527, 1528, 1529, 1530, 1531,
                        1532, 1533, 1534, 1535, 1536, 1537, 1538, 1539, 1540, 1541, flag0 + 1542, flag0 + 1543, flag0 + 1544, flag3 + 1545, flag3 + 1546, flag3 + 1547,
                        flag3 + 1548, flag3 + 1549, flag3 + 1550, flag3 + 1551, flag3 + 1552, flag3 + 1553, flag3 + 1554, flag3 + 1555, flag3 + 1556, flag3 + 1557,
                        flag3 + 1558, flag3 + 1559, flag3 + 1560, flag3 + 1561, flag3 + 1562, flag3 + 1563, flag3 + 1564, flag3 + 1565, flag3 + 1566, flag3 + 1567,
                        flag3 + 1568, flag3 + 1569, flag3 + 1570, flag3 + 1571, flag3 + 1572, flag3 + 1573, flag3 + 1574, flag3 + 1575, flag3 + 1576, flag3 + 1577,
                        flag3 + 1578, flag3 + 1579, flag3 + 1580, flag3 + 1581, flag3 + 1582, flag3 + 1583, flag3 + 1586, flag3 + 1587, flag3 + 1588, flag3 + 1592,
                        flag3 + 1593, flag3 + 1594, flag3 + 1595, flag3 + 1596, flag55 + 1597, flag55 + 1598, flag55 + 1599, flag55 + 1600, flag55 + 1601, flag55 + 1602,
                        flag55 + 1603, flag55 + 1604, flag55 + 1605, flag3 + 1606, flag3 + 1607, flag3 + 1608, flag3 + 1609, flag3 + 1610, flag3 + 1611, flag3 + 1612,
                        flag3 + 1613, flag3 + 1614, flag3 + 1615, flag3 + 1616, flag3 + 1617, flag3 + 1618, flag3 + 1619, flag3 + 1620, flag3 + 1621, flag3 + 1622,
                        flag3 + 1623, flag82 + 1624, flag82 + 1625, flag82 + 1626, flag82 + 1627, flag82 + 1628, flag82 + 1629, flag88 + 1630, flag88 + 1631, flag88 + 1632,
                        flag88 + 1633, flag88 + 1634, flag88 + 1635, flag88 + 1636, flag88 + 1637, flag88 + 1638, flag88 + 1639, flag88 + 1640, flag88 + 1641,
                        flag88 + 1642, flag88 + 1643, flag88 + 1644, flag88 + 1645, flag88 + 1646, flag88 + 1647, flag88 + 1648, flag107 + 1649, flag107 + 1650,
                        flag107 + 1651, flag107 + 1652, flag107 + 1653, flag107 + 1654, flag113 + 1655, flag113 + 1656, flag115 + 1657, flag115 + 1658, flag115 + 1659,
                        flag115 + 1660, flag115 + 1661, flag115 + 1662, flag115 + 1663, flag115 + 1664, flag123 + 1665, flag123 + 1666, flag123 + 1667, flag123 + 1668,
                        flag123 + 1669, flag123 + 1670, flag123 + 1671, flag123 + 1672, flag131 + 1673, flag132 + 1674, flag132 + 1675, flag132 + 1676, flag132 + 1677,
                        flag132 + 1678, flag132 + 1679, flag132 + 1680, flag132 + 1681, flag42 + 1682, flag42 + 1683, flag42 + 1684, flag143 + 1685, flag42 + 1686,
                        flag42 + 1687, flag42 + 1688, flag42 + 1689, flag42 + 1690, flag42 + 1691, flag42 + 1692, flag42 + 1693, flag42 + 1694, flag42 + 1695,
                        flag42 + 1696, flag42 + 1697, flag42 + 1698, flag157 + 1699, flag157 + 1700, flag157 + 1701, flag160 + 1702, flag160 + 1703, flag42 + 1704,
                        flag42 + 1705, flag42 + 1706, flag42 + 1707, flag42 + 1708, flag42 + 1709, flag42 + 1710, flag42 + 1711, flag42 + 1712, flag42 + 1713,
                        flag42 + 1714, flag42 + 1715, flag42 + 1716, flag42 + 1717, flag42 + 1718, flag42 + 1719, flag42 + 1720, flag42 + 1721, flag42 + 1722,
                        flag42 + 1723, flag42 + 1724
                },
                "glClientAttribDefaultEXT", "glPushClientAttribDefaultEXT", "glMatrixLoadfEXT", "glMatrixLoaddEXT", "glMatrixMultfEXT", "glMatrixMultdEXT",
                "glMatrixLoadIdentityEXT", "glMatrixRotatefEXT", "glMatrixRotatedEXT", "glMatrixScalefEXT", "glMatrixScaledEXT", "glMatrixTranslatefEXT",
                "glMatrixTranslatedEXT", "glMatrixOrthoEXT", "glMatrixFrustumEXT", "glMatrixPushEXT", "glMatrixPopEXT", "glTextureParameteriEXT",
                "glTextureParameterivEXT", "glTextureParameterfEXT", "glTextureParameterfvEXT", "glTextureImage1DEXT", "glTextureImage2DEXT",
                "glTextureSubImage1DEXT", "glTextureSubImage2DEXT", "glCopyTextureImage1DEXT", "glCopyTextureImage2DEXT", "glCopyTextureSubImage1DEXT",
                "glCopyTextureSubImage2DEXT", "glGetTextureImageEXT", "glGetTextureParameterfvEXT", "glGetTextureParameterivEXT", "glGetTextureLevelParameterfvEXT",
                "glGetTextureLevelParameterivEXT", "glTextureImage3DEXT", "glTextureSubImage3DEXT", "glCopyTextureSubImage3DEXT", "glBindMultiTextureEXT",
                "glMultiTexCoordPointerEXT", "glMultiTexEnvfEXT", "glMultiTexEnvfvEXT", "glMultiTexEnviEXT", "glMultiTexEnvivEXT", "glMultiTexGendEXT",
                "glMultiTexGendvEXT", "glMultiTexGenfEXT", "glMultiTexGenfvEXT", "glMultiTexGeniEXT", "glMultiTexGenivEXT", "glGetMultiTexEnvfvEXT",
                "glGetMultiTexEnvivEXT", "glGetMultiTexGendvEXT", "glGetMultiTexGenfvEXT", "glGetMultiTexGenivEXT", "glMultiTexParameteriEXT",
                "glMultiTexParameterivEXT", "glMultiTexParameterfEXT", "glMultiTexParameterfvEXT", "glMultiTexImage1DEXT", "glMultiTexImage2DEXT",
                "glMultiTexSubImage1DEXT", "glMultiTexSubImage2DEXT", "glCopyMultiTexImage1DEXT", "glCopyMultiTexImage2DEXT", "glCopyMultiTexSubImage1DEXT",
                "glCopyMultiTexSubImage2DEXT", "glGetMultiTexImageEXT", "glGetMultiTexParameterfvEXT", "glGetMultiTexParameterivEXT",
                "glGetMultiTexLevelParameterfvEXT", "glGetMultiTexLevelParameterivEXT", "glMultiTexImage3DEXT", "glMultiTexSubImage3DEXT",
                "glCopyMultiTexSubImage3DEXT", "glEnableClientStateIndexedEXT", "glDisableClientStateIndexedEXT", "glGetFloatIndexedvEXT", "glGetDoubleIndexedvEXT",
                "glGetPointerIndexedvEXT", "glEnableIndexedEXT", "glDisableIndexedEXT", "glIsEnabledIndexedEXT", "glGetIntegerIndexedvEXT",
                "glGetBooleanIndexedvEXT", "glNamedProgramStringEXT", "glNamedProgramLocalParameter4dEXT", "glNamedProgramLocalParameter4dvEXT",
                "glNamedProgramLocalParameter4fEXT", "glNamedProgramLocalParameter4fvEXT", "glGetNamedProgramLocalParameterdvEXT",
                "glGetNamedProgramLocalParameterfvEXT", "glGetNamedProgramivEXT", "glGetNamedProgramStringEXT", "glCompressedTextureImage3DEXT",
                "glCompressedTextureImage2DEXT", "glCompressedTextureImage1DEXT", "glCompressedTextureSubImage3DEXT", "glCompressedTextureSubImage2DEXT",
                "glCompressedTextureSubImage1DEXT", "glGetCompressedTextureImageEXT", "glCompressedMultiTexImage3DEXT", "glCompressedMultiTexImage2DEXT",
                "glCompressedMultiTexImage1DEXT", "glCompressedMultiTexSubImage3DEXT", "glCompressedMultiTexSubImage2DEXT", "glCompressedMultiTexSubImage1DEXT",
                "glGetCompressedMultiTexImageEXT", "glMatrixLoadTransposefEXT", "glMatrixLoadTransposedEXT", "glMatrixMultTransposefEXT",
                "glMatrixMultTransposedEXT", "glNamedBufferDataEXT", "glNamedBufferSubDataEXT", "glMapNamedBufferEXT", "glUnmapNamedBufferEXT",
                "glGetNamedBufferParameterivEXT", "glGetNamedBufferSubDataEXT", "glProgramUniform1fEXT", "glProgramUniform2fEXT", "glProgramUniform3fEXT",
                "glProgramUniform4fEXT", "glProgramUniform1iEXT", "glProgramUniform2iEXT", "glProgramUniform3iEXT", "glProgramUniform4iEXT",
                "glProgramUniform1fvEXT", "glProgramUniform2fvEXT", "glProgramUniform3fvEXT", "glProgramUniform4fvEXT", "glProgramUniform1ivEXT",
                "glProgramUniform2ivEXT", "glProgramUniform3ivEXT", "glProgramUniform4ivEXT", "glProgramUniformMatrix2fvEXT", "glProgramUniformMatrix3fvEXT",
                "glProgramUniformMatrix4fvEXT", "glProgramUniformMatrix2x3fvEXT", "glProgramUniformMatrix3x2fvEXT", "glProgramUniformMatrix2x4fvEXT",
                "glProgramUniformMatrix4x2fvEXT", "glProgramUniformMatrix3x4fvEXT", "glProgramUniformMatrix4x3fvEXT", "glTextureBufferEXT", "glMultiTexBufferEXT",
                "glTextureParameterIivEXT", "glTextureParameterIuivEXT", "glGetTextureParameterIivEXT", "glGetTextureParameterIuivEXT", "glMultiTexParameterIivEXT",
                "glMultiTexParameterIuivEXT", "glGetMultiTexParameterIivEXT", "glGetMultiTexParameterIuivEXT", "glProgramUniform1uiEXT", "glProgramUniform2uiEXT",
                "glProgramUniform3uiEXT", "glProgramUniform4uiEXT", "glProgramUniform1uivEXT", "glProgramUniform2uivEXT", "glProgramUniform3uivEXT",
                "glProgramUniform4uivEXT", "glNamedProgramLocalParameters4fvEXT", "glNamedProgramLocalParameterI4iEXT", "glNamedProgramLocalParameterI4ivEXT",
                "glNamedProgramLocalParametersI4ivEXT", "glNamedProgramLocalParameterI4uiEXT", "glNamedProgramLocalParameterI4uivEXT",
                "glNamedProgramLocalParametersI4uivEXT", "glGetNamedProgramLocalParameterIivEXT", "glGetNamedProgramLocalParameterIuivEXT",
                "glNamedRenderbufferStorageEXT", "glGetNamedRenderbufferParameterivEXT", "glNamedRenderbufferStorageMultisampleEXT",
                "glNamedRenderbufferStorageMultisampleCoverageEXT", "glCheckNamedFramebufferStatusEXT", "glNamedFramebufferTexture1DEXT",
                "glNamedFramebufferTexture2DEXT", "glNamedFramebufferTexture3DEXT", "glNamedFramebufferRenderbufferEXT",
                "glGetNamedFramebufferAttachmentParameterivEXT", "glGenerateTextureMipmapEXT", "glGenerateMultiTexMipmapEXT", "glFramebufferDrawBufferEXT",
                "glFramebufferDrawBuffersEXT", "glFramebufferReadBufferEXT", "glGetFramebufferParameterivEXT", "glNamedCopyBufferSubDataEXT",
                "glNamedFramebufferTextureEXT", "glNamedFramebufferTextureLayerEXT", "glNamedFramebufferTextureFaceEXT", "glTextureRenderbufferEXT",
                "glMultiTexRenderbufferEXT", "glVertexArrayVertexOffsetEXT", "glVertexArrayColorOffsetEXT", "glVertexArrayEdgeFlagOffsetEXT",
                "glVertexArrayIndexOffsetEXT", "glVertexArrayNormalOffsetEXT", "glVertexArrayTexCoordOffsetEXT", "glVertexArrayMultiTexCoordOffsetEXT",
                "glVertexArrayFogCoordOffsetEXT", "glVertexArraySecondaryColorOffsetEXT", "glVertexArrayVertexAttribOffsetEXT",
                "glVertexArrayVertexAttribIOffsetEXT", "glEnableVertexArrayEXT", "glDisableVertexArrayEXT", "glEnableVertexArrayAttribEXT",
                "glDisableVertexArrayAttribEXT", "glGetVertexArrayIntegervEXT", "glGetVertexArrayPointervEXT", "glGetVertexArrayIntegeri_vEXT",
                "glGetVertexArrayPointeri_vEXT", "glMapNamedBufferRangeEXT", "glFlushMappedNamedBufferRangeEXT"
        )) || reportMissing("GL", "GL_EXT_direct_state_access");
    }

    private static boolean check_EXT_draw_buffers2(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_EXT_draw_buffers2")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1725, 1596, 1595, 1592, 1593, 1594
                },
                "glColorMaskIndexedEXT", "glGetBooleanIndexedvEXT", "glGetIntegerIndexedvEXT", "glEnableIndexedEXT", "glDisableIndexedEXT", "glIsEnabledIndexedEXT"
        )) || reportMissing("GL", "GL_EXT_draw_buffers2");
    }

    private static boolean check_EXT_draw_instanced(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_EXT_draw_instanced")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1726, 1727
                },
                "glDrawArraysInstancedEXT", "glDrawElementsInstancedEXT"
        )) || reportMissing("GL", "GL_EXT_draw_instanced");
    }

    private static boolean check_EXT_EGL_image_storage(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_EXT_EGL_image_storage")) {
            return false;
        }

        int flag0 = hasDSA(ext) ? 0 : Integer.MIN_VALUE;

        return (checkFunctions(provider, caps, new int[] {
                        1728, flag0 + 1729
                },
                "glEGLImageTargetTexStorageEXT", "glEGLImageTargetTextureStorageEXT"
        )) || reportMissing("GL", "GL_EXT_EGL_image_storage");
    }

    private static boolean check_EXT_external_buffer(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_EXT_external_buffer")) {
            return false;
        }

        int flag0 = hasDSA(ext) ? 0 : Integer.MIN_VALUE;

        return (checkFunctions(provider, caps, new int[] {
                        1730, flag0 + 1731
                },
                "glBufferStorageExternalEXT", "glNamedBufferStorageExternalEXT"
        )) || reportMissing("GL", "GL_EXT_external_buffer");
    }

    private static boolean check_EXT_framebuffer_blit(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_EXT_framebuffer_blit")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1732
                },
                "glBlitFramebufferEXT"
        )) || reportMissing("GL", "GL_EXT_framebuffer_blit");
    }

    private static boolean check_EXT_framebuffer_multisample(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_EXT_framebuffer_multisample")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1733
                },
                "glRenderbufferStorageMultisampleEXT"
        )) || reportMissing("GL", "GL_EXT_framebuffer_multisample");
    }

    private static boolean check_EXT_framebuffer_object(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_EXT_framebuffer_object")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1734, 1735, 1736, 1737, 1738, 1739, 1740, 1741, 1742, 1743, 1744, 1745, 1746, 1747, 1748, 1749, 1750
                },
                "glIsRenderbufferEXT", "glBindRenderbufferEXT", "glDeleteRenderbuffersEXT", "glGenRenderbuffersEXT", "glRenderbufferStorageEXT",
                "glGetRenderbufferParameterivEXT", "glIsFramebufferEXT", "glBindFramebufferEXT", "glDeleteFramebuffersEXT", "glGenFramebuffersEXT",
                "glCheckFramebufferStatusEXT", "glFramebufferTexture1DEXT", "glFramebufferTexture2DEXT", "glFramebufferTexture3DEXT",
                "glFramebufferRenderbufferEXT", "glGetFramebufferAttachmentParameterivEXT", "glGenerateMipmapEXT"
        )) || reportMissing("GL", "GL_EXT_framebuffer_object");
    }

    private static boolean check_EXT_geometry_shader4(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_EXT_geometry_shader4")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1751, 1752, 1753, 1754
                },
                "glProgramParameteriEXT", "glFramebufferTextureEXT", "glFramebufferTextureLayerEXT", "glFramebufferTextureFaceEXT"
        )) || reportMissing("GL", "GL_EXT_geometry_shader4");
    }

    private static boolean check_EXT_gpu_program_parameters(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_EXT_gpu_program_parameters")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1755, 1756
                },
                "glProgramEnvParameters4fvEXT", "glProgramLocalParameters4fvEXT"
        )) || reportMissing("GL", "GL_EXT_gpu_program_parameters");
    }

    private static boolean check_EXT_gpu_shader4(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_EXT_gpu_shader4")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1757, 1758, 1759, 1760, 1761, 1762, 1763, 1764, 1765, 1766, 1767, 1768, 1769, 1770, 1771, 1772, 1773, 1774, 1775, 1776, 1777, 1778, 1779, 1780,
                        1781, 1782, 1783, 1784, 1785, 1786, 1787, 1788, 1789, 1790
                },
                "glVertexAttribI1iEXT", "glVertexAttribI2iEXT", "glVertexAttribI3iEXT", "glVertexAttribI4iEXT", "glVertexAttribI1uiEXT", "glVertexAttribI2uiEXT",
                "glVertexAttribI3uiEXT", "glVertexAttribI4uiEXT", "glVertexAttribI1ivEXT", "glVertexAttribI2ivEXT", "glVertexAttribI3ivEXT",
                "glVertexAttribI4ivEXT", "glVertexAttribI1uivEXT", "glVertexAttribI2uivEXT", "glVertexAttribI3uivEXT", "glVertexAttribI4uivEXT",
                "glVertexAttribI4bvEXT", "glVertexAttribI4svEXT", "glVertexAttribI4ubvEXT", "glVertexAttribI4usvEXT", "glVertexAttribIPointerEXT",
                "glGetVertexAttribIivEXT", "glGetVertexAttribIuivEXT", "glGetUniformuivEXT", "glBindFragDataLocationEXT", "glGetFragDataLocationEXT",
                "glUniform1uiEXT", "glUniform2uiEXT", "glUniform3uiEXT", "glUniform4uiEXT", "glUniform1uivEXT", "glUniform2uivEXT", "glUniform3uivEXT",
                "glUniform4uivEXT"
        )) || reportMissing("GL", "GL_EXT_gpu_shader4");
    }

    private static boolean check_EXT_memory_object(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_EXT_memory_object")) {
            return false;
        }

        int flag0 = hasDSA(ext) ? 0 : Integer.MIN_VALUE;

        return (checkFunctions(provider, caps, new int[] {
                        1791, 1792, 1793, 1794, 1795, 1796, 1797, 1798, 1799, 1800, 1801, 1802, flag0 + 1803, flag0 + 1804, flag0 + 1805, flag0 + 1806, flag0 + 1807, 1808,
                        flag0 + 1809
                },
                "glGetUnsignedBytevEXT", "glGetUnsignedBytei_vEXT", "glDeleteMemoryObjectsEXT", "glIsMemoryObjectEXT", "glCreateMemoryObjectsEXT",
                "glMemoryObjectParameterivEXT", "glGetMemoryObjectParameterivEXT", "glTexStorageMem2DEXT", "glTexStorageMem2DMultisampleEXT",
                "glTexStorageMem3DEXT", "glTexStorageMem3DMultisampleEXT", "glBufferStorageMemEXT", "glTextureStorageMem2DEXT",
                "glTextureStorageMem2DMultisampleEXT", "glTextureStorageMem3DEXT", "glTextureStorageMem3DMultisampleEXT", "glNamedBufferStorageMemEXT",
                "glTexStorageMem1DEXT", "glTextureStorageMem1DEXT"
        )) || reportMissing("GL", "GL_EXT_memory_object");
    }

    private static boolean check_EXT_memory_object_fd(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_EXT_memory_object_fd")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1810
                },
                "glImportMemoryFdEXT"
        )) || reportMissing("GL", "GL_EXT_memory_object_fd");
    }

    private static boolean check_EXT_memory_object_win32(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_EXT_memory_object_win32")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1811, 1812
                },
                "glImportMemoryWin32HandleEXT", "glImportMemoryWin32NameEXT"
        )) || reportMissing("GL", "GL_EXT_memory_object_win32");
    }

    private static boolean check_EXT_point_parameters(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_EXT_point_parameters")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1813, 1814
                },
                "glPointParameterfEXT", "glPointParameterfvEXT"
        )) || reportMissing("GL", "GL_EXT_point_parameters");
    }

    private static boolean check_EXT_polygon_offset_clamp(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_EXT_polygon_offset_clamp")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1815
                },
                "glPolygonOffsetClampEXT"
        )) || reportMissing("GL", "GL_EXT_polygon_offset_clamp");
    }

    private static boolean check_EXT_provoking_vertex(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_EXT_provoking_vertex")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1816
                },
                "glProvokingVertexEXT"
        )) || reportMissing("GL", "GL_EXT_provoking_vertex");
    }

    private static boolean check_EXT_raster_multisample(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_EXT_raster_multisample")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1817
                },
                "glRasterSamplesEXT"
        )) || reportMissing("GL", "GL_EXT_raster_multisample");
    }

    private static boolean check_EXT_secondary_color(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_EXT_secondary_color")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1818, 1819, 1820, 1821, 1822, 1823, 1824, 1825, 1826, 1827, 1828, 1829, 1830, 1831, 1832, 1833, 1834
                },
                "glSecondaryColor3bEXT", "glSecondaryColor3sEXT", "glSecondaryColor3iEXT", "glSecondaryColor3fEXT", "glSecondaryColor3dEXT",
                "glSecondaryColor3ubEXT", "glSecondaryColor3usEXT", "glSecondaryColor3uiEXT", "glSecondaryColor3bvEXT", "glSecondaryColor3svEXT",
                "glSecondaryColor3ivEXT", "glSecondaryColor3fvEXT", "glSecondaryColor3dvEXT", "glSecondaryColor3ubvEXT", "glSecondaryColor3usvEXT",
                "glSecondaryColor3uivEXT", "glSecondaryColorPointerEXT"
        )) || reportMissing("GL", "GL_EXT_secondary_color");
    }

    private static boolean check_EXT_semaphore(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_EXT_semaphore")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1791, 1792, 1835, 1836, 1837, 1838, 1839, 1840, 1841
                },
                "glGetUnsignedBytevEXT", "glGetUnsignedBytei_vEXT", "glGenSemaphoresEXT", "glDeleteSemaphoresEXT", "glIsSemaphoreEXT",
                "glSemaphoreParameterui64vEXT", "glGetSemaphoreParameterui64vEXT", "glWaitSemaphoreEXT", "glSignalSemaphoreEXT"
        )) || reportMissing("GL", "GL_EXT_semaphore");
    }

    private static boolean check_EXT_semaphore_fd(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_EXT_semaphore_fd")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1842
                },
                "glImportSemaphoreFdEXT"
        )) || reportMissing("GL", "GL_EXT_semaphore_fd");
    }

    private static boolean check_EXT_semaphore_win32(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_EXT_semaphore_win32")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1843, 1844
                },
                "glImportSemaphoreWin32HandleEXT", "glImportSemaphoreWin32NameEXT"
        )) || reportMissing("GL", "GL_EXT_semaphore_win32");
    }

    private static boolean check_EXT_separate_shader_objects(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_EXT_separate_shader_objects")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1845, 1846, 1847
                },
                "glUseShaderProgramEXT", "glActiveProgramEXT", "glCreateShaderProgramEXT"
        )) || reportMissing("GL", "GL_EXT_separate_shader_objects");
    }

    private static boolean check_EXT_shader_framebuffer_fetch_non_coherent(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_EXT_shader_framebuffer_fetch_non_coherent")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1848
                },
                "glFramebufferFetchBarrierEXT"
        )) || reportMissing("GL", "GL_EXT_shader_framebuffer_fetch_non_coherent");
    }

    private static boolean check_EXT_shader_image_load_store(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_EXT_shader_image_load_store")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1849, 1850
                },
                "glBindImageTextureEXT", "glMemoryBarrierEXT"
        )) || reportMissing("GL", "GL_EXT_shader_image_load_store");
    }

    private static boolean check_EXT_stencil_clear_tag(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_EXT_stencil_clear_tag")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1851
                },
                "glStencilClearTagEXT"
        )) || reportMissing("GL", "GL_EXT_stencil_clear_tag");
    }

    private static boolean check_EXT_stencil_two_side(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_EXT_stencil_two_side")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1852
                },
                "glActiveStencilFaceEXT"
        )) || reportMissing("GL", "GL_EXT_stencil_two_side");
    }

    private static boolean check_EXT_texture_array(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_EXT_texture_array")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1753
                },
                "glFramebufferTextureLayerEXT"
        )) || reportMissing("GL", "GL_EXT_texture_array");
    }

    private static boolean check_EXT_texture_buffer_object(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_EXT_texture_buffer_object")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1853
                },
                "glTexBufferEXT"
        )) || reportMissing("GL", "GL_EXT_texture_buffer_object");
    }

    private static boolean check_EXT_texture_integer(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_EXT_texture_integer")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1854, 1855, 1856, 1857, 1858, 1859
                },
                "glClearColorIiEXT", "glClearColorIuiEXT", "glTexParameterIivEXT", "glTexParameterIuivEXT", "glGetTexParameterIivEXT", "glGetTexParameterIuivEXT"
        )) || reportMissing("GL", "GL_EXT_texture_integer");
    }

    private static boolean check_EXT_texture_storage(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_EXT_texture_storage")) {
            return false;
        }

        int flag0 = hasDSA(ext) ? 0 : Integer.MIN_VALUE;

        return (checkFunctions(provider, caps, new int[] {
                        1860, 1861, 1862, flag0 + 1375, flag0 + 1376, flag0 + 1377
                },
                "glTexStorage1DEXT", "glTexStorage2DEXT", "glTexStorage3DEXT", "glTextureStorage1DEXT", "glTextureStorage2DEXT", "glTextureStorage3DEXT"
        )) || reportMissing("GL", "GL_EXT_texture_storage");
    }

    private static boolean check_EXT_timer_query(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_EXT_timer_query")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1863, 1864
                },
                "glGetQueryObjecti64vEXT", "glGetQueryObjectui64vEXT"
        )) || reportMissing("GL", "GL_EXT_timer_query");
    }

    private static boolean check_EXT_transform_feedback(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_EXT_transform_feedback")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1865, 1866, 1867, 1868, 1869, 1870, 1871, 1595, 1596
                },
                "glBindBufferRangeEXT", "glBindBufferOffsetEXT", "glBindBufferBaseEXT", "glBeginTransformFeedbackEXT", "glEndTransformFeedbackEXT",
                "glTransformFeedbackVaryingsEXT", "glGetTransformFeedbackVaryingEXT", "glGetIntegerIndexedvEXT", "glGetBooleanIndexedvEXT"
        )) || reportMissing("GL", "GL_EXT_transform_feedback");
    }

    private static boolean check_EXT_vertex_attrib_64bit(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_EXT_vertex_attrib_64bit")) {
            return false;
        }

        int flag0 = ext.contains("GL_EXT_direct_state_access") ? 0 : Integer.MIN_VALUE;

        return (checkFunctions(provider, caps, new int[] {
                        1872, 1873, 1874, 1875, 1876, 1877, 1878, 1879, 1880, 1881, flag0 + 1384
                },
                "glVertexAttribL1dEXT", "glVertexAttribL2dEXT", "glVertexAttribL3dEXT", "glVertexAttribL4dEXT", "glVertexAttribL1dvEXT", "glVertexAttribL2dvEXT",
                "glVertexAttribL3dvEXT", "glVertexAttribL4dvEXT", "glVertexAttribLPointerEXT", "glGetVertexAttribLdvEXT", "glVertexArrayVertexAttribLOffsetEXT"
        )) || reportMissing("GL", "GL_EXT_vertex_attrib_64bit");
    }

    private static boolean check_EXT_win32_keyed_mutex(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_EXT_win32_keyed_mutex")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1882, 1883
                },
                "glAcquireKeyedMutexWin32EXT", "glReleaseKeyedMutexWin32EXT"
        )) || reportMissing("GL", "GL_EXT_win32_keyed_mutex");
    }

    private static boolean check_EXT_window_rectangles(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_EXT_window_rectangles")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1884
                },
                "glWindowRectanglesEXT"
        )) || reportMissing("GL", "GL_EXT_window_rectangles");
    }

    private static boolean check_EXT_x11_sync_object(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_EXT_x11_sync_object")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1885
                },
                "glImportSyncEXT"
        )) || reportMissing("GL", "GL_EXT_x11_sync_object");
    }

    private static boolean check_GREMEDY_frame_terminator(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_GREMEDY_frame_terminator")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1886
                },
                "glFrameTerminatorGREMEDY"
        )) || reportMissing("GL", "GL_GREMEDY_frame_terminator");
    }

    private static boolean check_GREMEDY_string_marker(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_GREMEDY_string_marker")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1887
                },
                "glStringMarkerGREMEDY"
        )) || reportMissing("GL", "GL_GREMEDY_string_marker");
    }

    private static boolean check_INTEL_framebuffer_CMAA(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_INTEL_framebuffer_CMAA")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1888
                },
                "glApplyFramebufferAttachmentCMAAINTEL"
        )) || reportMissing("GL", "GL_INTEL_framebuffer_CMAA");
    }

    private static boolean check_INTEL_map_texture(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_INTEL_map_texture")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1889, 1890, 1891
                },
                "glSyncTextureINTEL", "glUnmapTexture2DINTEL", "glMapTexture2DINTEL"
        )) || reportMissing("GL", "GL_INTEL_map_texture");
    }

    private static boolean check_INTEL_performance_query(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_INTEL_performance_query")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1892, 1893, 1894, 1895, 1896, 1897, 1898, 1899, 1900, 1901
                },
                "glBeginPerfQueryINTEL", "glCreatePerfQueryINTEL", "glDeletePerfQueryINTEL", "glEndPerfQueryINTEL", "glGetFirstPerfQueryIdINTEL",
                "glGetNextPerfQueryIdINTEL", "glGetPerfCounterInfoINTEL", "glGetPerfQueryDataINTEL", "glGetPerfQueryIdByNameINTEL", "glGetPerfQueryInfoINTEL"
        )) || reportMissing("GL", "GL_INTEL_performance_query");
    }

    private static boolean check_KHR_blend_equation_advanced(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_KHR_blend_equation_advanced")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1902
                },
                "glBlendBarrierKHR"
        )) || reportMissing("GL", "GL_KHR_blend_equation_advanced");
    }

    private static boolean check_KHR_debug(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_KHR_debug")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        875, 876, 877, 878, 879, 880, 881, 882, 883, 884
                },
                "glDebugMessageControl", "glDebugMessageInsert", "glDebugMessageCallback", "glGetDebugMessageLog", "glPushDebugGroup", "glPopDebugGroup",
                "glObjectLabel", "glGetObjectLabel", "glObjectPtrLabel", "glGetObjectPtrLabel"
        )) || reportMissing("GL", "GL_KHR_debug");
    }

    private static boolean check_KHR_parallel_shader_compile(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_KHR_parallel_shader_compile")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1903
                },
                "glMaxShaderCompilerThreadsKHR"
        )) || reportMissing("GL", "GL_KHR_parallel_shader_compile");
    }

    private static boolean check_KHR_robustness(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_KHR_robustness")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1024, 1033, 1040, 1042, 1043
                },
                "glGetGraphicsResetStatus", "glReadnPixels", "glGetnUniformfv", "glGetnUniformiv", "glGetnUniformuiv"
        )) || reportMissing("GL", "GL_KHR_robustness");
    }

    private static boolean check_MESA_framebuffer_flip_y(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_MESA_framebuffer_flip_y")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1904, 1905
                },
                "glFramebufferParameteriMESA", "glGetFramebufferParameterivMESA"
        )) || reportMissing("GL", "GL_MESA_framebuffer_flip_y");
    }

    private static boolean check_NV_alpha_to_coverage_dither_control(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_NV_alpha_to_coverage_dither_control")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1906
                },
                "glAlphaToCoverageDitherControlNV"
        )) || reportMissing("GL", "GL_NV_alpha_to_coverage_dither_control");
    }

    private static boolean check_NV_bindless_multi_draw_indirect(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_NV_bindless_multi_draw_indirect")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1907, 1908
                },
                "glMultiDrawArraysIndirectBindlessNV", "glMultiDrawElementsIndirectBindlessNV"
        )) || reportMissing("GL", "GL_NV_bindless_multi_draw_indirect");
    }

    private static boolean check_NV_bindless_multi_draw_indirect_count(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_NV_bindless_multi_draw_indirect_count")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1909, 1910
                },
                "glMultiDrawArraysIndirectBindlessCountNV", "glMultiDrawElementsIndirectBindlessCountNV"
        )) || reportMissing("GL", "GL_NV_bindless_multi_draw_indirect_count");
    }

    private static boolean check_NV_bindless_texture(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_NV_bindless_texture")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1911, 1912, 1913, 1914, 1915, 1916, 1917, 1918, 1919, 1920, 1921, 1922, 1923
                },
                "glGetTextureHandleNV", "glGetTextureSamplerHandleNV", "glMakeTextureHandleResidentNV", "glMakeTextureHandleNonResidentNV", "glGetImageHandleNV",
                "glMakeImageHandleResidentNV", "glMakeImageHandleNonResidentNV", "glUniformHandleui64NV", "glUniformHandleui64vNV", "glProgramUniformHandleui64NV",
                "glProgramUniformHandleui64vNV", "glIsTextureHandleResidentNV", "glIsImageHandleResidentNV"
        )) || reportMissing("GL", "GL_NV_bindless_texture");
    }

    private static boolean check_NV_blend_equation_advanced(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_NV_blend_equation_advanced")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1924, 1925
                },
                "glBlendParameteriNV", "glBlendBarrierNV"
        )) || reportMissing("GL", "GL_NV_blend_equation_advanced");
    }

    private static boolean check_NV_clip_space_w_scaling(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_NV_clip_space_w_scaling")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1926
                },
                "glViewportPositionWScaleNV"
        )) || reportMissing("GL", "GL_NV_clip_space_w_scaling");
    }

    private static boolean check_NV_command_list(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_NV_command_list")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1927, 1928, 1929, 1930, 1931, 1932, 1933, 1934, 1935, 1936, 1937, 1938, 1939, 1940, 1941, 1942, 1943
                },
                "glCreateStatesNV", "glDeleteStatesNV", "glIsStateNV", "glStateCaptureNV", "glGetCommandHeaderNV", "glGetStageIndexNV", "glDrawCommandsNV",
                "glDrawCommandsAddressNV", "glDrawCommandsStatesNV", "glDrawCommandsStatesAddressNV", "glCreateCommandListsNV", "glDeleteCommandListsNV",
                "glIsCommandListNV", "glListDrawCommandsStatesClientNV", "glCommandListSegmentsNV", "glCompileCommandListNV", "glCallCommandListNV"
        )) || reportMissing("GL", "GL_NV_command_list");
    }

    private static boolean check_NV_conditional_render(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_NV_conditional_render")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1944, 1945
                },
                "glBeginConditionalRenderNV", "glEndConditionalRenderNV"
        )) || reportMissing("GL", "GL_NV_conditional_render");
    }

    private static boolean check_NV_conservative_raster(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_NV_conservative_raster")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1946
                },
                "glSubpixelPrecisionBiasNV"
        )) || reportMissing("GL", "GL_NV_conservative_raster");
    }

    private static boolean check_NV_conservative_raster_dilate(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_NV_conservative_raster_dilate")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1947
                },
                "glConservativeRasterParameterfNV"
        )) || reportMissing("GL", "GL_NV_conservative_raster_dilate");
    }

    private static boolean check_NV_conservative_raster_pre_snap_triangles(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_NV_conservative_raster_pre_snap_triangles")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1948
                },
                "glConservativeRasterParameteriNV"
        )) || reportMissing("GL", "GL_NV_conservative_raster_pre_snap_triangles");
    }

    private static boolean check_NV_copy_image(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_NV_copy_image")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1949
                },
                "glCopyImageSubDataNV"
        )) || reportMissing("GL", "GL_NV_copy_image");
    }

    private static boolean check_NV_depth_buffer_float(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_NV_depth_buffer_float")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1950, 1951, 1952
                },
                "glDepthRangedNV", "glClearDepthdNV", "glDepthBoundsdNV"
        )) || reportMissing("GL", "GL_NV_depth_buffer_float");
    }

    private static boolean check_NV_draw_texture(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_NV_draw_texture")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1953
                },
                "glDrawTextureNV"
        )) || reportMissing("GL", "GL_NV_draw_texture");
    }

    private static boolean check_NV_draw_vulkan_image(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_NV_draw_vulkan_image")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1954, 1955, 1956, 1957, 1958
                },
                "glDrawVkImageNV", "glGetVkProcAddrNV", "glWaitVkSemaphoreNV", "glSignalVkSemaphoreNV", "glSignalVkFenceNV"
        )) || reportMissing("GL", "GL_NV_draw_vulkan_image");
    }

    private static boolean check_NV_explicit_multisample(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_NV_explicit_multisample")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1959, 1960, 1961
                },
                "glGetMultisamplefvNV", "glSampleMaskIndexedNV", "glTexRenderbufferNV"
        )) || reportMissing("GL", "GL_NV_explicit_multisample");
    }

    private static boolean check_NV_fence(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_NV_fence")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1962, 1963, 1964, 1965, 1966, 1967, 1968
                },
                "glDeleteFencesNV", "glGenFencesNV", "glIsFenceNV", "glTestFenceNV", "glGetFenceivNV", "glFinishFenceNV", "glSetFenceNV"
        )) || reportMissing("GL", "GL_NV_fence");
    }

    private static boolean check_NV_fragment_coverage_to_color(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_NV_fragment_coverage_to_color")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1969
                },
                "glFragmentCoverageColorNV"
        )) || reportMissing("GL", "GL_NV_fragment_coverage_to_color");
    }

    private static boolean check_NV_framebuffer_mixed_samples(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_NV_framebuffer_mixed_samples")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1817, 1970, 1971, 1972
                },
                "glRasterSamplesEXT", "glCoverageModulationTableNV", "glGetCoverageModulationTableNV", "glCoverageModulationNV"
        )) || reportMissing("GL", "GL_NV_framebuffer_mixed_samples");
    }

    private static boolean check_NV_framebuffer_multisample_coverage(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_NV_framebuffer_multisample_coverage")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1973
                },
                "glRenderbufferStorageMultisampleCoverageNV"
        )) || reportMissing("GL", "GL_NV_framebuffer_multisample_coverage");
    }

    private static boolean check_NV_gpu_multicast(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_NV_gpu_multicast")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        1974, 1975, 1976, 1977, 1978, 1979, 1980, 1981, 1982, 1983, 1984, 1985
                },
                "glRenderGpuMaskNV", "glMulticastBufferSubDataNV", "glMulticastCopyBufferSubDataNV", "glMulticastCopyImageSubDataNV",
                "glMulticastBlitFramebufferNV", "glMulticastFramebufferSampleLocationsfvNV", "glMulticastBarrierNV", "glMulticastWaitSyncNV",
                "glMulticastGetQueryObjectivNV", "glMulticastGetQueryObjectuivNV", "glMulticastGetQueryObjecti64vNV", "glMulticastGetQueryObjectui64vNV"
        )) || reportMissing("GL", "GL_NV_gpu_multicast");
    }

    private static boolean check_NV_gpu_shader5(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_NV_gpu_shader5")) {
            return false;
        }

        int flag0 = ext.contains("GL_EXT_direct_state_access") ? 0 : Integer.MIN_VALUE;

        return (checkFunctions(provider, caps, new int[] {
                        1058, 1059, 1060, 1061, 1062, 1063, 1064, 1065, 1066, 1067, 1068, 1069, 1070, 1071, 1072, 1073, 1074, 1075, flag0 + 1076, flag0 + 1077,
                        flag0 + 1078, flag0 + 1079, flag0 + 1080, flag0 + 1081, flag0 + 1082, flag0 + 1083, flag0 + 1084, flag0 + 1085, flag0 + 1086, flag0 + 1087,
                        flag0 + 1088, flag0 + 1089, flag0 + 1090, flag0 + 1091
                },
                "glUniform1i64NV", "glUniform2i64NV", "glUniform3i64NV", "glUniform4i64NV", "glUniform1i64vNV", "glUniform2i64vNV", "glUniform3i64vNV",
                "glUniform4i64vNV", "glUniform1ui64NV", "glUniform2ui64NV", "glUniform3ui64NV", "glUniform4ui64NV", "glUniform1ui64vNV", "glUniform2ui64vNV",
                "glUniform3ui64vNV", "glUniform4ui64vNV", "glGetUniformi64vNV", "glGetUniformui64vNV", "glProgramUniform1i64NV", "glProgramUniform2i64NV",
                "glProgramUniform3i64NV", "glProgramUniform4i64NV", "glProgramUniform1i64vNV", "glProgramUniform2i64vNV", "glProgramUniform3i64vNV",
                "glProgramUniform4i64vNV", "glProgramUniform1ui64NV", "glProgramUniform2ui64NV", "glProgramUniform3ui64NV", "glProgramUniform4ui64NV",
                "glProgramUniform1ui64vNV", "glProgramUniform2ui64vNV", "glProgramUniform3ui64vNV", "glProgramUniform4ui64vNV"
        )) || reportMissing("GL", "GL_NV_gpu_shader5");
    }

    private static boolean check_NV_half_float(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_NV_half_float")) {
            return false;
        }

        int flag0 = ext.contains("GL_EXT_fog_coord") ? 0 : Integer.MIN_VALUE;
        int flag2 = ext.contains("GL_EXT_secondary_color") ? 0 : Integer.MIN_VALUE;
        int flag4 = ext.contains("GL_EXT_vertex_weighting") ? 0 : Integer.MIN_VALUE;
        int flag6 = ext.contains("GL_NV_vertex_program") ? 0 : Integer.MIN_VALUE;

        return (checkFunctions(provider, caps, new int[] {
                        1986, 1987, 1988, 1989, 1990, 1991, 1992, 1993, 1994, 1995, 1996, 1997, 1998, 1999, 2000, 2001, 2002, 2003, 2004, 2005, 2006, 2007, 2008, 2009,
                        2010, 2011, 2012, 2013, flag0 + 2014, flag0 + 2015, flag2 + 2016, flag2 + 2017, flag4 + 2018, flag4 + 2019, flag6 + 2020, flag6 + 2021,
                        flag6 + 2022, flag6 + 2023, flag6 + 2024, flag6 + 2025, flag6 + 2026, flag6 + 2027, flag6 + 2028, flag6 + 2029, flag6 + 2030, flag6 + 2031
                },
                "glVertex2hNV", "glVertex2hvNV", "glVertex3hNV", "glVertex3hvNV", "glVertex4hNV", "glVertex4hvNV", "glNormal3hNV", "glNormal3hvNV", "glColor3hNV",
                "glColor3hvNV", "glColor4hNV", "glColor4hvNV", "glTexCoord1hNV", "glTexCoord1hvNV", "glTexCoord2hNV", "glTexCoord2hvNV", "glTexCoord3hNV",
                "glTexCoord3hvNV", "glTexCoord4hNV", "glTexCoord4hvNV", "glMultiTexCoord1hNV", "glMultiTexCoord1hvNV", "glMultiTexCoord2hNV",
                "glMultiTexCoord2hvNV", "glMultiTexCoord3hNV", "glMultiTexCoord3hvNV", "glMultiTexCoord4hNV", "glMultiTexCoord4hvNV", "glFogCoordhNV",
                "glFogCoordhvNV", "glSecondaryColor3hNV", "glSecondaryColor3hvNV", "glVertexWeighthNV", "glVertexWeighthvNV", "glVertexAttrib1hNV",
                "glVertexAttrib1hvNV", "glVertexAttrib2hNV", "glVertexAttrib2hvNV", "glVertexAttrib3hNV", "glVertexAttrib3hvNV", "glVertexAttrib4hNV",
                "glVertexAttrib4hvNV", "glVertexAttribs1hvNV", "glVertexAttribs2hvNV", "glVertexAttribs3hvNV", "glVertexAttribs4hvNV"
        )) || reportMissing("GL", "GL_NV_half_float");
    }

    private static boolean check_NV_internalformat_sample_query(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_NV_internalformat_sample_query")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        2032
                },
                "glGetInternalformatSampleivNV"
        )) || reportMissing("GL", "GL_NV_internalformat_sample_query");
    }

    private static boolean check_NV_memory_attachment(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_NV_memory_attachment")) {
            return false;
        }

        int flag0 = hasDSA(ext) ? 0 : Integer.MIN_VALUE;

        return (checkFunctions(provider, caps, new int[] {
                        2033, 2034, 2035, 2036, flag0 + 2037, flag0 + 2038
                },
                "glGetMemoryObjectDetachedResourcesuivNV", "glResetMemoryObjectParameterNV", "glTexAttachMemoryNV", "glBufferAttachMemoryNV",
                "glTextureAttachMemoryNV", "glNamedBufferAttachMemoryNV"
        )) || reportMissing("GL", "GL_NV_memory_attachment");
    }

    private static boolean check_NV_memory_object_sparse(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_NV_memory_object_sparse")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        2039, 2040, 2041, 2042
                },
                "glBufferPageCommitmentMemNV", "glNamedBufferPageCommitmentMemNV", "glTexPageCommitmentMemNV", "glTexturePageCommitmentMemNV"
        )) || reportMissing("GL", "GL_NV_memory_object_sparse");
    }

    private static boolean check_NV_mesh_shader(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_NV_mesh_shader")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        2043, 2044, 2045, 2046
                },
                "glDrawMeshTasksNV", "glDrawMeshTasksIndirectNV", "glMultiDrawMeshTasksIndirectNV", "glMultiDrawMeshTasksIndirectCountNV"
        )) || reportMissing("GL", "GL_NV_mesh_shader");
    }

    private static boolean check_NV_path_rendering(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_NV_path_rendering")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        2047, 2048, 2049, 2050, 2051, 2052, 2053, 2056, 2058, 2059, 2060, 2061, 2062, 2063, 2064, 2065, 2066, 2067, 2068, 2069, 2070, 2071, 2072, 2073,
                        2074, 2078, 2079, 2080, 2081, 2088, 2089, 2090, 2091, 2092, 2093, 2094, 2095, 2100, 2101, 2102, 2103
                },
                "glPathCommandsNV", "glPathCoordsNV", "glPathSubCommandsNV", "glPathSubCoordsNV", "glPathStringNV", "glPathGlyphsNV", "glPathGlyphRangeNV",
                "glCopyPathNV", "glInterpolatePathsNV", "glTransformPathNV", "glPathParameterivNV", "glPathParameteriNV", "glPathParameterfvNV",
                "glPathParameterfNV", "glPathDashArrayNV", "glGenPathsNV", "glDeletePathsNV", "glIsPathNV", "glPathStencilFuncNV", "glPathStencilDepthOffsetNV",
                "glStencilFillPathNV", "glStencilStrokePathNV", "glStencilFillPathInstancedNV", "glStencilStrokePathInstancedNV", "glPathCoverDepthFuncNV",
                "glCoverFillPathNV", "glCoverStrokePathNV", "glCoverFillPathInstancedNV", "glCoverStrokePathInstancedNV", "glGetPathParameterivNV",
                "glGetPathParameterfvNV", "glGetPathCommandsNV", "glGetPathCoordsNV", "glGetPathDashArrayNV", "glGetPathMetricsNV", "glGetPathMetricRangeNV",
                "glGetPathSpacingNV", "glIsPointInFillPathNV", "glIsPointInStrokePathNV", "glGetPathLengthNV", "glPointAlongPathNV"
        )) || reportMissing("GL", "GL_NV_path_rendering");
    }

    private static boolean check_NV_pixel_data_range(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_NV_pixel_data_range")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        2111, 2112
                },
                "glPixelDataRangeNV", "glFlushPixelDataRangeNV"
        )) || reportMissing("GL", "GL_NV_pixel_data_range");
    }

    private static boolean check_NV_point_sprite(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_NV_point_sprite")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        2113, 2114
                },
                "glPointParameteriNV", "glPointParameterivNV"
        )) || reportMissing("GL", "GL_NV_point_sprite");
    }

    private static boolean check_NV_primitive_restart(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_NV_primitive_restart")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        2115, 2116
                },
                "glPrimitiveRestartNV", "glPrimitiveRestartIndexNV"
        )) || reportMissing("GL", "GL_NV_primitive_restart");
    }

    private static boolean check_NV_query_resource(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_NV_query_resource")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        2117
                },
                "glQueryResourceNV"
        )) || reportMissing("GL", "GL_NV_query_resource");
    }

    private static boolean check_NV_query_resource_tag(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_NV_query_resource_tag")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        2118, 2119, 2120
                },
                "glGenQueryResourceTagNV", "glDeleteQueryResourceTagNV", "glQueryResourceTagNV"
        )) || reportMissing("GL", "GL_NV_query_resource_tag");
    }

    private static boolean check_NV_sample_locations(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_NV_sample_locations")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        2121, 2122, 2123
                },
                "glFramebufferSampleLocationsfvNV", "glNamedFramebufferSampleLocationsfvNV", "glResolveDepthValuesNV"
        )) || reportMissing("GL", "GL_NV_sample_locations");
    }

    private static boolean check_NV_scissor_exclusive(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_NV_scissor_exclusive")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        2124, 2125
                },
                "glScissorExclusiveArrayvNV", "glScissorExclusiveNV"
        )) || reportMissing("GL", "GL_NV_scissor_exclusive");
    }

    private static boolean check_NV_shader_buffer_load(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_NV_shader_buffer_load")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        2126, 2127, 2128, 2129, 2130, 2131, 2132, 2133, 2134, 2135, 2136, 1075, 2137, 2138
                },
                "glMakeBufferResidentNV", "glMakeBufferNonResidentNV", "glIsBufferResidentNV", "glMakeNamedBufferResidentNV", "glMakeNamedBufferNonResidentNV",
                "glIsNamedBufferResidentNV", "glGetBufferParameterui64vNV", "glGetNamedBufferParameterui64vNV", "glGetIntegerui64vNV", "glUniformui64NV",
                "glUniformui64vNV", "glGetUniformui64vNV", "glProgramUniformui64NV", "glProgramUniformui64vNV"
        )) || reportMissing("GL", "GL_NV_shader_buffer_load");
    }

    private static boolean check_NV_shading_rate_image(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_NV_shading_rate_image")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        2139, 2140, 2141, 2142, 2143, 2144, 2145
                },
                "glBindShadingRateImageNV", "glShadingRateImagePaletteNV", "glGetShadingRateImagePaletteNV", "glShadingRateImageBarrierNV",
                "glShadingRateSampleOrderNV", "glShadingRateSampleOrderCustomNV", "glGetShadingRateSampleLocationivNV"
        )) || reportMissing("GL", "GL_NV_shading_rate_image");
    }

    private static boolean check_NV_texture_barrier(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_NV_texture_barrier")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        2146
                },
                "glTextureBarrierNV"
        )) || reportMissing("GL", "GL_NV_texture_barrier");
    }

    private static boolean check_NV_texture_multisample(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_NV_texture_multisample")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        2147, 2148, 2149, 2150, 2151, 2152
                },
                "glTexImage2DMultisampleCoverageNV", "glTexImage3DMultisampleCoverageNV", "glTextureImage2DMultisampleNV", "glTextureImage3DMultisampleNV",
                "glTextureImage2DMultisampleCoverageNV", "glTextureImage3DMultisampleCoverageNV"
        )) || reportMissing("GL", "GL_NV_texture_multisample");
    }

    private static boolean check_NV_timeline_semaphore(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_NV_timeline_semaphore")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        2153, 2154, 2155
                },
                "glCreateSemaphoresNV", "glSemaphoreParameterivNV", "glGetSemaphoreParameterivNV"
        )) || reportMissing("GL", "GL_NV_timeline_semaphore");
    }

    private static boolean check_NV_transform_feedback(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_NV_transform_feedback")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        2156, 2157, 2158, 2159, 2160, 2161, 2162, 2163, 2164, 2165, 2166, 2167
                },
                "glBeginTransformFeedbackNV", "glEndTransformFeedbackNV", "glTransformFeedbackAttribsNV", "glBindBufferRangeNV", "glBindBufferOffsetNV",
                "glBindBufferBaseNV", "glTransformFeedbackVaryingsNV", "glActiveVaryingNV", "glGetVaryingLocationNV", "glGetActiveVaryingNV",
                "glGetTransformFeedbackVaryingNV", "glTransformFeedbackStreamAttribsNV"
        )) || reportMissing("GL", "GL_NV_transform_feedback");
    }

    private static boolean check_NV_transform_feedback2(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_NV_transform_feedback2")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        2168, 2169, 2170, 2171, 2172, 2173, 2174
                },
                "glBindTransformFeedbackNV", "glDeleteTransformFeedbacksNV", "glGenTransformFeedbacksNV", "glIsTransformFeedbackNV", "glPauseTransformFeedbackNV",
                "glResumeTransformFeedbackNV", "glDrawTransformFeedbackNV"
        )) || reportMissing("GL", "GL_NV_transform_feedback2");
    }

    private static boolean check_NV_vertex_array_range(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_NV_vertex_array_range")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        2175, 2176
                },
                "glVertexArrayRangeNV", "glFlushVertexArrayRangeNV"
        )) || reportMissing("GL", "GL_NV_vertex_array_range");
    }

    private static boolean check_NV_vertex_attrib_integer_64bit(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_NV_vertex_attrib_integer_64bit")) {
            return false;
        }

        int flag0 = ext.contains("GL_NV_vertex_buffer_unified_memory") ? 0 : Integer.MIN_VALUE;

        return (checkFunctions(provider, caps, new int[] {
                        2177, 2178, 2179, 2180, 2181, 2182, 2183, 2184, 2185, 2186, 2187, 2188, 2189, 2190, 2191, 2192, 2193, 2194, flag0 + 2195
                },
                "glVertexAttribL1i64NV", "glVertexAttribL2i64NV", "glVertexAttribL3i64NV", "glVertexAttribL4i64NV", "glVertexAttribL1i64vNV",
                "glVertexAttribL2i64vNV", "glVertexAttribL3i64vNV", "glVertexAttribL4i64vNV", "glVertexAttribL1ui64NV", "glVertexAttribL2ui64NV",
                "glVertexAttribL3ui64NV", "glVertexAttribL4ui64NV", "glVertexAttribL1ui64vNV", "glVertexAttribL2ui64vNV", "glVertexAttribL3ui64vNV",
                "glVertexAttribL4ui64vNV", "glGetVertexAttribLi64vNV", "glGetVertexAttribLui64vNV", "glVertexAttribLFormatNV"
        )) || reportMissing("GL", "GL_NV_vertex_attrib_integer_64bit");
    }

    private static boolean check_NV_vertex_buffer_unified_memory(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_NV_vertex_buffer_unified_memory")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        2196, 2197, 2198, 2199, 2200, 2201, 2202, 2203, 2204, 2205, 2206, 2207
                },
                "glBufferAddressRangeNV", "glVertexFormatNV", "glNormalFormatNV", "glColorFormatNV", "glIndexFormatNV", "glTexCoordFormatNV", "glEdgeFlagFormatNV",
                "glSecondaryColorFormatNV", "glFogCoordFormatNV", "glVertexAttribFormatNV", "glVertexAttribIFormatNV", "glGetIntegerui64i_vNV"
        )) || reportMissing("GL", "GL_NV_vertex_buffer_unified_memory");
    }

    private static boolean check_NV_viewport_swizzle(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_NV_viewport_swizzle")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        2208
                },
                "glViewportSwizzleNV"
        )) || reportMissing("GL", "GL_NV_viewport_swizzle");
    }

    private static boolean check_NVX_conditional_render(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_NVX_conditional_render")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        2209, 2210
                },
                "glBeginConditionalRenderNVX", "glEndConditionalRenderNVX"
        )) || reportMissing("GL", "GL_NVX_conditional_render");
    }

    private static boolean check_NVX_gpu_multicast2(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_NVX_gpu_multicast2")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        2211, 2212, 2213, 2214, 2215, 2216
                },
                "glAsyncCopyImageSubDataNVX", "glAsyncCopyBufferSubDataNVX", "glUploadGpuMaskNVX", "glMulticastViewportArrayvNVX", "glMulticastScissorArrayvNVX",
                "glMulticastViewportPositionWScaleNVX"
        )) || reportMissing("GL", "GL_NVX_gpu_multicast2");
    }

    private static boolean check_NVX_progress_fence(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_NVX_progress_fence")) {
            return false;
        }

        return (checkFunctions(provider, caps, new int[] {
                        2217, 2218, 2219, 2220
                },
                "glCreateProgressFenceNVX", "glSignalSemaphoreui64NVX", "glWaitSemaphoreui64NVX", "glClientWaitSemaphoreui64NVX"
        )) || reportMissing("GL", "GL_NVX_progress_fence");
    }

    private static boolean check_OVR_multiview(FunctionProvider provider, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("GL_OVR_multiview")) {
            return false;
        }

        int flag0 = hasDSA(ext) ? 0 : Integer.MIN_VALUE;

        return (checkFunctions(provider, caps, new int[] {
                        2221, flag0 + 2222
                },
                "glFramebufferTextureMultiviewOVR", "glNamedFramebufferTextureMultiviewOVR"
        )) || reportMissing("GL", "GL_OVR_multiview");
    }

    private static boolean hasDSA(Set<String> ext) {
        return ext.contains("GL45") || ext.contains("GL_ARB_direct_state_access") || ext.contains("GL_EXT_direct_state_access");
    }

    private static boolean ARB_framebuffer_object(Set<String> ext) { return ext.contains("OpenGL30") || ext.contains("GL_ARB_framebuffer_object"); }
    private static boolean ARB_map_buffer_range(Set<String> ext) { return ext.contains("OpenGL30") || ext.contains("GL_ARB_map_buffer_range"); }
    private static boolean ARB_vertex_array_object(Set<String> ext) { return ext.contains("OpenGL30") || ext.contains("GL_ARB_vertex_array_object"); }
    private static boolean ARB_copy_buffer(Set<String> ext) { return ext.contains("OpenGL31") || ext.contains("GL_ARB_copy_buffer"); }
    private static boolean ARB_texture_buffer_object(Set<String> ext) { return ext.contains("OpenGL31") || ext.contains("GL_ARB_texture_buffer_object"); }
    private static boolean ARB_uniform_buffer_object(Set<String> ext) { return ext.contains("OpenGL31") || ext.contains("GL_ARB_uniform_buffer_object"); }
    private static boolean ARB_instanced_arrays(Set<String> ext) { return ext.contains("OpenGL33") || ext.contains("GL_ARB_instanced_arrays"); }
    private static boolean ARB_sampler_objects(Set<String> ext) { return ext.contains("OpenGL33") || ext.contains("GL_ARB_sampler_objects"); }
    private static boolean ARB_transform_feedback2(Set<String> ext) { return ext.contains("OpenGL40") || ext.contains("GL_ARB_transform_feedback2"); }
    private static boolean ARB_vertex_attrib_64bit(Set<String> ext) { return ext.contains("OpenGL41") || ext.contains("GL_ARB_vertex_attrib_64bit"); }
    private static boolean ARB_separate_shader_objects(Set<String> ext) { return ext.contains("OpenGL41") || ext.contains("GL_ARB_separate_shader_objects"); }
    private static boolean ARB_texture_storage(Set<String> ext) { return ext.contains("OpenGL42") || ext.contains("GL_ARB_texture_storage"); }
    private static boolean ARB_texture_storage_multisample(Set<String> ext) { return ext.contains("OpenGL43") || ext.contains("GL_ARB_texture_storage_multisample"); }
    private static boolean ARB_vertex_attrib_binding(Set<String> ext) { return ext.contains("OpenGL43") || ext.contains("GL_ARB_vertex_attrib_binding"); }
    private static boolean ARB_invalidate_subdata(Set<String> ext) { return ext.contains("OpenGL43") || ext.contains("GL_ARB_invalidate_subdata"); }
    private static boolean ARB_texture_buffer_range(Set<String> ext) { return ext.contains("OpenGL43") || ext.contains("GL_ARB_texture_buffer_range"); }
    private static boolean ARB_clear_buffer_object(Set<String> ext) { return ext.contains("OpenGL43") || ext.contains("GL_ARB_clear_buffer_object"); }
    private static boolean ARB_framebuffer_no_attachments(Set<String> ext) { return ext.contains("OpenGL43") || ext.contains("GL_ARB_framebuffer_no_attachments"); }
    private static boolean ARB_buffer_storage(Set<String> ext) { return ext.contains("OpenGL44") || ext.contains("GL_ARB_buffer_storage"); }
    private static boolean ARB_clear_texture(Set<String> ext) { return ext.contains("OpenGL44") || ext.contains("GL_ARB_clear_texture"); }
    private static boolean ARB_multi_bind(Set<String> ext) { return ext.contains("OpenGL44") || ext.contains("GL_ARB_multi_bind"); }
    private static boolean ARB_query_buffer_object(Set<String> ext) { return ext.contains("OpenGL44") || ext.contains("GL_ARB_query_buffer_object"); }

}