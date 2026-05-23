package web;

/**
 * レジスタの変更差分を表すクラス。
 *
 * 1ステップ実行したときに、あるレジスタの値が
 * 実行前から実行後へどのように変化したかを保持する。
 *
 * Web画面では、このクラスのリストを使って
 * 変更されたレジスタだけを表示する。
 */
public class RegisterDiff {

    /** レジスタ番号。 */
    private final int registerNumber;

    /** 実行前の値。 */
    private final int beforeValue;

    /** 実行後の値。 */
    private final int afterValue;

    /**
     * RegisterDiffを生成する。
     *
     * @param registerNumber レジスタ番号
     * @param beforeValue    実行前の値
     * @param afterValue     実行後の値
     */
    public RegisterDiff(int registerNumber, int beforeValue, int afterValue) {
        this.registerNumber = registerNumber;
        this.beforeValue = beforeValue;
        this.afterValue = afterValue;
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
     * 実行前の値を返す。
     *
     * @return 実行前の値
     */
    public int getBeforeValue() {
        return beforeValue;
    }

    /**
     * 実行後の値を返す。
     *
     * @return 実行後の値
     */
    public int getAfterValue() {
        return afterValue;
    }
}