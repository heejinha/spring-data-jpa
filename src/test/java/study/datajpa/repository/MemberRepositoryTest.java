package study.datajpa.repository;

import static org.mockito.Mockito.lenient;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import study.datajpa.entity.Member;

@SpringBootTest
@Transactional // test 코드에서 @transactional 은 실행 후 모든 데이터 rollback
@Rollback(false)
public class MemberRepositoryTest {

    @Autowired MemberRepository memberRepository;

    @Test
    void testMember() {
        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);

        Member findMember = memberRepository.findById(savedMember.getId()).get();

        Assertions.assertThat(findMember.getId().equals(member.getId()));
        Assertions.assertThat(findMember.getUsername().equals(member.getUsername()));        
        Assertions.assertThat(findMember.equals(member));
    }

    @Test
    void basicCRUD() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");

        memberRepository.save(member1);
        memberRepository.save(member2);

        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();

        Assertions.assertThat(findMember1.equals(member1));
        Assertions.assertThat(findMember2.equals(member2));

        // 리스트 조회 검증
        List<Member> all = memberRepository.findAll();
        Assertions.assertThat(all.size()).isEqualTo(2);

        // 카운트 검증
        long count = memberRepository.count();
        Assertions.assertThat(count).isEqualTo(2);

        // 삭제 검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);

        long deleteCount = memberRepository.count();
        Assertions.assertThat(deleteCount).isEqualTo(0);
    }

    @Test
    void findByUsernameAndAgeGreaterThan() {

        Member m1 = new Member("memberC", 12);
        Member m2 = new Member("memberC", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> list = memberRepository.findByUsernameAndAgeGreaterThan("memberC", 15);

        Assertions.assertThat(list.get(0).getUsername()).isEqualTo("memberC");
        Assertions.assertThat(list.size()).isEqualTo(1);

    }
}
