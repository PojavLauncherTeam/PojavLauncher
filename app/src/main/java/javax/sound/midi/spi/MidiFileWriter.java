package javax.sound.midi.spi;

import android.support.annotation.NonNull;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import javax.sound.midi.Sequence;

/**
 * Abstract class for MIDI File Writer
 *
 * @author K.Shoji
 */
public abstract class MidiFileWriter {

    /**
     * Get the all of the file types ID
     *
     * @return the array of file type
     */
    @NonNull
    public abstract int[] getMidiFileTypes();

    /**
     * Get the all of the file types ID on the specified {@link Sequence}
     *
     * @param sequence the sequence
     * @return the array of file type
     */
    @NonNull
    public abstract int[] getMidiFileTypes(@NonNull Sequence sequence);

    /**
     * Check if the specified file type is supported
     *
     * @param fileType the file type
     * @return true if the specified file type is supported
     */
	public boolean isFileTypeSupported(int fileType) {
		int[] supported = getMidiFileTypes();
		for (int element : supported) {
			if (fileType == element) {
				return true;
			}
		}
		return false;
	}

    /**
     * Check if the specified file type is supported on the specified {@link Sequence}
     *
     * @param fileType the file type
     * @param sequence the sequence
     * @return true if the specified file type is supported on the sequence
     */
	public boolean isFileTypeSupported(int fileType, @NonNull Sequence sequence) {
		int[] supported = getMidiFileTypes(sequence);
		for (int element : supported) {
			if (fileType == element) {
				return true;
			}
		}
		return false;
	}

    /**
     * Write the {@link Sequence} to the {@link File} with the file type
     *
     * @param sequence the sequence
     * @param fileType the file type
     * @param file the file
     * @return the written data length
     * @throws IOException
     */
	public abstract int write(@NonNull Sequence sequence, int fileType, @NonNull File file) throws IOException;

    /**
     * Write the {@link Sequence} to the {@link OutputStream} with the file type
     *
     * @param sequence the sequence
     * @param fileType the file type
     * @param outputStream the OutputStream
     * @return the written data length
     * @throws IOException
     */
	public abstract int write(@NonNull Sequence sequence, int fileType, @NonNull OutputStream outputStream) throws IOException;
}
