package models;


import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@SuppressWarnings("ConstantConditions")
public class DepartmentTest {

    private Department newDept(){
        return new Department("Sales","We Strive for the best");
    }

    @Test
    public void initializeCorrectly(){
        Department department = newDept();
        assertTrue(department instanceof Department);
    }

    @Test
    public void getMethodsWorkCorrectly(){
        Department department = newDept();
        assertEquals("Sales",department.getName());
        assertEquals("We Strive for the best",department.getDescription());
    }

}