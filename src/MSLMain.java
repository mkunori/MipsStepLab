import java.util.List;

import cpu.Cpu;
import instruction.AddInstruction;
import instruction.AddiInstruction;
import instruction.Instruction;
import instruction.LiInstruction;
import instruction.SubInstruction;

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

        // 命令サンプル
        List<Instruction> program = List.of(
                new LiInstruction(8, 10), // li $t0, 10
                new LiInstruction(9, 20), // li $t1, 20
                new AddInstruction(10, 8, 9), // add $t2, $t0, $t1
                // new AddiInstruction(10, 10, 5), // addi $t2, $t2, 5
                // new SubInstruction(11, 10, 8), // sub $t3, $t2, $t0
                new LiInstruction(0, 999) // li $zero, 999（無視される）
        );

        System.out.println("=== MIPS Simulator Start ===");
        System.out.println();

        for (Instruction instruction : program) {
            printStepHeader(cpu, instruction);
            cpu.execute(instruction);
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
