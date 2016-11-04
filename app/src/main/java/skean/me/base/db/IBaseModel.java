package skean.me.base.db;

import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * 为BaseModel添加了一些特性
 */
public interface IBaseModel {

    boolean isDelete();

    void setDelete(boolean delete);

    boolean isNew();

    void setNew(boolean aNew);

    /**
     * 初始化的一些操作
     */
    void init();

    void abort();

    /**
     * 放弃保存时候的一些操作, 如:有些包含图片的model要删除新增图片
     */
    void onAbort();

    /**
     * 保存之前的一些操作, 如:删除多余图片, 更新修改/创建时间等
     */
    void onSave();

    /**
     * 删除之前的一些操作, 如:删除全部的图片
     */
    void onDelete();

    /**
     * 序列话之前的操作
     */
    void onSerialize();
}
