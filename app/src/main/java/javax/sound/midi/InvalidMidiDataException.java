package javax.sound.midi;

import android.support.annotation.NonNull;

/**
 * {@link Exception} for invalid MIDI data.
 * 
 * @author K.Shoji
 */
public class InvalidMidiDataException extends Exception {
	private static final long serialVersionUID = 2780771756789932067L;

    /**
     * Constructor
     */
	public InvalidMidiDataException() {
		super();
	}

    /**
     * Constructor with the message
     *
     * @param message the message
     */
	public InvalidMidiDataException(@NonNull String message) {
		super(message);
	}
}
