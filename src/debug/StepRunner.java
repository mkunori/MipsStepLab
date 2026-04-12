package debug;

import java.util.List;
import java.util.Scanner;

import cpu.Cpu;
import instruction.Instruction;

/**
 * プログラムをステップ実行するクラス。
 * 
 * Enterキーで1命令ずつ進めたり、
 * runコマンドで最後まで連続実行したりできる。
 */
public class StepRunner {

    /**
     * ステップ実行で使用するCPU。
     */
    private final Cpu cpu;

    /**
     * 実行対象の命令列。
     */
    private final List<Instruction> program;

    /**
     * デバッグ表示を担当するビュー。
     */
    private final StepView view;

    /**
     * ステップ番号。
     */
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
            boolean autoRun = false;

            while (cpu.getPc() < program.size()) {
                int currentPc = cpu.getPc();
                Instruction instruction = program.get(currentPc);

                int[] registersBefore = cpu.copyRegisters();
                byte[] memoryBefore = cpu.copyMemory();

                cpu.execute(instruction);
                int newPc = cpu.getPc();

                view.printStep(step, currentPc, instruction, cpu, newPc,
                        registersBefore, memoryBefore, program);

                step++;

                if (autoRun) {
                    continue;
                }

                String command = readCommand(scanner);

                if ("quit".equals(command)) {
                    System.out.println("実行を終了します。");
                    return;
                }

                if ("run".equals(command)) {
                    autoRun = true;
                }
            }
        }

        System.out.println("プログラムが終了しました。");
    }

    /**
     * ユーザーからコマンドを受け取る。
     * 
     * @param scanner Scanner
     * @return 入力されたコマンド
     */
    private String readCommand(Scanner scanner) {
        while (true) {
            System.out.print("Command [Enter=step, run, quit]: ");
            String input = scanner.nextLine().trim().toLowerCase();

            if (input.isEmpty() || "run".equals(input) || "quit".equals(input)) {
                return input;
            }

            System.out.println("不正なコマンドです。Enter / run / quit のいずれかを入力してください。");
        }
    }
}