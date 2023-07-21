package study.datajpa.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import study.datajpa.entity.Member;

@Getter @Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MemberDTO {
    private Long id;
    private String username;
    private String teamName;

    public MemberDTO(Member member) {
        id = member.getId();
        username = member.getUsername();
        teamName = null;
    }
    
}
