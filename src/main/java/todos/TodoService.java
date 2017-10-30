package todos;

import org.hibernate.Session;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.MatchMode;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Collections;
import java.util.List;

@Stateless
public class TodoService {

  @PersistenceContext(unitName = "default")
  private EntityManager em;

  public Todo save(Todo todo) {

    if (todo.getId() == null) {
      em.persist(todo);
      return todo;
    }

    return em.merge(todo);
  }

  public Todo getById(Long id) {
    return em.find(Todo.class, id);
  }

  public List<Todo> findAll() {
    return em.createQuery("from " + Todo.class.getName(), Todo.class).getResultList();
  }

  public boolean deleteById(Long id) {

    Todo toDelete = this.getById(id);
    if (toDelete == null) {
      return false;
    }

    em.remove(toDelete);

    return true;
  }

  public Todo update(Long id, Todo todo) {

    Todo updated = getById(id);
    if (updated == null) {
      return null;
    }

    if (todo.getCompleted() != null) {
      updated.setCompleted(todo.getCompleted());
    }

    if (todo.getTitle() != null) {
      updated.setTitle(todo.getTitle());
    }

    return em.merge(updated);
  }

  public List<?> findAllByExample(Todo todo) {

    if (todo.getId() != null) {
      return Collections.singletonList(getById(todo.getId()));
    }

    // Hibernate specific API, not part of JPA 2.1

    Session session = (Session) em.getDelegate();
    Example todoExample = Example.create(todo).enableLike(MatchMode.START).excludeZeroes();

    return session.createCriteria(Todo.class).add(todoExample).list();
  }
}
