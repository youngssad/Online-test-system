package cn.com.OnlineTest.dao;

import cn.com.OnlineTest.model.Paper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


public interface PaperDao extends JpaRepository<Paper,Integer> {
    @Query(value ="select * from paper where eid =? and stype=?", nativeQuery = true)
    List<Paper> finbytype(Integer eid, Integer stype);

    @Query(value ="select * from paper where eid =?", nativeQuery = true)
    List<Paper> finbyEid(Integer eid);

    @Modifying
    @Transactional
    @Query(value="delete from paper where eid = ?",nativeQuery = true)
    void deleteByEid(Integer eid);

    @Modifying
    @Transactional
    @Query(value="delete from paper where cno = ?",nativeQuery = true)
    void deleteByCno(Integer cno);
}
