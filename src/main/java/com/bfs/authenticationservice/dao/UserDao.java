package com.bfs.authenticationservice.dao;

import com.bfs.authenticationservice.domain.User;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import java.util.Optional;

@Repository
public class UserDao extends AbstractHibernateDao<User> {

    @Autowired
    public UserDao() {
        setClazz(User.class);
    }

    public Optional<User> loadUserByEmail(String email){
        String hql = "FROM User u WHERE u.email = :email";
        Query query = getCurrentSession().createQuery(hql, User.class);
        query.setParameter("email", email);
        User res;

        try {
            res = (User) query.getSingleResult();
        } catch (NoResultException re) {
            res = null;
        }

        return Optional.ofNullable(res);
    }

    public void addUser(User user) {
        this.add(user);
    }

}
