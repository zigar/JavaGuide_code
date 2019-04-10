import java.io.*;

public class TransientTest {

    public static void main(String[] args) {
        ExternalizableTest et = new ExternalizableTest();
        User user = new User();
        user.setUsername("Zigar");
        user.setPasswd("123456");
        user.setAge((short)30);
        System.out.println("read before Serializable: ");
        System.out.println("username: " + user.getUsername());
        System.out.println("password: " + user.getPasswd());
        System.out.println("age: " + user.getAge());

        try {
            ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream("/tmp/user.txt"));
            os.writeObject(user); // 将User对象写进文件
            os.flush();
            os.close();
            ObjectOutput out = new ObjectOutputStream(new FileOutputStream(new File("/tmp/test")));
            out.writeObject(et);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            //在反序列化之前改变age的值
            User.age = (short)40;

            ObjectInputStream is = new ObjectInputStream(new FileInputStream("/tmp/user.txt"));
            user = (User) is.readObject(); // 从流中读取User的数据
            is.close();

            System.out.println("\nread after Serializable: ");
            System.out.println("username: " + user.getUsername());
            System.err.println("password: " + user.getPasswd());
            System.err.println("age: " + user.getAge() + " //一个静态变量不管是否被transient修饰，均不能被序列化");
            ObjectInput in = new ObjectInputStream(new FileInputStream(new File("/tmp/test")));
            ExternalizableTest et_in = (ExternalizableTest) in.readObject();
            System.out.println(et_in.content);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

}

class User implements Serializable{

    private static final long serialVersionUID = -2513747641863637392L;

    private String username;
    private transient String passwd;
    static short age;

    Short getAge() {
        return age;
    }

    void setAge(Short age) {
        this.age = age;
    }



    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }
}

class ExternalizableTest implements Externalizable {
    private static final long serialVersionUID = -2513747641863637393L;

    public transient String content = "Content: Yes, I'll always be serialized whether or not I'm transient";

    public ExternalizableTest(){

    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(content);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        content = (String) in.readObject();
    }
}