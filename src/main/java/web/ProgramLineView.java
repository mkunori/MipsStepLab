package web;

/**
 * Web画面のプログラム一覧に表示する1行分の情報。
 *
 * textarea上の行と、実行対象命令のPCは必ずしも一致しない。
 * ラベル行は画面には表示するが、実行対象命令ではないためPCを持たない。
 */
public class ProgramLineView {

    /** textarea上の行番号。 */
    private final int lineNumber;

    /** 実行対象命令のPC。ラベル行などPCを持たない行ではnull。 */
    private final Integer pc;

    /** 表示するプログラム行の文字列。 */
    private final String text;

    /**
     * ProgramLineViewを生成する。
     *
     * @param lineNumber textarea上の行番号
     * @param pc         実行対象命令のPC。PCを持たない行ではnull
     * @param text       表示するプログラム行の文字列
     */
    public ProgramLineView(int lineNumber, Integer pc, String text) {
        this.lineNumber = lineNumber;
        this.pc = pc;
        this.text = text;
    }

    /**
     * textarea上の行番号を返す。
     *
     * @return textarea上の行番号
     */
    public int getLineNumber() {
        return lineNumber;
    }

    /**
     * 実行対象命令のPCを返す。
     *
     * @return 実行対象命令のPC。PCを持たない行ではnull
     */
    public Integer getPc() {
        return pc;
    }

    /**
     * 表示するプログラム行の文字列を返す。
     *
     * @return 表示するプログラム行の文字列
     */
    public String getText() {
        return text;
    }

    /**
     * この行が実行対象命令かどうかを返す。
     *
     * @return PCを持つ場合はtrue
     */
    public boolean isInstructionLine() {
        return pc != null;
    }
}