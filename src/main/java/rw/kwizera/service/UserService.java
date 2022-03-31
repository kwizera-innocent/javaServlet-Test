package rw.kwizera.service;

import rw.kwizera.LoginServerlet;
import rw.kwizera.model.*;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public class UserService {
    private ConcurrentMap<String, User> users;
    private AtomicInteger key;

    public UserService() {
        this.users = new ConcurrentHashMap<>();
        key = new AtomicInteger();

        this.addAdmin(
                new Admin("admin","adminpas92","Admin","lastadmin", eGender.MALE,32, "0788694532","admin")
        );
        this.addGuest(
                new Guest("guest","geus1","Guest","lastguest", eGender.MALE,21,"0788694532","guest")
        );

    }
    public Admin addAdmin(Admin admin) {
        Integer id = key.incrementAndGet();
        admin.setId(id);
        Admin admin1 = (Admin) admin.register(admin);
        if(admin1 == null)
            return null;

        this.users.put(admin.getUserName(), admin);
        return admin;
     }

    public ConcurrentMap<String, User> findAllUsers() {
        return this.users;
    }

    public Guest addGuest(Guest guest) {
        Integer id = key.incrementAndGet();
        guest.setId(id);

        Guest guest1 = (Guest) guest.register(guest);
        if(guest1 == null)
            return null;

        this.users.put(guest.getUserName(), guest);
        return guest1;
    }

    public User login(LoginDto loginBody) {
        Admin admin = new Admin();
        boolean isLoggedIn = admin.login(loginBody.getPassword(), this.users.get(loginBody.getUserName()));
        if(isLoggedIn)
            return this.users.get(loginBody.getUserName());
        return null;
    }
}
