package javax.sound.midi.impl;

import android.support.annotation.NonNull;
import android.util.SparseIntArray;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.VoiceStatus;

/**
 * {@link javax.sound.midi.MidiChannel} implementation
 *
 * @author K.Shoji
 */
public final class MidiChannelImpl implements MidiChannel {
    private final int channel;
    private final Receiver receiver;
    private final VoiceStatus voiceStatus;

    private int channelPressure = 127;
    private SparseIntArray controller = new SparseIntArray();
    private int program = 0;
    private boolean mono = false;
    private boolean mute = false;
    private boolean omni = false;
    private int pitchbend = 8192;
    private SparseIntArray polyPressure = new SparseIntArray();
    private boolean solo = false;
    private boolean localControl = true;

    /**
     * Constructor
     *
     * @param channel the channel 0-15
     * @param receiver the receiver
     */
    public MidiChannelImpl(final int channel, @NonNull final Receiver receiver, @NonNull final VoiceStatus voiceStatus) {
        this.channel = voiceStatus.channel;
        this.receiver = receiver;
        this.voiceStatus = voiceStatus;
        this.voiceStatus.channel = channel;
    }

    @Override
    public void allNotesOff() {
        try {
            receiver.send(new ShortMessage(ShortMessage.CONTROL_CHANGE | channel, 123, 0), 0);
        } catch (final InvalidMidiDataException ignored) {
        }
    }

    @Override
    public void allSoundOff() {
        try {
            receiver.send(new ShortMessage(ShortMessage.CONTROL_CHANGE | channel, 120, 0), 0);
        } catch (final InvalidMidiDataException ignored) {
        }
    }

    @Override
    public void resetAllControllers() {
        try {
            receiver.send(new ShortMessage(ShortMessage.CONTROL_CHANGE | channel, 121, 0), 0);
        } catch (final InvalidMidiDataException ignored) {
        }
    }

    @Override
    public void noteOff(final int noteNumber) {
        if (voiceStatus.active && voiceStatus.note == noteNumber) {
            voiceStatus.active = false;
        }

        try {
            receiver.send(new ShortMessage(ShortMessage.NOTE_OFF | channel, noteNumber, 0), 0);
        } catch (final InvalidMidiDataException ignored) {
        }
    }

    @Override
    public void noteOff(final int noteNumber, final int velocity) {
        // treat note state as monophonic
        if (voiceStatus.active && voiceStatus.note == noteNumber) {
            voiceStatus.active = false;
        }

        try {
            receiver.send(new ShortMessage(ShortMessage.NOTE_OFF | channel, noteNumber, velocity), 0);
        } catch (final InvalidMidiDataException ignored) {
        }
    }

    @Override
    public void noteOn(final int noteNumber, final int velocity) {
        // treat note state as monophonic
        if (!voiceStatus.active) {
            voiceStatus.note = noteNumber;
            voiceStatus.volume = velocity;
            voiceStatus.active = true;
        }

        try {
            receiver.send(new ShortMessage(ShortMessage.NOTE_ON | channel, noteNumber, velocity), 0);
        } catch (final InvalidMidiDataException ignored) {
        }
    }

    @Override
    public int getChannelPressure() {
        return channelPressure;
    }

    @Override
    public void setChannelPressure(final int pressure) {
        channelPressure = pressure;

        try {
            receiver.send(new ShortMessage(ShortMessage.CHANNEL_PRESSURE | channel, pressure >> 7, pressure & 0x7f), 0);
        } catch (final InvalidMidiDataException ignored) {
        }
    }

    @Override
    public int getController(final int controller) {
        return this.controller.get(controller, 0);
    }

    @Override
    public void controlChange(final int controller, final int value) {
        this.controller.put(controller, value);

        try {
            receiver.send(new ShortMessage(ShortMessage.CONTROL_CHANGE | channel, controller, value), 0);
        } catch (final InvalidMidiDataException ignored) {
        }
    }

    @Override
    public int getProgram() {
        return program;
    }

    @Override
    public void programChange(final int program) {
        this.program = program;

        try {
            receiver.send(new ShortMessage(ShortMessage.PROGRAM_CHANGE | channel, program, 0), 0);
        } catch (final InvalidMidiDataException ignored) {
        }
    }

    @Override
    public void programChange(final int bank, final int program) {
        this.program = program;
        voiceStatus.bank = bank;
        voiceStatus.program = program;

        try {
            receiver.send(new ShortMessage(ShortMessage.CONTROL_CHANGE | channel, 0, bank >> 7), 0);
            receiver.send(new ShortMessage(ShortMessage.CONTROL_CHANGE | channel, 32, bank & 0x7f), 0);
            receiver.send(new ShortMessage(ShortMessage.PROGRAM_CHANGE | channel, program, 0), 0);
        } catch (final InvalidMidiDataException ignored) {
        }
    }

    @Override
    public boolean getMono() {
        return mono;
    }

    @Override
    public void setMono(final boolean on) {
        mono = on;

        if (mono) {
            try {
                receiver.send(new ShortMessage(ShortMessage.CONTROL_CHANGE | channel, 126, 0), 0);
            } catch (final InvalidMidiDataException ignored) {
            }
        } else {
            try {
                receiver.send(new ShortMessage(ShortMessage.CONTROL_CHANGE | channel, 127, 0), 0);
            } catch (final InvalidMidiDataException ignored) {
            }
        }
    }

    @Override
    public boolean getMute() {
        return mute;
    }

    @Override
    public void setMute(final boolean mute) {
        this.mute = mute;
    }

    @Override
    public boolean getOmni() {
        return omni;
    }

    @Override
    public void setOmni(final boolean on) {
        omni = on;

        if (omni) {
            try {
                receiver.send(new ShortMessage(ShortMessage.CONTROL_CHANGE | channel, 125, 0), 0);
            } catch (final InvalidMidiDataException ignored) {
            }
        } else {
            try {
                receiver.send(new ShortMessage(ShortMessage.CONTROL_CHANGE | channel, 124, 0), 0);
            } catch (final InvalidMidiDataException ignored) {
            }
        }
    }

    @Override
    public int getPitchBend() {
        return pitchbend;
    }

    @Override
    public void setPitchBend(final int bend) {
        pitchbend = bend;

        try {
            receiver.send(new ShortMessage(ShortMessage.PITCH_BEND | channel, bend >> 7, bend & 0x7f), 0);
        } catch (final InvalidMidiDataException ignored) {
        }
    }

    @Override
    public int getPolyPressure(final int noteNumber) {
        return polyPressure.get(noteNumber, 0);
    }

    @Override
    public void setPolyPressure(final int noteNumber, final int pressure) {
        polyPressure.put(noteNumber, pressure);

        try {
            receiver.send(new ShortMessage(ShortMessage.PITCH_BEND | channel, pressure >> 7, pressure & 0x7f), 0);
        } catch (final InvalidMidiDataException ignored) {
        }
    }

    @Override
    public boolean getSolo() {
        return solo;
    }

    @Override
    public void setSolo(final boolean soloState) {
        solo = soloState;
    }

    @Override
    public boolean localControl(final boolean on) {
        localControl = on;

        if (localControl) {
            try {
                receiver.send(new ShortMessage(ShortMessage.CONTROL_CHANGE | channel, 122, 127), 0);
            } catch (final InvalidMidiDataException ignored) {
            }
        } else {
            try {
                receiver.send(new ShortMessage(ShortMessage.CONTROL_CHANGE | channel, 122, 0), 0);
            } catch (final InvalidMidiDataException ignored) {
            }
        }

        return localControl;
    }
}
