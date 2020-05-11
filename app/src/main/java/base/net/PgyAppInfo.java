package base.net;

import java.util.List;

/**
 *蒲公英VersionInfo
 */
public class PgyAppInfo {

    private int code;
    private String message;
    private List<PgyVersionInfo> data;

    public void setCode(int code) {
        this.code = code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setData(List<PgyVersionInfo> data) {
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public List<PgyVersionInfo> getData() {
        return data;
    }

}
