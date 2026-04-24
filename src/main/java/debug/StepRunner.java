package debug;

import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import cpu.Cpu;
import instruction.Instruction;

/**
 * プログラムをステップ実行するクラス。
 *
 * Enterキーで1命令ずつ進めたり、
 * runコマンドでブレークポイントまで連続実行したりできる。
 */
public class StepRunner {

    /** ステップ実行で使用するCPU。 */
    private final Cpu cpu;

    /** 実行対象の命令列。 */
    private final List<Instruction> program;

    /** デバッグ表示を担当するビュー。 */
    private final StepView view;

    /** ブレークポイントとして停止するPC番号。 */
    private final Set<Integer> breakpoints = new HashSet<>();

    /** ステップ番号。 */
    private int step;

    /**
     * StepRunnerを生成する。
     * 
     * @param cpu     CPU
     * @param program 命令列
     * @param view    表示担当
     */
    public StepRunner(Cpu cpu, List<Instruction> program, StepView view) {
        this.cpu = cpu;
        this.program = program;
        this.view = view;
        this.step = 1;
    }

    /**
     * プログラムを対話的にステップ実行する。
     */
    public void runInteractive() {
        try (Scanner scanner = new Scanner(System.in)) {
            boolean autoRun = false; // false:手動ステップ中 true:自動ステップ中

            while (cpu.getPc() < program.size()) {
                // ブレークポイントに到達した？
                if (autoRun && breakpoints.contains(cpu.getPc())) {
                    System.out.println("Breakpoint hit: PC " + cpu.getPc());
                    autoRun = false;
                }

                // 手動ステップ中？
                if (!autoRun) {
                    String command = readCommand(scanner);

                    if ("quit".equals(command)) {
                        System.out.println("実行を終了します。");
                        return;
                    }

                    autoRun = handleCommand(command);

                    // break delete breals の後は命令実行しない
                    if (!command.isEmpty() && !autoRun) {
                        continue;
                    }
                }

                executeOneStep();
            }
        }

        System.out.println("プログラムが終了しました。");
    }

    /**
     * 1命令だけ実行して結果を表示する。
     */
    private void executeOneStep() {
        StepResult result = step();

        view.printStep(result, program);
    }

    /**
     * 1命令だけ実行し、その実行結果を返す。
     * 
     * このメソッドは表示を行わない。
     * 命令を1つ進める処理だけを担当する。
     * 
     * CUIでは、この戻り値をStepViewに渡して表示する。
     * Webアプリでは、この戻り値を画面表示用のデータに変換して使う。
     * 
     * @return 1命令分の実行結果
     */
    public StepResult step() {
        int currentPc = cpu.getPc();
        Instruction instruction = program.get(currentPc);

        // 命令実行前の状態を保存する。
        int[] registersBefore = cpu.copyRegisters();
        byte[] memoryBefore = cpu.copyMemory();
        int hiBefore = cpu.getHi();
        int loBefore = cpu.getLo();

        // 命令を1つ実行する。
        cpu.execute(instruction);

        // 分岐命令ではPCが通常の+1にならない場合があるため、
        // 実行後のPCはCPUから取得する。
        int newPc = cpu.getPc();

        // 命令実行後の状態を保存する。
        int[] registersAfter = cpu.copyRegisters();
        byte[] memoryAfter = cpu.copyMemory();
        int hiAfter = cpu.getHi();
        int loAfter = cpu.getLo();

        return new StepResult(
                step++,
                currentPc,
                newPc,
                instruction,
                registersBefore,
                registersAfter,
                memoryBefore,
                memoryAfter,
                hiBefore,
                hiAfter,
                loBefore,
                loAfter);
    }

    /**
     * コマンドを処理する。
     *
     * @param command 入力コマンド
     * @return run状態に入る場合はtrue
     */
    private boolean handleCommand(String command) {
        if (command.isEmpty()) {
            return false;
        }

        if ("run".equals(command)) {
            return true;
        }

        if ("breaks".equals(command)) {
            printBreakpoints();
            return false;
        }

        if ("clear".equals(command)) {
            clearBreakpoints();
            return false;
        }

        if (command.startsWith("break ")) {
            addBreakpoint(command);
            return false;
        }

        if (command.startsWith("delete ")) {
            deleteBreakpoint(command);
            return false;
        }

        return false;
    }

    /**
     * ブレークポイントを追加する。
     *
     * @param command breakコマンド
     */
    private void addBreakpoint(String command) {
        int pc = parsePcArgument(command, "break");
        validateProgramPc(pc);

        breakpoints.add(pc);
        System.out.println("Breakpoint added: PC " + pc);
    }

    /**
     * ブレークポイントを削除する。
     *
     * @param command deleteコマンド
     */
    private void deleteBreakpoint(String command) {
        int pc = parsePcArgument(command, "delete");

        if (breakpoints.remove(pc)) {
            System.out.println("Breakpoint deleted: PC " + pc);
        } else {
            System.out.println("Breakpoint not found: PC " + pc);
        }
    }

    /**
     * ブレークポイント一覧を表示する。
     */
    private void printBreakpoints() {
        if (breakpoints.isEmpty()) {
            System.out.println("No breakpoints.");
            return;
        }

        System.out.println("Breakpoints:");
        for (int pc : breakpoints) {
            System.out.println("PC " + pc);
        }
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
     * @param scanner Scanner
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
     * @return 有効ならtrue
     */
    private boolean isValidCommand(String input) {
        return input.isEmpty() // Enter
                || "run".equals(input)
                || "quit".equals(input)
                || "breaks".equals(input)
                || "clear".equals(input)
                || input.startsWith("break ")
                || input.startsWith("delete ");
    }

    /**
     * すべてのブレークポイントを削除する。
     */
    private void clearBreakpoints() {
        if (breakpoints.isEmpty()) {
            System.out.println("No breakpoints to clear.");
            return;
        }

        breakpoints.clear();
        System.out.println("All breakpoints cleared.");
    }
}