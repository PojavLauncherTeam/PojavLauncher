package javax.sound.midi.spi;

import androidx.annotation.*;
import java.io.*;
import java.net.*;
import javax.sound.midi.*;

/**
 * Abstract class for MIDI File Reader
 *
 * @author K.Shoji
 */
public abstract class MidiFileReader {

    /**
     * Get the {@link MidiFileFormat} from the specified file
     *
     * @param file the file
     * @return MidiFileFormat
     * @throws InvalidMidiDataException
     * @throws IOException
     */
    @NonNull
    public abstract MidiFileFormat getMidiFileFormat(@NonNull File file) throws InvalidMidiDataException, IOException;

    /**
     * Get the {@link MidiFileFormat} from the specified stream
     *
     * @param stream the InputStream
     * @return MidiFileFormat
     * @throws InvalidMidiDataException
     * @throws IOException
     */
    @NonNull
    public abstract MidiFileFormat getMidiFileFormat(@NonNull InputStream stream) throws InvalidMidiDataException, IOException;

    /**
     * Get the {@link MidiFileFormat} from the specified URL
     *
     * @param url the URL
     * @return MidiFileFormat
     * @throws InvalidMidiDataException
     * @throws IOException
     */
    @NonNull
    public abstract MidiFileFormat getMidiFileFormat(@NonNull URL url) throws InvalidMidiDataException, IOException;

    /**
     * Get the {@link Sequence} from the specified file
     *
     * @param file the file
     * @return Sequence
     * @throws InvalidMidiDataException
     * @throws IOException
     */
    @NonNull
    public abstract Sequence getSequence(@NonNull File file) throws InvalidMidiDataException, IOException;

    /**
     * Get the {@link Sequence} from the specified stream
     *
     * @param stream the InputStream
     * @return Sequence
     * @throws InvalidMidiDataException
     * @throws IOException
     */
    @NonNull
    public abstract Sequence getSequence(@NonNull InputStream stream) throws InvalidMidiDataException, IOException;

    /**
     * Get the {@link Sequence} from the specified URL
     *
     * @param url the URL
     * @return Sequence
     * @throws InvalidMidiDataException
     * @throws IOException
     */
    @NonNull
    public abstract Sequence getSequence(@NonNull URL url) throws InvalidMidiDataException, IOException;

}
