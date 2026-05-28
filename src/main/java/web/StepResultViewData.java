package web;

import java.util.List;

/**
 * StepResultから作成したWeb表示用データをまとめるクラス。
 *
 * 1ステップ実行後の画面では、レジスタ差分、レジスタ一覧、
 * HI/LO差分、HI/LO一覧、メモリ差分など複数の表示用データが必要になる。
 *
 * それらを個別にControllerへ返すと引数や変数が増えるため、
 * このクラスにまとめて扱う。
 */
public class StepResultViewData {

    /** レジスタ変更差分。 */
    private final List<RegisterDiff> registerDiffs;

    /** レジスタ現在値一覧。 */
    private final List<RegisterValue> registerValues;

    /** HI/LO変更差分。 */
    private final List<HiLoDiff> hiLoDiffs;

    /** HI/LO現在値一覧。 */
    private final List<HiLoValue> hiLoValues;

    /** メモリ変更差分。 */
    private final List<MemoryDiff> memoryDiffs;

    /** メモリ現在値一覧。 */
    private final List<MemoryValue> memoryValues;

    /**
     * StepResultViewDataを生成する。
     *
     * @param registerDiffs  レジスタ変更差分
     * @param registerValues レジスタ現在値一覧
     * @param hiLoDiffs      HI/LO変更差分
     * @param hiLoValues     HI/LO現在値一覧
     * @param memoryDiffs    メモリ変更差分
     * @param memoryValues   メモリ現在値一覧
     */
    public StepResultViewData(
            List<RegisterDiff> registerDiffs,
            List<RegisterValue> registerValues,
            List<HiLoDiff> hiLoDiffs,
            List<HiLoValue> hiLoValues,
            List<MemoryDiff> memoryDiffs,
            List<MemoryValue> memoryValues) {

        this.registerDiffs = registerDiffs;
        this.registerValues = registerValues;
        this.hiLoDiffs = hiLoDiffs;
        this.hiLoValues = hiLoValues;
        this.memoryDiffs = memoryDiffs;
        this.memoryValues = memoryValues;
    }

    /**
     * レジスタ変更差分を返す。
     *
     * @return レジスタ変更差分
     */
    public List<RegisterDiff> getRegisterDiffs() {
        return registerDiffs;
    }

    /**
     * レジスタ現在値一覧を返す。
     *
     * @return レジスタ現在値一覧
     */
    public List<RegisterValue> getRegisterValues() {
        return registerValues;
    }

    /**
     * HI/LO変更差分を返す。
     *
     * @return HI/LO変更差分
     */
    public List<HiLoDiff> getHiLoDiffs() {
        return hiLoDiffs;
    }

    /**
     * HI/LO現在値一覧を返す。
     *
     * @return HI/LO現在値一覧
     */
    public List<HiLoValue> getHiLoValues() {
        return hiLoValues;
    }

    /**
     * メモリ変更差分を返す。
     *
     * @return メモリ変更差分
     */
    public List<MemoryDiff> getMemoryDiffs() {
        return memoryDiffs;
    }

    /**
     * メモリ現在値一覧を返す。
     *
     * @return メモリ現在値一覧
     */
    public List<MemoryValue> getMemoryValues() {
        return memoryValues;
    }
}