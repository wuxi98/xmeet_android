package cn.nchu.wuxi.xlivemeet.core.chat;


public class InitSocketThread extends Thread {
    private String phone;

    public InitSocketThread(String phone) {
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public void run() {
        super.run();
        ChatWsManager instance = ChatWsManager.getInstance();
        instance.getListener().setPhone(phone);
        instance.connectChat();
    }
}