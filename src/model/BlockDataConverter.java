package model;

import java.nio.ByteBuffer;

public class BlockDataConverter {


    public static byte[] getBlockByPositionInList(byte[] bytes, int blockSize, int index) {
        int positionInList = index * blockSize;
        if (positionInList >= bytes.length) {
            return new byte[0];
        }
        byte[] listElement = new byte[blockSize];
        int toIndex = Math.min(positionInList + blockSize, bytes.length);
        System.arraycopy(bytes, positionInList, listElement, 0, toIndex - positionInList);
        return listElement;
    }


    public static int getIntsFromBytes(byte[] blockByteList) {
        validateByteArraySize(blockByteList, 4);
        return ByteBuffer.wrap(blockByteList).getInt();
    }

    public static short getShortsFromBytes(byte[] blockByteList) {
        validateByteArraySize(blockByteList, 2);
        return ByteBuffer.wrap(blockByteList).getShort();

    }

    public static long getLongsFromBytes(byte[] blockByteList) {
        validateByteArraySize(blockByteList, 8);
        return ByteBuffer.wrap(blockByteList).getLong();
    }

    public static double getDoublesFromBytes(byte[] blockByteList) {
        validateByteArraySize(blockByteList, 8);
        return ByteBuffer.wrap(blockByteList).getDouble();
    }

    public static float getFloatsFromBytes(byte[] blockByteList) {
        validateByteArraySize(blockByteList, 4);
        return ByteBuffer.wrap(blockByteList).getFloat();

    }

    private static void validateByteArraySize(byte[] byteArray, int expectedSize) {
        System.out.println(byteArray.length);
        if (byteArray.length < expectedSize) {
            throw new IllegalArgumentException("Недостаточно байт для укрупнения выборки байт ");
        }
    }

}
