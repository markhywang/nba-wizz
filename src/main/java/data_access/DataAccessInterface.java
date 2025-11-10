package data_access;

import java.util.List;
import java.util.Optional;

public interface DataAccessInterface<T> {
    void save(T entity);
    Optional<T> findById(int id);
    List<T> findAll();
    void update(T entity);
    void delete(T entity);
}
