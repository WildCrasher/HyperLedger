package pl.put.hyperledgerapp;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestApi {

    @GetMapping("/test-api")
    public String testApi() {
        return "success";
    }
}
