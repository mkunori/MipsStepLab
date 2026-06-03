package web;

/**
 * 実行された命令の表示用データを保持するクラス。
 *
 * StepResultそのものではなく、画面に表示したい情報だけをまとめることで、
 * Thymeleaf側でStepResultの内部構造を直接扱いすぎないようにする。
 */
public class ExecutedInstructionView {

    /** 実行ステップ番号。 */
    private final int step;

    /** 実行前PC。 */
    private final int pcBefore;

    /** 実行後PC。 */
    private final int pcAfter;

    /** 実行された命令の表示文字列。 */
    private final String instructionText;

    /**
     * 実行命令表示用データを生成する。
     *
     * @param step            実行ステップ番号
     * @param pcBefore        実行前PC
     * @param pcAfter         実行後PC
     * @param instructionText 実行された命令の表示文字列
     */
    public ExecutedInstructionView(
            int step,
            int pcBefore,
            int pcAfter,
            String instructionText) {

        this.step = step;
        this.pcBefore = pcBefore;
        this.pcAfter = pcAfter;
        this.instructionText = instructionText == null ? "" : instructionText;
    }

    /**
     * 実行ステップ番号を返す。
     *
     * @return 実行ステップ番号
     */
    public int getStep() {
        return step;
    }

    /**
     * 実行前PCを返す。
     *
     * @return 実行前PC
     */
    public int getPcBefore() {
        return pcBefore;
    }

    /**
     * 実行後PCを返す。
     *
     * @return 実行後PC
     */
    public int getPcAfter() {
        return pcAfter;
    }

    /**
     * 実行された命令の表示文字列を返す。
     *
     * @return 実行された命令の表示文字列
     */
    public String getInstructionText() {
        return instructionText;
    }

    /**
     * 実行された命令の表示文字列があるかどうかを返す。
     *
     * @return 表示文字列が空でない場合はtrue
     */
    public boolean hasInstructionText() {
        return !instructionText.isEmpty();
    }
}
