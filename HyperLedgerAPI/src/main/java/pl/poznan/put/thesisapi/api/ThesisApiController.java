package pl.poznan.put.thesisapi.api;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import pl.poznan.put.thesisapi.AddToWallet;
import pl.poznan.put.thesisapi.Issue;
import pl.poznan.put.thesisapi.Query;
import pl.poznan.put.thesisapi.Thesis;

@RestController()
@RequestMapping("/api/thesis")
public class ThesisApiController {

    @PostMapping()
    public String issue(@RequestBody() Thesis thesis) {
        new AddToWallet().run();
        new Issue().run(thesis);
        return "ok";
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String query(@PathVariable(value = "id") String id) {
        new AddToWallet().run();
        return new Query().run(id);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public String query() {
        new AddToWallet().run();
        return new Query().getAll();
    }
}
