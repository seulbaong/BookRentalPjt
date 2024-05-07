package com.office.library.admin.member;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminMemberDao {
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	//중복아이디를 체크하는 메소드
	public boolean isAdminMember(String a_m_id) {
		System.out.println("[AdminMemberDao] isAdminMember()");
		
		String sql = "SELECT COUNT(*) FROM tbl_admin_member " + "WHERE a_m_id = ?";
		
		int result = jdbcTemplate.queryForObject(sql, Integer.class, a_m_id);
		
		if(result > 0) {
			return true;
		} else {
			return false;
		}
		//아이디가 있냐고 물었을때 있으면 회원가입을 진행할 수 없음
	}

	public int insertAdminAccount(AdminMemberVo adminMemberVo) {
		System.out.println("[AdminMemberDao] insertAdminAccount()");
		
		List<String> args = new ArrayList<String>();
		
		String sql = "INSERT INTO tbl_admin_member(";
		//sql에는 아래 sql에 하나씩 추가 되고 그에 대응되는 값들을 보기좋게 나열해놓은것임 sql은 sql변수에 담기고 args는 args변수에 담김
		
		if(adminMemberVo.getA_m_id().equals("super admin")) {
			sql += "a_m_approval, ";
			args.add("1");
		}
		//super admin 최초 발생하는 최고 관리자이기 때문에 a_m_approval 항목을 미리 1로 지정 (다른 관리자가 회원가입을 할 경우 0이 되어있고 최초관리자가 승인을 해줘야함)
		
		sql += "a_m_id, ";
		args.add(adminMemberVo.getA_m_id());
		
		sql += "a_m_pw, ";
		args.add(passwordEncoder.encode(adminMemberVo.getA_m_pw()));
		
		sql += "a_m_name, ";
		args.add(adminMemberVo.getA_m_name());
		
		sql += "a_m_gender, ";
		args.add(adminMemberVo.getA_m_gender());
		
		sql += "a_m_part, ";
		args.add(adminMemberVo.getA_m_part());
		
		sql += "a_m_position, ";
		args.add(adminMemberVo.getA_m_position());
		
		sql += "a_m_mail, ";
		args.add(adminMemberVo.getA_m_mail());
		
		sql += "a_m_phone, ";
		args.add(adminMemberVo.getA_m_phone());
		
		sql += "a_m_reg_date, a_m_mod_date) ";
		
		if(adminMemberVo.getA_m_id().equals("super admin")) {
			sql += "VALUES(?,?,?,?,?,?,?,?,?,NOW(),NOW())";
		//super admin 이라면 앞에 이미 a_m_approval 항목에 1을 넣어놨으니 ? 가 9개가 되어야함
		} else {
			sql += "VALUES(?,?,?,?,?,?,?,?,NOW(),NOW())";
		}
		
		int result = -1;
		
		try {
			result = jdbcTemplate.update(sql, args.toArray());
			//toArray는 가변배열로 super일때랑 아닐때랑 배열의 길이가 달라짐
			//args에 값들을 모은다음 한번에 update하는것
			//insert한 후 실행한 행의 갯수를 반환해서 변수에 담음
		} catch(Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public AdminMemberVo selectAdmin(AdminMemberVo adminMemberVo) {
		//로그인 하려는 관리자가 입력한 정보와 일치하는 관리자를 테이블에서 조회하기 위한 메서드 (같은 메소드가 총 3개임)
		System.out.println("[AdminMemberDao] selectAdmin()");
		
		String sql =  "SELECT * FROM tbl_admin_member "
					+ "WHERE a_m_id = ? AND a_m_approval > 0";
		
		//관리자를 조회하는 쿼리. 관리자가 입력한 id가 저장되어있는 id와 일치하고 승인이 완료된(1) 회원을 조회
	
		List<AdminMemberVo> adminMemberVos = new ArrayList<AdminMemberVo>();
		//혼동하지 않기위해!!! adminMemberVo가 입력받은 정보고 adminMemberVos가 DB에서 긁어온 정보임 둘을 비교할것임!!
		
		try {
			
			adminMemberVos = jdbcTemplate.query(sql, new RowMapper<AdminMemberVo>() {
				
			//jdbcTemplate의 query를 이용해 회원을 조회하는 쿼리를 실행하고 결과를 adminMemberVos에 넣음
				@Override
				public AdminMemberVo mapRow(ResultSet rs, int rowNum) throws SQLException {
				//RowMapper를 구현한 익명클래스. 꼭 구현해야하는 추상메소드가 mapRow임
				//ResultSet과 행의 갯수를 파라미터로 받음
				
					AdminMemberVo adminMemberVo = new AdminMemberVo();
					
					adminMemberVo.setA_m_no(rs.getInt("a_m_no"));
					adminMemberVo.setA_m_approval(rs.getInt("a_m_approval"));
					adminMemberVo.setA_m_id(rs.getString("a_m_id"));
					adminMemberVo.setA_m_pw(rs.getString("a_m_pw"));
					adminMemberVo.setA_m_name(rs.getString("a_m_name"));
					adminMemberVo.setA_m_gender(rs.getString("a_m_gender"));
					adminMemberVo.setA_m_part(rs.getString("a_m_part"));
					adminMemberVo.setA_m_position(rs.getString("a_m_position"));
					adminMemberVo.setA_m_mail(rs.getString("a_m_mail"));
					adminMemberVo.setA_m_phone(rs.getString("a_m_phone"));
					adminMemberVo.setA_m_reg_date(rs.getString("a_m_reg_date"));
					adminMemberVo.setA_m_mod_date(rs.getString("a_m_mod_date"));
					
					//RowMapper를 구현한 클래스는 데이터베이스의 row를 VO 객체로 매핑함
					return adminMemberVo;
					//반환이 되고 나면 adminMemberVos에 List타입으로 저장됨
				}
				
			}, adminMemberVo.getA_m_id()); //←가 jdbcTemplate.query가 jdbcTemplate.query의 3번째 파라미터 요것이 물음표에 들어갈것
			
			if (!passwordEncoder.matches(adminMemberVo.getA_m_pw(), adminMemberVos.get(0).getA_m_pw()))
				adminMemberVos.clear();
			//matches는 비밀번호를 복호화해서 관리자가 입력한 비밀번호와 비교연산하고 결과를 반환
			//다르면 adminMemberVos는 지워야함
		} catch (Exception e) {
			e.printStackTrace();
			
		}
		
		return adminMemberVos.size() > 0 ? adminMemberVos.get(0) : null;
		//adminMemberVos의 길이가 0보다 크다면 로그인에 성공하고 관리자정보를 반환
		//0 이하라면 로그인 인증에 실패 서비스에 null을 반환
	}
	
	public List<AdminMemberVo> selectAdmins() {

		System.out.println("[AdminMemberDao] selectAdmins()");
		
		String sql = "SELECT * FROM tbl_admin_member";
		
		//테이블에서 모든 관리자를 조회
		
		List<AdminMemberVo> adminMemberVos = new ArrayList<AdminMemberVo>();
		
		try {
			
			adminMemberVos = jdbcTemplate.query(sql, new RowMapper<AdminMemberVo>() {
				
				@Override
				public AdminMemberVo mapRow(ResultSet rs, int rowNum) throws SQLException {
				
					AdminMemberVo adminMemberVo = new AdminMemberVo();
					
					adminMemberVo.setA_m_no(rs.getInt("a_m_no"));
					adminMemberVo.setA_m_approval(rs.getInt("a_m_approval"));
					adminMemberVo.setA_m_id(rs.getString("a_m_id"));
					adminMemberVo.setA_m_pw(rs.getString("a_m_pw"));
					adminMemberVo.setA_m_name(rs.getString("a_m_name"));
					adminMemberVo.setA_m_gender(rs.getString("a_m_gender"));
					adminMemberVo.setA_m_part(rs.getString("a_m_part"));
					adminMemberVo.setA_m_position(rs.getString("a_m_position"));
					adminMemberVo.setA_m_mail(rs.getString("a_m_mail"));
					adminMemberVo.setA_m_phone(rs.getString("a_m_phone"));
					adminMemberVo.setA_m_reg_date(rs.getString("a_m_reg_date"));
					adminMemberVo.setA_m_mod_date(rs.getString("a_m_mod_date"));
					
					return adminMemberVo;

				}
				//RowMapper를 이용해서 adminMemberVo리스트에 저장
				
			});

		} catch (Exception e) {
			e.printStackTrace();
			
		}
		return adminMemberVos;
		//이 메소드는 데이터베이스에서 모든 관리자를 조회한 후 서비스에 반환하고 서비스는 컨트롤러에게 반환함
	}
	
	public int updateAdminAccount(int a_m_no) {
		System.out.println("[AdminMemberDao] updateAdminAccount()");
		
		String sql = "UPDATE tbl_admin_member SET "
				+ "a_m_approval = 1 "
				+ "WHERE a_m_no = ?";
		
		int result = -1;
		
		try {
			result = jdbcTemplate.update(sql, a_m_no);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public int updateAdminAccount(AdminMemberVo adminMemberVo) {
		System.out.println("[AdminMemberDao] updateAdminAccount()");
		
		String sql = "UPDATE tbl_admin_member SET "
				+ "a_m_name = ?, "
				+ "a_m_gender = ?, "
				+ "a_m_part = ?, "
				+ "a_m_position = ?, "
				+ "a_m_mail = ?, "
				+ "a_m_phone = ?, "
				+ "a_m_mod_date = NOW() "
				+ "WHERE a_m_no = ?";
		int result = -1;
		try {
			result = jdbcTemplate.update(sql, 
					adminMemberVo.getA_m_name(),
					adminMemberVo.getA_m_gender(),
					adminMemberVo.getA_m_part(),
					adminMemberVo.getA_m_position(),
					adminMemberVo.getA_m_mail(),
					adminMemberVo.getA_m_phone(),
					adminMemberVo.getA_m_no()
					);
			//jdbcTemplate.update는 변경된 행의 갯수를 리턴함
		} catch(Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public AdminMemberVo selectAdmin(int a_m_no) {
		//관리자 정보를 조회하는 메소드 no로
		System.out.println("[AdminMemberDao] selectAdmin()");
		
		String sql =  "SELECT * FROM tbl_admin_member "
					+ "WHERE a_m_no = ?";
		
		List<AdminMemberVo> adminMemberVos = new ArrayList<AdminMemberVo>();
		
		try {
			adminMemberVos = jdbcTemplate.query(sql, new RowMapper<AdminMemberVo>() {			
				@Override
				public AdminMemberVo mapRow(ResultSet rs, int rowNum) throws SQLException {
				
					AdminMemberVo adminMemberVo = new AdminMemberVo();
					
					adminMemberVo.setA_m_no(rs.getInt("a_m_no"));
					adminMemberVo.setA_m_approval(rs.getInt("a_m_approval"));
					adminMemberVo.setA_m_id(rs.getString("a_m_id"));
					adminMemberVo.setA_m_pw(rs.getString("a_m_pw"));
					adminMemberVo.setA_m_name(rs.getString("a_m_name"));
					adminMemberVo.setA_m_gender(rs.getString("a_m_gender"));
					adminMemberVo.setA_m_part(rs.getString("a_m_part"));
					adminMemberVo.setA_m_position(rs.getString("a_m_position"));
					adminMemberVo.setA_m_mail(rs.getString("a_m_mail"));
					adminMemberVo.setA_m_phone(rs.getString("a_m_phone"));
					adminMemberVo.setA_m_reg_date(rs.getString("a_m_reg_date"));
					adminMemberVo.setA_m_mod_date(rs.getString("a_m_mod_date"));
					
					return adminMemberVo;
				}
				
			}, a_m_no);
			
		} catch (Exception e) {
			e.printStackTrace();	
		}
		return adminMemberVos.size() > 0 ? adminMemberVos.get(0) : null;
	}
	
	public AdminMemberVo selectAdmin(String a_m_id, String a_m_name, String a_m_mail) {
		//비밀번호를 찾기위한 selectAdmin. 받은 정보로 찾음
		System.out.println("[AdminMemberDao] selectAdmin()");
		
		String sql =  "SELECT * FROM tbl_admin_member "
				+ "WHERE a_m_id = ? AND a_m_name = ? AND a_m_mail = ?";
		
		List<AdminMemberVo> adminMemberVos = new ArrayList<AdminMemberVo>();
		
		try {
			adminMemberVos = jdbcTemplate.query(sql, new RowMapper<AdminMemberVo>() {			
				@Override
				public AdminMemberVo mapRow(ResultSet rs, int rowNum) throws SQLException {
				
					AdminMemberVo adminMemberVo = new AdminMemberVo();
					
					adminMemberVo.setA_m_no(rs.getInt("a_m_no"));
					adminMemberVo.setA_m_id(rs.getString("a_m_id"));
					adminMemberVo.setA_m_pw(rs.getString("a_m_pw"));
					adminMemberVo.setA_m_name(rs.getString("a_m_name"));
					adminMemberVo.setA_m_gender(rs.getString("a_m_gender"));
					adminMemberVo.setA_m_part(rs.getString("a_m_part"));
					adminMemberVo.setA_m_position(rs.getString("a_m_position"));
					adminMemberVo.setA_m_mail(rs.getString("a_m_mail"));
					adminMemberVo.setA_m_phone(rs.getString("a_m_phone"));
					adminMemberVo.setA_m_reg_date(rs.getString("a_m_reg_date"));
					adminMemberVo.setA_m_mod_date(rs.getString("a_m_mod_date"));
					
					return adminMemberVo;
				}
				
			}, a_m_id, a_m_name, a_m_mail);
			
		} catch (Exception e) {
			e.printStackTrace();	
		}
		return adminMemberVos.size() > 0 ? adminMemberVos.get(0) : null;
	}
	
	public int updatePassword(String a_m_id, String newPassword) {
		//서비스에서 만든 랜덤 비번을 받아와 DB에 저장
		System.out.println("[AdminMemberDao] updatePassword()");
		
		String sql = "UPDATE tbl_admin_member SET "
				+ "a_m_pw = ?, "
				+ "a_m_mod_date = NOW() "
				+ "WHERE a_m_id = ?";
		
		int result = -1;
		
		try {
			result = jdbcTemplate.update(sql, 
					passwordEncoder.encode(newPassword), a_m_id
					); //암호화 해서 저장
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
