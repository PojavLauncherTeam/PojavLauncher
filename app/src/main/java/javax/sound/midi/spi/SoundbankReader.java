package javax.sound.midi.spi;

import android.support.annotation.NonNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.Soundbank;

/**
 * Abstract class for Soundbank Reader
 *
 * @author K.Shoji
 */
public abstract class SoundbankReader {

    /**
     * Constructor
     */
    public SoundbankReader() {
    }

    /**
     * Get the Soundbank from the specified {@link File}
     *
     * @param file the file
     * @return Soundbank
     * @throws InvalidMidiDataException
     * @throws IOException
     */
    @NonNull
    public abstract Soundbank getSoundbank(@NonNull File file) throws InvalidMidiDataException, IOException;

    /**
     * Get the Soundbank from the specified {@link InputStream}
     *
     * @param stream the InputStream
     * @return Soundbank
     * @throws InvalidMidiDataException
     * @throws IOException
     */
    @NonNull
    public abstract Soundbank getSoundbank(@NonNull InputStream stream) throws InvalidMidiDataException, IOException;

    /**
     * Get the Soundbank from the specified {@link URL}
     *
     * @param url the URL
     * @return Soundbank
     * @throws InvalidMidiDataException
     * @throws IOException
     */
    @NonNull
    public abstract Soundbank getSoundbank(@NonNull URL url) throws InvalidMidiDataException, IOException;
}
