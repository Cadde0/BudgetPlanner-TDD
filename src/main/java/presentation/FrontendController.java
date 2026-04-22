package presentation;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FrontendController {
    @GetMapping({"/", "/index", "/home"})
    public String index() {
        return "index";
    }
}
