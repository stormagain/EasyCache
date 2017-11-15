package com.stormagain.example;

import com.stormagain.easycache.annotation.Cache;
import com.stormagain.easycache.annotation.Clear;
import com.stormagain.easycache.annotation.EasyCache;
import com.stormagain.easycache.annotation.Key;
import com.stormagain.easycache.annotation.LoadCache;
import com.stormagain.easycache.annotation.RemoveKey;
import com.stormagain.easycache.Type;

import java.util.HashSet;

import io.reactivex.Observable;

/**
 * Created by 37X21=777 on 15/11/18.
 */
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
