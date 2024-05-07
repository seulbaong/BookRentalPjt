package com.office.library.admin.member;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin/member")
public class AdminMemberController {
	
	@Autowired
	AdminMemberService adminMemberService;
	//Member Service는 Service 빈으로 설정되어있어 Autowired로 주입받아 사용함
	
	@GetMapping("/createAccountForm")
	public String createAccountForm() {
		System.out.println("[AdminMemberController] createAccountForm()");
		String nextPage = "admin/member/create_account_form";
		
		return nextPage;
	}
	// /admin/member 링크를 타고 들어왔을때 get요청이면 해당 메소드 실행.
	// 위 메소드는 바로 멤버생성 jsp페이지로 갈수있게 리턴.
	
	@PostMapping("/createAccountConfirm")
	public String createAccountConfirm(AdminMemberVo adminMemberVo) {
		System.out.println("[AdminMemberController] createAccountConfirm()");
		
		String nextPage = "admin/member/create_account_ok";
		
		int result = adminMemberService.createAccountConfirm(adminMemberVo);
		
		if(result <= 0) {
			nextPage = "admin/member/create_account_ng";
		}
		//result값이 0보다 같거나 작을경우 회원가입에 실패한것이기 때문에 가입실패 페이지로 이동
		return nextPage;
	}
	// 사용자가 멤버생성 페이지를 모두 작성후 submit을 하면 이 post 메소드가 실행됨
	// 작성된 내용을 AdminMemberVo 객체에 담아서 adminService에 멤버생성컴펌 메소드를 실행
	
	@GetMapping("/loginForm")
	public String loginForm() {
		System.out.println("[AdminMemberController] loginForm()");
		
		String nextPage = "admin/member/login_form";
		
		return nextPage;
	}
	
	@PostMapping("/loginConfirm")
	public String loginConfirm(AdminMemberVo adminMemberVo, HttpSession session) {
		System.out.println("[AdminMemberController] loginConfirm()");
		
		String nextPage = "admin/member/login_ok";
		
		AdminMemberVo loginedAdminMemberVo = adminMemberService.loginConfirm(adminMemberVo);
		
		if(loginedAdminMemberVo == null) {
			nextPage = "admin/member/login_ng";
		} else {
			session.setAttribute("loginedAdminMemberVo", loginedAdminMemberVo);
			//session에서 로그인 정보를 저장할 때 사용하는 메소드
			session.setMaxInactiveInterval(60 * 30);
			//세션의 유효 기간을 설정하는 메소드(단위 : 초)
			
			//adminMemberService에서 AdminMemberVo객체를 반환하면 null인경우 세션작업 없음. null이 아닌경우 세션에 VO객체를 저장
		}
		
		return nextPage;
	}
	
	@RequestMapping(value = "/logoutConfirm", method = RequestMethod.GET)
	public String logoutConfirm(HttpSession session) {
		System.out.println("[AdminMemberController] logoutConfirm()");
		
		String nextPage = "redirect:/admin";
		
		session.invalidate();
		//HttpSession을 매개변수로 받고 invalidate()를 호출함
		//세션을 무효화 시키는 것으로 세션에 저장된 데이터를 더이상 사용할 수 없게됨
		//따라서 로그인 상태는 해제되고 /admin 으로 redirect됨
		//리다이렉트 되면 컨트롤러의 홈이 호출되어 admin/home.jsp가 클라이언트에 응답함
		return nextPage;
	}
	
	@RequestMapping(value = "/listupAdmin", method = RequestMethod.GET)
	public String listupAdmin(Model model) {
		//서비스로 요청 후 dao에 selectAdmins로 가서 관리자 리스트를 모델로 받아 클라이언트로 전달
		System.out.println("[AdminMemberController] listupAdmin()");
		
		String nextPage = "admin/member/listup_admins";
		
		List<AdminMemberVo> adminMemberVos = adminMemberService.listupAdmin();
		
		model.addAttribute("adminMemberVos", adminMemberVos);
		//모델을 사용하기 위해선 우선 매개변수로 모델을 받고 addAttribute로 데이터를 추가해야함
		return nextPage;
	}
	
	@RequestMapping(value = "/setAdminApproval", method = RequestMethod.GET)
	public String setAdminApproval(@RequestParam("a_m_no") int a_m_no) {
		System.out.println("[AdminMemberController] setAdminApproval()");
		
		String nextPage = "redirect:/admin/member/listupAdmin";
		
		adminMemberService.setAdminApproval(a_m_no);
		
		return nextPage;
	}
	
	@GetMapping("/modifyAccountForm")
	public String modifyAccountForm(HttpSession session) {
		System.out.println("[AdminMemberController] modifyAccountForm()");
		
		String nextPage = "admin/member/modify_account_form";
		
		AdminMemberVo loginedAdminMemberVo = (AdminMemberVo) session.getAttribute("loginedAdminMemberVo");
		
		if(loginedAdminMemberVo == null) {
			nextPage = "redirect:/admin/member/loginForm";
		}
		//세션에 저장되어있는 관리자 정보가 null이면 로그인 전으로 판단해서 로그인 화면으로 리다이렉트함
		//도메인에 /admin/member/modifyAccountForm 를 입력하고 들어오면 로그인 하지 않고도 modifyAccountForm 메소드가 호출될수 있기때문에 필요한 코드!
		//또 로그인은 했지만 세션에 설정한 유효시간을 초과해서 자리를 비우는경우 로그아웃 상태가 되기때문에 null이됨
		
		return nextPage;
	}
	
	@PostMapping("/modifyAccountConfirm")
	public String modifyAccountConfirm(AdminMemberVo adminMemberVo, HttpSession session) {
		//관리자 계정정보 수정
		System.out.println("[AdminMemberController] modifyAccountConfirm()");
		
		String nextPage = "admin/member/modify_account_ok";
		
		int result = adminMemberService.modifyAccountConfirm(adminMemberVo);
		//정보수정 실행되고 돌아옴
		
		if(result > 0) {
			AdminMemberVo loginedAdminMemberVo = adminMemberService.getLoginedAdminMemberVo(adminMemberVo.getA_m_no());
			//가장 최근에 수정된 정보를 가져옴
			session.setAttribute("loginedAdminMemberVo", loginedAdminMemberVo);
			session.setMaxInactiveInterval(60 * 30);
			//다시 계정수정 버튼을 눌렀을때 수정된 계정정보가 보여야하니 세션에 한번 업데이트 해줌
		} else {
			nextPage = "redirect:/admin/member/modify_account_ng";
		}
		
		return nextPage;
	}
	
	@GetMapping("/findPasswordForm")
	public String findPasswordForm() {
		System.out.println("[AdminMemberController] findPasswordForm()");
		
		String nextPage = "admin/member/find_password_form";
		
		return nextPage;
	}
	//비밀번호 찾기. 폼에서 입력한 정보로 DB에서 찾아 계정 메일로 임시 비밀번호를 보낼것임
	
	@PostMapping("/findPasswordConfirm")
	public String findPasswordConfirm(AdminMemberVo adminMemberVo) {
		System.out.println("[AdminMemberController] findPasswordConfirm()");
		
		String nextPage = "admin/member/find_password_ok";
		
		int result = adminMemberService.findPasswordConfirm(adminMemberVo);
		//입력받은 정보를 넘김
		
		if(result <= 0) {
			nextPage = "admin/member/find_password_ng";
		}
		return nextPage;
	}
}
