package net.kdt.pojavlaunch.memory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;

public class SelfMapsParser {
    private final Callback mCallback;
    public SelfMapsParser(Callback callback) {
        mCallback = callback;
    }

    public void run() throws IOException, NumberFormatException {
        try (FileInputStream fileInputStream = new FileInputStream("/proc/self/maps")) {
            Scanner scanner = new Scanner(fileInputStream);
            while(scanner.hasNextLine()) {
                if(!forEachLine(scanner.nextLine())) break;
            }
        }
    }

    private boolean forEachLine(String line) throws NumberFormatException {
        int firstSpaceIndex = line.indexOf(' ');
        String addresses = line.substring(0, firstSpaceIndex);
        String[] addressArray = addresses.split("-");
        if(addressArray.length < 2) return true;
        long begin = Long.parseLong(addressArray[0], 16);
        long end = Long.parseLong(addressArray[1], 16);
        return mCallback.process(begin, end, line);
    }

    public interface Callback {
        boolean process(long startAddress, long endAddress, String wholeLine);
    }
}
