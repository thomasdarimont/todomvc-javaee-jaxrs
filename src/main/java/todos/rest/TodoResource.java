package todos.rest;

import todos.Todo;
import todos.TodoService;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;

/**
 * <pre>
 * {@code
 *
 *   # list all todos
 *   curl -v -H "Accept: application/json" http://localhost:8080/todomvc-javaee/resources/todos
 *
 *   # Create new todo
 *   curl -v -X POST -H "Content-type: application/json" -d '{ "title":"Test TODO", "completed":false }' http://localhost:8080/todomvc-javaee/resources/todos
 *
 *   # show todo with id 1
 *   curl -v -H "Accept: application/json" http://localhost:8080/todomvc-javaee/resources/todos/1
 *
 *   # delete todo with id 1
 *   curl -v -X DELETE http://localhost:8080/todomvc-javaee/resources/todos/1
 *
 *   # search for all todo's with titles beginning with 'test'
 *   curl -v -d '{"title":"test"}' -H "Content-type: application/json" -H "Accept: application/json" http://localhost:8080/todomvc-javaee/resources/todos/search
 *
 *   # search for all todo's with completed = true
 *   curl -v -d '{"title":"test"}' -H "Content-type: application/json" -H "Accept: application/json" http://localhost:8080/todomvc-javaee/resources/todos/search
 * }
 * </pre>
 */
@Path("/todos")
public class TodoResource {

  @Context
  private UriInfo uriInfo;

  @Inject
  private TodoService todos;

  @POST
  @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
  @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
  public Response create(Todo todo) {

    Todo saved = this.todos.save(todo);

    URI newLocation = uriInfo.getAbsolutePathBuilder().path(saved.getId().toString()).build();
    return Response.created(newLocation).build();
  }

  @GET
  @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
  public Response list() {
    return Response.ok(this.todos.findAll()).build();
  }

  @GET
  @Path("/{id}")
  @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
  public Response getById(@PathParam("id") Long id) {

    Todo found = this.todos.getById(id);
    if (found == null) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }

    return Response.ok(found).build();
  }

  @PUT
  @Path("/{id}")
  @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
  @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
  public Response update(@PathParam("id") Long id, Todo todo) {

    Todo updated = todos.update(id, todo);

    if (updated == null) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }

    return Response.ok(updated).build();
  }

  @DELETE
  @Path("/{id}")
  @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
  public Response remove(@PathParam("id") Long id) {

    if (todos.deleteById(id)) {
      return Response.noContent().build();
    }

    return Response.status(Response.Status.NOT_FOUND).build();
  }

  @POST
  @Path("/search")
  @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
  public Response search(Todo example) {

    List<?> result = this.todos.findAllByExample(example);

    return Response.ok(result).build();
  }
}
