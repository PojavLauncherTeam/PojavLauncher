/*
 * Copyright LWJGL. All rights reserved.
 * License terms: http://lwjgl.org/license.php
 */
package org.lwjgl.openal;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.EXTEfx.*;

import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;

/**
 * Utility class for the OpenAL extension AL_EXT_EFX. Provides functions to check for the extension
 * and support of various effects and filters.
 * <p/>
 * Currently supports AL_EXT_EFX version 1.0 effects and filters.
 *
 * @author Ciardhubh <ciardhubh[at]ciardhubh.de>
 */
public final class EFXUtil {

	/** Constant for testSupportGeneric to check an effect. */
	private static final int EFFECT = 1111;
	/** Constant for testSupportGeneric to check a filter. */
	private static final int FILTER = 2222;

	/** Utility class, hidden contructor. */
	private EFXUtil() {
	}

	/**
	 * Checks if OpenAL implementation is loaded and supports AL_EXT_EFX.
	 *
	 * @return True if AL_EXT_EFX is supported, false if not.
	 *
	 * @throws org.lwjgl.openal.OpenALException
	 *          If OpenAL has not been created yet.
	 */
	public static boolean isEfxSupported() {
		//return ALC.getCapabilities().ALC_EXT_EFX;
		return ALC.createCapabilities(AL.alcDevice.device).ALC_EXT_EFX;
	}

	/**
	 * Tests OpenAL to see whether the given effect type is supported. This is done by creating an
	 * effect of the given type. If creation succeeds the effect is supported.
	 *
	 * @param effectType Type of effect whose support is to be tested, e.g. AL_EFFECT_REVERB.
	 *
	 * @return True if it is supported, false if not.
	 *
	 * @throws org.lwjgl.openal.OpenALException
	 *                                  If the request fails due to an AL_OUT_OF_MEMORY error or OpenAL has
	 *                                  not been created yet.
	 * @throws IllegalArgumentException effectType is not a valid effect type.
	 */
	public static boolean isEffectSupported(int effectType) {
		// Make sure type is a real effect.
		switch ( effectType ) {
			case AL_EFFECT_NULL:
			case AL_EFFECT_EAXREVERB:
			case AL_EFFECT_REVERB:
			case AL_EFFECT_CHORUS:
			case AL_EFFECT_DISTORTION:
			case AL_EFFECT_ECHO:
			case AL_EFFECT_FLANGER:
			case AL_EFFECT_FREQUENCY_SHIFTER:
			case AL_EFFECT_VOCAL_MORPHER:
			case AL_EFFECT_PITCH_SHIFTER:
			case AL_EFFECT_RING_MODULATOR:
			case AL_EFFECT_AUTOWAH:
			case AL_EFFECT_COMPRESSOR:
			case AL_EFFECT_EQUALIZER:
				break;
			default:
				throw new IllegalArgumentException("Unknown or invalid effect type: " + effectType);
		}

		return testSupportGeneric(EFFECT, effectType);
	}

	/**
	 * Tests OpenAL to see whether the given filter type is supported. This is done by creating a
	 * filter of the given type. If creation succeeds the filter is supported.
	 *
	 * @param filterType Type of filter whose support is to be tested, e.g. AL_FILTER_LOWPASS.
	 *
	 * @return True if it is supported, false if not.
	 *
	 * @throws org.lwjgl.openal.OpenALException
	 *                                  If the request fails due to an AL_OUT_OF_MEMORY error or OpenAL has
	 *                                  not been created yet.
	 * @throws IllegalArgumentException filterType is not a valid filter type.
	 */
	public static boolean isFilterSupported(int filterType) {
		// Make sure type is a real filter.
		switch ( filterType ) {
			case AL_FILTER_NULL:
			case AL_FILTER_LOWPASS:
			case AL_FILTER_HIGHPASS:
			case AL_FILTER_BANDPASS:
				break;
			default:
				throw new IllegalArgumentException("Unknown or invalid filter type: " + filterType);
		}

		return testSupportGeneric(FILTER, filterType);
	}

	/**
	 * Generic test function to see if an EFX object supports a given kind of type. Works for
	 * effects and filters.
	 *
	 * @param objectType Type of object to test. Must be either EFXUtil.EFFECT or EFXUtil.FILTER.
	 * @param typeValue  OpenAL type the object should be tested for support, e.g. AL_FILTER_LOWPASS
	 *                   or AL_EFFECT_REVERB.
	 *
	 * @return True if object supports typeValue, false else.
	 */
	private static boolean testSupportGeneric(int objectType, int typeValue) {
		// Check for supported objectType.
		switch ( objectType ) {
			case EFFECT:
			case FILTER:
				break;
			default:
				throw new IllegalArgumentException("Invalid objectType: " + objectType);
		}

		boolean supported = false;
		if ( isEfxSupported() ) {

			// Try to create object in order to check AL's response.
			alGetError();
			int genError;
			int testObject = 0;
			try {
				switch ( objectType ) { // Create object based on type
					case EFFECT:
						testObject = alGenEffects();
						break;
					case FILTER:
						testObject = alGenFilters();
						break;
					default:
						throw new IllegalArgumentException("Invalid objectType: " + objectType);
				}
				genError = alGetError();
			} catch (OpenALException debugBuildException) {
				// Hack because OpenALException hides the original error code (short of parsing the
				// error message String which would break if it gets changed).
				if ( debugBuildException.getMessage().contains("AL_OUT_OF_MEMORY") ) {
					genError = AL_OUT_OF_MEMORY;
				} else {
					genError = AL_INVALID_OPERATION;
				}
			}

			if ( genError == AL_NO_ERROR ) {
				// Successfully created, now try to set type.
				alGetError();
				int setError;
				try {
					switch ( objectType ) { // Set based on object type
						case EFFECT:
							alEffecti(testObject, AL_EFFECT_TYPE, typeValue);
							break;
						case FILTER:
							alFilteri(testObject, AL_FILTER_TYPE, typeValue);
							break;
						default:
							throw new IllegalArgumentException("Invalid objectType: " + objectType);
					}
					setError = alGetError();
				} catch (OpenALException debugBuildException) {
					// Hack because OpenALException hides the original error code (short of parsing
					// the error message String which would break when it gets changed).
					setError = AL_INVALID_VALUE;
				}

				if ( setError == AL_NO_ERROR ) {
					supported = true;
				}

				// Cleanup
				try {
					switch ( objectType ) { // Set based on object type
						case EFFECT:
							alDeleteEffects(testObject);
							break;
						case FILTER:
							alDeleteFilters(testObject);
							break;
						default:
							throw new IllegalArgumentException("Invalid objectType: " + objectType);
					}
				} catch (OpenALException debugBuildException) {
					// Don't care about cleanup errors.
				}

			} else if ( genError == AL_OUT_OF_MEMORY ) {
				throw new OpenALException(AL10.alGetString(genError));
			}
		}

		return supported;
	}

}
