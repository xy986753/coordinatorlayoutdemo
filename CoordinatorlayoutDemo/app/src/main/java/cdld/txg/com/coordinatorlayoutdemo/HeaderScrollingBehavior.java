package cdld.txg.com.coordinatorlayoutdemo;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Scroller;

import java.lang.ref.WeakReference;


public class HeaderScrollingBehavior extends CoordinatorLayout.Behavior<RecyclerView> {

    private boolean isExpanded = false;
    private boolean isScrolling = false;

    private WeakReference<View> dependentView;
    private Scroller scroller;
    private Handler handler;

    public HeaderScrollingBehavior(Context context, AttributeSet attrs) {
        //带有参数的这个构造必须要重载，因为在CoordinatorLayout里利用反射去获取这个Behavior的时候就是拿的这个构造。
        super(context, attrs);
        scroller = new Scroller(context);
        handler = new Handler();
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, RecyclerView child, View dependency) {
        if (dependency != null && dependency.getId() == R.id.scrolling_header) {//recycleview(child)关联的view(dependency)
            dependentView = new WeakReference<>(dependency);
            return true;
        }
        return false;
    }

    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, RecyclerView child, int layoutDirection) {
        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) child.getLayoutParams();
        if (lp.height == CoordinatorLayout.LayoutParams.MATCH_PARENT) {
            child.layout(0, 0, parent.getWidth(), (int) (parent.getHeight() - getDependentViewCollapsedHeight()));
            return true;
        }
        return super.onLayoutChild(parent, child, layoutDirection);
    }

    /**
     * 作用：dependency变化时设置child的变化
     * @param parent
     * @param child
     * @param dependency
     * @return
     */
    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, RecyclerView child, View dependency) {
        Resources resources = getDependentView().getResources();
        //translationY是Y平移的距离
        child.setTranslationY(dependency.getHeight() + dependency.getTranslationY());

        //得到滑动时dependency的透明度，动态设置dependency透明度
        final float progress = 1.f - Math.abs(dependency.getTranslationY() / (dependency.getHeight() - resources.getDimension(R.dimen.collapsed_header_height)));
//        dependency.setAlpha(progress);

        //动态设置dependency放大缩小
        float scale = 1 + 0.4f * (1.f - progress);
        dependency.setScaleX(scale);
        dependency.setScaleY(scale);
        return true;
    }

    /**
     * 嵌套滑动开始了，问下该父View是否接受嵌套滑动
     * @param coordinatorLayout
     * @param child 嵌套滑动对应的父类的子类(因为嵌套滑动对于的父View不一定是一级就能找到的，可能挑了两级父View的父View，child的辈分>=target)
     * @param directTargetChild
     * @param target    具体嵌套滑动的那个子类
     * @param nestedScrollAxes  支持嵌套滚动轴。水平方向，垂直方向，或者不指定
     * @return
     */
    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, RecyclerView child, View directTargetChild, View target, int nestedScrollAxes) {
        return (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }

    /**
     * 该父View接受了嵌套滑动的请求该函数调用。onStartNestedScroll返回true该函数会被调用。
     * 参数和onStartNestedScroll一样
     */
    @Override
    public void onNestedScrollAccepted(CoordinatorLayout coordinatorLayout, RecyclerView child, View directTargetChild, View target, int nestedScrollAxes) {
        scroller.abortAnimation();
        isScrolling = false;
        super.onNestedScrollAccepted(coordinatorLayout, child, directTargetChild, target, nestedScrollAxes);
    }

    /**
     * 在嵌套滑动的子View未滑动之前告诉过来的准备滑动的情况
     * @param coordinatorLayout
     * @param child
     * @param target    具体嵌套滑动的那个子类
     * @param dx    水平方向嵌套滑动的子View想要变化的距离
     * @param dy    垂直方向嵌套滑动的子View想要变化的距离
     * @param consumed  这个参数要我们在实现这个函数的时候指定，回头告诉子View当前父View消耗的距离
     *                  consumed[0] 水平消耗的距离，consumed[1] 垂直消耗的距离 好让子view做出相应的调整
     */
    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, RecyclerView child, View target, int dx, int dy, int[] consumed) {
        if (dy < 0) {
            return;
        }
        View dependentView = getDependentView();//child的关联布局
        float newTranslateY = dependentView.getTranslationY() - dy;
        float minHeaderTranslate = -(dependentView.getHeight() - getDependentViewCollapsedHeight());
        if (newTranslateY > minHeaderTranslate) {
            dependentView.setTranslationY(newTranslateY);
            consumed[1] = dy;
        }
    }

    /**
     * 嵌套滑动的子View在滑动之后报告过来的滑动情况
     * @param coordinatorLayout
     * @param child
     * @param target    具体嵌套滑动的那个子类
     * @param dxConsumed    水平方向嵌套滑动的子View滑动的距离(消耗的距离)
     * @param dyConsumed    垂直方向嵌套滑动的子View滑动的距离(消耗的距离)
     * @param dxUnconsumed  水平方向嵌套滑动的子View未滑动的距离(未消耗的距离)
     * @param dyUnconsumed  垂直方向嵌套滑动的子View未滑动的距离(未消耗的距离)
     */
    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, RecyclerView child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        if (dyUnconsumed > 0) {
            return;
        }
        View dependentView = getDependentView();
        float newTranslateY = dependentView.getTranslationY() - dyUnconsumed;
        final float maxHeaderTranslate = 0;

        if (newTranslateY < maxHeaderTranslate) {
            dependentView.setTranslationY(newTranslateY);
        }
    }

    //惯性滚动
    @Override
    public boolean onNestedPreFling(CoordinatorLayout coordinatorLayout, RecyclerView child, View target, float velocityX, float velocityY) {
        return onUserStopDragging(velocityY);
    }

    @Override
    public void onStopNestedScroll(CoordinatorLayout coordinatorLayout, RecyclerView child, View target) {
        if (!isScrolling) {
            onUserStopDragging(800);
        }
    }

    private boolean onUserStopDragging(float velocity) {
        View dependentView = getDependentView();
        float translateY = dependentView.getTranslationY();
        float minHeaderTranslate = -(dependentView.getHeight() - getDependentViewCollapsedHeight());

        if (translateY == 0 || translateY == minHeaderTranslate) {
            return false;
        }

        boolean targetState; // Flag indicates whether to expand the content.
        if (Math.abs(velocity) <= 800) {
            targetState = Math.abs(translateY) >= Math.abs(translateY - minHeaderTranslate);
            velocity = 800; // Limit velocity's minimum value.
        } else {
            targetState = velocity > 0;
        }

        float targetTranslateY = targetState ? minHeaderTranslate : 0;

        scroller.startScroll(0, (int) translateY, 0, (int) (targetTranslateY - translateY), (int) (1000000 / Math.abs(velocity)));
        handler.post(flingRunnable);
        isScrolling = true;
        return true;
    }

    private float getDependentViewCollapsedHeight() {
        return getDependentView().getResources().getDimension(R.dimen.collapsed_header_height);
    }

    private View getDependentView() {
        return dependentView.get();
    }

    private Runnable flingRunnable = new Runnable() {
        @Override
        public void run() {
            if (scroller.computeScrollOffset()) {
                getDependentView().setTranslationY(scroller.getCurrY());
                handler.post(this);
            } else {
                isExpanded = getDependentView().getTranslationY() != 0;
                isScrolling = false;
            }
        }
    };

}
