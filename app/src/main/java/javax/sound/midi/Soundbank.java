package javax.sound.midi;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Interface for MIDI Soundbank
 *
 * @author K.Shoji
 */
public interface Soundbank {

    /**
     * Get the name of Soundbank
     *
     * @return the name of Soundbank
     */
    @NonNull
    String getName();

    /**
     * Get the description string
     *
     * @return the description
     */
    @NonNull
    String getDescription();

    /**
     * Get the vendor string
     *
     * @return the vendor
     */
    @NonNull
    String getVendor();

    /**
     * Get the version string
     *
     * @return the version
     */
    @NonNull
    String getVersion();

    /**
     * Get the {@link Instrument}
     *
     * @param patch the {@link Patch}
     * @return {@link Instrument} matches with patch
     */
    @Nullable
    Instrument getInstrument(@NonNull Patch patch);

    /**
     * Get all of {@link Instrument}s
     *
     * @return the array of {@link Instrument}s
     */
    @NonNull
    Instrument[] getInstruments();

    /**
     * Get all of {@link SoundbankResource}s
     * @return the array of {@link SoundbankResource}s
     */
    @NonNull
    SoundbankResource[] getResources();
}
