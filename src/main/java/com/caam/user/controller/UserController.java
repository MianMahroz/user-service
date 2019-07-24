package com.caam.user.controller;

import static com.caam.commons.constants.CaamResponseCodes.BAD_REQUEST_CODE;
import static com.caam.commons.constants.CaamResponseCodes.LOGIN_STRING_REQUIRED_CODE;
import static com.caam.commons.constants.CaamResponseCodes.PASSWORD_REQUIRED_CODE;
import static com.caam.commons.constants.CaamResponseCodes.RECORD_ALREADY_EXIST_CODE;
import static com.caam.commons.constants.CaamResponseCodes.RECORD_NOT_FOUND_CODE;
import static com.caam.commons.constants.CaamResponseCodes.RECORD_UPDATED_CODE;
import static com.caam.commons.constants.CaamResponseCodes.SUCCESS_CODE;
import static com.caam.commons.constants.CaamResponseCodes.RECORD_CREATED_CODE;
import static com.caam.commons.utils.UtilsCommons.getEncodedPassword;
import static com.caam.commons.utils.UtilsCommons.getMapper;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import com.caam.commons.response.utills.ResponseUtill;
import com.caam.user.domain.CaamUser;
import com.caam.user.dto.ActivationDto;
import com.caam.user.dto.LoginDto;
import com.caam.user.dto.UserDetailDto;
import com.caam.user.dto.UserDetailsRequestDto;
import com.caam.user.dto.UserDto;
import com.caam.user.dto.UserInfoDto;
import com.caam.user.exception.UserAlreadyExistException;
import com.caam.user.exception.UserAlreadyVerifiedException;
import com.caam.user.exception.UserBadRequestException;
import com.caam.user.exception.UserNotFoundException;
import com.caam.user.service.UserService;
import com.caam.user.vo.ActivationVO;
import com.caam.user.vo.LoginVO;
import com.caam.user.vo.ResetPasswordVO;
import com.caam.user.vo.UserUpdateVO;
import com.caam.user.vo.UserVO;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping("/api/")
@CrossOrigin(origins = "*")
public class UserController {

	private static final Logger logger = LogManager.getLogger(UserController.class);

	@Autowired
	private UserService userService;

	private ResponseUtill responseUtill = new ResponseUtill();

	@ApiOperation(value = "saveUser", response = ResponseEntity.class)
	@ApiResponses(value = { @ApiResponse(code = 2, message = "User Successfully Created"),
			@ApiResponse(code = 4, message = "Failure Code Internal Server Error!"),
			@ApiResponse(code = 18, message = "Bad Request Code (Some field is missing or value is not correct)"),
			@ApiResponse(code = 8, message = "Record Already Exist") })
	@PostMapping("user")
	public ResponseEntity<?> saveUser(@RequestBody UserVO userVO)
			throws UserBadRequestException, UserAlreadyExistException {
		logger.info("saveUser called....");
		if (userVO.getLoginString() == null || userVO.getLoginString().isEmpty()
				|| userVO.getLoginString().length() <= 0) {
			throw new UserBadRequestException(BAD_REQUEST_CODE);
		}
		UserDto userDto = this.userService.getCaamUserByLoginString(userVO.getLoginString());
		if (userDto != null) {
			if (userDto.isVerified()) {
				if(userDto.getRegisterFrom()!=null && userDto.getRegisterFrom().equalsIgnoreCase("facebook")) {

				UserInfoDto userInfoDto = getMapper().map(userDto, UserInfoDto.class);
				return responseUtill.getApiResponse(RECORD_CREATED_CODE, null, userInfoDto);
				}else {
				throw new UserAlreadyExistException(RECORD_ALREADY_EXIST_CODE);
			}
			}
			else {
				this.userService.deleteByLoginString(userVO.getLoginString());
			}
		}
		userDto = userService.saveUser(getMapper().map(userVO, UserDto.class));
		UserInfoDto userInfoDto = getMapper().map(userDto, UserInfoDto.class);

		try {
			String encodedString = Base64.getEncoder().encodeToString(userDto.getUserImage());
			userInfoDto.setUserImage("data:image/jpeg;base64," + encodedString);
		} catch (NullPointerException e) {
		}

		logger.info("saveUser finished.");
		/*
		 * String accountKitUrl = "https://www.accountkit.com/v1.0/basic/dialog/"; if
		 * (userDto.getLoginString().contains("@")) { accountKitUrl +=
		 * "email_login/?email=" + userDto.getLoginString(); } else { accountKitUrl +=
		 * "sms_login/?phone_number=" + userDto.getLoginString().substring(2) +
		 * "&country_code=" + userDto.getLoginString().substring(0, 2); } accountKitUrl
		 * +=
		 * "&app_id=302011317368908&redirect=https://www.caam.pk:8443/user-service/api/verify/&state="
		 * + userDto.getLoginString() + "&fbAppEventsEnabled=true&debug=true&"; //return
		 * new RedirectView(accountKitUrl);
		 */ return responseUtill.getApiResponse(RECORD_CREATED_CODE, null, userInfoDto);
	}

