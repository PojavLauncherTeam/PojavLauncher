package java.awt.image;

public class WritableRaster {
    private BufferedImage image;

    public WritableRaster(BufferedImage image) {
        this.image = image;
    }

    public DataBuffer getDataBuffer() {
        int[] theBuf = new int[(this.image.getWidth() * this.image.getHeight())];
        this.image.getRGB(0, 0, this.image.getWidth(), this.image.getHeight(), theBuf, 0, this.image.getWidth());
        return new DataBufferInt(theBuf, theBuf.length);
    }
}

