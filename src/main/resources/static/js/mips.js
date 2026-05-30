/**
 * MipsStepLab画面のスクロール位置を保持する。
 *
 * フォーム送信後はページが再読み込みされるため、
 * 送信直前のスクロール位置をsessionStorageに保存し、
 * 読み込み後に同じ位置へ戻す。
 */
document.addEventListener("DOMContentLoaded", () => {
    const scrollKey = "mips-scroll-y";

    const savedScrollY = sessionStorage.getItem(scrollKey);

    if (savedScrollY !== null) {
        window.scrollTo(0, Number(savedScrollY));
        sessionStorage.removeItem(scrollKey);
    }

    const forms = document.querySelectorAll("form");

    forms.forEach((form) => {
        form.addEventListener("submit", () => {
            sessionStorage.setItem(scrollKey, String(window.scrollY));
        });
    });
});