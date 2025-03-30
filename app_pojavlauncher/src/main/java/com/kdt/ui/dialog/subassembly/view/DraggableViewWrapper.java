package com.kdt.ui.subassembly.view;

import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.view.View;

public class DraggableViewWrapper {
    private final View mainView;
    private final AttributesFetcher fetcher;
    private long lastUpdateTime = 0L;
    private float initialX = 0f;
    private float initialY = 0f;
    private float touchX = 0f;
    private float touchY = 0f;

    public DraggableViewWrapper(View mainView, AttributesFetcher fetcher) {
        this.mainView = mainView;
        this.fetcher = fetcher;
    }

    @SuppressLint("ClickableViewAccessibility")
    public void init() {
        mainView.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (updateRateLimits()) return false;

                    initialX = (float) fetcher.get()[0];
                    initialY = (float) fetcher.get()[1];
                    touchX = event.getRawX();
                    touchY = event.getRawY();
                    return true;
                case MotionEvent.ACTION_MOVE:
                    if (updateRateLimits()) return false;

                    int x = (int) Math.max(fetcher.getScreenPixels().minX, Math.min(fetcher.getScreenPixels().maxX,
                            (initialX + (double) (event.getRawX() - touchX)))
                    );
                    int y = (int) Math.max(fetcher.getScreenPixels().minY, Math.min(fetcher.getScreenPixels().maxY,
                            (initialY + (double) (event.getRawY() - touchY)))
                    );
                    fetcher.set(x, y);
                    return true;
            }
            return false;
        });
    }

    //避免过于频繁的更新导致的性能开销
    private boolean updateRateLimits() {
        boolean limit = false;
        long millis = System.currentTimeMillis();
        if (millis - lastUpdateTime < 5) limit = true;
        lastUpdateTime = millis;
        return limit;
    }

    public interface AttributesFetcher {
        //获取对应的屏幕的高宽限制值
        ScreenPixels getScreenPixels();
        int[] get(); //获取x, y值
        void set(int x, int y);
    }

    public static class ScreenPixels {
        private final int minX;
        private final int minY;
        private final int maxX;
        private final int maxY;

        public ScreenPixels(int minX, int minY, int maxX, int maxY) {
            this.minX = minX;
            this.minY = minY;
            this.maxX = maxX;
            this.maxY = maxY;
        }

        public int getMinX() {
            return minX;
        }

        public int getMinY() {
            return minY;
        }

        public int getMaxX() {
            return maxX;
        }

        public int getMaxY() {
            return maxY;
        }
    }
}
