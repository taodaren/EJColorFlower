package cn.eejing.colorflower.util;

import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;

import java.lang.reflect.Field;

import cn.eejing.colorflower.R;
import cn.eejing.colorflower.app.BaseApplication;

public class BtnBarUtil {

    /**
     * @param bottomNavigationBar 需要修改的 BottomNavigationBar
     * @param space               图片与文字之间的间距
     * @param imgLen              单位：dp，图片大小，应 <= 36dp
     * @param textSize            单位：dp，文字大小，应 <= 20dp
     *                            <p>
     *                            使用方法：直接调用setBottomNavigationItem(bottomNavigationBar, 6, 26, 10);
     *                            代表将bottomNavigationBar的文字大小设置为10dp，图片大小为26dp，二者间间距为6dp
     **/
    public static void setBottomNavigationItem(BottomNavigationBar bottomNavigationBar, int space, int imgLen, int textSize) {
        Class barClass = bottomNavigationBar.getClass();
        Field[] fields = barClass.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            if (field.getName().equals("mTabContainer")) {
                try {
                    // 反射得到 mTabContainer
                    LinearLayout mTabContainer = (LinearLayout) field.get(bottomNavigationBar);
                    for (int j = 0; j < mTabContainer.getChildCount(); j++) {
                        // 获取到容器内的各个 Tab
                        View view = mTabContainer.getChildAt(j);
                        // 获取到 Tab 内的各个显示控件
                        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dip2px(56));
                        FrameLayout container = view.findViewById(R.id.fixed_bottom_navigation_container);
                        container.setLayoutParams(params);
                        container.setPadding(dip2px(12), dip2px(0), dip2px(12), dip2px(0));

                        // 获取到 Tab 内的文字控件
                        TextView labelView = view.findViewById(com.ashokvarma.bottomnavigation.R.id.fixed_bottom_navigation_title);
                        // 计算文字的高度 DP 值并设置，setTextSize 为设置文字正方形的对角线长度
                        // 所以：文字高度（总内容高度减去间距和图片高度）* 根号 2 即为对角线长度，此处用 DP 值，设置该值即可。
                        labelView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSize);
                        labelView.setIncludeFontPadding(false);
                        labelView.setPadding(0, 0, 0, dip2px(20 - textSize - space / 2));

                        // 获取到 Tab 内的图像控件
                        ImageView iconView = view.findViewById(com.ashokvarma.bottomnavigation.R.id.fixed_bottom_navigation_icon);
                        // 设置图片参数，其中，MethodUtils.dip2px()：换算 dp 值
                        params = new FrameLayout.LayoutParams(dip2px(imgLen), dip2px(imgLen));
                        params.setMargins(0, 0, 0, space / 2);
                        params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
                        iconView.setLayoutParams(params);
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static int dip2px(float dpValue) {
        final float scale = BaseApplication.getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
