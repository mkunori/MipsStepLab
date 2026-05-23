package web;

/**
 * メモリの変更差分を表すクラス。
 *
 * 1ステップ実行したときに、あるメモリアドレスの値が
 * 実行前から実行後へどのように変化したかを保持する。
 *
 * Web画面では、このクラスのリストを使って
 * 変更されたメモリだけを表示する。
 */
public class MemoryDiff {

    /** メモリアドレス。 */
    private final int address;

    /** 実行前の値。 */
    private final byte beforeValue;

    /** 実行後の値。 */
    private final byte afterValue;

    /**
     * MemoryDiffを生成する。
     *
     * @param address     メモリアドレス
     * @param beforeValue 実行前の値
     * @param afterValue  実行後の値
     */
    public MemoryDiff(int address, byte beforeValue, byte afterValue) {
        this.address = address;
        this.beforeValue = beforeValue;
        this.afterValue = afterValue;
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
     * 実行前の値を返す。
     *
     * @return 実行前の値
     */
    public byte getBeforeValue() {
        return beforeValue;
    }

    /**
     * 実行後の値を返す。
     *
     * @return 実行後の値
     */
    public byte getAfterValue() {
        return afterValue;
    }
}