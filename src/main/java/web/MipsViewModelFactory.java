package web;

import java.util.ArrayList;
import java.util.List;

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
                .registerValues(createInitialRegisterValues())
                .hiLoValues(createInitialHiLoValues())
                .memoryValues(createInitialMemoryValues())
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
                .registerValues(createInitialRegisterValues())
                .hiLoValues(createInitialHiLoValues())
                .memoryValues(createInitialMemoryValues())
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
                .registerValues(createInitialRegisterValues())
                .hiLoValues(createInitialHiLoValues())
                .memoryValues(createInitialMemoryValues())
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
                .registerValues(createCurrentRegisterValues(mipsSession))
                .hiLoValues(createCurrentHiLoValues(mipsSession))
                .memoryValues(createCurrentMemoryValues(mipsSession))
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

    /**
     * 初期表示用のレジスタ現在値一覧を作成する。
     *
     * @return 0で初期化されたレジスタ現在値一覧
     */
    private List<RegisterValue> createInitialRegisterValues() {
        List<RegisterValue> values = new ArrayList<>();

        for (int i = 0; i < 32; i++) {
            values.add(new RegisterValue(i, 0, false));
        }

        return values;
    }

    /**
     * 初期表示用のHI/LO現在値一覧を作成する。
     *
     * @return 0で初期化されたHI/LO現在値一覧
     */
    private List<HiLoValue> createInitialHiLoValues() {
        return List.of(
                new HiLoValue("HI", 0, false),
                new HiLoValue("LO", 0, false));
    }

    /**
     * 初期表示用のメモリ現在値一覧を作成する。
     *
     * @return 0で初期化されたメモリ現在値一覧
     */
    private List<MemoryValue> createInitialMemoryValues() {
        List<MemoryValue> values = new ArrayList<>();

        for (int address = 0; address < 32; address++) {
            values.add(new MemoryValue(address, (byte) 0, false));
        }

        return values;
    }

    /**
     * 現在のCPU状態から、レジスタ現在値一覧を作成する。
     *
     * セッション状態表示では、直前のStepResultがない場合でも、
     * 現在のCPU状態を画面に表示したい。
     * そのため、WebMipsSessionが持つCpuから現在値を取り出す。
     *
     * @param mipsSession Web版の実行状態
     * @return レジスタ現在値一覧
     */
    private List<RegisterValue> createCurrentRegisterValues(WebMipsSession mipsSession) {
        List<RegisterValue> values = new ArrayList<>();

        for (int i = 0; i < 32; i++) {
            values.add(new RegisterValue(i, mipsSession.getCpu().getRegister(i), false));
        }

        return values;
    }

    /**
     * 現在のCPU状態から、HI/LO現在値一覧を作成する。
     *
     * @param mipsSession Web版の実行状態
     * @return HI/LO現在値一覧
     */
    private List<HiLoValue> createCurrentHiLoValues(WebMipsSession mipsSession) {
        return List.of(
                new HiLoValue("HI", mipsSession.getCpu().getHi(), false),
                new HiLoValue("LO", mipsSession.getCpu().getLo(), false));
    }

    /**
     * 現在のCPU状態から、メモリ現在値一覧を作成する。
     *
     * Cpu#copyMemory()でメモリ内容のコピーを取得し、
     * Web画面で表示する先頭32バイト分だけMemoryValueに変換する。
     *
     * @param mipsSession Web版の実行状態
     * @return メモリ現在値一覧
     */
    private List<MemoryValue> createCurrentMemoryValues(WebMipsSession mipsSession) {
        byte[] memory = mipsSession.getCpu().copyMemory();
        List<MemoryValue> values = new ArrayList<>();

        int displaySize = Math.min(32, memory.length);

        for (int address = 0; address < displaySize; address++) {
            values.add(new MemoryValue(address, memory[address], false));
        }

        return values;
    }

    /**
     * 入力欄クリア後のViewModelを作成する。
     *
     * プログラム入力欄を空にし、実行状態も未解析状態として表示する。
     * CPU状態は初期値として0を表示する。
     *
     * @param message 画面に表示するメッセージ
     * @return 入力欄クリア後のViewModel
     */
    public MipsViewModel createClearedViewModel(String message) {
        return MipsViewModel.builder()
                .programText("")
                .programLines(List.of())
                .message(message)
                .messageType(MessageType.INFO)
                .parseSuccess(false)
                .instructionCount(0)
                .readyToRun(false)
                .currentPc(-1)
                .registerValues(createInitialRegisterValues())
                .hiLoValues(createInitialHiLoValues())
                .memoryValues(createInitialMemoryValues())
                .build();
    }
}