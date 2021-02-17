package org.lwjgl.openal;

import java.nio.*;

import org.lwjgl.openal.EXTEfx;

public class EFX10 {

    public static final int AL_EFFECT_TYPE = EXTEfx.AL_EFFECT_TYPE;
    public static final int AL_EFFECTSLOT_EFFECT = EXTEfx.AL_EFFECTSLOT_EFFECT;
    public static final int AL_EFFECT_ECHO = EXTEfx.AL_EFFECT_ECHO;
    public static final float AL_ECHO_MIN_DAMPING = EXTEfx.AL_ECHO_MIN_DAMPING;
    public static final int AL_ECHO_DAMPING = EXTEfx.AL_ECHO_DAMPING;
    public static final float AL_ECHO_MAX_DAMPING = EXTEfx.AL_ECHO_MAX_DAMPING;
    public static final float AL_ECHO_MIN_DELAY = EXTEfx.AL_ECHO_MIN_DELAY;
    public static final int AL_ECHO_DELAY = EXTEfx.AL_ECHO_DELAY;
    public static final float AL_ECHO_MAX_DELAY = EXTEfx.AL_ECHO_MAX_DELAY;
    public static final float AL_ECHO_MIN_FEEDBACK = EXTEfx.AL_ECHO_MIN_FEEDBACK;
    public static final int AL_ECHO_FEEDBACK = EXTEfx.AL_ECHO_FEEDBACK;
    public static final float AL_ECHO_MAX_FEEDBACK = EXTEfx.AL_ECHO_MAX_FEEDBACK;
    public static final float AL_ECHO_MIN_LRDELAY = EXTEfx.AL_ECHO_MIN_LRDELAY;
    public static final int AL_ECHO_LRDELAY = EXTEfx.AL_ECHO_LRDELAY;
    public static final float AL_ECHO_MAX_LRDELAY = EXTEfx.AL_ECHO_MAX_LRDELAY;
    public static final float AL_ECHO_MIN_SPREAD = EXTEfx.AL_ECHO_MIN_SPREAD;
    public static final int AL_ECHO_SPREAD = EXTEfx.AL_ECHO_SPREAD;
    public static final float AL_ECHO_MAX_SPREAD = EXTEfx.AL_ECHO_MAX_SPREAD;
    public static final int AL_EFFECT_REVERB = EXTEfx.AL_EFFECT_REVERB;
    public static final int AL_RING_MODULATOR_SINUSOID = EXTEfx.AL_RING_MODULATOR_SINUSOID;
    public static final int AL_RING_MODULATOR_SAWTOOTH = EXTEfx.AL_RING_MODULATOR_SAWTOOTH;
    public static final int AL_RING_MODULATOR_SQUARE = EXTEfx.AL_RING_MODULATOR_SQUARE;
    public static final int AL_EFFECT_RING_MODULATOR = EXTEfx.AL_EFFECT_RING_MODULATOR;
    public static final float AL_RING_MODULATOR_MAX_FREQUENCY = EXTEfx.AL_RING_MODULATOR_MAX_FREQUENCY;
    public static final int AL_RING_MODULATOR_FREQUENCY = EXTEfx.AL_RING_MODULATOR_FREQUENCY;
    public static final float AL_RING_MODULATOR_MIN_FREQUENCY = EXTEfx.AL_RING_MODULATOR_MIN_FREQUENCY;
    public static final float AL_RING_MODULATOR_MAX_HIGHPASS_CUTOFF = EXTEfx.AL_RING_MODULATOR_MAX_HIGHPASS_CUTOFF;
    public static final int AL_RING_MODULATOR_HIGHPASS_CUTOFF = EXTEfx.AL_RING_MODULATOR_HIGHPASS_CUTOFF;
    public static final float AL_RING_MODULATOR_MIN_HIGHPASS_CUTOFF = EXTEfx.AL_RING_MODULATOR_MIN_HIGHPASS_CUTOFF;
    public static final int AL_RING_MODULATOR_WAVEFORM = EXTEfx.AL_RING_MODULATOR_WAVEFORM;
    public static final int AL_FILTER_TYPE = EXTEfx.AL_FILTER_TYPE;
    public static final int AL_FILTER_LOWPASS = EXTEfx.AL_FILTER_LOWPASS;
    public static final int AL_LOWPASS_GAIN = EXTEfx.AL_LOWPASS_GAIN;
    public static final int AL_LOWPASS_GAINHF = EXTEfx.AL_LOWPASS_GAINHF;
    public static final int AL_EFFECTSLOT_NULL = EXTEfx.AL_EFFECTSLOT_NULL;
    public static final int AL_FILTER_NULL = EXTEfx.AL_FILTER_NULL;
    public static final int AL_AUXILIARY_SEND_FILTER = EXTEfx.AL_AUXILIARY_SEND_FILTER;
    public static final int AL_DIRECT_FILTER = EXTEfx.AL_DIRECT_FILTER;
    public static final int ALC_MAX_AUXILIARY_SENDS = EXTEfx.ALC_MAX_AUXILIARY_SENDS;
    public static final int ALC_EFX_MAJOR_VERSION = EXTEfx.ALC_EFX_MAJOR_VERSION;
    public static final int AL_REVERB_DECAY_TIME = EXTEfx.AL_REVERB_DECAY_TIME;
    public static final int AL_FILTER_HIGHPASS = EXTEfx.AL_FILTER_HIGHPASS;
    public static final int AL_FILTER_BANDPASS = EXTEfx.AL_FILTER_BANDPASS;
    public static final int AL_EFFECT_NULL = EXTEfx.AL_EFFECT_NULL;
    public static final int AL_EFFECT_EAXREVERB = EXTEfx.AL_EFFECT_EAXREVERB;
    public static final int AL_EFFECT_CHORUS = EXTEfx.AL_EFFECT_CHORUS;
    public static final int AL_EFFECT_DISTORTION = EXTEfx.AL_EFFECT_DISTORTION;
    public static final int AL_EFFECT_FLANGER = EXTEfx.AL_EFFECT_FLANGER;
    public static final int AL_EFFECT_FREQUENCY_SHIFTER = EXTEfx.AL_EFFECT_FREQUENCY_SHIFTER;
    public static final int AL_EFFECT_VOCAL_MORPHER = EXTEfx.AL_EFFECT_VOCAL_MORPHER;
    public static final int AL_EFFECT_PITCH_SHIFTER = EXTEfx.AL_EFFECT_PITCH_SHIFTER;
    public static final int AL_EFFECT_AUTOWAH = EXTEfx.AL_EFFECT_AUTOWAH;
    public static final int AL_EFFECT_COMPRESSOR = EXTEfx.AL_EFFECT_COMPRESSOR;
    public static final int AL_EFFECT_EQUALIZER = EXTEfx.AL_EFFECT_EQUALIZER;

