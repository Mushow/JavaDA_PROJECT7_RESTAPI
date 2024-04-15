package com.nnk.springboot.unit;

import com.nnk.springboot.domain.CustomUserDetails;
import com.nnk.springboot.domain.User;
import com.nnk.springboot.services.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

@SpringBootTest
@AutoConfigureMockMvc(addFilters=false)
@ActiveProfiles("test")
public class UserTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @BeforeEach
    public void setupAuth() {
        CustomUserDetails userDetails = new CustomUserDetails(new User(1, "user1", "password", "USER", "Test User"));
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, null, AuthorityUtils.createAuthorityList("ROLE_USER"));
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    public void userListTest() throws Exception {
        User user1 = new User();
        user1.setUsername("User1");
        user1.setRole("USER");
        user1.setPassword("123456789");
        user1.setFullname("User 1");

        User user2 = new User();
        user2.setUsername("Admin1");
        user2.setRole("ADMIN");
        user2.setPassword("123456789");
        user2.setFullname("Admin 1");

        List<User> users =  new ArrayList<>(Arrays.asList(user1, user2));

        Mockito.when(userService.findAll()).thenReturn(users);

        MvcResult resultActions = mockMvc.perform(get("/user/list")).andExpectAll(
                status().isOk()).andReturn();

        String content = resultActions.getResponse().getContentAsString();

        Assertions.assertTrue(content.contains("User1"));
        Assertions.assertTrue(content.contains("Admin1"));

        Mockito.verify(userService, Mockito.times(1)).findAll();
    }

    @Test
    public void userAddTest() throws Exception {
        mockMvc.perform(get("/user/add")).andExpectAll(
                status().isOk());
    }

    @Test
    public void userValidateErrorTest() throws Exception {
        User user = new User();

        Mockito.when(userService.save(any())).thenReturn(null);

        MvcResult resultActions = mockMvc.perform(post("/user/validate").flashAttr("user", user))
                .andExpect(status().isOk()).andReturn();

        String content = resultActions.getResponse().getContentAsString();

        Assertions.assertTrue(content.contains("Username is mandatory"));
        Assertions.assertTrue(content.contains("Password is mandatory"));
        Assertions.assertTrue(content.contains("Full name is mandatory"));
        Assertions.assertTrue(content.contains("Role is mandatory"));

        Mockito.verify(userService, Mockito.times(0)).save(any());
    }

    @Test
    public void userValidateTest() throws Exception {
        User user = new User();
        user.setUsername("User1");
        user.setRole("USER");
        user.setPassword("TestTest2*");
        user.setFullname("User 1");

        Mockito.when(userService.save(any())).thenReturn(null);

        mockMvc.perform(post("/user/validate").flashAttr("user", user))
                .andExpect(redirectedUrl("/user/list"));

        Mockito.verify(userService, Mockito.times(1)).save(any());
    }

    @Test
    public void userUpdateFindUserErrorTest() throws Exception {
        Mockito.when(userService.findById(anyInt())).thenReturn(Optional.empty());

        mockMvc.perform(get("/user/update/{id}", 1))
                .andExpect(redirectedUrl("/error"));

        Mockito.verify(userService, Mockito.times(1)).findById(anyInt());
    }

    @Test
    public void userUpdateTest() throws Exception {
        User user = new User();
        user.setId(1);
        user.setUsername("User1");
        user.setRole("USER");
        user.setPassword("123456789");
        user.setFullname("User 1");

        Mockito.when(userService.findById(anyInt())).thenReturn(Optional.of(user));

        mockMvc.perform(get("/user/update/{id}", 1))
                .andExpectAll(status().isOk());

        Mockito.verify(userService, Mockito.times(1)).findById(anyInt());
    }

    @Test
    public void userUpdateValidatorErrorTest() throws Exception {
        User user = new User();
        user.setId(1);

        Mockito.when(userService.save(any())).thenReturn(null);

        MvcResult resultActions = mockMvc.perform(post("/user/update/{id}", 1).flashAttr("user", user))
                .andExpect(status().isOk()).andReturn();

        String content = resultActions.getResponse().getContentAsString();

        Assertions.assertTrue(content.contains("Username is mandatory"));
        Assertions.assertTrue(content.contains("Password is mandatory"));
        Assertions.assertTrue(content.contains("Full name is mandatory"));

        Mockito.verify(userService, Mockito.times(0)).save(any());
    }

    @Test
    public void userUpdateValidatorTest() throws Exception {
        User user = new User();
        user.setId(1);
        user.setUsername("User");
        user.setRole("USER");
        user.setPassword("Mamamiaaa12!!!!!");
        user.setFullname("User");

        Mockito.when(userService.save(any())).thenReturn(null);

        mockMvc.perform(post("/user/update/{id}", 1).flashAttr("user", user))
                .andExpect(redirectedUrl("/user/list"));

        Mockito.verify(userService, Mockito.times(1)).save(any());
    }

    @Test
    public void userDeleteErrorTest() throws Exception {
        Mockito.when(userService.findById(anyInt())).thenReturn(Optional.empty());

        mockMvc.perform(get("/user/delete/{id}", 50)).andExpect(redirectedUrl("/error"));

        Mockito.verify(userService, Mockito.times(1)).findById(anyInt());
    }

    @Test
    public void userDeleteTest() throws Exception {
        User user = new User();
        user.setId(1);
        user.setUsername("User1");
        user.setRole("USER");
        user.setPassword("123456789");
        user.setFullname("User 1");

        Mockito.when(userService.findById(anyInt())).thenReturn(Optional.of(user));
        Mockito.doNothing().when(userService).deleteById(anyInt());

        mockMvc.perform(get("/user/delete/{id}", 1)).andExpect(redirectedUrl("/user/list"));

        Mockito.verify(userService, Mockito.times(1)).deleteById(anyInt());
    }

}