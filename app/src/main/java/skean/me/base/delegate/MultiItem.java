package skean.me.base.delegate;

import com.chad.library.adapter.base.entity.MultiItemEntity;

public class MultiItem implements MultiItemEntity {

    private int itemType;
    private int spanSize;
    private Object data;
    private Object data2;

    public MultiItem(int itemType, Object data) {
        this.itemType = itemType;
        this.data = data;
        spanSize = setUpSpanSize(itemType);
    }

    public MultiItem(int itemType, Object data, Object data2) {
        this.itemType = itemType;
        this.data = data;
        this.data2 = data2;
        spanSize = setUpSpanSize(itemType);
    }

    @Override
    public int getItemType() {
        return itemType;
    }

    @SuppressWarnings("all")
    public <T> T getData() {
        return (T) data;
    }

    @SuppressWarnings("all")
    public <T> T getData2() {
        return (T) data2;
    }

    public int setUpSpanSize(int itemType) {
        return 1;
    }

    public int getSpanSize() {
        return spanSize;
    }
}
