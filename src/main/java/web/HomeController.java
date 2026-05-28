package web;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import execution.StepResult;
import jakarta.servlet.http.HttpSession;

/**
 * MipsStepLab Web版の画面を表示するController。
 *
 * Controllerは、ブラウザからのリクエストを受け取り、
 * 表示に必要なデータをHTMLテンプレートへ渡す役割を持つ。
 */
@Controller
public class HomeController {

    /** 画面に初期表示するサンプルプログラム。 */
    private static final String DEFAULT_PROGRAM = String.join(System.lineSeparator(),
            "addi $t0, $zero, 5",
            "addi $t1, $zero, 3",
            "add $t2, $t0, $t1");

    /** Web版MipsStepLabの実行状態を扱うService。 */
    private final WebMipsSessionService mipsSessionService;

    /** StepResultをWeb表示用データに変換するMapper。 */
    private final StepResultViewMapper stepResultViewMapper;

    /**
     * HomeControllerを生成する。
     *
     * @param mipsSessionService   Web版MipsStepLabの実行状態を扱うService
     * @param stepResultViewMapper StepResultをWeb表示用データに変換するMapper
     */
    public HomeController(
            WebMipsSessionService mipsSessionService,
            StepResultViewMapper stepResultViewMapper) {

        this.mipsSessionService = mipsSessionService;
        this.stepResultViewMapper = stepResultViewMapper;
    }

    /**
     * MipsStepLab Web版のトップ画面を表示する。
     *
     * 初回表示時は、サンプルプログラムを入力欄に表示する。
     *
     * @param model HTMLテンプレートへデータを渡すための入れ物
     * @return 表示するテンプレート名
     */
    @GetMapping("/mips")
    public String home(Model model) {
        addInitialModel(model);

        return "mips";
    }

    /**
     * 入力されたMIPSプログラムを受け取り、命令として解析する。
     *
     * パースに成功した場合は、CpuとStepRunnerを作成し、
     * WebMipsSessionとしてHTTPセッションに保存する。
     *
     * @param programText textareaから送信されたプログラム文字列
     * @param model       HTMLテンプレートへデータを渡すための入れ物
     * @param session     ブラウザ利用者ごとの状態を保存するHTTPセッション
     * @return 表示するテンプレート名
     */
    @PostMapping("/mips")
    public String submitProgram(String programText, Model model, HttpSession session) {
        List<String> programLines = mipsSessionService.splitLines(programText);

        try {
            WebMipsSession mipsSession = mipsSessionService.createSession(programText);
            boolean readyToRun = mipsSessionService.canStep(mipsSession);

            if (readyToRun) {
                session.setAttribute("mipsSession", mipsSession);
            } else {
                session.removeAttribute("mipsSession");
            }

            String message = readyToRun
                    ? "パース成功: " + mipsSessionService.getInstructionCount(mipsSession) + " 命令"
                    : "パース成功しましたが、実行できる命令がありません。";

            addParsedModel(
                    model,
                    programText,
                    programLines,
                    message,
                    MessageType.SUCCESS,
                    true,
                    mipsSessionService.getInstructionCount(mipsSession),
                    readyToRun);
        } catch (IllegalArgumentException e) {
            session.removeAttribute("mipsSession");

            addParsedModel(
                    model,
                    programText,
                    programLines,
                    "入力エラー: " + e.getMessage(),
                    MessageType.ERROR,
                    false,
                    0,
                    false);
        }

        return "mips";
    }

