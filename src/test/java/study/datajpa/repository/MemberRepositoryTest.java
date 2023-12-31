package study.datajpa.repository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import study.datajpa.dto.MemberDTO;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

@SpringBootTest
@Transactional // test 코드에서 @transactional 은 실행 후 모든 데이터 rollback
@Rollback(false)
public class MemberRepositoryTest {

    @Autowired MemberRepository memberRepository;
    @Autowired TeamRepository teamRepository;
    @PersistenceContext EntityManager em;
    @Autowired MemberQueryRepository memberQueryRepository;

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
    void testFindByUsernameAndAgeGreaterThan() {

        Member m1 = new Member("memberC", 12);
        Member m2 = new Member("memberC", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> list = memberRepository.findByUsernameAndAgeGreaterThan("memberC", 15);

        Assertions.assertThat(list.get(0).getUsername()).isEqualTo("memberC");
        Assertions.assertThat(list.size()).isEqualTo(1);
    }

    @Test
    void testNamedQuery() {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);

        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> result = memberRepository.findByUsername("AAA");
        Member findMember = result.get(0);
        Assertions.assertThat(findMember).isEqualTo(member1);
    }

    @Test
    void testQuery() {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);

        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> result = memberRepository.findUser("AAA", 10);
        Assertions.assertThat(result.get(0)).isEqualTo(member1);
    }

    @Test
    void testFindUsernameList() {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);

        memberRepository.save(member1);
        memberRepository.save(member2);

        List<String> list = memberRepository.findUsernameList();

        for (String name : list) {
            System.out.println("===========name : " + name);
        }
    }

    @Test
    void testFindMemberDTO() {
        Team team = new Team("teamA");
        teamRepository.save(team);

        Member member1 = new Member("AAA", 10, team);
        memberRepository.save(member1);

        List<MemberDTO> dto = memberRepository.findMemberDTO();
        for (MemberDTO memberDTO : dto) {
            System.out.println(memberDTO);
        }
    }

    @Test
    void testFindByNames() {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);

        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> list = memberRepository.findByNames(Arrays.asList("AAA", "BBB"));
        System.out.println(list.get(0));
    }

    @Test
    void testReturnType() {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);

        memberRepository.save(member1);
        memberRepository.save(member2);

        Optional<Member> result = memberRepository.findOptionalByUsername("AAA");
        // Optional<Member> result = memberRepository.findOptionalByUsername("AAB");
        System.out.println(result.orElse(new Member("no data")));
    }

    @Test
    void testPaging() {
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
        int offset = 0;
        int limit = 3;

        PageRequest pageRequest = PageRequest.of(
            offset, 
            limit, 
            Sort.by(Sort.Direction.DESC, "username")
        );

        // when
        Page<Member> result = memberRepository.findByAge(age, pageRequest);
        Page<MemberDTO> resultDTO = result.map(member -> new MemberDTO(member.getId(), member.getUsername(), null));

        // then
        Assertions.assertThat(result.getContent().size()).isEqualTo(3);
        Assertions.assertThat(result.getTotalElements()).isEqualTo(5);
    }

    @Test
    void testSlice() {
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
        int offset = 0;
        int limit = 3;

        PageRequest pageRequest = PageRequest.of(
            offset, 
            limit, 
            Sort.by(Sort.Direction.DESC, "username")
        );

        // when
        Slice<Member> result = memberRepository.findSliceByAge(age, pageRequest);

        // then
        Assertions.assertThat(result.getContent().size()).isEqualTo(3);
        Assertions.assertThat(result.hasNext()).isEqualTo(true);
        Assertions.assertThat(result.hasPrevious()).isEqualTo(false);

    }

    @Test
    void bulkUpdate() {
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 19));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 21));
        memberRepository.save(new Member("member5", 40));

        // when
        int count = memberRepository.updateBulkAge(20);
  
        List<Member> list = memberRepository.findByUsername("member5");
        System.out.println("======== " + list.get(0).getAge());

        // then
        Assertions.assertThat(count).isEqualTo(3);
    }

    @Test
    public void findMemberLazy() {
        // given
        // member1 -> teamA, member2 -> teamB

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        // when N + 1
        /**
         *
         * select member 쿼리만 실행. team 객체는 Proxy 객체로 셋팅 됨           
         *   -> findMemberFetchJoin() 이용해서 fetch join 사용 가능
         *   -> @EntityGraph 이용해서 fetch 조인 가능
         *   
         **/ 
        List<Member> members = memberRepository.findAll();
        // left join fetch 를 이용해 한꺼번에 member, team 객체 쿼리 해옴
        // List<Member> members = memberRepository.findMemberFetchJoin();


        for (Member member : members) {
            System.out.println("member = " + member);
            // proxy 객체만 조회 됨.
            // member.team class = class study.datajpa.entity.Team$HibernateProxy$JAWTOiiD
            System.out.println("member.team class = " + member.getTeam().getClass());
            // member 의 team 정보 호출 시, lazy loading 으로 실제 select team 쿼리 실행 됨
            System.out.println("member.team = " + member.getTeam().getName());
        }
    }

    @Test
    void queryHint() {

        // given
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1);
        em.flush();
        em.clear();

        // when
        // Member findMember =  memberRepository.findOneByUsername("member1");
        Member findMember =  memberRepository.findReadOnlyByUsername("member1");
        findMember.setUsername("member2");

        em.flush();
    }

    @Test
    void queryLock() {

        // given
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1);
        em.flush();
        em.clear();

        // when
        Member findMember =  memberRepository.findOneLockByUsername("member1");
    }

    @Test
    void callCustom() {
        // memberRepository.findMemberCustom();
        memberQueryRepository.findAllMembers();
    }
}
