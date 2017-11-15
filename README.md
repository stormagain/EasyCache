# EasyCache
类似Retrofit风格的缓存库

# Feature
1:支持任意自定义类型数据结构存储

2:支持RxJava2

# Example
Step1:设置Context

    public class EasyCacheApplication extends Application {

        @Override
        public void onCreate() {
            super.onCreate();
            EasyCacheManager.getInstance().setup(this);
        }
    }

Step2:配置interface

    @EasyCache(name = "example", type = Type.SHARED_PREFERENCE)
    public interface ExampleProxy {

        @Cache
        boolean cacheStudent(@Key("student") Student student);

        @LoadCache(key = "student")
        Observable<Student> loadStudent();

        @RemoveKey({"student", "students"})
        void removeStudent();

        @Clear
        void clearExample();

        @Cache
        Observable<Boolean> cacheStudents(@Key("students") HashSet<Student> students);

        @LoadCache(key = "students")
        HashSet<Student> loadStudents();

    }

Step3:存储、读取

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
                        throwable.printStackTrace();
                    }
                }
        );


        HashSet<Student> students = new HashSet<>();
        students.add(student);
        students.add(student1);

        exampleProxy.cacheStudents(students).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(
                new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {

                    }
                },
                new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                }
        );

        HashSet<Student> set = exampleProxy.loadStudents();
        for (Student s : set) {
            Log.d("Student", "student:" + s.name + " " + s.age);
        }

# Attention
需依赖google的Gson库

ex:compile 'com.google.code.gson:gson:2.2.4'
