package web;

/**
 * レジスタの現在値を表すクラス。
 *
 * Web画面では、このクラスのリストを使って
 * R0〜R31の現在値を表形式で表示する。
 */
public class RegisterValue {

    /** レジスタ番号。 */
    private final int registerNumber;

    /** レジスタの現在値。 */
    private final int value;

    /** このステップで値が変化した場合はtrue。 */
    private final boolean changed;

    /**
     * RegisterValueを生成する。
     *
     * @param registerNumber レジスタ番号
     * @param value          レジスタの現在値
     * @param changed        このステップで値が変化した場合はtrue
     */
    public RegisterValue(int registerNumber, int value, boolean changed) {
        this.registerNumber = registerNumber;
        this.value = value;
        this.changed = changed;
    }

    /**
     * レジスタ番号を返す。
     *
     * @return レジスタ番号
     */
    public int getRegisterNumber() {
        return registerNumber;
    }

    /**
     * レジスタの現在値を返す。
     *
     * @return レジスタの現在値
     */
    public int getValue() {
        return value;
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