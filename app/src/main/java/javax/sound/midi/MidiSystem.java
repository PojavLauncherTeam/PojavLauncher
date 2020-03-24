package javax.sound.midi;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import javax.sound.midi.MidiDevice.Info;
import javax.sound.midi.impl.SequencerImpl;
import javax.sound.midi.io.StandardMidiFileReader;
import javax.sound.midi.io.StandardMidiFileWriter;

/**
 * MidiSystem porting for Android
 *
 * @author K.Shoji
 */
public final class MidiSystem {
	private static final Collection<MidiDevice> midiDevices = new HashSet<MidiDevice>();
    private static final Collection<Synthesizer> synthesizers = new HashSet<Synthesizer>();
    private static final StandardMidiFileReader standardMidiFileReader = new StandardMidiFileReader();
    private static final StandardMidiFileWriter standardMidiFileWriter = new StandardMidiFileWriter();

    /**
     * Add a {@link javax.sound.midi.MidiDevice} to the {@link javax.sound.midi.MidiSystem}
     *
     * @param midiDevice the device to add
     */
    public static void addMidiDevice(@NonNull final MidiDevice midiDevice) {
        synchronized (midiDevices) {
            midiDevices.add(midiDevice);
        }
    }

    /**
     * Remove a {@link javax.sound.midi.MidiDevice} from the {@link javax.sound.midi.MidiSystem}
     *
     * @param midiDevice the device to remove
     */
    public static void removeMidiDevice(@NonNull final MidiDevice midiDevice) {
        synchronized (midiDevices) {
            midiDevices.remove(midiDevice);
        }
    }

    /**
     * Add a {@link javax.sound.midi.Synthesizer} to the {@link javax.sound.midi.MidiSystem}
     *
     * @param synthesizer the device to add
     */
    public static void addSynthesizer(@NonNull final Synthesizer synthesizer) {
        synchronized (synthesizers) {
            synthesizers.add(synthesizer);
        }
    }

    /**
     * Remove a {@link javax.sound.midi.Synthesizer} from the {@link javax.sound.midi.MidiSystem}
     *
     * @param synthesizer the device to remove
     */
    public static void removeSynthesizer(@NonNull final Synthesizer synthesizer) {
        synchronized (synthesizers) {
            synthesizers.remove(synthesizer);
        }
    }
    /**
	 * Utilities for {@link MidiSystem}
	 *
	 * @author K.Shoji
	 */
	public static class MidiSystemUtils {
		/**
		 * Get currently connected {@link Receiver}s
		 *
		 * @return currently connected {@link Receiver}s
		 * @throws MidiUnavailableException
		 */
        @NonNull
        public static List<Receiver> getReceivers() throws MidiUnavailableException {
			final List<Receiver> result = new ArrayList<Receiver>();
			final Info[] midiDeviceInfos = MidiSystem.getMidiDeviceInfo();
			for (final Info midiDeviceInfo : midiDeviceInfos) {
				result.addAll(MidiSystem.getMidiDevice(midiDeviceInfo).getReceivers());
			}

			return result;
		}

		/**
		 * Get currently connected {@link Transmitter}s
		 *
		 * @return currently connected {@link Transmitter}s
		 * @throws MidiUnavailableException
		 */
        @NonNull
        public static List<Transmitter> getTransmitters() throws MidiUnavailableException {
			final List<Transmitter> result = new ArrayList<Transmitter>();
			final Info[] midiDeviceInfos = MidiSystem.getMidiDeviceInfo();
			for (final Info midiDeviceInfo : midiDeviceInfos) {
				result.addAll(MidiSystem.getMidiDevice(midiDeviceInfo).getTransmitters());
			}

			return result;
		}
	}

    /**
     * Private Constructor; this class can't be instantiated.
     */
	private MidiSystem() {
	}

	/**
	 * Get all connected {@link MidiDevice.Info} as array
	 *
	 * @return device information
	 */
    @NonNull
    public static MidiDevice.Info[] getMidiDeviceInfo() {
		final List<MidiDevice.Info> result = new ArrayList<MidiDevice.Info>();
		synchronized (midiDevices) {
            for (final MidiDevice device : midiDevices) {
                final Info deviceInfo = device.getDeviceInfo();
                if (deviceInfo != null) {
                    result.add(deviceInfo);
                }
            }
		}
		return result.toArray(new MidiDevice.Info[result.size()]);
	}

