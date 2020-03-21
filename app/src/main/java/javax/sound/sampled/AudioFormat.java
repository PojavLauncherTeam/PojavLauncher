package javax.sound.sampled;

public class AudioFormat {
    protected boolean bigEndian;
    protected int channels;
    protected float sampleRate;
    protected int sampleSizeInBits;
    protected boolean signed;

    public AudioFormat(float sampleRate, int sampleSizeInBits, int channels, boolean signed, boolean bigEndian) {
        this.sampleRate = sampleRate;
        this.sampleSizeInBits = sampleSizeInBits;
        this.channels = channels;
        this.signed = signed;
        this.bigEndian = bigEndian;
    }

    public float getSampleRate() {
        return this.sampleRate;
    }

    public int getSampleSizeInBits() {
        return this.sampleSizeInBits;
    }

    public int getChannels() {
        return this.channels;
    }

    public boolean isBigEndian() {
        return this.bigEndian;
    }
}

