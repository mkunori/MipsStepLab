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

    /** このステップで値が変化した場合はtrue。 */
    private final boolean changed;

    /**
     * MemoryValueを生成する。
     *
     * @param address メモリアドレス
     * @param value   メモリの現在値
     * @param changed このステップで値が変化した場合はtrue
     */
    public MemoryValue(int address, byte value, boolean changed) {
        this.address = address;
        this.value = value;
        this.changed = changed;
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

    /**
     * メモリの現在値を符号なし整数として返す。
     *
     * Javaのbyteは符号付きで扱われるため、Web画面の16進数表示では
     * 0〜255の値として表示できるように変換する。
     *
     * @return 符号なし整数として扱ったメモリの現在値
     */
    public int getUnsignedValue() {
        return Byte.toUnsignedInt(value);
    }

    /**
     * メモリの現在値を16進数文字列で返す。
     *
     * @return 0x00形式の16進数文字列
     */
    public String getHexValue() {
        return String.format("0x%02X", getUnsignedValue());
    }

    /**
     * メモリアドレスを16進数文字列で返す。
     *
     * @return 0x0000形式の16進数文字列
     */
    public String getHexAddress() {
        return String.format("0x%04X", address);
    }

    /**
     * このステップで値が変化したかを返す。
     *
     * @return 値が変化した場合はtrue
     */
    public boolean isChanged() {
        return changed;
    }
}