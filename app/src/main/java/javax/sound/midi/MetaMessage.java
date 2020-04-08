package javax.sound.midi;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Arrays;

/**
 * Represents MIDI Meta Message
 * 
 * @author K.Shoji
 */
public class MetaMessage extends MidiMessage {
	public static final int META = 0xff;
	
	public static final int TYPE_END_OF_TRACK = 0x2f;
	public static final int TYPE_TEMPO = 0x51;

	private static final byte[] defaultMessage = { (byte) META, 0, 0 };
	private static final byte[] emptyData = {};

	private int dataLength = 0;

	/**
	 * Constructor with default message
	 */
	public MetaMessage() {
		this(defaultMessage);
	}

	/**
	 * Constructor with raw data
	 * 
	 * @param data the data source with META header(2 bytes) + length( > 1 byte), the data.length must be >= 3 bytes
     * @throws NegativeArraySizeException MUST be caught. We can't throw {@link InvalidMidiDataException} because of API compatibility.
	 */
	protected MetaMessage(@NonNull final byte[] data) {
		super(data);

		if (data.length >= 3) {
			// check length
			dataLength = data.length - 3;
			int pos = 2;
			while (pos < data.length && (data[pos] & 0x80) != 0) {
				dataLength--;
				pos++;
			}
		}

        if (dataLength < 0) {
            // 'dataLength' may negative value. Negative 'dataLength' will throw NegativeArraySizeException when getData() called.
            throw new NegativeArraySizeException("Invalid meta event. data: " + Arrays.toString(data));
        }
	}

    /**
     * Constructor with the entire information of message
     *
     * @param type the data type
     * @param data the data source
     * @param length unused parameter. Use always data.length
     * @throws InvalidMidiDataException
     */
    public MetaMessage(final int type, @Nullable final byte[] data, final int length) throws InvalidMidiDataException {
        super(null);
        setMessage(type, data, length);
    }

	/**
	 * Set the entire information of message.
	 * 
	 * @param type the data type 0-127
	 * @param data the data source
	 * @param length unused parameter. Use always data.length
	 * @throws InvalidMidiDataException
	 */
	public void setMessage(final int type, @Nullable final byte[] data, final int length) throws InvalidMidiDataException {
		if (type >= 128 || type < 0) {
			throw new InvalidMidiDataException("Invalid meta event. type: " + type);
		}

		final byte[] newData;
		if (data == null) {
			newData = emptyData;
		} else {
			newData = data;
		}

        final int headerLength = 2 + getMidiValuesLength(newData.length);
        this.dataLength = newData.length;
        this.data = new byte[headerLength + newData.length];
        this.length = this.data.length;

        // Write header
		this.data[0] = (byte) META;
		this.data[1] = (byte) type;

        // Write data length
		writeMidiValues(this.data, 2, newData.length);

        // Write data
		if (newData.length > 0) {
			net.kdt.pojavlaunch.SystemCrackResolver.arraycopy(newData, 0, this.data, headerLength, newData.length);
		}
	}

	/**
	 * Get the type of {@link MetaMessage}
	 * 
	 * @return the type
	 */
	public int getType() {
		if (data != null && data.length >= 2) {
			return data[1] & 0xff;
		}
		return 0;
	}

	/**
	 * Get the data of {@link MetaMessage}
	 * 
	 * @return the data without header(`META`, type, data length)
	 */
    @NonNull
    public byte[] getData() {
		if (data == null) {
			return emptyData;
		}

		final byte[] returnedArray = new byte[dataLength];
		net.kdt.pojavlaunch.SystemCrackResolver.arraycopy(data, data.length - dataLength, returnedArray, 0, dataLength);
		return returnedArray;
	}

    @SuppressWarnings("CloneDoesntCallSuperClone")
	@NonNull
	@Override
	public Object clone() {
		if (data == null) {
			return new MetaMessage(emptyData);
		}
		final byte[] result = new byte[data.length];
		net.kdt.pojavlaunch.SystemCrackResolver.arraycopy(data, 0, result, 0, data.length);
		return new MetaMessage(result);
	}

    /**
     * Get the data length for the specified value
     *
     * @param value the value to write
     * @return the data length
     */
	private static int getMidiValuesLength(final long value) {
		int length = 0;
		long currentValue = value;
		do {
            currentValue >>= 7;
			length++;
		} while (currentValue > 0);
		return length;
	}

    /**
     * Write the MIDI value to the data
     *
     * @param data output byte array
     * @param offset the offset
     * @param value the value to write
     */
	private static void writeMidiValues(@NonNull final byte[] data, final int offset, final long value) {
		int shift = 63;
		while ((shift > 0) && ((value & (0x7f << shift)) == 0)) {
			shift -= 7;
		}
		int currentOffset = offset;
		while (shift > 0) {
			data[currentOffset++] = (byte) (((value & (0x7f << shift)) >> shift) | 0x80);
			shift -= 7;
		}
		data[currentOffset] = (byte) (value & 0x7f);
	}
}
