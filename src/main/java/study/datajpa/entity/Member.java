package study.datajpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
// entity 는 기본 생성자가 기본적으로 생성되어야 함. accessLevel 은 protected 로 설정
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "username", "age"})

@NamedQuery(
    name = "Member.findByUsername",
    query = "select m from Member m where m.username = :username" 
)
public class Member {

    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String username;

    private int age;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    public Member(String username) { this.username = username; }

    public void changeTeam(Team team) {
        this.team = team;
        team.getMembers().add(this);

    }

    public Member(String username, int age) {
        this.username = username;
        this.age = age;
    }


    public Member(String username, int age, Team team) {
        this.username = username;
        this.age = age;
        if (team != null) this.team = team;
    }

    
}
