package org.smartbit4all.api.session;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.smartbit4all.api.org.bean.User;

public class UserSessionApiTestImpl implements UserSessionApi{

  private List<User> users;
  
  public UserSessionApiTestImpl() {
    users = new ArrayList<User>();
    users.add(createUser("Maglódi Ádám ", "adam_maglodi", "adam.maglodi@it4all.hu"));
    users.add(createUser("Suller Zoltán", "zoltan_suller", "zoltan.suller@it4all.hu"));
    users.add(createUser("Boros Péter", "peter_boros", "peter.boros@it4all.hu"));
    users.add(createUser("Máté Attila", "attila_mate", "attila.mate@it4all.hu"));
    users.add(createUser("Horváth Balázs", "balazs_horvath", "balazs.horvath@it4all.hu"));
    users.add(createUser("Szegedi Zoltán", "zoltan_szegedi", "zoltan.szegedi@it4all.hu"));
  
  }
  
  private User createUser(String name, String userName, String email) {
    URI userUri = URI.create("user:/" + userName);

    return new User().uri(userUri).name(name).username(userName).email(email);
  }
  
  @Override
  public User currentUser() {
    return users.get(0);
  }
}
