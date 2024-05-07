package com.office.library.admin.member;

import java.security.SecureRandom;
import java.util.Date;
import java.util.List;

import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

@Service
public class AdminMemberService {
	
	final static public int ADMIN_ACCOUNT_ALREADY_EXIST = 0;
	final static public int ADMIN_ACCOUNT_CREATE_SUCCESS = 1;
	final static public int ADMIN_ACCOUNT_CREATE_FAIL = -1;
	
	//회원가입 시 0 이하의 값(-1 또는 0)을 컨트롤러에게 반환하게 되면 관리자 회원가입은 실패이고, 1을 반환하게 되면 회원가입은 성공임

	
	@Autowired
	AdminMemberDao adminMemberDao;
	
	@Autowired
	JavaMailSenderImpl javaMailSenderImpl;
	
	public int createAccountConfirm(AdminMemberVo adminMemberVo) {
		System.out.println("[AdminMemberService] createAccountConfirm()");
		
		boolean isMember = adminMemberDao.isAdminMember(adminMemberVo.getA_m_id());
		//중복아이디 체크부터 보냄. 중복아이디가 없음(false)으로 응답이 오면 ! 연산자로 아래 if문 실행
		if(!isMember) {
			int result = adminMemberDao.insertAdminAccount(adminMemberVo);
			
			if(result > 0) {
				return ADMIN_ACCOUNT_CREATE_SUCCESS;
			} else {
				return ADMIN_ACCOUNT_CREATE_FAIL;
			}
		} else {
			return ADMIN_ACCOUNT_ALREADY_EXIST;
		}

	}
	
	public AdminMemberVo loginConfirm(AdminMemberVo adminMemberVo) {
		System.out.println("[AdminMemberService] loginConfirm()");
		
		AdminMemberVo loginedAdminMemberVo = adminMemberDao.selectAdmin(adminMemberVo);
		
		if(loginedAdminMemberVo != null) {
			System.out.println("[AdminMemberService] ADMIN MEMBER LOGIN SUCCCESS!!");
		} else {
			System.out.println("[AdminMemberService] ADMIN MEMBER LOGIN FAIL!!");
		}
		
		//loginConfirm 메소드에서 dao에 요청한 회원검색 결과로 null이 아니면 로그인성공 null이면 실패
		//로그인에 성공했다면 컨트롤러는 loginedAdminMemberVo를 세션에 저장함
		
		return loginedAdminMemberVo;
	}
	
	public List<AdminMemberVo> listupAdmin() {
		System.out.println("[AdminMemberService] listupAdmin()");
		
		return adminMemberDao.selectAdmins();
	}
	
	public void setAdminApproval(int a_m_no) {
		System.out.println("[AdminMemberService] setAdminApproval()");
		
		int result = adminMemberDao.updateAdminAccount(a_m_no);
		
	}
	
	public int modifyAccountConfirm(AdminMemberVo adminMemberVo) {
		System.out.println("[AdminMemberService] modifyAccountConfirm()");
		
		return adminMemberDao.updateAdminAccount(adminMemberVo);
	}
	
	public AdminMemberVo getLoginedAdminMemberVo(int a_m_no) {
		System.out.println("[AdminMemberService] getLoginedAdminMemberVo()");
		
		return adminMemberDao.selectAdmin(a_m_no);
		//가장 최근에 수정한 정보를 가져오는 역할을 함
	}
	
	public int findPasswordConfirm(AdminMemberVo adminMemberVo) {
		System.out.println("[AdminMemberService] findPasswordConfirm()");
		
		AdminMemberVo selectedAdminMemberVo = adminMemberDao.selectAdmin(adminMemberVo.getA_m_id(), 
																		 adminMemberVo.getA_m_name(), 
																		 adminMemberVo.getA_m_mail());
		//받아온 정보 id, name, mail을 DAO에게 넘김
		
		int result = 0;
		
		if (selectedAdminMemberVo != null) {
			
			String newPassword = createNewPassword();
			//아래 createNewPassword 메소드를 통해 받은 랜덤 비밀번호를 DB에 넣기위해 DAO로 보냄
			//DB에 미리 넣어놔야 사용자가 메일을 받고 그걸로 로그인 할 수 있으니까
			result = adminMemberDao.updatePassword(adminMemberVo.getA_m_id(), newPassword);
			//정상적으로 업데이트가 됐으면 1이 리턴
			if (result > 0)
				sendNewPasswordByMail(adminMemberVo.getA_m_mail(), newPassword);
			//1이 리턴되면 내용을 모아서 메일발송
		}
		
        	return result;
		
	}
	
	private String createNewPassword() {
		System.out.println("[AdminMemberService] createNewPassword()");
		
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
		System.out.println("[AdminMemberService] NEW PASSWORD: " + stringBuffer.toString());
		
		return stringBuffer.toString();
	}
	
	private void sendNewPasswordByMail(String toMailAddr, String newPassword) {
		System.out.println("[AdminMemberService] sendNewPasswordByMail()");
		
		final MimeMessagePreparator mimeMessagePreparator = new MimeMessagePreparator() {
			@Override
			public void prepare(MimeMessage mimeMessage) throws Exception {
				final MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "utf-8");
				mimeMessageHelper.setTo("seulgi0587@gmail.com"); //실제론 받아온 toMailAddr로 아이디에 해당하는 메일주소로 발송
				mimeMessageHelper.setSubject("[한국도서관] 새비밀번호 안내입니다.");
				mimeMessageHelper.setText("새비밀번호 : " + newPassword, true);
			}
		};
		javaMailSenderImpl.send(mimeMessagePreparator);
		//메세지를 준비해서 받아온 매개(메일주소, 랜덤비밀번호)를 메일발송!!
	}

}
