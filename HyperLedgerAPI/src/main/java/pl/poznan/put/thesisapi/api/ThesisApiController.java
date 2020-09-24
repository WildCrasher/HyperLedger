package pl.poznan.put.thesisapi.api;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.poznan.put.thesisapi.AddToWallet;
import pl.poznan.put.thesisapi.Issue;

@RestController()
@RequestMapping("/api/thesis")
public class ThesisApiController {

    @PostMapping()
    public String issue() {
        new AddToWallet().run();
        new Issue().run();
        return "ok2";
    }
}
