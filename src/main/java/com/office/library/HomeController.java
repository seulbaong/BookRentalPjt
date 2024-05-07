package com.office.library;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {
	//http://localhost:8090/library/로 접속하면 /user로 바로 갈수 있도록 리다이렉트 처리함
	@GetMapping("/")
	public String home() {
		System.out.println("[HomeController] home()");
		
		return "redirect:/user/";
	}

}
