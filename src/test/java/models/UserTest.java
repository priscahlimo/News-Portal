package models;

import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@SuppressWarnings("ConstantConditions")
public class UserTest {


    private User newUser(){
        return new User("The Star","Manager","Author","Media");
    }

    @Test
    public void initializeCorrectly(){
        User user = newUser();
        assertTrue(user instanceof User);
    }

    @Test
    public void getFunctionsWorkWell(){
        User user = newUser();
        assertEquals(user.getName(),newUser().getName());
        assertEquals(user.getPosition(),newUser().getPosition());
        assertEquals(user.getRole(),newUser().getRole());
        assertEquals(user.getDepartment(),newUser().getDepartment());
    }


}