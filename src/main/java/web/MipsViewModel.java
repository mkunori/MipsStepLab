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
    private MipsViewModel(
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

    /**
     * MipsViewModelを段階的に生成するBuilderクラス。
     *
     * MipsViewModelは画面表示用の項目が多いため、
     * コンストラクタに多数の引数を並べると、順番の間違いが起きやすくなる。
     *
     * Builderを使うことで、どの値を設定しているかを名前付きで読みやすくする。
     */
    public static class Builder {

        /** 入力欄に表示するMIPSプログラム文字列。 */
        private String programText = "";

        /** 行ごとに分割したMIPSプログラム。 */
        private List<String> programLines = List.of();

        /** 画面に表示するメッセージ。 */
        private String message;

        /** メッセージ表示用のCSSクラス名。 */
        private String messageType = MessageType.INFO.getCssClassName();

        /** パースまたは実行準備に成功している場合はtrue。 */
        private Boolean parseSuccess;

        /** 解析された命令数。 */
        private int instructionCount;

        /** 次の命令を実行できる場合はtrue。 */
        private boolean readyToRun;

        /** 現在のPC。次に実行する命令がない場合は-1。 */
        private int currentPc = -1;

        /** 実行済みのPC番号一覧。 */
        private Set<Integer> executedPcs = Set.of();

        /** ブレークポイントのPC番号一覧。 */
        private Set<Integer> breakpoints = Set.of();

        /** 1ステップ実行結果。未実行の場合はnull。 */
        private StepResult stepResult;

        /** 実行した命令の表示文字列。 */
        private String executedInstructionText;

        /** レジスタ変更差分。 */
        private List<RegisterDiff> registerDiffs = List.of();

        /** レジスタ現在値一覧。 */
        private List<RegisterValue> registerValues = List.of();

        /** HI/LO変更差分。 */
        private List<HiLoDiff> hiLoDiffs = List.of();

        /** HI/LO現在値一覧。 */
        private List<HiLoValue> hiLoValues = List.of();

        /** メモリ変更差分。 */
        private List<MemoryDiff> memoryDiffs = List.of();

        /**
         * 入力欄に表示するMIPSプログラム文字列を設定する。
         *
         * @param programText 入力欄に表示するMIPSプログラム文字列
         * @return このBuilder
         */
        public Builder programText(String programText) {
            this.programText = programText;
            return this;
        }

        /**
         * 行ごとに分割したMIPSプログラムを設定する。
         *
         * @param programLines 行ごとに分割したMIPSプログラム
         * @return このBuilder
         */
        public Builder programLines(List<String> programLines) {
            this.programLines = programLines;
            return this;
        }

        /**
         * 画面に表示するメッセージを設定する。
         *
         * @param message 画面に表示するメッセージ
         * @return このBuilder
         */
        public Builder message(String message) {
            this.message = message;
            return this;
        }

        /**
         * メッセージ種別を設定する。
         *
         * @param messageType メッセージ種別
         * @return このBuilder
         */
        public Builder messageType(MessageType messageType) {
            this.messageType = messageType.getCssClassName();
            return this;
        }

        /**
         * パースまたは実行準備の成功状態を設定する。
         *
         * @param parseSuccess パースまたは実行準備に成功している場合はtrue
         * @return このBuilder
         */
        public Builder parseSuccess(Boolean parseSuccess) {
            this.parseSuccess = parseSuccess;
            return this;
        }

        /**
         * 解析された命令数を設定する。
         *
         * @param instructionCount 解析された命令数
         * @return このBuilder
         */
        public Builder instructionCount(int instructionCount) {
            this.instructionCount = instructionCount;
            return this;
        }

        /**
         * 次の命令を実行できるかどうかを設定する。
         *
         * @param readyToRun 次の命令を実行できる場合はtrue
         * @return このBuilder
         */
        public Builder readyToRun(boolean readyToRun) {
            this.readyToRun = readyToRun;
            return this;
        }

        /**
         * 現在のPCを設定する。
         *
         * @param currentPc 現在のPC
         * @return このBuilder
         */
        public Builder currentPc(int currentPc) {
            this.currentPc = currentPc;
            return this;
        }

        /**
         * 実行済みPC番号一覧を設定する。
         *
         * @param executedPcs 実行済みPC番号一覧
         * @return このBuilder
         */
        public Builder executedPcs(Set<Integer> executedPcs) {
            this.executedPcs = executedPcs;
            return this;
        }

        /**
         * ブレークポイント一覧を設定する。
         *
         * @param breakpoints ブレークポイント一覧
         * @return このBuilder
         */
        public Builder breakpoints(Set<Integer> breakpoints) {
            this.breakpoints = breakpoints;
            return this;
        }

        /**
         * 1ステップ実行結果を設定する。
         *
         * @param stepResult 1ステップ実行結果
         * @return このBuilder
         */
        public Builder stepResult(StepResult stepResult) {
            this.stepResult = stepResult;
            return this;
        }

        /**
         * 実行した命令の表示文字列を設定する。
         *
         * @param executedInstructionText 実行した命令の表示文字列
         * @return このBuilder
         */
        public Builder executedInstructionText(String executedInstructionText) {
            this.executedInstructionText = executedInstructionText;
            return this;
        }

        /**
         * レジスタ変更差分を設定する。
         *
         * @param registerDiffs レジスタ変更差分
         * @return このBuilder
         */
        public Builder registerDiffs(List<RegisterDiff> registerDiffs) {
            this.registerDiffs = registerDiffs;
            return this;
        }

        /**
         * レジスタ現在値一覧を設定する。
         *
         * @param registerValues レジスタ現在値一覧
         * @return このBuilder
         */
        public Builder registerValues(List<RegisterValue> registerValues) {
            this.registerValues = registerValues;
            return this;
        }

        /**
         * HI/LO変更差分を設定する。
         *
         * @param hiLoDiffs HI/LO変更差分
         * @return このBuilder
         */
        public Builder hiLoDiffs(List<HiLoDiff> hiLoDiffs) {
            this.hiLoDiffs = hiLoDiffs;
            return this;
        }

        /**
         * HI/LO現在値一覧を設定する。
         *
         * @param hiLoValues HI/LO現在値一覧
         * @return このBuilder
         */
        public Builder hiLoValues(List<HiLoValue> hiLoValues) {
            this.hiLoValues = hiLoValues;
            return this;
        }

        /**
         * メモリ変更差分を設定する。
         *
         * @param memoryDiffs メモリ変更差分
         * @return このBuilder
         */
        public Builder memoryDiffs(List<MemoryDiff> memoryDiffs) {
            this.memoryDiffs = memoryDiffs;
            return this;
        }

        /**
         * 設定された値を使ってMipsViewModelを生成する。
         *
         * @return MipsViewModel
         */
        public MipsViewModel build() {
            return new MipsViewModel(
                    programText,
                    programLines,
                    message,
                    messageType,
                    parseSuccess,
                    instructionCount,
                    readyToRun,
                    currentPc,
                    executedPcs,
                    breakpoints,
                    stepResult,
                    executedInstructionText,
                    registerDiffs,
                    registerValues,
                    hiLoDiffs,
                    hiLoValues,
                    memoryDiffs);
        }
    }

    /**
     * MipsViewModelのBuilderを生成する。
     *
     * @return MipsViewModel用Builder
     */
    public static Builder builder() {
        return new Builder();
    }
}