package com.juzipi;

import com.juzipi.domain.entity.User;
import com.juzipi.mapper.UserMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @author juzipi
 * @date 2024/7/21 20:55
 */
@SpringBootTest(classes = MainApplication.class)
@RunWith(SpringRunner.class)
public class MainApplicationTest {
	
	@Autowired
	private UserMapper userMapper;
	
	@Test
	public void test(){
		String userAccount = "小张";
		List<User> userList = userMapper.selectListUser("小明");
		
		for (User user : userList) {
			System.out.println("user = " + user);
		}
		
	}
}
