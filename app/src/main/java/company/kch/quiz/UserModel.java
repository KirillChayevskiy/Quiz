package company.kch.quiz;


public class UserModel {

    public String num, name, key;
    public int score;

    public UserModel() {}

    public UserModel(String name, int score, String key) {
        //this.num = num;
        this.name = name;
        this.score = score;
        this.key = key;


    }
}
