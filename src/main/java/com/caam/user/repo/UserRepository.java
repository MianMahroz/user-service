package com.caam.user.repo;

import com.caam.user.domain.CaamUser;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.List;

@Repository
public interface UserRepository extends CrudRepository<CaamUser, Long> {
	 @Query(value = "select * from caamuser WHERE login_string = ?1 and verified=true", nativeQuery = true)
    Optional<CaamUser> findByloginString(String login);

    CaamUser findByLoginStringAndActivatedTrueAndVerifiedTrue(String login);

    CaamUser getByLoginString(String login);

    CaamUser getById(Long loginUserId);

    @Query(value = "select * from caamuser WHERE Id IN(?1) ", nativeQuery = true)
    List<CaamUser> getAll(List userIds);

    @Query(value = "select  * from caamuser where (activation_token=?1 OR activation_otp=?2) AND activation_otp>0", nativeQuery = true)
    Optional<CaamUser> findByTokenOrOtp(String activationtoken, int otp);
	 Optional<CaamUser> findByForgotPasswordToken(String resetToken);
	 @Query(value = "select  * from caamuser where (login_string=?1)", nativeQuery = true)
	 Optional<CaamUser> findByForgotPasswordTokenANDLoginString(String login);

	 @Query(value = "select  * from caamuser where (login_string=?1)", nativeQuery = true)
	 Optional<CaamUser> findByActivationOtpANDLoginString(String login);
	 long deleteByLoginString(String title);
}
