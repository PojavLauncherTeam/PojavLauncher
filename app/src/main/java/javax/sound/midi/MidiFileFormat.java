package javax.sound.midi;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents MIDI File Format
 * 
 * @author K.Shoji
 */
public class MidiFileFormat {
	public static final int HEADER_MThd = 0x4d546864;
	public static final int HEADER_MTrk = 0x4d54726b;

	public static final int UNKNOWN_LENGTH = -1;

	protected int byteLength;
	protected float divisionType;
	protected long microsecondLength;
	protected int resolution;
	protected int type;
	private final AbstractMap<String, Object> properties;

    /**
     * Constructor without properties
     *
     * @param type 0(SMF 0), or 1(SMF 1)
     * @param divisionType {@link Sequence#PPQ}, {@link Sequence#SMPTE_24}, {@link Sequence#SMPTE_25}, {@link Sequence#SMPTE_30DROP}, or {@link Sequence#SMPTE_30}.
     * @param resolution
     * <ul>
     * 	<li>divisionType == {@link Sequence#PPQ} : 0 - 0x7fff. typically 24, 480</li>
     * 	<li>divisionType == {@link Sequence#SMPTE_24}, {@link Sequence#SMPTE_25}, {@link Sequence#SMPTE_30DROP}, {@link Sequence#SMPTE_30} : 0 - 0xff</li>
     * </ul>
     * @param bytes the length of file
     * @param microseconds the length of time(in micro seconds)
     */
	public MidiFileFormat(final int type, final float divisionType, final int resolution, final int bytes, final long microseconds) {
		this.type = type;
		this.divisionType = divisionType;
		this.resolution = resolution;
		this.byteLength = bytes;
		this.microsecondLength = microseconds;
		this.properties = new HashMap<String, Object>();
	}

    /**
     * Constructor with properties
     *
     * @param type 0(SMF 0), or 1(SMF 1)
     * @param divisionType {@link Sequence#PPQ}, {@link Sequence#SMPTE_24}, {@link Sequence#SMPTE_25}, {@link Sequence#SMPTE_30DROP}, or {@link Sequence#SMPTE_30}.
     * @param resolution
     * <ul>
     * 	<li>divisionType == {@link Sequence#PPQ} : 0 - 0x7fff. typically 24, 480</li>
     * 	<li>divisionType == {@link Sequence#SMPTE_24}, {@link Sequence#SMPTE_25}, {@link Sequence#SMPTE_30DROP}, {@link Sequence#SMPTE_30} : 0 - 0xff</li>
     * </ul>
     * @param bytes the length of file
     * @param microseconds the length of time(in micro seconds)
     * @param properties the properties
     */
	public MidiFileFormat(final int type, final float divisionType, final int resolution, final int bytes, final long microseconds, @NonNull final Map<String, Object> properties) {
        this(type, divisionType, resolution, bytes, microseconds);
		this.properties.putAll(properties);
	}

	/**
	 * Get the length of {@link MidiFileFormat}
	 * 
	 * @return the length
	 */
	public int getByteLength() {
		return byteLength;
	}

	/**
	 * Get the division type of {@link MidiFileFormat}
	 * 
	 * @return the division type
	 */
	public float getDivisionType() {
		return divisionType;
	}

	/**
	 * Get the length in microseconds of {@link MidiFileFormat}
	 * 
	 * @return the length in microseconds
	 */
	public long getMicrosecondLength() {
		return microsecondLength;
	}

	/**
	 * Get the property of {@link MidiFileFormat}
	 * 
	 * @param key the property name
	 * @return the property
	 */
    @Nullable
    public Object getProperty(final String key) {
		return properties.get(key);
	}

	/**
	 * Get the resolution of {@link MidiFileFormat}
	 * 
	 * @return the resolution
	 */
	public int getResolution() {
		return resolution;
	}

	/**
	 * Get the type of {@link MidiFileFormat}
	 * 
	 * @return the type
	 */
	public int getType() {
		return type;
	}

	/**
	 * Get properties {@link Map} of {@link MidiFileFormat}
	 * 
	 * @return properties {@link Map}
	 */
    @NonNull
    public Map<String, Object> properties() {
		return Collections.unmodifiableMap(properties);
	}
}
