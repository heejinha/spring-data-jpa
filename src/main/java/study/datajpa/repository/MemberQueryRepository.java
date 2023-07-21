package study.datajpa.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import study.datajpa.entity.Member;

@Repository
@RequiredArgsConstructor
public class MemberQueryRepository {
    private final EntityManager em; 

    List<Member> findAllMembers() {
        return em.createQuery("select m From Member m", Member.class)
                    .getResultList(); 
    }
    
}
