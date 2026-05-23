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
                    true,
                    mipsSessionService.getInstructionCount(mipsSession),
                    readyToRun);
        } catch (IllegalArgumentException e) {
            session.removeAttribute("mipsSession");

            addParsedModel(
                    model,
                    programText,
                    programLines,
                    "パース失敗: " + e.getMessage(),
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

        List<HiLoDiff> hiLoDiffs = stepResultViewMapper.createHiLoDiffs(result);

        String executedInstructionText = stepResultViewMapper.getExecutedInstructionText(result, programLines);

        addStepResultModel(
                model,
                mipsSession,
                result,
                readyToRun,
                registerDiffs,
                hiLoDiffs,
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

        String programText = newSession.getProgramText();
        List<String> programLines = mipsSessionService.splitLines(programText);
        boolean readyToRun = mipsSessionService.canStep(newSession);

        addParsedModel(
                model,
                programText,
                programLines,
                "実行状態をリセットしました。",
                true,
                mipsSessionService.getInstructionCount(newSession),
                readyToRun);

        return "mips";
    }

    /**
     * 初期表示用のModel属性を設定する。
     *
     * /mips に最初にアクセスしたときや、
     * 実行状態が存在しない場合に使う。
     *
     * @param model HTMLテンプレートへデータを渡すための入れ物
     */
    private void addInitialModel(Model model) {
        List<String> programLines = mipsSessionService.splitLines(DEFAULT_PROGRAM);

        model.addAttribute("programText", DEFAULT_PROGRAM);
        model.addAttribute("programLines", programLines);
        model.addAttribute("parseMessage", null);
        model.addAttribute("parseSuccess", null);
        model.addAttribute("instructionCount", 0);
        model.addAttribute("readyToRun", false);
        model.addAttribute("registerDiffs", List.of());
        model.addAttribute("hiLoDiffs", List.of());
        model.addAttribute("currentPc", 0);
        model.addAttribute("executedPcs", Set.of());
    }

    /**
     * 実行状態が存在しない場合のModel属性を設定する。
     *
     * たとえば、セッションが切れた状態で1ステップ実行やリセットを押した場合に使う。
     *
     * @param model   HTMLテンプレートへデータを渡すための入れ物
     * @param message 画面に表示するメッセージ
     */
    private void addNoSessionModel(Model model, String message) {
        List<String> programLines = mipsSessionService.splitLines(DEFAULT_PROGRAM);

        model.addAttribute("programText", DEFAULT_PROGRAM);
        model.addAttribute("programLines", programLines);
        model.addAttribute("parseMessage", message);
        model.addAttribute("parseSuccess", false);
        model.addAttribute("instructionCount", 0);
        model.addAttribute("readyToRun", false);
        model.addAttribute("registerDiffs", List.of());
        model.addAttribute("hiLoDiffs", List.of());
        model.addAttribute("currentPc", -1);
        model.addAttribute("executedPcs", Set.of());
    }

    /**
     * パース後のModel属性を設定する。
     *
     * @param model            HTMLテンプレートへデータを渡すための入れ物
     * @param programText      ユーザーが入力したプログラム文字列
     * @param programLines     行ごとに分割したプログラム文字列
     * @param message          画面に表示するメッセージ
     * @param parseSuccess     パースに成功した場合はtrue
     * @param instructionCount 解析できた命令数
     * @param readyToRun       1ステップ実行できる状態ならtrue
     */
    private void addParsedModel(
            Model model,
            String programText,
            List<String> programLines,
            String message,
            boolean parseSuccess,
            int instructionCount,
            boolean readyToRun) {

        model.addAttribute("programText", programText);
        model.addAttribute("programLines", programLines);
        model.addAttribute("parseMessage", message);
        model.addAttribute("parseSuccess", parseSuccess);
        model.addAttribute("instructionCount", instructionCount);
        model.addAttribute("readyToRun", readyToRun);
        model.addAttribute("registerDiffs", List.of());
        model.addAttribute("hiLoDiffs", List.of());
        model.addAttribute("currentPc", readyToRun ? 0 : -1);
        model.addAttribute("executedPcs", Set.of());
    }

    /**
     * 1ステップ実行後のModel属性を設定する。
     *
     * @param model                   HTMLテンプレートへデータを渡すための入れ物
     * @param mipsSession             Web版の実行状態
     * @param result                  1ステップ分の実行結果
     * @param readyToRun              次の命令を実行できる場合はtrue
     * @param registerDiffs           レジスタ変更差分
     * @param hiLoDiffs               HI/LO変更差分
     * @param executedInstructionText 実行した命令の表示文字列
     */
    private void addStepResultModel(
            Model model,
            WebMipsSession mipsSession,
            StepResult result,
            boolean readyToRun,
            List<RegisterDiff> registerDiffs,
            List<HiLoDiff> hiLoDiffs,
            String executedInstructionText) {

        model.addAttribute("programText", mipsSession.getProgramText());
        model.addAttribute("programLines", mipsSessionService.splitLines(mipsSession.getProgramText()));

        if (readyToRun) {
            model.addAttribute("parseMessage", "実行中: 1ステップ実行しました。");
        } else {
            model.addAttribute("parseMessage", "プログラムが終了しました。");
        }

        model.addAttribute("parseSuccess", true);
        model.addAttribute("instructionCount", mipsSession.getProgram().size());
        model.addAttribute("readyToRun", readyToRun);
        model.addAttribute("stepResult", result);
        model.addAttribute("registerDiffs", registerDiffs);
        model.addAttribute("hiLoDiffs", hiLoDiffs);
        model.addAttribute("executedInstructionText", executedInstructionText);
        model.addAttribute("executedPcs", mipsSession.getExecutedPcs());

        int currentPc = readyToRun ? result.getPcAfter() : -1;
        model.addAttribute("currentPc", currentPc);
    }
}