import java.util.List;

import cpu.Cpu;
import instruction.Instruction;
import parser.InstructionParser;

/**
 * MIPS風シミュレータの動作確認を行うメインクラス。
 *
 * 一旦は最小構成として、命令オブジェクトをあらかじめ並べた
 * サンプルプログラムを実行する。
 * 
 * 実行のたびに以下を表示する。
 * - 現在のPC
 * - 実行する命令
 * - 実行後の主要レジスタ状態
 * 
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

        // 命令サンプル
        List<String> source = List.of(
                "# 全命令サンプル（分岐成立）",

                "li $t0, 10",
                "li $t1, 20",

                "add $t2, $t0, $t1",
                "addi $t2, $t2, 5",
                "sub $t3, $t2, $t0",
                "addi $t3, $t3, -5",

                "check: beq $t3, $t1, equal",
                "li $v0, 0",
                "j end",

                "equal: li $v0, 1",

                "end: addi $v0, $v0, 10");

        List<Instruction> program = parser.parse(source);

        System.out.println("=== MIPS Simulator Start ===");
        System.out.println();

        while (cpu.getPc() < program.size()) {
            Instruction instruction = program.get(cpu.getPc());

            printStepHeader(cpu, instruction);

            int oldPc = cpu.getPc();
            cpu.execute(instruction);
            int newPc = cpu.getPc();

            if (newPc != oldPc + 1) {
                System.out.println(">>> PC changed: " + oldPc + " -> " + newPc);
            }

            printCpuState(cpu);
        }

        System.out.println("=== Finished ===");
    }

    /**
     * 実行前の見出しを表示する。
     *
     * @param cpu         現在のCPU
     * @param instruction これから実行する命令
     */
    private static void printStepHeader(Cpu cpu, Instruction instruction) {
        System.out.println("PC = " + cpu.getPc() + " : " + instruction.toAssembly());
    }

    /**
     * 実行後のCPU状態を表示する。
     *
     * @param cpu 実行後のCPU
     */
    private static void printCpuState(Cpu cpu) {
        System.out.println(cpu.dumpRegisters());
    }
}
