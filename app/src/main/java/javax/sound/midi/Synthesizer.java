package javax.sound.midi;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Interface for MIDI Synthesizer
 *
 * @author K.Shoji
 */
public interface Synthesizer extends MidiDevice {

    /**
     * Get the all of {@link MidiChannel}s
     *
     * @return the array of MidiChannel
     */
    @NonNull
    MidiChannel[] getChannels();

    /**
     * Get the latency in microseconds
     *
     * @return the latency in microseconds
     */
    long getLatency();

    /**
     * Get the maximum count of polyphony
     *
     * @return the maximum count of polyphony
     */
    int getMaxPolyphony();

    /**
     * Get the current {@link VoiceStatus} of the Synthesizer
     *
     * @return the array of VoiceStatus
     */
    @NonNull
    VoiceStatus[] getVoiceStatus();

    /**
     * Get the default {@link Soundbank}
     *
     * @return the Soundbank
     */
    @Nullable
    Soundbank getDefaultSoundbank();

    /**
     * Check if the specified {@link Soundbank} is supported
     *
     * @param soundbank the Soundbank
     * @return true if the Soundbank is supported
     */
    boolean isSoundbankSupported(@NonNull Soundbank soundbank);

    /**
     * Get the all available {@link Instrument}s
     *
     * @return the array of Instrument
     */
    @NonNull
    Instrument[] getAvailableInstruments();

    /**
     * Get the all loaded {@link Instrument}s
     *
     * @return the array of Instrument
     */
    @NonNull
    Instrument[] getLoadedInstruments();

    /**
     * Remap an Instrument
     *
     * @param from to be replaced
     * @param to the new Instrument
     * @return true if succeed to remap
     */
    boolean remapInstrument(@NonNull Instrument from, @NonNull Instrument to);

    /**
     * Load all instruments belongs specified {@link Soundbank}
     *
     * @param soundbank the Soundbank
     * @return true if succeed to load
     */
    boolean loadAllInstruments(@NonNull Soundbank soundbank);

    /**
     * Unload all instruments belongs specified {@link Soundbank}
     *
     * @param soundbank the Soundbank
     */
    void unloadAllInstruments(@NonNull Soundbank soundbank);

    /**
     * Load the specified {@link Instrument}
     *
     * @param instrument the instrument
     * @return true if succeed to load
     */
    boolean loadInstrument(@NonNull Instrument instrument);

    /**
     * Unload the specified {@link Instrument}
     *
     * @param instrument the instrument
     */
    void unloadInstrument(@NonNull Instrument instrument);

    /**
     * Load all instruments belongs specified {@link Soundbank} and {@link Patch}es
     *
     * @param soundbank the the Soundbank
     * @param patchList the array of Patch
     * @return true if succeed to load
     */
    boolean loadInstruments(@NonNull Soundbank soundbank, @NonNull Patch[] patchList);

    /**
     * Unload all instruments belongs specified {@link Soundbank} and {@link Patch}es
     *
     * @param soundbank the the Soundbank
     * @param patchList the array of Patch
     */
    void unloadInstruments(@NonNull Soundbank soundbank, @NonNull Patch[] patchList);
}
