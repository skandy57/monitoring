package sbt.qsecure.monitoring.session;

import java.io.Serializable;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberSession implements Serializable{
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final Long memberSq;

    public static MemberSession of(Long memberId) {
        return new MemberSession(memberId);
    }
}