	@ApiOperation(value = "saveUserMobile", response = ResponseEntity.class)
	@ApiResponses(value = { @ApiResponse(code = 2, message = "User Successfully Created"),
			@ApiResponse(code = 4, message = "Failure Code Internal Server Error!"),
			@ApiResponse(code = 18, message = "Bad Request Code (Some field is missing or value is not correct)"),
			@ApiResponse(code = 8, message = "Record Already Exist") })
	@PostMapping("userMobile")
	public ResponseEntity<?> saveUserMobile(@RequestBody UserVO userVO)
			throws UserBadRequestException, UserAlreadyExistException {
		logger.info("saveUser called....");
		if (userVO.getLoginString() == null || userVO.getLoginString().isEmpty()
				|| userVO.getLoginString().length() <= 0) {
			throw new UserBadRequestException(BAD_REQUEST_CODE);
		}
		UserDto userDto = this.userService.getCaamUserByLoginString(userVO.getLoginString());
		if (userDto != null) {
			if (userDto.isVerified())
				throw new UserAlreadyExistException(RECORD_ALREADY_EXIST_CODE);
			else {
				this.userService.deleteByLoginString(userVO.getLoginString());
			}
		}
		userDto = userService.saveUser(getMapper().map(userVO, UserDto.class));
		UserInfoDto userInfoDto = getMapper().map(userDto, UserInfoDto.class);

		try {
			String encodedString = Base64.getEncoder().encodeToString(userDto.getUserImage());
			userInfoDto.setUserImage("data:image/jpeg;base64," + encodedString);
		} catch (NullPointerException e) {
		}

		logger.info("saveUser finished.");

		return responseUtill.getApiResponse(RECORD_CREATED_CODE, null, userInfoDto);
	}

	@ApiOperation(value = "SignIn", response = ResponseEntity.class)
	@ApiResponses(value = { @ApiResponse(code = 2, message = "User Login Successfully"),
			@ApiResponse(code = 4, message = "Failure Code Internal Server Error!"),
			@ApiResponse(code = 18, message = "Bad Request Code (Some field is missing or value is not correct)"),
			@ApiResponse(code = 14, message = "Login String Required"),
			@ApiResponse(code = 12, message = "Password Required"), @ApiResponse(code = 10, message = "User Not Found"),
			@ApiResponse(code = 24, message = "Invalid LoginString"),
			@ApiResponse(code = 22, message = "Invalid Password"), })
	@PostMapping("signin")
	public ResponseEntity<?> signin(@RequestBody LoginVO loginVO)
			throws UserNotFoundException, UserBadRequestException {
		logger.info("signin called.....");
		if (loginVO.getLoginString() == null || loginVO.getLoginString().isEmpty()
				|| loginVO.getLoginString().length() <= 0) {
			throw new UserBadRequestException(LOGIN_STRING_REQUIRED_CODE);
		}
		if (loginVO.getPassword() == null || loginVO.getPassword().isEmpty()) {
			throw new UserBadRequestException(PASSWORD_REQUIRED_CODE);
		}
		UserDto userDTO = userService.login(getMapper().map(loginVO, LoginDto.class));

		UserInfoDto userInfoDto = getMapper().map(userDTO, UserInfoDto.class);

		try {
			String encodedString = Base64.getEncoder().encodeToString(userDTO.getUserImage());
			userInfoDto.setUserImage("data:image/jpeg;base64," + encodedString);
		} catch (NullPointerException e) {
		}

		logger.info("signin finished...");
		return responseUtill.getApiResponse(SUCCESS_CODE, null, userInfoDto);
	}

