package com.caam.user.service;

import com.caam.user.domain.CaamUser;
import com.caam.user.dto.ActivationDto;
import com.caam.user.dto.LoginDto;
import com.caam.user.dto.UserDetailDto;
import com.caam.user.dto.UserDetailsRequestDto;
import com.caam.user.dto.UserDto;
import com.caam.user.dto.UserInfoDto;
import com.caam.user.exception.DocumentNotSavedException;
import com.caam.user.exception.UserAlreadyVerifiedException;
import com.caam.user.exception.UserBadRequestException;
import com.caam.user.exception.UserNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.List;

@Service
public interface UserService {

	UserDto saveUser(UserDto userDto);

    UserDto login(LoginDto loginDto) throws UserNotFoundException;

    boolean verifyUser(String state) throws UserNotFoundException, UserAlreadyVerifiedException;
    boolean verifyMobileUser(ActivationDto activationDto) throws UserNotFoundException, UserAlreadyVerifiedException;

    boolean forgotPassword(String loginString);

    UserDto getCaamUserByLoginString(String loginString);

    UserInfoDto getCaamUserById(long loginUserId) throws UserNotFoundException;

    List<UserDetailDto> getCaamUsersByAll(UserDetailsRequestDto userIds);

    UserDto updateUser(UserDto userDto) throws UserNotFoundException;
    Optional<CaamUser> findByForgotPasswordToken(String resetToken) throws UserNotFoundException;
    Optional<CaamUser> findByLoginString(String loginString) throws UserNotFoundException;
    UserDto updateUserPassword(UserDto userDto) throws UserNotFoundException;
    long deleteByLoginString(String title);
   // public com.caam.user.dto.UserDto updateUserCity(com.caam.user.dto.UserDto userDto)throws UserNotFoundException;


}
