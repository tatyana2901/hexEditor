package model;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

public class BlockDataConverter {


    public static byte[] getBlockByPositionInList(List<Byte> bytes, int blockSize, int index) {


        if (index >= bytes.size()) {
            return new byte[0];
        }

        int positionInList = index * blockSize;
        byte[] listElement = new byte[blockSize];
        int toIndex = Math.min(positionInList + blockSize, bytes.size());
        Byte[] block = bytes.subList(positionInList, toIndex).toArray(new Byte[0]);
        byte[] primitiveByteArray = unboxByteArray(block);

        System.arraycopy(primitiveByteArray, 0, listElement, 0, block.length);
        if (block.length < blockSize) {
            Arrays.fill(listElement, block.length, blockSize, (byte) 0); //заполняе нулями незаполненные ячейки блока
        }

        return listElement;
    }


    private static byte[] unboxByteArray(Byte[] block) {

        byte[] array = new byte[block.length];
        for (int i = 0; i < block.length; i++) {
            array[i] = block[i];
        }
        return array;
    }


    public static int getIntsFromBytes(byte[] blockByteList) {


        validateByteArraySize(blockByteList, 4);
        return ByteBuffer.wrap(blockByteList).getInt();


    }

    public static short getShortsFromBytes(byte[] blockByteList) {


        //условие о том что байтов должна ыть не меньше 2!!! добавить

        validateByteArraySize(blockByteList, 2);
        //  shortList.add();

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
        if (byteArray.length < expectedSize) {
            throw new IllegalArgumentException("Недостаточно байт для укрупнения выборки байт ");
        }
    }

}
