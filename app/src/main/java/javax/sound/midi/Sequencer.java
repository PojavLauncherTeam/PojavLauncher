package javax.sound.midi;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.EventListener;

/**
 * Interface for MIDI Sequencer
 * 
 * @author K.Shoji
 */
public interface Sequencer extends MidiDevice {

	/**
	 * Loop eternally.
	 * 
	 * @see Sequencer#setLoopCount(int)
	 */
    int LOOP_CONTINUOUSLY = -1;

    /**
     * {@link Sequencer}'s Synchronization mode
     * 
     * @author K.Shoji
     */
    class SyncMode {
		public static final SyncMode INTERNAL_CLOCK = new SyncMode("Internal Clock");
        public static final SyncMode NO_SYNC = new SyncMode("No Sync");

        private final String name;

        protected SyncMode(@NonNull final String name) {
            this.name = name;
        }
        
        @Override
        public final boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
            	return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final SyncMode other = (SyncMode) obj;
            if (!name.equals(other.name)) {
                return false;
            }
            return true;
        }

        @Override
        public final int hashCode() {
            final int PRIME = 31;
            int result = super.hashCode();
            result = PRIME * result + name.hashCode();
            return result;
        }

        @Override
        public final String toString() {
            return name;
        }
    }
    
    /**
     * Get the available {@link SyncMode} for master.
     * 
     * @return the available {@link SyncMode} for master.
     */
    @NonNull
    Sequencer.SyncMode[] getMasterSyncModes();

    /**
     * Get the {@link SyncMode} for master.
     * 
     * @return the {@link SyncMode} for master.
     */
    @NonNull
    Sequencer.SyncMode getMasterSyncMode();

    /**
     * Set the {@link SyncMode} for master.
     * 
     * @param sync the {@link SyncMode} for master.
     */
    void setMasterSyncMode(@NonNull Sequencer.SyncMode sync);
    
    /**
     * Get the available {@link SyncMode} for slave.
     * 
     * @return the available {@link SyncMode} for slave.
     */
    @NonNull
    Sequencer.SyncMode[] getSlaveSyncModes();

    /**
     * Get the {@link SyncMode} for slave.
     * 
     * @return the {@link SyncMode} for slave.
     */
    @NonNull
    Sequencer.SyncMode getSlaveSyncMode();
    
    /**
     * Set the {@link SyncMode} for slave.
     * @param sync the {@link SyncMode} for slave.
     */
    void setSlaveSyncMode(@NonNull Sequencer.SyncMode sync);

    /**
     * Get the {@link Sequence}
     * 
     * @return the {@link Sequence}
     */
    @Nullable
    Sequence getSequence();
   
    /**
     * Load a {@link Sequence} from stream.
     * 
     * @param stream sequence source
     * @throws IOException
     * @throws InvalidMidiDataException
     */
    void setSequence(@NonNull InputStream stream) throws IOException, InvalidMidiDataException;

    /**
     * Set the {@link Sequence} for the {@link Sequencer}
     * 
     * @param sequence the {@link Sequence}
     * @throws InvalidMidiDataException
     */
    void setSequence(@Nullable Sequence sequence) throws InvalidMidiDataException;

    /**
     * Add {@link EventListener} for {@link ShortMessage#CONTROL_CHANGE}
     * 
     * @param listener event listener
     * @param controllers controller codes
     * @return int[] registered controllers for the specified listener
     */
    @NonNull
    int[] addControllerEventListener(@NonNull ControllerEventListener listener, @NonNull int[] controllers);

    /**
     * Remove {@link EventListener} for {@link ShortMessage#CONTROL_CHANGE}
     * 
     * @param listener event listener
     * @param controllers controller codes
     * @return int[] registered controllers for the specified listener
     */
    @NonNull
    int[] removeControllerEventListener(@NonNull ControllerEventListener listener, @NonNull int[] controllers);
    
    /**
     * Add {@link EventListener} for {@link MetaMessage}
     * 
     * @param listener event listener
     * @return true if registered successfully
     */
    boolean addMetaEventListener(@NonNull MetaEventListener listener);

    /**
     * Remove {@link EventListener} for {@link MetaMessage}
     * 
     * @param listener event listener
     */
    void removeMetaEventListener(@NonNull MetaEventListener listener);
    
    /**
     * Get if the {@link Sequencer} is recording.
     * 
     * @return true if the {@link Sequencer} is recording
     */
    boolean isRecording();

    /**
     * Get if the {@link Sequencer} is playing OR recording.
     * 
     * @return true if the {@link Sequencer} is playing OR recording
     */
    boolean isRunning();

    /**
     * Set the {@link Track} to disable recording
     * 
     * @param track the {@link Track} to disable recording
     */
    void recordDisable(@Nullable Track track);

    /***
     * Set the {@link Track} to enable recording on the specified channel.
     * 
     * @param track the {@link Track}
     * @param channel the channel, 0-15
     */
    void recordEnable(@NonNull Track track, int channel);
    
    /**
     * Get the count of loop.
     * 
     * @return the count of loop
     * <ul>
     * <li>{@link #LOOP_CONTINUOUSLY}: play loops eternally</li>
     * <li>0: play once(no loop)</li>
     * <li>1: play twice(loop once)</li>
     * </ul>
     */
    int getLoopCount();

    /**
     * Set count of loop.
     * 
     * @param count
     * <ul>
     * <li>{@link #LOOP_CONTINUOUSLY}: play loops eternally</li>
     * <li>0: play once(no loop)</li>
     * <li>1: play twice(loop once)</li>
     * </ul>
     */
    void setLoopCount(int count);
    
    /**
     * Get start point(ticks) of loop.
     * 
     * @return ticks
     */
    long getLoopStartPoint();
    
    /**
     * Set start point(ticks) of loop.
     * 
     * @param tick 0: start of {@link Sequence}
     */
    void setLoopStartPoint(long tick);

    /**
     * Get the end point(ticks) of loop.
     * 
     * @return the end point(ticks) of loop
     */
    long getLoopEndPoint();
    
    /**
     * Set end point(ticks) of loop.
     * 
     * @param tick -1: end of {@link Sequence}
     */
    void setLoopEndPoint(long tick);

    /**
     * Get the tempo factor.
     * 
     * @return the tempo factor
     */
    float getTempoFactor();
    
    /**
     * Set the tempo factor. This method don't change {@link Sequence}'s tempo.
     * 
     * @param factor 
     * <ul>
     * <li>1.0f : the normal tempo</li>
     * <li>0.5f : half slow tempo</li>
     * <li>2.0f : 2x fast tempo</li>
     * </ul>
     */
    void setTempoFactor(float factor);

    /**
     * Get the tempo in the Beats per minute.
     *
     * @return the tempo in the Beats per minute.
     */
    float getTempoInBPM();

    /**
     * Set the tempo in the Beats per minute.
     * 
     * @param bpm the tempo in the Beats per minute
     */
    void setTempoInBPM(float bpm);

    /**
     * Get the tempos in the microseconds per quarter note.
     *
     * @return the tempos in the microseconds per quarter note
     */
    float getTempoInMPQ();

    /**
     * Set the tempos in the microseconds per quarter note.
     * 
     * @param mpq the tempos in the microseconds per quarter note
     */
    void setTempoInMPQ(float mpq);

    /**
     * Get the {@link Sequence} length in ticks.
     * 
     * @return the {@link Sequence} length in ticks
     */
    long getTickLength();

    /**
     * Get the {@link Sequence} length in microseconds.
     * 
     * @return the {@link Sequence} length in microseconds
     */
    long getMicrosecondLength();
    
    /**
     * Get the current tick position.
     * 
     * @return the current tick position
     */
    long getTickPosition();
    
    /**
     * Set the current tick position.
     * 
     * @param tick the current tick position
     */
    void setTickPosition(long tick);

    /**
     * Get the current microsecond position.
     */
    @Override
    long getMicrosecondPosition();

    /**
     * Set the current microsecond position.
     * 
     * @param microseconds the current microsecond position
     */
    void setMicrosecondPosition(long microseconds);

    /**
     * Get if the track is mute on the playback.
     *
     * @param track the track number
     * @return true if the track is mute on the playback
     */
    boolean getTrackMute(int track);
    
    /**
     * Set the track to mute on the playback.
     * 
     * @param track the track number
     * @param mute true to set mute the track
     */
    void setTrackMute(int track, boolean mute);

    /**
     * Get if the track is solo on the playback.
     *
     * @param track the track number
     * @return true if the track is solo on the playback.
     */
    boolean getTrackSolo(int track);
    
    /**
     * Set track to solo on the playback.
     * 
     * @param track the track number
     * @param solo true to set solo the track
     */
    void setTrackSolo(int track, boolean solo);

    /**
     * Start playing (starting at current sequencer position)
     */
    void start();

    /**
     * Start recording (starting at current sequencer position)
     * 
     * Current {@link Sequence}'s events are sent to the all {@link Transmitter}.
     * Received events art also sent to the all {@link Transmitter}.
     */
    void startRecording();

    /**
     * Stop playing AND recording.
     */
    void stop();

    /**
     * Stop recording. Playing continues.
     */
    void stopRecording();
}
