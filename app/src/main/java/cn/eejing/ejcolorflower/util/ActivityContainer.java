package cn.eejing.ejcolorflower.util;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * Android退出应用的四种方式: https://www.jianshu.com/p/10f586950c7f
 */

public class ActivityContainer {
    private static ActivityContainer instance = new ActivityContainer();
    private static List<Activity> activityStack = new ArrayList<>();

    private ActivityContainer() {
    }

    public static ActivityContainer getInstance() {
        return instance;
    }

    public void addActivity(Activity aty) {
        activityStack.add(aty);
    }

    public void removeActivity(Activity aty) {
        activityStack.remove(aty);
    }

    public void finishAllActivity() {
        for (int i = 0, size = activityStack.size(); i < size; i++) {
            if (null != activityStack.get(i)) {
                activityStack.get(i).finish();
            }
        }
        activityStack.clear();
    }

}
