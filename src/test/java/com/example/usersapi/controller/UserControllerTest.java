package com.example.usersapi.controller;

import static com.example.usersapi.util.TestUtil.REQUEST_DTO;
import static com.example.usersapi.util.TestUtil.RESPONSE_DTO1;
import static com.example.usersapi.util.TestUtil.RESPONSE_DTO2;
import static com.example.usersapi.util.TestUtil.RESPONSE_DTO3;
import static com.example.usersapi.util.TestUtil.RESPONSE_DTO4;
import static com.example.usersapi.util.TestUtil.RESPONSE_DTO5;
import static com.example.usersapi.util.TestUtil.USER1;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.usersapi.dto.UserPatchRequestDto;
import com.example.usersapi.dto.UserResponseDto;
import com.example.usersapi.dto.UserResponseDtoWrapper;
import com.example.usersapi.service.impl.UserServiceImpl;
import com.example.usersapi.util.TestUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
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
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private UserServiceImpl userService;

    @Test
    @DisplayName("Add a new user")
    void addUser_ValidRequest_ShouldAddUser() throws Exception {
        Mockito.when(userService.createUser(REQUEST_DTO))
                .thenReturn(new UserResponseDtoWrapper<>(RESPONSE_DTO1));
        String jasonObject = objectMapper.writeValueAsString(REQUEST_DTO);
        mvc.perform(post("/api/users")
                        .content(jasonObject)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.email").value(REQUEST_DTO.email()))
                .andExpect(jsonPath("$.data.address").value(REQUEST_DTO.address()))
                .andExpect(jsonPath("$.data.lastName").value(REQUEST_DTO.lastName()))
                .andExpect(jsonPath("$.data.birthDate").value(REQUEST_DTO.birthDate()))
                .andExpect(jsonPath("$.data.firstName").value(REQUEST_DTO.firstName()))
                .andExpect(jsonPath("$.data.phoneNumber").value(REQUEST_DTO.phoneNumber()));
    }

    @Test
    @DisplayName("Patch user")
    void patchUser_ValidRequest_ShouldPatchUserInfo() throws Exception {
        UserPatchRequestDto patchRequestDto =
                new UserPatchRequestDto(null, "Adam", null, null, null, "(111) 111-2222");
        UserResponseDto responseDto = TestUtil.getResponseDtoFromUser(USER1);
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
        Mockito.when(userService.updateUser(userId, REQUEST_DTO))
                .thenReturn(new UserResponseDtoWrapper<>(RESPONSE_DTO1));
        String jasonObject = objectMapper.writeValueAsString(REQUEST_DTO);
        mvc.perform(put("/api/users/" + userId)
                        .content(jasonObject)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value(REQUEST_DTO.email()))
                .andExpect(jsonPath("$.data.address").value(REQUEST_DTO.address()))
                .andExpect(jsonPath("$.data.lastName").value(REQUEST_DTO.lastName()))
                .andExpect(jsonPath("$.data.birthDate").value(REQUEST_DTO.birthDate()))
                .andExpect(jsonPath("$.data.firstName").value(REQUEST_DTO.firstName()))
                .andExpect(jsonPath("$.data.phoneNumber").value(REQUEST_DTO.phoneNumber()));
    }

    @Test
    @DisplayName("Delete user")
    void deleteUserById_ValidRequest_ShouldDeleteUser() throws Exception {
        Long userId = 1L;
        mvc.perform(delete("/api/users/" + userId))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Search for users in birth dates range from 02/03/1986 to 10/03/1995")
    void searchByBirthDates_DatesRangeFrom1986To1995_ShouldReturnListOfUsersInDatesRange()
            throws Exception {
        String from = "02/03/1986";
        String to = "10/03/1995";
        Mockito.when(userService.searchByBirthDates(from, to)).thenReturn(
                new UserResponseDtoWrapper<>(List.of(RESPONSE_DTO1, RESPONSE_DTO3, RESPONSE_DTO4)));
        String getUrl = "/api/users/search?fromDate=%s&toDate=%s";
        mvc.perform(get(String.format(getUrl, from, to)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.size()").value(3))
                .andExpect(jsonPath("$.data[0].firstName").value(RESPONSE_DTO1.getFirstName()))
                .andExpect(jsonPath("$.data[0].birthDate").value(RESPONSE_DTO1.getBirthDate()))
                .andExpect(jsonPath("$.data[0].email").value(RESPONSE_DTO1.getEmail()))
                .andExpect(jsonPath("$.data[1].firstName").value(RESPONSE_DTO3.getFirstName()))
                .andExpect(jsonPath("$.data[1].birthDate").value(RESPONSE_DTO3.getBirthDate()))
                .andExpect(jsonPath("$.data[1].email").value(RESPONSE_DTO3.getEmail()))
                .andExpect(jsonPath("$.data[2].firstName").value(RESPONSE_DTO4.getFirstName()))
                .andExpect(jsonPath("$.data[2].birthDate").value(RESPONSE_DTO4.getBirthDate()))
                .andExpect(jsonPath("$.data[2].email").value(RESPONSE_DTO4.getEmail()));
    }

    @Test
    @DisplayName("Search for users in birth dates range from 15/04/1985 to 10/03/2000")
    void searchByBirthDates_DatesRangeFrom1985To2000_ShouldReturnListOfUsersInDatesRange()
            throws Exception {
        String from = "15/04/1985";
        String to = "10/03/2000";

        Mockito.when(userService.searchByBirthDates(from, to))
                .thenReturn(new UserResponseDtoWrapper<>(List.of(
                        RESPONSE_DTO1,
                        RESPONSE_DTO2,
                        RESPONSE_DTO3,
                        RESPONSE_DTO4,
                        RESPONSE_DTO5
                )));
        String getUrl = "/api/users/search?fromDate=%s&toDate=%s";
        mvc.perform(get(String.format(getUrl, from, to)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.size()").value(5))
                .andExpect(jsonPath("$.data[0].firstName").value(RESPONSE_DTO1.getFirstName()))
                .andExpect(jsonPath("$.data[0].birthDate").value(RESPONSE_DTO1.getBirthDate()))
                .andExpect(jsonPath("$.data[0].email").value(RESPONSE_DTO1.getEmail()))
                .andExpect(jsonPath("$.data[1].firstName").value(RESPONSE_DTO2.getFirstName()))
                .andExpect(jsonPath("$.data[1].birthDate").value(RESPONSE_DTO2.getBirthDate()))
                .andExpect(jsonPath("$.data[1].email").value(RESPONSE_DTO2.getEmail()))
                .andExpect(jsonPath("$.data[2].firstName").value(RESPONSE_DTO3.getFirstName()))
                .andExpect(jsonPath("$.data[2].birthDate").value(RESPONSE_DTO3.getBirthDate()))
                .andExpect(jsonPath("$.data[2].email").value(RESPONSE_DTO3.getEmail()))
                .andExpect(jsonPath("$.data[3].firstName").value(RESPONSE_DTO4.getFirstName()))
                .andExpect(jsonPath("$.data[3].birthDate").value(RESPONSE_DTO4.getBirthDate()))
                .andExpect(jsonPath("$.data[3].email").value(RESPONSE_DTO4.getEmail()))
                .andExpect(jsonPath("$.data[4].firstName").value(RESPONSE_DTO5.getFirstName()))
                .andExpect(jsonPath("$.data[4].birthDate").value(RESPONSE_DTO5.getBirthDate()))
                .andExpect(jsonPath("$.data[4].email").value(RESPONSE_DTO5.getEmail()));
    }

    @Test
    @DisplayName("Search for users in birth dates range from 15/04/1990 to 10/03/2003")
    void searchByBirthDates_DatesRangeFrom1990To2003_ShouldReturnListOfUsersInDatesRange()
            throws Exception {
        String from = "15/04/1990";
        String to = "10/03/2003";
        Mockito.when(userService.searchByBirthDates(from, to))
                .thenReturn(new UserResponseDtoWrapper<>(List.of(RESPONSE_DTO5)));
        String getUrl = "/api/users/search?fromDate=%s&toDate=%s";
        mvc.perform(get(String.format(getUrl, from, to)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.size()").value(1))
                .andExpect(jsonPath("$.data[0].firstName").value(RESPONSE_DTO5.getFirstName()))
                .andExpect(jsonPath("$.data[0].birthDate").value(RESPONSE_DTO5.getBirthDate()))
                .andExpect(jsonPath("$.data[0].email").value(RESPONSE_DTO5.getEmail()));
    }
}
