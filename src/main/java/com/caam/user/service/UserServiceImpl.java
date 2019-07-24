package com.caam.user.service;

import static com.caam.commons.constants.CaamResponseCodes.LOGIN_STRING_INVALID_CODE;
import static com.caam.commons.constants.CaamResponseCodes.PASSWORD_INVALID_CODE;
import static com.caam.commons.constants.CaamResponseCodes.RECORD_NOT_FOUND_CODE;
import static com.caam.commons.constants.CaamResponseCodes.USER_ALREADY_VERIFIED_CODE;
import static com.caam.commons.utils.UtilsCommons.generateOTP;
import static com.caam.commons.utils.UtilsCommons.generateToken;
import static com.caam.commons.utils.UtilsCommons.getEncodedPassword;
import static com.caam.commons.utils.UtilsCommons.getEncoder;
import static com.caam.commons.utils.UtilsCommons.getMapper;

import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.caam.user.domain.CaamUser;
import com.caam.user.dto.ActivationDto;
import com.caam.user.dto.LoginDto;
import com.caam.user.dto.UserDetailDto;
import com.caam.user.dto.UserDetailsRequestDto;
import com.caam.user.dto.UserDto;
import com.caam.user.dto.UserInfoDto;
import com.caam.user.exception.UserAlreadyVerifiedException;
import com.caam.user.exception.UserNotFoundException;
import com.caam.user.repo.UserRepository;

@Service
public class UserServiceImpl implements UserService {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LogManager.getLogger(UserServiceImpl.class.getName());

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private com.caam.user.client.notification.NotificationClient notificationClient;

	boolean jobStatus = false;
	UserDto UserDto = null;

	@Override
	public UserDto saveUser(UserDto userDto) {
		CaamUser newUser = getMapper().map(userDto, CaamUser.class);
		newUser.setPassword(getEncodedPassword(userDto.getPassword()));
		newUser.setActivated(true);
		if(newUser.getRegisterFrom()!=null && newUser.getRegisterFrom().equalsIgnoreCase("facebook")) {
			newUser.setVerified(true);
		}
	/*
		 * if (userDto.getLoginString().contains("@"))
		 * newUser.setActivationToken(generateToken()); else {
		 * newUser.setActivationOtp(otp);
		 * //notificationClient.sendTextMessage(userDto.getLoginString(),
		 * "Your OTP to register on caam is "+otp+".");
		 * 
		 * CompletableFuture.runAsync(() -> {
		 * notificationClient.sendTextMessage(userDto.getLoginString(),
		 * "Thank you for registering with Caam.pk. please visit http://bit.ly/2HhWBcA for detail"
		 * ); // notificationClient.sendTextMessage("923008875939",
		 * "Thank you for registering with Caam.pk. please visit http://bit.ly/2HhWBcA for detail"
		 * ); });
		 * 
		 * }
		 */
		newUser = userRepository.save(newUser);

		/*
		 * // sending email to user for activation if (newUser.getId() != 0 &&
		 * userDto.getLoginString().contains("@")) {
		 * userDto.setActivationToken(newUser.getActivationToken());
		 * CompletableFuture.runAsync(() -> { logger.info("Inside Future:");
		 * notificationClient.sendActivationEmail(userDto); }); } else if
		 * (newUser.getId() != 0 && !userDto.getLoginString().contains("@")) {
		 * logger.info("Inside Mobile Future1"); CompletableFuture.runAsync(() -> {
		 * logger.info("Inside Future:");
		 * notificationClient.sendTextMessage(userDto.getLoginString(),
		 * "your caam code to register is " + otp + "."); });
		 * 
		 * }
		 */
		return getMapper().map(newUser, UserDto.class);
	}

	@Override
	public UserDto login(LoginDto loginDto) throws UserNotFoundException {
		Optional<CaamUser> rtnUserByLoginString = userRepository.findByloginString(loginDto.getLoginString());
		if (!rtnUserByLoginString.isPresent())
			throw new UserNotFoundException(LOGIN_STRING_INVALID_CODE);
		if (getEncoder().matches(loginDto.getPassword(), rtnUserByLoginString.get().getPassword())) {
			return getMapper().map(rtnUserByLoginString.get(), UserDto.class);
		} else {
			throw new UserNotFoundException(PASSWORD_INVALID_CODE);
		}
	}

	@Override
	public boolean verifyUser(String state) throws UserNotFoundException, UserAlreadyVerifiedException {
		CaamUser verifyUser = userRepository.getByLoginString(state);
		jobStatus = false;
		if (verifyUser==null)
			throw new UserNotFoundException(RECORD_NOT_FOUND_CODE);
		if (verifyUser.getId() != 0) {
			if (verifyUser.isVerified()) {
				throw new UserAlreadyVerifiedException(USER_ALREADY_VERIFIED_CODE);
			}
			logger.info("Here inside if" + jobStatus);
			verifyUser.setVerified(true);
			userRepository.save(verifyUser);
			CompletableFuture.runAsync(() -> {
				notificationClient.sendTextMessage(verifyUser.getLoginString(),
						"Thank you for registering with CAAM. Please visit caam.pk to post or search caam.");
				// notificationClient.sendTextMessage("923008875939", "Thank you for registering
				// with Caam.pk. please visit http://bit.ly/2HhWBcA for detail");
			});
			jobStatus = true;
		}
		return jobStatus;

	}

	@Override
	public boolean forgotPassword(String loginString) {
		CaamUser loginUserFound = userRepository.getByLoginString(loginString);
		if(loginUserFound!=null) {jobStatus=true;}
		return jobStatus;
	}

