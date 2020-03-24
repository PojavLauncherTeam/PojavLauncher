package javax.sound.midi;

import android.support.annotation.NonNull;

import java.util.EventListener;

/**
 * {@link EventListener} for MIDI Meta messages.
 * 
 * @author K.Shoji
 */
public interface MetaEventListener extends EventListener {

	/**
	 * Called at {@link MetaMessage} event has fired
	 * 
	 * @param meta the source event
	 */
	void meta(@NonNull MetaMessage meta);
}
