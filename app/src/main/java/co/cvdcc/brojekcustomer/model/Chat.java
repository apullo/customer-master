package co.cvdcc.brojekcustomer.model;

import java.io.Serializable;

import static co.cvdcc.brojekcustomer.model.FCMType.CHAT;

/**
 * Created by GagahIB on 19/10/2016.
 */
public class Chat implements Serializable{
    public int type = CHAT;
    public String id_tujuan;
    public String nama_tujuan; // namaku
    public String reg_id_tujuan;
    public String isi_chat;
    public String waktu;
    public int status;
    public int chat_from;
    public transient String reg_id_from;
}
