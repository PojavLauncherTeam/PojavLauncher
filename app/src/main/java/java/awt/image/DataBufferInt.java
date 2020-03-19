package java.awt.image;

public class DataBufferInt extends DataBuffer {
    private int[] array;

    public DataBufferInt(int[] array, int size) {
        this.array = array;
    }

    public int[] getData() {
        return this.array;
    }
}

