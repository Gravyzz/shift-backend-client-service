package ru.shift.userimporter.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.shift.userimporter.api.dto.ClientResponse;
import ru.shift.userimporter.api.mapper.ClientMapper;
import ru.shift.userimporter.core.model.User;
import ru.shift.userimporter.core.repository.OffsetBasedPageRequest;
import ru.shift.userimporter.core.repository.UserRepository;
import ru.shift.userimporter.core.repository.UserSpecifications;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final UserRepository userRepository;
    private final ClientMapper clientMapper;

    @Transactional(readOnly = true)
    public List<ClientResponse> getClients(Long phone, String name, String lastName, String email, int limit, int offset) {
        Specification<User> specification = UserSpecifications.withFilters(phone, name, lastName, email);
        Pageable pageable = new OffsetBasedPageRequest(offset, limit, Sort.by("id"));
        return userRepository.findAll(specification, pageable).stream()
                .map(clientMapper::toClientResponse)
                .toList();
    }
}
