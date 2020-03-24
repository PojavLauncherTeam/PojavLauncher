package javax.sound.midi.io;

import android.support.annotation.NonNull;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiFileFormat;
import javax.sound.midi.Sequence;
import javax.sound.midi.Track;
import javax.sound.midi.spi.MidiFileWriter;

/**
 * The implementation SMF writer
 *
 * @author K.Shoji
 */
public class StandardMidiFileWriter extends MidiFileWriter {

    /**
     * Represents OutputStream for MIDI Data
     *
     * @author K.Shoji
     */
    private static class MidiDataOutputStream extends DataOutputStream {

        /**
         * Constructor
         *
         * @param outputStream the source stream
         */
		public MidiDataOutputStream(@NonNull final OutputStream outputStream) {
			super(outputStream);
		}

        /**
         * Convert the specified value into the value for MIDI data
         *
         * @param value the original value
         * @return the raw data to write
         */
		private static int getValueToWrite(final int value) {
			int result = value & 0x7f;
			int currentValue = value;

			while ((currentValue >>= 7) != 0) {
				result <<= 8;
				result |= ((currentValue & 0x7f) | 0x80);
			}
			return result;
		}

        /**
         * Get the data length for the specified value
         *
         * @param value the value
         * @return the data length
         */
		private static int variableLengthIntLength(final int value) {
			int valueToWrite = getValueToWrite(value);

			int length = 0;
			while (true) {
				length++;

				if ((valueToWrite & 0x80) != 0) {
					valueToWrite >>>= 8;
				} else {
					break;
				}
			}

			return length;
		}

        /**
         * Write the specified value to the OutputStream
         *
         * @param value the value
         * @throws IOException
         */
		private void writeVariableLengthInt(final int value) throws IOException {
			int valueToWrite = getValueToWrite(value);

			while (true) {
				writeByte(valueToWrite & 0xff);

				if ((valueToWrite & 0x80) != 0) {
					valueToWrite >>>= 8;
				} else {
					break;
				}
			}
		}
	}

	@NonNull
    @Override
	public int[] getMidiFileTypes() {
		return new int[] { 0, 1 };
	}

	@NonNull
    @Override
	public int[] getMidiFileTypes(@NonNull final Sequence sequence) {
		if (sequence.getTracks().length > 1) {
			return new int[] { 1 };
		} else {
			return new int[] { 0, 1 };
		}
	}

	@Override
	public int write(@NonNull final Sequence sequence, final int fileType, @NonNull final File file) throws IOException {
		final FileOutputStream fileOutputStream = new FileOutputStream(file);
		final int written = write(sequence, fileType, fileOutputStream);
		fileOutputStream.close();
		return written;
	}

	@Override
	public int write(@NonNull final Sequence sequence, final int fileType, @NonNull final OutputStream outputStream) throws IOException {
		final MidiDataOutputStream midiDataOutputStream = new MidiDataOutputStream(outputStream);

		final Track[] tracks = sequence.getTracks();
		midiDataOutputStream.writeInt(MidiFileFormat.HEADER_MThd);
		midiDataOutputStream.writeInt(6);
		midiDataOutputStream.writeShort(fileType);
		midiDataOutputStream.writeShort(tracks.length);
		
		final float divisionType = sequence.getDivisionType();
		final int resolution = sequence.getResolution();
		int division = 0;
		if (divisionType == Sequence.PPQ) {
			division = resolution & 0x7fff;
		} else if (divisionType == Sequence.SMPTE_24) {
			division = (24 << 8) * -1;
			division += resolution & 0xff;
		} else if (divisionType == Sequence.SMPTE_25) {
			division = (25 << 8) * -1;
			division += resolution & 0xff;
		} else if (divisionType == Sequence.SMPTE_30DROP) {
			division = (29 << 8) * -1;
			division += resolution & 0xff;
		} else if (divisionType == Sequence.SMPTE_30) {
			division = (30 << 8) * -1;
			division += resolution & 0xff;
		}
		midiDataOutputStream.writeShort(division);
		
		int length = 0;
		for (final Track track : tracks) {
			length += writeTrack(track, midiDataOutputStream);
		}
		
		midiDataOutputStream.close();
		return length + 14;
	}

	/**
	 * Write {@link Track} data into {@link MidiDataOutputStream}
	 * 
	 * @param track the track
	 * @param midiDataOutputStream the OutputStream
	 * @return written byte length
	 * @throws IOException
	 */
	private static int writeTrack(@NonNull final Track track, @NonNull final MidiDataOutputStream midiDataOutputStream) throws IOException {
		final int eventCount = track.size();

		// track header
        midiDataOutputStream.writeInt(MidiFileFormat.HEADER_MTrk);

		// calculate the track length
		int trackLength = 0;
		long lastTick = 0;
		MidiEvent midiEvent = null;
		for (int i = 0; i < eventCount; i++) {
            midiEvent = track.get(i);
			final long tick = midiEvent.getTick();
			trackLength += MidiDataOutputStream.variableLengthIntLength((int) (tick - lastTick));
			lastTick = tick;

			trackLength += midiEvent.getMessage().getLength();
		}

        // process End of Track message
		boolean needEndOfTrack = true;
		if (midiEvent != null && (midiEvent.getMessage() instanceof MetaMessage) && //
            ((MetaMessage)midiEvent.getMessage()).getType() == MetaMessage.TYPE_END_OF_TRACK) {
            needEndOfTrack = false;
        } else {
            trackLength += 4; // End of Track
        }
        midiDataOutputStream.writeInt(trackLength);

        // write the track data
		lastTick = 0;
		for (int i = 0; i < eventCount; i++) {
            midiEvent = track.get(i);
            final long tick = midiEvent.getTick();
			midiDataOutputStream.writeVariableLengthInt((int) (tick - lastTick));
			lastTick = tick;
			
			midiDataOutputStream.write(midiEvent.getMessage().getMessage(), 0, midiEvent.getMessage().getLength());
        }

        // write End of Track message if not found.
        if (needEndOfTrack) {
            midiDataOutputStream.writeVariableLengthInt(0);
            midiDataOutputStream.writeByte(MetaMessage.META);
            midiDataOutputStream.writeByte(MetaMessage.TYPE_END_OF_TRACK);
            midiDataOutputStream.writeVariableLengthInt(0);
        }

		return trackLength + 4;
	}
}
