/*******************************************************************************
 * Copyright (C) 2020 - 2020 it4all Hungary Kft.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.smartbit4all.domain.meta;

import java.util.Map;
import java.util.Map.Entry;
import org.smartbit4all.domain.data.DataRow;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.data.TableDatas;
import org.smartbit4all.domain.service.CrudService;
import org.smartbit4all.domain.service.CrudServiceFactory;
import org.smartbit4all.domain.service.identifier.IdentifierService;
import org.smartbit4all.domain.service.identifier.NextIdentifier;
import org.smartbit4all.domain.service.modify.Create;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class EntityServiceImpl<E extends EntityDefinition>
    implements EntityService<E>, ApplicationContextAware {

  protected CrudService<E> crudService;

  protected E entityDef;

  private ApplicationContext ctx;

  public EntityServiceImpl(E entityDef) {
    this.entityDef = entityDef;
  }

  @Override
  public CrudService<E> crud() {
    if (crudService == null) {
      initCrudService();
    }
    return crudService;
  }

  protected void initCrudService() {
    if (crudService == null) {
      CrudServiceFactory crudServiceFactory =
          (CrudServiceFactory) ctx.getBean(CrudServiceFactory.SERVICE_NAME);
      String serviceName = entityDef.entityDefName() + "Service";
      crudService = crudServiceFactory.createCrudService(entityDef, serviceName);
    }
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.ctx = applicationContext;
  }

  @Override
  public <T> TableData<E> lockOrCreateAndLock(Property<T> uniqueProperty, T uniqueValue,
      String sequenceName, Map<Property<?>, Object> extraValues) throws Exception {
    // We try to lock the records by querying with lock. We must collect the values of the unique
    // columns.
    TableData<E> result = new TableData<>(entityDef);
    crud().query().all().where(uniqueProperty.eq(uniqueValue)).lock().into(result).execute();
    if (result.isEmpty()) {
      // The record is missing from the database so we try to insert it
      IdentifierService identifierService = ctx.getBean(IdentifierService.class);
      NextIdentifier next = identifierService.next();
      next.setInput(sequenceName);
      next.execute();
      TableData<E> td = TableDatas.of(entityDef, entityDef.allProperties());
      DataRow row = td.addRow();
      row.set(uniqueProperty, uniqueValue);
      // TODO We don't have the correct interface for the multiple primary key!
      for (Property<?> primaryProperty : entityDef.PRIMARYKEYDEF()) {
        row.setObject(primaryProperty, next.output());
      }
      if (extraValues != null) {
        for (Entry<Property<?>, Object> entry : extraValues.entrySet()) {
          row.setObject(entry.getKey(), entry.getValue());
        }
      }
      try {
        Create<E> create = (Create<E>) entityDef.services().crud().create();
        create.values(td).execute();
        // In this case we managed to insert the record. Therefore it's already locked until the end
        // of the transaction.
        return td;
      } catch (Exception e) {
        // We failed to insert the record. Someone else could insert it. We can call ourselves
        // recursively to query.
        // TODO Check if it's the unique index violation exception from the database. We need to
        // catch the DuplicateKeyException but it's tx module!
        return lockOrCreateAndLock(uniqueProperty, uniqueValue, sequenceName, extraValues);
      }
    }
    return result;
  }

}
