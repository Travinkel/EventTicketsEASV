// GenericDAOFactory.java
package org.example.eventticketsystem.dal.dao;

import org.reflections.Reflections;


import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class GenericDAOFactory {
    private static final Map<Class<?>, GenericDAO<?>> daoMap = new HashMap<>();

    static {
        Reflections reflections = new Reflections("org.example.eventticketsystem.dal.dao");
        Set<Class<? extends GenericDAO>> daoClasses = reflections.getSubTypesOf(GenericDAO.class);

        for (Class<? extends GenericDAO> daoClass : daoClasses) {
            try {
                GenericDAO<?> daoInstance = daoClass.getDeclaredConstructor().newInstance();
                Class<?> entityClass = (Class<?>) ((java.lang.reflect.ParameterizedType) daoClass.getGenericInterfaces()[0]).getActualTypeArguments()[0];
                daoMap.put(entityClass, daoInstance);
            } catch (Exception e) {
                throw new RuntimeException("Failed to initialize DAO: " + daoClass.getName(), e);
            }
        }
    }
    public static <T> GenericDAO<T> getDAO(Class<T> entityClass) {
        GenericDAO<T> dao = (GenericDAO<T>) daoMap.get(entityClass);
        if (dao == null) {
            throw new IllegalArgumentException("No DAO found for class: " + entityClass.getName());
        }
        return dao;
    }
}
