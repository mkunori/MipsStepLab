package console;

import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import execution.BreakpointManager;
import execution.StepResult;
import execution.StepRunner;
import instruction.Instruction;

/**
 * コンソール上でステップ実行を操作するクラス。
 * 
 * このクラスは、ユーザーからコマンドを受け取り、
 * StepRunnerを使って命令を実行し、ConsoleStepViewで結果を表示する。
 * 
 * 実際にCPUを動かす処理はStepRunnerに任せる。
 * このクラスは、CUI用の入力と操作の流れだけを担当する。
 */
public class ConsoleStepRunner {

    /** 1ステップ実行を担当するクラス。 */
    private final StepRunner runner;

    /** 実行結果の表示を担当するクラス。 */
    private final ConsoleStepView view;

    /** 実行対象の命令列。 */
    private final List<Instruction> program;

    /** ブレークポイント管理を担当するクラス。 */
    private final BreakpointManager breakpointManager = new BreakpointManager();

    /**
     * ConsoleStepRunnerを生成する。
     * 
     * @param runner  1ステップ実行を担当するクラス
     * @param view    実行結果の表示を担当するクラス
     * @param program 実行対象の命令列
     */
    public ConsoleStepRunner(
            StepRunner runner,
            ConsoleStepView view,
            List<Instruction> program) {

        this.runner = runner;
        this.view = view;
        this.program = program;
    }

    /**
     * プログラムを対話的にステップ実行する。
     * 
     * Enterキーで1命令だけ実行し、runコマンドで連続実行する。
     * breakコマンドで指定したPCに到達すると、自動実行を停止する。
     */
    public void runInteractive() {
        try (Scanner scanner = new Scanner(System.in)) {
            boolean autoRun = false;

            while (runner.hasNext()) {
                if (autoRun && breakpointManager.contains(runner.getPc())) {
                    System.out.println("Breakpoint hit: PC " + runner.getPc());
                    autoRun = false;
                }

                if (!autoRun) {
                    String command = readCommand(scanner);

                    if (parseCommand(command) == ConsoleCommand.QUIT) {
                        System.out.println("実行を終了します。");
                        return;
                    }

                    autoRun = handleCommand(command);

                    // break/delete/breaks/clear のような操作コマンドでは、
                    // 命令を実行せずに次の入力へ戻る。
                    if (!command.isEmpty() && !autoRun) {
                        continue;
                    }
                }

                StepResult result = runner.step();
                view.printStep(result, program);
            }
        }

        System.out.println("プログラムが終了しました。");
    }

    /**
     * コマンドを処理する。
     * 
     * @param command 入力されたコマンド文字列
     * @return run状態に入る場合はtrue
     */
    private boolean handleCommand(String command) {
        ConsoleCommand type = parseCommand(command);

        switch (type) {
            case STEP:
                return false;

            case RUN:
                return true;

            case BREAKS:
                printBreakpoints();
                return false;

            case CLEAR:
                clearBreakpoints();
                return false;

            case BREAK:
                addBreakpoint(command);
                return false;

            case DELETE:
                deleteBreakpoint(command);
                return false;

            case QUIT:
            case UNKNOWN:
            default:
                return false;
        }
    }

    /**
     * ブレークポイントを追加する。
     * 
     * @param command breakコマンド
     */
    private void addBreakpoint(String command) {
        int pc = parsePcArgument(command, "break");
        validateProgramPc(pc);

        breakpointManager.add(pc);
        System.out.println("Breakpoint added: PC " + pc);
    }

    /**
     * ブレークポイントを削除する。
     * 
     * @param command deleteコマンド
     */
    private void deleteBreakpoint(String command) {
        int pc = parsePcArgument(command, "delete");

        if (breakpointManager.remove(pc)) {
            System.out.println("Breakpoint deleted: PC " + pc);
        } else {
            System.out.println("Breakpoint not found: PC " + pc);
        }
    }

    /**
     * ブレークポイント一覧を表示する。
     */
    private void printBreakpoints() {
        if (breakpointManager.isEmpty()) {
            System.out.println("No breakpoints.");
            return;
        }

        System.out.println("Breakpoints:");
        for (int pc : breakpointManager.getAll()) {
            System.out.println("PC " + pc);
        }
    }

    /**
     * すべてのブレークポイントを削除する。
     */
    private void clearBreakpoints() {
        if (breakpointManager.isEmpty()) {
            System.out.println("No breakpoints to clear.");
            return;
        }

        breakpointManager.clear();
        System.out.println("All breakpoints cleared.");
    }

    /**
     * コマンドからPC番号を取り出す。
     * 
     * @param command     入力コマンド
     * @param commandName コマンド名
     * @return PC番号
     */
    private int parsePcArgument(String command, String commandName) {
        String[] parts = command.split("\\s+");

        if (parts.length != 2) {
            throw new IllegalArgumentException(commandName + " コマンドの形式が不正です。");
        }

        try {
            return Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("PC番号は整数で指定してください: " + parts[1], e);
        }
    }

    /**
     * プログラム内のPC番号として有効か検証する。
     * 
     * @param pc PC番号
     */
    private void validateProgramPc(int pc) {
        if (pc < 0 || pc >= program.size()) {
            throw new IllegalArgumentException("PCがプログラム範囲外です: " + pc);
        }
    }

    /**
     * ユーザーからコマンドを受け取る。
     * 
     * @param scanner 標準入力を読むScanner
     * @return 入力されたコマンド
     */
    private String readCommand(Scanner scanner) {
        while (true) {
            System.out.print("Command [Enter=step, run, break <pc>, delete <pc>, breaks, clear, quit]: ");
            String input = scanner.nextLine().trim().toLowerCase();

            if (isValidCommand(input)) {
                return input;
            }

            System.out.println("不正なコマンドです。");
        }
    }

    /**
     * 入力されたコマンドが有効か判定する。
     * 
     * @param input 入力文字列
     * @return 有効なコマンドならtrue
     */
    private boolean isValidCommand(String input) {
        return parseCommand(input) != ConsoleCommand.UNKNOWN;
    }

    /**
     * 入力文字列からコマンド種別を判定する。
     * 
     * @param input 入力文字列
     * @return 判定したコマンド種別
     */
    private ConsoleCommand parseCommand(String input) {
        if (input.isEmpty()) {
            return ConsoleCommand.STEP;
        }

        if ("run".equals(input)) {
            return ConsoleCommand.RUN;
        }

        if ("quit".equals(input)) {
            return ConsoleCommand.QUIT;
        }

        if ("breaks".equals(input)) {
            return ConsoleCommand.BREAKS;
        }

        if ("clear".equals(input)) {
            return ConsoleCommand.CLEAR;
        }

        if (input.startsWith("break ")) {
            return ConsoleCommand.BREAK;
        }

        if (input.startsWith("delete ")) {
            return ConsoleCommand.DELETE;
        }

        return ConsoleCommand.UNKNOWN;
    }
}
