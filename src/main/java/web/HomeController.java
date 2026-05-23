package web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * MipsStepLab Web版の画面を表示するController。
 *
 * Controllerは、ブラウザからのリクエストを受け取り、
 * 表示するHTMLテンプレート名を返す役割を持つ。
 */
@Controller
public class HomeController {

    /**
     * MipsStepLab Web版のトップ画面を表示する。
     *
     * ブラウザで http://localhost:8080/mips にアクセスすると、
     * templates/mips.html が表示される。
     *
     * @return 表示するテンプレート名
     */
    @GetMapping("/mips")
    public String home() {
        return "mips";
    }
}