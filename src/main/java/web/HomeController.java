package web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import execution.StepResult;
import instruction.Instruction;
import jakarta.servlet.http.HttpSession;
import parser.InstructionParser;

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
        List<String> programLines = splitLines(DEFAULT_PROGRAM);

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

        return "mips";
    }

    /**
     * 入力されたMIPSプログラムを受け取り、命令として解析する。
     *
     * パースに成功した場合は、CpuとStepRunnerを作成し、
     * WebMipsSessionとしてHTTPセッションに保存する。
     *
     * これにより、次回以降のリクエストでも同じ実行状態を使える。
     *
     * @param programText textareaから送信されたプログラム文字列
     * @param model       HTMLテンプレートへデータを渡すための入れ物
     * @param session     ブラウザ利用者ごとの状態を保存するHTTPセッション
     * @return 表示するテンプレート名
     */
    @PostMapping("/mips")
    public String submitProgram(String programText, Model model, HttpSession session) {
        List<String> programLines = splitLines(programText);

        model.addAttribute("programText", programText);
        model.addAttribute("programLines", programLines);

        try {
            List<Instruction> instructions = parseProgram(programLines);

            boolean readyToRun = !instructions.isEmpty();

            model.addAttribute("instructionCount", instructions.size());

            if (readyToRun) {
                WebMipsSession mipsSession = WebMipsSession.create(programText, instructions);
                session.setAttribute("mipsSession", mipsSession);
            } else {
                session.removeAttribute("mipsSession");
            }

            model.addAttribute("parseSuccess", true);
            model.addAttribute("readyToRun", readyToRun);
            model.addAttribute("currentPc", readyToRun ? 0 : -1);
        } catch (IllegalArgumentException e) {
            session.removeAttribute("mipsSession");

            model.addAttribute("instructionCount", 0);
            model.addAttribute("parseMessage", "パース失敗: " + e.getMessage());
            model.addAttribute("parseSuccess", false);
            model.addAttribute("readyToRun", false);
            model.addAttribute("currentPc", -1);
        }

        model.addAttribute("registerDiffs", List.of());
        model.addAttribute("hiLoDiffs", List.of());
        model.addAttribute("executedPcs", Set.of());

        return "mips";
    }

    /**
     * 行ごとの文字列をInstructionのリストに変換する。
     *
     * InstructionParserは、プログラム全体をまとめて解析する。
     * これは、ラベル定義と分岐先を対応付けるため。
     *
     * @param programLines MIPS命令を表す文字列のリスト
     * @return 解析されたInstructionのリスト
     */
    private List<Instruction> parseProgram(List<String> programLines) {
        InstructionParser parser = new InstructionParser();

        return parser.parse(programLines);
    }

    /**
     * 入力されたプログラム文字列を行ごとのリストに変換する。
     *
     * 空行は命令として扱わないため、除外している。
     *
     * @param text 入力されたプログラム文字列
     * @return 空行を除いた命令行のリスト
     */
    private List<String> splitLines(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }

        return Arrays.stream(text.split("\\R"))
                .map(String::trim)
                .filter(line -> !line.isEmpty())
                .toList();
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
            model.addAttribute("programText", DEFAULT_PROGRAM);
            model.addAttribute("programLines", splitLines(DEFAULT_PROGRAM));
            model.addAttribute("parseMessage", "実行状態がありません。先にプログラムを解析してください。");
            model.addAttribute("parseSuccess", false);
            model.addAttribute("instructionCount", 0);
            model.addAttribute("readyToRun", false);
            model.addAttribute("registerDiffs", List.of());
            model.addAttribute("hiLoDiffs", List.of());
            model.addAttribute("currentPc", -1);
            model.addAttribute("executedPcs", Set.of());

            return "mips";
        }

        StepResult result = mipsSession.getStepRunner().step();
        mipsSession.markExecuted(result.getPcBefore());

        boolean readyToRun = mipsSession.getStepRunner().hasNext();

        List<RegisterDiff> registerDiffs = createRegisterDiffs(result);
        List<HiLoDiff> hiLoDiffs = createHiLoDiffs(result);
        String executedInstructionText = getExecutedInstructionText(result, mipsSession.getProgramText());

        model.addAttribute("programText", mipsSession.getProgramText());
        model.addAttribute("programLines", splitLines(mipsSession.getProgramText()));

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

        return "mips";
    }

    /**
     * StepResultからレジスタ変更差分のリストを作成する。
     *
     * 実行前と実行後のレジスタ配列を比較し、
     * 値が変わったレジスタだけをRegisterDiffとして返す。
     *
     * @param result 1ステップ分の実行結果
     * @return 変更されたレジスタの差分リスト
     */
    private List<RegisterDiff> createRegisterDiffs(StepResult result) {
        int[] before = result.getRegistersBefore();
        int[] after = result.getRegistersAfter();

        List<RegisterDiff> diffs = new java.util.ArrayList<>();

        for (int i = 0; i < before.length; i++) {
            if (before[i] != after[i]) {
                diffs.add(new RegisterDiff(i, before[i], after[i]));
            }
        }

        return diffs;
    }

    /**
     * StepResultからHI/LOレジスタ変更差分のリストを作成する。
     *
     * 実行前と実行後のHI/LOレジスタを比較し、
     * 値が変わったものだけをHiLoDiffとして返す。
     *
     * @param result 1ステップ分の実行結果
     * @return 変更されたHI/LOレジスタの差分リスト
     */
    private List<HiLoDiff> createHiLoDiffs(StepResult result) {
        List<HiLoDiff> diffs = new ArrayList<>();

        if (result.getHiBefore() != result.getHiAfter()) {
            diffs.add(new HiLoDiff("HI", result.getHiBefore(), result.getHiAfter()));
        }

        if (result.getLoBefore() != result.getLoAfter()) {
            diffs.add(new HiLoDiff("LO", result.getLoBefore(), result.getLoAfter()));
        }

        return diffs;
    }

    /**
     * 実行された命令を画面表示用の文字列として取得する。
     *
     * StepResultにはInstructionオブジェクトが入っているが、
     * Instructionクラス側でtoString()を実装していない場合、
     * クラス名とハッシュ値のような表示になってしまう。
     *
     * そのため、Web画面では元の入力文字列から、
     * 実行前PCに対応する行を取り出して表示する。
     *
     * @param result      1ステップ分の実行結果
     * @param programText ユーザーが入力したMIPSプログラム文字列
     * @return 実行された命令の表示文字列
     */
    private String getExecutedInstructionText(StepResult result, String programText) {
        List<String> programLines = splitLines(programText);
        int pc = result.getPcBefore();

        if (pc < 0 || pc >= programLines.size()) {
            return "";
        }

        return programLines.get(pc);
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
            model.addAttribute("programText", DEFAULT_PROGRAM);
            model.addAttribute("programLines", splitLines(DEFAULT_PROGRAM));
            model.addAttribute("parseMessage", "実行状態がありません。先にプログラムを解析してください。");
            model.addAttribute("parseSuccess", false);
            model.addAttribute("instructionCount", 0);
            model.addAttribute("readyToRun", false);
            model.addAttribute("registerDiffs", List.of());
            model.addAttribute("hiLoDiffs", List.of());
            model.addAttribute("currentPc", -1);
            model.addAttribute("executedPcs", Set.of());

            return "mips";
        }

        String programText = oldSession.getProgramText();
        List<String> programLines = splitLines(programText);
        List<Instruction> instructions = parseProgram(programLines);

        WebMipsSession newSession = WebMipsSession.create(programText, instructions);
        session.setAttribute("mipsSession", newSession);

        boolean readyToRun = !instructions.isEmpty();

        model.addAttribute("programText", programText);
        model.addAttribute("programLines", programLines);
        model.addAttribute("parseMessage", "実行状態をリセットしました。");
        model.addAttribute("parseSuccess", true);
        model.addAttribute("instructionCount", instructions.size());
        model.addAttribute("readyToRun", readyToRun);
        model.addAttribute("registerDiffs", List.of());
        model.addAttribute("hiLoDiffs", List.of());
        model.addAttribute("currentPc", readyToRun ? 0 : -1);
        model.addAttribute("executedPcs", Set.of());

        return "mips";
    }
}