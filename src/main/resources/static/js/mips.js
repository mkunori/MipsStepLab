/**
 * MipsStepLab画面の補助処理を行う。
 *
 * 主な役割:
 * - フォーム送信後もスクロール位置を維持する
 * - ボタンの多重送信を防ぐ
 * - サンプルプログラムの入力と説明表示を行う
 * - メモリ表示を10進数 / 16進数で切り替える
 * - 現在PC行がプログラム一覧内で見える位置へ自動スクロールする
 */
document.addEventListener("DOMContentLoaded", () => {
    restoreScrollPosition();
    setupFormSubmitHandlers();
    setupSampleProgramSelector();
    setupMemoryDisplayToggle();
    scrollCurrentInstructionIntoView();
});

/** スクロール位置を保存するためのキー。 */
const SCROLL_KEY = "mips-scroll-y";

/** メモリ表示形式を保存するためのキー。 */
const MEMORY_DISPLAY_MODE_KEY = "mips-memory-display-mode";

/** メモリ表示形式の初期値。 */
const DEFAULT_MEMORY_DISPLAY_MODE = "decimal";

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
 * プルダウンでサンプルを選ぶと説明を表示する。
 * 「サンプルを入力」ボタンを押すと、textareaへサンプルプログラムを反映する。
 */
function setupSampleProgramSelector() {
    const select = document.getElementById("sampleProgramSelect");
    const button = document.getElementById("loadSampleButton");
    const textarea = document.getElementById("programTextArea");
    const description = document.getElementById("sampleProgramDescription");

    if (select === null || button === null || textarea === null || description === null) {
        return;
    }

    updateSampleDescription(select, description);

    select.addEventListener("change", () => {
        updateSampleDescription(select, description);
    });

    button.addEventListener("click", () => {
        const sample = findSelectedSample(select);

        if (sample === null) {
            return;
        }

        textarea.value = sample.program;
    });
}

/**
 * 選択中のサンプル説明を表示する。
 *
 * @param {HTMLSelectElement} select サンプル選択欄
 * @param {HTMLElement} description 説明表示欄
 */
function updateSampleDescription(select, description) {
    const sample = findSelectedSample(select);

    if (sample === null) {
        description.textContent = "サンプルを選択すると、ここに説明が表示されます。";
        return;
    }

    description.textContent = sample.description;
}

/**
 * 選択中のサンプルプログラムを取得する。
 *
 * @param {HTMLSelectElement} select サンプル選択欄
 * @returns {{program: string, description: string} | null} サンプルプログラム情報
 */
function findSelectedSample(select) {
    const sampleKey = select.value;

    if (sampleKey === "") {
        return null;
    }

    const sample = SAMPLE_PROGRAMS[sampleKey];

    if (sample === undefined) {
        return null;
    }

    return sample;
}



/**
 * メモリ表示形式切り替えボタンのイベントを設定する。
 */
function setupMemoryDisplayToggle() {
    const buttons = document.querySelectorAll("[data-memory-display-mode]");

    if (buttons.length === 0) {
        return;
    }

    const savedMode = sessionStorage.getItem(MEMORY_DISPLAY_MODE_KEY);
    const initialMode = isValidMemoryDisplayMode(savedMode)
        ? savedMode
        : DEFAULT_MEMORY_DISPLAY_MODE;

    updateMemoryDisplay(initialMode);

    buttons.forEach((button) => {
        button.addEventListener("click", () => {
            const mode = button.dataset.memoryDisplayMode;

            if (!isValidMemoryDisplayMode(mode)) {
                return;
            }

            sessionStorage.setItem(MEMORY_DISPLAY_MODE_KEY, mode);
            updateMemoryDisplay(mode);
        });
    });
}

/**
 * メモリ表示形式として使える値かを判定する。
 *
 * @param {string | null | undefined} mode 表示形式
 * @returns {boolean} 使える表示形式の場合はtrue
 */
function isValidMemoryDisplayMode(mode) {
    return mode === "decimal" || mode === "hex";
}

