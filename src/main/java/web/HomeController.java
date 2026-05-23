package web;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

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

            WebMipsSession mipsSession = WebMipsSession.create(instructions);
            session.setAttribute("mipsSession", mipsSession);

            model.addAttribute("instructionCount", instructions.size());
            model.addAttribute("parseMessage", "パース成功: " + instructions.size() + " 命令");
            model.addAttribute("parseSuccess", true);
            model.addAttribute("readyToRun", true);
        } catch (IllegalArgumentException e) {
            session.removeAttribute("mipsSession");

            model.addAttribute("instructionCount", 0);
            model.addAttribute("parseMessage", "パース失敗: " + e.getMessage());
            model.addAttribute("parseSuccess", false);
            model.addAttribute("readyToRun", false);
        }

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
}