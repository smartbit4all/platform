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
package org.smartbit4all.domain.meta;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.smartbit4all.domain.meta.ExpressionFinder.FoundExpression;
import org.smartbit4all.domain.security.SecurityEntityConfiguration;
import org.smartbit4all.domain.security.UserAccountDef;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SB4ExpressionTest {

  protected static AnnotationConfigApplicationContext ctx;

  @BeforeAll
  static void setup() {
    ctx = new AnnotationConfigApplicationContext();
    ctx.register(MetaConfiguration.class);
    ctx.register(SecurityEntityConfiguration.class);
    ctx.refresh();
  }

  @AfterAll
  static void tearDown() {
    ctx.close();
  }

  @Test
  void expressionTest() throws Exception {
    UserAccountDef userAccountDef = ctx.getBean(UserAccountDef.class);
    ExpressionClause expression = userAccountDef.fullname().like("%oro%")
        .AND(userAccountDef.id().lt(100l)).OR(userAccountDef.primaryAddress().zipcode().eq("2030"));
    ExpressionClause express = userAccountDef.fullname().like("%oro%")
        .AND(userAccountDef.id().lt(100l)).OR(userAccountDef.primaryAddress().zipcode().eq("2030"));
    ExpressionClause or = expression.OR(express);


    // userAccountDef.ID().IN( userRoleDef.ISADMIN().equals(Boolean.TRUE) )
    //
    // userAccountDef.ROLES().IN( userRoleDef.ISADMIN().equals(Boolean.TRUE) );
    //
    // AddressDef addressDef;
    //
    // // These set can be joined
    // Expression2Operand<String> addressSet = addressDef.ZIPCODE().eq("2030");
    //
    // // Here we can decide to join this instead of running alone.
    // userAccountDef.PRIMARYADDRESS().IN( addressSet );
    //
    // // These are fix list to the SQL.
    // List<Integer> favorites = Arrays.asList(12, 34, 55);
    // userAccountDef.PRIMARYADDRESS().ID().IN( favorites );

    String actual = ExpressionToString.toString(expression);
    assertEquals("fullname LIKE \"%oro%\" AND id <= 100 OR primaryZipcode = \"2030\"",
        actual);
    System.out.println(actual);
  }

  @Test
  void expressionFindTest1() throws Exception {
    UserAccountDef userAccountDef = ctx.getBean(UserAccountDef.class);
    ExpressionClause exp1 = userAccountDef.fullname().eq("aaa")
        .AND(userAccountDef.id().lt(100l)).OR(userAccountDef.primaryAddress().zipcode().eq("2030"));
    ExpressionClause exp2 = userAccountDef.fullname().like("%bbb%")
        .AND(userAccountDef.id().gt(100l))
        .OR(userAccountDef.primaryAddress().zipcode().eq("2030").NOT());
    ExpressionClause or = exp1.OR(exp2);

    List<FoundExpression> list = ExpressionFinder.find(or, (Expression e) -> e == exp2);

    assertEquals(1, list.size());
    assertEquals(exp2, list.get(0).expression);
    assertEquals(or, list.get(0).parent);

  }

  @Test
  void expressionFindTest2() throws Exception {
    UserAccountDef userAccountDef = ctx.getBean(UserAccountDef.class);
    Expression in = userAccountDef.fullname().in(Arrays.asList("Aaa", "Bbb"));

    ExpressionClause restExp =
        userAccountDef.id().lt(100l).AND(userAccountDef.primaryAddress().zipcode().eq("2030"));

    ExpressionClause fullExp = restExp.AND(in);

    List<FoundExpression> list = ExpressionFinder.find(fullExp, (Expression e) -> e == in);

    assertEquals(1, list.size());
    assertEquals(in, list.get(0).expression);
    assertEquals(fullExp, list.get(0).parent);

  }

  @Test
  void expressionReplaceTest1() throws Exception {
    UserAccountDef userAccountDef = ctx.getBean(UserAccountDef.class);
    Expression in = userAccountDef.fullname().in(Arrays.asList("Aaa", "Bbb"));

    ExpressionClause restExp =
        userAccountDef.id().lt(100l).AND(userAccountDef.primaryAddress().zipcode().eq("2030"));

    ExpressionClause fullExp = restExp.AND(in);

    Expression newExp = userAccountDef.fullname().eq("Ccc");

    assertEquals("id <= 100 AND primaryZipcode = \"2030\" AND fullname IN {\"Aaa\", \"Bbb\"}",
        fullExp.toString());

    Expression result = ExpressionReplacer.replace(fullExp, in, newExp);

    assertEquals("id <= 100 AND primaryZipcode = \"2030\" AND fullname = \"Ccc\"",
        result.toString());

  }

}