	@ApiOperation(value = "Verify", response = ResponseEntity.class)
	@ApiResponses(value = { @ApiResponse(code = 2, message = "Success Code"),
			@ApiResponse(code = 4, message = "Failure Code Internal Server Error!"),
			@ApiResponse(code = 18, message = "Bad Request Code (Some field is missing or value is not correct)"),
			@ApiResponse(code = 16, message = "User Already Verified"),
			@ApiResponse(code = 10, message = "User Not Found") })
	@GetMapping("verify") //// verify user by phone number or email links
	public RedirectView activateCaamUser(@RequestParam String status, @RequestParam String state)
			throws UserNotFoundException, UserAlreadyVerifiedException {
		logger.info("activateCaamUser called..........");
		String redirect = "";
		String[] statArr = state.split(",");
		if (status.equals("PARTIALLY_AUTHENTICATED")) {
			if(statArr.length<2) {
				if(userService.getCaamUserByLoginString(statArr[0]) ==null) {
					userService.verifyUser(statArr[0]);
				}		
				redirect = "https://caam.pk/enterCity";
			}else {
				redirect = "https://caam.pk/forgotPasswordForm/"+statArr[0];
			}
		} else {
			redirect = "https://caam.pk/";
		}
		logger.info("activateCaamUser finished.......");
		return new RedirectView(redirect);
	}

	@ApiOperation(value = "verifyMobile", response = ResponseEntity.class)
	@ApiResponses(value = { @ApiResponse(code = 2, message = "Success Code"),
			@ApiResponse(code = 4, message = "Failure Code Internal Server Error!"),
			@ApiResponse(code = 18, message = "Bad Request Code (Some field is missing or value is not correct)"),
			@ApiResponse(code = 16, message = "User Already Verified"),
			@ApiResponse(code = 10, message = "User Not Found") })
	@GetMapping("verifyMobile") //// verify user by phone number or email links
	public ResponseEntity<?> activateCaamMobileUser(@RequestParam String loginString)
			throws UserNotFoundException, UserAlreadyVerifiedException {
		logger.info("activateCaamUser called..........");
		userService.verifyUser(loginString);
		logger.info("activateCaamUser finished.......");
		return responseUtill.getApiResponse(SUCCESS_CODE, null, null);
	}
	/*
	 * @PostMapping("verify") //// verify user by phone number or email links public
	 * ResponseEntity<?> activateCaamUser(@RequestBody ActivationVO activationVO)
	 * throws UserNotFoundException, UserAlreadyVerifiedException {
	 * logger.info("activateCaamUser called..........");
	 * userService.verifyUser((getMapper().map(activationVO, ActivationDto.class)));
	 * logger.info("activateCaamUser finished......."); return
	 * responseUtill.getApiResponse(SUCCESS_CODE, null, null); }
	 */

