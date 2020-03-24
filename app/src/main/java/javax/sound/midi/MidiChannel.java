package javax.sound.midi;

/**
 * Interface for MIDI Channel
 *
 * @author K.Shoji
 */
public interface MidiChannel {

    /**
     * Indicate 'All notes off' message to the {@link MidiChannel}
     */
    void allNotesOff();

    /**
     * Indicate 'All sound off' message to the {@link MidiChannel}
     */
    void allSoundOff();

    /**
     * Indicate 'Reset all controllers' message to the {@link MidiChannel}
     */
    void resetAllControllers();

    /**
     * Indicate 'Note off' message to the {@link MidiChannel}
     *
     * @param noteNumber the note number
     */
    void noteOff(int noteNumber);

    /**
     * Indicate 'Note off' message to the {@link MidiChannel}
     *
     * @param noteNumber the note number
     * @param velocity the note off velocity
     */
    void noteOff(int noteNumber, int velocity);

    /**
     * Indicate 'Note on' message to the {@link MidiChannel}
     *
     * @param noteNumber the note number
     * @param velocity the note on velocity
     */
    void noteOn(int noteNumber, int velocity);

    /**
     * Get 'Channel pressure' status of the {@link MidiChannel}
     *
     * @return 'Channel pressure' status
     */
    int getChannelPressure();

    /**
     * Set 'Channel pressure' status to the {@link MidiChannel}
     *
     * @param pressure the channel pressure
     */
    void setChannelPressure(int pressure);

    /**
     * Get 'Controller' status of the {@link MidiChannel}
     *
     * @param controller the controller ID
     * @return 'Controller' status
     */
    int getController(int controller);

    /**
     * Set 'Controller' status to the {@link MidiChannel}
     *
     * @param controller the controller ID
     * @param value the controller value
     */
    void controlChange(int controller, int value);

    /**
     * Get 'Program' status of the {@link MidiChannel}
     *
     * @return 'Program' status
     */
    int getProgram();

    /**
     * Set 'Program' status to the {@link MidiChannel}
     *
     * @param program the program
     */
    void programChange(int program);

    /**
     * Set 'Bank' and 'Program' status to the {@link MidiChannel}
     *
     * @param bank the bank
     * @param program the program
     */
    void programChange(int bank, int program);

    /**
     * Get 'Mono' status of the {@link MidiChannel}
     *
     * @return 'Mono' status
     */
    boolean getMono();

    /**
     * Set 'Mono' status to the {@link MidiChannel}
     *
     * @param on 'Mono' status
     */
    void setMono(boolean on);

    /**
     * Get 'Mute' status of the {@link MidiChannel}
     *
     * @return 'Mute' status
     */
    boolean getMute();

    /**
     * Set 'Mute' status to the {@link MidiChannel}
     *
     * @param mute 'Mute' status
     */
    void setMute(boolean mute);

    /**
     * Get 'Omni' status of the {@link MidiChannel}
     *
     * @return 'Omni' status
     */
    boolean getOmni();

    /**
     * Set 'Omni' status to the {@link MidiChannel}
     *
     * @param on 'Omni' status
     */
    void setOmni(boolean on);

    /**
     * Get 'Pitch bend' status of the {@link MidiChannel}
     *
     * @return 'Pitch bend' status
     */
    int getPitchBend();

    /**
     * Set 'Pitch bend' status to the {@link MidiChannel}
     *
     * @param bend 'Pitch bend' amount 0-8191-16383
     */
    void setPitchBend(int bend);

    /**
     * Get 'Poly pressure' status of the {@link MidiChannel}
     *
     * @param noteNumber the note number
     * @return 'Poly pressure'
     */
    int getPolyPressure(int noteNumber);

    /**
     * Set 'Poly pressure' status to the {@link MidiChannel}
     *
     * @param noteNumber the note number
     * @param pressure the note pressure
     */
    void setPolyPressure(int noteNumber, int pressure);

    /**
     * Get 'Solo' status of the {@link MidiChannel}
     *
     * @return 'Solo' status
     */
    boolean getSolo();

    /**
     * Set 'Solo' status to the {@link MidiChannel}
     *
     * @param soloState 'Solo' status
     */
    void setSolo(boolean soloState);

    /**
     * Set 'Local control' status to the {@link MidiChannel}
     *
     * @param on 'Local control' status
     * @return the new status. If always returns false: not supported on this instance
     */
    boolean localControl(boolean on);
}
