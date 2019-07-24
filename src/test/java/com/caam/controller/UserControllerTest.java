package com.caam.controller;

import static com.caam.commons.utils.UtilsCommons.*;
import com.caam.user.CaamUserServiceApplication;

import com.caam.user.controller.UserController;
import com.caam.user.dto.UserDto;
import com.caam.user.service.UserService;
import com.caam.user.service.UserServiceImpl;
import com.caam.user.vo.LoginVO;
import com.caam.user.vo.UserUpdateVO;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Date;

import static com.caam.commons.constants.CaamResponseCodes.*;
import static com.caam.commons.utils.UtilsCommons.jsonString;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

//@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CaamUserServiceApplication.class)
public class UserControllerTest {
	private static String BASE_URL = "/api/";
	private MockMvc mockMvc;
	@SuppressWarnings("unused")
	private MockMvc mockStandaloneMvc;

	@InjectMocks
	private UserController userController;
	@Autowired
	private UserService userService;
	@Autowired
	private WebApplicationContext webApplicationContext;
	private static String loginString = "caamUser = " + new Date().getTime();

	@Before
	public void setup() throws Exception {
		MockitoAnnotations.initMocks(this);
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		mockStandaloneMvc = MockMvcBuilders.standaloneSetup(userController).build();
	}

	@Test
	public void saveUserAsNewTest() throws Exception {
		String str = genearteRandomNo("T");
		UserDto userDto = new UserDto();
		userDto.setLoginString(str);
		userDto.setPassword(str);
		userDto.setName(str);
		mockMvc.perform(
				post(BASE_URL + "user").contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).content(jsonString(userDto)))
				/* .andExpect(status().isOk()) */
				.andExpect(jsonPath("$.[*].code").value(hasItem(RECORD_CREATED_CODE)));
	}

	@Test
	public void saveUserAsAlreadyExistsTest() throws Exception {
		String str = genearteRandomNo("T");
		UserDto userDto = new UserDto();
		userDto.setLoginString(str);
		userDto.setPassword(str);
		userDto.setName(str);
		this.userService.saveUser(userDto);
		mockMvc.perform(
				post(BASE_URL + "user").contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).content(jsonString(userDto)))
				.andDo(print()).andExpect(jsonPath("$.[*].code").value(hasItem(RECORD_ALREADY_EXIST_CODE)));
	}

	@Test
	public void signinTest() throws Exception {
		String str = genearteRandomNo("T");
		UserDto userDto = new UserDto();
		userDto.setLoginString(str);
		userDto.setPassword(str);
		userDto.setName(str);
		this.userService.saveUser(userDto);

		UserDto userRequestDto = new UserDto();
		userRequestDto.setLoginString(str);
		userRequestDto.setName(str);
		userRequestDto.setPassword(str);
		mockMvc.perform(post(BASE_URL + "signin").contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
				.content(jsonString(userRequestDto))).andDo(print())
				.andExpect(jsonPath("$.[*].code").value(hasItem(SUCCESS_CODE)));
	}

	@Test
	public void signinLoginStringNullTest() throws Exception {
		LoginVO loginVo = new LoginVO();
//        loginVo.setLoginString("caamUser@yop.com");
		loginVo.setPassword("caamUser");
		mockMvc.perform(post(BASE_URL + "signin").contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
				.content(jsonString(loginVo))).andDo(print())
				.andExpect(jsonPath("$.[*].code").value(hasItem(LOGIN_STRING_REQUIRED_CODE)));
	}

	@Test
	public void signinPasswordNullTest() throws Exception {
		LoginVO loginVo = new LoginVO();
		loginVo.setLoginString("caamUser@yop.com");
//		loginVo.setPassword("caamUser");
		mockMvc.perform(post(BASE_URL + "signin").contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
				.content(jsonString(loginVo))).andDo(print())
				.andExpect(jsonPath("$.[*].code").value(hasItem(PASSWORD_REQUIRED_CODE)));
	}

	@Test
	public void signinInvalidLoginStringTest() throws Exception {
		String str = genearteRandomNo("T");
		UserDto userDto = new UserDto();
		userDto.setLoginString(str);
		userDto.setPassword(str);
		userDto.setName(str);
		this.userService.saveUser(userDto);

		UserDto userRequestDto = new UserDto();
		userRequestDto.setLoginString("invalid");
		userRequestDto.setName(str);
		userRequestDto.setPassword(str);
		mockMvc.perform(post(BASE_URL + "signin").contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
				.content(jsonString(userRequestDto))).andDo(print())
				.andExpect(jsonPath("$.[*].code").value(hasItem(LOGIN_STRING_INVALID_CODE)));
	}

	@Test
	public void signinInvalidPasswordTest() throws Exception {
		String str = genearteRandomNo("T");
		UserDto userDto = new UserDto();
		userDto.setLoginString(str);
		userDto.setPassword(str);
		userDto.setName(str);
		this.userService.saveUser(userDto);

		UserDto userRequestDto = new UserDto();
		userRequestDto.setLoginString(str);
		userRequestDto.setName(str);
		userRequestDto.setPassword("invalid");
		mockMvc.perform(post(BASE_URL + "signin").contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
				.content(jsonString(userRequestDto))).andDo(print())
				.andExpect(jsonPath("$.[*].code").value(hasItem(PASSWORD_INVALID_CODE)));
	}

	@Test
	public void forgotPasswordTest() throws Exception {
		String str = genearteRandomNo("T");
		UserDto userDto = new UserDto();
		userDto.setLoginString(str);
		userDto.setPassword(str);
		userDto.setName(str);
		this.userService.saveUser(userDto);

		mockMvc.perform(post(BASE_URL + "forgot/" + str).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
				.content(loginString)).andExpect(jsonPath("$.[*].code").value(hasItem(SUCCESS_CODE)));
	}

	@Test
	public void forgotPasswordUnauthorizedUserTest() throws Exception {
		String loginString = "admin@yop.com1";
		mockMvc.perform(post(BASE_URL + "forgot/" + loginString).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
				.content(loginString)).andExpect(jsonPath("$.[*].code").value(hasItem(RECORD_NOT_FOUND_CODE)));
	}

	@Test
	public void getUserInfoByUserIdRecordNotFoundTest() throws Exception {
		mockMvc.perform(get(BASE_URL + "user/-1").contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).content(""))
				.andExpect(jsonPath("$.[*].code").value(hasItem(RECORD_NOT_FOUND_CODE)));
	}

