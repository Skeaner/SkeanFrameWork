package skean.me.base.component;

import android.content.Intent;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import skean.me.base.utils.FileUtil;
import skean.me.base.utils.ImageUtil;

/**
 * 图片操作页基类
 */
public abstract class BasePictureActivity extends BaseActivity {

    protected File containerFolder;
    protected long containerId;
    protected ArrayList<File> compressedPictures = new ArrayList<>();

    protected int pictureTag = 0;

    public static final String EXTENTION = ".jpg";
    public static final String SEPERATOR = "_";

    public static final String EXTRA_PIC_LIST = "list";

    /**
     * 返回关联的容器ID
     *
     * @return id
     */
    protected abstract long containerId();

    protected File doPictureCompress(File rawPicture) throws RuntimeException {
        File compressedFile = null;
        try {
            containerFolder = new File(AppApplication.getAppPicturesDirectory(), containerId + "");
            FileUtil.initializeFile(containerFolder, true);
            String fileName = new StringBuilder().append(containerId).append(SEPERATOR).append(findPictureTag()).append(EXTENTION).toString();
            compressedFile = new File(containerFolder, fileName);
            compressedFile.createNewFile();
            ImageUtil.Compressor.toPreferSizeFile(context, rawPicture, compressedFile);
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                File SdDir = Environment.getExternalStorageDirectory();
            } else {
                // 没sd卡
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("压缩图片出错了");
        }
        return compressedFile;
    }

    protected int findPictureTag() {
        if (pictureTag == 0) {
            if (containerFolder != null && containerFolder.exists()) {
                for (String name : containerFolder.list()) {
                    String[] tags = name.replaceAll(EXTENTION, "").split(SEPERATOR);
                    int tempTag = Integer.valueOf(tags[tags.length - 1]);
                    if (pictureTag < tempTag) pictureTag = tempTag;
                }
                pictureTag++;
            } else pictureTag = 1;
        } else pictureTag++;
        return pictureTag;
    }

    protected void returnPictures(File... rawPictures) {
        compressedPictures.clear();
        Observable.fromArray(rawPictures).subscribeOn(AndroidSchedulers.mainThread()).observeOn(Schedulers.io()).map(new Function<File, File>() {
            @Override
            public File apply(File rawPicture) throws Exception {
                return doPictureCompress(rawPicture);
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<File>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(File file) {
                compressedPictures.add(file);
            }

            @Override
            public void onError(Throwable e) {
                for (File file : compressedPictures) {
                    file.delete();
                }
                setResult(RESULT_ERROR);
                finish();
            }

            @Override
            public void onComplete() {
                setResult(RESULT_OK, new Intent().putExtra(EXTRA_PIC_LIST, compressedPictures));
                finish();
            }
        });
    }
}
