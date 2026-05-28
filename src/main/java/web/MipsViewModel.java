package web;

import java.util.List;
import java.util.Set;

import execution.StepResult;

/**
 * MipsStepLab Web画面に表示する情報をまとめるクラス。
 *
 * ControllerからThymeleafテンプレートへ多数の値を個別に渡す代わりに、
 * 画面表示に必要な情報をこのクラスへまとめる。
 *
 * これにより、HomeControllerのModel設定処理を整理しやすくする。
 */
public class MipsViewModel {

    /** 入力欄に表示するMIPSプログラム文字列。 */
    private final String programText;

    /** 行ごとに分割したMIPSプログラム。 */
    private final List<String> programLines;

    /** 画面に表示するメッセージ。 */
    private final String message;

    /** メッセージ表示用のCSSクラス名。 */
    private final String messageType;

    /** パースまたは実行準備に成功している場合はtrue。 */
    private final Boolean parseSuccess;

    /** 解析された命令数。 */
    private final int instructionCount;

    /** 次の命令を実行できる場合はtrue。 */
    private final boolean readyToRun;

    /** 現在のPC。次に実行する命令がない場合は-1。 */
    private final int currentPc;

    /** 実行済みのPC番号一覧。 */
    private final Set<Integer> executedPcs;

    /** ブレークポイントのPC番号一覧。 */
    private final Set<Integer> breakpoints;

    /** 1ステップ実行結果。未実行の場合はnull。 */
    private final StepResult stepResult;

    /** 実行した命令の表示文字列。 */
    private final String executedInstructionText;

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

    /**
     * MipsViewModelを生成する。
     *
     * @param programText             入力欄に表示するMIPSプログラム文字列
     * @param programLines            行ごとに分割したMIPSプログラム
     * @param message                 画面に表示するメッセージ
     * @param messageType             メッセージ表示用のCSSクラス名
     * @param parseSuccess            パースまたは実行準備に成功している場合はtrue
     * @param instructionCount        解析された命令数
     * @param readyToRun              次の命令を実行できる場合はtrue
     * @param currentPc               現在のPC
     * @param executedPcs             実行済みPC番号一覧
     * @param breakpoints             ブレークポイント一覧
     * @param stepResult              1ステップ実行結果
     * @param executedInstructionText 実行した命令の表示文字列
     * @param registerDiffs           レジスタ変更差分
     * @param registerValues          レジスタ現在値一覧
     * @param hiLoDiffs               HI/LO変更差分
     * @param hiLoValues              HI/LO現在値一覧
     * @param memoryDiffs             メモリ変更差分
     */
    public MipsViewModel(
            String programText,
            List<String> programLines,
            String message,
            String messageType,
            Boolean parseSuccess,
            int instructionCount,
            boolean readyToRun,
            int currentPc,
            Set<Integer> executedPcs,
            Set<Integer> breakpoints,
            StepResult stepResult,
            String executedInstructionText,
            List<RegisterDiff> registerDiffs,
            List<RegisterValue> registerValues,
            List<HiLoDiff> hiLoDiffs,
            List<HiLoValue> hiLoValues,
            List<MemoryDiff> memoryDiffs) {

        this.programText = programText;
        this.programLines = programLines;
        this.message = message;
        this.messageType = messageType;
        this.parseSuccess = parseSuccess;
        this.instructionCount = instructionCount;
        this.readyToRun = readyToRun;
        this.currentPc = currentPc;
        this.executedPcs = executedPcs;
        this.breakpoints = breakpoints;
        this.stepResult = stepResult;
        this.executedInstructionText = executedInstructionText;
        this.registerDiffs = registerDiffs;
        this.registerValues = registerValues;
        this.hiLoDiffs = hiLoDiffs;
        this.hiLoValues = hiLoValues;
        this.memoryDiffs = memoryDiffs;
    }

    public String getProgramText() {
        return programText;
    }

    public List<String> getProgramLines() {
        return programLines;
    }

    public String getMessage() {
        return message;
    }

    public String getMessageType() {
        return messageType;
    }

    public Boolean getParseSuccess() {
        return parseSuccess;
    }

    public int getInstructionCount() {
        return instructionCount;
    }

    public boolean isReadyToRun() {
        return readyToRun;
    }

    public int getCurrentPc() {
        return currentPc;
    }

    public Set<Integer> getExecutedPcs() {
        return executedPcs;
    }

    public Set<Integer> getBreakpoints() {
        return breakpoints;
    }

    public StepResult getStepResult() {
        return stepResult;
    }

    public String getExecutedInstructionText() {
        return executedInstructionText;
    }

    public List<RegisterDiff> getRegisterDiffs() {
        return registerDiffs;
    }

    public List<RegisterValue> getRegisterValues() {
        return registerValues;
    }

    public List<HiLoDiff> getHiLoDiffs() {
        return hiLoDiffs;
    }

    public List<HiLoValue> getHiLoValues() {
        return hiLoValues;
    }

    public List<MemoryDiff> getMemoryDiffs() {
        return memoryDiffs;
    }
}