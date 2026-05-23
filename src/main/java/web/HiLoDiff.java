package web;

/**
 * HIレジスタまたはLOレジスタの変更差分を表すクラス。
 *
 * MIPSでは、乗算や除算の結果がHI/LOレジスタに保存される。
 * このクラスは、HIまたはLOの値が1ステップ実行によって
 * どのように変化したかをWeb画面に表示するために使う。
 */
public class HiLoDiff {

    /** レジスタ名。HIまたはLO。 */
    private final String registerName;

    /** 実行前の値。 */
    private final int beforeValue;

    /** 実行後の値。 */
    private final int afterValue;

    /**
     * HiLoDiffを生成する。
     *
     * @param registerName レジスタ名
     * @param beforeValue  実行前の値
     * @param afterValue   実行後の値
     */
    public HiLoDiff(String registerName, int beforeValue, int afterValue) {
        this.registerName = registerName;
        this.beforeValue = beforeValue;
        this.afterValue = afterValue;
    }

    /**
     * レジスタ名を返す。
     *
     * @return レジスタ名
     */
    public String getRegisterName() {
        return registerName;
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