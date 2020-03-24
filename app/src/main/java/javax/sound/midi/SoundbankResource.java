package javax.sound.midi;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Interface for MIDI Soundbank resource
 *
 * @author K.Shoji
 */
public abstract class SoundbankResource {
    private final Soundbank soundbank;
    private final String name;
    private final Class<?> dataClass;

    /**
     * Constructor
     *
     * @param soundbank the Soundbank
     * @param name the name of {@link SoundbankResource}
     * @param dataClass the class of data
     */
    protected SoundbankResource(@NonNull final Soundbank soundbank, @NonNull final String name, @NonNull final Class<?> dataClass) {
        this.soundbank = soundbank;
        this.name = name;
        this.dataClass = dataClass;
    }

    /**
     * Get the data of {@link SoundbankResource}
     *
     * @return the data
     */
    @Nullable
    public abstract Object getData();

    /**
     * Get the class of data(obtained by {@link #getData()}
     *
     * @return the class
     */
    @Nullable
    public Class<?> getDataClass() {
        return dataClass;
    }

    /**
     * Get the name of {@link SoundbankResource}
     *
     * @return the name
     */
    @NonNull
    public String getName() {
        return name;
    }

    /**
     * Get the {@link Soundbank}
     *
     * @return the Soundbank
     */
    @NonNull
    public Soundbank getSoundbank() {
        return soundbank;
    }
}