	/**
	 * Get {@link MidiDevice} by device information
	 *
	 * @param info the device information
	 * @return {@link MidiDevice}
	 * @throws MidiUnavailableException
	 * @throws IllegalArgumentException if the device not found.
	 */
    @NonNull
    public static MidiDevice getMidiDevice(@NonNull final MidiDevice.Info info) throws MidiUnavailableException, IllegalArgumentException {
        if (midiDevices.isEmpty()) {
            throw new MidiUnavailableException("MidiDevice not found");
        }

        synchronized (midiDevices) {
            for (final MidiDevice midiDevice : midiDevices) {
                if (info.equals(midiDevice.getDeviceInfo())) {
                    return midiDevice;
                }
            }
		}

		throw new IllegalArgumentException("Requested device not installed: " + info);
	}

	/**
	 * Get the first detected Receiver
	 *
	 * @return {@link Receiver}
	 * @throws MidiUnavailableException
	 */
    @Nullable
    public static Receiver getReceiver() throws MidiUnavailableException {
        synchronized (midiDevices) {
            for (final MidiDevice midiDevice : midiDevices) {
                final Receiver receiver = midiDevice.getReceiver();
                if (receiver != null) {
                    return receiver;
                }
            }
		}
		throw new MidiUnavailableException("Receiver not found");
	}

	/**
	 * Get the first detected Transmitter
	 *
	 * @return {@link Transmitter}
	 * @throws MidiUnavailableException
	 */
    @Nullable
    public static Transmitter getTransmitter() throws MidiUnavailableException {
        synchronized (midiDevices) {
            for (final MidiDevice midiDevice : midiDevices) {
                final Transmitter transmitter = midiDevice.getTransmitter();
                if (transmitter != null) {
                    return transmitter;
                }
            }
		}
        throw new MidiUnavailableException("Transmitter not found");
	}

	/**
	 * Get a {@link Sequence} from the specified File.
	 *
	 * @param file the SMF
	 * @return the {@link Sequence}
	 * @throws InvalidMidiDataException
	 * @throws IOException
	 */
    @NonNull
    public static Sequence getSequence(@NonNull final File file) throws InvalidMidiDataException, IOException {
		return standardMidiFileReader.getSequence(file);
	}

	/**
	 * Get a {@link Sequence} from the specified input stream.
	 *
	 * @param stream the input stream of SMF
     * @return the {@link Sequence}
	 * @throws InvalidMidiDataException
	 * @throws IOException
	 */
    @NonNull
    public static Sequence getSequence(@NonNull final InputStream stream) throws InvalidMidiDataException, IOException {
		return standardMidiFileReader.getSequence(stream);
	}

	/**
	 * Get a {@link Sequence} from the specified URL.
     *
	 * @param url the URL of SMF
     * @return the {@link Sequence}
	 * @throws InvalidMidiDataException
	 * @throws IOException
	 */
    @NonNull
    public static Sequence getSequence(@NonNull final URL url) throws InvalidMidiDataException, IOException {
		return standardMidiFileReader.getSequence(url);
	}

	/**
	 * Get the default {@link Sequencer}, connected to a default device.
	 *
	 * @return {@link Sequencer} must call the {@link Sequencer#open()} method.
	 * @throws MidiUnavailableException
	 */
    @NonNull
    public static Sequencer getSequencer() throws MidiUnavailableException {
		return new SequencerImpl();
	}

	/**
	 * Get the default {@link Sequencer}, optionally connected to a default device.
	 *
	 * @param connected ignored
	 * @return {@link Sequencer} must call the {@link Sequencer#open()} method.
	 * @throws MidiUnavailableException
	 */
    @NonNull
    public static Sequencer getSequencer(final boolean connected) throws MidiUnavailableException {
		return new SequencerImpl();
	}

    /**
     * Obtain {@link javax.sound.midi.Soundbank} from File<br />
     * not implemented.
     *
     * @param file the Soundbank file
     * @return {@link javax.sound.midi.Soundbank}
     * @throws InvalidMidiDataException
     * @throws IOException
     */
    @NonNull
    public static Soundbank getSoundbank(@NonNull final File file) throws InvalidMidiDataException, IOException {
        throw new UnsupportedOperationException("not implemented.");
    }

    /**
     * Obtain {@link javax.sound.midi.Soundbank} from InputStream<br />
     * not implemented.
     *
     * @param stream the input stream of Soundbank
     * @return {@link javax.sound.midi.Soundbank}
     * @throws InvalidMidiDataException
     * @throws IOException
     */
    @NonNull
    public static Soundbank getSoundbank(@NonNull final InputStream stream) throws InvalidMidiDataException, IOException {
        throw new UnsupportedOperationException("not implemented.");
    }

    /**
     * Obtain {@link javax.sound.midi.Soundbank} from URL<br />
     * not implemented.
     *
     * @param url the URL of Soundbank
     * @return {@link javax.sound.midi.Soundbank}
     * @throws InvalidMidiDataException
     * @throws IOException
     */
    @NonNull
    public static Soundbank getSoundbank(@NonNull final URL url) throws InvalidMidiDataException, IOException {
        throw new UnsupportedOperationException("not implemented.");
    }

