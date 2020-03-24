package javax.sound.midi;

import android.support.annotation.NonNull;

/**
 * Interface for {@link MidiDevice} transmitter.
 *
 * @author K.Shoji
 */
public interface MidiDeviceTransmitter extends Transmitter {

    /**
     * Get the {@link javax.sound.midi.MidiDevice} associated with this instance.
     *
     * @return the {@link javax.sound.midi.MidiDevice} associated with this instance.
     */
    @NonNull
    MidiDevice getMidiDevice();
}
