package vn.com.vng.zalopay.data.cache.global;

import org.greenrobot.greendao.annotation.*;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit.

/**
 * Entity mapped to table "APPTRANSID_LOG_API_CALL_GD".
 */
@Entity
public class ApptransidLogApiCallGD {

    @Id(autoincrement = true)
    public Long id;
    public String apptransid;
    public Long apiid;
    public Long timebegin;
    public Long timeend;
    public Integer returncode;


}