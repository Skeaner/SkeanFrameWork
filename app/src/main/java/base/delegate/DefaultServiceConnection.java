package base.delegate;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

/**
 * ServiceConnection的默认实现
 */
public class DefaultServiceConnection implements ServiceConnection {
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {

    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }
}
