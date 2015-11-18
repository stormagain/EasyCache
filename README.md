# EasyCache
A SharedPreferences storage framework based on Java dynamic proxy

# Example
Step1:设置Context

    public class EasyCacheApplication extends Application {

        @Override
        public void onCreate() {
            super.onCreate();
            EasyCacheManager.getInstance().setup(this);
        }
    }

Step2:代理接口：

    @EasySpCache(name = "example_sp")
    public interface ExampleProxy {

        @Cache
        void cacheStudent(@Key(value = "student") Student student);

        @LoadCache(key = "student",getClassType = Student.class)
        Student loadStudent();

    }

Step3:存储或读取

    Student student=new Student();
    student.name="zhangsan";
    student.age=18;

    ExampleProxy exampleProxy= EasyCacheManager.getInstance().getCacheProxy().create(ExampleProxy.class);
    //cache
    exampleProxy.cacheStudent(student);
    //loadCache
    Student cachedStudent=exampleProxy.loadStudent();
    Log.d("Student","student:"+cachedStudent.name+" "+cachedStudent.age);

＃ Attention
To use this library, you need to add a GSON dependency
ex:compile 'com.google.code.gson:gson:2.2.4'
