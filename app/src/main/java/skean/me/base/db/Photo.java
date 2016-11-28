package skean.me.base.db;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;

/**
 * 图片
 */
public class Photo extends AppBaseModel implements IBaseModel, Parcelable {
    private String desc;
    private File file;

    public Photo() {
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.desc);
        dest.writeSerializable(this.file);
        dest.writeByte(this.isDelete ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isNew ? (byte) 1 : (byte) 0);
    }

    protected Photo(Parcel in) {
        this.desc = in.readString();
        this.file = (File) in.readSerializable();
        this.isDelete = in.readByte() != 0;
        this.isNew = in.readByte() != 0;
    }

    public static final Creator<Photo> CREATOR = new Creator<Photo>() {
        @Override
        public Photo createFromParcel(Parcel source) {
            return new Photo(source);
        }

        @Override
        public Photo[] newArray(int size) {
            return new Photo[size];
        }
    };
}
