package web;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    /** POST後に戻るMipsStepLab画面のパス。 */
    private static final String REDIRECT_MIPS = "redirect:/";

    /** 画面に初期表示するサンプルプログラム。 */
    private static final String DEFAULT_PROGRAM = String.join(System.lineSeparator(),
            "addi $t0, $zero, 5",
            "addi $t1, $zero, 3",
            "add $t2, $t0, $t1");

    /** Web版MipsStepLabの実行状態を扱うService。 */
    private final WebMipsSessionService mipsSessionService;

    /** StepResultをWeb表示用データに変換するMapper。 */
    private final StepResultViewMapper stepResultViewMapper;

    /** MipsViewModelを生成するFactory。 */
    private final MipsViewModelFactory viewModelFactory;

    /**
     * HomeControllerを生成する。
     *
     * @param mipsSessionService   Web版MipsStepLabの実行状態を扱うService
     * @param stepResultViewMapper StepResultをWeb表示用データに変換するMapper
     * @param viewModelFactory     MipsViewModelを生成するFactory
     */
    public HomeController(
            WebMipsSessionService mipsSessionService,
            StepResultViewMapper stepResultViewMapper,
            MipsViewModelFactory viewModelFactory) {

        this.mipsSessionService = mipsSessionService;
        this.stepResultViewMapper = stepResultViewMapper;
        this.viewModelFactory = viewModelFactory;
    }

    /**
     * MipsStepLab Web版のトップ画面を表示する。
     *
     * POST後のリダイレクトでViewModelが渡されている場合は、そのViewModelを表示する。
     * 初回表示時は、サンプルプログラムを入力欄に表示する。
     *
     * @param model HTMLテンプレートへデータを渡すための入れ物
     * @return 表示するテンプレート名
     */
    @GetMapping("/")
    public String home(Model model) {
        if (!model.containsAttribute("viewModel")) {
            addInitialModel(model);
        }

        return "mips";
    }

    /**
     * 入力されたMIPSプログラムを受け取り、命令として解析する。
     *
     * パースに成功した場合は、CpuとStepRunnerを作成し、
     * WebMipsSessionとしてHTTPセッションに保存する。
     *
     * @param programText        textareaから送信されたプログラム文字列
     * @param redirectAttributes リダイレクト後の画面へデータを渡すための入れ物
     * @param session            ブラウザ利用者ごとの状態を保存するHTTPセッション
     * @return リダイレクト先
     */
    @PostMapping("/")
    public String submitProgram(
            String programText,
            RedirectAttributes redirectAttributes,
            HttpSession session) {

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

            addParsedFlashModel(
                    redirectAttributes,
                    programText,
                    programLines,
                    message,
                    MessageType.SUCCESS,
                    true,
                    mipsSessionService.getInstructionCount(mipsSession),
                    readyToRun);
        } catch (IllegalArgumentException e) {
            session.removeAttribute("mipsSession");

            addParsedFlashModel(
                    redirectAttributes,
                    programText,
                    programLines,
                    "入力エラー: " + e.getMessage(),
                    MessageType.ERROR,
                    false,
                    0,
                    false);
        }

        return REDIRECT_MIPS;
    }

    /**
     * 現在の実行状態から1命令だけ実行する。
     *
     * HttpSessionに保存してあるWebMipsSessionを取り出し、
     * StepRunnerを使って1ステップだけ進める。
     *
     * @param redirectAttributes リダイレクト後の画面へデータを渡すための入れ物
     * @param session            ブラウザ利用者ごとの状態を保存するHTTPセッション
     * @return リダイレクト先
     */
    @PostMapping("/step")
    public String step(RedirectAttributes redirectAttributes, HttpSession session) {
        WebMipsSession mipsSession = (WebMipsSession) session.getAttribute("mipsSession");

        if (mipsSession == null) {
            addNoSessionFlashModel(redirectAttributes, "実行状態がありません。先にプログラムを解析してください。");

            return REDIRECT_MIPS;
        }

        StepResult result = mipsSessionService.step(mipsSession);
        boolean readyToRun = mipsSessionService.canStep(mipsSession);

        List<String> programLines = mipsSessionService.splitLines(mipsSession.getProgramText());

        StepResultViewData viewData = stepResultViewMapper.toViewData(result);

        ExecutedInstructionView executedInstructionView = stepResultViewMapper.createExecutedInstructionView(
                result,
                programLines);

        String message = readyToRun
                ? "実行中: 1ステップ実行しました。"
                : "プログラムが終了しました。";

        MessageType messageType = readyToRun
                ? MessageType.INFO
                : MessageType.SUCCESS;

        addStepResultFlashModel(
                redirectAttributes,
                mipsSession,
                result,
                message,
                messageType,
                viewData,
                executedInstructionView);

        return REDIRECT_MIPS;
    }

    /**
     * 現在のプログラムを最初から実行し直せる状態に戻す。
     *
     * 入力されたプログラム文字列はそのまま使い、
     * CpuとStepRunnerだけを新しく作り直す。
     *
     * @param redirectAttributes リダイレクト後の画面へデータを渡すための入れ物
     * @param session            ブラウザ利用者ごとの状態を保存するHTTPセッション
     * @return リダイレクト先
     */
    @PostMapping("/reset")
    public String reset(RedirectAttributes redirectAttributes, HttpSession session) {
        WebMipsSession oldSession = (WebMipsSession) session.getAttribute("mipsSession");

        if (oldSession == null) {
            addNoSessionFlashModel(redirectAttributes, "実行状態がありません。先にプログラムを解析してください。");
            return REDIRECT_MIPS;
        }

        WebMipsSession newSession = mipsSessionService.resetSession(oldSession);
        session.setAttribute("mipsSession", newSession);

        addSessionStateFlashModel(
                redirectAttributes,
                newSession,
                "実行状態をリセットしました。",
                MessageType.INFO);

        return REDIRECT_MIPS;
    }

    /**
     * ブレークポイントを追加する。
     *
     * @param breakpointPc       ブレークポイントとして追加するPC番号
     * @param redirectAttributes リダイレクト後の画面へデータを渡すための入れ物
     * @param session            ブラウザ利用者ごとの状態を保存するHTTPセッション
     * @return リダイレクト先
     */
    @PostMapping("/breakpoints")
    public String addBreakpoint(
            Integer breakpointPc,
            RedirectAttributes redirectAttributes,
            HttpSession session) {

        WebMipsSession mipsSession = (WebMipsSession) session.getAttribute("mipsSession");

        if (mipsSession == null) {
            addNoSessionFlashModel(redirectAttributes, "実行状態がありません。先にプログラムを解析してください。");
            return REDIRECT_MIPS;
        }

        if (breakpointPc == null) {
            addSessionStateFlashModel(
                    redirectAttributes,
                    mipsSession,
                    "PC番号を入力してください。",
                    MessageType.ERROR);
            return REDIRECT_MIPS;
        }

        try {
            mipsSessionService.addBreakpoint(mipsSession, breakpointPc);

            addSessionStateFlashModel(
                    redirectAttributes,
                    mipsSession,
                    "ブレークポイントを追加しました: PC " + breakpointPc,
                    MessageType.SUCCESS);
        } catch (IllegalArgumentException e) {
            addSessionStateFlashModel(
                    redirectAttributes,
                    mipsSession,
                    "ブレークポイント追加失敗: " + e.getMessage(),
                    MessageType.ERROR);
        }

        return REDIRECT_MIPS;
    }

    /**
     * ブレークポイントを削除する。
     *
     * @param breakpointPc       削除するPC番号
     * @param redirectAttributes リダイレクト後の画面へデータを渡すための入れ物
     * @param session            ブラウザ利用者ごとの状態を保存するHTTPセッション
     * @return リダイレクト先
     */
    @PostMapping("/breakpoints/delete")
    public String deleteBreakpoint(
            Integer breakpointPc,
            RedirectAttributes redirectAttributes,
            HttpSession session) {

        WebMipsSession mipsSession = (WebMipsSession) session.getAttribute("mipsSession");

        if (mipsSession == null) {
            addNoSessionFlashModel(redirectAttributes, "実行状態がありません。先にプログラムを解析してください。");
            return REDIRECT_MIPS;
        }

        if (breakpointPc == null) {
            addSessionStateFlashModel(
                    redirectAttributes,
                    mipsSession,
                    "削除するPC番号を入力してください。",
                    MessageType.ERROR);
            return REDIRECT_MIPS;
        }

        boolean removed = mipsSessionService.removeBreakpoint(mipsSession, breakpointPc);

        if (removed) {
            addSessionStateFlashModel(
                    redirectAttributes,
                    mipsSession,
                    "ブレークポイントを削除しました: PC " + breakpointPc,
                    MessageType.SUCCESS);
        } else {
            addSessionStateFlashModel(
                    redirectAttributes,
                    mipsSession,
                    "ブレークポイントは登録されていません: PC " + breakpointPc,
                    MessageType.WARNING);
        }

        return REDIRECT_MIPS;
    }

    /**
     * ブレークポイントまたはプログラム終了まで連続実行する。
     *
     * @param redirectAttributes リダイレクト後の画面へデータを渡すための入れ物
     * @param session            ブラウザ利用者ごとの状態を保存するHTTPセッション
     * @return リダイレクト先
     */
    @PostMapping("/run")
    public String run(RedirectAttributes redirectAttributes, HttpSession session) {
        WebMipsSession mipsSession = (WebMipsSession) session.getAttribute("mipsSession");

        if (mipsSession == null) {
            addNoSessionFlashModel(redirectAttributes, "実行状態がありません。先にプログラムを解析してください。");

            return REDIRECT_MIPS;
        }

        RunResult runResult = mipsSessionService.runUntilBreakpoint(mipsSession);
        StepResult result = runResult.getLastStepResult();

        boolean readyToRun = mipsSessionService.canStep(mipsSession);

        if (result == null) {
            addSessionStateFlashModel(
                    redirectAttributes,
                    mipsSession,
                    createRunMessage(runResult),
                    getRunMessageType(runResult, readyToRun));

            return REDIRECT_MIPS;
        }

        List<String> programLines = mipsSessionService.splitLines(mipsSession.getProgramText());

        StepResultViewData viewData = stepResultViewMapper.toViewData(result);

        ExecutedInstructionView executedInstructionView = stepResultViewMapper.createExecutedInstructionView(
                result,
                programLines);

        String message = createRunMessage(runResult);

        MessageType messageType = getRunMessageType(runResult, readyToRun);

        addStepResultFlashModel(
                redirectAttributes,
                mipsSession,
                result,
                message,
                messageType,
                viewData,
                executedInstructionView);

        return REDIRECT_MIPS;
    }

    /**
     * 入力欄とWeb実行状態をクリアする。
     *
     * textareaだけをブラウザ側で消すと、次回のPOST時に
     * セッション内のプログラムが再表示されてしまう。
     * そのため、入力欄のクリア時にはセッション内の実行状態も削除する。
     *
     * @param redirectAttributes リダイレクト後の画面へデータを渡すための入れ物
     * @param session            ブラウザ利用者ごとの状態を保存するHTTPセッション
     * @return リダイレクト先
     */
    @PostMapping("/clear")
    public String clear(RedirectAttributes redirectAttributes, HttpSession session) {
        session.removeAttribute("mipsSession");

        MipsViewModel viewModel = viewModelFactory.createClearedViewModel(
                "入力欄をクリアしました。");

        addFlashViewModel(redirectAttributes, viewModel);

        return REDIRECT_MIPS;
    }

    /**
     * run実行結果に対応するメッセージ種別を返す。
     *
     * 最大実行ステップ数に到達した場合は、
     * プログラムが終了していないため警告として扱う。
     *
     * @param runResult  連続実行の結果
     * @param readyToRun 次の命令を実行できる状態ならtrue
     * @return 画面に表示するメッセージ種別
     */
    private MessageType getRunMessageType(RunResult runResult, boolean readyToRun) {
        if (runResult.isMaxStepsReached()) {
            return MessageType.WARNING;
        }

        return readyToRun
                ? MessageType.INFO
                : MessageType.SUCCESS;
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
     * 初期表示用のModel属性を設定する。
     *
     * @param model HTMLテンプレートへデータを渡すための入れ物
     */
    private void addInitialModel(Model model) {
        MipsViewModel viewModel = viewModelFactory.createInitialViewModel(DEFAULT_PROGRAM);

        addViewModel(model, viewModel);
    }

    /**
     * 実行状態が存在しない場合のFlash属性を設定する。
     *
     * @param redirectAttributes リダイレクト後の画面へデータを渡すための入れ物
     * @param message            画面に表示するメッセージ
     */
    private void addNoSessionFlashModel(RedirectAttributes redirectAttributes, String message) {
        MipsViewModel viewModel = viewModelFactory.createNoSessionViewModel(DEFAULT_PROGRAM, message);

        addFlashViewModel(redirectAttributes, viewModel);
    }

    /**
     * パース後または操作後のFlash属性を設定する。
     *
     * @param redirectAttributes リダイレクト後の画面へデータを渡すための入れ物
     * @param programText        ユーザーが入力したプログラム文字列
     * @param programLines       行ごとに分割したプログラム文字列
     * @param message            画面に表示するメッセージ
     * @param messageType        メッセージ種別
     * @param parseSuccess       パースまたは操作に成功した場合はtrue
     * @param instructionCount   解析できた命令数
     * @param readyToRun         1ステップ実行できる状態ならtrue
     */
    private void addParsedFlashModel(
            RedirectAttributes redirectAttributes,
            String programText,
            List<String> programLines,
            String message,
            MessageType messageType,
            boolean parseSuccess,
            int instructionCount,
            boolean readyToRun) {

        MipsViewModel viewModel = viewModelFactory.createParsedViewModel(
                programText,
                programLines,
                message,
                messageType,
                parseSuccess,
                instructionCount,
                readyToRun);

        addFlashViewModel(redirectAttributes, viewModel);
    }

    /**
     * 1ステップ実行後、またはrun実行後のFlash属性を設定する。
     *
     * @param redirectAttributes      リダイレクト後の画面へデータを渡すための入れ物
     * @param mipsSession             Web版の実行状態
     * @param result                  1ステップ分の実行結果
     * @param message                 画面に表示するメッセージ
     * @param messageType             メッセージ種別
     * @param viewData                StepResultから作成したWeb表示用データ
     * @param executedInstructionView 実行命令の表示用データ
     */
    private void addStepResultFlashModel(
            RedirectAttributes redirectAttributes,
            WebMipsSession mipsSession,
            StepResult result,
            String message,
            MessageType messageType,
            StepResultViewData viewData,
            ExecutedInstructionView executedInstructionView) {

        MipsViewModel viewModel = viewModelFactory.createStepResultViewModel(
                mipsSession,
                result,
                message,
                messageType,
                viewData,
                executedInstructionView);

        addFlashViewModel(redirectAttributes, viewModel);
    }

    /**
     * 現在のWeb実行状態をFlash属性に設定する。
     *
     * @param redirectAttributes リダイレクト後の画面へデータを渡すための入れ物
     * @param mipsSession        Web版の実行状態
     * @param message            画面に表示するメッセージ
     * @param messageType        メッセージ種別
     */
    private void addSessionStateFlashModel(
            RedirectAttributes redirectAttributes,
            WebMipsSession mipsSession,
            String message,
            MessageType messageType) {

        MipsViewModel viewModel = viewModelFactory.createSessionStateViewModel(
                mipsSession,
                message,
                messageType);

        addFlashViewModel(redirectAttributes, viewModel);
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

    /**
     * ViewModelをリダイレクト後のFlash属性へ設定する。
     *
     * @param redirectAttributes リダイレクト後の画面へデータを渡すための入れ物
     * @param viewModel          画面表示用データ
     */
    private void addFlashViewModel(RedirectAttributes redirectAttributes, MipsViewModel viewModel) {
        redirectAttributes.addFlashAttribute("viewModel", viewModel);
    }
}