	@ApiOperation(value = "forgotPassword", response = ResponseEntity.class)
	@ApiResponses(value = { @ApiResponse(code = 2, message = "Success Code"),
			@ApiResponse(code = 4, message = "Failure Code Internal Server Error!"),
			@ApiResponse(code = 18, message = "Bad Request Code (Some field is missing or value is not correct)"),
			@ApiResponse(code = 10, message = "User Not Found") })
	@PostMapping("forgot")
	public ResponseEntity<?> forgotPassword(@RequestParam String loginString) throws UserNotFoundException {
		logger.info("forgotPassword called.......");
		boolean bool = false;
		
		if (loginString == null || loginString.isEmpty() || loginString.length() <= 0) {
			throw new UserNotFoundException(LOGIN_STRING_REQUIRED_CODE);
		}
		if (this.userService.getCaamUserByLoginString(loginString) == null) {
			throw new UserNotFoundException(RECORD_NOT_FOUND_CODE);
		}else {
			bool = true;
		}
		logger.info("forgotPassword finished.......");
		
		
		return responseUtill.getApiResponse(SUCCESS_CODE, "user exist", bool);
	}

	/**
	 * We are facing some issues when return response Entity that`s why we return
	 * DTO for feign calls .
	 */
//	@GetMapping("user")
//	public @ResponseBody UserDto getCaamUserByLoginString(@RequestBody LoginVO loginVO) {
//		return userService.getCaamUserByLoginString(loginVO.getLoginString());
//
//	}

	@ApiOperation(value = "userByName", response = UserDto.class)
	@ApiResponses(value = { @ApiResponse(code = 2, message = "Success Code"),
			@ApiResponse(code = 4, message = "Failure Code Internal Server Error!"),
			@ApiResponse(code = 18, message = "Bad Request Code (Some field is missing or value is not correct)") })
	@GetMapping("userByName/{userName}")
	public @ResponseBody UserDto getCaamUserByLoginString(@PathVariable(name = "userName") String userName) {
		return userService.getCaamUserByLoginString(userName);
	}

	/**
	 * we are using for web We are facing some issues when return response Entity
	 * that`s why we return DTO for feign calls .
	 */

	@ApiOperation(value = "user/{id}", response = ResponseEntity.class)
	@ApiResponses(value = { @ApiResponse(code = 2, message = "Success Code"),
			@ApiResponse(code = 4, message = "Failure Code Internal Server Error!"),
			@ApiResponse(code = 18, message = "Bad Request Code (Some field is missing or value is not correct)") })
	@GetMapping("user/{id}")
	public @ResponseBody ResponseEntity getUserInfoByUserId(@PathVariable("id") long id) throws UserNotFoundException {
		return responseUtill.getApiResponse(SUCCESS_CODE, "Found successfully. ", userService.getCaamUserById(id));
	}

	@ApiOperation(value = "users", response = ResponseEntity.class)
	@ApiResponses(value = { @ApiResponse(code = 2, message = "Success Code"),
			@ApiResponse(code = 4, message = "Failure Code Internal Server Error!"),
			@ApiResponse(code = 18, message = "Bad Request Code (Some field is missing or value is not correct)") })
	@PostMapping("users")
	public List<UserDetailDto> getUsersByAll(@RequestBody UserDetailsRequestDto userIds) {
		logger.info(userIds);
		return userService.getCaamUsersByAll(userIds);
	}

	@ApiOperation(value = "user/image", response = ResponseEntity.class)
	@ApiResponses(value = { @ApiResponse(code = 2, message = "Success Code"),
			@ApiResponse(code = 4, message = "Failure Code Internal Server Error!"),
			@ApiResponse(code = 18, message = "Bad Request Code (Some field is missing or value is not correct)") })
	@PutMapping("user/image")
	public ResponseEntity<?> updateUser(@RequestParam(name = "file") MultipartFile file,
			@RequestParam(name = "name") String name, @RequestParam(name = "lastName") String lastName,
			@RequestParam(name = "loginString") String loginString, @RequestParam(name = "cnic") String cnic,
			@RequestParam(name = "location") String location, @RequestParam(name = "dob") String dob,
			@RequestParam(name = "description") String description, @RequestParam(name = "userId") String userId)
			throws UserBadRequestException, UserNotFoundException, IOException {

		if (Long.valueOf(userId).longValue() <= 0) {
			throw new UserBadRequestException(BAD_REQUEST_CODE);
		}

		UserUpdateVO profileVo = new UserUpdateVO();
		profileVo.setCnic(cnic);
		profileVo.setDescription(description);
		profileVo.setDob(dob);
		profileVo.setId(Long.valueOf(userId).longValue());
		profileVo.setLastName(lastName);
		profileVo.setLocation(location);
		profileVo.setLoginString(loginString);
		profileVo.setName(name);
		profileVo.setUserImage(file.getBytes());
		UserDto userDto = userService.updateUser(getMapper().map(profileVo, UserDto.class));
		UserInfoDto infoDto = getMapper().map(userDto, UserInfoDto.class);

		String encodedString = Base64.getEncoder().encodeToString(file.getBytes());
		infoDto.setUserImage("data:image/jpeg;base64," + encodedString);

		return responseUtill.getApiResponse(RECORD_UPDATED_CODE, null, infoDto);
	}

