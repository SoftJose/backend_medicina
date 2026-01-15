package saitel.medicina.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PRUEBA_AbrirEnNavegador {
  @GetMapping("/")
    public String home() {
        return "La API Medicina está funcionando correctamente.";
}
}
