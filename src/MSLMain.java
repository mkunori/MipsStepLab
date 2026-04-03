import java.util.List;

import cpu.Cpu;
import cpu.RegisterNames;
import instruction.Instruction;
import parser.InstructionParser;

/**
 * MIPS風シミュレータの動作確認を行うメインクラス。
 * 
 * アセンブリ文字列をパースして命令列を生成し、
 * デバッグビュー風の表示で1ステップずつ実行結果を確認する。
 */
public class MSLMain {
    /**
     * アプリケーションのエントリーポイント。
     *
     * @param args コマンドライン引数
     */
    public static void main(String[] args) {
        Cpu cpu = new Cpu();
        InstructionParser parser = new InstructionParser();

        // 実行するサンプルを選ぶ
        List<String> source = createFullSample();
        // List<String> source = createBranchSample();
        // List<String> source = createMemorySample();

        List<Instruction> program = parser.parse(source);

        int step = 1;

        while (cpu.getPc() < program.size()) {
            int currentPc = cpu.getPc();
            Instruction instruction = program.get(currentPc);

            int[] registersBefore = cpu.copyRegisters();
            int[] memoryBefore = cpu.copyMemory();

            cpu.execute(instruction);
            int newPc = cpu.getPc();

            printDebugView(step, currentPc, instruction, cpu, newPc,
                    registersBefore, memoryBefore, program);

            step++;
        }
    }

    /**
     * デバッグビュー風の表示を行う。
     * 
     * @param step            ステップ番号
     * @param currentPc       実行前PC
     * @param instruction     実行した命令
     * @param cpu             実行後のCPU
     * @param newPc           実行後PC
     * @param registersBefore 実行前のレジスタ状態
     * @param memoryBefore    実行前のメモリ状態
     * @param program         命令リスト
     */
    private static void printDebugView(int step, int currentPc, Instruction instruction,
            Cpu cpu, int newPc, int[] registersBefore, int[] memoryBefore,
            List<Instruction> program) {

        System.out.println("==================================================");
        System.out.println("STEP " + step);
        System.out.println("PC      : " + currentPc);
        System.out.println("INSTR   : " + instruction.toAssembly());

        System.out.println("--------------------------------------------------");
        System.out.println("REGISTERS");
        printRegisters(cpu);

        System.out.println("--------------------------------------------------");
        System.out.println("MEMORY");
        printMemory(cpu, 0, 3);

        System.out.println("--------------------------------------------------");
        System.out.println("EVENT");
        printEvent(currentPc, newPc);

        System.out.println("--------------------------------------------------");
        System.out.println("CHANGES");
        printRegisterDiff(cpu, registersBefore);
        printMemoryDiff(cpu, memoryBefore, 0, 3);

        System.out.println("--------------------------------------------------");
        System.out.println("NEXT");
        printNextInstruction(program, newPc);

        System.out.println("==================================================");
        System.out.println();
    }

    /**
     * 主要レジスタを表示する。
     * 
     * @param cpu CPU
     */
    private static void printRegisters(Cpu cpu) {
        int[] targets = { 0, 2, 8, 9, 10, 11 };

        for (int index : targets) {
            System.out.println(cpu.formatRegisterAligned(index));
        }
    }

    /**
     * 指定範囲のメモリを表示する。
     * 
     * @param cpu   CPU
     * @param start 開始アドレス
     * @param end   終了アドレス
     */
    private static void printMemory(Cpu cpu, int start, int end) {
        for (int i = start; i <= end; i++) {
            System.out.println(cpu.formatMemory(i));
        }
    }

    /**
     * PC変化イベントを表示する。
     * 
     * @param oldPc 実行前PC
     * @param newPc 実行後PC
     */
    private static void printEvent(int oldPc, int newPc) {
        if (newPc != oldPc + 1) {
            System.out.println("PC changed: " + oldPc + " -> " + newPc);
        } else {
            System.out.println("sequential execution");
        }
    }

