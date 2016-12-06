package kz.qobyzbook.F_Test;




public class TestModel {

    private String answ1, answ2, answ3, answ4, answ5, question, right;




    public TestModel() {
    }

    public TestModel(String answ1, String answ2, String answ3, String answ4, String answ5, String question, String right) {
        this.answ1 = answ1;
        this.answ2 = answ2;
        this.answ3 = answ3;
        this.answ4 = answ4;
        this.answ5 = answ5;
        this.question = question;
        this.right = right;

    }

    public String getAnsw1() {
        return answ1;
    }

    public String getAnsw2() {
        return answ2;
    }

    public String getAnsw3() {
        return answ3;
    }

    public String getAnsw4() {
        return answ4;
    }

    public String getAnsw5() {
        return answ5;
    }

    public String getQuestion() {
        return question;
    }

    public String getRight() {
        return right;
    }

    public void setAnsw1(String answ1) {
        this.answ1 = answ1;
    }

    public void setAnsw2(String answ2) {
        this.answ2 = answ2;
    }

    public void setAnsw3(String answ3) {
        this.answ3 = answ3;
    }

    public void setAnsw4(String answ4) {
        this.answ4 = answ4;
    }

    public void setAnsw5(String answ5) {
        this.answ5 = answ5;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public void setRight(String right) {
        this.right = right;
    }


}