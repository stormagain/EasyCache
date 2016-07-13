# EasyCache
A convenient cache framework for each kind of data structure ,based on Java dynamic proxy for Android

# Example
Step1:setup Context (this step can be skipped although that is not recommend)

    public class EasyCacheApplication extends Application {

        @Override
        public void onCreate() {
            super.onCreate();
            EasyCacheManager.getInstance().setup(this);
        }
    }

Step2:Config your interface (annotation EasyCache also can be skipped)

    @EasyCache(name = "example", type = Type.SHARED_PREFERENCE)
    public interface ExampleProxy {

        @Cache
        void cacheStudent(@Key(value = "student") Student student);

        @LoadCache(key = "student", classType = Student.class)
        Student loadStudent();

        @RemoveKey(key = "student")
        void removeStudent();

        @Clear
        void clearExample();

        @Cache
        void cacheStudents(@Key(value = "students") HashSet<Student> students);

        @LoadCache(key = "students", classType = Student.class, collectionType = HashSet.class)
        HashSet<Student> loadStudents();

    }

Step3:cache or loadCache

    Student student=new Student();
    student.name="zhangsan";
    student.age=18;

    ExampleProxy exampleProxy= EasyCacheManager.getInstance().create(ExampleProxy.class);
    //cache
    exampleProxy.cacheStudent(student);
    //loadCache
    Student cachedStudent=exampleProxy.loadStudent();
    Log.d("Student","student:"+cachedStudent.name+" "+cachedStudent.age);

# Attention
As type erasure, Map is not support at this time. Of course, you can use List,Set,ArrayList,LinkedList,HashSet unquestionably.
To use this library, you need to add a GSON dependency

ex:compile 'com.google.code.gson:gson:2.2.4'
