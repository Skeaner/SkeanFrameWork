package skean.me.base.delegate;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import skean.me.base.component.BaseActivity;
import skean.me.base.db.IPhoto;
import skean.yzsm.com.framework.R;

public class GalleryAdapter extends RV.ArrayAdapter<IPhoto, GalleryAdapter.ViewHolder> {

    private int maxItemSize;

    private boolean inEdit = false;

    long targetId;
    private WeakReference<Fragment> fRef;
    private WeakReference<BaseActivity> aRef;
    private boolean holdByFragment;

    public static final int REQUEST_IMAGES = 89;
    public static final int REQUEST_PREVIEW = 88;

    public GalleryAdapter(Context context, int maxItemSize, long targetId) {
        super(context);
        this.maxItemSize = maxItemSize;
        this.targetId = targetId;
    }

    public GalleryAdapter(Context context, boolean notifyOnChange, int maxItemSize, long targetId) {
        super(context, notifyOnChange);
        this.maxItemSize = maxItemSize;
        this.targetId = targetId;
    }

    public GalleryAdapter(Context context, List<IPhoto> list, int maxItemSize, long targetId) {
        super(context, list);
        this.maxItemSize = maxItemSize;
        this.targetId = targetId;
    }

    public GalleryAdapter(Context context, List<IPhoto> items, boolean notifyOnChange, int maxItemSize, long targetId) {
        super(context, items, notifyOnChange);
        this.maxItemSize = maxItemSize;
        this.targetId = targetId;
    }

    @Override
    protected void init() {
        super.init();
        beginFiltering("并没有什么卵用");
    }

    @Override
    protected boolean doFiltering(IPhoto item, String constraintStr) {
        return !item.isDelete();
    }

    public void setupContainer(BaseActivity activity) {
        aRef = new WeakReference<>(activity);
    }

    public void setupContainer(Fragment fragment) {
        holdByFragment = true;
        fRef = new WeakReference<>(fragment);
    }

    public void beginEdit() {
        inEdit = true;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        //编辑模式下增加一个拍照按键
        int itemSize = items.size();
        return inEdit && itemSize < maxItemSize ? itemSize + 1 : itemSize;
    }

    public int getItemSize() {
        return items.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.listitem_gallery_deletable, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (!inEdit) {
            holder.imbRemove.setVisibility(View.GONE);
            Picasso.with(getContext()).load(getItem(position).getPictureFile()).resize(100, 100).into(holder.icon);
        } else if (items.size() < maxItemSize) {
            if (position == 0) {
                holder.imbRemove.setVisibility(View.GONE);
                holder.icon.setImageResource(R.drawable.ic_add_image);
            } else {
                holder.imbRemove.setVisibility(View.VISIBLE);
                Picasso.with(getContext()).load(getItem(position - 1).getPictureFile()).resize(100, 100).into(holder.icon);
            }
        } else {
            holder.imbRemove.setVisibility(View.VISIBLE);
            Picasso.with(getContext()).load(getItem(position).getPictureFile()).resize(100, 100).into(holder.icon);
        }
    }

    protected void onItemClick(int position) {
//        Intent viewIntent = PicturePagerActivity_.intent(context)
//                                                 .IPhotoList(new ArrayList<>(items))
//                                                 .showDescription(true)
//                                                 .inEdit(inEdit)
//                                                 .defaultPosition(position)
//                                                 .get();
//        if (holdByFragment) fRef.get().startActivityForResult(viewIntent, REQUEST_PREVIEW);
//        else aRef.get().startActivityForResult(viewIntent, REQUEST_PREVIEW);
    }

    protected void toAddItem(View anchorView) {
        PopupMenu menu = new PopupMenu(context, anchorView);
        menu.inflate(R.menu.pop_add_image);
        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.mniTakePicture:
//                        Intent captureIntent = TakePictureActivity_.intent(context).targetId(targetId).get();
//                        if (holdByFragment) fRef.get().startActivityForResult(captureIntent, REQUEST_IMAGES);
//                        else aRef.get().startActivityForResult(captureIntent, REQUEST_IMAGES);
                        break;
                    case R.id.mniPickPicture:
//                        Intent pickIntent = PickPictureActivity2_.intent(context)
//                                                                 .targetId(targetId)
//                                                                 .remainNum(maxItemSize - items.size())
//                                                                 .get();
//                        if (holdByFragment) fRef.get().startActivityForResult(pickIntent, REQUEST_IMAGES);
//                        else aRef.get().startActivityForResult(pickIntent, REQUEST_IMAGES);
                        break;
                }
                return true;
            }
        });
        MenuPopupHelper helper = new MenuPopupHelper(context, (MenuBuilder) menu.getMenu(), anchorView);
        helper.setForceShowIcon(true);
        helper.show();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageButton imbRemove;
        ImageView icon;

        public ViewHolder(View view) {
            super(view);
            imbRemove = (ImageButton) view.findViewById(R.id.imbRemove);
            icon = (ImageView) view.findViewById(R.id.imvIcon);
            imbRemove.setOnClickListener(listener);
            icon.setOnClickListener(listener);
        }

        private View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int position = getAdapterPosition();
                switch (v.getId()) {
                    case R.id.imbRemove:
                        AlertDialog.Builder ab = new AlertDialog.Builder(context);
                        ab.setTitle(R.string.tips)
                          .setMessage("删除照片后将无法恢复, 确认删除该照片吗?")
                          .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                              @Override
                              public void onClick(DialogInterface dialog, int which) {
                                  int realPosition = items.size() < maxItemSize ? position - 1 : position;
                                  IPhoto photo = items.get(realPosition);
                                  photo.setDelete(true);
                                  notifyDataSetChangedAuto();
                              }
                          })
                          .setNegativeButton(R.string.cancel, null)
                          .setCancelable(true)
                          .show();
                        break;
                    case R.id.imvIcon:
                        if (!inEdit) {
                            onItemClick(position);
                        } else if (items.size() < maxItemSize) {
                            if (position == 0) toAddItem(v);
                            else onItemClick(position - 1);
                        } else onItemClick(position);
                        break;
                }
            }
        };

    }

}