//	@Test
//	public void getUsersByAllTest() throws Exception {
//		mockMvc.perform(get(BASE_URL + "users").contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).content(""))
//				.andExpect(jsonPath("$.[*].code").value(hasItem(SUCCESS_CODE)));
//	}

	@Test
	public void updateUserTest() throws Exception {
		String str = genearteRandomNo("T");
		UserDto userDto = new UserDto();
		userDto.setLoginString(str);
		userDto.setPassword(str);
		userDto.setName(str);
		userDto = this.userService.saveUser(userDto);

		UserUpdateVO updateVO = new UserUpdateVO();
		updateVO.setId(userDto.getId());
		updateVO.setLastName(str);
		updateVO.setDescription(str);

		mockMvc.perform(
				put(BASE_URL + "user").contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).content(jsonString(updateVO)))
				.andExpect(jsonPath("$.[*].code").value(hasItem(RECORD_UPDATED_CODE)));
	}

	@Test
	public void updateUser_WHEN_UserId_ZeroOrLessThanZero_THEN_BadRequestException() throws Exception {
		String str = genearteRandomNo("T");

		UserUpdateVO updateVO = new UserUpdateVO();
		updateVO.setId(0);
		updateVO.setLastName(str);
		updateVO.setDescription(str);

		mockMvc.perform(
				put(BASE_URL + "user").contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).content(jsonString(updateVO)))
				.andExpect(jsonPath("$.[*].code").value(hasItem(BAD_REQUEST_CODE)));
	}

}