package com.office.library.user.member;

import java.security.SecureRandom;
import java.util.Date;

import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

@Service
public class UserMemberService {
	
	final static public int USER_ACCOUNT_ALREADY_EXIST = 0;
	final static public int USER_ACCOUNT_CREATE_SUCCESS = 1;
	final static public int USER_ACCOUNT_CREATE_FAIL = -1;
	
	@Autowired
	UserMemberDao userMemberDao;
	
	@Autowired
	JavaMailSenderImpl javaMailSenderImpl;
	
	public int createAccountConfirm(UserMemberVo userMemberVo) {
		System.out.println("[UserMemberService] createAccountConfirm()");
		
		boolean isMember = userMemberDao.isUserMember(userMemberVo.getU_m_id());
		//id를 넘겨서 이미 존재하는 계정인지 확인
		if(!isMember) {
			//없는 회원이라면 회원가입 진행
			int result = userMemberDao.insertUserAccount(userMemberVo);
			
			if(result > 0) {
				//결과를 받아와 0을 초과(1개 이상이면 실행 완료)
				return USER_ACCOUNT_CREATE_SUCCESS;
			} else {
				//0이하라면 실패
				return USER_ACCOUNT_CREATE_FAIL;
			}
		} else {
			//있는 회원이라면 이미 있다고 메세지 띄움!
			return USER_ACCOUNT_ALREADY_EXIST;
		}
	}
	
	public UserMemberVo loginConfirm(UserMemberVo userMemberVo) {
		System.out.println("[UserMemberService] loginConfirm()");
		
		UserMemberVo loginedUserMemberVo = userMemberDao.selectUser(userMemberVo);

		if(loginedUserMemberVo != null) {
			System.out.println("[UserMemberService] USER MEMBER LOGIN SUCCESS!!");
		} else {
			System.out.println("[UserMemberService] USER MEMBER LOGIN FAIL!!");
		}
		return loginedUserMemberVo;
	}
	
	public int modifyAccountConfirm(UserMemberVo userMemberVo) {
		System.out.println("[UserMemberService] loginConfirm()");
		
		return userMemberDao.updateUserAccount(userMemberVo);
	}
	
	public UserMemberVo getLoginedUserMemberVo(int u_m_no) {
		System.out.println("[UserMemberService] getLoginedUserMemberVo()");
		
		return userMemberDao.selectUser(u_m_no);
	}
	
	public int findPasswordConfirm(UserMemberVo userMemberVo) {
		System.out.println("[UserMemberService] findPasswordConfirm()");
		
		UserMemberVo selectedUserMemberVo = userMemberDao.selectUser(
											userMemberVo.getU_m_id(),
											userMemberVo.getU_m_name(),
											userMemberVo.getU_m_mail());
		int result = 0;
		
		if(selectedUserMemberVo != null) {
			String newPassword = createNewPassword();
			
			result = userMemberDao.updatePassword(userMemberVo.getU_m_id(), newPassword);
			
			if(result > 0) {
				sendNewPasswordByMail(userMemberVo.getU_m_mail(), newPassword);
			}
		}
		return result;
	}
	
	private String createNewPassword() {
		System.out.println("[UserMemberService] createNewPassword()");
		
		char[] chars = new char[] {
				'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 
				'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 
				'u', 'v', 'w', 'x', 'y', 'z'
				};

		StringBuffer stringBuffer = new StringBuffer();
		//문자열을 추가하거나 변경할때 사용
		SecureRandom secureRandom = new SecureRandom();
		//랜덤의 업그레이드 버전
		secureRandom.setSeed(new Date().getTime());
		
		int index = 0;
		int length = chars.length;
		for (int i = 0; i < 8; i++) {
			index = secureRandom.nextInt(length);
		
			if (index % 2 == 0) 
				stringBuffer.append(String.valueOf(chars[index]).toUpperCase());
			else
				stringBuffer.append(String.valueOf(chars[index]).toLowerCase());
		
		}
		//현재 시간을 랜덤에게 매개로 주고 랜덤으로 chars에서 값을 꺼냄
		System.out.println("[UserMemberService] NEW PASSWORD: " + stringBuffer.toString());
		
		return stringBuffer.toString();
	}
	
	private void sendNewPasswordByMail(String toMailAddr, String newPassword) {
		System.out.println("[AdminMemberService] sendNewPasswordByMail()");
		
		final MimeMessagePreparator mimeMessagePreparator = new MimeMessagePreparator() {
			@Override
			public void prepare(MimeMessage mimeMessage) throws Exception {
				final MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "utf-8");
				//mimeMessageHelper.setTo(toMailAddr); 실제론 받아온 toMailAddr로 아이디에 해당하는 메일주소로 발송
				mimeMessageHelper.setTo("seulgi0587@gmail.com");
				mimeMessageHelper.setSubject("[한국도서관] 새비밀번호 안내입니다.");
				mimeMessageHelper.setText("새비밀번호 : " + newPassword, true);
			}
		};
		javaMailSenderImpl.send(mimeMessagePreparator);
		//메세지를 준비해서 받아온 매개(메일주소, 랜덤비밀번호)를 메일발송!!
	}
}
