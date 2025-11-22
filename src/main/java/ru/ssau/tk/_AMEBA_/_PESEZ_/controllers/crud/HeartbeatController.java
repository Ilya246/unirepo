package ru.ssau.tk._AMEBA_._PESEZ_.controllers.crud;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/heartbeat")
@RequiredArgsConstructor
public class HeartbeatController {
    @GetMapping
    @Operation(summary = "Проверка работоспособности сервера")
    public void getHeartbeat() {}
}
