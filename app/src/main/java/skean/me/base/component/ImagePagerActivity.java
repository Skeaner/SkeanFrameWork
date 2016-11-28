package skean.me.base.component;

import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.viewpagerindicator.CirclePageIndicator;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.util.ArrayList;

import skean.me.base.db.Photo;
import skean.me.base.utils.ContentUtil;
import skean.yzsm.com.framework.R;

/**
 * 预览图片
 */
@EActivity(R.layout.activity_image_pager)
public class ImagePagerActivity extends BaseActivity {

    @ViewById
    ViewPager vpgGallery;
    @ViewById
    CirclePageIndicator indicator;
    LinearLayout panelDesc;
    EditText edtTitle;
    ImageButton btnSave;
    ImageButton btnEdit;

    @Extra
    boolean inEdit = false;
    @Extra
    int defaultPosition;
    @Extra
    boolean showDescription;
    @Extra
    ArrayList<Photo> photoList;

    boolean inEditDesc = false;

    PagerAdapter adapter;

    Photo currentPhoto;

    @AfterViews
    protected void init() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#66000000")));
        getSupportActionBar().setCustomView(R.layout.bar_custom_image_pager);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        panelDesc = (LinearLayout) getSupportActionBar().getCustomView().findViewById(R.id.panelDesc);
        edtTitle = (EditText) getSupportActionBar().getCustomView().findViewById(R.id.edtTitle);
        btnSave = (ImageButton) getSupportActionBar().getCustomView().findViewById(R.id.btnSave);
        btnEdit = (ImageButton) getSupportActionBar().getCustomView().findViewById(R.id.btnEdit);
        if (!inEdit) {
            btnEdit.setVisibility(View.GONE);
        }
        edtTitle.setEnabled(false);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPhoto.setDesc(ContentUtil.nullIfEmpty(edtTitle));
                getAppApplication().setTempObject(photoList);
                setResult(RESULT_OK);
                finish();
            }
        });
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inEditDesc = true;
                btnEdit.setVisibility(View.GONE);
                btnSave.setVisibility(View.VISIBLE);
                edtTitle.setEnabled(true);
                showSoftKeyboard(edtTitle);
            }
        });
        adapter = new ImagesPagerAdapter();
        vpgGallery.setAdapter(adapter);
        vpgGallery.setOffscreenPageLimit(2);
        indicator.setViewPager(vpgGallery);
        indicator.setOnPageChangeListener(pageLsn);
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
            if (!getSupportActionBar().isShowing()) toggle();
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

    private ViewPager.SimpleOnPageChangeListener pageLsn = new ViewPager.SimpleOnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            super.onPageSelected(position);
            hideSoftKeyboard();
            if (showDescription) {
                if (currentPhoto != null) currentPhoto.setDesc(ContentUtil.nullIfEmpty(edtTitle));
                currentPhoto = photoList.get(position);
                edtTitle.setText(currentPhoto.getDesc());
            } else {
                currentPhoto = photoList.get(position);
                edtTitle.setText(currentPhoto.getFile().getName());
            }
        }
    };

    public void toggle() {
        if (getSupportActionBar().isShowing()) {
            getSupportActionBar().hide();
            indicator.setVisibility(View.GONE);
        } else {
            getSupportActionBar().show();
            indicator.setVisibility(View.VISIBLE);
        }
    }

    class ImagesPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return photoList.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            SubsamplingScaleImageView imageView = new SubsamplingScaleImageView(container.getContext());
            File imageFile = photoList.get(position).getFile();
            imageView.setImage(ImageSource.uri(imageFile.getPath()));
            imageView.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CUSTOM);
            imageView.setMinScale(getScaleRatio(imageFile));
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggle();
                }
            });
            container.addView(imageView, ViewPager.LayoutParams.MATCH_PARENT, ViewPager.LayoutParams.MATCH_PARENT);
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        private float getScaleRatio(File imageFile) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(imageFile.getPath(), options);
            float width = (float) (getResources().getDisplayMetrics().widthPixels + 0.1 - 0.1);
            return width / options.outWidth;
        }
    }

}
