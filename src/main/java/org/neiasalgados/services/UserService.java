package org.neiasalgados.services;

import org.neiasalgados.domain.dto.response.MessageResponseDTO;
import org.neiasalgados.domain.dto.response.PageResponseDTO;
import org.neiasalgados.domain.dto.response.ResponseDataDTO;
import org.neiasalgados.domain.dto.response.UserResponseDTO;
import org.neiasalgados.domain.entity.User;
import org.neiasalgados.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public ResponseDataDTO<PageResponseDTO<UserResponseDTO>> findAll(String surname, Pageable pageable) {
        Page<User> userPage = Optional.ofNullable(surname)
                .filter(name -> !name.isEmpty())
                .map(name -> userRepository.findBySurnameContaining(name, pageable))
                .orElseGet(() -> userRepository.findAll(pageable));

        Page<UserResponseDTO> userResponseDTOPage = userPage.map(user -> new UserResponseDTO(user.getName(), user.getSurname(), user.getCpf(), user.getPhone(), user.getEmail(), user.isActive()));
        var pageResponse = new PageResponseDTO<>(userResponseDTOPage);
        var messageResponse = new MessageResponseDTO("success", "Sucesso", List.of("Usuários listados com sucesso"));

        return new ResponseDataDTO<>(pageResponse, messageResponse, HttpStatus.OK.value());
    }
}
