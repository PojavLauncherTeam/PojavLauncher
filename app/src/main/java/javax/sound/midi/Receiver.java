package javax.sound.midi;

import androidx.annotation.*;

/**
 * Interface for {@link MidiMessage} receiver.
 * 
 * @author K.Shoji
 */
public interface Receiver {

	/**
	 * Called at {@link MidiMessage} receiving
	 * 
	 * @param message the received message
	 * @param timeStamp -1 if the timeStamp information is not available
	 */
	void send(@NonNull MidiMessage message, long timeStamp);

	/**
	 * Close the {@link Receiver}
	 */
	void close();
}
