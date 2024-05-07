package com.example.usersapi.service;

import static com.example.usersapi.util.TestUtil.FORMATTER;
import static com.example.usersapi.util.TestUtil.RESPONSE_DTO1;
import static com.example.usersapi.util.TestUtil.RESPONSE_DTO2;
import static com.example.usersapi.util.TestUtil.RESPONSE_DTO3;
import static com.example.usersapi.util.TestUtil.RESPONSE_DTO4;
import static com.example.usersapi.util.TestUtil.RESPONSE_DTO5;
import static com.example.usersapi.util.TestUtil.USER1;
import static com.example.usersapi.util.TestUtil.USER2;
import static com.example.usersapi.util.TestUtil.USER3;
import static com.example.usersapi.util.TestUtil.USER4;
import static com.example.usersapi.util.TestUtil.USER5;

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
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void init() throws Exception {
        Field field = userService.getClass().getDeclaredField("minAge");
        field.setAccessible(true);
        field.setInt(userService, 18);
    }

    @Test
    @DisplayName("Add a new used to db")
    void createUser_ValidRequest_ShouldAddUser() {
        Mockito.when(userMapper.toModel(TestUtil.REQUEST_DTO)).thenReturn(USER1);
        Mockito.when(userRepository.save(USER1)).thenReturn(USER1);
        Mockito.when(userMapper.toDto(USER1)).thenReturn(RESPONSE_DTO1);

        UserResponseDto actual = userService.createUser(TestUtil.REQUEST_DTO).getData();

        Assertions.assertEquals(RESPONSE_DTO1.getId(), actual.getId());
        Assertions.assertEquals(RESPONSE_DTO1.getBirthDate(), actual.getBirthDate());
        Assertions.assertEquals(RESPONSE_DTO1.getFirstName(), actual.getFirstName());
        Assertions.assertEquals(RESPONSE_DTO1.getAddress(), actual.getAddress());
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
        updatedUser.setEmail(USER1.getEmail());
        updatedUser.setLastName(newLastName);
        updatedUser.setFirstName(USER1.getFirstName());
        updatedUser.setAddress(USER1.getAddress());
        updatedUser.setPhoneNumber(USER1.getPhoneNumber());
        updatedUser.setBirthDate(USER1.getBirthDate());

        UserResponseDto updatedResponseDto = TestUtil.getResponseDtoFromUser(updatedUser);

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(USER1));
        Mockito.when(userRepository.save(USER1)).thenReturn(USER1);
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
    @DisplayName("Search for users in birth dates range from 02/03/1986 to 10/03/1995")
    void searchByBirthDates_DatesRangeFrom1986To1995_ShouldReturnListOfUsersInDatesRange() {
        Mockito.when(userMapper.toDto(USER1)).thenReturn(RESPONSE_DTO1);
        Mockito.when(userMapper.toDto(USER3)).thenReturn(RESPONSE_DTO3);
        Mockito.when(userMapper.toDto(USER4)).thenReturn(RESPONSE_DTO4);

        String from = "02/03/1986";
        String to = "10/03/1995";
        Mockito.when(userRepository.findByBirthDateBetween(
                LocalDate.parse(from, FORMATTER),
                LocalDate.parse(to, FORMATTER))
        ).thenReturn(List.of(USER1, USER3, USER4));
        List<UserResponseDto> expected =
                List.of(RESPONSE_DTO1, RESPONSE_DTO3, RESPONSE_DTO4);
        List<UserResponseDto> actual = userService.searchByBirthDates(from, to).getData();
        Assertions.assertEquals(expected.size(), actual.size());
        Assertions.assertTrue(actual.containsAll(expected));
    }

    @Test
    @DisplayName("Search for users in birth dates range from 15/04/1985 to 10/03/2000")
    void searchByBirthDates_DatesRangeFrom1985To2000_ShouldReturnListOfUsersInDatesRange() {
        Mockito.when(userMapper.toDto(USER1)).thenReturn(RESPONSE_DTO1);
        Mockito.when(userMapper.toDto(USER2)).thenReturn(RESPONSE_DTO2);
        Mockito.when(userMapper.toDto(USER3)).thenReturn(RESPONSE_DTO3);
        Mockito.when(userMapper.toDto(USER4)).thenReturn(RESPONSE_DTO4);
        Mockito.when(userMapper.toDto(USER5)).thenReturn(RESPONSE_DTO5);

        String from = "15/04/1985";
        String to = "10/03/2000";
        Mockito.when(userRepository.findByBirthDateBetween(
                LocalDate.parse(from, FORMATTER),
                LocalDate.parse(to, FORMATTER))
        ).thenReturn(List.of(USER1, USER2, USER3, USER4, USER5));
        List<UserResponseDto> expected = List.of(
                RESPONSE_DTO1,
                RESPONSE_DTO2,
                RESPONSE_DTO3,
                RESPONSE_DTO4,
                RESPONSE_DTO5
        );
        List<UserResponseDto> actual = userService.searchByBirthDates(from, to).getData();
        Assertions.assertEquals(expected.size(), actual.size());
        Assertions.assertTrue(actual.containsAll(expected));
    }

    @Test
    @DisplayName("Search for users in birth dates range from 15/04/1990 to 10/03/2003")
    void searchByBirthDates_DatesRangeFrom1990To2003_ShouldReturnListOfUsersInDatesRange() {
        Mockito.when(userMapper.toDto(USER5)).thenReturn(RESPONSE_DTO5);

        String from = "15/04/1990";
        String to = "10/03/2003";
        Mockito.when(userRepository.findByBirthDateBetween(
                LocalDate.parse(from, FORMATTER),
                LocalDate.parse(to, FORMATTER))
        ).thenReturn(List.of(USER5));
        List<UserResponseDto> expected = List.of(RESPONSE_DTO5);
        List<UserResponseDto> actual = userService.searchByBirthDates(from, to).getData();
        Assertions.assertEquals(expected.size(), actual.size());
        Assertions.assertTrue(actual.containsAll(expected));
    }
}
