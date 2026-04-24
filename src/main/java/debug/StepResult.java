package debug;

import instruction.Instruction;

/**
 * 1ステップ実行した結果を表すクラス。
 * 
 * このクラスは「命令を1つ実行したときに、CPUの状態がどう変化したか」を
 * まとめて保持するために使う。
 * 
 * StepRunnerは命令を実行する担当であり、
 * StepViewは表示する担当である。
 * その間で受け渡すデータとして、このStepResultを使う。
 */
public class StepResult {

    /** 何ステップ目の実行かを表す番号。 */
    private final int step;

    /** 実行前のPC。 */
    private final int pcBefore;

    /** 実行後のPC。 */
    private final int pcAfter;

    /** 今回実行した命令。 */
    private final Instruction instruction;

    /** 実行前のレジスタ状態。 */
    private final int[] registersBefore;

    /** 実行後のレジスタ状態。 */
    private final int[] registersAfter;

    /** 実行前のメモリ状態。 */
    private final byte[] memoryBefore;

    /** 実行後のメモリ状態。 */
    private final byte[] memoryAfter;

    /** 実行前のHIレジスタ。 */
    private final int hiBefore;

    /** 実行後のHIレジスタ。 */
    private final int hiAfter;

    /** 実行前のLOレジスタ。 */
    private final int loBefore;

    /** 実行後のLOレジスタ。 */
    private final int loAfter;

    /**
     * StepResultを生成する。
     * 
     * @param step            実行ステップ番号
     * @param pcBefore        実行前のPC
     * @param pcAfter         実行後のPC
     * @param instruction     実行した命令
     * @param registersBefore 実行前のレジスタ状態
     * @param registersAfter  実行後のレジスタ状態
     * @param memoryBefore    実行前のメモリ状態
     * @param memoryAfter     実行後のメモリ状態
     * @param hiBefore        実行前のHIレジスタ
     * @param hiAfter         実行後のHIレジスタ
     * @param loBefore        実行前のLOレジスタ
     * @param loAfter         実行後のLOレジスタ
     */
    public StepResult(
            int step,
            int pcBefore,
            int pcAfter,
            Instruction instruction,
            int[] registersBefore,
            int[] registersAfter,
            byte[] memoryBefore,
            byte[] memoryAfter,
            int hiBefore,
            int hiAfter,
            int loBefore,
            int loAfter) {

        this.step = step;
        this.pcBefore = pcBefore;
        this.pcAfter = pcAfter;
        this.instruction = instruction;
        this.registersBefore = registersBefore;
        this.registersAfter = registersAfter;
        this.memoryBefore = memoryBefore;
        this.memoryAfter = memoryAfter;
        this.hiBefore = hiBefore;
        this.hiAfter = hiAfter;
        this.loBefore = loBefore;
        this.loAfter = loAfter;
    }

    /**
     * 実行ステップ番号を返す。
     * 
     * @return 実行ステップ番号
     */
    public int getStep() {
        return step;
    }

    /**
     * 実行前のPCを返す。
     * 
     * @return 実行前のPC
     */
    public int getPcBefore() {
        return pcBefore;
    }

    /**
     * 実行後のPCを返す。
     * 
     * @return 実行後のPC
     */
    public int getPcAfter() {
        return pcAfter;
    }

    /**
     * 実行した命令を返す。
     * 
     * @return 実行した命令
     */
    public Instruction getInstruction() {
        return instruction;
    }

    /**
     * 実行前のレジスタ状態を返す。
     * 
     * @return 実行前のレジスタ状態
     */
    public int[] getRegistersBefore() {
        return registersBefore;
    }

    /**
     * 実行後のレジスタ状態を返す。
     * 
     * @return 実行後のレジスタ状態
     */
    public int[] getRegistersAfter() {
        return registersAfter;
    }

    /**
     * 実行前のメモリ状態を返す。
     * 
     * @return 実行前のメモリ状態
     */
    public byte[] getMemoryBefore() {
        return memoryBefore;
    }

    /**
     * 実行後のメモリ状態を返す。
     * 
     * @return 実行後のメモリ状態
     */
    public byte[] getMemoryAfter() {
        return memoryAfter;
    }

    /**
     * 実行前のHIレジスタを返す。
     * 
     * @return 実行前のHIレジスタ
     */
    public int getHiBefore() {
        return hiBefore;
    }

    /**
     * 実行後のHIレジスタを返す。
     * 
     * @return 実行後のHIレジスタ
     */
    public int getHiAfter() {
        return hiAfter;
    }

    /**
     * 実行前のLOレジスタを返す。
     * 
     * @return 実行前のLOレジスタ
     */
    public int getLoBefore() {
        return loBefore;
    }

    /**
     * 実行後のLOレジスタを返す。
     * 
     * @return 実行後のLOレジスタ
     */
    public int getLoAfter() {
        return loAfter;
    }
}