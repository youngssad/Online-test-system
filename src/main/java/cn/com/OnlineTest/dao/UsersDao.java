package cn.com.OnlineTest.dao;

import cn.com.OnlineTest.model.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
public interface UsersDao extends JpaRepository<Users,Integer> {
    @Query(value ="select * from users where roleid =?", nativeQuery = true)
    Page<Users> findTeacher(Integer roleid,Pageable pageable);
    @Query(value ="select * from users where username =?", nativeQuery = true)
     Users findByUsername(String username);
    @Query(value ="select * from users where userid =?", nativeQuery = true)
    Users findByUserid (Integer userid);
    @Query(value = "select * from users where classid = ? and roleid=2",nativeQuery = true)
    Page<Users> findByClassid(@Param("classid") Integer classid, Pageable pageable);
    @Modifying
    @Transactional
    @Query(value ="update Users set userpwd =? where userid =?",nativeQuery = true)
    Integer updatepwd(String userpwd,Integer userid);
    @Modifying
    @Transactional
    @Query(value ="delete from users where userid =?",nativeQuery = true)
    Integer deleteByUserid(Integer userid);
    @Modifying
    @Transactional
    @Query(value = "delete from users where classid = ? and roleid=2",nativeQuery = true)
    void deleteByClassid(Integer classid);

    @Override
    public Page<Users> findAll(Pageable pageable);

}