	@ApiOperation(value = "user", response = ResponseEntity.class)
	@ApiResponses(value = { @ApiResponse(code = 26, message = "Success Code"),
			@ApiResponse(code = 4, message = "Failure Code Internal Server Error!"),
			@ApiResponse(code = 18, message = "Bad Request Code (Some field is missing or value is not correct)"),
			@ApiResponse(code = 10, message = "Record Not Found") })
	@PutMapping("user")
	public ResponseEntity<?> updateUser(@RequestBody UserInfoDto userVO)
			throws UserBadRequestException, UserNotFoundException {

		if (userVO.getId() <= 0) {
			throw new UserBadRequestException(BAD_REQUEST_CODE);
		}

		UserDto userDto = userService.updateUser(getMapper().map(userVO, UserDto.class));
		return responseUtill.getApiResponse(RECORD_UPDATED_CODE, null, getMapper().map(userDto, UserInfoDto.class));
	}

	@GetMapping("getUserByForgotPasswordToken")
	public ResponseEntity<?> getUserByForgotPasswordToken(@RequestParam String token)
			throws UserBadRequestException, UserNotFoundException {
		Optional<CaamUser> caamuser = userService.findByForgotPasswordToken(token);
		if (token.isEmpty() || !caamuser.isPresent()) {
			throw new UserBadRequestException(BAD_REQUEST_CODE);
		}

		UserDto userDto = getMapper().map(caamuser.get(), UserDto.class);
		return responseUtill.getApiResponse(SUCCESS_CODE, "Found successfully. ", userDto);
	}

	@PostMapping("ResetPassword")
	public ResponseEntity<?> resetpassword(@RequestBody ResetPasswordVO resetVO)
			throws UserBadRequestException, UserNotFoundException {

		Optional<CaamUser> caamuser = null;
		
		if (resetVO.getLoginString().contains("@")) {

			caamuser = userService.findByLoginString(
					resetVO.getLoginString());
		} else {
			caamuser = userService.findByLoginString(
					resetVO.getLoginString());
		}
		if (caamuser.isPresent()) {
			CaamUser caamUser = caamuser.get();
			caamUser.setPassword(getEncodedPassword(resetVO.getPassword()));

			UserDto userDto = getMapper().map(caamUser, UserDto.class);
			userService.updateUserPassword(userDto);
			return responseUtill.getApiResponse(SUCCESS_CODE, "Password Updated successfully. ", userDto);
		} else {
			throw new UserNotFoundException(RECORD_NOT_FOUND_CODE);
		}
	}

	@PostMapping("getUser")
	public UserInfoDto getUserById(@RequestParam long id) throws UserNotFoundException {
		return userService.getCaamUserById(id);
	}
	
	@Transactional
	@PostMapping("addCity")
	public ResponseEntity<?> addCity(@RequestParam long id,@RequestParam String city) throws UserNotFoundException {
		UserInfoDto userInfoDto = userService.getCaamUserById(id);
		userInfoDto.setLocation(city);
		UserDto userDto = getMapper().map(userInfoDto, UserDto.class);
		userService.updateUser(userDto);
		return   responseUtill.getApiResponse(SUCCESS_CODE, "City Added successfully. ", userDto);
		
		
	}

}
