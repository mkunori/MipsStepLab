package web;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * MipsStepLab Web版の画面を表示するController。
 *
 * Controllerは、ブラウザからのリクエストを受け取り、
 * 表示に必要なデータをHTMLテンプレートへ渡す役割を持つ。
 */
@Controller
public class HomeController {

    /**
     * MipsStepLab Web版のトップ画面を表示する。
     *
     * 現在は動作確認用として、固定のサンプルプログラムを画面へ渡す。
     *
     * @param model HTMLテンプレートへデータを渡すための入れ物
     * @return 表示するテンプレート名
     */
    @GetMapping("/mips")
    public String home(Model model) {
        List<String> sampleProgram = List.of(
                "addi $t0, $zero, 5",
                "addi $t1, $zero, 3",
                "add $t2, $t0, $t1");

        model.addAttribute("sampleProgram", sampleProgram);

        return "mips";
    }
}