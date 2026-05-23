package web;

import java.util.List;

import cpu.Cpu;
import execution.StepRunner;
import instruction.Instruction;

/**
 * Web版MipsStepLabで使用する実行状態を保持するクラス。
 *
 * Webアプリでは、ボタンを押すたびにHTTPリクエストが送られる。
 * そのため、CPUやStepRunnerを毎回作り直すと、
 * どこまで実行したかという状態が失われてしまう。
 *
 * このクラスは、1つのMIPSプログラムを実行するために必要な状態を
 * まとめて保持する。
 */
public class WebMipsSession {

    /** ステップ実行で使用するCPU。 */
    private final Cpu cpu;

    /** 実行対象の命令列。 */
    private final List<Instruction> program;

    /** 1ステップ実行を担当するクラス。 */
    private final StepRunner stepRunner;

    /**
     * WebMipsSessionを生成する。
     *
     * @param cpu        ステップ実行で使用するCPU
     * @param program    実行対象の命令列
     * @param stepRunner 1ステップ実行を担当するクラス
     */
    public WebMipsSession(Cpu cpu, List<Instruction> program, StepRunner stepRunner) {
        this.cpu = cpu;
        this.program = program;
        this.stepRunner = stepRunner;
    }

    /**
     * 新しいMIPSプログラム用の実行状態を生成する。
     *
     * CpuとStepRunnerをここでまとめて生成することで、
     * Controller側の処理をシンプルにする。
     *
     * @param program 実行対象の命令列
     * @return 新しい実行状態
     */
    public static WebMipsSession create(List<Instruction> program) {
        Cpu cpu = new Cpu();
        StepRunner stepRunner = new StepRunner(cpu, program);

        return new WebMipsSession(cpu, program, stepRunner);
    }

    /**
     * CPUを返す。
     *
     * @return CPU
     */
    public Cpu getCpu() {
        return cpu;
    }

    /**
     * 実行対象の命令列を返す。
     *
     * @return 実行対象の命令列
     */
    public List<Instruction> getProgram() {
        return program;
    }

    /**
     * 1ステップ実行を担当するStepRunnerを返す。
     *
     * @return StepRunner
     */
    public StepRunner getStepRunner() {
        return stepRunner;
    }
}