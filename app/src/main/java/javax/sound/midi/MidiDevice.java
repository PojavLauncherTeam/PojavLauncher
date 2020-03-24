package javax.sound.midi;

import android.support.annotation.NonNull;

import java.util.List;

/**
 * Interface for MIDI Device
 *
 * @author K.Shoji
 */
public interface MidiDevice {

    /**
     * Get the device information
     *
     * @return the device information
     */
    @NonNull
    Info getDeviceInfo();

	/**
	 * Open the {@link MidiDevice}. This method must be called at getting the new instance.
	 * 
	 * @throws MidiUnavailableException
	 */
	void open() throws MidiUnavailableException;

	/**
	 * Close the {@link MidiDevice}. This method must be called at finishing to use the instance.
	 */
	void close();

	/**
	 * Check if the {@link MidiDevice} opened.
	 * 
	 * @return true if already opened
	 */
	boolean isOpen();

	/**
	 * Get the {@link MidiDevice}'s timeStamp.
	 * @return -1 if the timeStamp not supported.
	 */
	long getMicrosecondPosition();

	/**
	 * Get the number of the {@link Receiver}s. 
	 * 
	 * @return the number of the {@link Receiver}s.
	 */
	int getMaxReceivers();

	/**
	 * Get the number of the {@link Transmitter}s. 
	 * 
	 * @return the number of the {@link Transmitter}s.
	 */
	int getMaxTransmitters();

	/**
	 * Get the default {@link Receiver}.
	 * 
	 * @return the default {@link Receiver}.
	 * @throws MidiUnavailableException
	 */
	@NonNull
    Receiver getReceiver() throws MidiUnavailableException;

	/**
	 * Get the all of {@link Receiver}s.
	 * 
	 * @return the all of {@link Receiver}s.
	 */
    @NonNull
    List<Receiver> getReceivers();

	/**
	 * Get the default {@link Transmitter}.
	 * 
	 * @return the default {@link Transmitter}.
	 * @throws MidiUnavailableException
	 */
    @NonNull
    Transmitter getTransmitter() throws MidiUnavailableException;

	/**
	 * Get the all of {@link Transmitter}s.
	 * 
	 * @return the all of {@link Transmitter}s.
	 */
    @NonNull
    List<Transmitter> getTransmitters();

	/**
	 * Represents the {@link MidiDevice}'s information
	 *
	 * @author K.Shoji
	 */
	class Info {
		private final String name;
		private final String vendor;
		private final String description;
		private final String version;

        /**
         * Constructor
         *
         * @param name the name string
         * @param vendor the vendor string
         * @param description the description string
         * @param version the version string
         */
		public Info(@NonNull final String name, @NonNull final String vendor, @NonNull final String description, @NonNull final String version) {
			this.name = name;
			this.vendor = vendor;
			this.description = description;
			this.version = version;
		}

		/**
		 * Get the name of {@link MidiDevice}
		 * 
		 * @return the name of {@link MidiDevice}
		 */
        @NonNull
        public final String getName() {
			return name;
		}

		/**
		 * Get the vendor of {@link MidiDevice}
		 * 
		 * @return the vendor of {@link MidiDevice}
		 */
        @NonNull
        public final String getVendor() {
			return vendor;
		}

		/**
		 * Get the description of {@link MidiDevice}
		 * 
		 * @return the description of {@link MidiDevice}
		 */
        @NonNull
        public final String getDescription() {
			return description;
		}

		/**
		 * Get the version of {@link MidiDevice}
		 * 
		 * @return the version of {@link MidiDevice}
		 */
        @NonNull
        public final String getVersion() {
			return version;
		}

        @NonNull
		@Override
        public final String toString() {
			return name;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + description.hashCode();
			result = prime * result + name.hashCode();
			result = prime * result + vendor.hashCode();
			result = prime * result + version.hashCode();
			return result;
		}

		@Override
		public boolean equals(final Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			final Info other = (Info) obj;
			if (!description.equals(other.description)) {
				return false;
			}
			if (!name.equals(other.name)) {
				return false;
			}
			if (!vendor.equals(other.vendor)) {
				return false;
			}
			if (!version.equals(other.version)) {
				return false;
			}
			return true;
		}
	}
}
