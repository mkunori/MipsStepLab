/**
 * MipsStepLab画面の補助処理を行う。
 *
 * 主な役割:
 * - フォーム送信後もスクロール位置を維持する
 * - ボタンの多重送信を防ぐ
 */
document.addEventListener("DOMContentLoaded", () => {
    restoreScrollPosition();
    setupFormSubmitHandlers();
    setupSampleProgramSelector();
});

/** スクロール位置を保存するためのキー。 */
const SCROLL_KEY = "mips-scroll-y";

/**
 * 保存されているスクロール位置へ戻す。
 *
 * フォーム送信後はページが再読み込みされるため、
 * sessionStorageに保存しておいた位置へ戻す。
 */
function restoreScrollPosition() {
    const savedScrollY = sessionStorage.getItem(SCROLL_KEY);

    if (savedScrollY !== null) {
        window.scrollTo(0, Number(savedScrollY));
        sessionStorage.removeItem(SCROLL_KEY);
    }
}

/**
 * 画面内のフォームに送信時の補助処理を設定する。
 *
 * フォーム送信時に現在のスクロール位置を保存し、
 * 送信ボタンを無効化して多重送信を防ぐ。
 */
function setupFormSubmitHandlers() {
    const forms = document.querySelectorAll("form");

    forms.forEach((form) => {
        form.addEventListener("submit", () => {
            sessionStorage.setItem(SCROLL_KEY, String(window.scrollY));
            disableSubmitButtons(form);
        });
    });
}

/**
 * 送信されたフォーム内のsubmitボタンを無効化する。
 *
 * @param {HTMLFormElement} form 送信されたフォーム
 */
function disableSubmitButtons(form) {
    const buttons = form.querySelectorAll("button[type='submit']");

    buttons.forEach((button) => {
        button.disabled = true;
        button.textContent = "処理中...";
    });
}

/**
 * サンプルプログラム選択欄のイベントを設定する。
 *
 * プルダウンでサンプルを選び、「サンプルを入力」ボタンを押すと、
 * textareaへサンプルプログラムを反映する。
 */
function setupSampleProgramSelector() {
    const select = document.getElementById("sampleProgramSelect");
    const button = document.getElementById("loadSampleButton");
    const textarea = document.getElementById("programTextArea");

    if (select === null || button === null || textarea === null) {
        return;
    }

    button.addEventListener("click", () => {
        const sampleKey = select.value;

        if (sampleKey === "") {
            return;
        }

        const sampleProgram = SAMPLE_PROGRAMS[sampleKey];

        if (sampleProgram === undefined) {
            return;
        }

        textarea.value = sampleProgram;
    });
}

/**
 * MIPS命令のサンプルプログラム一覧。
 *
 * keyはHTML側のselect option valueと対応している。
 */
const SAMPLE_PROGRAMS = {
    arithmetic: [
        "addi $t0, $zero, 5",
        "addi $t1, $zero, 3",
        "add $t2, $t0, $t1",
        "sub $t3, $t0, $t1"
    ].join("\n"),

    logical: [
        "addi $t0, $zero, 12",
        "addi $t1, $zero, 10",
        "and $t2, $t0, $t1",
        "or $t3, $t0, $t1",
        "xor $t4, $t0, $t1",
        "nor $t5, $t0, $t1",
        "andi $t6, $t0, 8",
        "ori $t7, $t0, 1",
        "xori $s0, $t0, 15",
        "lui $s1, 1"
    ].join("\n"),

    shift: [
        "addi $t0, $zero, 8",
        "addi $t1, $zero, 1",
        "sll $t2, $t0, 1",
        "srl $t3, $t0, 1",
        "sra $t4, $t0, 1",
        "sllv $t5, $t0, $t1",
        "srlv $t6, $t0, $t1",
        "srav $t7, $t0, $t1"
    ].join("\n"),

    compare: [
        "addi $t0, $zero, 3",
        "addi $t1, $zero, 5",
        "slt $t2, $t0, $t1",
        "slti $t3, $t0, 4",
        "sltu $t4, $t0, $t1",
        "sltiu $t5, $t0, 4"
    ].join("\n"),

    branch: [
        "addi $t0, $zero, 0",
        "addi $t1, $zero, 3",
        "loop:",
        "addi $t0, $t0, 1",
        "bne $t0, $t1, loop",
        "addi $t2, $zero, 99"
    ].join("\n"),

    memory: [
        "addi $t0, $zero, 5",
        "sw $t0, 0($zero)",
        "lw $t1, 0($zero)",
        "sb $t0, 4($zero)",
        "lb $t2, 4($zero)",
        "lbu $t3, 4($zero)",
        "sh $t0, 8($zero)",
        "lh $t4, 8($zero)",
        "lhu $t5, 8($zero)"
    ].join("\n"),

    hilo: [
        "addi $t0, $zero, 5",
        "addi $t1, $zero, 3",
        "mult $t0, $t1",
        "mflo $t2",
        "mfhi $t3",
        "div $t0, $t1",
        "mflo $t4",
        "mfhi $t5",
        "mthi $t0",
        "mtlo $t1",
        "mfhi $t6",
        "mflo $t7"
    ].join("\n"),

    pseudo: [
        "addi $t0, $zero, 10",
        "addi $t1, $zero, 3",
        "move $t2, $t0",
        "mul $t3, $t0, $t1",
        "rem $t4, $t0, $t1",
        "beqz $zero, skip",
        "addi $t5, $zero, 99",
        "skip:",
        "b end",
        "addi $t6, $zero, 88",
        "end:",
        "nop"
    ].join("\n")
};
