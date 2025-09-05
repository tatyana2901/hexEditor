package model;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BlockDataConverter {


    //разбить список прочитанных байтов на список блоков байтов

    public static List<Object> convertToTypedObjectList(List<Byte> bytes, DataType dataType) {
        List<Object> objectList = null;

        if (dataType == DataType.BYTE) {
            objectList = new ArrayList<>(bytes);

        } else {
            List<byte[]> blocksList = getBlocksList(bytes, dataType.getBlockSize());
            switch (dataType) {

                case SHORT:
                    objectList = new ArrayList<>(getShortsFromBytes(blocksList));
                    break;
                case INTEGER:
                    objectList = new ArrayList<>(getIntsFromBytes(blocksList));
                    break;
                case LONG:
                    objectList = new ArrayList<>(getLongsFromBytes(blocksList));
                    break;
                case FLOAT:
                    objectList = new ArrayList<>(getFloatsFromBytes(blocksList));
                    break;
                case DOUBLE:
                    objectList = new ArrayList<>(getDoublesFromBytes(blocksList));
                    break;
            }
        }
        return objectList;
    }


    private static List<byte[]> getBlocksList(List<Byte> bytes, int blockSize) {
        List<byte[]> blockBytesList = new ArrayList<>();

        for (int i = 0; i < bytes.size(); i = i + blockSize) {

            byte[] listElement = new byte[blockSize];
            //     System.out.println("i = " + i);
            int toIndex = Math.min(i + blockSize, bytes.size());


            Byte[] block = bytes.subList(i, toIndex).toArray(new Byte[0]);
            byte[] primitiveByteArray = unboxByteArray(block);

            //    System.out.println(Arrays.toString(block));
            System.arraycopy(primitiveByteArray, 0, listElement, 0, block.length);
            if (block.length < blockSize) {
                Arrays.fill(listElement, block.length, blockSize, (byte) 0); //заполняе нулями незаполненные ячейки блока
            }

            blockBytesList.add(listElement);
            // System.out.println(Arrays.toString(listElement));
        }
        //  System.out.println(blockBytesList);
        return blockBytesList;
    }

    private static byte[] unboxByteArray(Byte[] block) {

        byte[] array = new byte[block.length];
        for (int i = 0; i < block.length; i++) {
            array[i] = block[i];
        }
        return array;
    }


    private static List<Integer> getIntsFromBytes(List<byte[]> blockByteList) {
        List<Integer> intList = new ArrayList<>();


        //условие о том что байтов должна ыть не меньше 4!!! добавить
        for (byte[] byteArray : blockByteList) {
            validateByteArraySize(byteArray,4);
            intList.add(ByteBuffer.wrap(byteArray).getInt());
        }
        return intList;
    }

    private static List<Short> getShortsFromBytes(List<byte[]> blockByteList) {
        List<Short> shortList = new ArrayList<>();

        //условие о том что байтов должна ыть не меньше 2!!! добавить
        for (byte[] byteArray : blockByteList) {
            validateByteArraySize(byteArray,2);
            shortList.add(ByteBuffer.wrap(byteArray).getShort());
        }
        return shortList;

    }

    private static List<Long> getLongsFromBytes(List<byte[]> blockByteList) {
        List<Long> longList = new ArrayList<>();

        //условие о том что байтов должна ыть не меньше 8!!! добавить
        for (byte[] byteArray : blockByteList) {
            validateByteArraySize(byteArray,8);
            longList.add(ByteBuffer.wrap(byteArray).getLong());
        }
        return longList;

    }

    private static List<Double> getDoublesFromBytes(List<byte[]> blockByteList) {

        List<Double> doubleList = new ArrayList<>();

        //условие о том что байтов должна ыть не меньше 8!!! добавить
        for (byte[] byteArray : blockByteList) {
            validateByteArraySize(byteArray,8);
            doubleList.add(ByteBuffer.wrap(byteArray).getDouble());
        }
        return doubleList;

    }

    private static List<Float> getFloatsFromBytes(List<byte[]> blockByteList) {
        List<Float> floatList = new ArrayList<>();

        //условие о том что байтов должна ыть не меньше 4!!! добавить
        for (byte[] byteArray : blockByteList) {
            validateByteArraySize(byteArray,4);
            floatList.add(ByteBuffer.wrap(byteArray).getFloat());
        }
        return floatList;

    }

    private static void validateByteArraySize(byte[] byteArray, int expectedSize) {
        if (byteArray.length < expectedSize) {
            throw new IllegalArgumentException("Недостаточно байт для укрупнения выборки байт " );
        }
    }

}
