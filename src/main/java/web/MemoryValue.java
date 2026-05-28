package web;

/**
 * メモリの現在値を表すクラス。
 *
 * Web画面では、このクラスのリストを使って
 * 指定範囲のメモリ現在値を表形式で表示する。
 */
public class MemoryValue {

    /** メモリアドレス。 */
    private final int address;

    /** メモリの現在値。 */
    private final byte value;

    /**
     * MemoryValueを生成する。
     *
     * @param address メモリアドレス
     * @param value   メモリの現在値
     */
    public MemoryValue(int address, byte value) {
        this.address = address;
        this.value = value;
    }

    /**
     * メモリアドレスを返す。
     *
     * @return メモリアドレス
     */
    public int getAddress() {
        return address;
    }

    /**
     * メモリの現在値を返す。
     *
     * @return メモリの現在値
     */
    public byte getValue() {
        return value;
    }
}