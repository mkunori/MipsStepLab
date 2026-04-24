package execution;

import java.util.List;

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

    /** ステップ番号。 */
    private int step;

    /**
     * StepRunnerを生成する。
     * 
     * @param cpu     CPU
     * @param program 命令列
     */
    public StepRunner(Cpu cpu, List<Instruction> program) {
        this.cpu = cpu;
        this.program = program;
        this.step = 1;
    }

    /**
     * まだ実行できる命令が残っているか判定する。
     * 
     * @return 次に実行する命令がある場合はtrue
     */
    public boolean hasNext() {
        return cpu.getPc() < program.size();
    }

    /**
     * 現在のPCを返す。
     *
     * @return 現在のPC
     */
    public int getPc() {
        return cpu.getPc();
    }

    /**
     * 1命令だけ実行し、その実行結果を返す。
     *
     * このメソッドは表示を行わない。
     * 命令を1つ進める処理だけを担当する。
     *
     * @return 1命令分の実行結果
     */
    public StepResult step() {
        int currentPc = cpu.getPc();
        Instruction instruction = program.get(currentPc);

        // 命令実行前の状態を保存する。
        int[] registersBefore = cpu.copyRegisters();
        int hiBefore = cpu.getHi();
        int loBefore = cpu.getLo();
        byte[] memoryBefore = cpu.copyMemory();

        // 命令を1つ実行する。
        cpu.execute(instruction);

        // 分岐命令ではPCが通常の+1にならない場合があるため、
        // 実行後のPCはCPUから取得する。
        int newPc = cpu.getPc();

        // 命令実行後の状態を保存する。
        int[] registersAfter = cpu.copyRegisters();
        int hiAfter = cpu.getHi();
        int loAfter = cpu.getLo();
        byte[] memoryAfter = cpu.copyMemory();

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
}