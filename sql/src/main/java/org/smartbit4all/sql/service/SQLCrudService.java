package org.smartbit4all.sql.service;

import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.service.CrudService;
import org.smartbit4all.domain.service.modify.Create;
import org.smartbit4all.domain.service.modify.Delete;
import org.smartbit4all.domain.service.modify.Update;
import org.smartbit4all.domain.service.query.Query;
import org.smartbit4all.sql.service.modify.SQLCreate;
import org.smartbit4all.sql.service.modify.SQLDelete;
import org.smartbit4all.sql.service.modify.SQLUpdate;
import org.smartbit4all.sql.service.query.SQLQuery;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Return the implementation of the CRUD functions at SQL level.
 * 
 * @author Zoltan Suller
 */
public class SQLCrudService<E extends EntityDefinition> implements CrudService<E> {

  protected E entityDef;
  protected JdbcTemplate jdbcTemplate;
  protected String name;

  public SQLCrudService(JdbcTemplate jdbcTemplate, E entityDef, String name) {
    super();
    this.jdbcTemplate = jdbcTemplate;
    this.entityDef = entityDef;
    this.name = name;
  }

  @Override
  public Query<E> query() {
    SQLQuery<E> query = new SQLQuery<>(jdbcTemplate);
    return query.from(entityDef);
  }

  @Override
  public Create<E> create() {
    return new SQLCreate<>(jdbcTemplate, entityDef);
  }


  @Override
  public Update update() {
    return new SQLUpdate(jdbcTemplate, entityDef);
  }

  @Override
  public Delete delete() {
    return new SQLDelete(jdbcTemplate, entityDef);
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public E entityDef() {
    return entityDef;
  }

}
