package web;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import execution.StepResult;

/**
 * MipsViewModelを生成するFactoryクラス。
 *
 * HomeControllerの中で直接new MipsViewModel(...)を書くと、
 * 引数が多くなりControllerが読みにくくなる。
 *
 * このクラスにViewModel生成処理を集約することで、
 * Controllerを「リクエストを受ける役割」に集中させる。
 */
@Component
public class MipsViewModelFactory {

    /** Web版MipsStepLabの実行状態を扱うService。 */
    private final WebMipsSessionService mipsSessionService;

    /**
     * MipsViewModelFactoryを生成する。
     *
     * @param mipsSessionService Web版MipsStepLabの実行状態を扱うService
     */
    public MipsViewModelFactory(WebMipsSessionService mipsSessionService) {
        this.mipsSessionService = mipsSessionService;
    }

    /**
     * 初期表示用のViewModelを作成する。
     *
     * @param defaultProgram 初期表示するサンプルプログラム
     * @return 初期表示用ViewModel
     */
    public MipsViewModel createInitialViewModel(String defaultProgram) {
        List<String> programLines = mipsSessionService.splitLines(defaultProgram);

        return new MipsViewModel(
                defaultProgram,
                programLines,
                null,
                MessageType.INFO.getCssClassName(),
                null,
                0,
                false,
                0,
                Set.of(),
                Set.of(),
                null,
                null,
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                List.of());
    }

    /**
     * 実行状態が存在しない場合のViewModelを作成する。
     *
     * @param defaultProgram 初期表示するサンプルプログラム
     * @param message        画面に表示するエラーメッセージ
     * @return セッションなしエラー用ViewModel
     */
    public MipsViewModel createNoSessionViewModel(String defaultProgram, String message) {
        List<String> programLines = mipsSessionService.splitLines(defaultProgram);

        return new MipsViewModel(
                defaultProgram,
                programLines,
                message,
                MessageType.ERROR.getCssClassName(),
                false,
                0,
                false,
                -1,
                Set.of(),
                Set.of(),
                null,
                null,
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                List.of());
    }

    /**
     * パース後のViewModelを作成する。
     *
     * 新しいプログラムを解析した直後や、パース失敗時に使う。
     *
     * @param programText      ユーザーが入力したプログラム文字列
     * @param programLines     行ごとに分割したプログラム文字列
     * @param message          画面に表示するメッセージ
     * @param messageType      メッセージ種別
     * @param parseSuccess     パースに成功した場合はtrue
     * @param instructionCount 解析できた命令数
     * @param readyToRun       1ステップ実行できる場合はtrue
     * @return パース後表示用ViewModel
     */
    public MipsViewModel createParsedViewModel(
            String programText,
            List<String> programLines,
            String message,
            MessageType messageType,
            boolean parseSuccess,
            int instructionCount,
            boolean readyToRun) {

        return new MipsViewModel(
                programText,
                programLines,
                message,
                messageType.getCssClassName(),
                parseSuccess,
                instructionCount,
                readyToRun,
                readyToRun ? 0 : -1,
                Set.of(),
                Set.of(),
                null,
                null,
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                List.of());
    }

    /**
     * 現在のWeb実行状態を表示するViewModelを作成する。
     *
     * ブレークポイント追加・削除、リセット直後など、
     * StepResultを表示しない操作後に使う。
     *
     * @param mipsSession Web版の実行状態
     * @param message     画面に表示するメッセージ
     * @param messageType メッセージ種別
     * @return セッション状態表示用ViewModel
     */
    public MipsViewModel createSessionStateViewModel(
            WebMipsSession mipsSession,
            String message,
            MessageType messageType) {

        String programText = mipsSession.getProgramText();
        List<String> programLines = mipsSessionService.splitLines(programText);
        boolean readyToRun = mipsSessionService.canStep(mipsSession);
        int currentPc = readyToRun ? mipsSession.getStepRunner().getPc() : -1;

        return new MipsViewModel(
                programText,
                programLines,
                message,
                messageType.getCssClassName(),
                true,
                mipsSessionService.getInstructionCount(mipsSession),
                readyToRun,
                currentPc,
                mipsSession.getExecutedPcs(),
                mipsSessionService.getBreakpoints(mipsSession),
                null,
                null,
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                List.of());
    }

    /**
     * ステップ実行結果を表示するViewModelを作成する。
     *
     * @param mipsSession             Web版の実行状態
     * @param result                  1ステップ分の実行結果
     * @param message                 画面に表示するメッセージ
     * @param messageType             メッセージ種別
     * @param registerDiffs           レジスタ変更差分
     * @param registerValues          レジスタ現在値一覧
     * @param hiLoDiffs               HI/LO変更差分
     * @param hiLoValues              HI/LO現在値一覧
     * @param memoryDiffs             メモリ変更差分
     * @param executedInstructionText 実行した命令の表示文字列
     * @return ステップ実行結果表示用ViewModel
     */
    public MipsViewModel createStepResultViewModel(
            WebMipsSession mipsSession,
            StepResult result,
            String message,
            MessageType messageType,
            List<RegisterDiff> registerDiffs,
            List<RegisterValue> registerValues,
            List<HiLoDiff> hiLoDiffs,
            List<HiLoValue> hiLoValues,
            List<MemoryDiff> memoryDiffs,
            String executedInstructionText) {

        String programText = mipsSession.getProgramText();
        List<String> programLines = mipsSessionService.splitLines(programText);
        boolean readyToRun = mipsSessionService.canStep(mipsSession);
        int currentPc = readyToRun ? mipsSession.getStepRunner().getPc() : -1;

        return new MipsViewModel(
                programText,
                programLines,
                message,
                messageType.getCssClassName(),
                true,
                mipsSessionService.getInstructionCount(mipsSession),
                readyToRun,
                currentPc,
                mipsSession.getExecutedPcs(),
                mipsSessionService.getBreakpoints(mipsSession),
                result,
                executedInstructionText,
                registerDiffs,
                registerValues,
                hiLoDiffs,
                hiLoValues,
                memoryDiffs);
    }
}