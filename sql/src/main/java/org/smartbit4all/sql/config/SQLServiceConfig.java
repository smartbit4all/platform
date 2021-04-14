/*******************************************************************************
 * Copyright (C) 2020 - 2020 it4all Hungary Kft.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.smartbit4all.sql.config;

import org.smartbit4all.domain.application.TimeManagementService;
import org.smartbit4all.domain.service.dataset.DataSetApi;
import org.smartbit4all.sql.application.TimeManagementServiceImpl;
import org.smartbit4all.sql.service.dataset.DataSetApiSql;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * The basic service for the SQL layer to provide database specific configurations.
 * 
 * @author Peter Boros
 */
@Configuration
@EnableScheduling
public class SQLServiceConfig {

  @Bean
  public TimeManagementService timeManagementService() {
    return new TimeManagementServiceImpl();
  }

  @Bean
  public DataSetApi dataSetApiSQL() {
    // TODO Later on we must use more api like this. So we cann't identify the api as such. We need
    // to add some routing in every call.
    return new DataSetApiSql();
  }

}
