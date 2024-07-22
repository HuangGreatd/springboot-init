package com.juzipi.mapper;

import com.juzipi.domain.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author 73782
* @description 针对表【user(用户)】的数据库操作Mapper
* @createDate 2024-07-15 20:47:38
* @Entity generator.domain.User
*/
public interface UserMapper extends BaseMapper<User> {
	
	List<User> selectListUser(@Param("userName") String userName);
}




