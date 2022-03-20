package cn.com.OnlineTest.dao;

import cn.com.OnlineTest.model.Role;
import org.springframework.data.repository.CrudRepository;

public interface RoleDao extends CrudRepository<Role,Integer> {
}