    public static int alGenAuxiliaryEffectSlots() {
        return EXTEfx.alGenAuxiliaryEffectSlots();
    }

    public static int alGenFilters() {
        return EXTEfx.alGenFilters();
    }

    public static void alDeleteFilters(int filter) {
        EXTEfx.alDeleteFilters(filter);
    }

    public static void alFilteri(int filter, int param, int value) {
        EXTEfx.alFilteri(filter, param, value);
    }

    public static void alFilterf(int filter, int param, float value) {
        EXTEfx.alFilterf(filter, param, value);
    }

    public static float alGetFilterf(int filter, int param) {
        return EXTEfx.alGetFilterf(filter, param);
    }

    public static int alGenEffects() {
        return EXTEfx.alGenEffects();
    }

    public static void alDeleteEffects(int effect) {
        EXTEfx.alDeleteAuxiliaryEffectSlots(effect);
    }

    public static void alDeleteAuxiliaryEffectSlots(int effectSlot) {
        EXTEfx.alDeleteAuxiliaryEffectSlots(effectSlot);
    }

    public static void alEffecti(int effect, int param, int value) {
        EXTEfx.alEffecti(effect, param, value);
    }

    public static float alGetEffectf(int effect, int param) {
        return EXTEfx.alGetEffectf(effect, param);
    }

    public static int alGetEffecti(int effect, int param) {
        return EXTEfx.alGetEffecti(effect, param);
    }

