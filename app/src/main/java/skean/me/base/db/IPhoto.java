package skean.me.base.db;

import android.os.Parcelable;

import java.io.File;

/**
 * 图片接口
 */
public interface IPhoto extends IBaseModel, Parcelable {
    File getPictureFile();
    String getDesc();
    void setDesc(String desc);
}
