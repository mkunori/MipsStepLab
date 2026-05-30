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