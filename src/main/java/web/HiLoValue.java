package web;

/**
 * HIレジスタまたはLOレジスタの現在値を表すクラス。
 *
 * Web画面では、このクラスのリストを使って
 * HI/LOレジスタの現在値を表形式で表示する。
 */
public class HiLoValue {

    /** レジスタ名。HIまたはLO。 */
    private final String registerName;

    /** レジスタの現在値。 */
    private final int value;

    /**
     * HiLoValueを生成する。
     *
     * @param registerName レジスタ名
     * @param value        レジスタの現在値
     */
    public HiLoValue(String registerName, int value) {
        this.registerName = registerName;
        this.value = value;
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
     * レジスタの現在値を返す。
     *
     * @return レジスタの現在値
     */
    public int getValue() {
        return value;
    }
}