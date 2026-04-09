package debug;

import java.util.List;

import cpu.Cpu;
import cpu.RegisterNames;
import instruction.*;

/**
 * ステップ実行時の表示を担当するクラス。
 */
public class StepView {

    /**
     * 1ステップ分のデバッグ表示を行う。
     * 
     * @param step            ステップ番号
     * @param currentPc       実行前PC
     * @param instruction     実行した命令
     * @param cpu             実行後のCPU
     * @param newPc           実行後PC
     * @param registersBefore 実行前レジスタ
     * @param memoryBefore    実行前メモリ
     * @param program         プログラム全体
     */
    public void printStep(int step, int currentPc, Instruction instruction,
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
        printMemory(cpu, 0, 15);

        System.out.println("--------------------------------------------------");
        System.out.println("EVENT");
        printEvent(instruction, cpu, currentPc, newPc);

        System.out.println("--------------------------------------------------");
        System.out.println("CHANGES");
        printRegisterDiff(cpu, registersBefore);
        printMemoryDiff(cpu, memoryBefore, 0, 15);

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
    private void printRegisters(Cpu cpu) {
        int[] targets = { 0, 2, 8, 9, 10, 11, 31 };

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
    private void printMemory(Cpu cpu, int start, int end) {
        for (int i = start; i <= end; i++) {
            System.out.println(cpu.formatMemory(i));
        }
    }

    /**
     * PC変化イベントを表示する。
     * 
     * $ra を使う場合は、関数からの戻りとして扱う。
     * 
     * @param instruction 命令
     * @param cpu         CPU
     * @param oldPc       実行前PC
     * @param newPc       実行後PC
     */
    private void printEvent(Instruction instruction, Cpu cpu, int oldPc, int newPc) {
        if (instruction instanceof JalInstruction) {
            System.out.println("call: save return address ($ra = " + cpu.getRegister(31) + ")");
            System.out.println("jump to: PC " + newPc);
            return;
        }

        if (instruction instanceof JrInstruction jrInstruction) {
            int registerIndex = jrInstruction.getSourceRegister();
            String registerName = RegisterNames.getName(registerIndex);

            if ("$ra".equals(registerName)) {
                System.out.println("return: jump to $ra");
            } else {
                System.out.println("jump register: " + registerName);
            }

            System.out.println("jump to: PC " + newPc);
            return;
        }

        if (instruction instanceof BeqInstruction) {
            if (newPc != oldPc + 1) {
                System.out.println("branch taken: beq matched");
                System.out.println("jump to: PC " + newPc);
            } else {
                System.out.println("branch not taken: beq did not match");
            }
            return;
        }

        if (instruction instanceof BneInstruction) {
            if (newPc != oldPc + 1) {
                System.out.println("branch taken: bne matched");
                System.out.println("jump to: PC " + newPc);
            } else {
                System.out.println("branch not taken: bne did not match");
            }
            return;
        }

        if (instruction instanceof JumpInstruction) {
            System.out.println("jump: PC changed");
            System.out.println("jump to: PC " + newPc);
            return;
        }

        if (instruction instanceof LwInstruction lwInstruction) {
            int targetRegister = lwInstruction.getDestRegister();
            int baseRegister = lwInstruction.getBaseRegister();
            int offset = lwInstruction.getOffset();

            int address = cpu.getRegister(baseRegister) + offset;
            String targetName = RegisterNames.getName(targetRegister);

            System.out.println("load word: " + targetName + " = mem[" + address + "]");
            System.out.println("loaded value: " + cpu.getRegister(targetRegister));
            return;
        }

        if (instruction instanceof SwInstruction swInstruction) {
            int sourceRegister = swInstruction.getSourceRegister();
            int baseRegister = swInstruction.getBaseRegister();
            int offset = swInstruction.getOffset();

            int address = cpu.getRegister(baseRegister) + offset;
            String sourceName = RegisterNames.getName(sourceRegister);

            System.out.println("store word: mem[" + address + "] = " + cpu.loadWord(address));
            System.out.println(
                    "stored from: " + sourceName + " (" + cpu.getRegister(swInstruction.getSourceRegister()) + ")");
            return;
        }

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
    private void printRegisterDiff(Cpu cpu, int[] before) {
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
    private void printMemoryDiff(Cpu cpu, int[] before, int start, int end) {
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
    private void printNextInstruction(List<Instruction> program, int nextPc) {
        if (nextPc >= 0 && nextPc < program.size()) {
            System.out.println("NEXT PC   : " + nextPc);
            System.out.println("NEXT INST : " + program.get(nextPc).toAssembly());
        } else {
            System.out.println("NEXT PC   : " + nextPc);
            System.out.println("NEXT INST : <end>");
        }
    }
}