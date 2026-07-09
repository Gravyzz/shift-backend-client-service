package ru.shift.userimporter.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.shift.userimporter.api.dto.ClientResponse;
import ru.shift.userimporter.api.mapper.ClientMapper;
import ru.shift.userimporter.core.model.User;
import ru.shift.userimporter.core.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final UserRepository userRepository;
    private final ClientMapper clientMapper;

    public List<ClientResponse> getClients(Long phone, String name, String lastName, String email, int limit, int offset) {
        String phoneStr = phone == null ? null : phone.toString();
        List<User> users = userRepository.search(phoneStr, name, lastName, email, limit, offset);
        return users.stream()
                .map(clientMapper::toClientResponse)
                .toList();
    }
}