    /**
     * 変化したレジスタを表示する。
     * 
     * @param cpu    実行後のCPU
     * @param before 実行前のレジスタ状態
     */
    private static void printRegisterDiff(Cpu cpu, int[] before) {
        boolean changed = false;

        for (int i = 0; i < before.length; i++) {
            int afterValue = cpu.getRegister(i);

            if (before[i] != afterValue) {
                System.out.println(RegisterNames.getName(i)
                        + " : " + before[i] + " -> " + afterValue);
                changed = true;
            }
        }

        if (!changed) {
            System.out.println("no register changes");
        }
    }

    /**
     * 指定範囲で変化したメモリを表示する。
     * 
     * @param cpu    実行後のCPU
     * @param before 実行前のメモリ状態
     * @param start  開始アドレス
     * @param end    終了アドレス
     */
    private static void printMemoryDiff(Cpu cpu, int[] before, int start, int end) {
        boolean changed = false;

        for (int i = start; i <= end; i++) {
            int afterValue = cpu.loadWord(i);

            if (before[i] != afterValue) {
                System.out.println("mem[" + i + "] : "
                        + before[i] + " -> " + afterValue);
                changed = true;
            }
        }

        if (!changed) {
            System.out.println("no memory changes");
        }
    }

    /**
     * 次に実行される命令を表示する。
     * 
     * @param program 命令一覧
     * @param nextPc  次に実行されるPC
     */
    private static void printNextInstruction(List<Instruction> program, int nextPc) {
        if (nextPc >= 0 && nextPc < program.size()) {
            System.out.println("NEXT PC   : " + nextPc);
            System.out.println("NEXT INST : " + program.get(nextPc).toAssembly());
        } else {
            System.out.println("NEXT PC   : " + nextPc);
            System.out.println("NEXT INST : <end>");
        }
    }

    /**
     * 分岐命令の動作確認用サンプルを返す。
     * 
     * {@code beq}、{@code bne}、{@code j} の動作を確認するためのサンプル。
     * 
     * @return 分岐サンプル
     */
    private static List<String> createBranchSample() {
        return List.of(
                "# 分岐サンプル",
                "# beq, bne, j の確認",

                "li $t0, 10",
                "li $t1, 10",

                "# beq成立",
                "beq $t0, $t1, equal",
                "li $v0, 0",
                "j afterEqual",

                "equal: li $v0, 1",
                "afterEqual: addi $v0, $v0, 10",

                "li $t0, 3",
                "li $t1, 5",

                "# bne成立",
                "bne $t0, $t1, notEqual",
                "li $v0, 100",
                "j end",

                "notEqual: addi $v0, $v0, 20",
                "end: addi $v0, $v0, 1");
    }

    /**
     * メモリ操作の動作確認用サンプルを返す。
     * 
     * {@code sw} でメモリへ格納し、{@code lw} で読み戻して
     * 値を加工する流れを確認するためのサンプル。
     * 
     * @return メモリサンプル
     */
    private static List<String> createMemorySample() {
        return List.of(
                "# メモリサンプル",
                "# lw, sw の確認",

                "li $t0, 10", // ベースアドレス
                "li $t1, 3", // 保存する値

                "sw $t1, 0($t0)", // mem[10] = 3
                "lw $t2, 0($t0)", // $t2 = 3
                "addi $t2, $t2, 7", // $t2 = 10
                "sw $t2, 1($t0)", // mem[11] = 10
                "lw $v0, 1($t0)" // $v0 = 10
        );
    }

    /**
     * 代表サンプルを返す。
     * 
     * 1から5までの値を順番にメモリへ保存するループ。
     * 分岐、加算、メモリ操作を組み合わせて、
     * 現在の命令セットでできることを自然な流れで確認できる。
     * 
     * @return 代表サンプル
     */
    private static List<String> createFullSample() {
        return List.of(
                "# 代表サンプル",
                "# 1から5までをメモリに順に保存する",
                "# 結果: mem[0] = 1, mem[1] = 2, ..., mem[4] = 5",

                "li $t0, 0", // 書き込み先アドレス
                "li $t1, 1", // 書き込む値
                "li $t2, 6", // 終了判定用（6になったら終了）

                "loop: sw $t1, 0($t0)",
                "addi $t0, $t0, 1",
                "addi $t1, $t1, 1",
                "bne $t1, $t2, loop",

                "# 最後に mem[4] を読んで確認",
                "lw $v0, 4($zero)");
    }
}