package org.smartbit4all.domain.utility.crud;

import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.service.modify.Create;
import org.smartbit4all.domain.service.modify.Delete;
import org.smartbit4all.domain.service.modify.Update;

public class Crud {

  public static <E extends EntityDefinition> CrudRead<E> read(E entityDef) {
    return new CrudRead<>(entityDef);
  }

  public static <E extends EntityDefinition> void update(TableData<E> tableData) throws Exception {
    @SuppressWarnings("unchecked")
    Update<E> update = (Update<E>) tableData.entity().services().crud().update();
    update.values(tableData).execute();
  }

  public static <E extends EntityDefinition> void create(TableData<E> tableData) throws Exception {
    @SuppressWarnings("unchecked")
    Create<E> create = (Create<E>) tableData.entity().services().crud().create();
    create.values(tableData).execute();
  }

  public static <E extends EntityDefinition> void delete(TableData<E> tableData) throws Exception {
    @SuppressWarnings("unchecked")
    Delete<E> delete = (Delete<E>) tableData.entity().services().crud().delete();
    delete.by(tableData).execute();
  }

}
