package instruction;

import cpu.Cpu;

/**
 * MIPS命令を表すインターフェース。
 * 
 * 各命令はこのインターフェースを実装し、
 * {@link #execute(Cpu)} でCPUの状態を変更する。
 */
public interface Instruction {

    /**
     * 命令を実行する。
     * 
     * 命令に応じてCPUのレジスタやPCなどの状態を変更する。
     * 
     * @param cpu 実行対象のCPU
     */
    void execute(Cpu cpu);

    /**
     * 命令をアセンブリ風の文字列で返す。
     * 
     * 実行ログを見やすくするために使用する。
     * 
     * @return 命令の文字列表現
     */
    String toAssembly();

}
