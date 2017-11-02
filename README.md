# EasyCache
A convenient cache library for each kind of data structure ,based on Java dynamic proxy for Android

# Feature
1:支持任意自定义类型数据结构存储

2:支持RxJava2

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
       void cacheStudent(@Key("student") Student student);

       @LoadCache(key = "student")
       Observable<Student> loadStudent();

       @RemoveKey({"student", "students"})
       void removeStudent();

       @Clear
       void clearExample();

       @Cache
       void cacheStudents(@Key("students") HashSet<Student> students);

       @LoadCache(key = "students")
       HashSet<Student> loadStudents();

    }

Step3:cache or loadCache

        Student student = new Student();
        student.name = "zhangsan";
        student.age = 18;

        Student student1 = new Student();
        student1.name = "lisi";
        student1.age = 20;

        ExampleProxy exampleProxy = EasyCacheManager.getInstance().create(ExampleProxy.class);
        //cache
        exampleProxy.cacheStudent(student);
        //loadCache
        exampleProxy.loadStudent().observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(
                new Consumer<Student>() {
                    @Override
                    public void accept(Student cachedStudent) throws Exception {
                        Log.d("Student", "student:" + cachedStudent.name + " " + cachedStudent.age);
                    }
                },
                new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                }
        );


        HashSet<Student> students = new HashSet<>();
        students.add(student);
        students.add(student1);

        exampleProxy.cacheStudents(students);
        HashSet<Student> set = exampleProxy.loadStudents();
        for (Student s : set) {
            Log.d("Student", "student:" + s.name + " " + s.age);
        }

# Attention
As type erasure, Map is not support at this time. Of course, you can use List,Set,ArrayList,LinkedList,HashSet unquestionably.
To use this library, you need to add a GSON dependency

ex:compile 'com.google.code.gson:gson:2.2.4'
