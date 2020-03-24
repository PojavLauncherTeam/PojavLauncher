package javax.sound.midi;

import android.support.annotation.NonNull;

/**
 * Abstract Class for MIDI Instrument
 *
 * @author K.Shoji
 */
public abstract class Instrument extends SoundbankResource {
    private final Patch patch;

    /**
     * Constructor
     *
     * @param soundbank the soundbank
     * @param patch the patch
     * @param name the name
     * @param dataClass the dataClass
     */
    protected Instrument(@NonNull final Soundbank soundbank, @NonNull final Patch patch, @NonNull final String name, @NonNull final Class<?> dataClass) {
        super(soundbank, name, dataClass);
        this.patch = patch;
    }

    /**
     * Get the patch of the {@link Instrument}
     *
     * @return the patch
     */
    @NonNull
    public Patch getPatch() {
        return patch;
    }
}
