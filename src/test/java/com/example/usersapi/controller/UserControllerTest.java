package com.example.usersapi.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.usersapi.dto.CreateUserRequestDto;
import com.example.usersapi.dto.UserPatchRequestDto;
import com.example.usersapi.dto.UserResponseDto;
import com.example.usersapi.dto.UserResponseDtoWrapper;
import com.example.usersapi.model.User;
import com.example.usersapi.service.impl.UserServiceImpl;
import com.example.usersapi.util.TestUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
class UserControllerTest {
    private CreateUserRequestDto requestDto;
    private UserResponseDto responseDto;
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private UserServiceImpl userService;

    @BeforeEach
    void init() {
        requestDto = new CreateUserRequestDto(
                "user@gmail.com",
                "Bob",
                "Jackson",
                "01/02/1990",
                "414 Union Ave, Brooklyn, NY 11211",
                "(111) 111-1111"
        );
        User user = TestUtil.getUserFromCreateUserDto(1L, requestDto);
        responseDto = TestUtil.getResponseDtoFromUser(user);
    }

    @Test
    @DisplayName("Add a new user")
    void addUser_ValidRequest_ShouldAddUser() throws Exception {
        Mockito.when(userService.createUser(requestDto))
                .thenReturn(new UserResponseDtoWrapper<>(responseDto));
        String jasonObject = objectMapper.writeValueAsString(requestDto);
        mvc.perform(post("/api/users")
                        .content(jasonObject)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.email").value(requestDto.email()))
                .andExpect(jsonPath("$.data.address").value(requestDto.address()))
                .andExpect(jsonPath("$.data.lastName").value(requestDto.lastName()))
                .andExpect(jsonPath("$.data.birthDate").value(requestDto.birthDate()))
                .andExpect(jsonPath("$.data.firstName").value(requestDto.firstName()))
                .andExpect(jsonPath("$.data.phoneNumber").value(requestDto.phoneNumber()));
    }

    @Test
    @DisplayName("Patch user")
    void patchUser_ValidRequest_ShouldPatchUserInfo() throws Exception {
        UserPatchRequestDto patchRequestDto =
                new UserPatchRequestDto(null, "Adam", null, null, null, "(111) 111-2222");
        responseDto.setFirstName(patchRequestDto.firstName());
        responseDto.setPhoneNumber(patchRequestDto.phoneNumber());
        String jasonObject = objectMapper.writeValueAsString(patchRequestDto);
        Long userId = 1L;
        Mockito.when(userService.patchUser(userId, patchRequestDto))
                .thenReturn(new UserResponseDtoWrapper<>(responseDto));

        mvc.perform(patch("/api/users/" + userId)
                        .content(jasonObject)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.firstName").value(patchRequestDto.firstName()))
                .andExpect(jsonPath("$.data.phoneNumber").value(patchRequestDto.phoneNumber()));
    }

    @Test
    @DisplayName("Update user")
    void updateUser_ValidRequest_ShouldUpdateUserInfo() throws Exception {
        Long userId = 1L;
        Mockito.when(userService.updateUser(userId, requestDto))
                .thenReturn(new UserResponseDtoWrapper<>(responseDto));
        String jasonObject = objectMapper.writeValueAsString(requestDto);
        mvc.perform(put("/api/users/" + userId)
                        .content(jasonObject)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value(requestDto.email()))
                .andExpect(jsonPath("$.data.address").value(requestDto.address()))
                .andExpect(jsonPath("$.data.lastName").value(requestDto.lastName()))
                .andExpect(jsonPath("$.data.birthDate").value(requestDto.birthDate()))
                .andExpect(jsonPath("$.data.firstName").value(requestDto.firstName()))
                .andExpect(jsonPath("$.data.phoneNumber").value(requestDto.phoneNumber()));
    }

    @Test
    @DisplayName("Delete user")
    void deleteUserById_ValidRequest_ShouldDeleteUser() throws Exception {
        Long userId = 1L;
        mvc.perform(delete("/api/users/" + userId))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Search for users in birth dates range")
    void searchByBirthDates_ValidRequest_ShouldReturnListOfUsersInDatesRange() throws Exception {
        User user2 = new User();
        user2.setId(2L);
        user2.setFirstName("Kassandra");
        user2.setLastName("Hernandez");
        user2.setEmail("Kassandra@example.com");
        user2.setBirthDate(LocalDate.of(1985, 5, 15));

        User user3 = new User();
        user3.setId(3L);
        user3.setFirstName("William");
        user3.setLastName("Williamson");
        user3.setEmail("William@example.com");
        user3.setBirthDate(LocalDate.of(1988, 9, 20));

        User user4 = new User();
        user4.setId(4L);
        user4.setFirstName("Kevin");
        user4.setLastName("Thompson");
        user4.setEmail("Kevin@example.com");
        user4.setBirthDate(LocalDate.of(1995, 3, 10));

        User user5 = new User();
        user5.setId(5L);
        user5.setFirstName("Matt");
        user5.setLastName("Wallace");
        user5.setEmail("Matt@example.com");
        user5.setBirthDate(LocalDate.of(1992, 11, 28));

        UserResponseDto responseDto3 = TestUtil.getResponseDtoFromUser(user3);
        UserResponseDto responseDto4 = TestUtil.getResponseDtoFromUser(user4);

        String from = "02/03/1986";
        String to = "10/03/1995";
        Mockito.when(userService.searchByBirthDates(from, to)).thenReturn(
                new UserResponseDtoWrapper<>(List.of(responseDto, responseDto3, responseDto4)));
        String getUrl = "/api/users/search?fromDate=%s&toDate=%s";
        mvc.perform(get(String.format(getUrl, from, to)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.size()").value(3))
                .andExpect(jsonPath("$.data[0].firstName").value(responseDto.getFirstName()))
                .andExpect(jsonPath("$.data[0].birthDate").value(responseDto.getBirthDate()))
                .andExpect(jsonPath("$.data[0].email").value(responseDto.getEmail()))
                .andExpect(jsonPath("$.data[1].firstName").value(responseDto3.getFirstName()))
                .andExpect(jsonPath("$.data[1].birthDate").value(responseDto3.getBirthDate()))
                .andExpect(jsonPath("$.data[1].email").value(responseDto3.getEmail()))
                .andExpect(jsonPath("$.data[2].firstName").value(responseDto4.getFirstName()))
                .andExpect(jsonPath("$.data[2].birthDate").value(responseDto4.getBirthDate()))
                .andExpect(jsonPath("$.data[2].email").value(responseDto4.getEmail()));

        UserResponseDto responseDto2 = TestUtil.getResponseDtoFromUser(user2);
        UserResponseDto responseDto5 = TestUtil.getResponseDtoFromUser(user5);

        from = "15/04/1985";
        to = "10/03/2000";
        Mockito.when(userService.searchByBirthDates(from, to))
                .thenReturn(new UserResponseDtoWrapper<>(List.of(
                        responseDto, responseDto2, responseDto3, responseDto4, responseDto5
                )));
        mvc.perform(get(String.format(getUrl, from, to)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.size()").value(5))
                .andExpect(jsonPath("$.data[0].firstName").value(responseDto.getFirstName()))
                .andExpect(jsonPath("$.data[0].birthDate").value(responseDto.getBirthDate()))
                .andExpect(jsonPath("$.data[0].email").value(responseDto.getEmail()))
                .andExpect(jsonPath("$.data[1].firstName").value(responseDto2.getFirstName()))
                .andExpect(jsonPath("$.data[1].birthDate").value(responseDto2.getBirthDate()))
                .andExpect(jsonPath("$.data[1].email").value(responseDto2.getEmail()))
                .andExpect(jsonPath("$.data[2].firstName").value(responseDto3.getFirstName()))
                .andExpect(jsonPath("$.data[2].birthDate").value(responseDto3.getBirthDate()))
                .andExpect(jsonPath("$.data[2].email").value(responseDto3.getEmail()))
                .andExpect(jsonPath("$.data[3].firstName").value(responseDto4.getFirstName()))
                .andExpect(jsonPath("$.data[3].birthDate").value(responseDto4.getBirthDate()))
                .andExpect(jsonPath("$.data[3].email").value(responseDto4.getEmail()))
                .andExpect(jsonPath("$.data[4].firstName").value(responseDto5.getFirstName()))
                .andExpect(jsonPath("$.data[4].birthDate").value(responseDto5.getBirthDate()))
                .andExpect(jsonPath("$.data[4].email").value(responseDto5.getEmail()));

        from = "15/04/1990";
        to = "10/03/2003";
        Mockito.when(userService.searchByBirthDates(from, to))
                .thenReturn(new UserResponseDtoWrapper<>(List.of(responseDto5)));
        mvc.perform(get(String.format(getUrl, from, to)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.size()").value(1))
                .andExpect(jsonPath("$.data[0].firstName").value(responseDto5.getFirstName()))
                .andExpect(jsonPath("$.data[0].birthDate").value(responseDto5.getBirthDate()))
                .andExpect(jsonPath("$.data[0].email").value(responseDto5.getEmail()));
    }
}
