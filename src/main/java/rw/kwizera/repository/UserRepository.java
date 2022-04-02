package rw.kwizera.repository;

import rw.kwizera.model.Admin;
import rw.kwizera.model.Guest;
import rw.kwizera.model.User;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public class UserRepository {

    private ConcurrentMap<String, User> users;
    private AtomicInteger key;

    public UserRepository() {
        this.users = new ConcurrentHashMap<>();
        key = new AtomicInteger();
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
}
