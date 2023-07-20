package com.bfs.authenticationservice.dao;

import com.bfs.authenticationservice.domain.User;
import org.hibernate.query.Query;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import java.beans.PropertyDescriptor;
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

    public Boolean updateUser(User updatedUser) {

        // Load the existing user
        User existingUser = this.getCurrentSession().get(User.class, updatedUser.getUserId());

        if (existingUser != null) {
            BeanWrapper existingUserWrapper = new BeanWrapperImpl(existingUser);
            BeanWrapper newUserWrapper = new BeanWrapperImpl(updatedUser);

            for (PropertyDescriptor descriptor : existingUserWrapper.getPropertyDescriptors()) {
                String propertyName = descriptor.getName();

                Object oldValue = existingUserWrapper.getPropertyValue(propertyName);
                Object newValue = newUserWrapper.getPropertyValue(propertyName);

                if (oldValue != null && newValue != null && !oldValue.equals(newValue)) {
                    existingUserWrapper.setPropertyValue(propertyName, newValue);
                }
            }

            return true;

        } else {

            return false;

        }
    }

}
