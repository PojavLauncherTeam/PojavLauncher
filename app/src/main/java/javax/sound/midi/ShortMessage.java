package javax.sound.midi;

import android.support.annotation.NonNull;

/**
 * Represents MIDI Short Message
 * 
 * @author K.Shoji
 */
public class ShortMessage extends MidiMessage {
	public static final int NOTE_OFF = 0x80;
	public static final int NOTE_ON = 0x90;
	public static final int POLY_PRESSURE = 0xa0;
	public static final int CONTROL_CHANGE = 0xb0;
	public static final int PROGRAM_CHANGE = 0xc0;
	public static final int CHANNEL_PRESSURE = 0xd0;
	public static final int PITCH_BEND = 0xe0;
	public static final int START_OF_EXCLUSIVE = 0xf0;
	public static final int MIDI_TIME_CODE = 0xf1;
	public static final int SONG_POSITION_POINTER = 0xf2;
	public static final int SONG_SELECT = 0xf3;
	public static final int BUS_SELECT = 0xf5;
	public static final int TUNE_REQUEST = 0xf6;
	public static final int END_OF_EXCLUSIVE = 0xf7;
	public static final int TIMING_CLOCK = 0xf8;
	public static final int START = 0xfa;
	public static final int CONTINUE = 0xfb;
	public static final int STOP = 0xfc;
	public static final int ACTIVE_SENSING = 0xfe;
	public static final int SYSTEM_RESET = 0xff;
	
	public static final int MASK_EVENT = 0xf0;
	public static final int MASK_CHANNEL = 0x0f;

	/**
	 * Default constructor, set up 'note on' message.
	 */
	public ShortMessage() {
		this(new byte[] { (byte) NOTE_ON, 0x40, 0x7f });
	}

	/**
	 * Constructor with raw data.
	 * 
	 * @param data the raw data
	 */
	protected ShortMessage(@NonNull final byte[] data) {
		super(data);
	}

    /**
     * Constructor with the kind of message
     *
     * @param status the status data
     * @throws InvalidMidiDataException
     */
    public ShortMessage(final int status) throws InvalidMidiDataException {
        super(null);
        setMessage(status);
    }

    /**
     * Constructor with the entire information of message
     *
     * @param status the status data
     * @param data1 the first data
     * @param data2 the second data
     * @throws InvalidMidiDataException
     */
    public ShortMessage(final int status, final int data1, final int data2) throws InvalidMidiDataException {
        super(null);
        setMessage(status, data1, data2);
    }

    /**
     * Constructor with the entire information of message
     *
     * @param command the command
     * @param channel the channel
     * @param data1 the first data
     * @param data2 the second data
     * @throws InvalidMidiDataException
     */
    public ShortMessage(final int command, final int channel, final int data1, final int data2) throws InvalidMidiDataException {
        super(null);
        setMessage(command, channel, data1, data2);
    }

	/**
	 * Set the kind of message.
	 *
     * @param status the status data
	 * @throws InvalidMidiDataException
	 */
	public void setMessage(final int status) throws InvalidMidiDataException {
		final int dataLength = getDataLength(status);
		if (dataLength != 0) {
			throw new InvalidMidiDataException("Status byte: " + status + " requires " + dataLength + " data bytes length");
		}
		setMessage(status, 0, 0);
	}

	/**
	 * Set the entire information of message.
	 * 
	 * @param status the status data
     * @param data1 the first data
     * @param data2 the second data
	 * @throws InvalidMidiDataException
	 */
	public void setMessage(final int status, final int data1, final int data2) throws InvalidMidiDataException {
		final int dataLength = getDataLength(status);
		if (dataLength > 0) {
			if (data1 < 0 || data1 > 0x7f) {
				throw new InvalidMidiDataException("data1 out of range: " + data1);
			}
			if (dataLength > 1) {
				if (data2 < 0 || data2 > 0x7f) {
					throw new InvalidMidiDataException("data2 out of range: " + data2);
				}
			}
		}
		
		if (data == null || data.length != dataLength + 1) {
			data = new byte[dataLength + 1];
		}
        length = data.length;

		data[0] = (byte) (status & 0xff);
		if (data.length > 1) {
			data[1] = (byte) (data1 & 0xff);
			if (data.length > 2) {
				data[2] = (byte) (data2 & 0xff);
			}
		}
	}

	/**
	 * Set the entire information of message.
	 * 
	 * @param command the command
	 * @param channel the channel
	 * @param data1 the first data
	 * @param data2 the second data
	 * @throws InvalidMidiDataException
	 */
	public void setMessage(final int command, final int channel, final int data1, final int data2) throws InvalidMidiDataException {
		if (command >= 0xf0 || command < 0x80) {
			throw new InvalidMidiDataException("command out of range: 0x" + Integer.toHexString(command));
		}
		if (channel > 0x0f) {
			throw new InvalidMidiDataException("channel out of range: " + channel);
		}
		setMessage((command & 0xf0) | (channel & 0x0f), data1, data2);
	}

	/**
	 * Get the channel of this message.
	 * 
	 * @return the channel
	 */
	public int getChannel() {
		return (getStatus() & 0x0f);
	}

	/**
	 * Get the kind of command for this message.
	 * 
	 * @return the kind of command
	 */
	public int getCommand() {
		return (getStatus() & 0xf0);
	}

	/**
	 * Get the first data for this message.
	 * 
	 * @return the first data
	 */
	public int getData1() {
		if (data.length > 1) {
			return data[1] & 0xff;
		}
		return 0;
	}

	/**
	 * Get the second data for this message.
	 * 
	 * @return the second data
	 */
	public int getData2() {
		if (data.length > 2) {
			return data[2] & 0xff;
		}
		return 0;
	}

	@Override
	public Object clone() {
		final byte[] result = new byte[data.length];
		net.kdt.pojavlaunch.SystemCrackResolver.arraycopy(data, 0, result, 0, result.length);
		return new ShortMessage(result);
	}

    /**
     * Get data length of MIDI message from MIDI event status
     *
     * @param status MIDI event status
     * @return length of MIDI message
     * @throws InvalidMidiDataException
     */
	protected static int getDataLength(final int status) throws InvalidMidiDataException {
		switch (status) {
			case TUNE_REQUEST:
			case END_OF_EXCLUSIVE:
			case TIMING_CLOCK:
			case 0xf9:
			case START:
			case CONTINUE:
			case STOP:
			case 0xfd:
			case ACTIVE_SENSING:
			case SYSTEM_RESET:
				return 0;
			case MIDI_TIME_CODE:
			case SONG_SELECT:
				return 1;
			case SONG_POSITION_POINTER:
				return 2;
			default:
		}

		switch (status & MASK_EVENT) {
			case NOTE_OFF:
			case NOTE_ON:
			case POLY_PRESSURE:
			case CONTROL_CHANGE:
			case PITCH_BEND:
				return 2;
			case PROGRAM_CHANGE:
			case CHANNEL_PRESSURE:
				return 1;
			default:
				throw new InvalidMidiDataException("Invalid status byte: " + status);
		}
	}
}
