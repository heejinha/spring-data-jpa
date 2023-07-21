package study.datajpa.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import study.datajpa.dto.MemberDTO;
import study.datajpa.entity.Member;


public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    /**
     * 쿼리 어노테이션 선언하지 않아도 동일하게 적용 됨
     *  1순위가 Member객체에 선언된 findByUsername 네임드 쿼리를 서치 함. 
     *  없는 경우 2순위로 쿼리 메소드 생성
     * 
     * 하지만 실무에서는 거의 사용하지 않음
     *  => 리포지토리 메소드에 @Query 정의하는 방식을 많이 씀
     * 
     * [네임드쿼리의 장점]
     *   어플리케이션 로딩 시점에 네임드 쿼리 파싱을 먼저 실행 함.
     *   오류 발생한 경우 오류 발생 시키고 어플리케이션 다운 됨.
     */ 
    @Query(name = "Member.findByUsername")  
    List<Member> findByUsername(@Param("username")  String username);

    /**
     * 실무에서는 리포지토리에 @Query 메소드 정의하는 방식을 많이 사용 함.
     *   어플리케이션 로딩 시점에 쿼리 파싱하기 때문에 오류 확인 가능
     */
    @Query("select m from Member m where m.username = :username and m.age = :age")
    List<Member> findUser(@Param("username") String username, @Param("age") int age);

    @Query("select m.username from Member m")
    List<String> findUsernameList();

    @Query("select new study.datajpa.dto.MemberDTO(m.id, m.username, t.name) from Member m join m.team t")
    List<MemberDTO> findMemberDTO();

    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") List<String> names);

    List<Member> findListByUsername(String username);
    Member findOneByUsername(String username);
    Optional<Member> findOptionalByUsername(String username);

    Page<Member> findByAge(int age, Pageable pageable);

    Slice<Member> findSliceByAge(int age, Pageable pageable);

    @Modifying(clearAutomatically = true)
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    int updateBulkAge(@Param("age") int age);

    @Query("select m from Member m left join fetch m.team")
    List<Member> findMemberFetchJoin();

    // 공통 쿼리 메소드 오버라이드 + @EntityGraph
    @Override
    @EntityGraph(attributePaths = {"team"})
    List<Member> findAll();

    // 쿼리 메소드 + @EntityGraph
    @EntityGraph(attributePaths = {"team"})
    List<Member> findEntityGraphByUsername(String username);

    // JPQL + @EntityGraph
    @EntityGraph(attributePaths = {"team"})
    @Query("select m from Member m")
    List<Member> findMemberEntityGraph();

    // read only 는  dirty check(변경감지) x
    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Member findReadOnlyByUsername(String username);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Member findOneLockByUsername(String name);
}

 