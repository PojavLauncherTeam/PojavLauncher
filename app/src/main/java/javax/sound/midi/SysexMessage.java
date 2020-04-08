package javax.sound.midi;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Represents MIDI SysEx Message
 * 
 * @author K.Shoji
 */
public class SysexMessage extends MidiMessage {

	/**
	 * Default constructor.
	 */
	public SysexMessage() {
		this(new byte[] { (byte) (ShortMessage.START_OF_EXCLUSIVE & 0xff), (byte) (ShortMessage.END_OF_EXCLUSIVE & 0xff) });
	}

	/**
	 * Constructor with raw data.
	 * 
	 * @param data the SysEx data
	 */
	protected SysexMessage(@NonNull byte[] data) {
		super(data);
	}

    /**
     * Constructor with raw data and length.
     *
     * @param data the SysEx data
     * @param length the data length
     * @throws InvalidMidiDataException
     */
	public SysexMessage(@NonNull final byte[] data, final int length) throws InvalidMidiDataException {
		super(null);
		setMessage(data, length);
	}

    /**
     * Constructor with raw data and length.
     *
     * @param status must be ShortMessage.START_OF_EXCLUSIVE or ShortMessage.END_OF_EXCLUSIVE
     * @param data the SysEx data
     * @param length unused parameter. Use always data.length
     * @throws InvalidMidiDataException
     */
    public SysexMessage(final int status, @NonNull final byte[] data, final int length) throws InvalidMidiDataException {
        super(null);
        setMessage(status, data, length);
    }

	@Override
	public void setMessage(@Nullable final byte[] data, final int length) throws InvalidMidiDataException {
        if (data == null) {
            throw new InvalidMidiDataException("SysexMessage data is null");
        }

		final int status = data[0] & 0xff;
		if ((status != ShortMessage.START_OF_EXCLUSIVE) && (status != ShortMessage.END_OF_EXCLUSIVE)) {
			throw new InvalidMidiDataException("Invalid status byte for SysexMessage: 0x" + Integer.toHexString(status));
		}
		super.setMessage(data, length);
	}

	/**
	 * Set the entire information of message.
	 * 
	 * @param status must be ShortMessage.START_OF_EXCLUSIVE or ShortMessage.END_OF_EXCLUSIVE
	 * @param data the SysEx data
	 * @param length unused parameter. Use always data.length
	 * @throws InvalidMidiDataException
	 */
	public void setMessage(final int status, @NonNull final byte[] data, final int length) throws InvalidMidiDataException {
		if ((status != ShortMessage.START_OF_EXCLUSIVE) && (status != ShortMessage.END_OF_EXCLUSIVE)) {
			throw new InvalidMidiDataException("Invalid status byte for SysexMessage: 0x" + Integer.toHexString(status));
		}

		// extend 1 byte
		this.data = new byte[data.length + 1];
        this.length = this.data.length;

		this.data[0] = (byte) (status & 0xff);
		if (data.length > 0) {
			net.kdt.pojavlaunch.SystemCrackResolver.arraycopy(data, 0, this.data, 1, data.length);
		}
	}

	/**
	 * Get the SysEx data.
	 * 
	 * @return SysEx data
	 */
    @NonNull
    public byte[] getData() {
		final byte[] result = new byte[data.length];
		net.kdt.pojavlaunch.SystemCrackResolver.arraycopy(data, 0, result, 0, result.length);
		return result;
	}

	@Override
	public Object clone() {
        return new SysexMessage(getData());
	}
}
