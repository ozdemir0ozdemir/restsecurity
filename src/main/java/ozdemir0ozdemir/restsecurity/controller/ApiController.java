package ozdemir0ozdemir.restsecurity.controller;

import org.glowroot.agent.api.Instrumentation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public record ApiController() {

    @Instrumentation.Timer("Get Authentication")
    @Instrumentation.TraceEntry(message = "Authentication", timer = "Get Authentication")
    @GetMapping("api/data")
    public ResponseEntity<String> getSecureData() {
        Authentication token =
                SecurityContextHolder.getContext().getAuthentication();
        String s = "Token : %s".formatted(token);
        return ResponseEntity.ok(s);
    }

    @GetMapping("data")
    public ResponseEntity<String> getUnsecureData() {
        Authentication token =
                SecurityContextHolder.getContext().getAuthentication();
        String s = "Token : %s".formatted(token);
        return ResponseEntity.ok(s);
    }
}
