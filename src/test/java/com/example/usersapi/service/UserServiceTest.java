package com.example.usersapi.service;

import com.example.usersapi.dto.CreateUserRequestDto;
import com.example.usersapi.dto.UserPatchRequestDto;
import com.example.usersapi.dto.UserResponseDto;
import com.example.usersapi.exception.EntityNotFoundException;
import com.example.usersapi.exception.RegistrationException;
import com.example.usersapi.mapper.UserMapper;
import com.example.usersapi.model.User;
import com.example.usersapi.repository.UserRepository;
import com.example.usersapi.service.impl.UserServiceImpl;
import com.example.usersapi.util.TestUtil;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    private CreateUserRequestDto requestDto;
    private User user;
    private UserResponseDto responseDto;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void init() throws Exception {
        requestDto = new CreateUserRequestDto(
                "user@gmail.com",
                "Bob",
                "Jackson",
                "01/02/1990",
                "414 Union Ave, Brooklyn, NY 11211",
                "(111) 111-1111"
        );
        user = TestUtil.getUserFromCreateUserDto(1L, requestDto);
        responseDto = TestUtil.getResponseDtoFromUser(user);

        Field field = userService.getClass().getDeclaredField("minAge");
        field.setAccessible(true);
        field.setInt(userService, 18);
    }

    @Test
    @DisplayName("Add a new used to db")
    void createUser_ValidRequest_ShouldAddUser() {
        Mockito.when(userMapper.toModel(requestDto)).thenReturn(user);
        Mockito.when(userRepository.save(user)).thenReturn(user);
        Mockito.when(userMapper.toDto(user)).thenReturn(responseDto);

        UserResponseDto actual = userService.createUser(requestDto).getData();

        Assertions.assertEquals(responseDto.getId(), actual.getId());
        Assertions.assertEquals(responseDto.getBirthDate(), actual.getBirthDate());
        Assertions.assertEquals(responseDto.getFirstName(), actual.getFirstName());
        Assertions.assertEquals(responseDto.getAddress(), actual.getAddress());
    }

    @Test
    @DisplayName("Add a new used to db. Throws Exception")
    void createUser_AgeLessThan18_ThrowsException() {
        CreateUserRequestDto requestDto = new CreateUserRequestDto(
                "user@gmail.com",
                "Bob",
                "Jackson",
                "01/02/2020",
                null,
                null
        );

        User user = TestUtil.getUserFromCreateUserDto(1L, requestDto);
        Mockito.when(userMapper.toModel(requestDto)).thenReturn(user);

        Assertions.assertThrows(RegistrationException.class,
                () -> userService.createUser(requestDto));
    }

    @Test
    @DisplayName("Update some user fields")
    void patchUser_ValidRequest_ShouldUpdateUserField() {
        String newLastName = "Moore";
        Long userId = 1L;
        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setEmail(user.getEmail());
        updatedUser.setLastName(newLastName);
        updatedUser.setFirstName(user.getFirstName());
        updatedUser.setAddress(user.getAddress());
        updatedUser.setPhoneNumber(user.getPhoneNumber());
        updatedUser.setBirthDate(user.getBirthDate());

        UserResponseDto updatedResponseDto = TestUtil.getResponseDtoFromUser(updatedUser);

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(userRepository.save(user)).thenReturn(user);
        Mockito.when(userMapper.toDto(updatedUser)).thenReturn(updatedResponseDto);

        UserPatchRequestDto userPatchRequestDto = new UserPatchRequestDto(
                null, null, newLastName, null, null, null
        );
        UserResponseDto actual = userService.patchUser(userId, userPatchRequestDto).getData();
        Assertions.assertEquals(newLastName, actual.getLastName());

        String newEmail = "usersNewEmal@gmail.com";
        updatedUser.setEmail(newEmail);
        updatedResponseDto.setEmail(newEmail);
        UserPatchRequestDto userPatchRequestDto1 = new UserPatchRequestDto(
                newEmail, null, null, null, null, null
        );
        actual = userService.patchUser(userId, userPatchRequestDto1).getData();
        Assertions.assertEquals(newEmail, actual.getEmail());
    }

    @Test
    @DisplayName("Update all user's info. Throws exception")
    void patchUser_WrongUserId_ShouldThrowException() {
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> userService.patchUser(
                        2L, new UserPatchRequestDto(null, null, null, null, null, null)
                )
        );
    }

    @Test
    @DisplayName("Update all user's info")
    void updateUser_ValidRequest_ShouldUpdateAllUserInfo() {
        CreateUserRequestDto requestDto = new CreateUserRequestDto(
                "userBob@gmail.com",
                "Bobby",
                "Spears",
                "01/02/1989",
                "4005 Dayrl Rd, Jacksonville, FL 32207",
                "(222) 222-2222"
        );
        Long userId = 1L;
        User updatedUser = TestUtil.getUserFromCreateUserDto(userId, requestDto);
        UserResponseDto updatedResponseDto = TestUtil.getResponseDtoFromUser(updatedUser);

        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(userMapper.toModel(requestDto)).thenReturn(updatedUser);
        Mockito.when(userMapper.toDto(updatedUser)).thenReturn(updatedResponseDto);
        Mockito.when(userRepository.save(updatedUser)).thenReturn(updatedUser);

        UserResponseDto actual = userService.updateUser(userId, requestDto).getData();
        Assertions.assertEquals(requestDto.email(), actual.getEmail());
        Assertions.assertEquals(requestDto.firstName(), actual.getFirstName());
        Assertions.assertEquals(requestDto.lastName(), actual.getLastName());
        Assertions.assertEquals(requestDto.address(), actual.getAddress());
        Assertions.assertEquals(requestDto.phoneNumber(), actual.getPhoneNumber());
        Assertions.assertEquals(requestDto.birthDate(), actual.getBirthDate());
    }

    @Test
    @DisplayName("Search for users in birth dates range")
    void searchByBirthDates_ValidRequest_ShouldReturnListOfUsersInDatesRange() {
        User user2 = new User();
        user2.setId(2L);
        user2.setFirstName("Jane");
        user2.setLastName("Smith");
        user2.setEmail("user2@example.com");
        user2.setBirthDate(LocalDate.of(1985, 5, 15));

        User user3 = new User();
        user3.setId(3L);
        user3.setFirstName("Alice");
        user3.setLastName("Johnson");
        user3.setEmail("user3@example.com");
        user3.setBirthDate(LocalDate.of(1988, 9, 20));

        User user4 = new User();
        user4.setId(4L);
        user4.setFirstName("Bob");
        user4.setLastName("Brown");
        user4.setEmail("user4@example.com");
        user4.setBirthDate(LocalDate.of(1995, 3, 10));

        User user5 = new User();
        user5.setId(5L);
        user5.setFirstName("Emily");
        user5.setLastName("Davis");
        user5.setEmail("user5@example.com");
        user5.setBirthDate(LocalDate.of(1992, 11, 28));

        UserResponseDto responseDto2 = TestUtil.getResponseDtoFromUser(user2);
        UserResponseDto responseDto3 = TestUtil.getResponseDtoFromUser(user3);
        UserResponseDto responseDto4 = TestUtil.getResponseDtoFromUser(user4);
        UserResponseDto responseDto5 = TestUtil.getResponseDtoFromUser(user5);

        Mockito.when(userMapper.toDto(user)).thenReturn(responseDto);
        Mockito.when(userMapper.toDto(user2)).thenReturn(responseDto2);
        Mockito.when(userMapper.toDto(user3)).thenReturn(responseDto3);
        Mockito.when(userMapper.toDto(user4)).thenReturn(responseDto4);
        Mockito.when(userMapper.toDto(user5)).thenReturn(responseDto5);

        String from = "02/03/1986";
        String to = "10/03/1995";
        Mockito.when(userRepository.findByBirthDateBetween(
                LocalDate.parse(from, TestUtil.FORMATTER),
                LocalDate.parse(to, TestUtil.FORMATTER))
        ).thenReturn(List.of(user, user3, user4));
        List<UserResponseDto> expected = List.of(responseDto, responseDto3, responseDto4);
        List<UserResponseDto> actual = userService.searchByBirthDates(from, to).getData();
        Assertions.assertEquals(expected.size(), actual.size());
        Assertions.assertTrue(actual.containsAll(expected));

        from = "15/04/1985";
        to = "10/03/2000";
        Mockito.when(userRepository.findByBirthDateBetween(
                LocalDate.parse(from, TestUtil.FORMATTER),
                LocalDate.parse(to, TestUtil.FORMATTER))
        ).thenReturn(List.of(user, user2, user3, user4, user5));
        expected = List.of(responseDto, responseDto2, responseDto3, responseDto4, responseDto5);
        actual = userService.searchByBirthDates(from, to).getData();
        Assertions.assertEquals(expected.size(), actual.size());
        Assertions.assertTrue(actual.containsAll(expected));

        from = "15/04/1990";
        to = "10/03/2003";
        Mockito.when(userRepository.findByBirthDateBetween(
                LocalDate.parse(from, TestUtil.FORMATTER),
                LocalDate.parse(to, TestUtil.FORMATTER))
        ).thenReturn(List.of(user5));
        expected = List.of(responseDto5);
        actual = userService.searchByBirthDates(from, to).getData();
        Assertions.assertEquals(expected.size(), actual.size());
        Assertions.assertTrue(actual.containsAll(expected));
    }
}
