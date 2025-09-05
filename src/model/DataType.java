package model;

public enum DataType {

    BYTE(1, false),
    SHORT(2, false),
    INTEGER(4, false),
    LONG(8, false),
    FLOAT(4, true),
    DOUBLE(8, true);


    private final int blockSize;
    private final boolean isFloatingPointNumber;

    DataType(int blockSize, boolean isFloatingPointNumber) {
        this.blockSize = blockSize;
        this.isFloatingPointNumber = isFloatingPointNumber;
    }

    public int getBlockSize() {
        return blockSize;
    }
}
