package org.etcsoft.devicemanagement.model;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.HashMap;

import static junit.framework.Assert.assertEquals;

/**
 * Created by mauro on 05/05/16.
 */
public class UserTest {
    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Test
    public void BuildUser()
    {
        String userName = "user";
        String password = "passwd";
        User user = User.builder()
                .user(userName)
                .passwd(password)
                .deviceName("deviceX")
                .property("design", "x-model")
                .build();

        assertEquals(userName, user.getUser());
        assertEquals(password, user.getPasswd());
        assertEquals(new ArrayList<String>()
                     {{
                         add("deviceX");
                     }},
                user.getDeviceNames();
        assertEquals(new HashMap<String, String>()
                     {{
                         put("design", "x-model");
                     }},
                user.getProperties())s;
    }
}
