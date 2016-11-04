package skean.me.base.component;

import android.content.DialogInterface;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.OptionsMenuItem;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

import skean.me.base.db.IPhoto;
import skean.me.base.utils.ContentUtil;
import skean.yzsm.com.framework.R;
import uk.co.senab.photoview.PhotoView;

/**
 * 预览图片
 */
@EActivity(R.layout.activity_picture_pager)
@OptionsMenu(R.menu.menu_form)
public class PicturePagerActivity extends BaseActivity {

    @ViewById
    ViewPager vpgGallery;
    @ViewById
    LinearLayout panelDesc;
    @ViewById
    EditText edtDesc;

    @OptionsMenuItem
    MenuItem mniSave;
    @OptionsMenuItem
    MenuItem mniEdit;

    @Extra
    boolean inEdit = false;
    @Extra
    int defaultPosition;
    @Extra
    boolean showDescription;
    @Extra
    ArrayList<IPhoto> photoList;

    boolean inEditDesc = false;

    PagerAdapter adapter;


    IPhoto currentPhoto;


    @AfterViews
    protected void init() {
        if (!showDescription) panelDesc.setVisibility(View.GONE);
        adapter = new PicturePagerAdapter();
        vpgGallery.setAdapter(adapter);
        vpgGallery.addOnPageChangeListener(pageLsn);
        vpgGallery.post(new Runnable() {
            @Override
            public void run() {
                if (defaultPosition == 0) pageLsn.onPageSelected(0);
                else vpgGallery.setCurrentItem(defaultPosition);
            }
        });
    }

    @Override
    public boolean onBack() {
        if (!super.onBack() && inEditDesc) {
            new AlertDialog.Builder(alertTheme).setTitle(R.string.tips)
                                               .setMessage(getString(R.string.abortDataConfirm))
                                               .setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                                                   @Override
                                                   public void onClick(DialogInterface dialog, int which) {
                                                       finish();
                                                   }
                                               })
                                               .setNegativeButton(getString(R.string.cancel), null)
                                               .show();
            return true;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        mniEdit.setVisible(inEdit);
        return result;
    }

    @OptionsItem
    protected void mniEdit() {
        inEditDesc = true;
        mniEdit.setVisible(false);
        edtDesc.setEnabled(true);
        mniSave.setVisible(true);
    }

    @OptionsItem
    protected void mniSave() {
        currentPhoto.setDesc(ContentUtil.nullIfEmpty(edtDesc));
        getAppApplication().setTempObject(photoList);
        setResult(RESULT_OK);
        finish();
    }

    private ViewPager.SimpleOnPageChangeListener pageLsn = new ViewPager.SimpleOnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            super.onPageSelected(position);
            setPagerTitle(position + 1);
            hideSoftKeyboard();
            if (showDescription) {
                if (currentPhoto != null) currentPhoto.setDesc(ContentUtil.nullIfEmpty(edtDesc));
                currentPhoto = photoList.get(position);
                edtDesc.setText(currentPhoto.getDesc());
            }
        }
    };

    protected void setPagerTitle(int position) {
        setTitle(String.format("第%d张共%d张", position, photoList.size()));
    }

    class PicturePagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return photoList.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            PhotoView photoView = new PhotoView(container.getContext());
            photoView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            photoView.setAdjustViewBounds(true);
            // Now just add PhotoView to ViewPager and return it
            container.addView(photoView, ViewPager.LayoutParams.MATCH_PARENT, ViewPager.LayoutParams.MATCH_PARENT);
            Picasso.with(getContext()).load(photoList.get(position).getPictureFile()).into(photoView);
            return photoView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

}
