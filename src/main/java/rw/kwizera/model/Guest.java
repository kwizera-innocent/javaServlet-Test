package rw.kwizera.model;

public class Guest extends User {

    public Guest() {
    }

    public Guest(String userName, String password, String firstName, String lastName, eGender sex, int age, String phone, String role) {
        super(userName, password, firstName, lastName, sex, age, phone, role);
    }

    @Override
    public User register(User user) {
        if (user.getPassword() == null || user.getPassword().isEmpty() || !containLetter(user.getPassword()) || !len(user.getPassword()) || !containsNumber(user.getPassword()))
            return null;

        String reversedPswd = "";
        for (int i = 0; i < user.getPassword().length(); i++) {
            char c = user.getPassword().charAt(i);
            reversedPswd = c + reversedPswd;
        }
        user.setPassword("**" + reversedPswd + user.getAge() + "**");
        return user;
    }

    @Override
    public boolean login(String harshedPassword, User user) {
        String actialPasssword = "";
        String reversedPswd = user.getPassword().substring(2,user.getPassword().length()-4);

        for (int i = 0; i < reversedPswd.length(); i++) {
            char c = reversedPswd.charAt(i);
            actialPasssword = c + actialPasssword;
        }
        return actialPasssword.equals(harshedPassword);
    }

    public boolean len(String password) {
        return password.length() == 5;
    }
    public boolean containsNumber(String password) {
        boolean count = false;
        for (int i = 0; i < 10; i++) {
            String s = Integer.toString(i);
            if (password.contains(s)) {
                count = true;
                break;
            }
        }
        return count;
    }
    public boolean containLetter(String password) {
        return (password.matches(".*[a-z].*"));
    }
}
