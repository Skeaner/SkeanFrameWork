package skean.me.base.db;

import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * 为BaseModel添加了一些特性
 */
public class AppBaseModel extends BaseModel implements IBaseModel {

    /**
     * 是否准备从数据库删除
     */
    protected boolean isDelete = false;

    protected boolean isNew = false;

    public AppBaseModel() {
        init();
    }

    public AppBaseModel(boolean isNew) {
        this();
        this.isNew = isNew;
    }

    public boolean isDelete() {
        return isDelete;
    }

    public void setDelete(boolean delete) {
        isDelete = delete;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    /**
     * 初始化的一些操作
     */
    public void init() {
    }

    @Override
    public boolean save() {
        onSave();
       return   super.save();
    }

    @Override
    public boolean delete() {
        onDelete();
        return super.delete();
    }

    public void abort() {
        onAbort();
    }

    /**
     * 放弃保存时候的一些操作, 如:有些包含图片的model要删除新增图片
     */
    public void onAbort() {
    }

    /**
     * 保存之前的一些操作, 如:删除多余图片, 更新修改/创建时间等
     */
    public void onSave() {
    }

    /**
     * 删除之前的一些操作, 如:删除全部的图片
     */
    public void onDelete() {
    }

    /**
     * 序列话之前的操作
     */
    public void onSerialize() {
    }

}
