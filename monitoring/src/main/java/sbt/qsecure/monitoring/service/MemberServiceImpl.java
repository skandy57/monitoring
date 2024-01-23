package sbt.qsecure.monitoring.service;

import java.lang.reflect.Field;
import java.nio.file.AccessDeniedException;
import java.util.Iterator;
import java.util.Objects;
import java.util.stream.Stream;

import org.apache.ibatis.javassist.bytecode.stackmap.TypeData.ClassName;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionManager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sbt.qsecure.monitoring.mapper.MemberMapper;
import sbt.qsecure.monitoring.vo.MemberVO;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {


	private final MemberMapper memberMapper;
//	private final PasswordEncoder passwordEncoder;
	
	@Override
	public MemberVO login(String userId, String passwd) {
		log.info("MemberServiceImpl.login_Try");
		log.info("userId : " +userId+ " passwd : "+passwd);
		try {
			MemberVO vo = memberMapper.login(userId, passwd);
			return vo;
		} catch (Exception e) {
			log.error("MemberServiceImpl.login_FAIL : "+e.getMessage());
			return null;
		}
	}
	
	@Override
	public MemberVO updateMember(MemberVO vo) {
	    log.info("MemberServiceImpl.updateMember_Try");

	    MemberVO member = memberMapper.getMemberById(vo.userId());

	    if (member == null) {
	        log.error("MemberServiceImpl.updateMember_FAIL: " + vo.userId()+" 은 없는 계정");
	        return null;
	    }
	    
	    Stream.of(MemberVO.class.getDeclaredFields())
	            .peek(field -> field.setAccessible(true))
	            .filter(field -> {
	                try {
	                    return !Objects.equals(field.get(vo), field.get(member));
	                } catch (IllegalAccessException e) {
	                    log.error("MemberServiceImpl.updateMember_FAIL: " + e.getMessage());
	                    return false;
	                }
	            })
	            .forEach(field -> {
	                try {
	                    field.set(member, field.get(vo));
	                } catch (IllegalAccessException e) {
	                    log.error("MemberServiceImpl.updateMember_FAIL: " + e.getMessage());
	                }
	            });

	    try {
	        memberMapper.updateMember(member);
	        log.info("MemberServiceImpl.updateMember_SUCCESS");
	        return vo;
	    } catch (Exception e) {
	        log.error("MemberServiceImpl.updateMember_FAIL: " + e.getMessage());
	        return null;
	    }
	}

	@Override
	public int deleteMember(MemberVO vo) throws Exception {
		
//		if (isAdmin(vo)) {
//			memberMapper.deleteMember(vo);
//			return SUCCESS;
//		}else {
//			log.warn("MemberServiceImpl.deleteMmeber_Fail: 어드민 계정이 아닌 '{}'이 삭제 시도",vo.getUserId());
//			throw new AccessDeniedException("권한이 있는 계정만이 계정삭제기능을 사용할 수 있음");
//		}
		return 0;
	}

//	@Override
//	public int updateAuth(MemberVO vo, String targetMember,String authGrade) {
//	    if (!isAdmin(vo)) {
//	    	log.info("권한부여권한이 없는 "+vo.getUserId()+"이 "+targetMember);
//	        return FAIL;
//	    }
//
//	    MemberVO member = memberMapper.getMemberById(targetMember);
//	    member.setAuthGrade(authGrade);
//	    try {
//		    memberMapper.updateAuth(targetMember, authGrade);
//		} catch (Exception e) {
//			e.printStackTrace();
//			return FAIL;
//		}
//	    return SUCCESS;
//	}

	

	@Override
	public MemberVO getMemberById(String userId) {
		return memberMapper.getMemberById(userId);
	}



	@Override
	public boolean isAdmin(MemberVO vo) {
		if (vo.authGrade()=="admin") {
			return true;
		}else {
			return false;
		}
	}

	@Override
	public MemberVO regMember(MemberVO vo) {
		// TODO Auto-generated method stub
		return null;
	}

//	@Override
//	public MemberVO updateMember(MemberVO vo) {
//
//		return vo;
//	}
	public MemberVO loginTest(String userId, String passwd) {
		return memberMapper.loginTest(userId, passwd);
	}
}