    /**
     * 現在の実行状態から1命令だけ実行する。
     *
     * HttpSessionに保存してあるWebMipsSessionを取り出し、
     * StepRunnerを使って1ステップだけ進める。
     *
     * @param model   HTMLテンプレートへデータを渡すための入れ物
     * @param session ブラウザ利用者ごとの状態を保存するHTTPセッション
     * @return 表示するテンプレート名
     */
    @PostMapping("/mips/step")
    public String step(Model model, HttpSession session) {
        WebMipsSession mipsSession = (WebMipsSession) session.getAttribute("mipsSession");

        if (mipsSession == null) {
            addNoSessionModel(model, "実行状態がありません。先にプログラムを解析してください。");

            return "mips";
        }

        StepResult result = mipsSessionService.step(mipsSession);
        boolean readyToRun = mipsSessionService.canStep(mipsSession);

        List<String> programLines = mipsSessionService.splitLines(mipsSession.getProgramText());

        List<RegisterDiff> registerDiffs = stepResultViewMapper.createRegisterDiffs(result);

        List<RegisterValue> registerValues = stepResultViewMapper.createRegisterValues(result);

        List<HiLoDiff> hiLoDiffs = stepResultViewMapper.createHiLoDiffs(result);

        List<HiLoValue> hiLoValues = stepResultViewMapper.createHiLoValues(result);

        List<MemoryDiff> memoryDiffs = stepResultViewMapper.createMemoryDiffs(result);

        String executedInstructionText = stepResultViewMapper.getExecutedInstructionText(result, programLines);

        String message = readyToRun
                ? "実行中: 1ステップ実行しました。"
                : "プログラムが終了しました。";

        MessageType messageType = readyToRun
                ? MessageType.INFO
                : MessageType.SUCCESS;

        addStepResultModel(
                model,
                mipsSession,
                result,
                message,
                messageType,
                registerDiffs,
                registerValues,
                hiLoDiffs,
                hiLoValues,
                memoryDiffs,
                executedInstructionText);

        return "mips";
    }

    /**
     * 現在のプログラムを最初から実行し直せる状態に戻す。
     *
     * 入力されたプログラム文字列はそのまま使い、
     * CpuとStepRunnerだけを新しく作り直す。
     *
     * @param model   HTMLテンプレートへデータを渡すための入れ物
     * @param session ブラウザ利用者ごとの状態を保存するHTTPセッション
     * @return 表示するテンプレート名
     */
    @PostMapping("/mips/reset")
    public String reset(Model model, HttpSession session) {
        WebMipsSession oldSession = (WebMipsSession) session.getAttribute("mipsSession");

        if (oldSession == null) {
            addNoSessionModel(model, "実行状態がありません。先にプログラムを解析してください。");
            return "mips";
        }

        WebMipsSession newSession = mipsSessionService.resetSession(oldSession);
        session.setAttribute("mipsSession", newSession);

        addSessionStateModel(
                model,
                newSession,
                "実行状態をリセットしました。",
                MessageType.INFO);

        return "mips";
    }

