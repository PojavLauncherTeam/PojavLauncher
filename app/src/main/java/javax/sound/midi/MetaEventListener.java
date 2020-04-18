package javax.sound.midi;

import androidx.annotation.*;
import java.util.*;

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