    public static void alEffectf(int effect, int param, float value) {
        EXTEfx.alEffectf(effect, param, value);
    }

    public static void alAuxiliaryEffectSloti(int effectSlot, int param, int value) {
        EXTEfx.alAuxiliaryEffectSloti(effectSlot, param, value);
    }

    public static void alGenAuxiliaryEffectSlots(IntBuffer effectSlots) {
        EXTEfx.alGenAuxiliaryEffectSlots(effectSlots);
    }

    public static void alGenEffects(IntBuffer effects) {
        EXTEfx.alGenEffects(effects);
    }

    public static void alDeleteEffects(IntBuffer effects) {
        EXTEfx.alDeleteEffects(effects);
    }

    public static void alDeleteAuxiliaryEffectSlots(IntBuffer effectSlots) {
        EXTEfx.alDeleteAuxiliaryEffectSlots(effectSlots);
    }

    public static void alGenFilters(IntBuffer filters) {
        EXTEfx.alGenFilters(filters);
    }

    public static void alDeleteFilters(IntBuffer filters) {
        EXTEfx.alDeleteFilters(filters);
    }







    public static void alGetAuxiliaryEffectSlot(int var0, int var1, FloatBuffer var2) {
        EXTEfx.alGetAuxiliaryEffectSlotfv(var0, var1, var2);
    }

    public static void alGetAuxiliaryEffectSlot(int var0, int var1, IntBuffer var2) {
        EXTEfx.alGetAuxiliaryEffectSlotiv(var0, var1, var2);
    }

    public static float alGetAuxiliaryEffectSlotf(int var0, int var1) {
        return EXTEfx.alGetAuxiliaryEffectSlotf(var0, var1);
    }

    public static int alGetAuxiliaryEffectSloti(int var0, int var1) {
        return EXTEfx.alGetAuxiliaryEffectSloti(var0, var1);
    }

    public static void alEffect(int var0, int var1, FloatBuffer var2) {
        EXTEfx.alEffectfv(var0, var1, var2);
    }

    public static void alEffect(int var0, int var1, IntBuffer var2) {
        EXTEfx.alEffectiv(var0, var1, var2);
    }

    public static void alFilter(int var0, int var1, FloatBuffer var2) {
        EXTEfx.alFilterfv(var0, var1, var2);
    }

    public static void alFilter(int var0, int var1, IntBuffer var2) {
        EXTEfx.alFilteriv(var0, var1, var2);
    }

    public static void alAuxiliaryEffectSlot(int var0, int var1, FloatBuffer var2) {
        EXTEfx.alAuxiliaryEffectSlotfv(var0, var1, var2);
    }

    public static void alAuxiliaryEffectSlot(int var0, int var1, IntBuffer var2) {
        EXTEfx.alAuxiliaryEffectSlotiv(var0, var1, var2);
    }

    public static void alAuxiliaryEffectSlotf(int var0, int var1, float var2) {
        EXTEfx.alAuxiliaryEffectSlotf(var0, var1, var2);
    }

    public static void alGetEffect(int var0, int var1, FloatBuffer var2) {
        EXTEfx.alGetEffectfv(var0, var1, var2);
    }

    public static void alGetEffect(int var0, int var1, IntBuffer var2) {
        EXTEfx.alGetEffectiv(var0, var1, var2);
    }

    public static void alGetFilter(int var0, int var1, FloatBuffer var2) {
        EXTEfx.alGetFilterfv(var0, var1, var2);
    }

    public static void alGetFilter(int var0, int var1, IntBuffer var2) {
        EXTEfx.alGetFilteriv(var0, var1, var2);
    }

    public static int alGetFilteri(int var0, int var1) {
        return EXTEfx.alGetFilteri(var0, var1);
    }

    public static boolean alIsAuxiliaryEffectSlot(int var0) {
        return EXTEfx.alIsAuxiliaryEffectSlot(var0);
    }

    public static boolean alIsEffect(int var0) {
        return EXTEfx.alIsEffect(var0);
    }

    public static boolean alIsFilter(int var0) {
        return EXTEfx.alIsFilter(var0);
    }
}