    /**
     * 初期表示用のModel属性を設定する。
     *
     * /mips に最初にアクセスしたときに使う。
     *
     * @param model HTMLテンプレートへデータを渡すための入れ物
     */
    private void addInitialModel(Model model) {
        List<String> programLines = mipsSessionService.splitLines(DEFAULT_PROGRAM);

        MipsViewModel viewModel = new MipsViewModel(
                DEFAULT_PROGRAM,
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

        addViewModel(model, viewModel);
    }

    /**
     * 実行状態が存在しない場合のModel属性を設定する。
     *
     * セッションが切れた状態で1ステップ実行やリセットを押した場合に使う。
     *
     * @param model   HTMLテンプレートへデータを渡すための入れ物
     * @param message 画面に表示するメッセージ
     */
    private void addNoSessionModel(Model model, String message) {
        List<String> programLines = mipsSessionService.splitLines(DEFAULT_PROGRAM);

        MipsViewModel viewModel = new MipsViewModel(
                DEFAULT_PROGRAM,
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

        addViewModel(model, viewModel);
    }

    /**
     * パース後または操作後のModel属性を設定する。
     *
     * 新しいプログラムを解析した直後や、パース失敗時に使う。
     * この時点では、ステップ実行結果はまだ表示しないため、
     * 差分表示やCPU状態表示は空にしている。
     *
     * @param model            HTMLテンプレートへデータを渡すための入れ物
     * @param programText      ユーザーが入力したプログラム文字列
     * @param programLines     行ごとに分割したプログラム文字列
     * @param message          画面に表示するメッセージ
     * @param messageType      メッセージ種別
     * @param parseSuccess     パースまたは操作に成功した場合はtrue
     * @param instructionCount 解析できた命令数
     * @param readyToRun       1ステップ実行できる状態ならtrue
     */
    private void addParsedModel(
            Model model,
            String programText,
            List<String> programLines,
            String message,
            MessageType messageType,
            boolean parseSuccess,
            int instructionCount,
            boolean readyToRun) {

        MipsViewModel viewModel = new MipsViewModel(
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

        addViewModel(model, viewModel);
    }

    /**
     * 1ステップ実行後、またはrun実行後のModel属性を設定する。
     *
     * StepResultと各種表示用データをMipsViewModelにまとめ、
     * 画面へ渡す。
     *
     * @param model                   HTMLテンプレートへデータを渡すための入れ物
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
     */
    private void addStepResultModel(
            Model model,
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

        MipsViewModel viewModel = new MipsViewModel(
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

        addViewModel(model, viewModel);
    }

    /**
     * ブレークポイントを追加する。
     *
     * @param breakpointPc ブレークポイントとして追加するPC番号
     * @param model        HTMLテンプレートへデータを渡すための入れ物
     * @param session      ブラウザ利用者ごとの状態を保存するHTTPセッション
     * @return 表示するテンプレート名
     */
    @PostMapping("/mips/breakpoints")
    public String addBreakpoint(Integer breakpointPc, Model model, HttpSession session) {
        WebMipsSession mipsSession = (WebMipsSession) session.getAttribute("mipsSession");

        if (mipsSession == null) {
            addNoSessionModel(model, "実行状態がありません。先にプログラムを解析してください。");
            return "mips";
        }

        if (breakpointPc == null) {
            addSessionStateModel(
                    model,
                    mipsSession,
                    "PC番号を入力してください。",
                    MessageType.ERROR);
            return "mips";
        }

        try {
            mipsSessionService.addBreakpoint(mipsSession, breakpointPc);

            addSessionStateModel(
                    model,
                    mipsSession,
                    "ブレークポイントを追加しました: PC " + breakpointPc,
                    MessageType.SUCCESS);
        } catch (IllegalArgumentException e) {
            addSessionStateModel(
                    model,
                    mipsSession,
                    "ブレークポイント追加失敗: " + e.getMessage(),
                    MessageType.ERROR);
        }

        return "mips";
    }

    /**
     * ブレークポイントを削除する。
     *
     * @param breakpointPc 削除するPC番号
     * @param model        HTMLテンプレートへデータを渡すための入れ物
     * @param session      ブラウザ利用者ごとの状態を保存するHTTPセッション
     * @return 表示するテンプレート名
     */
    @PostMapping("/mips/breakpoints/delete")
    public String deleteBreakpoint(Integer breakpointPc, Model model, HttpSession session) {
        WebMipsSession mipsSession = (WebMipsSession) session.getAttribute("mipsSession");

        if (mipsSession == null) {
            addNoSessionModel(model, "実行状態がありません。先にプログラムを解析してください。");
            return "mips";
        }

        if (breakpointPc == null) {
            addSessionStateModel(
                    model,
                    mipsSession,
                    "削除するPC番号を入力してください。",
                    MessageType.ERROR);
            return "mips";
        }

        boolean removed = mipsSessionService.removeBreakpoint(mipsSession, breakpointPc);

        if (removed) {
            addSessionStateModel(
                    model,
                    mipsSession,
                    "ブレークポイントを削除しました: PC " + breakpointPc,
                    MessageType.SUCCESS);
        } else {
            addSessionStateModel(
                    model,
                    mipsSession,
                    "ブレークポイントは登録されていません: PC " + breakpointPc,
                    MessageType.WARNING);
        }

        return "mips";
    }

    /**
     * ブレークポイントまたはプログラム終了まで連続実行する。
     *
     * @param model   HTMLテンプレートへデータを渡すための入れ物
     * @param session ブラウザ利用者ごとの状態を保存するHTTPセッション
     * @return 表示するテンプレート名
     */
    @PostMapping("/mips/run")
    public String run(Model model, HttpSession session) {
        WebMipsSession mipsSession = (WebMipsSession) session.getAttribute("mipsSession");

        if (mipsSession == null) {
            addNoSessionModel(model, "実行状態がありません。先にプログラムを解析してください。");

            return "mips";
        }

        RunResult runResult = mipsSessionService.runUntilBreakpoint(mipsSession);
        StepResult result = runResult.getLastStepResult();

        boolean readyToRun = mipsSessionService.canStep(mipsSession);

        if (result == null) {
            addSessionStateModel(
                    model,
                    mipsSession,
                    createRunMessage(runResult),
                    MessageType.INFO);

            return "mips";
        }
        String message = createRunMessage(runResult);

        MessageType messageType = readyToRun
                ? MessageType.INFO
                : MessageType.SUCCESS;

        List<String> programLines = mipsSessionService.splitLines(mipsSession.getProgramText());

        List<RegisterDiff> registerDiffs = stepResultViewMapper.createRegisterDiffs(result);

        List<RegisterValue> registerValues = stepResultViewMapper.createRegisterValues(result);

        List<HiLoDiff> hiLoDiffs = stepResultViewMapper.createHiLoDiffs(result);

        List<HiLoValue> hiLoValues = stepResultViewMapper.createHiLoValues(result);

        List<MemoryDiff> memoryDiffs = stepResultViewMapper.createMemoryDiffs(result);

        String executedInstructionText = stepResultViewMapper.getExecutedInstructionText(result, programLines);

        addStepResultModel(
                model,
                mipsSession,
                result,
                message,
                messageType,
                registerDiffs,
                registerValues,
                hiLoDiffs,
                hiLoValues,
                memoryDiffs,
                executedInstructionText);

        model.addAttribute("message", createRunMessage(runResult));
        model.addAttribute("messageType", messageType.getCssClassName());

        return "mips";
    }

    /**
     * run実行結果の表示メッセージを作成する。
     *
     * @param runResult 連続実行の結果
     * @return 画面に表示するメッセージ
     */
    private String createRunMessage(RunResult runResult) {
        return runResult.getMessage()
                + "（実行ステップ数: " + runResult.getExecutedStepCount() + "）";
    }

    /**
     * 現在のWeb実行状態をModelに設定する。
     *
     * WebMipsSessionが存在する場合に、
     * 入力プログラム、命令一覧、実行済みPC、ブレークポイントなど、
     * 画面表示に必要な共通情報をまとめて設定する。
     *
     * このメソッドでは、ステップ実行結果や差分表示は空にする。
     * ステップ実行後の差分表示が必要な場合は addStepResultModel を使う。
     *
     * @param model       HTMLテンプレートへデータを渡すための入れ物
     * @param mipsSession Web版の実行状態
     * @param message     画面に表示するメッセージ
     * @param messageType メッセージ種別
     */
    private void addSessionStateModel(
            Model model,
            WebMipsSession mipsSession,
            String message,
            MessageType messageType) {

        String programText = mipsSession.getProgramText();
        List<String> programLines = mipsSessionService.splitLines(programText);
        boolean readyToRun = mipsSessionService.canStep(mipsSession);
        int currentPc = readyToRun ? mipsSession.getStepRunner().getPc() : -1;

        MipsViewModel viewModel = new MipsViewModel(
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

        addViewModel(model, viewModel);
    }

    /**
     * ViewModelをModelへ設定する。
     *
     * Thymeleafテンプレートでは、viewModel.xxx の形で値を参照する。
     *
     * @param model     HTMLテンプレートへデータを渡すための入れ物
     * @param viewModel 画面表示用データ
     */
    private void addViewModel(Model model, MipsViewModel viewModel) {
        model.addAttribute("viewModel", viewModel);
    }
}