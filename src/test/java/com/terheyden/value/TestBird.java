package com.terheyden.value;

/**
 * TestBird class.
 */
public class TestBird extends TestAnimal {

    private final int wingCount;

    public TestBird(String name, int age, int wingCount) {
        super(name, age);
        this.wingCount = wingCount;
    }

    public int getWingCount() {
        return wingCount;
    }
}
