package javax.sound.midi;

import android.support.annotation.NonNull;

/**
 * {@link Exception} thrown when unable to use {@link MidiDevice}s.
 *
 * @author K.Shoji
 */
public class MidiUnavailableException extends Exception {
	private static final long serialVersionUID = 6093809578628944323L;

    /**
     * Constructor
     */
	public MidiUnavailableException() {
		super();
	}

    /**
     * Constructor with a message
     *
     * @param message the message
     */
	public MidiUnavailableException(@NonNull String message) {
		super(message);
	}
}
