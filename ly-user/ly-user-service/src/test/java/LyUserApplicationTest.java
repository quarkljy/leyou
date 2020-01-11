//import com.leyou.LyUserApplication;
//import com.leyou.user.mapper.UserMapper;
//import com.leyou.user.pojo.User;
//import com.leyou.user.util.CodecUtils;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import java.util.Date;
//
//@RunWith(SpringRunner.class)
//@SpringBootTest(classes = LyUserApplication.class) //需要写主类
//public class LyUserApplicationTest {
//    @Autowired
//    private UserMapper userMapper;
//
//    @Test
//    public void  addUser(){
//        User user = new User();
//        user.setUsername("quark");
//        user.setPassword("quark");
//        user.setPhone("18888888888");
//        // 生成盐
//        String salt = CodecUtils.generateSalt();
//        user.setSalt(salt);
//        // 对密码加密
//        user.setPassword(CodecUtils.md5Hex(user.getPassword(), salt));
//        // 写入数据库
//        user.setCreated(new Date());
//        userMapper.insert(user);
//
//    }
//}