	@Override
	public UserDto getCaamUserByLoginString(String loginString) {
		Optional<CaamUser> caamUser = this.userRepository.findByActivationOtpANDLoginString(loginString);
		if (caamUser.isPresent())
			return new ModelMapper().map(caamUser.get(), UserDto.class);
		return null;
	}

	@Override
	public UserInfoDto getCaamUserById(long userId) throws UserNotFoundException {
		CaamUser caamUser = this.userRepository.getById(userId);
		if (caamUser == null) {
			throw new UserNotFoundException(RECORD_NOT_FOUND_CODE);
		}
		UserInfoDto dto = getMapper().map(caamUser, UserInfoDto.class);
		try {
			String encodedString = Base64.getEncoder().encodeToString(caamUser.getUserImage());
			dto.setUserImage("data:image/jpeg;base64," + encodedString);
		} catch (NullPointerException e) {
		}
		return dto;
	}

	@Override
	public List<UserDetailDto> getCaamUsersByAll(UserDetailsRequestDto userIds) {
		logger.info(userIds.getList());
		List<CaamUser> list = this.userRepository.getAll(userIds.getList());
		logger.info(list);
		List<UserDetailDto> userDetails = list.stream().map(s -> getMapper().map(s, UserDetailDto.class))
				.collect(Collectors.toList());
		logger.info(userDetails);
		return userDetails;
	}

	@Override
	public com.caam.user.dto.UserDto updateUser(com.caam.user.dto.UserDto userDto) throws UserNotFoundException {
		Optional<CaamUser> caamUser = userRepository.findById(userDto.getId());
		if (caamUser.isPresent()) {
			CaamUser user = caamUser.get();
			user.setName(userDto.getName());
			user.setLoginString(userDto.getLoginString());
			user.setLastName(userDto.getLastName());
			user.setDob(userDto.getDob());
			user.setDescription(userDto.getDescription());
			user.setUserImage(userDto.getUserImage());
			user.setCnic(userDto.getCnic());
			user.setLocation(userDto.getLocation());
			userRepository.save(user);
		} else {
			throw new UserNotFoundException(RECORD_NOT_FOUND_CODE);
		}

		return userDto;
	}

	@Override
	public Optional<CaamUser> findByForgotPasswordToken(String resetToken) throws UserNotFoundException {

		Optional<CaamUser> caamUser = userRepository.findByForgotPasswordToken(resetToken);
		if (!caamUser.isPresent()) {
			throw new UserNotFoundException(RECORD_NOT_FOUND_CODE);
		}
		return caamUser;
	}

	@Override
	public Optional<CaamUser> findByLoginString(String loginString)
			throws UserNotFoundException {
		Optional<CaamUser> caamUser = userRepository.findByForgotPasswordTokenANDLoginString(loginString);
		if (!caamUser.isPresent()) {
			throw new UserNotFoundException(RECORD_NOT_FOUND_CODE);
		}
		return caamUser;
	}

	@Override
	public com.caam.user.dto.UserDto updateUserPassword(com.caam.user.dto.UserDto userDto)
			throws UserNotFoundException {
		Optional<CaamUser> caamUser = userRepository.findById(userDto.getId());
		if (caamUser.isPresent()) {
			CaamUser user = caamUser.get();
			user.setName(userDto.getName());
			user.setLoginString(userDto.getLoginString());
			user.setLastName(userDto.getLastName());
			user.setDob(userDto.getDob());
			user.setDescription(userDto.getDescription());
			user.setUserImage(userDto.getUserImage());
			user.setCnic(userDto.getCnic());
			user.setLocation(userDto.getLocation());
			user.setForgotPasswordToken("");
			user.setPassword(userDto.getPassword());

			userRepository.save(user);
		} else {
			throw new UserNotFoundException(RECORD_NOT_FOUND_CODE);
		}

		return userDto;
	}

	/*
	 * @Override public Optional<CaamUser> findByActivationOtpAndLoginString( String
	 * loginString) throws UserNotFoundException { Optional<CaamUser> caamUser =
	 * userRepository.findByActivationOtpANDLoginString( loginString); if
	 * (!caamUser.isPresent()) { throw new
	 * UserNotFoundException(RECORD_NOT_FOUND_CODE); } return caamUser; }
	 */

	@Transactional
	@Override
	public long deleteByLoginString(String title) {
		// TODO Auto-generated method stub
		this.userRepository.deleteByLoginString(title);
		return 0;
	}

	@Override
	public boolean verifyMobileUser(ActivationDto activationDto)
			throws UserNotFoundException, UserAlreadyVerifiedException {
		Optional<CaamUser> verifyUser = userRepository.findByTokenOrOtp(activationDto.getActivationToken(),activationDto.getActivationOtp());
		jobStatus = false;
		if (!verifyUser.isPresent())
			throw new UserNotFoundException(RECORD_NOT_FOUND_CODE);
		if (verifyUser.get().getId() != 0) {
			if (verifyUser.get().isVerified()) {
				throw new UserAlreadyVerifiedException(USER_ALREADY_VERIFIED_CODE);
			}
			logger.info("Here inside if" + jobStatus);
			verifyUser.get().setVerified(true);
			userRepository.save(verifyUser.get());
			CompletableFuture.runAsync(() -> {
				notificationClient.sendTextMessage(verifyUser.get().getLoginString(),
						"Thank you for registering with CAAM. Please visit caam.pk to post or search caam.");
				// notificationClient.sendTextMessage("923008875939", "Thank you for registering
				// with Caam.pk. please visit http://bit.ly/2HhWBcA for detail");
			});
			jobStatus = true;
		}
		return jobStatus;
	}
	
	
}
