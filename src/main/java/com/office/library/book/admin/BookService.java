package com.office.library.book.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.office.library.book.BookVo;
import com.office.library.book.HopeBookVo;
import com.office.library.book.RentalBookVo;

@Service
public class BookService {
	
	final static public int BOOK_ISBN_ALREADY_EXIST = 0;
	final static public int BOOK_REGISTER_SUCCESS = 1;
	final static public int BOOK_REGISTER_FAIL = -1;
	
	@Autowired
	BookDao bookDao;
	
	public int registerBookConfirm(BookVo bookVo) {
		System.out.println("[BookService] registerBookConfirm()");
		
		boolean isISBN = bookDao.isISBN(bookVo.getB_isbn());
		//중복검사
		if(!isISBN) {
			int result = bookDao.insertBook(bookVo);
			
			if(result > 0) {
				return BOOK_REGISTER_SUCCESS;
			} else {
				return BOOK_REGISTER_FAIL;
			}
		} else {
			return BOOK_ISBN_ALREADY_EXIST;
		}
		//1개를 insert했으면 1이 리턴돼서 BOOK_REGISTER_SUCCESS인 1을 컨트롤러에 리턴
		//이미 책 정보가 있다면 아래 else문으로 나옴
	}
	
	public List<BookVo> searchBookConfirm(BookVo bookVo) {
		System.out.println("[BookService] searchBookConfirm()");
		
		return bookDao.selectBooksBySearch(bookVo);
		//검색어를 받아 DAO에 전달
	}
	
	public BookVo bookDetail(int b_no) {
		System.out.println("[BookService] searchBookConfirm()");
		
		return bookDao.selectBook(b_no);
		//select로 수정할 책 정보를 찾아옴
	}
	
	public BookVo modifyBookForm(int b_no) {
		System.out.println("[BookService] modifyBookForm()");
		
		return bookDao.selectBook(b_no);
	}
	
	public int modifyBookConfirm(BookVo bookVo) {
		System.out.println("[BookService] modifyBookConfirm()");
		
		return bookDao.updateBook(bookVo);
		//받아온 정보로 업데이트 보냄~!
	}
	
	public int deleteBookConfirm(int b_no) {
		System.out.println("[BookService] deleteBookConfirm()");
		
		return bookDao.deleteBook(b_no);
	}
	
	public List<RentalBookVo> getRentalBooks() {
		System.out.println("[BookService] getRentalBooks()");
		
		return bookDao.selectRentalBooks();
	}
	
	public int returnBookConfirm(int b_no, int rb_no) {
		System.out.println("[BookService] returnBookConfirm()");
		
		int result = bookDao.updateRentalBook(rb_no);
		
		if(result > 0) {
			result = bookDao.updateBook(b_no);
		}
		return result;
	}
	
	public List<HopeBookVo> getHopeBooks() {
		System.out.println("[BookService] getHopeBooks()");
		
		return bookDao.selectHopeBooks();
	}
	
	public int registerHopeBookConfirm(BookVo bookVo, int hb_no) {
		System.out.println("[BookService] registerHopeBookConfirm()");

		boolean isISBN = bookDao.isISBN(bookVo.getB_isbn());
		
		if(!isISBN) {
			int result = bookDao.insertBook(bookVo);
			
			if(result > 0) {
				bookDao.updateHopeBookResult(hb_no);
				
				return BOOK_REGISTER_SUCCESS;
			} else {
				return BOOK_REGISTER_FAIL;
			}
		} else {
			return BOOK_ISBN_ALREADY_EXIST;
		}
	}
}
