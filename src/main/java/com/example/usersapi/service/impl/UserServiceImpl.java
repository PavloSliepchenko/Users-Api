package com.example.usersapi.service.impl;

import com.example.usersapi.dto.CreateUserRequestDto;
import com.example.usersapi.dto.UserPatchRequestDto;
import com.example.usersapi.dto.UserResponseDto;
import com.example.usersapi.dto.UserResponseDtoWrapper;
import com.example.usersapi.exception.DatesOrderException;
import com.example.usersapi.exception.EntityNotFoundException;
import com.example.usersapi.exception.RegistrationException;
import com.example.usersapi.mapper.UserMapper;
import com.example.usersapi.model.User;
import com.example.usersapi.repository.UserRepository;
import com.example.usersapi.service.UserService;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    @Value("${user.min.age}")
    private int minAge;

    @Override
    public UserResponseDtoWrapper<UserResponseDto> createUser(CreateUserRequestDto requestDto) {
        User user = userMapper.toModel(requestDto);
        if (Period.between(user.getBirthDate(), LocalDate.now()).getYears() <= minAge) {
            throw new RegistrationException(
                    String.format("The user must be over %s years old", minAge));
        }
        return new UserResponseDtoWrapper<>(userMapper.toDto(userRepository.save(user)));
    }

    @Override
    public UserResponseDtoWrapper<UserResponseDto> patchUser(
            Long userId, UserPatchRequestDto requestDto) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("There is no user with id " + userId));
        if (requestDto.email() != null) {
            user.setEmail(requestDto.email());
        }
        if (requestDto.firstName() != null) {
            user.setFirstName(requestDto.firstName());
        }
        if (requestDto.lastName() != null) {
            user.setLastName(requestDto.lastName());
        }
        if (requestDto.birthDate() != null) {
            user.setBirthDate(LocalDate.parse(requestDto.birthDate(), formatter));
        }
        if (requestDto.address() != null) {
            user.setAddress(requestDto.address());
        }
        if (requestDto.phoneNumber() != null) {
            user.setPhoneNumber(requestDto.phoneNumber());
        }
        return new UserResponseDtoWrapper<>(userMapper.toDto(userRepository.save(user)));
    }

    @Override
    public UserResponseDtoWrapper<UserResponseDto> updateUser(
            Long userId, CreateUserRequestDto requestDto) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("There is no user with id " + userId);
        }
        User user = userMapper.toModel(requestDto);
        user.setId(userId);
        return new UserResponseDtoWrapper<>(userMapper.toDto(userRepository.save(user)));
    }

    @Override
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("There is no user with id " + userId);
        }
        userRepository.deleteById(userId);
    }

    @Override
    public UserResponseDtoWrapper<List<UserResponseDto>> searchByBirthDates(
            String fromDate, String toDate) {
        LocalDate from = LocalDate.parse(fromDate, formatter);
        LocalDate to = LocalDate.parse(toDate, formatter);
        if (to.isBefore(from) || to.isEqual(from)) {
            throw new DatesOrderException("'From date' must be before 'to date'");
        }
        List<UserResponseDto> result = userRepository.findByBirthDateBetween(from, to).stream()
                .map(userMapper::toDto)
                .toList();
        return new UserResponseDtoWrapper<>(result);
    }
}
