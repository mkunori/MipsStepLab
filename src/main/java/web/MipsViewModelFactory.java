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

        return MipsViewModel.builder()
                .programText(defaultProgram)
                .programLines(programLines)
                .message(null)
                .messageType(MessageType.INFO)
                .parseSuccess(null)
                .instructionCount(0)
                .readyToRun(false)
                .currentPc(0)
                .build();
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

        return MipsViewModel.builder()
                .programText(defaultProgram)
                .programLines(programLines)
                .message(message)
                .messageType(MessageType.ERROR)
                .parseSuccess(false)
                .instructionCount(0)
                .readyToRun(false)
                .currentPc(-1)
                .build();
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

        return MipsViewModel.builder()
                .programText(programText)
                .programLines(programLines)
                .message(message)
                .messageType(messageType)
                .parseSuccess(parseSuccess)
                .instructionCount(instructionCount)
                .readyToRun(readyToRun)
                .currentPc(readyToRun ? 0 : -1)
                .build();
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

        return MipsViewModel.builder()
                .programText(programText)
                .programLines(programLines)
                .message(message)
                .messageType(messageType)
                .parseSuccess(true)
                .instructionCount(mipsSessionService.getInstructionCount(mipsSession))
                .readyToRun(readyToRun)
                .currentPc(currentPc)
                .executedPcs(mipsSession.getExecutedPcs())
                .breakpoints(mipsSessionService.getBreakpoints(mipsSession))
                .build();
    }

    /**
     * ステップ実行結果を表示するViewModelを作成する。
     *
     * @param mipsSession             Web版の実行状態
     * @param result                  1ステップ分の実行結果
     * @param message                 画面に表示するメッセージ
     * @param messageType             メッセージ種別
     * @param viewData                StepResultから作成したWeb表示用データ
     * @param executedInstructionText 実行した命令の表示文字列
     * @return ステップ実行結果表示用ViewModel
     */
    public MipsViewModel createStepResultViewModel(
            WebMipsSession mipsSession,
            StepResult result,
            String message,
            MessageType messageType,
            StepResultViewData viewData,
            String executedInstructionText) {

        String programText = mipsSession.getProgramText();
        List<String> programLines = mipsSessionService.splitLines(programText);
        boolean readyToRun = mipsSessionService.canStep(mipsSession);
        int currentPc = readyToRun ? mipsSession.getStepRunner().getPc() : -1;

        return MipsViewModel.builder()
                .programText(programText)
                .programLines(programLines)
                .message(message)
                .messageType(messageType)
                .parseSuccess(true)
                .instructionCount(mipsSessionService.getInstructionCount(mipsSession))
                .readyToRun(readyToRun)
                .currentPc(currentPc)
                .executedPcs(mipsSession.getExecutedPcs())
                .breakpoints(mipsSessionService.getBreakpoints(mipsSession))
                .stepResult(result)
                .executedInstructionText(executedInstructionText)
                .registerDiffs(viewData.getRegisterDiffs())
                .registerValues(viewData.getRegisterValues())
                .hiLoDiffs(viewData.getHiLoDiffs())
                .hiLoValues(viewData.getHiLoValues())
                .memoryDiffs(viewData.getMemoryDiffs())
                .memoryValues(viewData.getMemoryValues())
                .build();
    }
}