package pt.isec.pd.multicast.multicast_ex12;

import java.io.Serial;
import java.io.Serializable;

public class Msg implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    protected String nickname;
    protected String msg;

    public Msg(String nickname, String msg) {
        this.nickname = nickname;
        this.msg = msg;
    }
    public String getNickname() {
        return nickname;
    }
    public String getMsg() {
        return msg;
    }
}
