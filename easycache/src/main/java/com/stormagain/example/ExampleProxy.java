package com.stormagain.example;

import com.stormagain.easycache.Cache;
import com.stormagain.easycache.Clear;
import com.stormagain.easycache.EasySpCache;
import com.stormagain.easycache.Key;
import com.stormagain.easycache.LoadCache;
import com.stormagain.easycache.RemoveKey;

import java.util.ArrayList;

/**
 * Created by 37X21=777 on 15/11/18.
 */

@EasySpCache(name = "example_sp")
public interface ExampleProxy {

    @Cache
    void cacheStudent(@Key(value = "student") Student student);

    @LoadCache(key = "student", getClassType = Student.class)
    Student loadStudent();

    @RemoveKey(key = "student")
    void removeStudent();

    @Clear
    void clearExample();

    @Cache
    void cacheStudents(@Key(value = "students") ArrayList<Student> students);

    @LoadCache(key = "students", getClassType = Student[].class)
    ArrayList<Student> loadStudents();

}
