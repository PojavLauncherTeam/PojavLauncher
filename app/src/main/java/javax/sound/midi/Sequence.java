package javax.sound.midi;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Vector;

/**
 * Represents MIDI Sequence
 *
 * @author K.Shoji
 */
public class Sequence {
	public static final float PPQ = 0.0f;
	public static final float SMPTE_24 = 24.0f;
	public static final float SMPTE_25 = 25.0f;
	public static final float SMPTE_30 = 30.0f;
	public static final float SMPTE_30DROP = 29.969999313354492f;

	protected float divisionType;
	protected int resolution;
	protected Vector<Track> tracks;

	private static final float[] SUPPORTING_DIVISION_TYPES = {PPQ, SMPTE_24, SMPTE_25, SMPTE_30, SMPTE_30DROP};

	/**
	 * Check if the divisionType supported
	 * @param divisionType the divisionType
	 * @return true if the specified divisionType is supported
	 */
	private static boolean isSupportingDivisionType(final float divisionType) {
		for (final float supportingDivisionType : SUPPORTING_DIVISION_TYPES) {
			if (divisionType == supportingDivisionType) {
				return true;
			}
		}
		return false;
	}


	/**
	 * Create {@link Sequence} with divisionType and resolution.
	 * 
	 * @param divisionType {@link #PPQ}, {@link #SMPTE_24}, {@link #SMPTE_25}, {@link #SMPTE_30DROP}, or {@link #SMPTE_30}.
	 * @param resolution
	 * <ul>
	 * 	<li>divisionType == {@link #PPQ} : 0 - 0x7fff. typically 24, 480</li>
	 * 	<li>divisionType == {@link #SMPTE_24}, {@link #SMPTE_25}, {@link #SMPTE_30DROP}, {@link #SMPTE_30} : 0 - 0xff</li>
	 * </ul>
	 * @throws InvalidMidiDataException
	 */
	public Sequence(final float divisionType, final int resolution) throws InvalidMidiDataException {
		if (isSupportingDivisionType(divisionType) == false) {
			throw new InvalidMidiDataException("Unsupported division type: " + divisionType);
		}
		this.divisionType = divisionType;
		this.resolution = resolution;
		this.tracks = new Vector<Track>();
	}

	/**
	 * Create {@link Sequence} with divisionType, resolution and numberOfTracks.
	 * 
	 * @param divisionType {@link #PPQ}, {@link #SMPTE_24}, {@link #SMPTE_25}, {@link #SMPTE_30DROP}, or {@link #SMPTE_30}.
	 * @param resolution
	 * <ul>
	 * 	<li>divisionType == {@link #PPQ} : 0 - 0x7fff. typically 24, 480</li>
	 * 	<li>divisionType == {@link #SMPTE_24}, {@link #SMPTE_25}, {@link #SMPTE_30DROP}, {@link #SMPTE_30} : 0 - 0xff</li>
	 * </ul>
	 * @param numberOfTracks > 0
	 * @throws InvalidMidiDataException
	 */
	public Sequence(final float divisionType, final int resolution, final int numberOfTracks) throws InvalidMidiDataException {
        this(divisionType, resolution);

		if (numberOfTracks > 0) {
			for (int i = 0; i < numberOfTracks; i++) {
				tracks.add(new Track());
			}
		}
	}

	/**
	 * Create an empty {@link Track}
	 * 
	 * @return an empty {@link Track}
	 */
    @NonNull
    public Track createTrack() {
		/*
		 * new Tracks accrue to the end of vector
		 */
		final Track track = new Track();
		tracks.add(track);
		return track;
	}

	/**
	 * Delete specified {@link Track}
	 * 
	 * @param track to delete
	 * @return true if the track is successfully deleted
	 */
	public boolean deleteTrack(@Nullable final Track track) {
		return tracks.remove(track);
	}

	/**
	 * Get the divisionType of the {@link Sequence}
	 * 
	 * @return the divisionType of the {@link Sequence}
	 */
	public float getDivisionType() {
		return divisionType;
	}

	/**
	 * Get the {@link Sequence} length in microseconds
	 * 
	 * @return the {@link Sequence} length in microseconds
	 */
	public long getMicrosecondLength() {
		return (long) (1000000.0f * getTickLength() / ((this.divisionType == 0.0f ? 2 : this.divisionType) * this.resolution * 1.0f));
	}

	/**
	 * Get the resolution
	 * 
	 * @return the resolution
	 */
	public int getResolution() {
		return resolution;
	}

	/**
	 * Get the biggest tick length
	 * 
	 * @return tick length
	 */
	public long getTickLength() {
		/*
		 * this method return the biggest value of tick of all tracks contain in the Sequence
		 */
		long maxTick = 0;
		for (int i = 0; i < tracks.size(); i++) {
			maxTick = Math.max(maxTick, tracks.get(i).ticks());
		}
		return maxTick;
	}

	/**
	 * Get the array of {@link Track}s
     *
	 * @return array of tracks
	 */
    @NonNull
    public Track[] getTracks() {
		final Track[] track = new Track[tracks.size()];
		tracks.toArray(track);
		return track;
	}

    /**
     * Get list of {@link Patch}es used in this Sequence.
     *
     * @return empty array(not implemented)
     */
    @NonNull
    public Patch[] getPatchList() {
        return new Patch[] {};
    }
}
