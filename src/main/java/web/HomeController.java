package web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * MipsStepLab Web版のトップページを表示するController。
 *
 * Controllerは、ブラウザから送られてきたリクエストを受け取り、
 * それに対応するレスポンスを返す役割を持つ。
 */
@RestController
public class HomeController {

    /**
     * MipsStepLab Web版のトップページ用文字列を返す。
     *
     * ブラウザで http://localhost:8080/mips にアクセスすると、
     * このメソッドの戻り値が表示される。
     *
     * @return トップページに表示する文字列
     */
    @GetMapping("/mips")
    public String home() {
        return "MipsStepLab Web";
    }
}