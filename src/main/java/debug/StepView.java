package debug;

import java.util.List;

import cpu.Cpu;
import cpu.RegisterNames;
import instruction.AddInstruction;
import instruction.AddiInstruction;
import instruction.AndInstruction;
import instruction.AndiInstruction;
import instruction.BeqInstruction;
import instruction.BgezInstruction;
import instruction.BgtzInstruction;
import instruction.BlezInstruction;
import instruction.BltzInstruction;
import instruction.BneInstruction;
import instruction.DivInstruction;
import instruction.DivuInstruction;
import instruction.Instruction;
import instruction.JalInstruction;
import instruction.JalrInstruction;
import instruction.JrInstruction;
import instruction.JumpInstruction;
import instruction.LbInstruction;
import instruction.LbuInstruction;
import instruction.LhInstruction;
import instruction.LhuInstruction;
import instruction.LuiInstruction;
import instruction.LwInstruction;
import instruction.MfhiInstruction;
import instruction.MfloInstruction;
import instruction.MthiInstruction;
import instruction.MtloInstruction;
import instruction.MultInstruction;
import instruction.MultuInstruction;
import instruction.NorInstruction;
import instruction.OrInstruction;
import instruction.OriInstruction;
import instruction.RemInstruction;
import instruction.SbInstruction;
import instruction.ShInstruction;
import instruction.SllvInstruction;
import instruction.SltInstruction;
import instruction.SltiInstruction;
import instruction.SltiuInstruction;
import instruction.SltuInstruction;
import instruction.SravInstruction;
import instruction.SrlvInstruction;
import instruction.SubInstruction;
import instruction.SwInstruction;
import instruction.XorInstruction;
import instruction.XoriInstruction;

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
            Cpu cpu, int newPc, int[] registersBefore, byte[] memoryBefore,
            List<Instruction> program) {

        System.out.println("==================================================");
        System.out.println("STEP " + step);
        System.out.println("PC      : " + currentPc);
        System.out.println("INSTR   : " + instruction.toAssembly());

        System.out.println("--------------------------------------------------");
        System.out.println("REGISTERS");
        printRegisters(cpu);

        System.out.println("--------------------------------------------------");
        System.out.println("SPECIAL REGISTERS");
        printSpecialRegisters(cpu);

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
        int[] targets = { 0, 2, 8, 9, 10, 11, 12, 31 };

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
     * 命令に応じたイベントを表示する。
     * 
     * @param instruction 命令
     * @param cpu         CPU
     * @param oldPc       実行前PC
     * @param newPc       実行後PC
     */
    private void printEvent(Instruction instruction, Cpu cpu, int oldPc, int newPc) {
        if (printJumpEvent(instruction, cpu, oldPc, newPc)) {
            return;
        }

        if (printMemoryEvent(instruction, cpu)) {
            return;
        }

        if (printArithmeticEvent(instruction, cpu)) {
            return;
        }

        if (printShiftEvent(instruction, cpu)) {
            return;
        }

        if (printLogicEvent(instruction, cpu)) {
            return;
        }

        if (printComparisonEvent(instruction, cpu)) {
            return;
        }

        if (newPc != oldPc + 1) {
            System.out.println("PC changed: " + oldPc + " -> " + newPc);
        } else {
            System.out.println("sequential execution");
        }
    }

    /**
     * ジャンプ系のPC変化イベントを表示する。
     * 
     * @param instruction 命令
     * @param cpu         CPU
     * @param oldPc       実行前PC
     * @param newPc       実行後PC
     * @return 対応するイベントがあれば true
     */
    private boolean printJumpEvent(Instruction instruction, Cpu cpu, int oldPc, int newPc) {
        if (instruction instanceof JalInstruction) {
            System.out.println("call: save return address ($ra = " + cpu.getRegister(31) + ")");
            System.out.println("jump to: PC " + newPc);
            return true;
        }

        if (instruction instanceof JrInstruction jrInstruction) {
            int registerIndex = jrInstruction.getSrcRegister();
            String registerName = RegisterNames.getName(registerIndex);

            if ("$ra".equals(registerName)) {
                System.out.println("return: jump to $ra");
            } else {
                System.out.println("jump register: " + registerName);
            }

            System.out.println("jump to: PC " + newPc);
            return true;
        }

        if (instruction instanceof BeqInstruction) {
            if (newPc != oldPc + 1) {
                System.out.println("branch taken: beq matched");
                System.out.println("jump to: PC " + newPc);
            } else {
                System.out.println("branch not taken: beq did not match");
            }
            return true;
        }

        if (instruction instanceof BneInstruction) {
            if (newPc != oldPc + 1) {
                System.out.println("branch taken: bne matched");
                System.out.println("jump to: PC " + newPc);
            } else {
                System.out.println("branch not taken: bne did not match");
            }
            return true;
        }

        if (instruction instanceof JumpInstruction) {
            System.out.println("jump: PC changed");
            System.out.println("jump to: PC " + newPc);
            return true;
        }

        if (instruction instanceof JalrInstruction jalrInstruction) {
            int registerIndex = jalrInstruction.getSrcRegister();
            String registerName = RegisterNames.getName(registerIndex);

            System.out.println("call register: save return address ($ra = " + cpu.getRegister(31) + ")");
            System.out.println("jump to: " + registerName + " -> PC " + newPc);
            return true;
        }

        if (instruction instanceof BgezInstruction) {
            if (newPc != oldPc + 1) {
                System.out.println("branch taken: bgez matched");
                System.out.println("jump to: PC " + newPc);
            } else {
                System.out.println("branch not taken: bgez did not match");
            }
            return true;
        }

        if (instruction instanceof BltzInstruction) {
            if (newPc != oldPc + 1) {
                System.out.println("branch taken: bltz matched");
                System.out.println("jump to: PC " + newPc);
            } else {
                System.out.println("branch not taken: bltz did not match");
            }
            return true;
        }

        if (instruction instanceof BgtzInstruction) {
            if (newPc != oldPc + 1) {
                System.out.println("branch taken: bgtz matched");
                System.out.println("jump to: PC " + newPc);
            } else {
                System.out.println("branch not taken: bgtz did not match");
            }
            return true;
        }

        if (instruction instanceof BlezInstruction) {
            if (newPc != oldPc + 1) {
                System.out.println("branch taken: blez matched");
                System.out.println("jump to: PC " + newPc);
            } else {
                System.out.println("branch not taken: blez did not match");
            }
            return true;
        }

        return false;
    }

    /**
     * メモリ操作系のイベントを表示する。
     * 
     * @param instruction 命令
     * @param cpu         CPU
     * @return 対応するイベントがあれば true
     */
    private boolean printMemoryEvent(Instruction instruction, Cpu cpu) {
        if (instruction instanceof LwInstruction lwInstruction) {
            int targetRegister = lwInstruction.getDestRegister();
            int baseRegister = lwInstruction.getBaseRegister();
            int offset = lwInstruction.getOffset();

            int address = cpu.getRegister(baseRegister) + offset;
            String targetName = RegisterNames.getName(targetRegister);

            System.out.println("load word: " + targetName + " = mem[" + address + "]");
            System.out.println("loaded value: " + cpu.getRegister(targetRegister));
            return true;
        }

        if (instruction instanceof SwInstruction swInstruction) {
            int srcRegister = swInstruction.getSrcRegister();
            int baseRegister = swInstruction.getBaseRegister();
            int offset = swInstruction.getOffset();

            int address = cpu.getRegister(baseRegister) + offset;
            String srcName = RegisterNames.getName(srcRegister);

            System.out.println("store word: mem[" + address + "] = " + cpu.loadWord(address));
            System.out.println("stored from: " + srcName + " (" + cpu.getRegister(srcRegister) + ")");
            return true;
        }

        if (instruction instanceof LbInstruction lbInstruction) {
            int destRegister = lbInstruction.getDestRegister();
            int baseRegister = lbInstruction.getBaseRegister();
            int offset = lbInstruction.getOffset();

            int address = cpu.getRegister(baseRegister) + offset;
            String destName = RegisterNames.getName(destRegister);

            System.out.println("load byte: " + destName + " = mem[" + address + "]");
            System.out.println("loaded value: " + cpu.getRegister(destRegister));
            return true;
        }

        if (instruction instanceof SbInstruction sbInstruction) {
            int srcRegister = sbInstruction.getSrcRegister();
            int baseRegister = sbInstruction.getBaseRegister();
            int offset = sbInstruction.getOffset();

            int address = cpu.getRegister(baseRegister) + offset;
            String srcName = RegisterNames.getName(srcRegister);

            System.out.println("store byte: mem[" + address + "] = " + (cpu.loadByte(address) & 0xFF));
            System.out.println("stored from: " + srcName + " (" + (cpu.getRegister(srcRegister) & 0xFF) + ")");
            return true;
        }

        if (instruction instanceof LhInstruction lhInstruction) {
            int destRegister = lhInstruction.getDestRegister();
            int baseRegister = lhInstruction.getBaseRegister();
            int offset = lhInstruction.getOffset();

            int address = cpu.getRegister(baseRegister) + offset;
            String destName = RegisterNames.getName(destRegister);

            System.out.println("load halfword: " + destName + " = mem[" + address + ".." + (address + 1) + "]");
            System.out.println("loaded value: " + cpu.getRegister(destRegister));
            return true;
        }

        if (instruction instanceof ShInstruction shInstruction) {
            int srcRegister = shInstruction.getSrcRegister();
            int baseRegister = shInstruction.getBaseRegister();
            int offset = shInstruction.getOffset();

            int address = cpu.getRegister(baseRegister) + offset;
            String srcName = RegisterNames.getName(srcRegister);

            System.out.println("store halfword: mem[" + address + ".." + (address + 1) + "] = "
                    + (cpu.loadHalfWord(address) & 0xFFFF));
            System.out.println("stored from: " + srcName + " (" + (cpu.getRegister(srcRegister) & 0xFFFF) + ")");
            return true;
        }

        if (instruction instanceof LbuInstruction lbuInstruction) {
            int destRegister = lbuInstruction.getDestRegister();
            int baseRegister = lbuInstruction.getBaseRegister();
            int offset = lbuInstruction.getOffset();

            int address = cpu.getRegister(baseRegister) + offset;
            String destName = RegisterNames.getName(destRegister);

            System.out.println("load byte unsigned: " + destName + " = mem[" + address + "]");
            System.out.println("loaded value: " + cpu.getRegister(destRegister));
            return true;
        }

        if (instruction instanceof LhuInstruction lhuInstruction) {
            int destRegister = lhuInstruction.getDestRegister();
            int baseRegister = lhuInstruction.getBaseRegister();
            int offset = lhuInstruction.getOffset();

            int address = cpu.getRegister(baseRegister) + offset;
            String destName = RegisterNames.getName(destRegister);

            System.out
                    .println("load halfword unsigned: " + destName + " = mem[" + address + ".." + (address + 1) + "]");
            System.out.println("loaded value: " + cpu.getRegister(destRegister));
            return true;
        }

        return false;
    }

    /**
     * 算術演算系のイベントを表示する。
     * 
     * @param instruction 命令
     * @param cpu         CPU
     * @return 対応するイベントがあれば true
     */
    private boolean printArithmeticEvent(Instruction instruction, Cpu cpu) {
        if (instruction instanceof AddInstruction addInstruction) {
            String destName = RegisterNames.getName(addInstruction.getDestRegister());
            String leftName = RegisterNames.getName(addInstruction.getLeftRegister());
            String rightName = RegisterNames.getName(addInstruction.getRightRegister());

            System.out.println("arithmetic: " + destName + " = " + leftName + " + " + rightName);
            System.out.println("result: " + cpu.getRegister(addInstruction.getDestRegister()));
            return true;
        }

        if (instruction instanceof AddiInstruction addiInstruction) {
            String destName = RegisterNames.getName(addiInstruction.getDestRegister());
            String srcName = RegisterNames.getName(addiInstruction.getSrcRegister());

            System.out.println("arithmetic: " + destName + " = " + srcName
                    + " + " + addiInstruction.getImmediate());
            System.out.println("result: " + cpu.getRegister(addiInstruction.getDestRegister()));
            return true;
        }

        if (instruction instanceof SubInstruction subInstruction) {
            String destName = RegisterNames.getName(subInstruction.getDestRegister());
            String leftName = RegisterNames.getName(subInstruction.getLeftRegister());
            String rightName = RegisterNames.getName(subInstruction.getRightRegister());

            System.out.println("arithmetic: " + destName + " = " + leftName + " - " + rightName);
            System.out.println("result: " + cpu.getRegister(subInstruction.getDestRegister()));
            return true;
        }

        if (instruction instanceof LuiInstruction luiInstruction) {
            String destName = RegisterNames.getName(luiInstruction.getDestRegister());

            System.out.println("load upper: " + destName + " = "
                    + luiInstruction.getImmediateValue() + " << 16");
            System.out.println("result: " + cpu.getRegister(luiInstruction.getDestRegister()));
            return true;
        }

        if (instruction instanceof MfhiInstruction mfhiInstruction) {
            String destName = RegisterNames.getName(mfhiInstruction.getDestRegister());

            System.out.println("move from HI: " + destName + " = HI");
            System.out.println("value: " + cpu.getRegister(mfhiInstruction.getDestRegister()));
            return true;
        }

        if (instruction instanceof MfloInstruction mfloInstruction) {
            String destName = RegisterNames.getName(mfloInstruction.getDestRegister());

            System.out.println("move from LO: " + destName + " = LO");
            System.out.println("value: " + cpu.getRegister(mfloInstruction.getDestRegister()));
            return true;
        }

        if (instruction instanceof MthiInstruction mthiInstruction) {
            String srcName = RegisterNames.getName(mthiInstruction.getSrcRegister());

            System.out.println("move to HI: HI = " + srcName);
            System.out.println("value: " + cpu.getHi());
            return true;
        }

        if (instruction instanceof MtloInstruction mtloInstruction) {
            String srcName = RegisterNames.getName(mtloInstruction.getSrcRegister());

            System.out.println("move to LO: LO = " + srcName);
            System.out.println("value: " + cpu.getLo());
            return true;
        }

        if (instruction instanceof MultInstruction multInstruction) {
            String leftName = RegisterNames.getName(multInstruction.getLeftRegister());
            String rightName = RegisterNames.getName(multInstruction.getRightRegister());

            System.out.println("multiply: HI:LO = " + leftName + " * " + rightName);
            System.out.println("HI = " + cpu.getHi());
            System.out.println("LO = " + cpu.getLo());
            return true;
        }

        if (instruction instanceof MultuInstruction multuInstruction) {
            String leftName = RegisterNames.getName(multuInstruction.getLeftRegister());
            String rightName = RegisterNames.getName(multuInstruction.getRightRegister());

            System.out.println("multiply unsigned: HI:LO = " + leftName + " * " + rightName);
            System.out.println("HI = " + cpu.getHi());
            System.out.println("LO = " + cpu.getLo());
            return true;
        }

        if (instruction instanceof DivInstruction divInstruction) {
            String leftName = RegisterNames.getName(divInstruction.getLeftRegister());
            String rightName = RegisterNames.getName(divInstruction.getRightRegister());

            System.out.println("divide: " + leftName + " / " + rightName);
            System.out.println("LO (quotient) = " + cpu.getLo());
            System.out.println("HI (remainder) = " + cpu.getHi());
            return true;
        }

        if (instruction instanceof DivuInstruction divuInstruction) {
            String leftName = RegisterNames.getName(divuInstruction.getLeftRegister());
            String rightName = RegisterNames.getName(divuInstruction.getRightRegister());

            System.out.println("divide unsigned: " + leftName + " / " + rightName);
            System.out.println("LO (quotient) = " + cpu.getLo());
            System.out.println("HI (remainder) = " + cpu.getHi());
            return true;
        }

        if (instruction instanceof RemInstruction remInstruction) {
            String destName = RegisterNames.getName(remInstruction.getDestRegister());
            String leftName = RegisterNames.getName(remInstruction.getLeftRegister());
            String rightName = RegisterNames.getName(remInstruction.getRightRegister());

            System.out.println("remainder: " + destName + " = " + leftName + " % " + rightName);
            System.out.println("result: " + cpu.getRegister(remInstruction.getDestRegister()));
            System.out.println("LO (quotient) = " + cpu.getLo());
            System.out.println("HI (remainder) = " + cpu.getHi());
            return true;
        }

        return false;
    }

    /**
     * シフト命令系のイベントを表示する。
     *
     * @param instruction 命令
     * @param cpu         CPU
     * @return 対応するイベントがあれば true
     */
    private boolean printShiftEvent(Instruction instruction, Cpu cpu) {
        if (instruction instanceof SllvInstruction sllvInstruction) {
            String destName = RegisterNames.getName(sllvInstruction.getDestRegister());
            String valueName = RegisterNames.getName(sllvInstruction.getValueRegister());
            String shiftName = RegisterNames.getName(sllvInstruction.getShiftRegister());

            System.out.println("shift: " + destName + " = " + valueName + " << " + shiftName);
            System.out.println("result: " + cpu.getRegister(sllvInstruction.getDestRegister()));
            return true;
        }

        if (instruction instanceof SrlvInstruction srlvInstruction) {
            String destName = RegisterNames.getName(srlvInstruction.getDestRegister());
            String valueName = RegisterNames.getName(srlvInstruction.getValueRegister());
            String shiftName = RegisterNames.getName(srlvInstruction.getShiftRegister());

            System.out.println("shift: " + destName + " = " + valueName + " >>> " + shiftName);
            System.out.println("result: " + cpu.getRegister(srlvInstruction.getDestRegister()));
            return true;
        }

        if (instruction instanceof SravInstruction sravInstruction) {
            String destName = RegisterNames.getName(sravInstruction.getDestRegister());
            String valueName = RegisterNames.getName(sravInstruction.getValueRegister());
            String shiftName = RegisterNames.getName(sravInstruction.getShiftRegister());

            System.out.println("shift: " + destName + " = " + valueName + " >> " + shiftName);
            System.out.println("result: " + cpu.getRegister(sravInstruction.getDestRegister()));
            return true;
        }

        return false;
    }

    /**
     * 論理演算系のイベントを表示する。
     * 
     * @param instruction 命令
     * @param cpu         CPU
     * @return 対応するイベントがあれば true
     */
    private boolean printLogicEvent(Instruction instruction, Cpu cpu) {
        if (instruction instanceof AndInstruction andInstruction) {
            String destName = RegisterNames.getName(andInstruction.getDestRegister());
            String leftName = RegisterNames.getName(andInstruction.getLeftRegister());
            String rightName = RegisterNames.getName(andInstruction.getRightRegister());

            System.out.println("logic: " + destName + " = " + leftName + " & " + rightName);
            System.out.println("result: " + cpu.getRegister(andInstruction.getDestRegister()));
            return true;
        }

        if (instruction instanceof OrInstruction orInstruction) {
            String destName = RegisterNames.getName(orInstruction.getDestRegister());
            String leftName = RegisterNames.getName(orInstruction.getLeftRegister());
            String rightName = RegisterNames.getName(orInstruction.getRightRegister());

            System.out.println("logic: " + destName + " = " + leftName + " | " + rightName);
            System.out.println("result: " + cpu.getRegister(orInstruction.getDestRegister()));
            return true;
        }

        if (instruction instanceof XorInstruction xorInstruction) {
            String destName = RegisterNames.getName(xorInstruction.getDestRegister());
            String leftName = RegisterNames.getName(xorInstruction.getLeftRegister());
            String rightName = RegisterNames.getName(xorInstruction.getRightRegister());

            System.out.println("logic: " + destName + " = " + leftName + " ^ " + rightName);
            System.out.println("result: " + cpu.getRegister(xorInstruction.getDestRegister()));
            return true;
        }

        if (instruction instanceof NorInstruction norInstruction) {
            String destName = RegisterNames.getName(norInstruction.getDestRegister());
            String leftName = RegisterNames.getName(norInstruction.getLeftRegister());
            String rightName = RegisterNames.getName(norInstruction.getRightRegister());

            System.out.println("logic: " + destName + " = ~(" + leftName + " | " + rightName + ")");
            System.out.println("result: " + cpu.getRegister(norInstruction.getDestRegister()));
            return true;
        }

        if (instruction instanceof AndiInstruction andiInstruction) {
            String destName = RegisterNames.getName(andiInstruction.getDestRegister());
            String srcName = RegisterNames.getName(andiInstruction.getSrcRegister());

            System.out.println("logic: " + destName + " = " + srcName
                    + " & " + andiInstruction.getImmediateValue());
            System.out.println("result: " + cpu.getRegister(andiInstruction.getDestRegister()));
            return true;
        }

        if (instruction instanceof OriInstruction oriInstruction) {
            String destName = RegisterNames.getName(oriInstruction.getDestRegister());
            String srcName = RegisterNames.getName(oriInstruction.getSrcRegister());

            System.out.println("logic: " + destName + " = " + srcName
                    + " | " + oriInstruction.getImmediateValue());
            System.out.println("result: " + cpu.getRegister(oriInstruction.getDestRegister()));
            return true;
        }

        if (instruction instanceof XoriInstruction xoriInstruction) {
            String destName = RegisterNames.getName(xoriInstruction.getDestRegister());
            String srcName = RegisterNames.getName(xoriInstruction.getSrcRegister());

            System.out.println("logic: " + destName + " = " + srcName
                    + " ^ " + xoriInstruction.getImmediateValue());
            System.out.println("result: " + cpu.getRegister(xoriInstruction.getDestRegister()));
            return true;
        }

        return false;
    }

    /**
     * 比較命令系のイベントを表示する。
     *
     * @param instruction 命令
     * @param cpu         CPU
     * @return 対応するイベントがあれば true
     */
    private boolean printComparisonEvent(Instruction instruction, Cpu cpu) {
        if (instruction instanceof SltInstruction sltInstruction) {
            String destName = RegisterNames.getName(sltInstruction.getDestRegister());
            String leftName = RegisterNames.getName(sltInstruction.getLeftRegister());
            String rightName = RegisterNames.getName(sltInstruction.getRightRegister());

            System.out.println("compare: " + destName + " = (" + leftName + " < " + rightName + ")");
            System.out.println("result: " + cpu.getRegister(sltInstruction.getDestRegister()));
            return true;
        }

        if (instruction instanceof SltiInstruction sltiInstruction) {
            String destName = RegisterNames.getName(sltiInstruction.getDestRegister());
            String srcName = RegisterNames.getName(sltiInstruction.getSrcRegister());

            System.out.println("compare: " + destName + " = (" + srcName + " < "
                    + sltiInstruction.getImmediateValue() + ")");
            System.out.println("result: " + cpu.getRegister(sltiInstruction.getDestRegister()));
            return true;
        }

        if (instruction instanceof SltuInstruction sltuInstruction) {
            String destName = RegisterNames.getName(sltuInstruction.getDestRegister());
            String leftName = RegisterNames.getName(sltuInstruction.getLeftRegister());
            String rightName = RegisterNames.getName(sltuInstruction.getRightRegister());

            System.out.println("compare unsigned: " + destName + " = (" + leftName + " < " + rightName + ")");
            System.out.println("result: " + cpu.getRegister(sltuInstruction.getDestRegister()));
            return true;
        }

        if (instruction instanceof SltiuInstruction sltiuInstruction) {
            String destName = RegisterNames.getName(sltiuInstruction.getDestRegister());
            String srcName = RegisterNames.getName(sltiuInstruction.getSrcRegister());

            System.out.println("compare unsigned: " + destName + " = (" + srcName + " < "
                    + sltiuInstruction.getImmediateValue() + ")");
            System.out.println("result: " + cpu.getRegister(sltiuInstruction.getDestRegister()));
            return true;
        }

        return false;
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
    private void printMemoryDiff(Cpu cpu, byte[] before, int start, int end) {
        boolean changed = false;

        for (int i = start; i <= end; i++) {
            int beforeValue = before[i] & 0xFF;
            int afterValue = cpu.loadByte(i) & 0xFF;

            if (beforeValue != afterValue) {
                System.out.println("mem[" + i + "] : "
                        + beforeValue + " -> " + afterValue);
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

    /**
     * 特殊レジスタ（HI,LO）を表示する。
     * 
     * @param cpu CPU
     */
    private void printSpecialRegisters(Cpu cpu) {
        System.out.println(String.format("%-5s = %d", "HI", cpu.getHi()));
        System.out.println(String.format("%-5s = %d", "LO", cpu.getLo()));
    }
}