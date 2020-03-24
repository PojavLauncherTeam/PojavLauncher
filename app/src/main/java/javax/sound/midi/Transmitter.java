package javax.sound.midi;

import android.support.annotation.Nullable;

/**
 * Interface for MIDI Transmitter.
 * 
 * @author K.Shoji
 */
public interface Transmitter {

	/**
	 * Set the {@link Receiver} for this {@link Transmitter}
     *
	 * @param receiver the Receiver
	 */
	void setReceiver(@Nullable Receiver receiver);

	/**
	 * Get the {@link Receiver} for this {@link Transmitter}
     *
	 * @return the Receiver
	 */
    @Nullable
    Receiver getReceiver();

	/**
	 * Close this {@link Transmitter}
	 */
	void close();
}