/**
 * メモリ表示を指定された形式へ切り替える。
 *
 * @param {string} mode 表示形式
 */
function updateMemoryDisplay(mode) {
    const numbers = document.querySelectorAll(".memory-display-number");
    const buttons = document.querySelectorAll("[data-memory-display-mode]");

    numbers.forEach((number) => {
        const text = mode === "hex" ? number.dataset.hex : number.dataset.decimal;

        if (text !== undefined) {
            number.textContent = text;
        }
    });

    buttons.forEach((button) => {
        button.classList.toggle("active", button.dataset.memoryDisplayMode === mode);
    });
}

/**
 * 現在PC行がプログラム一覧内で見える位置へスクロールする。
 *
 * プログラム一覧が縦に長い場合でも、ステップ実行後に次の実行位置を追いやすくする。
 */
function scrollCurrentInstructionIntoView() {
    const programList = document.querySelector(".program-list");
    const currentInstruction = document.querySelector(".program-list .current-instruction");

    if (programList === null || currentInstruction === null) {
        return;
    }

    const targetScrollTop = currentInstruction.offsetTop
        - programList.offsetTop
        - (programList.clientHeight / 2)
        + (currentInstruction.clientHeight / 2);

    programList.scrollTop = Math.max(0, targetScrollTop);
}

/**
 * MIPS命令のサンプルプログラム一覧。
 *
 * keyはHTML側のselect option valueと対応している。
 */
const SAMPLE_PROGRAMS = {
    arithmetic: {
        description: "addi / add / sub を使って、レジスタ値の加算・減算を確認します。",
        program: [
            "addi $t0, $zero, 5",
            "addi $t1, $zero, 3",
            "add $t2, $t0, $t1",
            "sub $t3, $t0, $t1"
        ].join("\n")
    },

    logical: {
        description: "and / or / xor / nor などを使って、ビット単位の論理演算を確認します。",
        program: [
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
        ].join("\n")
    },

    shift: {
        description: "sll / srl / sra と可変シフト命令を使って、値が左右にシフトする様子を確認します。",
        program: [
            "addi $t0, $zero, 8",
            "addi $t1, $zero, 1",
            "sll $t2, $t0, 1",
            "srl $t3, $t0, 1",
            "sra $t4, $t0, 1",
            "sllv $t5, $t0, $t1",
            "srlv $t6, $t0, $t1",
            "srav $t7, $t0, $t1"
        ].join("\n")
    },

    compare: {
        description: "slt / slti / sltu / sltiu を使って、比較結果が0または1として保存されることを確認します。",
        program: [
            "addi $t0, $zero, 3",
            "addi $t1, $zero, 5",
            "slt $t2, $t0, $t1",
            "slti $t3, $t0, 4",
            "sltu $t4, $t0, $t1",
            "sltiu $t5, $t0, 4"
        ].join("\n")
    },

    branch: {
        description: "ラベルとbneを使って、条件が成り立つ間だけループする流れを確認します。",
        program: [
            "addi $t0, $zero, 0",
            "addi $t1, $zero, 3",
            "loop:",
            "addi $t0, $t0, 1",
            "bne $t0, $t1, loop",
            "addi $t2, $zero, 99"
        ].join("\n")
    },

    memory: {
        description: "sw / lw / sb / lb / sh / lh などを使って、メモリへの読み書きを確認します。",
        program: [
            "addi $t0, $zero, 5",
            "sw $t0, 0($zero)",
            "lw $t1, 0($zero)",
            "sb $t0, 4($zero)",
            "lb $t2, 4($zero)",
            "lbu $t3, 4($zero)",
            "sh $t0, 8($zero)",
            "lh $t4, 8($zero)",
            "lhu $t5, 8($zero)"
        ].join("\n")
    },

    hilo: {
        description: "mult / div と mfhi / mflo / mthi / mtlo を使って、HI/LOレジスタの変化を確認します。",
        program: [
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
        ].join("\n")
    },

    pseudo: {
        description: "move / mul / rem / beqz / bnez / b / nop などの擬似命令や簡略命令を確認します。",
        program: [
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
    }
};
