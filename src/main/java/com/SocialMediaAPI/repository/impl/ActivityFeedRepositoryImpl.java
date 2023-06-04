package com.SocialMediaAPI.repository.impl;

import com.SocialMediaAPI.model.Post;
import com.SocialMediaAPI.model.User;
import com.SocialMediaAPI.repository.ActivityFeedRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

public class ActivityFeedRepositoryImpl implements ActivityFeedRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Post> findAllActivityFeedPostsOrderByCreatedDesc(User user) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Post> query = cb.createQuery(Post.class);
        Root<Post> post = query.from(Post.class);

        List<Predicate> predicates = new ArrayList<>();
        Path<User> author = post.get("user");
        for(var publisher: user.getPublishers()) {
            predicates.add(cb.equal(author, publisher));
        }
        query.
                select(post).
                where(cb.or(predicates.toArray(new Predicate[0]))).
                orderBy(cb.desc(post.get("created")));

        return entityManager.createQuery(query).getResultList();
    }

    @Override
    public List<Post> findAllActivityFeedPostsOrderByCreatedDescPageable(User user, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Post> query = cb.createQuery(Post.class);
        Root<Post> post = query.from(Post.class);

        List<Predicate> predicates = new ArrayList<>();
        Path<User> author = post.get("user");
        for(var publisher: user.getPublishers()) {
            predicates.add(cb.equal(author, publisher));
        }
        query.
                select(post).
                where(cb.or(predicates.toArray(new Predicate[0]))).
                orderBy(cb.desc(post.get("created")));

        return entityManager.createQuery(query).
                setMaxResults(pageable.getPageSize()).
                setFirstResult(pageable.getPageNumber() * pageable.getPageSize()).
                getResultList();
    }
}
