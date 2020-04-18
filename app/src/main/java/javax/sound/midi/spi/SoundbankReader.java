package javax.sound.midi.spi;

import androidx.annotation.*;
import java.io.*;
import java.net.*;
import javax.sound.midi.*;

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
