package com.caam.user.domain;

import com.caam.commons.base.domain.BaseDomain;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@Entity
@Table(name = "caamuser")
public class CaamUser extends BaseDomain {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private long id;

	@Column(name = "name")
	@NotNull
	@Size(min = 1, max = 50)
	private String name;

	@NotNull
	@Column(name = "password")
	private String password;

	@Column(name = "login_String")
	@Size(min = 1, max = 50)
	private String loginString;

	@CreationTimestamp
	@Column(name = "created")
	private Date created;

	@Column(name = "created_By")
	private long createdBy;

	@UpdateTimestamp
	@Column(name = "updated")
	private Date updated;

	@Column(name = "updated_by")
	private long updatedBy;

	@NotNull
	@Column(nullable = false)
	private boolean activated;// on save we will pass true , mean activated,
	@NotNull
	@Column(nullable = false)
	private boolean verified;

	@Column(name = "activation_Token")
	private String activationToken;

	@Column(name = "activation_Otp")
	private int activationOtp;

	@Column(name = "forgot_Password_Token")
	private String forgotPasswordToken;

	@Column(name = "last_Name")
	private String lastName;

	@Column(name = "location")
	private String location;

	@Column(name = "dob")
	private String dob;

	@Column(name = "description")
	private String description;

	@Column(name = "user_Image")
	private byte[] userImage;

	@Column(name="cnic")
	private String cnic;

	@Column(name = "device_id")
	private String deviceId;
	
	@Column(name = "register_from")
	private String registerFrom;
	
	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public CaamUser() {
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getLoginString() {
		return loginString;
	}

	public void setLoginString(String loginString) {
		this.loginString = loginString;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public long getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(long createdBy) {
		this.createdBy = createdBy;
	}

	public Date getUpdated() {
		return updated;
	}

	public void setUpdated(Date updated) {
		this.updated = updated;
	}

	public long getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(long updatedBy) {
		this.updatedBy = updatedBy;
	}

	public boolean isActivated() {
		return activated;
	}

	public void setActivated(boolean activated) {
		this.activated = activated;
	}

	public boolean isVerified() {
		return verified;
	}

	public void setVerified(boolean verified) {
		this.verified = verified;
	}

	public String getActivationToken() {
		return activationToken;
	}

	public void setActivationToken(String activationToken) {
		this.activationToken = activationToken;
	}

	public int getActivationOtp() {
		return activationOtp;
	}

	public void setActivationOtp(int activationOtp) {
		this.activationOtp = activationOtp;
	}

	public String getForgotPasswordToken() {
		return forgotPasswordToken;
	}

	public void setForgotPasswordToken(String forgotPasswordToken) {
		this.forgotPasswordToken = forgotPasswordToken;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getDob() {
		return dob;
	}

	public void setDob(String dob) {
		this.dob = dob;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public byte[] getUserImage() {
		return userImage;
	}

	public void setUserImage(byte[] userImage) {
		this.userImage = userImage;
	}

	public String getCnic() {
		return cnic;
	}

	public void setCnic(String cnic) {
		this.cnic = cnic;
	}

	public String getRegisterFrom() {
		return registerFrom;
	}

	public void setRegisterFrom(String registerFrom) {
		this.registerFrom = registerFrom;
	}


	
}
