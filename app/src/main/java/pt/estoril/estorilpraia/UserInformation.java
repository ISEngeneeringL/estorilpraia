package pt.estoril.estorilpraia;

/**
 * Created by Leonardo on 18/02/2017.
 */
class UserInformation {

    private String name, address, email, team, age;
    private boolean isAdmin;

    public UserInformation(){

    }

    UserInformation(String name, String address, String email, String team, String age, boolean isAdmin) {
        this.name = name;
        this.address = address;
        this.email = email;
        this.team = team;
        this.age = age;
        this.isAdmin = isAdmin;
    }

    public String getName() {
        return name;
    }
}
