package com.office.library.user.member;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user/member")
public class UserMemberController {
	@Autowired
	UserMemberService userMemberService;
	
	@GetMapping("/createAccountForm")
	public String createAccountForm() {
		//회원가입 폼으로 이동
		System.out.println("[UserMemberController] createAccountForm()");
		
		String nextPage = "user/member/create_account_form";
		
		return nextPage;
	}
	
	@PostMapping("/createAccountConfirm")
	public String createAccountConfirm(UserMemberVo userMemberVo) {
		//회원가입 검증
		System.out.println("[UserMemberController] createAccountConfirm()");
		
		String nextPage = "user/member/create_account_ok";
		
		int result = userMemberService.createAccountConfirm(userMemberVo);
		//성공하면 1을 받아옴
		if(result <= 0 ) {
			//1을 받아오지 않으면 ng페이지로 이동
			nextPage = "user/member/create_account_ng";
		}
		return nextPage;
	}
	
	@GetMapping("/loginForm")
	public String loginForm() {
		System.out.println("[UserMemberController] loginForm()");
		
		String nextPage = "user/member/login_form";
		
		return nextPage;
	}
	
	@PostMapping("/loginConfirm")
	public String loginConfirm(UserMemberVo userMemberVo, HttpSession session) {
		System.out.println("[UserMemberController] loginConfirm()");
		
		String nextPage = "user/member/login_ok";
		//받아온 아이디 비밀번호를 보냄
		UserMemberVo loginedUserMemberVo = userMemberService.loginConfirm(userMemberVo);

		if(loginedUserMemberVo == null) {
			nextPage = "user/member/login_ng";
		} else {
			session.setAttribute("loginedUserMemberVo", loginedUserMemberVo);
			session.setMaxInactiveInterval(60 * 30);
		}
		return nextPage;
	}
	
	@GetMapping("/modifyAccountForm")
	public String modifyAccountForm(HttpSession session) {
		System.out.println("[UserMemberController] modifyAccountForm()");
		
		String nextPage = "user/member/modify_account_form";
		
//		UserMemberVo loginedUserMemberVo = (UserMemberVo) session.getAttribute("loginedUserMemberVo");
//		
//		if(loginedUserMemberVo == null) {
//			nextPage = "redirect:/user/member/loginForm";
//		} 핸들러 인터셉터를 사용해서 로그인을 했는지 확인할 필요 없음!
		return nextPage;
	}
	
	@PostMapping("/modifyAccountConfirm")
	public String modifyAccountConfirm(UserMemberVo userMemberVo, HttpSession session) {
		System.out.println("[UserMemberController] modifyAccountConfirm()");
		
		String nextPage = "user/member/modify_account_ok";
		
		int result = userMemberService.modifyAccountConfirm(userMemberVo);
		//로그인 된 상태에서 수정을 완료하면 수정된 회원의 정보가 뜨게 하기 위해 추가로 설정
		if(result > 0) {
			UserMemberVo loginedUserMemberVo = userMemberService.getLoginedUserMemberVo(userMemberVo.getU_m_no());
			
			session.setAttribute("loginedUserMemberVo", loginedUserMemberVo);
			session.setMaxInactiveInterval(60 * 30);
		} else {
			nextPage = "user/member/modify_account_ng";
		}
		return nextPage;
	}
	
	@GetMapping("/logoutConfirm")
	public String logoutConfirm(HttpSession session) {
		System.out.println("[UserMemberController] logoutConfirm()");
		
		String nextPage = "redirect:/";
		session.invalidate();
		return nextPage;
	}
	
	@GetMapping("/findPasswordForm")
	public String findPasswordForm() {
		System.out.println("[UserMemberController] findPasswordForm()");
		
		String nextPage = "user/member/find_password_form";
		
		return nextPage;
	}
	
	@PostMapping("/findPasswordConfirm")
	public String findPasswordConfirm(UserMemberVo userMemberVo) {
		System.out.println("[UserMemberController] findPasswordConfirm()");
		
		String nextPage = "user/member/find_password_ok";
		
		int result = userMemberService.findPasswordConfirm(userMemberVo);
		
		if(result <= 0) {
			nextPage = "user/member/find_password_ng";
		}
		
		return nextPage;
	}
}
