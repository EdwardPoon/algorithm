package com.pan.test;

public class StaticMethod {

    public static void main(String... args) {
        Derived.f();
    }
}
class Base {
    static public void f() {System.out.println("Base");}
}
class Derived extends Base {
    static public void f() {System.out.println("Derive");}
}