    /**
     * Obtain {@link javax.sound.midi.Synthesizer} registered by {@link #registerSynthesizer(Synthesizer)}
     *
     * @return a Synthesizer, null if instance has not registered
     * @throws MidiUnavailableException
     */
    @Nullable
    public static Synthesizer getSynthesizer() throws MidiUnavailableException {
        synchronized (synthesizers) {
            for (final Synthesizer synthesizer : synthesizers) {
                // returns the first one
                return synthesizer;
            }
        }

        throw new MidiUnavailableException("Synthesizer not found");
    }

    /**
     * Register the {@link javax.sound.midi.Synthesizer} instance to the {@link MidiSystem}.
     *
     * @param synthesizer the {@link javax.sound.midi.Synthesizer} instance
     */
    public static void registerSynthesizer(@NonNull final Synthesizer synthesizer) {
        synchronized (synthesizers) {
            synthesizers.add(synthesizer);
        }
    }

	/**
	 * Get the {@link MidiFileFormat} information of the specified File.
	 * 
	 * @param file the SMF
	 * @return the {@link MidiFileFormat} information
	 * @throws InvalidMidiDataException
	 * @throws IOException
	 */
    @NonNull
    public static MidiFileFormat getMidiFileFormat(@NonNull final File file) throws InvalidMidiDataException, IOException {
		return standardMidiFileReader.getMidiFileFormat(file);
	}

	/**
	 * Get the {@link MidiFileFormat} information in the specified input stream.
	 * 
	 * @param stream the the input stream of SMF
     * @return the {@link MidiFileFormat} information
	 * @throws InvalidMidiDataException
	 * @throws IOException
	 */
    @NonNull
    public static MidiFileFormat getMidiFileFormat(@NonNull final InputStream stream) throws InvalidMidiDataException, IOException {
		return standardMidiFileReader.getMidiFileFormat(stream);
	}

	/**
	 * Get the {@link MidiFileFormat} information in the specified URL.
	 * 
	 * @param url the URL of SMF
     * @return the {@link MidiFileFormat} information
	 * @throws InvalidMidiDataException
	 * @throws IOException
	 */
    @NonNull
    public static MidiFileFormat getMidiFileFormat(@NonNull final URL url) throws InvalidMidiDataException, IOException {
		return standardMidiFileReader.getMidiFileFormat(url);
	}

	/**
	 * Get the set of SMF types that the library can write
	 * 
	 * @return the set of SMF types
	 */
    @NonNull
    public static int[] getMidiFileTypes() {
		return standardMidiFileWriter.getMidiFileTypes();
	}

	/**
	 * Get the set of SMF types that the library can write from the {@link Sequence}
	 * 
	 * @param sequence the {@link Sequence}
	 * @return the set of SMF types
	 */
    @NonNull
    public static int[] getMidiFileTypes(@NonNull final Sequence sequence) {
		return standardMidiFileWriter.getMidiFileTypes(sequence);
	}
	
	/**
	 * Check if the specified SMF fileType is available
	 * 
	 * @param fileType the fileType of SMF
	 * @return true if the fileType is available
	 */
	public static boolean isFileTypeSupported(final int fileType) {
		return standardMidiFileWriter.isFileTypeSupported(fileType);
	}

	/**
	 * Check if the specified SMF fileType is available from the {@link Sequence}
	 * 
	 * @param fileType the fileType of {@link Sequence}
	 * @param sequence the {@link Sequence}
     * @return true if the fileType is available
	 */
	public static boolean isFileTypeSupported(final int fileType, @NonNull final Sequence sequence) {
		return standardMidiFileWriter.isFileTypeSupported(fileType, sequence);
	}

	/**
	 * Write sequence to the specified {@link File} as SMF
	 * 
	 * @param sequence the {@link Sequence}
	 * @param fileType the fileType of {@link Sequence}
	 * @param file the {@link File} to write
	 * @return the file length
	 * @throws IOException
	 */
    public static int write(@NonNull final Sequence sequence, final int fileType, @NonNull final File file) throws IOException {
		return standardMidiFileWriter.write(sequence, fileType, file);
	}

	/**
	 * Write sequence to the specified {@link OutputStream} as SMF
	 *
     * @param sequence the {@link Sequence}
     * @param fileType the fileType of {@link Sequence}
	 * @param outputStream the {@link OutputStream} to write
     * @return the file length
	 * @throws IOException
	 */
    public static int write(@NonNull final Sequence sequence, final int fileType, @NonNull final OutputStream outputStream) throws IOException {
		return standardMidiFileWriter.write(sequence, fileType, outputStream);
	}
}
