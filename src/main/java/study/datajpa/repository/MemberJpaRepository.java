package study.datajpa.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import study.datajpa.entity.Member;

@Repository
public class MemberJpaRepository {

    @PersistenceContext
    private EntityManager em;

    public Member save(Member member) {
        em.persist(member);
        return member;
    }

    public void delete(Member member) {
        em.remove(member);
    }

    public Member find(Long id) {
        return em.find(Member.class, id);
    }

    public Optional<Member> findById(Long id) {
        Member member = em.find(Member.class, id);
        return Optional.ofNullable(member); 
    }

    public long count() {
        return em.createQuery("select count(m) from Member m", Long.class)
                    .getSingleResult();
    }

    public List<Member> findAll() {
        // jpql sql
        return em.createQuery("select m from Member m", Member.class)
                    .getResultList();
    }

    public List<Member> findByUsername(String username) {
        // name =>  Member.class 에 선언한 @NamedQuery > name 값 적용
        // 해당 name 값에 선언된 쿼리 호출
        return em.createNamedQuery("Member.findByUsername", Member.class)
                .setParameter("username", username)
                .getResultList();
    }

    public List<Member> findByPage(int age, int offset, int limit) {
        return em.createQuery("select m from Member m where m.age = :age order by m.username desc", Member.class)
                    .setParameter("age", age)
                    .setFirstResult(offset)
                    .setMaxResults(limit)
                    .getResultList();

    }
    
    public long totalCount(int age) {
        return em.createQuery("select count(m) from Member m where m.age = :age", Long.class)
                    .setParameter("age", age)
                    .getSingleResult();
    }
}
