package ru.shift.userimporter.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.shift.userimporter.api.dto.ClientResponse;
import ru.shift.userimporter.core.service.ClientService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @GetMapping("/clients")
    public List<ClientResponse> getClients(
            @RequestParam(required = false) Long phone,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String email,
            @RequestParam(defaultValue = "100") int limit,
            @RequestParam(defaultValue = "0") int offset) {
        return clientService.getClients(phone, name, lastName, email, limit, offset);
    }
}