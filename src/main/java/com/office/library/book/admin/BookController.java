package com.office.library.book.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.office.library.book.BookVo;
import com.office.library.book.HopeBookVo;
import com.office.library.book.RentalBookVo;
import com.office.library.book.admin.util.UploadFileService;

@Controller
@RequestMapping("/book/admin")
public class BookController {
	
	@Autowired
	BookService bookService;
	
	@Autowired
	UploadFileService uploadFileService;
	
	@GetMapping("/registerBookForm")
	public String registerBookForm() {
		System.out.println("[BookController] registerBookForm()");
		
		String nextPage = "admin/book/register_book_form";
		
		return nextPage;
	}
	//도서 등록 폼으로 이동
	
	@PostMapping("/registerBookConfirm")
	public String registerBookConfirm(BookVo bookVo, @RequestParam("file") MultipartFile file) {
		System.out.println("[BookController] registerBookConfirm()");
		
		String nextPage = "admin/book/register_book_ok";
		
		String savedFileName = uploadFileService.upload(file);
		//파일 업로드 클래스를 통해 받아온 파일을 지정된 장소에 저장 완료
		if (savedFileName != null) {
			bookVo.setB_thumbnail(savedFileName);
			int result = bookService.registerBookConfirm(bookVo);
			//도서등록 실행
			if (result <= 0)
				nextPage = "admin/book/register_book_ng";
		} else {
			nextPage = "admin/book/register_book_ng";
		}
		return nextPage;
	}
	
	@GetMapping("/searchBookConfirm")
	public String searchBookConfirm(BookVo bookVo, Model model) {
		//검색어를 가지고 서비스에 보냄
		System.out.println("[BookController] searchBookConfirm()");
		
		String nextPage = "admin/book/search_book";
		
		List<BookVo> bookVos = bookService.searchBookConfirm(bookVo);
		//찾아진 책 리스트를 받아서 뷰에 띄우기 위해 모델에 넣어줌
		model.addAttribute("bookVos", bookVos);
		
		return nextPage;
	}
	
	@GetMapping("/bookDetail")
	public String bookDetail(@RequestParam("b_no") int b_no, Model model) {
		//도서 상세보기
		System.out.println("[BookController] bookDetail()");
		
		String nextPage = "admin/book/book_detail";
		
		BookVo bookVo = bookService.bookDetail(b_no);
		//책 고유 넘버를 넘김
		model.addAttribute("bookVo", bookVo);
		//받아온 객체를 모델에 저장하고 뷰로 넘김
		return nextPage;
	}
	
	@GetMapping("/modifyBookForm")
	public String modifyBookForm(@RequestParam("b_no") int b_no, Model model) {
		System.out.println("[BookController] modifyBookForm()");
		
		String nextPage = "admin/book/modify_book_form";
		
		BookVo bookVo = bookService.modifyBookForm(b_no);
		//정보를 수정할때 기존 정보를 표시해줘야하니 책 넘버로 찾아옴
		model.addAttribute("bookVo", bookVo);
		
		return nextPage;
	}
	
	@PostMapping("/modifyBookConfirm")
	public String modifyBookConfirm(BookVo bookVo, @RequestParam("file") MultipartFile file) {
		System.out.println("[BookController] modifyBookConfirm()");
		
		String nextPage = "admin/book/modify_book_ok";
		
		if(!file.getOriginalFilename().equals("")) {
			//첨부파일을 수정할경우 빈값이 아니라면 다시 파일업로드가 필요함
			String savedFileName = uploadFileService.upload(file);
			if(savedFileName != null) {
				bookVo.setB_thumbnail(savedFileName);
			}
		}
		//첨부파일을 제외한 나머지 정보만 변경할경우가 아래
			int result = bookService.modifyBookConfirm(bookVo);
			
			if(result <= 0) {
				nextPage = "admin/book/modify_book_ng";
			}
			return nextPage;
	}
	
	@GetMapping("/deleteBookConfirm")
	public String deleteBookConfirm(@RequestParam("b_no") int b_no) {
		//도서삭제
		System.out.println("[BookController] deleteBookConfirm()");
		
		String nextPage = "admin/book/delete_book_ok";
		
		int result = bookService.deleteBookConfirm(b_no);
		
		if(result <= 0) {
			nextPage = "admin/book/delete_book_ng";
		}
		return nextPage;
	}
	@GetMapping("/getRentalBooks")
	public String getRentalBooks(Model model) {
		System.out.println("[BookController] getRentalBooks()");
		
		String nextPage = "admin/book/rental_books";
		
		List<RentalBookVo> rentalBookVos = bookService.getRentalBooks();
		
		model.addAttribute("rentalBookVos", rentalBookVos);
		
		return nextPage;
	}
	
	@GetMapping("/returnBookConfirm")
	public String returnBookConfirm(@RequestParam("b_no") int b_no, @RequestParam("rb_no") int rb_no) {
		System.out.println("[BookController] returnBookConfirm()");
		
		String nextPage = "admin/book/return_book_ok";
		
		int result = bookService.returnBookConfirm(b_no, rb_no);
		
		if(result <= 0) {
			nextPage = "admin/book/return_book_ng";
		}
		return nextPage;
	}
	
	@GetMapping("/getHopeBooks")
	public String getHopeBooks(Model model) {
		System.out.println("[BookController] getHopeBooks()");
		
		String nextPage = "admin/book/hope_books";
		
		List<HopeBookVo> hopeBookVos = bookService.getHopeBooks();
		
		model.addAttribute("hopeBookVos", hopeBookVos);
		
		return nextPage;
		
	}
	
	@GetMapping("registerHopeBookForm")
	public String registerHopeBookForm(Model model, HopeBookVo hopeBookVo) {
		System.out.println("[BookController] registerHopeBookForm()");
		
		String nextPage = "admin/book/register_hope_book_form";
		
		model.addAttribute("hopeBookVo", hopeBookVo);
		
		return nextPage;
	}
	
	@PostMapping("/registerHopeBookConfirm")
	public String registerHopeBookConfirm(BookVo bookVo, 
					@RequestParam("hb_no") int hb_no, 
					@RequestParam("file") MultipartFile file) {
		System.out.println("[BookController] registerHopeBookConfirm()");
		
		System.out.println("hb_no : " + hb_no);
		
		String nextPage = "admin/book/register_book_ok";
		
		String savedFileName = uploadFileService.upload(file);
		if (savedFileName != null) {
			bookVo.setB_thumbnail(savedFileName);
			int result = bookService.registerHopeBookConfirm(bookVo, hb_no);
			if (result <= 0)
				nextPage = "admin/book/register_book_ng";
		} else {
			nextPage = "admin/book/register_book_ng";
		}
		return nextPage;

	}
}

