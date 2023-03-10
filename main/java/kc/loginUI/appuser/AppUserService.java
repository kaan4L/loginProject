package kc.loginUI.appuser;


import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import kc.loginUI.registration.token.ConfirmationToken;
import kc.loginUI.registration.token.ConfirmationTokenService;

@Service
public class AppUserService implements UserDetailsService
{
	private String USER_NOT_FOUND="USER WITH EMAİL %S NOT FOUND!";
	private final AppUserRepository appUserRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	private final ConfirmationTokenService confirmationTokenService;

	public AppUserService(AppUserRepository appUserRepository,BCryptPasswordEncoder bCryptPasswordEncoder,ConfirmationTokenService confirmationTokenService) 
	{
		super();
		this.appUserRepository = appUserRepository;
		this.bCryptPasswordEncoder=bCryptPasswordEncoder;
		this.confirmationTokenService=confirmationTokenService;
	}

	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException 
	{
		return appUserRepository.findByEmail(email).orElseThrow(() ->new UsernameNotFoundException(String.format(USER_NOT_FOUND, email)));
	}
	
	public String signUpUser(AppUser appUser)
	{
		boolean userExists=appUserRepository.findByEmail(appUser.getEmail()).isPresent();
		
		if(userExists)
		{
			throw new IllegalStateException("user is already exists!");
		}
		
		String encodedPassword=bCryptPasswordEncoder.encode(appUser.getPassword());
		
		appUser.setPassword(encodedPassword);
		
		appUserRepository.save(appUser);
		
		String token=UUID.randomUUID().toString();
		ConfirmationToken confirmationToken=new ConfirmationToken(token,LocalDateTime.now(),LocalDateTime.now().plusMinutes(15),appUser);
		
		confirmationTokenService.saveConfirmationToken(confirmationToken);
		
		return token;
	}
	
		public int enableAppUser(String email)
		{
	        return appUserRepository.enableAppUser(email);
	    }
}


