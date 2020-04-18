package javax.sound.midi;

import androidx.annotation.*;

/**
 * Interface for {@link MidiDevice} receiver.
 *
 * @author K.Shoji
 */
public interface MidiDeviceReceiver extends Receiver {

    /**
     * Get the {@link javax.sound.midi.MidiDevice} associated with this instance.
     *
     * @return the {@link javax.sound.midi.MidiDevice} associated with this instance.
     */
    @NonNull
    MidiDevice getMidiDevice();
}
